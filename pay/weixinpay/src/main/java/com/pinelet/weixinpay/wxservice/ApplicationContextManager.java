package com.pinelet.weixinpay.wxservice;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class ApplicationContextManager {
	
	private static final String APPID = "pay.wx.appid";
	//private final String DEFAULT_APPID = "wxaea199a4b0a6faf8";//FIXME config in web.xml
	
	private static final String TOKEN = "pay.wx.token";
	
	private static final String APPSECRET = "pay.wx.appsecret";
	//private final String DEFAULT_APPSECRET = "";//FIXME config in web.xml
	
	private static final String ACCESS_TOKEN = "pay.wx.access_token";
	
	Map<String, String> property = Maps.newConcurrentMap();
	
	private static ApplicationContextManager context = new ApplicationContextManager(); 
	
	private ApplicationContextManager() {}
	public static ApplicationContextManager getInstance() {
		return context;
	}
	
	public ApplicationContextManager set(String key, String value) {
		property.put(Optional.of(key).get(), Optional.of(value).get());
		return this;
	}
	
	public String getAccessToken() {
		return get(ACCESS_TOKEN);
	}
	
	public String get(String key) {
		return property.get(key);
	}

}
