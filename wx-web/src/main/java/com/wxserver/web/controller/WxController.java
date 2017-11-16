package com.wxserver.web.controller;

import com.wxserver.common.Constants;
import com.wxserver.common.utils.WeixinManager;
import com.wxserver.web.cache.RedisCache;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jeewx.api.wxbase.wxtoken.JwTokenAPI;
import org.jeewx.api.wxuser.user.JwUserAPI;
import org.jeewx.api.wxuser.user.model.AccessToken;
import org.jeewx.api.wxuser.user.model.Wxuser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bjliubo on 2017/9/27.
 */
@RestController
@RequestMapping(value = "/web")
public class WxController {

    private static Logger logger = LoggerFactory.getLogger(WxController.class);

    @RequestMapping(value = "/wx/init", method = RequestMethod.GET)
    @ResponseBody
    public Object initConfig(@RequestParam(required = true) String redirect_uri) {

        Map<String, String> ret = new HashMap();
        try {
            logger.info("======>appId:" + Constants.WEIXIN_APPID);
            logger.info("======>secret:" + Constants.WEIXIN_SECRET);
            String accessToken = RedisCache.getWxAccessToken(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET);
            logger.info("======>accessToken:" + accessToken);
            String ticket = "";
            if (StringUtils.isNotEmpty(accessToken)) {
                ticket = RedisCache.getWxTicket(Constants.WEIXIN_APPID, accessToken);
            }
            if (StringUtils.isNoneBlank(ticket)) {
                String decodeUrl = URLDecoder.decode(redirect_uri, "UTF-8");
                ret = JwTokenAPI.sign(ticket, decodeUrl);
            }

            logger.info("======>返回信息:nonceStr->  " + ret.get("nonceStr"));
            logger.info("======>返回信息:signature->  " + ret.get("signature"));
            logger.info("======>返回信息:timestamp->  " + ret.get("timestamp"));
            logger.info("======>返回信息:jsapi_ticket->  " + ret.get("jsapi_ticket"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONObject.fromObject(ret);
    }


    @RequestMapping(value = "/wx/userInfo", method = RequestMethod.GET)
    public Object wxUserInfo(@RequestParam(required = false, defaultValue = "") String code,
                             @RequestParam(required = false, defaultValue = "") String openId) {
        Map<String, Object> result = new HashMap<String, Object>();
        String uinfo = null;
        if (StringUtils.isNotBlank(openId)) {
            uinfo = RedisCache.getWxUserInfo(Constants.WEIXIN_APPID, openId);
            if (StringUtils.isNotBlank(uinfo)) {
                result.put("result", "success");
                result.put("uinfo", uinfo);
                return JSONObject.fromObject(result);
            }
        }
        if (StringUtils.isBlank(uinfo)) {
            if (StringUtils.isBlank(code)) {
                result.put("result", "fail");
                result.put("message", "code为空");
                return JSONObject.fromObject(result);
            }
            AccessToken accessToken = JwUserAPI.getAccessTokenByCode(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET, code);
            if (null == accessToken) {
                result.put("result", "fail");
                result.put("message", "accessToken异常");
                return JSONObject.fromObject(result);
            }
            uinfo = WeixinManager.newInstance(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET).getUserInfo(accessToken.getAccess_token(), accessToken.getOpenid());
            logger.info("~~~~~~~~~~uinfo:" + uinfo);
            JSONObject jsonObject = JSONObject.fromObject(uinfo);
            Wxuser wxuser = (Wxuser) JSONObject.toBean(jsonObject, Wxuser.class);
            RedisCache.setWxUserInfo(Constants.WEIXIN_APPID, wxuser.getOpenid(), uinfo);
        }
        result.put("result", "success");
        result.put("uinfo", uinfo);
        return JSONObject.fromObject(result);
    }
}
