package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.OutputStream;
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

import com.beust.jcommander.internal.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pinelet.weixinpay.wxservice.QRCodeService;

/**
 * 生成二维码显示
 */

public class QRCodeServlet extends HttpServlet {
	
	static final long serialVersionUID = -5602565429759609059L;
	private int width = 300;
	private int height = 300;
	private ErrorCorrectionLevel level = ErrorCorrectionLevel.H;
	private String urlprefix = null;
	private Logger loger = LoggerFactory.getLogger(getClass());
 
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		width = Integer.parseInt(config.getInitParameter("width"));
		height = Integer.parseInt(config.getInitParameter("height"));
		//M, L, H, Q
		level = ErrorCorrectionLevel.forBits(Integer.parseInt(config.getInitParameter("level")));
		urlprefix = config.getInitParameter("urlprefix");
	}


	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String machineID = request.getParameter("mid");
		Map hints = Maps.newHashMap();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   // 内容所使用字符集编码  
        hints.put(EncodeHintType.ERROR_CORRECTION, level);  //L 7%/M 15%/Q 25%/H 30%
        BitMatrix bitMatrix = null;
		try {
			bitMatrix = new MultiFormatWriter().encode(urlprefix + "&m=" + machineID, BarcodeFormat.QR_CODE, width, height, hints);
		} catch (WriterException e) {
			loger.error("", e);
		}  
		response.setContentType("image/png");
        // 生成二维码  
		OutputStream out = response.getOutputStream();
        QRCodeService.writeToStream(bitMatrix, "png", out);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
