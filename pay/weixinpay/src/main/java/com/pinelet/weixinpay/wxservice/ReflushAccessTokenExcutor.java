package com.pinelet.weixinpay.wxservice;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pinelet.common.httpasync.HttpAsyncClientFactory;


public class ReflushAccessTokenExcutor {
	
	private String accessToken = null;
	private String atokenURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	
	public void buildAccessToken(final String appid, final String secret, int minute) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Callable<String>() {
			
			/**
			 * 调用获取access_token接口
			 */
			@Override
			public String call() throws Exception {
				HttpAsyncClientFactory.build(5, 100);
				return null;
			}
			
		}, minute, TimeUnit.MINUTES);
	}
	public String getAccessToken() {
		return accessToken;
	}

}
