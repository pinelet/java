package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.io.CharStreams;
import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;
import com.pinelet.weixinpay.util.XMLDocumentService;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

public class PublicSPIService extends AbsProcessMessage implements HttpClientCallback{

	Logger loger = LoggerFactory.getLogger(this.getClass());
	
	public PublicSPIService(AsyncContext ctx) {
		super(ctx);
	}
	
	
	@Override
	public void completed(HttpClientCallbackResult result) {
		XMLDocumentService xml = XMLDocumentService.getInstance();
		Document resdoc = xml.parse(result.getReplyData());
		if (loger.isDebugEnabled())
			loger.debug("receive union order: {}", resdoc.getTextContent());
		//解析回应结果
		String code = xml.getNodeContent("/xml/return_code", resdoc);
		if ("SUCCESS".equals(code)) {
			//向腾讯URL支付请求，生成prepay_id
			String prepay_id = null;
			ctx.getRequest().setAttribute("prepay_id", prepay_id);
			//组织返回客户支付页面的报文JSON appid/timeStamp/nonceStr/package(prepay_id)/signType/paySign
			String iSpikey = ApplicationContextManager.getInstance().get(ApplicationContextManager.SPIKEY);
			long iTimeStamp = System.currentTimeMillis()/1000;	//10
			String iNonceStr = getRandomString(32);	//32
			String iPackage = null;//统一下单后生成的
		}
		else {
			//支付页面显示错误信息
		}
	}

	@Override
	public void failed(HttpClientCallbackResult result) {
		try {
			loger.error("receive union order failed :{}", CharStreams.toString(new InputStreamReader(result.getReplyData())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}  
}
