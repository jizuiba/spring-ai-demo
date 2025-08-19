package cn.jizuiba.alibaba.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("alibaba/chat-client")
public class AlibabaChatClientController {

    private final static Logger log = LoggerFactory.getLogger(AlibabaChatClientController.class);

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient alibabaChatClient;

    public AlibabaChatClientController(ChatClient.Builder builder) {

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.alibabaChatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
//                                .withModel("qwen3-4b")
//                                .withEnableThinking(true)
                                .build()
                )
                .build();
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/simple/chat")
    public String simpleChat() {

        return alibabaChatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat() {
        return alibabaChatClient.prompt(DEFAULT_PROMPT).stream().content();
    }

    /**
     * ChatClient 流式响应
     */
    @GetMapping(value = "/stream/response")
    public Flux<ServerSentEvent<String>> simpleChat(@RequestParam(name = "message", defaultValue = "你是谁？") String message) {
        try {
            return alibabaChatClient.prompt()
                    .user(message)
                    .stream()
                    .content()
                    .map(content -> ServerSentEvent.<String>builder()
                            .data(content)
                            .build());
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage(), e);
        }
        return Flux.just(ServerSentEvent.<String>builder().data("错误").build());
    }
}
