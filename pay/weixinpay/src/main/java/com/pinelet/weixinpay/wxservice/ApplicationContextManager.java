package com.pinelet.weixinpay.wxservice;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.pinelet.common.httpasync.HttpAsyncClient;
import com.pinelet.common.httpasync.HttpAsyncClientFactory;

public class ApplicationContextManager {
	
	public static final String APPID = "pay.wx.appid";
	
	public static final String TOKEN = "pay.wx.token";
	
	public static final String APPSECRET = "pay.wx.appsecret";
	
	public static final String HTTPCLIENT = "pay.wx.httpclient";
	
	Map<String, String> property = Maps.newConcurrentMap();
	
	private HttpAsyncClient client = null;
	
	private static ApplicationContextManager context = new ApplicationContextManager(); 
	
	private ApplicationContextManager() {
		client = HttpAsyncClientFactory.build(5, 100);
	}
	public static ApplicationContextManager getInstance() {
		return context;
	}
	
	public ApplicationContextManager set(String key, String value) {
		property.put(Optional.of(key).get(), Optional.of(value).get());
		return this;
	}
	
	public static String map2String (Map<String, String> pairs) {
		StringBuffer urlsuffix = new StringBuffer();
		for (Entry<String, String> entry : pairs.entrySet()) {
			urlsuffix.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return urlsuffix.toString();
	}
	
	public HttpAsyncClient getClient() {
		return client;
	}
	public String get(String key) {
		return property.get(key);
	}

}
