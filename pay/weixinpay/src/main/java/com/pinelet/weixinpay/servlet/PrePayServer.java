package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.google.common.io.CharStreams;
import com.pinelet.weixinpay.msg.WXPayService;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

/**
 * 预支付
 */
public class PrePayServer extends HttpServlet {
	
	private static final long serialVersionUID = -8932695729655778308L;
	Logger loger = LoggerFactory.getLogger(getClass());
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
	private static String accessOpenidUrl = null;
	private static String verifyUrl = null;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		accessOpenidUrl = config.getInitParameter("access.openid.url");
		verifyUrl = config.getInitParameter("verify.status.url");
		
	}

	/**
	 * 支付页面入口跳转
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//判断用户的微信版本 from user agent
		String agent = request.getHeader("user-agent");
		if (agent != null && agent.contains("MicroMessenger/")) {
			int index = agent.indexOf("MicroMessenger/");
			int version = Integer.valueOf(agent.substring(index + 15, index + 16).trim());
			if (version >= 5) {
				//取得machineID
				String mid = request.getParameter("state");
				request.getSession().setAttribute("mid", mid);
				String code = request.getParameter("code");
				//判断是否存在此设备且设备状态为正常
				if (!devValid(mid)) {
					keepsilence(response, "<p>Device status is abnormal, please contact administrator.</p>");
					return;
				}
				//通过code换取网页授权access_token
				//appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
				String suffix = new StringBuffer("appid=").append(app.get(ApplicationContextManager.APPID))
						.append('&').append("secret=").append(app.get(ApplicationContextManager.APPSECRET))
						.append('&').append("code=").append(code)
						.append('&').append("grant_type=").append("authorization_code").toString();
				if (loger.isDebugEnabled()) {
					loger.debug("req openid from code URL[{}]", accessOpenidUrl + suffix);
				}
				app.getClient().doAsyncHttpGet(accessOpenidUrl + suffix, new WXPayService(request.startAsync(request, response)));
			}
			else keepsilence(response, "<p>此微信版本不支持微信支付，请升级后再进行支付。</p>");
		}
		else keepsilence(response, "<p>请使用微信进行支付。</p>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private boolean devValid(String imei) {
		//发起rs请求
		HttpURLConnection http = null;
		InputStream input = null;
		try {
			http = (HttpURLConnection) new URL(verifyUrl + imei).openConnection();
			http.setConnectTimeout(30000);
			http.setReadTimeout(30000); 
			input = http.getInputStream();
			return Boolean.parseBoolean(CharStreams.toString(new InputStreamReader(input, "UTF-8")));
		} catch (IOException e) {
			loger.error("connection pengda verify url error!", e);
		} finally {
			if (input != null)
				try {input.close();} catch (IOException e) {}
		}
		return false;
	}
	
	private void keepsilence(HttpServletResponse response, String message) throws IOException {
		 PrintWriter print = response.getWriter();
		 print.println(message == null? "" : message);
		 print.flush();
		 return;
	}

}
