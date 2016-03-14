package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Maps;
import com.google.common.io.CharStreams;
import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;
import com.pinelet.weixinpay.util.XMLDocumentService;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

/**
 * 统一下单后的返回调用
 * 成功时返回页面json例子：
 * {}
 * 失败返回页面json例子：
 * {}
 * @author fathead
 *
 */
public class PublicSPIService extends AbsProcessMessage implements HttpClientCallback{

	Logger loger = LoggerFactory.getLogger(this.getClass());
	
	public PublicSPIService(AsyncContext ctx) {
		super(ctx);
	}
	
	
	@Override
	public void completed(HttpClientCallbackResult result) {
		XMLDocumentService xml = XMLDocumentService.getInstance();
		ApplicationContextManager app = ApplicationContextManager.getInstance();
		Document resdoc = xml.parse(result.getReplyData());
		if (loger.isDebugEnabled())
			loger.debug("receive union order: {}", resdoc.getTextContent());
		//解析回应结果
		String code = xml.getNodeContent("/xml/return_code", resdoc);
		String prepay_id = null;
		if ("SUCCESS".equals(code)) {
			//result_code字段需要为成功
			String resultcode = xml.getNodeContent("/xml/result_code", resdoc);
			if ("SUCCESS".equals(resultcode)) {
				//TODO 结果与交易均成功,是否需要验签
				prepay_id = xml.getNodeContent("/xml/prepay_id", resdoc);
				//向腾讯URL支付请求，生成prepay_id				
				ctx.getRequest().setAttribute("prepay_id", prepay_id);
				//组织返回客户支付页面的报文JSON appid/timeStamp/nonceStr/package(prepay_id)/signType/paySign
				Map<String, String> payresult = Maps.newHashMap();
				payresult.put("appid", app.get(ApplicationContextManager.APPID));
				payresult.put("timeStamp", String.valueOf(System.currentTimeMillis()/1000));
				payresult.put("nonceStr", getRandomString(32));
				payresult.put("package", "prepay_id="+ prepay_id);//该值有效期为2小时
				payresult.put("signType", "MD5");
				payresult.put("paySign", AbsProcessMessage.createSignature(payresult));
//				String iSpikey = app.get(ApplicationContextManager.SPIKEY);
				String jsonr = JSON.toJSONString(payresult);
				try {
					ctx.getResponse().getOutputStream().println(jsonr);
				} catch (IOException e) {
					loger.error("reutrn to page the trx info failed.", e);
				}
			}
			else {
				//TODO 交易失败，页面显示失败原因
				loger.error("union trx failed.. err_code[{}], err_code_des[{}]", xml.getNodeContent("/xml/err_code", resdoc), xml.getNodeContent("/xml/err_code_des", resdoc));
				
			}
		}
		else {
			//TODO 支付页面显示错误信息
			loger.error("request union trx failed.. return_code[{}], return_msg[{}]", xml.getNodeContent("/xml/return_code", resdoc), xml.getNodeContent("/xml/return_msg", resdoc));
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
