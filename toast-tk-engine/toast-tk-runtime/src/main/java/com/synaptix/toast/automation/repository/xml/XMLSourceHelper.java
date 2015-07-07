package com.synaptix.toast.automation.repository.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.synaptix.toast.adapter.web.AbstractSynaptixWebPage;
import com.synaptix.toast.adapter.web.DefaultWebElement;
import com.synaptix.toast.adapter.web.ElementFactory;
import com.synaptix.toast.adapter.web.WebAutoElement;
import com.synaptix.toast.core.adapter.AutoWebType;
import com.synaptix.toast.core.runtime.IWebElement;
import com.synaptix.toast.core.runtime.IWebElement.LocationMethod;

public class XMLSourceHelper {

	private static XMLSourceHelper instance = new XMLSourceHelper();

	private XMLSourceHelper() {
	}

	public static XMLSourceHelper getHelper() {
		return instance;
	}

	public AbstractSynaptixWebPage getPage(
		InputStream fileStream) {
		AbstractSynaptixWebPage page = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(fileStream);
			doc.getDocumentElement().normalize();
			NodeList pageList = doc.getElementsByTagName("page");
			Element identifiedPage = (Element) pageList.item(0);
			String className = identifiedPage.getAttribute("bean-ref");
			String pageName = identifiedPage.getAttribute("name");
			page = (AbstractSynaptixWebPage) Class.forName(className).newInstance(); // concrete
// class
			page.setBeanClassName(className);
			page.setPageName(pageName);
			NodeList elementList = doc.getElementsByTagName("element");
			for(int temp = 0; temp < elementList.getLength(); temp++) {
				Node nNode = elementList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String type = eElement.getAttribute("type");
					String fieldName = eElement.getAttribute("name-ref");
					String method = "";
					Integer position = 0;
					String location = "";
					NodeList c = eElement.getChildNodes();
					for(int i = 0; i < c.getLength(); i++) {
						Node item = c.item(i);
						if(item.getNodeType() == Node.ELEMENT_NODE) {
							Element locator = (Element) item;
							method = locator.getAttribute("method");
							position = Integer.valueOf(locator.getAttribute("position"));
							location = locator.getAttribute("location");
						}
					}
					IWebElement element = new DefaultWebElement(fieldName, AutoWebType.valueOf(type), location,
						LocationMethod.valueOf(method), position);
					page.initElement(element);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * <element type="link" name-ref="newOrderLink"> <locator method="CSS" position="0" location="div.navbar-inner li a"/> </element>
	 * 
	 * @param fileContent
	 * @param br
	 */
	public static String updateItemLocator(
		InputStream fileContent,
		String nameRef,
		String method,
		int position,
		String type,
		String location) {
		String newFile = "";
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileContent);
			NodeList elementsByTagName = doc.getElementsByTagName("element");
			for(int i = 0; i < elementsByTagName.getLength(); i++) {
				Node node = elementsByTagName.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element) node;
					String attribute = n.getAttribute("name-ref");
					if(nameRef.equals(attribute)) {
						NodeList childNodes = n.getChildNodes();
						for(int j = 0; j < childNodes.getLength(); j++) {
							n.removeChild(childNodes.item(j));
						}
						Element locatorNode = doc.createElement("locator");
						// set attribute to staff element
						Attr methodAttr = doc.createAttribute("method");
						methodAttr.setValue(method);
						locatorNode.setAttributeNode(methodAttr);
						Attr positionAttr = doc.createAttribute("position");
						positionAttr.setValue(String.valueOf(position));
						locatorNode.setAttributeNode(positionAttr);
						Attr typeAttr = doc.createAttribute("type");
						typeAttr.setValue(type);
						locatorNode.setAttributeNode(typeAttr);
						Attr locationAttr = doc.createAttribute("location");
						locationAttr.setValue(location);
						locatorNode.setAttributeNode(locationAttr);
						n.appendChild(locatorNode);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			newFile = result.getWriter().toString();
		}
		catch(ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch(SAXException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch(TransformerException e) {
			e.printStackTrace();
		}
		return newFile;
	}

	/**
	 * 
	 * @param javaContainer
	 * @param targetFolder
	 * @param pageName
	 */
	public static void createXmlFromBean(
		Class<? extends AbstractSynaptixWebPage> javaContainer,
		String targetFolder,
		String pageName) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element pageRootNode = doc.createElement("page");
			doc.appendChild(pageRootNode);
			// set attribute to staff element
			Attr nameAttr = doc.createAttribute("name");
			nameAttr.setValue(pageName);
			pageRootNode.setAttributeNode(nameAttr);
			Attr beanClassAttr = doc.createAttribute("bean-ref");
			beanClassAttr.setValue(javaContainer.getName());
			pageRootNode.setAttributeNode(beanClassAttr);
			Element elementsNode = doc.createElement("elements");
			for(Field f : javaContainer.getFields()) {
				if(WebAutoElement.class.isAssignableFrom(f.getType())) {
					Element elementNode = doc.createElement("element");
					String varName = f.getName();
					// set attribute to staff element
					Attr nameRefAttr = doc.createAttribute("name-ref");
					nameRefAttr.setValue(varName);
					elementNode.setAttributeNode(nameRefAttr);
					AutoWebType classAutoType = ElementFactory.getClassAutoType(f.getType());
					String type = classAutoType.name();
					Attr typeAttr = doc.createAttribute("type");
					typeAttr.setValue(type);
					elementNode.setAttributeNode(typeAttr);
					Element locatorNode = doc.createElement("locator");
					// set attribute to staff element
					Attr methodAttr = doc.createAttribute("method");
					methodAttr.setValue("${" + pageName + "." + varName + ".method}");
					locatorNode.setAttributeNode(methodAttr);
					Attr positionAttr = doc.createAttribute("position");
					positionAttr.setValue("${" + pageName + "." + varName + ".position}");
					locatorNode.setAttributeNode(positionAttr);
					Attr locationAttr = doc.createAttribute("location");
					locationAttr.setValue("${" + pageName + "." + varName + ".location}");
					locatorNode.setAttributeNode(locationAttr);
					elementNode.appendChild(locatorNode);
					elementsNode.appendChild(elementNode);
				}
			}
			pageRootNode.appendChild(elementsNode);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			// StreamResult result = new StreamResult(new File("C:\\file.xml"));
			// Output to console for testing
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
