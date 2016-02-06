package com.pinelet.weixinpay.msg;

import java.util.Map;

import javax.servlet.AsyncContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;

public abstract class AbsProcessMessage implements Runnable {

	protected AsyncContext ctx = null;
	private Map<String, String> request = Maps.newHashMap();
	
	public AbsProcessMessage(AsyncContext ctx, Document doc) {
		this.ctx = ctx;
		NodeList nodes = doc.getChildNodes();
		Node node = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			request.put(node.getNodeName(), node.getNodeValue());
		}
	}
	
	public String getValue(String key) {
		return request.get(key);
	}

}
