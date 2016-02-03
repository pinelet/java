package com.pinelet.common.httpasync;


import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Http请求的工具类
 */
public class HttpAsyncClient {

	enum HttpMethod {
		GET, POST
	}
	
	private static HttpAsyncClient client = null;
	
	/**
	 * 需要先初始化调用build方法
	 * @param maxConnectionPerRoute
	 * @param maxTotalConnection
	 */
	public static void build(int maxConnectionPerRoute,int maxTotalConnection) {
		client=new HttpAsyncClient(maxConnectionPerRoute,maxTotalConnection);
	}
	
	public static void build(int maxConnectionPerRoute,int maxTotalConnection,int sockTimeout,int connectTimeout,int requestTimeout) {
		client=new HttpAsyncClient(maxConnectionPerRoute,maxTotalConnection,sockTimeout,connectTimeout,requestTimeout);
	}
	
	/**
	 * 在调用build方法之后可用
	 * @return
	 */
	public static HttpAsyncClient instance() {
		return client;
	}

	private CloseableHttpAsyncClient apacheAsyncClient;
	private int maxConnectionPerRoute=50;
	private int maxTotalConnection=500;
	
	protected HttpAsyncClient(int maxConnectionPerRoute,int maxTotalConnection) {
		apacheAsyncClient=initCloseableHttpAsyncClient(maxConnectionPerRoute,maxTotalConnection,10000,15000,10000);		
	}
	
	protected HttpAsyncClient(int maxConnectionPerRoute,int maxTotalConnection,int sockTimeout,int connectTimeout,int requestTimeout) {
		apacheAsyncClient=initCloseableHttpAsyncClient(maxConnectionPerRoute,maxTotalConnection,sockTimeout,connectTimeout,requestTimeout);
	}

    private CloseableHttpAsyncClient initCloseableHttpAsyncClient(int maxConnectionPerRoute,int maxTotalConnection,int sockTimeout,int connectTimeout,int requestTimeout){
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor();
        } catch (IOReactorException e) {
           //ignore
        }
        
        /**
         * 增加请求连接的相关超时         */
        RequestConfig requestConfig = RequestConfig.custom()
        	    .setSocketTimeout(sockTimeout)
        	    .setConnectTimeout(connectTimeout)
        	    .setConnectionRequestTimeout(requestTimeout).build();
        
        if (maxConnectionPerRoute>0) {
        	this.maxConnectionPerRoute=maxConnectionPerRoute;
        }
        
        if (maxTotalConnection>0) {
        	this.maxTotalConnection=maxTotalConnection;
        }
        
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
        cm.setDefaultMaxPerRoute(this.maxConnectionPerRoute);
        cm.setMaxTotal(this.maxTotalConnection);
        return HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(cm).build();
    }

	
	/**
	 * doAsyncHttpPostWithReqAsync
	 * @param url
	 * @param content
	 * @param callBack
	 * @param request
	 */
	public void doAsyncHttpPostWithReqAsync(String url, String content,
			HttpClientCallback callBack, HttpServletRequest request) {

		if (null==url||"".equals(url) || null == callBack || null == request) {
			return;
		}

		AsyncReqProcWithHttpClientCallback proc = buildProcCallback(callBack,
				request);

		doHttpOutboundAsyncInternal(HttpMethod.POST, url, content,null,null,"utf-8",proc);
	}
	
	/**
	 * doAsyncHttpPostWithReqAsync
	 * @param url
	 * @param data
	 * @param contentType
	 * @param encoding
	 * @param callBack
	 * @param request
	 */
	public void doAsyncHttpPostWithReqAsync(String url, byte[] data, String contentType, String encoding,
			HttpClientCallback callBack, HttpServletRequest request) {

		if (null==url||"".equals(url) || null == callBack || null == request) {
			return;
		}

		AsyncReqProcWithHttpClientCallback proc = buildProcCallback(callBack,
				request);

		doHttpOutboundAsyncInternal(HttpMethod.POST, url, null,data,contentType,encoding,proc);
	}
	
	/**
	 * doAsyncHttpPost
	 * @param url
	 * @param content
	 * @param callBack
	 */
	public void doAsyncHttpPost(String url, String content,
			HttpClientCallback callBack) {

		if (null==url||"".equals(url) || null == callBack) {
			return;
		}
		
		AsyncReqProcWithHttpClientCallback proc = new AsyncReqProcWithHttpClientCallback(
				callBack);

		doHttpOutboundAsyncInternal(HttpMethod.POST, url, content,null,null,"utf-8",proc);
	}
	
	/**
	 * doAsyncHttpPost
	 * @param url
	 * @param data
	 * @param contentType
	 * @param encoding
	 * @param callBack
	 */
	public void doAsyncHttpPost(String url, byte[] data, String contentType,String encoding,HttpClientCallback callBack) {
		
		if (null==url||"".equals(url) || null == callBack || null == data) {
			return;
		}
		
		AsyncReqProcWithHttpClientCallback proc = new AsyncReqProcWithHttpClientCallback(
				callBack);

		doHttpOutboundAsyncInternal(HttpMethod.POST, url, null,data,contentType,encoding,proc);
	}
	
	/**
	 * doAsyncHttpGetWithReqAsync
	 * @param url
	 * @param callBack
	 * @param request
	 */
	public void doAsyncHttpGetWithReqAsync(String url,
			HttpClientCallback callBack, HttpServletRequest request) {

		if (null==url||"".equals(url) || null == callBack || null == request) {
			return;
		}

		AsyncReqProcWithHttpClientCallback proc = buildProcCallback(callBack,
				request);

		doHttpOutboundAsyncInternal(HttpMethod.GET, url, null,null,null,"utf-8", proc);

	}
	
	/**
	 * doAsyncHttpGet
	 * @param url
	 * @param callBack
	 */
	public void doAsyncHttpGet(String url, HttpClientCallback callBack) {
		
		if (null==url||"".equals(url) || null == callBack) {
			return;
		}
		AsyncReqProcWithHttpClientCallback proc = new AsyncReqProcWithHttpClientCallback(
				callBack);

		doHttpOutboundAsyncInternal(HttpMethod.GET, url, null,null,null,"utf-8",proc);
	}
	
	/**
	 * buildProcCallback
	 * @param callBack
	 * @param request
	 * @return
	 */
	private AsyncReqProcWithHttpClientCallback buildProcCallback(
			HttpClientCallback callBack, HttpServletRequest request) {
		
		AsyncContext ac=request.startAsync();
		
		AsyncReqProcWithHttpClientCallback proc = new AsyncReqProcWithHttpClientCallback(
				ac, callBack);

		ac.addListener(proc);
		ac.setTimeout(2500);
		return proc;
	}
	
		
	/**
	 * doHttpOutboundAsyncInternal
	 * @param method
	 * @param url
	 * @param content
	 * @param callBack
	 */
	private void doHttpOutboundAsyncInternal(HttpMethod method, String url,
			String content, byte[] data, String contentType, String encoding, FutureCallback<HttpResponse> callBack) {
		
		if (null==url||"".equals(url) || null == callBack) {
			return;
		}
		
		if (null==encoding) {
			encoding="utf-8";
		}
		
		if (null==contentType) {
			contentType="application/octet-stream";
		}

		try {
			HttpUriRequest httpMethod = null;
			switch (method) {
			case POST:
				httpMethod = buildPostMethod(url, content, data, contentType,
						encoding);
				break;
			case GET:
			default:
				httpMethod = new HttpGet(url);
				break;
			}
			
			/**
			 * 这个方法可重入，直接start就好	 */
			this.apacheAsyncClient.start();
			
			this.apacheAsyncClient.execute(httpMethod, callBack);

		} catch (RuntimeException e) {
			//ignore
		}
	}

	/**
	 * buildPostMethod
	 * @param url
	 * @param content
	 * @param data
	 * @param contentType
	 * @param encoding
	 * @return
	 * @throws UnsupportedCharsetException
	 */
	private HttpUriRequest buildPostMethod(String url, String content,
			byte[] data, String contentType, String encoding)
			throws UnsupportedCharsetException {
		HttpUriRequest httpMethod;
		httpMethod = new HttpPost(url);

		if (null!=content&&!"".equals(content)) {
			((HttpPost) httpMethod).setEntity(new StringEntity(content,
					encoding));
		}
		else {
			if (null!=data&&data.length>0) {
				((HttpPost) httpMethod).setEntity(new ByteArrayEntity(data,ContentType.create(contentType,encoding)));
			}
		}
		return httpMethod;
	}
	
	public void shutdown() {
		
		try {
			this.apacheAsyncClient.close();
		} catch (IOException e) {
			//ignore
		}
	}
    
}
