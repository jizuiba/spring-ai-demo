package cn.jizuiba.chat.memory.controller;

import cn.jizuiba.chat.memory.config.FileChatMemoryRepository;
import cn.jizuiba.kryo.pool.KryoPool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("advisor/memory/file")
public class FileChatMemoryController {

    private final ChatClient chatClient;
    private final MessageWindowChatMemory messageWindowChatMemory;


    public FileChatMemoryController(ChatClient.Builder builder, KryoPool kryoPool) {

        FileChatMemoryRepository fileChatMemoryRepository = FileChatMemoryRepository.builder()
                .filePath("D:\\workSpace\\spring-ai-demo\\chat-memory-demo\\src\\main\\resources")
                .kryoPool(kryoPool)
                .build();

        this.messageWindowChatMemory = MessageWindowChatMemory.builder()
                .maxMessages(5)
                .chatMemoryRepository(fileChatMemoryRepository)
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
