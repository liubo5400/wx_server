package org.jeewx.api.wxuser.user;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jeewx.api.core.common.JSONHelper;
import org.jeewx.api.core.common.WxstoreUtils;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.core.req.WeiXinReqService;
import org.jeewx.api.core.req.model.user.UserBaseInfoGet;
import org.jeewx.api.core.req.model.user.UserInfoListGet;
import org.jeewx.api.wxstore.product.model.SkuInfo;
import org.jeewx.api.wxuser.user.model.AccessToken;
import org.jeewx.api.wxuser.user.model.Wxuser;

/**
 * 微信--用户
 * 
 * @author lizr
 * 
 */
public class JwUserAPI {

	private static String OAUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=$APP_ID&secret=$SECRET&code=$CODE&grant_type=authorization_code";

	/**
	 * 根据user_openid 获取关注用户的基本信息
	 * 
	 * @param shelf_id
	 * @return
	 * @throws WexinReqException
	 */
	public static Wxuser  getWxuser(String accesstoken,String user_openid) throws WexinReqException {
		if (accesstoken != null) {
			UserBaseInfoGet userBaseInfoGet = new UserBaseInfoGet();
			userBaseInfoGet.setAccess_token(accesstoken);
			userBaseInfoGet.setOpenid(user_openid);
			JSONObject result = WeiXinReqService.getInstance().doWeinxinReqJson(userBaseInfoGet);
			// 正常返回
			Wxuser wxuser = null;
			Object error = result.get("errcode");
			wxuser = (Wxuser) JSONObject.toBean(result, Wxuser.class);
			return wxuser;
		}
		return null;
	}

	/**
	 * 获取所有关注用户信息信息
	 * 
	 * @return
	 * @throws WexinReqException 
	 */
	public static List<Wxuser> getAllWxuser(String accesstoken,String next_openid) throws WexinReqException {
		if (accesstoken != null) {
			UserInfoListGet userInfoListGet = new UserInfoListGet();
			userInfoListGet.setAccess_token(accesstoken);
			userInfoListGet.setNext_openid(next_openid);
			JSONObject result = WeiXinReqService.getInstance().doWeinxinReqJson(userInfoListGet);
			Object error = result.get("errcode");
			List<Wxuser> lstUser = null;
			Wxuser mxuser = null;
			int total = result.getInt("total");
			int count = result.getInt("count");
			String strNextOpenId = result.getString("next_openid");
			JSONObject data = result.getJSONObject("data");
			lstUser = new ArrayList<Wxuser>(total);
			if (count > 0) {
				JSONArray lstOpenid = data.getJSONArray("openid");
				int iSize = lstOpenid.size();
				for (int i = 0; i < iSize; i++) {
					String openId = lstOpenid.getString(i);
					mxuser = getWxuser(accesstoken, openId);
					lstUser.add(mxuser);
				}
				if (strNextOpenId != null) {
					lstUser.addAll(getAllWxuser(accesstoken, strNextOpenId));
				}
			}
			return lstUser;
		}
		return null;
	}

	/**
	 * 根据CODE获取 Token信息
	 * @param appId
	 * @param secret
	 * @param code
	 * @return
	 */
	public static AccessToken getAccessTokenByCode(String appId, String secret, String code) {
		if (code != null) {
			String requestUrl = OAUTH_URL.replace("$APP_ID", appId).replace("$SECRET", secret).replace("$CODE",code);
			JSONObject result = WxstoreUtils.httpRequest(requestUrl, "GET", "");
			// 正常返回
			Object error = result.get("errcode");
			System.out.println(result.toString());
			AccessToken accessToken = (AccessToken) JSONObject.toBean(result, AccessToken.class);
			return accessToken;
		}
		return null;
	}

	public static void main(String[] args) throws WexinReqException {
		AccessToken a = getAccessTokenByCode("wx9f5d262b0354a938", "5c38ed4565669935c9d599395b676724",
				"031646704accc8f275bea73813a85f5G");
		System.out.println(a.getOpenid());
		System.out.println(a.getUnionid());
		Wxuser aa = getWxuser(a.getAccess_token(), a.getOpenid());
		System.out.println(aa.getUnionid());
	}

}
