package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.pinelet.weixinpay.util.XMLDocumentService;

/**
 * 接收消息推送主服务,默认定义为async处理servlet
 */
public class MainServer extends HttpServlet {
	
	private static final long serialVersionUID = -5257873814413697109L;
	private Executor executor = null;
	private static final int MAXREQ = 2048;
	private Logger loger = LoggerFactory.getLogger(getClass());
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		executor = new ThreadPoolExecutor(10, 50, 3L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(300));
	}

	/**
	 * 处理消息推送、请求信息
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		 //请求过长(>10K),可能是非法消息，暂不处理
		 if (request.getContentLength() > MAXREQ) {
			 loger.warn("request is too big, must be little than {}, now is {}", MAXREQ, request.getContentLength());
			 keepsilence(response, null);
			 return;
		 }
//		 BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		 String line = in.readLine();
//			 loger.info("get reqline [{}] ", line);
		//解析推送的xml数据
		 Document doc = XMLDocumentService.getInstance().parse(request.getInputStream());
		//判断是推送、还是请求消息跳转(MsgType)  
		 //如果
		if (doc == null) {
			keepsilence(response, null);
			return;
		}
		//调用对应的process
		//executor.execute(ProcessorMsgProvider.get(request.startAsync(), doc));
		
	}
	
	private void keepsilence(HttpServletResponse response, String message) throws IOException {
		 PrintWriter print = response.getWriter();
		 print.println(message == null? "" : message);
		 print.flush();
		 return;
	}

}
