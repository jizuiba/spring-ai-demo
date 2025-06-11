package cn.jizuiba.kryo.serializer;

/**
 * Kryo 序列化器接口
 */
public interface KryoSerializer {

    /**
     * 序列化对象为字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化字节数组为对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

    /**
     * 反序列化字节数组为对象（自动推断类型）
     */
    Object deserialize(byte[] bytes);

    /**
     * 深拷贝对象
     */
    <T> T copy(T obj);
}
