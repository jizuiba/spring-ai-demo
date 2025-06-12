package cn.jizuiba.vector.simple.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advisor/vector/simple")
public class VectorSimpleController {

    private final static Logger logger = LoggerFactory.getLogger(VectorSimpleController.class);

    private final SimpleVectorStore simpleVectorStore;
    private final static String OUTPUT_PATH = "D:\\workSpace\\spring-ai-demo\\rag-demo\\vector-simple-demo\\src\\main\\resources\\file.json";

    public VectorSimpleController(EmbeddingModel embeddingModel) {
        this.simpleVectorStore = SimpleVectorStore
                .builder(embeddingModel)
                .build();
    }

    /**
     * 添加文档到向量存储。
     */
    @GetMapping("/add")
    public void add() {
        logger.info("start add data");

        HashMap<String, Object> map = new HashMap<>();
        map.put("year", 2025);
        map.put("name", "jizuiba");
        List<Document> documents = List.of(
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("year", 2024)),
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", map),
                new Document("1", "test content", map));
        simpleVectorStore.add(documents);
    }

    /**
     * 根据文档ID删除文档。
     */
    @GetMapping("/delete")
    public void delete() {
        logger.info("start delete data");
        simpleVectorStore.delete(List.of("1"));
    }

    /**
     * 保存向量存储到文件。
     */
    @GetMapping("/save")
    public void save() {
        logger.info("start save data: {}", OUTPUT_PATH);
        File file = new File(OUTPUT_PATH);
        if (file.exists()) {
            file.delete();
        }
        simpleVectorStore.save(file);
    }

    /**
     * 从文件加载向量存储。
     */
    @GetMapping("/load")
    public void load() {
        logger.info("start load data: {}", OUTPUT_PATH);
        File file = new File(OUTPUT_PATH);
        simpleVectorStore.load(file);
    }

    /**
     * 相似度搜索，返回与查询最相关的文档。
     *
     * @return 匹配的文档列表
     */
    @GetMapping("/search")
    public List<Document> search() {
        logger.info("start search data");
        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring")
                .topK(2)
                .build());
    }

    /**
     * 带过滤条件的相似度搜索。
     *
     * @return 匹配的文档列表
     */
    @GetMapping("/search-filter")
    public List<Document> searchFilter() {
        logger.info("start search  filter data");
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression expression = b.and(
                b.in("year", 2025, 2024),
                b.eq("name", "jizuiba")
        ).build();

        return simpleVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("Spring")
                .topK(2)
                .filterExpression(expression).build());
    }
}
