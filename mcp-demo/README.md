# SpringBoot AI MCP demo

## Introduction

基于阿里百炼模型实现mcp示例demo

-[X] stdio方式
-[X] sse方式
-[ ] streamableHttp方式

| 传输方式       | 全称                  | 描述                                                         |
| -------------- | --------------------- | ------------------------------------------------------------ |
| stdio          | Standard Input/Output | 使用标准输入输出进行通信，通常用于本地运行的模型进程         |
| sse            | Server-Sent Events    | 基于 HTTP 的事件流协议，适合从服务器向客户端推送消息         |
| streamableHttp | Streamable HTTP       | 支持流式响应的 HTTP 协议，常用于远程模型服务（如 OpenAI、自建 API） |

| 特性         | stdio     | sse       | streamableHttp |
| ------------ | --------- | --------- | -------------- |
| 是否本地     | ✅ 是      | ❌ 否      | ❌ 否           |
| 是否远程     | ❌ 否      | ✅ 是      | ✅ 是           |
| 是否流式     | ✅ 是      | ✅ 是      | ✅ 是           |
| 是否双向通信 | ❌（单向） | ❌（单向） | ✅（可双向）    |
| 浏览器兼容   | 不适用    | ✅         | ✅              |


## 参考文档
- https://github.com/springaialibaba/spring-ai-alibaba-examples/tree/main/spring-ai-alibaba-mcp-example/spring-ai-alibaba-mcp-starter-example
- https://java2ai.com/docs/dev/get-started/
- https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html