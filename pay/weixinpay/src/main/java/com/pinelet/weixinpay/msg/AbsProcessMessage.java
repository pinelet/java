package com.pinelet.weixinpay.msg;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import javax.servlet.AsyncContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

public abstract class AbsProcessMessage {

	private static String randomstr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
    private static Random random = new Random();  
    
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
	public AbsProcessMessage(AsyncContext ctx) {
		this(ctx, Maps.<String, String>newHashMap());
	}
	
	public AbsProcessMessage(AsyncContext ctx, Map<String, String> infos) {
		this.ctx = ctx;
		request = infos;
	}
	
	/**
	 * 在正常处理通知信息后，默认回应成功结果并完成异步调用
	 */
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
	
	/**
	 * 生成指定长度的随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		
	    StringBuffer buf = new StringBuffer();  
	    for (int i = 0; i < length; i++) {  
	        int num = random.nextInt(62);  
	        buf.append(randomstr.charAt(num));  
	    }  
	    return buf.toString(); 
	}
	
	/**
	 * 根据微信签名算法要求生成签名值
	 * @param params
	 * @return
	 */
	public static String createSignature(Map<String, String> pairs) {
		//对参数按照key=value的格式，并按照参数名ASCII字典序排序
		String ruleNkey = ApplicationContextManager.map2String(pairs, true);
		//拼接API密钥
		String signature = DigestUtils.md5Hex(ruleNkey + "&key=" + ApplicationContextManager.getInstance().get(ApplicationContextManager.SPIKEY));
		//MD5(stringSignTemp).toUpperCase()
		return signature.toUpperCase();
	}
	
	/**
	 * 根据微信签名算法验证签名值
	 * @param pairs
	 * @param oSign
	 * @return
	 */
	public static boolean verifySignature(Map<String, String> pairs, String oSign) {
		String ruleNkey = ApplicationContextManager.map2String(pairs, true);
		String nSign = DigestUtils.md5Hex(ruleNkey + "&key=" + ApplicationContextManager.getInstance().get(ApplicationContextManager.SPIKEY));
		if (nSign.equalsIgnoreCase(oSign))
			return true;
		return false;
	}

}
