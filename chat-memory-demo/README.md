# Spring AI Memory chat demo

## Introduction

实现聊天记忆功能

### Spring AI
- 提供InMemory实现

### Spring AI Alibaba
- 提供Redis实现
- 提供MySQL实现

### 文件聊天记忆功能

1. 基于 kryo 实现 Pool，提供starter

2. 实现ChatMemoryRepository完成自定义FileChatMemoryRepository 

### 部分参数解释：
- MAX_MESSAGE：默认为20条上下文信息，达到后会删除旧消息

## 参考文档
- https://github.com/springaialibaba/spring-ai-alibaba-examples/tree/main/spring-ai-alibaba-chat-memory-example
- https://docs.spring.io/spring-ai/reference/1.1-SNAPSHOT/api/chat-memory.html