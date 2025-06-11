package cn.jizuiba.kryo;

import cn.jizuiba.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.Kryo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KryoPoolTest.TestApplication.class)
@TestPropertySource(properties = {
        "kryo.pool.enabled=true",
        "kryo.pool.max-total=8",
        "kryo.pool.max-idle=4",
        "kryo.pool.min-idle=1"
})
class KryoPoolTest {

    @Autowired
    private KryoPool kryoPool;

    @Test
    void testBorrowAndReturnKryo() {
        // 测试基本的借用和归还功能
        Kryo kryo = kryoPool.borrowKryo();
        assertNotNull(kryo);

        kryoPool.returnKryo(kryo);

        // 验证池状态
        KryoPool.PoolInfo poolInfo = kryoPool.getPoolInfo();
        assertTrue(poolInfo.idle() >= 0);
        assertTrue(poolInfo.active() >= 0);
    }

    @Test
    void testExecuteCallback() {
        // 测试回调执行
        String result = kryoPool.execute(kryo -> {
            assertNotNull(kryo);
            return "test-result";
        });

        assertEquals("test-result", result);
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // 测试并发访问
        int threadCount = 5;
        int operationsPerThread = 20;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int item = j;
                        kryoPool.execute(kryo -> {
                            assertNotNull(kryo);
                            // 模拟一些序列化操作
                            List<String> testData = new ArrayList<>();
                            testData.add("item-" + item);
                            kryo.copy(testData);
                            successCount.incrementAndGet();
                            return null;
                        });
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread, successCount.get());
    }

    @Test
    void testPoolInfo() {
        KryoPool.PoolInfo poolInfo = kryoPool.getPoolInfo();
        assertNotNull(poolInfo);
        assertTrue(poolInfo.maxTotal() > 0);
        assertTrue(poolInfo.maxIdle() >= 0);
        assertNotNull(poolInfo.toString());
    }

    @Test
    void testMultipleBorrowReturn() {
        // 测试多次借用和归还
        List<Kryo> kryos = new ArrayList<>();

        // 借用多个实例
        for (int i = 0; i < 3; i++) {
            Kryo kryo = kryoPool.borrowKryo();
            assertNotNull(kryo);
            kryos.add(kryo);
        }

        // 归还所有实例
        for (Kryo kryo : kryos) {
            kryoPool.returnKryo(kryo);
        }

        // 验证池状态
        KryoPool.PoolInfo poolInfo = kryoPool.getPoolInfo();
        assertTrue(poolInfo.active() >= 0);
        assertTrue(poolInfo.idle() >= 0);
    }

    // 测试应用程序配置
    @SpringBootApplication
    static class TestApplication {
        // 空的测试应用程序类
    }
}