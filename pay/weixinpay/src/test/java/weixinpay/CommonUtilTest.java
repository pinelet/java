package weixinpay;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pinelet.weixinpay.servlet.VaildService;

public class CommonUtilTest {
	
	static String r1 = "q1ChOpOYyJYcbpE2X10LFF19dlt8V75C";//32
	static String r2 = "7OEsWTwEY6aIJS3A7gcsqMyuY2myL1Mk";//32

	VaildService service = new VaildService();
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	
	}

	@Test
	public void doRandomString() {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
	    Random random = new Random();  
	    StringBuffer buf = new StringBuffer();  
	    for (int i = 0; i < 32; i++) {  
	        int num = random.nextInt(62);  
	        buf.append(str.charAt(num));  
	    }  
	    System.out.println(buf.toString()); 
	}
	
	@Test
	public void doTestSignatureVerify() {
		//service.signatureVerify("sign", "2016-02-01 18:29", "nonce");
	}
}
