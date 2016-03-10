<%@ page language="java" session="true" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="pragma" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache"> 
<meta http-equiv="expires" content="0"> 
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
			<div class="weui_cell_bd weui_cell_primary">${sessionScope.mid}</div>
		</div>
		<div class="weui_cell">
			<div class="weui_cell_hd">
				<label class="weui_label">金额</label>
			</div>
			<div class="weui_cell_bd weui_cell_primary">
				<input class="weui_input" id="amount" type="number" pattern="[0-9][.]*"
					placeholder="请输入金额(元)" />
			</div>
		</div>

		<div class="weui_btn_area">
			<a class="weui_btn weui_btn_primary" href="javascript:" id="pay">支付2</a>
		</div>
		<div class="weui_cells_tips">*测试用</div>
	</div>

	<script src="js/jquery-2.1.4.js"></script>
	<script type="js/jquery-weui.js"></script>
	<script type="text/javascript">
	$.ajaxSetup({cache:false});
	//支付
	$(document).ready(function(){
		if (typeof WeixinJSBridge == "undefined"){
		   if( document.addEventListener ){
		       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
		   }else if (document.attachEvent){
		       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
		       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
		   }
		}
	});
	//生成订单提交
	$('#pay').click(function() {
		//ajax提交订单
		amount = $("#id").val();
		if (isNaN(amount)) {
			$.alert("请输入金额");
			return false;
		}
		$.post("/wx/suborder", 
			   '{
			   	"amount": amount
			   }', function(result) {
			//根据返回值跳出确认域对话框
			$.confirm(
				'<br>商户名：水魔力<br>支付金额：' ＋ result.amount + '(元)', //弹出窗内容
				'确认支付', //弹出窗title
				activepay(result),//点确认时
				function(){
					$.alert('is callbackCancel');
				});
			result.
			},'json');
		//点击确认后提交支付

	});

	//用户提交支付，应放到跳出域调用
	var activepay = function(payinfo) {
	 WeixinJSBridge.invoke(
        'getBrandWCPayRequest', {
            "appId":payinfo.appid,     //公众号名称，由商户传入     
            "timeStamp":payinfo.timestamp,    //时间戳，自1970年以来的秒数     
            "nonceStr":payinfo.nonce, //随机串     
            "package":payinfo.prepay_id,//"prepay_id=u802345jgfjsdfgsdg888",     
            "signType":"MD5",         //微信签名方式：     
            "paySign":payinfo.sign //微信签名 
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
	};
</script>
</body>
</html>
