package com.pinelet.common.httpasync;

/**
 * Created by lijunteng on 15/7/21.
 */
public interface HttpClientCallback {

	public void completed(HttpClientCallbackResult result);

	public void failed(HttpClientCallbackResult result);
}
