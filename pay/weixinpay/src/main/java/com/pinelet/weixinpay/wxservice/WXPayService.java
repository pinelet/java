package com.pinelet.weixinpay.wxservice;

import java.util.Map;

import javax.servlet.AsyncContext;
import com.pinelet.weixinpay.msg.AbsProcessMessage;

public class WXPayService extends AbsProcessMessage{

	public WXPayService(AsyncContext ctx, Map<String, String[]> doc) {
		super(ctx, doc);
	}

	@Override
	public void run() {
		//取得machineID
		//判断是否存在此设备且设备状态为正常

		ctx.dispatch("/index.jsp");
	}

}
