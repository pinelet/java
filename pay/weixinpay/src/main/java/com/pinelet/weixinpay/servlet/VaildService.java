package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;

/**
 * Servlet implementation class TestService
 */
public class VaildService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String token = "7OEsWTwEY6aIJS3A7gcsqMyuY2myL1Mk";
	
	private static final String paramname_timestamp = "timestamp";
	private static final String paramname_nonce = "nonce";
	private static final String paramname_echostr = "echostr";
	private static final String paramname_signature = "signature";
	private MessageDigest digest = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VaildService() {
        super();
        try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			//log
		}
    }

	/**
	 * signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	*timestamp	时间戳
	*nonce	随机数
	*echostr	随机字符串
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String echostr = request.getParameter(paramname_echostr);
		String signature = request.getParameter(paramname_signature);
		String nonce = request.getParameter(paramname_nonce);
		String timestamp = request.getParameter(paramname_timestamp);
		if (signatureVerify(signature, timestamp, nonce)) {
			response.getWriter().append(echostr);
		}
		else {
			response.getWriter().append("verify failed at :").append(request.getContextPath());
		}
		
	}
	
	/**
 	加密/校验流程如下：
	* 1. 将token、timestamp、nonce三个参数进行字典序排序
	* 2. 将三个参数字符串拼接成一个字符串进行sha1加密
	* 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 * @param signature
	 * @param timestamp
	 * @param noce
	 * @return
	 */
	public boolean signatureVerify(String signature, String timestamp, String nonce) {
		if (signature == null || timestamp == null || nonce == null) 
			return false;
		List<String> signlist = Lists.newArrayList(token, timestamp, nonce);
		Collections.sort(signlist);
		String sign = "";
		for (String value : signlist) {
			sign += value;
		}
		digest.update(sign.getBytes());
		if (signature != null) {
			boolean vaild = signature.equals(new String(digest.digest()));
			digest.reset();
			return vaild;
		}
		return false;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
