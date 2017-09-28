package com.wxserver.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangmaocheng on 2015/9/10.
 */
public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    public static Pool pool = null;

    /**
     * 默认时间为一天
     */
    public static final int expireTime = 86400;

    public static final String separator = ":";

    static {
        PropertiesLoader loader = new PropertiesLoader("redis.properties");
        JedisPoolConfig config = new JedisPoolConfig();
        //最大空闲连接数, 默认8个
        config.setMaxIdle(loader.getInteger("redis.pool.maxIdle", 8));
        //最大连接数, 默认8个
        config.setMaxTotal(loader.getInteger("redis.pool.maxTotal", 8));
        //最小空闲连接数, 默认0
        config.setMinIdle(loader.getInteger("redis.pool.minIdle", 0));
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(loader.getInteger("redis.pool.maxWaitMillis", -1));
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(loader.getBoolean("redis.pool.testOnBorrow", false));
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(loader.getBoolean("redis.pool.testWhileIdle", false));
        config.setTestOnReturn(loader.getBoolean("redis.pool.testOnReturn", false));

        int timeout = loader.getInteger("redis.timeout", 5000);
        String password = StringUtils.isBlank(loader.getProperty("redis.password", null)) ?
                null : loader.getProperty("redis.password", null);
        int database = loader.getInteger("redis.database");

        boolean isSentinel = loader.getBoolean("redis.isSentinel", false);
        if (isSentinel) {
            String master = "mymaster";
            Set<String> sentinels = new HashSet<String>();
            sentinels.add(loader.getProperty("sentinel1.address"));
            sentinels.add(loader.getProperty("sentinel2.address"));
            pool = new JedisSentinelPool(master, sentinels, config, timeout, password, database);//timeout 读取超时
        } else {
            String host = loader.getProperty("redis.host");
            int port = loader.getInteger("redis.port", 6379);
            pool = new JedisPool(config, host, port, timeout, password, database);//timeout 读取超时
        }
    }

    /**
     * 获取系统默认jedis实例
     *
     * @return
     */
    public static Jedis getJedis() {
        return getJedis(pool);
    }

    /**
     * 获取Jedis
     *
     * @param pool
     * @return
     */
    public static Jedis getJedis(Pool pool) {
        return (Jedis) pool.getResource();
    }

    /**
     * 有序集合存储
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();

            return jedis.zrange(key, start, end);

        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis zadd error!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 有序集合存储
     *
     * @param key
     * @param score
     * @param member
     */
    public static void zadd(String key, long score, String member, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();

            jedis.zadd(key, score, member);

            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis zadd error!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 有序集合排名
     *
     * @param key
     * @param member
     */
    public static long zrank(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            Long rank = jedis.zrank(key, member);
            return null != rank ? rank.longValue() : -1l;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis zadd error!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 有序集合存储
     *
     * @param key
     * @param map
     * @param seconds
     */
    public static void zadd(String key, Map<String, Double> map, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();

            jedis.zadd(key, map);

            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis zadd error!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 有序集合存储
     *
     * @param key
     * @param members
     */
    public static void zrem(String key, String[] members) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();

            jedis.zrem(key, members);

        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis zrem error!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 存储单个字符串
     *
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public static void set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.set(key, value);
            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 将值存储到set中
     *
     * @param second
     * @param key
     * @param value
     */
    public static void sadd(int second, String key, String... value) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.sadd(key, value);
            if (second > 0) {
                jedis.expire(key, second);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 将值存储到set中 (不过期)
     *
     * @param key
     * @param value
     */
    public static void sadd(String key, String... value) {
        sadd(-1, key, value);
    }

    /**
     * 存储单个字符串 （不过期）
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        set(key, value, -1);
    }

    /**
     * 存储单个对象
     *
     * @param key
     * @param obj
     * @param seconds
     */
    public static void setObject(String key, Object obj, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.set(key.getBytes("utf-8"), SerializeUtil.serialize(obj));
            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 存储单个对象（不超时）
     *
     * @param key
     * @param obj
     */
    public static void setObject(String key, Object obj) {
        setObject(key, obj, -1);
    }

    /**
     * 获取对象
     *
     * @param key
     */
    public static Object getObject(String key) {
        Object obj = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            byte[] value = jedis.get(key.getBytes("utf-8"));
            if (value != null && value.length > 0) {
                obj = SerializeUtil.deserialize(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 获取数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return obj;
    }

    /**
     * 根据key取值
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        String value = "";
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            value = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 获取数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return value;
    }

    /**
     * 删除值
     *
     * @param keys
     * @return
     */
    public static long delete(String... keys) {
        long value = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            value = jedis.del(keys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 删除数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return value;
    }

    public static long deleteObject(String key) {
        long value = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            value = jedis.del(key.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 删除数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return value;
    }

    /**
     * 设置hash中的单个值
     *
     * @param hkey
     * @param field
     * @param value
     * @return
     */
    public static long hsetString(String hkey, String field, String value) {
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.hset(hkey, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    public static long hsetString(String hkey, String field, String value,
                                  int second) {
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            if (second > 0) {
                jedis.expire(hkey, second);
            }
            result = jedis.hset(hkey, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    public static long hset(String hkey, String field, Object obj) {
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            if (obj != null) {
                result = jedis.hset(hkey.getBytes("utf-8"),
                        field.getBytes("utf-8"), SerializeUtil.serialize(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    public static long hset(String hkey, String field, Object obj, int second) {
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            if (obj != null) {
                if (second > 0) {
                    jedis.expire(hkey.getBytes("utf-8"), second);
                }
                result = jedis.hset(hkey.getBytes("utf-8"),
                        field.getBytes("utf-8"), SerializeUtil.serialize(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    /**
     * 设置hash值(不过期)
     *
     * @param hkey
     * @param map
     */
    public static void hsetAll(String hkey, Map<String, String> map) {
        hsetAll(hkey, map, -1);
    }

    /**
     * 设置hash值(不过期)
     *
     * @param hkey
     * @param map
     */
    public static void hsetAll(String hkey, Map<String, String> map, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.hmset(hkey, map);
            if (seconds > 0) {
                jedis.expire(hkey, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 存储数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 从hash中获取单个数据
     *
     * @param hkey
     * @param filed
     * @return
     */
    public static String hgetString(String hkey, String filed) {
        String result = "";
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.hget(hkey, filed);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 获取数据错误!", e);
        } finally {
            closeJedis(jedis);
        }

        return result;
    }

    public static Object hget(String hkey, String field) {
        Object result = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            byte[] temp = jedis.hget(hkey.getBytes("utf-8"),
                    field.getBytes("utf-8"));
            if (temp != null && temp.length > 0) {
                result = SerializeUtil.deserialize(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 获取数据错误!", e);
        } finally {
            closeJedis(jedis);
        }

        return result;
    }

    /**
     * 从hash中获取所有值
     *
     * @param hkey
     * @return
     */
    public static Map<String, String> hgetAll(String hkey) {
        Map<String, String> result = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.hgetAll(hkey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 获取数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    /**
     * 从hash中删除某个值
     *
     * @param hkey
     * @param fields
     * @return
     */
    public static long hdel(String hkey, String... fields) {
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.hdel(hkey, fields);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 删除数据错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param seconds
     */
    public static void setExpires(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 设置过期时间错误!", e);
        } finally {
            closeJedis(jedis);
        }

    }

    /**
     * 判断是否存在
     *
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    /**
     * 模糊匹配所有符合条件的key
     *
     * @param key
     * @return
     */
    public static String[] getKeys(String key) {
        Jedis jedis = null;
        Set<String> set = null;
        String[] result = null;
        try {
            jedis = RedisUtil.getJedis();
            set = jedis.keys(key);
            if (set != null && set.size() > 0) {
                result = set.toArray(new String[]{});
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis 错误!", e);
        } finally {
            closeJedis(jedis);
        }
        return result;
    }

    public static void deleteAll(String key) {
        if (StringUtils.isNotBlank(key)) {
            key = key + RedisUtil.separator + "*";
            String[] keys = RedisUtil.getKeys(key);
            if (keys != null && keys.length > 0) {
                RedisUtil.delete(keys);
            }
        }
    }

    public static String lpop(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            String arr = jedis.lpop(key);
            return arr;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("redis lpop错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static List<String> lpop(String key, int times) {

        if (StringUtils.isBlank(key) || times <= 0) {
            return null;
        }
        String errMsg = "";
        List<String> result = null;
        Jedis jedis = null;
        Pipeline pipeline = null;

        try {
            try {
                jedis = RedisUtil.getJedis();
                pipeline = jedis.pipelined();
                // 管道开启事务
                pipeline.multi();
                for (int i = 0; i < times; i++) {
                    pipeline.lpop(key);
                }
                pipeline.exec();
            } catch (Exception e) {
                e.printStackTrace();
                pipeline.discard();
                logger.error("redis 链表数据出队错误!", e);
                throw new RuntimeException("redis 链表数据出队错误!", e);
            }

            try {
                List<Object> list = pipeline.syncAndReturnAll();
                Object o = list.get(list.size() - 1);
                if (o instanceof List) {
                    result = (List<String>) o;
                    if (result.size() == times) {
                        return result;
                    } else {
                        errMsg = "出队数与实际请求数不相符!";
                        String[] codes = result.toArray(new String[]{});
                        jedis.rpush(key, codes);
                    }
                } else {
                    errMsg = "解析redis结果集出错!";
                }

                if (StringUtils.isNotBlank(errMsg)) {
                    throw new RuntimeException(errMsg);
                }
                return null;
            } catch (Exception e) {
                if (StringUtils.isEmpty(errMsg)) {
                    errMsg = "解析redis结果集出错!";
                }
                e.printStackTrace();
                logger.error(errMsg, e);
                throw new RuntimeException(errMsg, e);
            }
        } finally {
            RedisUtil.closeJedis(jedis);
        }

    }

    public static Long rpush(String key, String[] values) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.rpush(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行rpush  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long rpushObject(String key, Object obj) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.rpush(key.getBytes("utf-8"), SerializeUtil.serialize(obj));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行rpush  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static void rpushList(String key, List<String> objects) {
        Jedis jedis = null;
        Pipeline pipeline = null;
        try {
            jedis = RedisUtil.getJedis();
            pipeline = jedis.pipelined();
            // 管道开启事务
            pipeline.multi();
            if (null != objects && objects.size() > 0) {
                for (int i = 0; i < objects.size(); i++) {
                    pipeline.rpush(key, objects.get(i));
                }
            }
            pipeline.exec();
        } catch (Exception e) {
            pipeline.discard();
            e.printStackTrace();
            throw new JedisConnectionException("执行rpushList  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Object blpopObject(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            // 无限阻塞
            List<byte[]> list = jedis.blpop(0, key.getBytes("utf-8"));
            if (null != list && list.size() > 1)
                return SerializeUtil.deserialize(list.get(1));
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行blpop  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行incr  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行decr  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 清空当前数据库缓存
     */
    public static void cleanDatebase() {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.flushDB();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行cleanDatebase  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }

    }

    /**
     * 功能： 当且仅当 key 不存在，将 key 的值设为 value ，并返回1；若给定的 key 已经存在，则 SETNX 不做任何动作，并返回0。
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.setnx(key, value).longValue() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行setnx  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 释放资源
     *
     * @param jedis
     */
    public static void closeJedis(Jedis jedis) {
        if (jedis != null) {
            pool.returnResourceObject(jedis);
        }
    }

    public static long zCount(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行zcount  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }

    public static long zCard(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JedisConnectionException("执行zcount  方法错误!", e);
        } finally {
            closeJedis(jedis);
        }
    }


    public static void main(String[] args) {
//        RedisUtil.delete("queue1");
//        RedisUtil.rpushObject("queue1", "aaaaaaaaaaaa");
//        RedisUtil.rpushObject("queue1", "bbbbbb");
//        String a = (String) RedisUtil.blpopObject("queue1");
//        System.out.println(a);

        RedisUtil.zadd("bbb",1l,"a",-1);
        RedisUtil.zadd("bbb",2l,"b",-1);
        RedisUtil.zadd("bbb",3l,"c",-1);
        RedisUtil.zadd("bbb",3l,"d",-1);
        RedisUtil.zadd("bbb",4l,"e",-1);

        long aaa = RedisUtil.zCount("bbb", "1", "3");
        System.out.println(aaa);
        long bbb = RedisUtil.zCard("bbb");
        System.out.println(bbb);
    }


}
