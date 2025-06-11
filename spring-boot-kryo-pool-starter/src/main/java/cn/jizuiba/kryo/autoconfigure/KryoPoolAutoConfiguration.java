package cn.jizuiba.kryo.autoconfigure;

import cn.jizuiba.kryo.pool.DefaultKryoPool;
import cn.jizuiba.kryo.pool.KryoFactory;
import cn.jizuiba.kryo.pool.KryoPool;
import cn.jizuiba.kryo.serializer.DefaultKryoSerializer;
import cn.jizuiba.kryo.serializer.KryoSerializer;
import com.esotericsoftware.kryo.Kryo;
import jakarta.annotation.PreDestroy;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * Kryo Pool 自动配置类
 */
@AutoConfiguration
@ConditionalOnClass({Kryo.class, GenericObjectPoolConfig.class})
@ConditionalOnProperty(prefix = "kryo.pool", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KryoPoolProperties.class)
public class KryoPoolAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(KryoPoolAutoConfiguration.class);

    private KryoPool kryoPool;

    @Bean
    @ConditionalOnMissingBean
    public KryoFactory kryoFactory(KryoPoolProperties properties) {
        logger.info("Creating KryoFactory with references={}, registrationRequired={}",
                properties.isReferences(), properties.isRegistrationRequired());
        return new KryoFactory(properties.isReferences(), properties.isRegistrationRequired());
    }

    @Bean
    @ConditionalOnMissingBean
    public GenericObjectPoolConfig<Kryo> kryoPoolConfig(KryoPoolProperties properties) {
        GenericObjectPoolConfig<Kryo> config = new GenericObjectPoolConfig<>();

        config.setMaxTotal(properties.getMaxTotal());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        config.setMaxWait(Duration.ofMillis(properties.getMaxWaitMillis()));
        config.setTestOnBorrow(properties.isTestOnBorrow());
        config.setTestOnReturn(properties.isTestOnReturn());
        config.setTestWhileIdle(properties.isTestWhileIdle());
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(properties.getTimeBetweenEvictionRunsMillis()));
        config.setMinEvictableIdleDuration(Duration.ofMillis(properties.getMinEvictableIdleTimeMillis()));

        // 设置 LIFO 行为，提高缓存效果
        config.setLifo(true);

        logger.info("Created Kryo pool config: {}", config);
        return config;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public KryoPool kryoPool(KryoFactory kryoFactory, GenericObjectPoolConfig<Kryo> config) {
        this.kryoPool = new DefaultKryoPool(kryoFactory, config);
        logger.info("Created KryoPool instance");
        return this.kryoPool;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public KryoSerializer kryoSerializer(KryoPool kryoPool) {
        logger.info("Created KryoSerializer instance");
        return new DefaultKryoSerializer(kryoPool);
    }

    @PreDestroy
    public void destroy() {
        if (kryoPool != null) {
            logger.info("Closing KryoPool on application shutdown");
            kryoPool.close();
        }
    }
}

