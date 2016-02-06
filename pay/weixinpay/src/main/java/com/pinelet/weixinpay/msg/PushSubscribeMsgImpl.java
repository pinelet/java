package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

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
		try {
			PrintWriter print = ctx.getResponse().getWriter();
			print.println("pushSubMsg said : " + new Date());
			print.flush();
		} catch (IOException e) {
			loger.error("", e);
		}
	}

}
