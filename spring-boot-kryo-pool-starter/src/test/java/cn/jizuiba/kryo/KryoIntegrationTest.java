package cn.jizuiba.kryo;

import cn.jizuiba.kryo.pool.KryoPool;
import cn.jizuiba.kryo.serializer.KryoSerializer;
import cn.jizuiba.kryo.util.KryoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KryoIntegrationTest.TestApplication.class)
@TestPropertySource(properties = {
        "kryo.pool.enabled=true",
        "kryo.pool.max-total=10",
        "kryo.pool.max-idle=5",
        "kryo.pool.min-idle=2"
})
class KryoIntegrationTest {

    @Autowired
    private KryoPool kryoPool;

    @Autowired
    private KryoSerializer kryoSerializer;

    @Test
    void testKryoUtilsWithApplicationContext() {
        // 测试工具类
        List<String> original = Arrays.asList("util1", "util2", "util3");

        byte[] serialized = KryoUtils.serialize(original);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        List<String> deserialized = (List<String>) KryoUtils.deserialize(serialized);
        assertEquals(original, deserialized);

        List<String> copied = KryoUtils.copy(original);
        assertEquals(original, copied);
        assertNotSame(original, copied);
    }

    @Test
    void testHighConcurrency() throws InterruptedException {
        int threadCount = 20;
        int operationsPerThread = 50;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        try {
                            String testData = "Thread-" + threadId + "-Data-" + j;

                            // 使用不同的方式进行序列化
                            if (j % 3 == 0) {
                                // 直接使用 KryoSerializer
                                byte[] serialized = kryoSerializer.serialize(testData);
                                String deserialized = kryoSerializer.deserialize(serialized, String.class);
                                assertEquals(testData, deserialized);
                            } else if (j % 3 == 1) {
                                // 使用 KryoPool 回调
                                String result = kryoPool.execute(kryo -> {
                                    return kryo.copy(testData);
                                });
                                assertEquals(testData, result);
                            } else {
                                // 使用工具类
                                byte[] serialized = KryoUtils.serialize(testData);
                                String deserialized = KryoUtils.deserialize(serialized, String.class);
                                assertEquals(testData, deserialized);
                            }

                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            e.printStackTrace();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread, successCount.get());
        assertEquals(0, errorCount.get());

        // 验证池状态
        KryoPool.PoolInfo poolInfo = kryoPool.getPoolInfo();
        assertTrue(poolInfo.active() >= 0);
        assertTrue(poolInfo.idle() >= 0);
    }

    @Test
    void testPoolResourceManagement() {
        // 测试资源管理
        KryoPool.PoolInfo initialInfo = kryoPool.getPoolInfo();

        // 批量操作
        for (int i = 0; i < 100; i++) {
            String data = "test-" + i;
            kryoPool.execute(kryo -> {
                return kryo.copy(data);
            });
        }

        // 验证池状态稳定
        KryoPool.PoolInfo finalInfo = kryoPool.getPoolInfo();
        assertTrue(finalInfo.active() >= 0);
        assertTrue(finalInfo.idle() >= 0);

        // 活跃连接数应该回到初始状态（或接近）
        assertEquals(0, finalInfo.active());
    }

    // 测试应用程序配置
    @SpringBootApplication
    static class TestApplication {
        // 空的测试应用程序类
    }
}
