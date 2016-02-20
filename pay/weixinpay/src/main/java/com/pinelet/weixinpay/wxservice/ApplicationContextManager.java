package com.pinelet.weixinpay.wxservice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.pinelet.common.httpasync.HttpAsyncClient;
import com.pinelet.common.httpasync.HttpAsyncClientFactory;

public class ApplicationContextManager {
	//公众号ID
	public static final String APPID = "pay.wx.appid";
	//目前用于报文验证接口时的token
	public static final String TOKEN = "pay.wx.token";
	//公众号密码
	public static final String APPSECRET = "pay.wx.appsecret";
	
	public static final String HTTPCLIENT = "pay.wx.httpclient";
	//在发送支付请求报文中的API密钥
	public static final String SPIKEY = "pay.wx.spikey";
	
	Map<String, String> property = Maps.newConcurrentMap();
	
	private HttpAsyncClient client = null;
	
	private static ApplicationContextManager context = new ApplicationContextManager(); 
	
	private ApplicationContextManager() {
		client = HttpAsyncClientFactory.build(5, 20);
	}
	public static ApplicationContextManager getInstance() {
		return context;
	}
	
	public ApplicationContextManager set(String key, String value) {
		property.put(Optional.of(key).get(), Optional.of(value).get());
		return this;
	}
	
	public static String map2String(Map<String, String> pairs, boolean isSort) {
		List<String> plist = Lists.newArrayList();
		for (Entry<String, String> param : pairs.entrySet()) {
			plist.add(param.getKey() + "=" + param.getValue());
		}
		if (isSort) {
			Collections.sort(plist);
		}
		StringBuffer ruleStr = new StringBuffer();
		for (String param : plist) {
			ruleStr.append(param).append("&");
		}
		return ruleStr.deleteCharAt(ruleStr.length()).toString();
	}
	
	public static String map2String (Map<String, String> pairs) {
		return map2String(pairs, false);
	}
	
	public HttpAsyncClient getClient() {
		return client;
	}
	public String get(String key) {
		return property.get(key);
	}

}
