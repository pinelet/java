package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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

import com.pinelet.weixinpay.wxservice.ProcessorMsgProvider;
import com.pinelet.weixinpay.wxservice.XMLDocumentService;

/**
 * 接收消息推送主服务,默认定义为async处理servlet
 */
public class MainService extends HttpServlet {
	
	private static final long serialVersionUID = -5257873814413697109L;
	private Executor executor = null;
	private static final int MAXREQ = 10240;
	private Logger loger = LoggerFactory.getLogger(getClass());
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		executor = new ThreadPoolExecutor(10, 50, 3L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(100));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		 //请求过长(>10K),可能是非法消息，暂不处理
		 if (request.getContentLength() > MAXREQ) {
			 loger.warn("request is too big, must be little than {}, now is {}", MAXREQ, request.getContentLength());
			 keepsilence(response);
			 return;
		 }
		//解析推送的xml数据
		 Document doc = XMLDocumentService.getInstance().parse(request.getInputStream());
		//判断是推动、还是请求消息(MsgType)
		if (doc == null) {
			keepsilence(response);
			return;
		}
		//调用对应的process
		executor.execute(ProcessorMsgProvider.get(request.startAsync(), doc));
		response.getWriter().append("success").append(new Date() + "at main").flush(); 
	}
	
	private void keepsilence(HttpServletResponse response) throws IOException {
		 PrintWriter print = response.getWriter();
		 print.println("");
		 print.flush();
		 return;
	}

}
