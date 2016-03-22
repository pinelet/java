package com.pinelet.weixinpay.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.pinelet.weixinpay.msg.AbsProcessMessage;
import com.pinelet.weixinpay.msg.PublicSPIService;
import com.pinelet.weixinpay.util.XMLDocumentService;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;

/**
 * 用户提交的统一下单申请
 */

public class SubmitOrderServer extends HttpServlet {
       
	private static final long serialVersionUID = -654405602341609622L;
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
	private static String prePayURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private Logger loger = LoggerFactory.getLogger(getClass());
	private String notifyurl = null;
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		notifyurl = config.getInitParameter("pay.notifyURL");
	}


	/**
	 * 统一下单请求
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String userIP = this.getIpAddr(request);
		 BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		 String line = in.readLine();
		 if (loger.isDebugEnabled())
			 loger.debug("get pre order line {}", line);
		 String amount = JSONObject.parseObject(line).getString("amount");
		 String openid = (String)request.getSession().getAttribute("openid");
		 String mid = (String)request.getSession().getAttribute("mid");
		 String reqxml = createContent(mid, openid, userIP, amount);
		 if (loger.isDebugEnabled())
			 loger.debug("req union trx xml --{}", reqxml);
		 //FIXME 生成的IP有问题，return_code[FAIL], return_msg[商户号mch_id与appid不匹配]
		app.getClient().doAsyncHttpPost(prePayURL, createContent(mid, openid, userIP, amount), new PublicSPIService(request.startAsync()));
	}
	
	
	/**
	 * <xml> 
	 * 	<appid>wx2421b1c4370ec43b</appid>
	 *  <attach>mid</attach>
	 *  <body>JSAPI支付测试</body>
	 *  <mch_id>10000100</mch_id>
	 *  <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
	 *  <notify_url>http://www.ssjcx.com/pay/notifyPayResult</notify_url>
	 *  <openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid>
	 *  <out_trade_no>1415659990</out_trade_no>
	 *  <spbill_create_ip>14.23.150.211</spbill_create_ip>
	 *  <total_fee>1</total_fee>
	 *  <trade_type>JSAPI</trade_type>
	 *  <sign>0CB01533B8C1EF103065174F50BCA001</sign> 
	 * </xml>
	 * @return
	 */
	private String createContent(String mid, String openid, String ip, String amount) {
		Map<String, String> infos = Maps.newHashMap("appid",app.get(ApplicationContextManager.APPID));

		String randomString = AbsProcessMessage.getRandomString(32);
		infos.put("attach", mid);//此可以放入设备ＩＤ之类信息
		infos.put("body","购水");//购水 充值
		infos.put("mch_id",app.get(ApplicationContextManager.SHOPID));//商户号
		infos.put("nonce_str",randomString);
		infos.put("notify_url",notifyurl);
		infos.put("openid",Optional.of(openid).get());
		infos.put("out_trade_no",randomString);//TODO 订单号 ，需要记录订单表
		infos.put("spbill_create_ip",ip);
		infos.put("total_fee",amount);
		infos.put("trade_type", "JSAPI");
		infos.put("sign",AbsProcessMessage.createSignature(infos));
		return XMLDocumentService.getInstance().createXML("xml", infos);
	}
	
	private String getIpAddr(HttpServletRequest request) {      

        String ip = request.getHeader("x-forwarded-for");      
        	if (loger.isDebugEnabled())
        		loger.debug("x-forwarded-for - [{}]", ip);
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
                ip = request.getHeader("Proxy-Client-IP");  
                if (loger.isDebugEnabled())
            		loger.debug("Proxy-Client-IP - [{}]", ip);
            }     

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
                ip = request.getHeader("WL-Proxy-Client-IP");     
                if (loger.isDebugEnabled())
            		loger.debug("WL-Proxy-Client-IP - [{}]", ip);
            }     

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
                ip = request.getRemoteAddr(); 
                if (loger.isDebugEnabled())
            		loger.debug("remotAddr - [{}]", ip);
            }   
       return Splitter.on(',').trimResults().splitToList(ip).get(0);     
    }

}
