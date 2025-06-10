package cn.jizuiba.chat.memory.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("advisor/memory")
public class InMemoryChatController {

    private final ChatClient chatClient;
    private static final int MAX_MESSAGE = 100;
    private final MessageWindowChatMemory messageWindowChatMemory;

    public InMemoryChatController(ChatClient.Builder builder, ChatMemoryRepository chatMemoryRepository) {
        this.messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(MAX_MESSAGE)
                .build();

        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory).build()
                )
                .build();
    }

    @GetMapping("/simple")
    public String simpleChat(@RequestParam(value = "message", defaultValue = "你好，我叫Jizuiba，请记住。") String message,
                             @RequestParam(value = "conversation_id", defaultValue = "jizuiba") String conversationId) {

        return chatClient.prompt(message)
                .advisors(
                        a -> a.param(CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }

    @GetMapping("/messages")
    public List<Message> messages(@RequestParam(value = "conversation_id", defaultValue = "jizuiba") String conversationId) {
        return messageWindowChatMemory.get(conversationId);
    }
}
