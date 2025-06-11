package cn.jizuiba.kryo.serializer;

import cn.jizuiba.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 默认 Kryo 序列化器实现
 */
public class DefaultKryoSerializer implements KryoSerializer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKryoSerializer.class);

    private final KryoPool kryoPool;

    public DefaultKryoSerializer(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return null;
        }

        return kryoPool.execute(kryo -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 Output output = new Output(baos)) {

                kryo.writeClassAndObject(output, obj);
                output.flush();

                byte[] result = baos.toByteArray();
                logger.debug("Serialized object of type {} to {} bytes",
                        obj.getClass().getSimpleName(), result.length);
                return result;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return kryoPool.execute(kryo -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                 Input input = new Input(bais)) {

                Object obj = kryo.readClassAndObject(input);
                logger.debug("Deserialized {} bytes to object of type {}",
                        bytes.length, obj != null ? obj.getClass().getSimpleName() : "null");

                if (obj != null && !clazz.isInstance(obj)) {
                    throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + clazz);
                }

                return (T) obj;
            }
        });
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return kryoPool.execute(kryo -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                 Input input = new Input(bais)) {

                Object obj = kryo.readClassAndObject(input);
                logger.debug("Deserialized {} bytes to object of type {}",
                        bytes.length, obj != null ? obj.getClass().getSimpleName() : "null");
                return obj;
            }
        });
    }

    @Override
    public <T> T copy(T obj) {
        if (obj == null) {
            return null;
        }

        return kryoPool.execute(kryo -> {
            T copy = (T) kryo.copy(obj);
            logger.debug("Deep copied object of type {}", obj.getClass().getSimpleName());
            return copy;
        });
    }
}
