package com.pinelet.common.httpasync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HttpClientCallbackResult {

	InputStream replyData;

	OutputStream responseForRequestAsync;

	HttpAsyncException exception;
	
	int retCode;

	public HttpClientCallbackResult(InputStream replyData,
			OutputStream responseForRequestAsync) {
		this.replyData = replyData;
		this.responseForRequestAsync = responseForRequestAsync;
	}

	public InputStream getReplyData() {
		return replyData;
	}
	
	public String getReplyDataAsString() {
		
		if (this.replyData==null){
			return "";
		}
		
		StringBuilder resp=new StringBuilder();
        InputStream currReplyData = this.replyData;

        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(currReplyData, "UTF-8"));
            while ((line = in.readLine()) != null) {
                resp.append(line);
            }
        } catch (IOException e) {
        	//ignore
        } finally {
            try {
				in.close();
			} catch (IOException e) {
				//ignore
			}
        }
        return resp.toString();
	}

	public OutputStream getResponseForRequestAsync() {
		return responseForRequestAsync;
	}

	public HttpAsyncException getException() {
		return exception;
	}

	protected void setException(HttpAsyncException throwe) {
		this.exception = throwe;
	}
	

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

}
