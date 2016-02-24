package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.pinelet.weixinpay.msg.WXPayService;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;
import com.pinelet.weixinpay.wxservice.ProcessorMsgProvider;

/**
 * 预支付
 */
public class PrePayServer extends HttpServlet {
	
	private static final long serialVersionUID = -8932695729655778308L;
	Logger loger = LoggerFactory.getLogger(getClass());
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
	private static String accessOpenidUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?";

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * 支付页面入口跳转
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> info = request.getParameterMap();
		//判断用户的微信版本 from user agent
		String agent = request.getHeader("user-agent");
		if (agent != null && agent.contains("MicroMessenger/")) {
			int index = agent.indexOf("MicroMessenger/");
			int version = Integer.valueOf(agent.substring(index + 15, index + 16).trim());
			if (version >= 5) {
				//取得machineID
				String mid = request.getParameter("state");
				String code = request.getParameter("code");
				if (loger.isDebugEnabled())
					loger.debug("get machine ID - [{}]", mid);
				//TODO 判断是否存在此设备且设备状态为正常
				//通过code换取网页授权access_token
				//appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
				String suffix = ApplicationContextManager.map2String(Maps.newHashMap("appid", app.get(ApplicationContextManager.APPID), 
																	 "secret", app.get(ApplicationContextManager.APPSECRET),
																	 "code", code,
																	 "grant_type","authorization_code"));
				app.getClient().doAsyncHttpGet(accessOpenidUrl + suffix, new WXPayService(request.startAsync(request, response), info));
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
	
	private void keepsilence(HttpServletResponse response, String message) throws IOException {
		 PrintWriter print = response.getWriter();
		 print.println(message == null? "" : message);
		 print.flush();
		 return;
	}

}
