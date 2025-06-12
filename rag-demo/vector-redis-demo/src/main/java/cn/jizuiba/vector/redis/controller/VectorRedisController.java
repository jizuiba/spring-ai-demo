package cn.jizuiba.vector.redis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advisor/vector/redis")
public class VectorRedisController {

    private final static Logger logger = LoggerFactory.getLogger(VectorRedisController.class);

    private final RedisVectorStore redisVectorStore;


    public VectorRedisController(RedisVectorStore redisVectorStore) {
        this.redisVectorStore = redisVectorStore;
    }

    /**
     * 导入示例数据到 Redis 向量存储。
     * 创建包含不同元数据的 Document 列表，并批量添加到 redisVectorStore。
     */
    @GetMapping("/add")
    public void add() {
        logger.info("start add data");

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", "12345");
        map.put("year", "2025");
        map.put("name", "jizuiba");
        List<Document> documents = List.of(
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("year", 2024)),
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", map));
        redisVectorStore.add(documents);
    }

    /**
     * 执行向量相似度搜索，返回与查询词“Spring”最相似的前2条Document。
     *
     * @return 匹配的Document列表
     */
    @GetMapping("/search")
    public List<Document> search() {
        logger.info("start search data");
        return redisVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring")
                .topK(2)
                .build());
    }

    /**
     * 根据过滤条件删除 Redis 向量存储中的数据。
     * 此方法会删除所有 name 字段为 "jizuiba" 的向量数据。
     */
    @GetMapping("delete-filter")
    public void searchFilter() {
        logger.info("start delete data with filter");
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression expression = b.eq("name", "jizuiba").build();

        redisVectorStore.delete(expression);
    }
}
