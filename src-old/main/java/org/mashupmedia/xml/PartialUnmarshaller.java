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

package org.mashupmedia.xml;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class PartialUnmarshaller<T> {
    XMLStreamReader reader;
    Class<T> clazz;
    Unmarshaller unmarshaller;

    public PartialUnmarshaller(InputStream stream, Class<T> clazz) throws XMLStreamException, FactoryConfigurationError, JAXBException {
        this.clazz = clazz;
        this.unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);

        /* ignore headers */
        skipElements(XMLStreamReader.START_DOCUMENT, XMLStreamReader.DTD);
        /* ignore root element */
        reader.nextTag();
        /* if there's no tag, ignore root element's end */
        skipElements(XMLStreamReader.END_ELEMENT);
    }

    public T next() throws XMLStreamException, JAXBException {
        if (!hasNext())
            throw new NoSuchElementException();

        T value = unmarshaller.unmarshal(reader, clazz).getValue();

        skipElements(XMLStreamReader.CHARACTERS, XMLStreamReader.END_ELEMENT);
        return value;
    }

    public boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    void skipElements(Integer... elements) throws XMLStreamException {
        int eventType = reader.getEventType();

        List<Integer> types = Arrays.asList(elements);
        while (types.contains(eventType)) {
            eventType = reader.next();
        }
    }
}
