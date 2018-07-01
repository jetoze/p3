package p3;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tzeth.exhume.dom.XmlPrettyPrint;

public final class XmlPersisterStore extends PersisterStore {
    // TODO: Key names may not be valid XML names. One option could be to enforce something 
    // like key names having to be valid java identifiers in the Persister class itself. 
    // Another option would be to implement some kind of escape mechanism, and (if necessary) 
    // include a translation table in the XML representation.

    // The name of the persister elements.
    private static final String NODE_NAME = "node";
    private static final String NAME_ATTR = "name";
    private static final String STRING_VALS = "strings";
    private static final String INT_VALS = "ints";
    private static final String LONG_VALS = "longs";
    private static final String DOUBLE_VALS = "doubles";
    
    private final Document document;
    private final Element element;
    private Element stringVals;
    private Element intVals;
    private Element longVals;
    private Element doubleVals;

    private XmlPersisterStore(Document doc, String name, @Nullable Element parent) {
        this.document = requireNonNull(doc);
        requireNonNull(name);
        this.element = createElement(NODE_NAME, parent);
        this.element.setAttribute(NAME_ATTR, name);
    }
    
    private Element createElement(String name, @Nullable Element parent) {
        Element e = document.createElement(name);
        if (parent != null) {
            parent.appendChild(e);
        }
        return e;
    }

    public static XmlPersisterStore newInstance() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            return new XmlPersisterStore(doc, "_root_", null);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void putString(String key, String value) {
        requireNonNull(key);
        requireNonNull(value);
        if (stringVals == null) {
            stringVals = createElement(STRING_VALS, element);
        }
        Element valueElement = createElement(key, stringVals);
        valueElement.setTextContent(value);
    }

    @Override
    void putInt(String key, int value) {
        requireNonNull(key);
        if (intVals == null) {
            intVals = createElement(INT_VALS, element);
        }
        Element valueElement = createElement(key, intVals);
        valueElement.setTextContent(String.valueOf(value));
    }

    @Override
    void putLong(String key, long value) {
        requireNonNull(key);
        if (longVals == null) {
            longVals = createElement(LONG_VALS, element);
        }
        Element valueElement = createElement(key, longVals);
        valueElement.setTextContent(String.valueOf(value));
    }

    @Override
    void putDouble(String key, double value) {
        requireNonNull(key);
        if (doubleVals == null) {
            doubleVals = createElement(DOUBLE_VALS, element);
        }
        Element valueElement = createElement(key, doubleVals);
        valueElement.setTextContent(String.valueOf(value));
    }

    @Override
    PersisterStore newChild(String name) {
        requireNonNull(name);
        return new XmlPersisterStore(this.document, name, this.element);
    }
    
    /**
     * Returns a string representation of the XML.
     */
    public String getXml() {
        return prettyPrinter().toString(this.document);
    }
    
    /**
     * Writes the XML to a file.
     */
    public void writeTo(File file) throws IOException {
        requireNonNull(file);
        prettyPrinter().write(this.document, file);
    }
    
    private static XmlPrettyPrint prettyPrinter() {
        return XmlPrettyPrint.withIndent(2);
    }
}
