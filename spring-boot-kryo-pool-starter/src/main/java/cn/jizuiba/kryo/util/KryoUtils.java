package cn.jizuiba.kryo.util;

import cn.jizuiba.kryo.pool.KryoPool;
import cn.jizuiba.kryo.serializer.KryoSerializer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Kryo 工具类
 */
@Component
public class KryoUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        KryoUtils.applicationContext = applicationContext;
    }

    /**
     * 获取 KryoPool 实例
     */
    public static KryoPool getKryoPool() {
        return applicationContext.getBean(KryoPool.class);
    }

    /**
     * 获取 KryoSerializer 实例
     */
    public static KryoSerializer getKryoSerializer() {
        return applicationContext.getBean(KryoSerializer.class);
    }

    /**
     * 序列化对象
     */
    public static byte[] serialize(Object obj) {
        return getKryoSerializer().serialize(obj);
    }

    /**
     * 反序列化对象
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return getKryoSerializer().deserialize(bytes, clazz);
    }

    /**
     * 反序列化对象（自动推断类型）
     */
    public static Object deserialize(byte[] bytes) {
        return getKryoSerializer().deserialize(bytes);
    }

    /**
     * 深拷贝对象
     */
    public static <T> T copy(T obj) {
        return getKryoSerializer().copy(obj);
    }
}