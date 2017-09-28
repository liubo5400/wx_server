package com.wxserver.web.cache;

import com.wxserver.common.utils.RedisUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.wxbase.wxtoken.JwTokenAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;

/**
 * Created by bjliubo on 2015/10/14.
 */
public class RedisCache {

    private static Logger logger = LoggerFactory.getLogger(RedisCache.class);


    public static String getWxAccessToken(String appId, String secret) {
        String accessToken = RedisUtil.get(getWxAccessTokenKey(appId));
        if (StringUtils.isBlank(accessToken)) {
            try {
                accessToken = JwTokenAPI.getAccessToken(appId, secret);
                RedisUtil.set(getWxAccessTokenKey(appId), accessToken, 5000);
            } catch (WexinReqException e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    public static String getWxTicket(String appId, String accessToken) {
        String ticket = RedisUtil.get(getWxTicketKey(accessToken));
        if (StringUtils.isBlank(ticket)) {
            ticket = JwTokenAPI.getTicket(accessToken);
            RedisUtil.set(getWxTicketKey(appId), ticket, 5000);
        }
        return ticket;
    }

    private static String getWxTicketKey(String appId) {
        return "wx:appId:" + appId + ":ticket";
    }

    private static String getWxAccessTokenKey(String appId) {
        return "wx:appId:" + appId + ":accessToken";
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        RedisUtil.set("aaa","1111");
        String aaa = RedisUtil.get("aaa");
        System.out.println(aaa);
    }


}
