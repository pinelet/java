package weixinpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;

public class EventMsgTest {

	DocumentBuilder dp = null;
	Transformer transfer = null;
	Logger loger = LoggerFactory.getLogger(getClass());
	XPath xpath = null;
	
	@Before
	public void setUp() throws Exception {
		dp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		transfer = TransformerFactory.newInstance().newTransformer();
		xpath = XPathFactory.newInstance().newXPath();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * <xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[FromUser]]></FromUserName>
		<CreateTime>123456789</CreateTime>
		<MsgType><![CDATA[event]]></MsgType>
		<Event><![CDATA[SCAN]]></Event>
		<EventKey><![CDATA[SCENE_VALUE]]></EventKey>
		<Ticket><![CDATA[TICKET]]></Ticket>
		</xml>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws TransformerException 
	 */
	@Test
	public void msgEventScanTest() throws MalformedURLException, IOException, TransformerException {
		Map<String, String> map = Maps.newHashMap();
		map.put("ToUserName", "touser");
		map.put("FromUserName","formuser");
		map.put("CreateTime","1348831860");
		map.put("MsgType","event");
		map.put("Event","subscribe");
		map.put("EventKey","qrscene_123123");
		map.put("Ticket","t");
		HttpURLConnection con = (HttpURLConnection)new URL("http://localhost:9090/pay/main").openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.connect();
		OutputStream out = con.getOutputStream();
		transfer.transform(new DOMSource(map2xml(map)), new StreamResult(out));
		out.flush();
		out.close();
		
		loger.info("返回：" + con.getResponseMessage());
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String res = null;
		while((res = reader.readLine()) != null) {
			loger.info("明细 :" + res);
		}
		con.disconnect();
	}
	
	private Document map2xml(Map<String, String> info) {
		Document doc = dp.newDocument();
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		Element element = null;
		for(Entry<String, String> node : info.entrySet()) {
			element = doc.createElement(node.getKey());
			element.setTextContent(node.getValue());
			xml.appendChild(element);
		}
		return doc;
	}
	
	@Test
	public void msgEventSub() throws XPathExpressionException {
		Map<String, String> info = Maps.newHashMap();
		info.put("return_code", "success");
		info.put("return_msg", "message" + System.currentTimeMillis()/1000);
		System.out.print(xpath.evaluate("/xml/return_code", map2xml(info)));
	}

}
