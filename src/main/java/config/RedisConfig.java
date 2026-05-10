package config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisConfig {
    private static final String HOST = "localhost";
    private static final int    PORT = 6379;

    private static final JedisPool pool;

    static {
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(10);
        cfg.setMaxIdle(5);
        cfg.setMinIdle(1);
        pool = new JedisPool(cfg, HOST, PORT);
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void fecharPool() {
        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }

    private RedisConfig() {}
}
