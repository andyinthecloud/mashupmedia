/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlHelper {

	private static DocumentBuilderFactory documentBuilderFactory;

	private static XPathFactory xPathFactory;

	public static DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory != null) {
			return documentBuilderFactory;
		}

		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		return documentBuilderFactory;
	}

	public static XPathFactory getxPathFactory() {
		if (xPathFactory != null) {
			return xPathFactory;
		}

		xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}

	public static void skipElements(XMLStreamReader reader, Integer... elements) throws XMLStreamException {
		int eventType = reader.getEventType();
		List<Integer> types = Arrays.asList(elements);
		while (types.contains(eventType)) {
			eventType = reader.next();
		}
	}

	public static Document createDocument(InputStream inputStream) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilder builder = getDocumentBuilderFactory().newDocumentBuilder();
		Document document = builder.parse(inputStream);
		return document;
	}

	public static String getTextFromElement(Document document, String expression) throws XPathExpressionException {
		XPath xPath = getxPathFactory().newXPath();
		XPathExpression xPathExpression = xPath.compile(expression);
		String value = (String) xPathExpression.evaluate(document, XPathConstants.STRING);
		value = StringUtils.trimToEmpty(value);
		return value;
	}

}
