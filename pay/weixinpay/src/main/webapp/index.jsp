<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>订单</title>
<link rel="stylesheet" href="css/weui.css">
<link rel="stylesheet" href="css/pay.css">
</head>
<body ontouchstart>
	<div class="page">
		<div class="page_title">买水支付</div>
		<div class="weui_cell">
			<div class="weui_cell_hd">
				<p>设备编号:</p>
			</div>
			<div class="weui_cell_bd weui_cell_primary">12334568</div>
		</div>
		<div class="weui_cell">
			<div class="weui_cell_hd">
				<label class="weui_label">金额</label>
			</div>
			<div class="weui_cell_bd weui_cell_primary">
				<input class="weui_input" type="number" pattern="[0-9][.]*"
					placeholder="请输入金额(元)" />
			</div>
		</div>

		<div class="weui_btn_area">
			<a class="weui_btn weui_btn_primary" href="javascript:" id="pay">支付4</a>
		</div>
		<div class="weui_cells_tips">*测试用</div>
	</div>

	<script src="js/jquery-2.1.4.js"></script>
	<script type="js/jquery-weui.js"></script>
	<script type="text/javascript">
	$.ajaxSetup({cache:false});
	//支付
 	/*function onBridgeReady(){
 		alert("p1");
  	  WeixinJSBridge.invoke(
       'getBrandWCPayRequest', {
           "appId" ： "wxaea199a4b0a6faf8",     //公众号名称，由商户传入     
           "timeStamp"："123456790",         //时间戳，自1970年以来的秒数     
           "nonceStr" ： "e61463f8efa94090b1f366cccfbbb444", //随机串     
           "package" ： "prepay_id=u802345jgfjsdfgsdg888",     
           "signType" ： "MD5",         //微信签名方式：     
           "paySign" ： "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名 
       },
       function(res){     
    	  	alert(res);
           if(res.err_msg == "get_brand_wcpay_request：ok" ) {
        	  //成功处理.使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它
				alert("success:" + res) 
       		}
       		if(res.err_msg == "get_brand_wcpay_request：fail") {
       			alert("fail :" + res);
       		}
       }
   ); 
} */
	$(document).ready(function(){
		alert("s1");
		if (typeof WeixinJSBridge == "undefined"){
		   if( document.addEventListener ){
		       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
		   }else if (document.attachEvent){
		       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
		       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
		   }
		}
	});
	$('#pay').click(function() {
		alert("p");
		// WeixinJSBridge.invoke(
  //      'getBrandWCPayRequest', {
  //          "appId" ： "wxaea199a4b0a6faf8",     //公众号名称，由商户传入     
  //          "timeStamp"："123456790",         //时间戳，自1970年以来的秒数     
  //          "nonceStr" ： "e61463f8efa94090b1f366cccfbbb444", //随机串     
  //          "package" ： "prepay_id=u802345jgfjsdfgsdg888",     
  //          "signType" ： "MD5",         //微信签名方式：     
  //          "paySign" ： "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名 
  //      },
  //      function(res){     
  //   	  	alert(res);
  //          if(res.err_msg == "get_brand_wcpay_request：ok" ) {
  //       	  //成功处理.使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它
		// 		alert("success:" + res) 
  //      		}
  //      		if(res.err_msg == "get_brand_wcpay_request：fail") {
  //      			alert("fail :" + res);
  //      		}
  //      }
  //  		);
	});
</script>
</body>
</html>
