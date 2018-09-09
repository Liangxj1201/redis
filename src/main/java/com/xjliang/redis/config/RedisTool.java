package com.xjliang.redis.config;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.util.Collections;

public class RedisTool {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private static final Long RELEASE_SUCCESS = 1L;
    /**
     * 尝试获取分布式锁
     * @param
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(RedisTemplate redisTemplate, String lockKey, String requestId, int expireTime) {

        /*String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;*/

        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Jedis jedis = (Jedis) redisConnection.getNativeConnection();
                String resultStr = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                return resultStr;
            }
        });
        if (LOCK_SUCCESS.equals(result.toString())) {
            return true;
        }
        return false;

    }
    /**
     * 释放分布式锁
     * @param
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(RedisTemplate redisTemplate, String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        Object result = redisTemplate.execute(new RedisCallback() {

            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Jedis jedis = (Jedis) redisConnection.getNativeConnection();
                Object resultStr = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
                return resultStr;
            }
        });
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
}
