package cn.jizuiba.chat.memory.config;

import cn.jizuiba.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileChatMemoryRepository implements ChatMemoryRepository, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(FileChatMemoryRepository.class);

    private static final String FILE_PREFIX_DEFAULT = "kryo_";
    private static final String FILE_EXTENSION_DEFAULT = ".kryo";

    private final KryoPool kryoPool;
    private final String filePath;

    private FileChatMemoryRepository(String filePath, KryoPool kryoPool) {
        Assert.notNull(kryoPool, "kryoPool cannot be null");
        Assert.notNull(filePath, "filePath cannot be null");
        this.filePath = filePath;
        this.kryoPool = kryoPool;
    }

    public static FileBuilder builder() {
        return new FileBuilder();
    }

    @Override
    public List<String> findConversationIds() {
        File directory = new File(filePath);
        List<String> conversationIds = new ArrayList<>();

        Pattern pattern = Pattern.compile("^kryo_(.*)\\.kryo$");

        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                String fileName = file.getName();
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.matches()) {
                    conversationIds.add(matcher.group(1));
                }
            }
        } else {
            logger.warn("The path provided is not a valid directory");
        }
        return conversationIds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> findByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");

        AtomicReference<List<Message>> messages = new AtomicReference<>(new ArrayList<>());
        File file = getConversationFile(conversationId);
        if (!file.exists()) {
            return messages.get();
        }

        try (Input input = new Input(new FileInputStream(file))) {
            kryoPool.execute(kryo -> {
                messages.set(kryo.readObject(input, ArrayList.class));
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return messages.get();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");

        this.deleteByConversationId(conversationId);
        try (Output output = new Output(new FileOutputStream(getConversationFile(conversationId)))) {
            kryoPool.execute(kryo -> {
                kryo.writeObject(output, messages);
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    @Override
    public void close() throws Exception {
        if (this.kryoPool != null) {
            this.kryoPool.close();
        }
    }

    public static class FileBuilder {
        private String filePath;
        private KryoPool kryoPool;

        public FileBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public FileBuilder kryoPool(KryoPool kryoPool) {
            this.kryoPool = kryoPool;
            return this;
        }

        public FileChatMemoryRepository build() {
            return new FileChatMemoryRepository(filePath, kryoPool);
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(filePath, FILE_PREFIX_DEFAULT + conversationId + FILE_EXTENSION_DEFAULT);
    }
}
