package cn.jizuiba.iFly.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("iFly/chat-client")
public class IFlyChatClientController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient iFlyOpenAiChatClient;
    private final ChatModel chatModel;

    public IFlyChatClientController(ChatModel chatModel) {
        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.iFlyOpenAiChatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .topP(0.7)
                                .build()
                )
                .build();
    }

    // 也可以使用如下的方式注入 ChatClient
    // public IFlyChatClientController(ChatClient.Builder chatClientBuilder) {
    //
    //  	this.iFlyOpenAiChatClient = chatClientBuilder.build();
    // }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/simple/chat")
    public String simpleChat() {

        return iFlyOpenAiChatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return iFlyOpenAiChatClient.prompt(DEFAULT_PROMPT).stream().content();
    }

    /**
     * ChatClient 流式响应
     */
    @GetMapping(value = "/stream/response")
    public Flux<ServerSentEvent<String>> simpleChat(@RequestParam("message") String message) {
        return iFlyOpenAiChatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(content -> ServerSentEvent.<String>builder()
                        .data(content)
                        .build());
    }
}
