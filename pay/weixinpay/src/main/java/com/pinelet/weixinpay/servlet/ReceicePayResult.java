package com.pinelet.weixinpay.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.io.ByteStreams;
import com.pinelet.weixinpay.msg.AbsProcessMessage;
import com.pinelet.weixinpay.util.XMLDocumentService;

/**
 * Servlet implementation class ReceicePayResult
 */
public class ReceicePayResult extends HttpServlet {
	
	private static final long serialVersionUID = 2717904282641542970L;
	private XMLDocumentService xml = XMLDocumentService.getInstance();
	Logger loger = LoggerFactory.getLogger(this.getClass());
	private String notifyUrl = null;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		notifyUrl = config.getInitParameter("result.notify.url");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Document doc = xml.parse(request.getInputStream());
		if (doc == null)return;
		Map<String, String> result = xml.parseXML("xml", doc);
		String signature = result.remove("sign");
		if (signature == null || "".equals(signature)) return;
		String code = result.get("return_code");
		String tCode = result.get("result_code");
		//验签
		if (!AbsProcessMessage.verifySignature(result, signature)) return;
		//支付成功
		if (code != null && tCode != null && "SUCCESS".equals(code) && "SUCCESS".equals(tCode)) {
			//更新支付状态
			
			//通知设备动作事件
			String total_fee = result.get("total_fee");
			String id = result.get("attach");
			String cash_fee = result.get("cash_fee");
			String wxOrderid = result.get("transaction_id");
			String time_end = result.get("time_end");
			HttpURLConnection http = (HttpURLConnection)new URL(notifyUrl).openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.setUseCaches(false); 
			StringBuffer params = new StringBuffer("data=");
	        //data:机器ID，交易类型（1充值，2打水），流水号（10位)，金额(分)，客户信息，操作时间
	        params.append(id).append(",2,")
	        				 .append(System.currentTimeMillis()/1000).append(",")
	        				 .append(total_fee).append(",")
	        				 .append(wxOrderid).append(",")
	        				 .append(time_end.substring(4));
	        OutputStream out = http.getOutputStream();
	        out.write(params.toString().getBytes());
	        out.flush();
	        out.close();
	        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
	        String str = in.readLine();
	        loger.info("return device notification -" + str);
	        if (in != null) in.close();
		}
		//TODO 支付失败，更新订单状态为失败
		else {
			String wxOrderID = result.get("transaction_id");
			String orderID = result.get("out_trade_no");
		}
		response.getWriter().println("success");
		response.flushBuffer();
	}

}
