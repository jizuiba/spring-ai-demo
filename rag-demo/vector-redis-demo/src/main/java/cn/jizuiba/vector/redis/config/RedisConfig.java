package cn.jizuiba.vector.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPooled;

/**
 * NOTE:
 * 仅在需要自定义或仅引入
 * <dependency>
 *     <groupId>org.springframework.ai</groupId>
 *     <artifactId>spring-ai-redis-store</artifactId>
 * </dependency>
 * 时需要手动配置。
 */
//@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);


    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.username}")
    private String username;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.ai.vectorstore.redis.prefix}")
    private String prefix;
    @Value("${spring.ai.vectorstore.redis.index-name}")
    private String indexName;

    @Bean
    public JedisPooled jedisPooled() {
        logger.info("Redis host: {}, port: {}", host, port);
        return new JedisPooled(host, port, username, password);
    }

    @Bean
    @Qualifier("redisVectorStoreCustom")
    public RedisVectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        logger.info("create redis vector store");
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(indexName)                           // Optional: defaults to "spring-ai-index"
                .prefix(prefix)                                 // Optional: defaults to "embedding:"
                .metadataFields(                                // Optional: define metadata fields for filtering
                        RedisVectorStore.MetadataField.tag("name"),
                        RedisVectorStore.MetadataField.numeric("year"))
                .initializeSchema(true)                         // Optional: defaults to false
                .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
                .build();
    }
}
