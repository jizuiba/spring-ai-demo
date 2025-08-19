package cn.jizuiba.controller;

import cn.jizuiba.entity.CustomChatOptions;
import cn.jizuiba.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/output-converter")
public class OutputConverterController {

    private final static Logger log = LoggerFactory.getLogger(OutputConverterController.class);

    private final ChatClient chatClient;

    public OutputConverterController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(
                        CustomChatOptions.builder()
//                                .model("Qwen/Qwen2-7B-Instruct")
                                .enableThinking(false)
                                .build()
                )
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    /**
     * 聊天格式接口，接收查询参数并返回转换为Person对象的AI聊天结果
     *
     * @param query 用户查询内容，默认值为"请尝试给 'jizuiba' 分配5个随机用户标签。"
     * @return ResponseEntity<Person> 包含转换为Person对象的AI聊天结果的响应实体
     */
    @GetMapping("/chat-format")
    public ResponseEntity<Person> ChatFormat(@RequestParam(name = "query", defaultValue = "请尝试给 'jizuiba' 分配5个随机用户标签。") String query) {
        // 调用聊天客户端获取AI响应结果，并将其转换为Person对象
        Person result = this.chatClient.prompt(query)
                .call()
                .entity(Person.class);

        return ResponseEntity.ok(result);
    }

    /**
     * 复杂聊天格式接口，接收查询参数并返回转换为Person列表的AI聊天结果
     *
     * @param query 用户查询内容，默认值为"请尝试分别给 'jizuiba' 和 'laowang' 分配5个随机用户标签。"
     * @return ResponseEntity<List<Person>> 包含转换为Person列表的AI聊天结果的响应实体
     */
    @GetMapping("/complex-chat-format")
    public ResponseEntity<List<Person>> ComplexChatFormat(@RequestParam(name = "query", defaultValue = "请尝试分别给 'jizuiba' 和 'laowang' 分配5个随机用户标签。") String query) {
        // 调用聊天客户端获取AI响应结果，并将其转换为Person列表
        List<Person> result = this.chatClient.prompt(query)
                .call()
                .entity(new ParameterizedTypeReference<List<Person>>() {
                });

        return ResponseEntity.ok(result);
    }
}
