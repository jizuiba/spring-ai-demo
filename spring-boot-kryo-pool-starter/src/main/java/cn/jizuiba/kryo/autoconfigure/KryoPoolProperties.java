package cn.jizuiba.kryo.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kryo Pool 配置属性
 */
@ConfigurationProperties(prefix = "kryo.pool")
public class KryoPoolProperties {

    /**
     * 是否启用 Kryo Pool
     */
    private boolean enabled = true;

    /**
     * 池中最大实例数
     */
    private int maxTotal = 8;

    /**
     * 池中最小空闲实例数
     */
    private int minIdle = 0;

    /**
     * 池中最大空闲实例数
     */
    private int maxIdle = 8;

    /**
     * 获取实例时最大等待时间（毫秒）
     */
    private long maxWaitMillis = -1L;

    /**
     * 是否在获取实例时进行验证
     */
    private boolean testOnBorrow = false;

    /**
     * 是否在归还实例时进行验证
     */
    private boolean testOnReturn = false;

    /**
     * 是否在空闲时进行验证
     */
    private boolean testWhileIdle = false;

    /**
     * 空闲实例检查间隔时间（毫秒）
     */
    private long timeBetweenEvictionRunsMillis = 30000L;

    /**
     * 实例在池中的最小空闲时间（毫秒）
     */
    private long minEvictableIdleTimeMillis = 60000L;

    /**
     * 是否启用引用跟踪（用于调试）
     */
    private boolean references = false;

    /**
     * 是否启用类注册
     */
    private boolean registrationRequired = false;

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isReferences() {
        return references;
    }

    public void setReferences(boolean references) {
        this.references = references;
    }

    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }
}