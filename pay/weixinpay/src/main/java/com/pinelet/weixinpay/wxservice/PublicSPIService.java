package com.pinelet.weixinpay.wxservice;

import java.util.Map;

import javax.servlet.AsyncContext;

import com.pinelet.weixinpay.msg.AbsProcessMessage;

public class PublicSPIService extends AbsProcessMessage {

	private static String prePayURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	public PublicSPIService(AsyncContext ctx, Map<String, String[]> infos) {
		super(ctx, infos);
	}

	@Override
	public void run() {

		//向腾讯URL支付请求，生成prepay_id
		String prepay_id = null;
		ctx.getRequest().setAttribute("prepay_id", prepay_id);
		//组织返回客户支付页面的报文JSON appid/timeStamp/nonceStr/package(prepay_id)/signType/paySign
		String iSpikey = app.get(ApplicationContextManager.SPIKEY);
		long iTimeStamp = System.currentTimeMillis()/1000;	//10
		String iNonceStr = getRandomString(32);	//32
		String iPackage = //统一下单后生成的
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
	 *  <total_fee>1</total_fee> <trade_type>JSAPI</trade_type>
	 *  <sign>0CB01533B8C1EF103065174F50BCA001</sign> 
	 * </xml>
	 * @return
	 */
	private String createPrePayRequest() {
		//TODO 需要取得当前客户的ID
	}
}
