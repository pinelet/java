package com.pinelet.weixinpay.wxservice;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;


public class ReflushAccessTokenExcutor {
	
	private static String accessToken = null;
	private static long expires = 0L; 
	ScheduledExecutorService executor = null;
	private Logger loger = LoggerFactory.getLogger(this.getClass());
	
	public void buildAccessToken(final String appid, final String secret, int minute) {
		
		final String atokenURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new TokenRunnable(atokenURL), 0, minute, TimeUnit.MINUTES);
	}
	
	public void shutdown() {
		executor.shutdownNow();
	}
	private class TokenRunnable implements Runnable {
		private String tokenURL = null;
		
		public TokenRunnable(String url) {
			this.tokenURL = url;
		}
		/**
		 * 调用获取access_token接口
		 */
		@Override
		public void run() {
			if (loger.isDebugEnabled())
				loger.debug("get access token from {}", tokenURL);
			ApplicationContextManager.getInstance().getClient().doAsyncHttpGet(tokenURL, new HttpClientCallback() {

				@Override
				public void completed(HttpClientCallbackResult result) {
					String tokenjson = result.getReplyDataAsString();
					//{"access_token":"ACCESS_TOKEN","expires_in":7200}
					 JSONObject tokenjo = JSON.parseObject(tokenjson);
					 int error = tokenjo.getIntValue("errcode");
					 if ( error != 0) {
						 loger.error("get access token failed errorcode is {} and errmsg is {}", error, tokenjson);
						 return;
					 }
					 long exminute = tokenjo.getLongValue("expires_in") ;
					 expires = System.currentTimeMillis()/1000 + exminute - 300;
					 accessToken = tokenjo.getString("access_token");
					 loger.info("success get access token is [{}], expires {}", accessToken, exminute);
				}

				@Override
				public void failed(HttpClientCallbackResult result) {
					loger.error("get access token failed and returncode is {}, replay message [{}]", result.getRetCode(), result.getReplyDataAsString());
					//try again by times
					
				}
				
			});
			
		}
	}
	public static String getAccessToken() {
		if (System.currentTimeMillis()/1000 - expires > 0) {
			//TODO try again
		}
		return accessToken;
	}

}
