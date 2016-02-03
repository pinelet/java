package com.pinelet.common.httpasync;

public class HttpAsyncClientFactory {

	public static HttpAsyncClient build(int maxConnectionPerRoute,int maxTotalConnection) {
		HttpAsyncClient client=new HttpAsyncClient(maxConnectionPerRoute,maxTotalConnection);
		return client;
	}
	
	public static HttpAsyncClient build(int maxConnectionPerRoute,int maxTotalConnection,int sockTimeout,int connectTimeout,int requestTimeout) {
		HttpAsyncClient client=new HttpAsyncClient(maxConnectionPerRoute,maxTotalConnection,sockTimeout,connectTimeout,requestTimeout);
		return client;
	}
}
