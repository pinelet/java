package com.pinelet.weixinpay.msg;

import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

public class WXPayService extends AbsProcessMessage implements HttpClientCallback {
	
	
	public WXPayService(AsyncContext context, Map<String, String[]> info) {
		super(context, info);
	}

	private static Logger loger = LoggerFactory.getLogger("WXPayService");
	
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
	
	private String createSign() {
		return null;
	}

	@Override
	public void completed(HttpClientCallbackResult result) {
		String reData = result.getReplyDataAsString();
		ctx.dispatch("/index.jsp");
		
	}

	@Override
	public void failed(HttpClientCallbackResult result) {
		loger.error("prepay(from code to openid) failed and returncode is {}, replay message [{}]", result.getRetCode(), result.getReplyDataAsString());
		
	}

}
