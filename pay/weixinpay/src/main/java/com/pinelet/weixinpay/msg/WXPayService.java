package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;

public class WXPayService extends AbsProcessMessage implements HttpClientCallback {
	
	
	public WXPayService(AsyncContext context) {
		super(context);
	}

	private static Logger loger = LoggerFactory.getLogger("WXPayService");
	
	//private ApplicationContextManager app = ApplicationContextManager.getInstance();

	@Override
	public void completed(HttpClientCallbackResult result) {
		String reData = null;
		try {
			reData = CharStreams.toString(new InputStreamReader(result.getReplyData()));
		} catch (IOException e) {
			loger.error("", e);
			ctx.complete();
		}
		JSONObject rejson = JSONObject.parseObject(reData);
		String openid = null;
		String errcode = null;
		if ((errcode = rejson.getString("errcode")) == null && (openid = rejson.getString("openid")) != null) {
			HttpServletRequest httpRequest = (HttpServletRequest)ctx.getRequest();
			httpRequest.getSession().setAttribute("openid", openid);
			ctx.dispatch("/index.jsp");
		}
		else {
			loger.error("get openid from code failed, errcode [{}] and errmsg [{}]", errcode, rejson.getString("errmsg"));
			ctx.complete();
		}
		
	}

	@Override
	public void failed(HttpClientCallbackResult result) {
		String restr = null;
		try {
			restr = CharStreams.toString(new InputStreamReader(result.getReplyData()));
		} catch (IOException e) {
			loger.error("httpsync is error", e);
		}
		loger.error("prepay(from code to openid) failed and returncode is {}, replay message [{}] or [{}]", result.getRetCode(), result.getReplyDataAsString(), restr);
		ctx.complete();
	}

}
