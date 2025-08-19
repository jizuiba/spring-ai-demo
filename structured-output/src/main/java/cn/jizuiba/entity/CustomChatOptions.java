package cn.jizuiba.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.lang.Nullable;

import java.util.*;

public class CustomChatOptions implements ToolCallingChatOptions {

    // Core chat options
    private String model;
    private Double temperature;
    private Double topP;
    private Integer topK;
    private Integer maxTokens;
    private Integer maxCompletionTokens;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private List<String> stopSequences;

    // Tool calling options
    private List<ToolCallback> toolCallbacks;
    private Set<String> toolNames;
    private Boolean internalToolExecutionEnabled;
    private Map<String, Object> toolContext;

    // Additional options
    private Integer n;
    private Integer seed;
    private String user;
    private Map<String, String> httpHeaders;
    private Map<String, String> metadata;
    private Boolean streamUsage;

    @JsonProperty("enable_thinking")
    private Boolean enableThinking;

    /**
     * Default constructor
     */
    public CustomChatOptions() {
        this.toolCallbacks = new ArrayList<>();
        this.toolNames = new HashSet<>();
        this.toolContext = new HashMap<>();
        this.httpHeaders = new HashMap<>();
        this.metadata = new HashMap<>();
        this.enableThinking = false;
    }

    /**
     * Builder pattern for creating CustomChatOptions
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create CustomChatOptions from another CustomChatOptions instance
     */
    public static CustomChatOptions fromOptions(CustomChatOptions fromOptions) {
        CustomChatOptions options = new CustomChatOptions();
        if (fromOptions != null) {
            options.setModel(fromOptions.getModel());
            options.setTemperature(fromOptions.getTemperature());
            options.setTopP(fromOptions.getTopP());
            options.setTopK(fromOptions.getTopK());
            options.setMaxTokens(fromOptions.getMaxTokens());
            options.setMaxCompletionTokens(fromOptions.getMaxCompletionTokens());
            options.setFrequencyPenalty(fromOptions.getFrequencyPenalty());
            options.setPresencePenalty(fromOptions.getPresencePenalty());
            options.setStopSequences(fromOptions.getStopSequences());
            options.setToolCallbacks(fromOptions.getToolCallbacks());
            options.setToolNames(fromOptions.getToolNames());
            options.setInternalToolExecutionEnabled(fromOptions.getInternalToolExecutionEnabled());
            options.setToolContext(fromOptions.getToolContext());
            options.setN(fromOptions.getN());
            options.setSeed(fromOptions.getSeed());
            options.setUser(fromOptions.getUser());
            options.setHttpHeaders(fromOptions.getHttpHeaders());
            options.setMetadata(fromOptions.getMetadata());
            options.setStreamUsage(fromOptions.getStreamUsage());
            options.setEnableThinking(fromOptions.getEnableThinking());
        }
        return options;
    }

    // ChatOptions interface methods
    @Override
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    @Override
    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    @Override
    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    @Override
    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    @Override
    public List<String> getStopSequences() {
        return stopSequences;
    }

    public void setStopSequences(List<String> stopSequences) {
        this.stopSequences = stopSequences;
    }

    @Override
    public CustomChatOptions copy() {
        return fromOptions(this);
    }

    // ToolCallingChatOptions interface methods
    @Override
    public List<ToolCallback> getToolCallbacks() {
        return toolCallbacks;
    }

    @Override
    public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
        this.toolCallbacks = toolCallbacks != null ? toolCallbacks : new ArrayList<>();
    }

    @Override
    public Set<String> getToolNames() {
        return toolNames;
    }

    @Override
    public void setToolNames(Set<String> toolNames) {
        this.toolNames = toolNames != null ? toolNames : new HashSet<>();
    }

    @Override
    @Nullable
    public Boolean getInternalToolExecutionEnabled() {
        return internalToolExecutionEnabled;
    }

    @Override
    public void setInternalToolExecutionEnabled(@Nullable Boolean internalToolExecutionEnabled) {
        this.internalToolExecutionEnabled = internalToolExecutionEnabled;
    }

    @Override
    public Map<String, Object> getToolContext() {
        return toolContext;
    }

    @Override
    public void setToolContext(Map<String, Object> toolContext) {
        this.toolContext = toolContext != null ? toolContext : new HashMap<>();
    }

    // Additional getters and setters
    public Integer getMaxCompletionTokens() {
        return maxCompletionTokens;
    }

    public void setMaxCompletionTokens(Integer maxCompletionTokens) {
        this.maxCompletionTokens = maxCompletionTokens;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders != null ? httpHeaders : new HashMap<>();
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public Boolean getStreamUsage() {
        return streamUsage;
    }

    public void setStreamUsage(Boolean streamUsage) {
        this.streamUsage = streamUsage;
    }

    // Custom field - enableThinking
    public Boolean getEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomChatOptions that = (CustomChatOptions) o;
        return Objects.equals(model, that.model) &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(topP, that.topP) &&
                Objects.equals(topK, that.topK) &&
                Objects.equals(maxTokens, that.maxTokens) &&
                Objects.equals(maxCompletionTokens, that.maxCompletionTokens) &&
                Objects.equals(frequencyPenalty, that.frequencyPenalty) &&
                Objects.equals(presencePenalty, that.presencePenalty) &&
                Objects.equals(stopSequences, that.stopSequences) &&
                Objects.equals(toolCallbacks, that.toolCallbacks) &&
                Objects.equals(toolNames, that.toolNames) &&
                Objects.equals(internalToolExecutionEnabled, that.internalToolExecutionEnabled) &&
                Objects.equals(toolContext, that.toolContext) &&
                Objects.equals(n, that.n) &&
                Objects.equals(seed, that.seed) &&
                Objects.equals(user, that.user) &&
                Objects.equals(httpHeaders, that.httpHeaders) &&
                Objects.equals(metadata, that.metadata) &&
                Objects.equals(streamUsage, that.streamUsage) &&
                Objects.equals(enableThinking, that.enableThinking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, temperature, topP, topK, maxTokens, maxCompletionTokens,
                frequencyPenalty, presencePenalty, stopSequences, toolCallbacks, toolNames,
                internalToolExecutionEnabled, toolContext, n, seed, user, httpHeaders,
                metadata, streamUsage, enableThinking);
    }

    @Override
    public String toString() {
        return "CustomChatOptions{" +
                "model='" + model + '\'' +
                ", temperature=" + temperature +
                ", topP=" + topP +
                ", topK=" + topK +
                ", maxTokens=" + maxTokens +
                ", maxCompletionTokens=" + maxCompletionTokens +
                ", frequencyPenalty=" + frequencyPenalty +
                ", presencePenalty=" + presencePenalty +
                ", stopSequences=" + stopSequences +
                ", toolCallbacks=" + toolCallbacks +
                ", toolNames=" + toolNames +
                ", internalToolExecutionEnabled=" + internalToolExecutionEnabled +
                ", toolContext=" + toolContext +
                ", n=" + n +
                ", seed=" + seed +
                ", user='" + user + '\'' +
                ", httpHeaders=" + httpHeaders +
                ", metadata=" + metadata +
                ", streamUsage=" + streamUsage +
                ", enableThinking=" + enableThinking +
                '}';
    }

    /**
     * Builder class for CustomChatOptions
     */
    public static class Builder {
        private final CustomChatOptions options;

        public Builder() {
            this.options = new CustomChatOptions();
        }

        public Builder model(String model) {
            options.setModel(model);
            return this;
        }

        public Builder temperature(Double temperature) {
            options.setTemperature(temperature);
            return this;
        }

        public Builder topP(Double topP) {
            options.setTopP(topP);
            return this;
        }

        public Builder topK(Integer topK) {
            options.setTopK(topK);
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }

        public Builder maxCompletionTokens(Integer maxCompletionTokens) {
            options.setMaxCompletionTokens(maxCompletionTokens);
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {
            options.setFrequencyPenalty(frequencyPenalty);
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {
            options.setPresencePenalty(presencePenalty);
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {
            options.setStopSequences(stopSequences);
            return this;
        }

        public Builder toolCallbacks(List<ToolCallback> toolCallbacks) {
            options.setToolCallbacks(toolCallbacks);
            return this;
        }

        public Builder toolNames(Set<String> toolNames) {
            options.setToolNames(toolNames);
            return this;
        }

        public Builder internalToolExecutionEnabled(Boolean internalToolExecutionEnabled) {
            options.setInternalToolExecutionEnabled(internalToolExecutionEnabled);
            return this;
        }

        public Builder toolContext(Map<String, Object> toolContext) {
            options.setToolContext(toolContext);
            return this;
        }

        public Builder n(Integer n) {
            options.setN(n);
            return this;
        }

        public Builder seed(Integer seed) {
            options.setSeed(seed);
            return this;
        }

        public Builder user(String user) {
            options.setUser(user);
            return this;
        }

        public Builder httpHeaders(Map<String, String> httpHeaders) {
            options.setHttpHeaders(httpHeaders);
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            options.setMetadata(metadata);
            return this;
        }

        public Builder streamUsage(Boolean streamUsage) {
            options.setStreamUsage(streamUsage);
            return this;
        }

        public Builder enableThinking(Boolean enableThinking) {
            options.setEnableThinking(enableThinking);
            return this;
        }

        public CustomChatOptions build() {
            return options.copy();
        }
    }
}
