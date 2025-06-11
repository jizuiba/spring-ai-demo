package cn.jizuiba.kryo.pool;

import com.esotericsoftware.kryo.Kryo;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

/**
 * Kryo 对象池接口
 */
public interface KryoPool {

    /**
     * 获取 Kryo 实例
     */
    Kryo borrowKryo();

    /**
     * 归还 Kryo 实例
     */
    void returnKryo(Kryo kryo);

    /**
     * 使用 Kryo 实例执行操作
     */
    <T> T execute(KryoCallback<T> callback);

    /**
     * 使用 Kryo 实例执行操作 无返回值
     */
    void execute(Consumer<Kryo> consumer);

    /**
     * 关闭池
     */
    void close();

    /**
     * 获取池状态信息
     */
    PoolInfo getPoolInfo();

    /**
     *
     */


    /**
     * Kryo 回调接口
     */
    @FunctionalInterface
    interface KryoCallback<T> {
        T doWithKryo(Kryo kryo) throws Exception;
    }

    /**
     * 池状态信息
     */
    record PoolInfo(int active, int idle, int maxTotal, int maxIdle) {

        @Override
        @NonNull
        public String toString() {
            return String.format("PoolInfo{active=%d, idle=%d, maxTotal=%d, maxIdle=%d}",
                    active, idle, maxTotal, maxIdle);
        }
    }
}
