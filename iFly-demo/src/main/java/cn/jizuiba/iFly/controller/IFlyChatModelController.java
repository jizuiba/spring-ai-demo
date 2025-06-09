package cn.jizuiba.iFly.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iFly/chat-model")
public class IFlyChatModelController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";
    private final ChatModel iFlyOpenAiChatModel;

    public IFlyChatModelController(ChatModel chatModel) {
        this.iFlyOpenAiChatModel = chatModel;
    }

    /**
     * 最简单的使用方式，没有任何 LLMs 参数注入。
     *
     * @return String types.
     */
    @GetMapping("/simple/chat")
    public String simpleChat() {
        try {
            return iFlyOpenAiChatModel.call(new Prompt(DEFAULT_PROMPT)).getResult().getOutput().getText();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error occurred while processing the request.";
        }
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.openai.OpenAiChatOptions}
     * 优先级高于在 application.yml 中配置的 LLMs 参数！
     */
    @GetMapping("/custom/chat")
    public String customChat() {
        OpenAiChatOptions customOptions = OpenAiChatOptions.builder()
                .topP(0.7)
                .model("lite")
                .maxTokens(1000)
                .temperature(0.8)
                .build();

        return iFlyOpenAiChatModel.call(new Prompt(DEFAULT_PROMPT, customOptions)).getResult().getOutput().getText();
    }
}
