package com.wxserver.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeixinManager {

	private static Logger logger = LoggerFactory.getLogger(WeixinManager.class);

	private String appId;

	private String secret;

	public static WeixinManager newInstance(String appId, String secret) {
		return new WeixinManager(appId, secret);
	}

	private WeixinManager(String appId, String secret) {
		this.appId = appId;
		this.secret = secret;
	}

	public String getWeixinOpenId(String code){

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
		String result = HttpClient4Util.GetContent(url, "");
		logger.info("获取OpenId信息：" + result);
		return result;
	}

	public String getUserInfo(String accessToken, String openid){
		String userInfo;
		String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid;
		userInfo = HttpClient4Util.GetContentForNew(userUrl,"UTF-8");
		logger.info("获取OpenId信息：" + userInfo);
		return userInfo;

	}


//	public String getTicket() {
//		String ticketKey = "wxapp:" + appId + ":ticket";
//		String ticket = RedisUtil.get(ticketKey);
//		if (StringUtils.isNotBlank(ticket)) {
//			return ticket;
//		}
//		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getToken() + "&type=jsapi";
//		String result = HttpClient4Util.GetContent(url, "");
//		logger.info("获取ticket信息：" + result);
//		Row row = new JsonMapper().fromJson(result, Row.class);
//
//		RedisUtil.set(ticketKey, row.gets("ticket"), row.getInt("expires_in") - 500);
//		return row.gets("ticket");
//	}

//	public Map<String, String> sign(String url) {
//		String jsapiTicket = getTicket();
//		Map<String, String> ret = new HashMap<String, String>();
//		String nonceStr = createNonceStr();
//		String timestamp = createTimestamp();
//		String string1;
//		String signature = "";
//
//		// 注意这里参数名必须全部小写，且必须有序
//		string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr
//				+ "&timestamp=" + timestamp + "&url=" + url;
//		logger.info(string1);
//
//		try {
//			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
//			crypt.reset();
//			crypt.update(string1.getBytes("UTF-8"));
//			signature = byteToHex(crypt.digest());
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		ret.put("url", url);
//		ret.put("jsapi_ticket", jsapiTicket);
//		ret.put("nonceStr", nonceStr);
//		ret.put("timestamp", timestamp);
//		ret.put("signature", signature);
//
//		return ret;
//	}

	public static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String createNonceStr() {
		return UUID.randomUUID().toString();
	}

	public static String createTimestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

}
