package com.pinelet.weixinpay.util;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLDocumentService {
	
	private DocumentBuilder docbuilder = null;
	private Logger loger = LoggerFactory.getLogger(getClass());
	private static XMLDocumentService service = new XMLDocumentService();
	private XPath xpath = null;
	
	private XMLDocumentService() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			docbuilder = factory.newDocumentBuilder();
			xpath = XPathFactory.newInstance().newXPath();
		} catch (ParserConfigurationException e) {
			loger.error("constructor document failed...", e);
		}
	}
	
	public static XMLDocumentService getInstance() {
		return service;
	}
	
	public Document parse(InputStream reader) {
		Document doc = null;
		try {
			doc = docbuilder.parse(reader);
		} catch (SAXException | IOException e) {
			loger.error("parse push message from mainservice error",e);
		}
		return doc;
	}

}
