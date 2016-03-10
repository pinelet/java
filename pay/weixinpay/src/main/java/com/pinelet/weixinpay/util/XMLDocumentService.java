package com.pinelet.weixinpay.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;

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
	
	public String createXML(String rootName, Map<String, String> nodes) {
		StringBuffer xml = new StringBuffer("<" + Optional.of(rootName) + ">");
		for (Entry<String, String> node : nodes.entrySet()) {
			xml.append('<').append(Optional.of(node.getKey())).append('>');
			xml.append(Optional.of(node.getValue()));
			xml.append("</").append(node.getKey()).append('>');
		}

		return xml.append("</" + rootName + ">").toString();
	}
	
	public Node getNode(String rule, Document doc) {
		Node node = null;
		try {
			node = (Node)xpath.evaluate(rule, doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;
	}
	
	public String getNodeContent(String rule, Document doc) {
		String node = null;
		try {
			node = xpath.evaluate(rule, doc);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return node;
	}

}
