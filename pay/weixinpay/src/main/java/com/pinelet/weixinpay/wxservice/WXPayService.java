package com.pinelet.weixinpay.wxservice;

import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinelet.weixinpay.msg.AbsProcessMessage;

public class WXPayService extends AbsProcessMessage{
	
	private static Logger loger = LoggerFactory.getLogger("WXPayService");
	
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
	
	public WXPayService(AsyncContext ctx, Map<String, String[]> doc) {
		super(ctx, doc);
	}

	@Override
	public void run() {
		//取得machineID
		String mid = ctx.getRequest().getParameter("m");
		if (loger.isDebugEnabled())
			loger.debug("get machine ID - [{}]", mid);
		//TODO 判断是否存在此设备且设备状态为正常
		//组织报文 appid/timeStamp/nonceStr/package/signType/paySign
		
		String iSpikey = app.get(ApplicationContextManager.SPIKEY);
		long iTimeStamp = System.currentTimeMillis()/1000;	//10
		String iNonceStr = getRandomString(32);	//32
		String iPackage = //统一下单后生成的
		ctx.getRequest().setAttribute("mid", mid);
		ctx.dispatch("/index.jsp");
	}
	
	private String createSign() {
		return null;
	}

}
