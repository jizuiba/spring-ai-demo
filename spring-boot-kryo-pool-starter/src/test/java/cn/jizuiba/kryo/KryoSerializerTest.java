package cn.jizuiba.kryo;

import cn.jizuiba.kryo.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KryoSerializerTest.TestApplication.class)
@TestPropertySource(properties = {
        "kryo.pool.enabled=true",
        "kryo.pool.max-total=4",
        "kryo.pool.max-idle=2"
})
class KryoSerializerTest {

    @Autowired
    private KryoSerializer kryoSerializer;

    @Test
    void testSerializeAndDeserializeString() {
        String original = "Hello, Kryo!";

        byte[] serialized = kryoSerializer.serialize(original);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);

        String deserialized = kryoSerializer.deserialize(serialized, String.class);
        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeAndDeserializeInteger() {
        Integer original = 12345;

        byte[] serialized = kryoSerializer.serialize(original);
        assertNotNull(serialized);

        Integer deserialized = kryoSerializer.deserialize(serialized, Integer.class);
        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeAndDeserializeComplexObject() {
        TestObject original = new TestObject();
        original.setId(123L);
        original.setName("Test Object");
        original.setCreatedAt(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
        original.setAmount(BigDecimal.valueOf(99.99));
        original.setTags(Arrays.asList("tag1", "tag2", "tag3"));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 42);
        original.setMetadata(metadata);

        byte[] serialized = kryoSerializer.serialize(original);
        assertNotNull(serialized);

        TestObject deserialized = kryoSerializer.deserialize(serialized, TestObject.class);
        assertNotNull(deserialized);
        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getCreatedAt(), deserialized.getCreatedAt());
        assertEquals(original.getAmount(), deserialized.getAmount());
        assertEquals(original.getTags(), deserialized.getTags());
        assertEquals(original.getMetadata(), deserialized.getMetadata());
    }

    @Test
    void testSerializeAndDeserializeCollection() {
        List<String> original = Arrays.asList("item1", "item2", "item3");

        byte[] serialized = kryoSerializer.serialize(original);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        List<String> deserialized = (List<String>) kryoSerializer.deserialize(serialized);
        assertEquals(original, deserialized);
    }

    @Test
    void testDeepCopy() {
        TestObject original = new TestObject();
        original.setId(456L);
        original.setName("Original Object");
        original.setTags(new ArrayList<>(Arrays.asList("tag1", "tag2")));

        TestObject copy = kryoSerializer.copy(original);

        assertNotNull(copy);
        assertNotSame(original, copy);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getTags(), copy.getTags());
        assertNotSame(original.getTags(), copy.getTags());

        // 修改拷贝不应该影响原对象
        copy.getTags().add("new-tag");
        assertNotEquals(original.getTags().size(), copy.getTags().size());
    }

    @Test
    void testSerializeNull() {
        byte[] serialized = kryoSerializer.serialize(null);
        assertNull(serialized);

        Object deserialized = kryoSerializer.deserialize(null);
        assertNull(deserialized);

        Object copied = kryoSerializer.copy(null);
        assertNull(copied);
    }

    @Test
    void testSerializeEmptyCollection() {
        List<String> emptyList = new ArrayList<>();

        byte[] serialized = kryoSerializer.serialize(emptyList);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        List<String> deserialized = (List<String>) kryoSerializer.deserialize(serialized);
        assertNotNull(deserialized);
        assertTrue(deserialized.isEmpty());
    }

    @Test
    void testSerializeMap() {
        Map<String, Integer> original = new HashMap<>();
        original.put("one", 1);
        original.put("two", 2);
        original.put("three", 3);

        byte[] serialized = kryoSerializer.serialize(original);
        assertNotNull(serialized);

        @SuppressWarnings("unchecked")
        Map<String, Integer> deserialized = (Map<String, Integer>) kryoSerializer.deserialize(serialized);
        assertEquals(original, deserialized);
    }

    // 测试用的内部类
    public static class TestObject {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
        private BigDecimal amount;
        private List<String> tags;
        private Map<String, Object> metadata;

        // 默认构造函数（Kryo需要）
        public TestObject() {
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(createdAt, that.createdAt) &&
                    Objects.equals(amount, that.amount) &&
                    Objects.equals(tags, that.tags) &&
                    Objects.equals(metadata, that.metadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, createdAt, amount, tags, metadata);
        }
    }

    // 测试应用程序配置
    @SpringBootApplication
    static class TestApplication {
        // 空的测试应用程序类
    }
}
