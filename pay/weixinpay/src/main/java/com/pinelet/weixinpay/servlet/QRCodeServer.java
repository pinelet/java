package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Charsets;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pinelet.common.httpasync.HttpClientCallback;
import com.pinelet.common.httpasync.HttpClientCallbackResult;
import com.pinelet.weixinpay.wxservice.ApplicationContextManager;
import com.pinelet.weixinpay.wxservice.QRCodeService;
import com.pinelet.weixinpay.wxservice.ReflushAccessTokenExcutor;

/**
 * 生成二维码显示
 */

public class QRCodeServer extends HttpServlet {
	
	static final long serialVersionUID = -5602565429759609059L;
	private int width = 300;
	private int height = 300;
	private ErrorCorrectionLevel level = ErrorCorrectionLevel.H;
	private String urlprefix = null;
	private String redirectUrl = null;
	private String shortUrlServer = null;
	private Logger loger = LoggerFactory.getLogger(getClass());
	private ApplicationContextManager app = ApplicationContextManager.getInstance();
 
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		width = Integer.parseInt(config.getInitParameter("qrcode.width"));
		height = Integer.parseInt(config.getInitParameter("qrcode.height"));
		//M, L, H, Q
		level = ErrorCorrectionLevel.forBits(Integer.parseInt(config.getInitParameter("qrcode.level")));
		urlprefix = config.getInitParameter("qrcode.urlprefix");
		redirectUrl = config.getInitParameter("qrcode.redirecturl");
		shortUrlServer = config.getInitParameter("qrcode.shorturl.server");
	}


	/**
	 * https://open.weixin.qq.com/connect/oauth2/authorize?
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String machineID = request.getParameter("mid");
        
        String longurl = urlprefix + "?" + getExtendParams(machineID);
        String req = JSON.toJSONString(Maps.newHashMap("action","long2short","long_url", longurl)); 
        if (loger.isDebugEnabled())
        	loger.debug("req long url {}", req);
		//try {
			//调用服务生成短URL
			app.getClient().doAsyncHttpPostWithReqAsync(shortUrlServer+ReflushAccessTokenExcutor.getAccessToken(), req.getBytes(), "image/png", null, new HttpClientCallback() {
				BitMatrix bitMatrix = null;
				@Override
				public void completed(HttpClientCallbackResult result) {
					
					JSONObject reJson = JSONObject.parseObject(result.getReplyDataAsString());
					if (loger.isDebugEnabled())
						loger.debug("return short url json -[{}]", reJson);
					if (reJson.getInteger("errcode") == 0) {
						Map<EncodeHintType, Object> hints = Maps.newHashMap();  
				        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   // 内容所使用字符集编码  
				        hints.put(EncodeHintType.ERROR_CORRECTION, level);  //L 7%/M 15%/Q 25%/H 30%
				        try (OutputStream out = result.getResponseForRequestAsync();){
							bitMatrix = new MultiFormatWriter().encode(reJson.getString("short_url"), BarcodeFormat.QR_CODE, width, height, hints);
					        QRCodeService.writeToStream(bitMatrix, "png", out);
					        out.flush();
				        } catch (Exception e) {
				        	loger.error("",e);
				        }
					}
				}

				@Override
				public void failed(HttpClientCallbackResult result) {
					loger.error("get ShortUrl failed and returncode is {}, replay message [{}]", result.getRetCode(), result.getReplyDataAsString());					
				}

			}, request);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
	 * @param machineID 自定义参数：
	 * @return
	 */
	private String getExtendParams(String machineID) {
		Map<String, String> pairs = Maps.newHashMap();
		pairs.put("appid", ApplicationContextManager.getInstance().get(ApplicationContextManager.APPID));
		try {
			pairs.put("redirect_uri", URLEncoder.encode(redirectUrl, "utf-8"));
		} catch (UnsupportedEncodingException e) {}
		pairs.put("response_type", "code");
		pairs.put("scope", "snsapi_base");
		pairs.put("state", machineID);
		return ApplicationContextManager.map2String(pairs, true) + "#wechat_redirect";
	}

}
