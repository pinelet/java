package weixinpay;

import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.*;

import com.pinelet.weixinpay.servlet.VaildService;

public class CommonUtilTest {
	
	static String r1 = "q1ChOpOYyJYcbpE2X10LFF19dlt8V75C";//32 
	static String r2 = "7OEsWTwEY6aIJS3A7gcsqMyuY2myL1Mk";//32 token

	VaildService service = new VaildService();
	MessageDigest digest = null;
	
	@Before
	public void setUp() throws Exception {
		service.init(new ServletConfig() {

			@Override
			public String getServletName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInitParameter(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		digest = DigestUtils.getSha1Digest();
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
		//sign[a60c037c88cdd03dded67be09b7db1b83a7f508a],timestamp[1455700236],nonce[1057192582],token[7OEsWTwEY6aIJS3A7gcsqMyuY2myL1Mk]
		String signature = "a60c037c88cdd03dded67be09b7db1b83a7f508a";
		String timestamp = "1455700236";
		String nonce = "1057192582";
		//digest.update((nonce + timestamp + r2).getBytes());
		boolean vaild = service.signatureVerify(signature, timestamp, nonce, r2);
		Assert.assertTrue("verify signature failed.", vaild);
	}
}
