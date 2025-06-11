package cn.jizuiba.kryo.pool;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * 基于 Apache Commons Pool2 的 Kryo 对象池实现
 */
public class DefaultKryoPool implements KryoPool {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKryoPool.class);

    private final GenericObjectPool<Kryo> pool;
    private final KryoFactory kryoFactory;

    public DefaultKryoPool(KryoFactory kryoFactory, GenericObjectPoolConfig<Kryo> config) {
        this.kryoFactory = kryoFactory;
        this.pool = new GenericObjectPool<>(new KryoPooledObjectFactory(), config);

        logger.info("Kryo pool initialized with config: maxTotal={}, maxIdle={}, minIdle={}",
                config.getMaxTotal(), config.getMaxIdle(), config.getMinIdle());
    }

    @Override
    public Kryo borrowKryo() {
        try {
            Kryo kryo = pool.borrowObject();
            logger.debug("Borrowed Kryo instance from pool. Active: {}, Idle: {}",
                    pool.getNumActive(), pool.getNumIdle());
            return kryo;
        } catch (Exception e) {
            logger.error("Failed to borrow Kryo instance from pool", e);
            throw new RuntimeException("Failed to borrow Kryo instance", e);
        }
    }

    @Override
    public void returnKryo(Kryo kryo) {
        if (kryo != null) {
            try {
                // 重置 Kryo 实例状态
                kryo.reset();
                pool.returnObject(kryo);
                logger.debug("Returned Kryo instance to pool. Active: {}, Idle: {}",
                        pool.getNumActive(), pool.getNumIdle());
            } catch (Exception e) {
                logger.error("Failed to return Kryo instance to pool", e);
                // 即使归还失败也不抛出异常，避免影响业务逻辑
            }
        }
    }

    @Override
    public <T> T execute(KryoCallback<T> callback) {
        Kryo kryo = borrowKryo();
        try {
            return callback.doWithKryo(kryo);
        } catch (Exception e) {
            logger.error("Error executing Kryo callback", e);
            throw new RuntimeException("Error executing Kryo callback", e);
        } finally {
            returnKryo(kryo);
        }
    }

    @Override
    public void execute(Consumer<Kryo> consumer) {
        Kryo kryo = borrowKryo();
        try {
            consumer.accept(kryo);
        } catch (Exception e) {
            logger.error("Error executing Kryo", e);
            throw new RuntimeException("Error executing Kryo", e);
        } finally {
            returnKryo(kryo);
        }
    }

    @Override
    public void close() {
        try {
            pool.close();
            logger.info("Kryo pool closed");
        } catch (Exception e) {
            logger.error("Error closing Kryo pool", e);
        }
    }

    @Override
    public PoolInfo getPoolInfo() {
        return new PoolInfo(
                pool.getNumActive(),
                pool.getNumIdle(),
                pool.getMaxTotal(),
                pool.getMaxIdle()
        );
    }

    /**
     * Kryo 对象工厂
     */
    private class KryoPooledObjectFactory extends BasePooledObjectFactory<Kryo> {

        @Override
        public Kryo create() throws Exception {
            Kryo kryo = kryoFactory.create();
            logger.debug("Created new Kryo instance");
            return kryo;
        }

        @Override
        public PooledObject<Kryo> wrap(Kryo kryo) {
            return new DefaultPooledObject<>(kryo);
        }

        @Override
        public boolean validateObject(PooledObject<Kryo> p) {
            // 简单验证 Kryo 实例是否可用
            try {
                Kryo kryo = p.getObject();
                return kryo != null;
            } catch (Exception e) {
                logger.warn("Kryo instance validation failed", e);
                return false;
            }
        }

        @Override
        public void destroyObject(PooledObject<Kryo> p) throws Exception {
            logger.debug("Destroyed Kryo instance");
            // Kryo 实例没有特殊的销毁逻辑
        }

        @Override
        public void passivateObject(PooledObject<Kryo> p) throws Exception {
            // 重置 Kryo 实例状态
            p.getObject().reset();
        }
    }
}
