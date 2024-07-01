package m.client.ide.morpheus.core.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.core.messages.CoreMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class XMLUtil {
	private static final Logger LOG = Logger.getInstance(XMLUtil.class);

	@Nullable public static Document getNewDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		}

		return doc;
	}

	public static void removeAll(@NotNull Node node) {
		NodeList nodeList = node.getChildNodes();
		for (int i = nodeList.getLength()-1; i >= 0 ; i--) {
			node.removeChild(nodeList.item(i));
		}
	}

	@Nullable public static Document getDocument(@NotNull File documentFile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(documentFile);
		} catch (Exception e) {
//			LOG.error(e);
		}

		return doc;
	}

	@Nullable public static Document getDocument(@NotNull InputStream inputStream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(inputStream);
		} catch (Exception e) {
//			LOG.error(e);
		}

		return doc;
	}

	@Nullable public static Document getDocument(@NotNull VirtualFile documentFile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(documentFile.getInputStream());
		} catch (Exception e) {
			LOG.error(e);
		}

		return doc;
	}

	public static Document getDocument(String xmlString) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)))); //$NON-NLS-1$

		} catch (Exception e) {
			LOG.error(e);
		}

		return doc;
	}
	
	public static Document getDocument2(@NotNull String xmlString) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))));
	}
	
	public static String toString(Element element) throws Exception {
		DOMSource domSource = new DOMSource(element);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
	}

	public static void addXMLNamespace(Element manifestRoot, Element configManifestElement) {
		if(manifestRoot == null || configManifestElement == null) { return; }

		NamedNodeMap configManifestAttributes = configManifestElement.getAttributes();
		if(configManifestAttributes == null) { return; }

		String EMPTYSTRING = "";
		String ENTITY_COLON = ":";
		String XMLNS_PREFIX = "xmlns";
		for(int i=0; i<configManifestAttributes.getLength(); i++) {
			Node attribute = configManifestAttributes.item(i);

			String name = attribute.getNodeName();
			String value = attribute.getNodeValue();
			int col = name.lastIndexOf(ENTITY_COLON);
			final String prefix = (col > 0) ? name.substring(0, col) : EMPTYSTRING;

			if (!EMPTYSTRING.equals(prefix))
			{
				if (prefix.equals(XMLNS_PREFIX)) {
					manifestRoot.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, name, value);
				}
			}
		}
	}

	private static byte @NotNull [] writerBOSFlush(Document newDoc, ByteArrayOutputStream bos, @NotNull PrintWriter writer) throws TransformerException {
		writer.flush();

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
		transformer.transform(new DOMSource(newDoc), new StreamResult(bos));

		return bos.toByteArray();
	}

	public static String writeXMLString(Document newDoc) {
		String contentString = null;
		String charset = "UTF-8"; //$NON-NLS-1$
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			PrintWriter writer;
			writer = new PrintWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8)); //$NON-NLS-1$
			writer.println(MessageFormat.format("<?xml version=\"1.0\" encoding=\"{0}\" standalone=\"yes\" ?>", charset)); //$NON-NLS-1$

			byte[] data = writerBOSFlush(newDoc, bos, writer);

			contentString = new String(data, 0, data.length);

		} catch (UnsupportedEncodingException | TransformerException | RuntimeException e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		// TODO: handle exception

		return contentString;
	}

	public static void writeXML(File documentFile, Document newDoc) {

		String charset = "UTF-8"; //$NON-NLS-1$
		FileOutputStream fos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8)); //$NON-NLS-1$
			writer.println(MessageFormat.format("<?xml version=\"1.0\" encoding=\"{0}\" standalone=\"yes\" ?>", charset)); //$NON-NLS-1$
			if (documentFile.getName().contains("library_server") || documentFile.getName().contains("library_local")) //$NON-NLS-1$ //$NON-NLS-2$
				writer.println("" + "<!DOCTYPE library-groups [\n" + "\t<!ATTLIST group\n " + "\t\tid ID #REQUIRED\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						+ "\t\ttype CDATA #REQUIRED\n" + "\t>\n" + "]>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			byte[] data = writerBOSFlush(newDoc, bos, writer);

			if (!documentFile.getParentFile().isDirectory()) {
				if(!documentFile.getParentFile().mkdirs()) {
					LOG.error(CoreMessages.get(CoreMessages.XmlUtilMkDirError, documentFile.getParentFile().getPath()));
				}
			}
			
			fos = new FileOutputStream(documentFile);
			fos.write(data);
			fos.flush();
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			try {
				bos.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Element getFirstChildElementByName(Element parent, String name) {
		if(parent == null) {
			return null;
		}
		
		NodeList nodelist = parent.getChildNodes();
		for(int i=0; i<nodelist.getLength(); i++) {
			Object obj = nodelist.item(i);
			if(obj instanceof Element) {
				Element ele = (Element) obj;
				if(ele.getTagName().equals(name)) {
					return ele;
				}
			}
		}
		return null;
	}
	
	public static @NotNull List<Element> getChildElementsByName(@NotNull Element parent, String name) {
		List<Element> list = new ArrayList<>();
		
		NodeList nodelist = parent.getChildNodes();
		for(int i=0; i<nodelist.getLength(); i++) {
			Object obj = nodelist.item(i);
			if(obj instanceof Element) {
				Element ele = (Element) obj;
				if(ele.getTagName().equals(name)) {
					list.add(ele);
				}
			}
		}
		return list;
	}
	
	public static @Nullable String getChildElementValueByName(Element parent, String name) {
		Element element = getFirstChildElementByName(parent, name);
		if(element != null) {
			return element.getTextContent();
		}
		return null;
	}
	
	public static void removeChildElement(@NotNull Element parent) {
		NodeList list = parent.getChildNodes();
		for(int i=0; i < list.getLength(); i ++) {
			Node node = list.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				parent.removeChild(node);
			}
		}
		parent.setTextContent("");
	}
	
	public static String getFullXPath(Node n) {
		if (null == n)
			return null;
		
		Node parent = null;
		Stack<Node> hierarchy = new Stack<>();
		var buffer = new StringBuilder();

		hierarchy.push(n);

		switch (n.getNodeType()) {
		case Node.ATTRIBUTE_NODE:
			parent = ((Attr) n).getOwnerElement();
			break;
		case Node.ELEMENT_NODE:
		case Node.DOCUMENT_NODE:
			parent = n.getParentNode();
			break;
		}

		while (parent != null && parent.getNodeType() != Node.DOCUMENT_NODE) {
			hierarchy.push(parent);
			parent = parent.getParentNode();
		}

		Node node;
		while (!hierarchy.isEmpty() && null != (node = hierarchy.pop())) {
			boolean handled = false;

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;

				if (buffer.length() == 0) {
					buffer.append(node.getNodeName());
				} else {
					buffer.append("/");
					buffer.append(node.getNodeName());

					if (node.hasAttributes()) {
						if (e.hasAttribute("id")) {
							buffer.append("[@id='").append(e.getAttribute("id")).append("']");
							handled = true;
						} else if (e.hasAttribute("name")) {
							buffer.append("[@name='").append(e.getAttribute("name")).append("']");
							handled = true;
						}
					}

					if (!handled) {
						int prev_siblings = 1;
						Node prev_sibling = node.getPreviousSibling();
						while (null != prev_sibling) {
							if (prev_sibling.getNodeType() == node.getNodeType()) {
								if (prev_sibling.getNodeName().equalsIgnoreCase(node.getNodeName())) {
									prev_siblings++;
								}
							}
							prev_sibling = prev_sibling.getPreviousSibling();
						}
						buffer.append("[").append(prev_siblings).append("]");
					}
				}
			} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				buffer.append("/@");
				buffer.append(node.getNodeName());
			}
		}
		return buffer.toString();
	} 
	
	public static int indent;
	
	public static Element appendChild(Document doc, Node parent, String childName) {
		indent = 0;
		getIndent(parent);

		var sb = new StringBuilder();
		sb.append("\t".repeat(Math.max(0, indent - 1)));
		
		String firstIndent = "\t";
		Node last = parent.getLastChild();
		if(last == null || last.getNodeType() != Node.TEXT_NODE) {
			firstIndent = "\n" + firstIndent + sb;
		}
		
		Text text = doc.createTextNode(firstIndent);
		parent.appendChild(text);
		
		Element child = doc.createElement(childName);
		parent.appendChild(child);
		
		text = doc.createTextNode("\n" + sb);
		parent.appendChild(text);
		
		return child;
	}
	
	public static void getIndent(Node element) {
		Node parent = element.getParentNode();
		if(parent != null && parent instanceof Element) {
			getIndent(parent);
		}
		indent++;
	}
	
	public static void removeChild(Node parent, Node child) {
		NodeList nodeList = parent.getChildNodes();
		
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
				Element ele = (Element) node;
				if(ele == child) {
					parent.removeChild(ele);
					
					int perIdx = i -1;
					if(perIdx > -1) {
						Node next = nodeList.item(i-1);
						if(next != null && next.getNodeType() == Node.TEXT_NODE) {
							parent.removeChild(next);
						}
					}
				}
			}
		}
		
		nodeList = parent.getChildNodes();
		int childCount = 0;
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
				childCount++;
			}
		}
		if(childCount == 0) {
			for(int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				parent.removeChild(node);
			}
		}
	}

	public static void setXmlData(Document document, String elementPath, String textContents) {
		String[] paths = elementPath.split("/");
		Element child = document.getDocumentElement();
		for(String path : paths) {
			if(child == null) {
				return;
			}
			child = getFirstChildElementByName(child, path);
		}
		if(child != null) {
			child.setTextContent(textContents);
		}
	}

	public static List<Element> getElements(Document document, String elementPath) {
		String[] paths = elementPath.split("/");
		Element child = document.getDocumentElement();
		int depth = 0;
		for(; depth < paths.length-1 ; depth ++) {
			String path = paths[depth];
			if(child == null) {
				return null;
			}
			child = getFirstChildElementByName(child, path);
		}
		if(child != null) {
			return getChildElementsByName(child, paths[depth]);
		}

		return null;
	}

	public static Element getElementByName(Element element, String child) {
		return getFirstChildElementByName(element, child);
	}
}
