package com.pinelet.weixinpay.msg;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class PushSubscribeMsgImpl extends AbsProcessMessage {
	
	Logger loger = LoggerFactory.getLogger(getClass());

	public PushSubscribeMsgImpl(AsyncContext ctx, Document doc) {
		super(ctx, doc);
	}

	@Override
	public void run() {
		String EventKey = getValue("EventKey");
		//如果eventkey为空，说明只是关注
		if (EventKey == null) {
			ctx.complete();
			return;
		}
		//get 二维码参数
		String machineID = null;
		String ticket = null;
		if (EventKey.startsWith("qrscene_")) {
			//此参数可能为设备关联号
			machineID = EventKey.substring(8);
			ticket = getValue("Ticket");
		}
		ctx.dispatch("/order.html?" + machineID);
	}

}
