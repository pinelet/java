package com.pinelet.weixinpay.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WXPayServlet
 */
public class WXPayPayTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WXPayPayTestServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().setAttribute("mid", request.getParameter("mid"));
		request.getSession().setAttribute("openid", request.getParameter("openid"));
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String str = "{\"timeStamp\":\"1458572764\",\"signType\":\"MD5\",\"package\":\"prepay_id=wx20160321230556278f2318"+
"910272301788\",\"appid\":\"wxaea199a4b0a6faf8\",\"nonceStr\":\"uKMQLNW6cPOPQRTQQoYVrJi7FDvXjAjw\",\"code\":\"SUCCESS\",\"paySign\":\"193"
+"F56935DCF353CA8EB113BE4C1BE69\"}";
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(str);
		out.flush();
		
	}

}
