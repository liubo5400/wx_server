package com.wxserver.web.cache;

import com.wxserver.common.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.wxbase.wxtoken.JwTokenAPI;
import org.jeewx.api.wxuser.user.model.Wxuser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static String getWxUserInfo(String appId, String openId) {
        return RedisUtil.get(getWxUserInfoKey(appId, openId));
    }

    public static void setWxUserInfo(String appId, String openId, String uinfo) {
        RedisUtil.setObject(getWxUserInfoKey(appId, openId), uinfo);
    }

    private static String getWxTicketKey(String appId) {
        return "wx:appId:" + appId + ":ticket";
    }

    private static String getWxAccessTokenKey(String appId) {
        return "wx:appId:" + appId + ":accessToken";
    }

    private static String getWxUserInfoKey(String appId, String openId) {
        return "wx:appId:" + appId + ":openId:" + openId;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        RedisUtil.set("aaa", "1111");
        String aaa = RedisUtil.get("aaa");
        System.out.println(aaa);
    }


}
