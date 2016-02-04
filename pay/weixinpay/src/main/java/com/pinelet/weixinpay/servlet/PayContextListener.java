package com.pinelet.weixinpay.servlet;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pinelet.weixinpay.wxservice.ApplicationContextManager.*;

import com.pinelet.weixinpay.wxservice.ApplicationContextManager;
import com.pinelet.weixinpay.wxservice.ReflushAccessTokenExcutor;

/**
 *初始化系统参数以及启动定时任务等
 */
@WebListener
public class PayContextListener implements ServletContextListener {

	private Logger loger = LoggerFactory.getLogger(this.getClass());
	private ReflushAccessTokenExcutor executor = null;
	/**
	 * 1.取得web.xml中的配置参数
     * 2.启动定时获取access_token
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	//log init
    	loger.info("- - - - - - - -   welcome to use PENGDA PAY application   - - - - - - - -");
    	ApplicationContextManager app = ApplicationContextManager.getInstance();
    	// add param from web.xml
    	Enumeration<String> paramnames = sce.getServletContext().getInitParameterNames();
    	while (paramnames.hasMoreElements()) {
    		String key = paramnames.nextElement();
    		app.set(key, sce.getServletContext().getInitParameter(key));
    		if(loger.isDebugEnabled())
    			loger.debug("init param key-[{}] and value-[{}]", key, app.get(key));
    	}
    	//start executor
    	executor = new ReflushAccessTokenExcutor();
    	executor.buildAccessToken(app.get(APPID), app.get(APPSECRET), 40);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         executor.shutdown();
         loger.info("- - - - - - -   good bye   - - - - - - -");
    }
	
}
