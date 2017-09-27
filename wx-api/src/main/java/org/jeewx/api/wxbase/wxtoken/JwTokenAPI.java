package org.jeewx.api.wxbase.wxtoken;

import net.sf.json.JSONObject;

import org.jeewx.api.core.common.WxstoreUtils;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.core.req.WeiXinReqService;
import org.jeewx.api.core.req.model.AccessToken;
import org.jeewx.api.core.util.WeiXinReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信--token信息
 * 
 * @author lizr
 * 
 */
public class JwTokenAPI {

	private static Logger logger = LoggerFactory.getLogger(JwTokenAPI.class);

	private static AccessToken atoken = null;

	private static String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=$TOKEN&type=jsapi";

	/**
	 * 获取权限令牌信息
	 * @param appid
	 * @param appscret
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public static String getAccessToken(String appid, String appscret) throws WexinReqException{
		String newAccessToken = "";
		atoken = new AccessToken();
		atoken.setAppid(appid);
		atoken.setSecret(appscret);
		JSONObject result = WeiXinReqService.getInstance().doWeinxinReqJson(atoken);
		// 正常返回
		newAccessToken = result.getString("access_token");
		return newAccessToken;
	}

	/**
	 * 获取ticket
	 * @return
	 */
	public static String getTicket(String token) {

		String requestUrl = TICKET_URL.replace("$TOKEN", token);
		JSONObject result = WxstoreUtils.httpRequest(requestUrl, "GET", "");
		logger.info("获取ticket信息：" + result);

		return result.get("ticket").toString();

	}

	/**
	 * 获取签名信息
	 * @param jsapiTicket
	 * @param url
	 * @return
	 */
	public static Map<String, String> sign(String jsapiTicket, String url){
		Map<String, String> ret = new HashMap<String, String>();
		String nonceStr = WeiXinReqUtil.createNonceStr();
		String timestamp = WeiXinReqUtil.createTimestamp();
		String string1;
		String signature = "";
		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr
				+ "&timestamp=" + timestamp + "&url=" + url;
		logger.info(string1);
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = WeiXinReqUtil.byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapiTicket);
		ret.put("nonceStr", nonceStr);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;

	}


	public static Map<String,String> sign(String appId, String accessToken, String url){
		Map<String, String> ret = new HashMap<String, String>();
		String nonceStr = WeiXinReqUtil.createNonceStr();
		String timestamp = WeiXinReqUtil.createTimestamp();

		String string1;
		String signature = "";
		// 注意这里参数名必须全部小写，且必须有序
		string1 = "accesstoken=" + accessToken + "&appid=" + appId
				+ "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
		logger.info(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = WeiXinReqUtil.byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("appId", appId);
		ret.put("nonceStr", nonceStr);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}


	 
	
	public static void main(String[] args){
		String ticket = JwTokenAPI.getTicket("kgt8ON7yVITDhtdwci0qefam7xP7NlJ-qO-rCUoAiojIhdryHmFQ2GHmYsP7NdvZpLpoYWuIo5ybWC_FZ5euew");
		System.out.println(ticket);
	}
}
