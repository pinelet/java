package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Servlet implementation class TestService
 */
public class VaildService extends HttpServlet {
	
	private static final long serialVersionUID = -111847272269443629L;

	private String token = null;
	
	private static final String initparam_token = "token";
	private static final String paramname_timestamp = "timestamp";
	private static final String paramname_nonce = "nonce";
	private static final String paramname_echostr = "echostr";
	private static final String paramname_signature = "signature";
	private Logger loger = LoggerFactory.getLogger(this.getClass());
       
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		token = Optional.fromNullable(config.getInitParameter(initparam_token)).or(initparam_token);
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
		if (signatureVerify(signature, timestamp, nonce, token)) {
			response.getWriter().append(echostr).flush();
			loger.info("vaild pass...");
		}
		else {
			response.getWriter().append("verify failed at :").append(request.getPathInfo()).flush();
			loger.error("vaild failed at : {}", request.getPathInfo());
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
	public boolean signatureVerify(String signature, String timestamp, String nonce, String token) {
		boolean vaild = false;
		if (signature == null || timestamp == null || nonce == null) {
			return vaild;
		}
		if (loger.isDebugEnabled())
			loger.debug("sign[{}],timestamp[{}],nonce[{}],token[{}]", signature, timestamp, nonce, token);
		List<String> signlist = Lists.newArrayList(token, timestamp, nonce);
		Collections.sort(signlist);
		String sign = "";
		for (String value : signlist) {
			sign += value;
		}
		if (signature != null) {
			vaild = signature.equals(DigestUtils.sha1Hex(sign));
		}
		return vaild;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
