package org.jeewx.api.wxuser;

import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.wxbase.wxtoken.JwTokenAPI;
import org.jeewx.api.wxuser.user.JwUserAPI;
import org.jeewx.api.wxuser.user.model.Wxuser;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		try {
			String s = JwTokenAPI.getAccessToken("wx5c5ffbd532e7d8e7","12dddf32bef1e63a54386be7f37ddce2");
			System.out.println(s);
			//System.out.println(JwUserAPI.getWxuser(s, "????").getNickname());


			List<Wxuser> users = JwUserAPI.getAllWxuser(s,"");
			for (int i = 0; i < users.size(); i++) {

				System.out.println(users.get(i).getNickname());
			}
		} catch (WexinReqException e) {
			e.printStackTrace();
		}
	}
}
