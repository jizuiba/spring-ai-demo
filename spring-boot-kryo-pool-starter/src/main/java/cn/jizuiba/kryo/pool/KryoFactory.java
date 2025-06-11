package cn.jizuiba.kryo.pool;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Kryo 实例工厂
 */
public class KryoFactory {

    private final boolean references;
    private final boolean registrationRequired;

    public KryoFactory(boolean references, boolean registrationRequired) {
        this.references = references;
        this.registrationRequired = registrationRequired;
    }

    /**
     * 创建 Kryo 实例
     */
    public Kryo create() {
        Kryo kryo = new Kryo();

        // 设置引用跟踪
        kryo.setReferences(references);

        // 设置是否需要注册类
        kryo.setRegistrationRequired(registrationRequired);

        // 设置实例化策略
        DefaultInstantiatorStrategy instantiatorStrategy = new DefaultInstantiatorStrategy();
        instantiatorStrategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.setInstantiatorStrategy(instantiatorStrategy);

        // 注册常用类
        registerCommonClasses(kryo);

        return kryo;
    }

    /**
     * 注册常用类
     */
    private void registerCommonClasses(Kryo kryo) {
        // 基础类型已经自动注册，这里可以注册一些常用的类
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.LinkedList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.util.LinkedHashMap.class);
        kryo.register(java.util.TreeMap.class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.LinkedHashSet.class);
        kryo.register(java.util.TreeSet.class);
        kryo.register(java.util.Date.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(java.time.LocalDate.class);
        kryo.register(java.time.LocalTime.class);
        kryo.register(java.math.BigDecimal.class);
        kryo.register(java.math.BigInteger.class);
    }
}
