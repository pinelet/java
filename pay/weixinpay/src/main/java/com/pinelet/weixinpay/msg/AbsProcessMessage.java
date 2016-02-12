package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;

public abstract class AbsProcessMessage implements Runnable {

	protected AsyncContext ctx = null;
	private Map<String, String> request = Maps.newHashMap();
	private Logger loger = LoggerFactory.getLogger(AbsProcessMessage.class);
	
	public AbsProcessMessage(AsyncContext ctx, Document doc) {
		this.ctx = ctx;
		NodeList nodes = doc.getChildNodes();
		Node node = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			request.put(node.getNodeName(), node.getNodeValue());
		}
	}
	
	public AbsProcessMessage(AsyncContext ctx, Map<String, String[]> infos) {
		this.ctx = ctx;
		for (Entry<String, String[]> info : infos.entrySet())
		request.put(info.getKey(), info.getValue()[0]);
	}
	
	public void returnEventSuccess() {
		try {
			ctx.getResponse().getWriter().append("success").flush();
			ctx.complete();
		} catch (IOException e) {
			loger.warn("not get response IO", e);
		}
	}
	public String getValue(String key) {
		return request.get(key);
	}

}
