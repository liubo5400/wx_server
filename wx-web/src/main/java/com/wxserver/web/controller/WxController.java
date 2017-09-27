package com.wxserver.web.controller;

import com.wxserver.common.Constants;
import com.wxserver.common.utils.WeixinManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jeewx.api.wxbase.wxtoken.JwTokenAPI;
import org.jeewx.api.wxuser.user.JwUserAPI;
import org.jeewx.api.wxuser.user.model.AccessToken;
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
            String accessToken = "";//RedisCache.getWxAccessToken(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET);
            logger.info("======>accessToken:" + accessToken);
            String ticket = "";
            if (StringUtils.isNotEmpty(accessToken)) {
                ticket = "";//RedisCache.getWxTicket(Constants.WEIXIN_APPID, accessToken);
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
    public Object wxUserInfo(@RequestParam(required = true) String code,
                             @RequestParam(required = true) boolean isAuth) {
        Map<String,String> result = new HashMap<String, String>();

        AccessToken accessToken = JwUserAPI.getAccessTokenByCode(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET, code);
        if (null == accessToken) {
            result.put("result","fail");
            result.put("message","accessToken异常");
            return JSONObject.fromObject(result);
        }
        if (isAuth) {
            String uinfo = WeixinManager.newInstance(Constants.WEIXIN_APPID, Constants.WEIXIN_SECRET).getUserInfo(accessToken.getAccess_token(), accessToken.getOpenid());
            JSONObject jsonObject = JSONObject.fromObject(uinfo);
            result.put("result","success");
            result.put("headUrl",null == jsonObject.getString("headUrl")?"":jsonObject.getString("headUrl"));
            result.put("nickname",null == jsonObject.getString("nickname")?"":jsonObject.getString("nickname"));
            result.put("subscribe",null == jsonObject.getString("subscribe")?"":jsonObject.getString("subscribe"));
            return JSONObject.fromObject(result);
        } else {
            result.put("result","fail");
            result.put("message","非认证");
            return JSONObject.fromObject(result);
        }

    }
}
