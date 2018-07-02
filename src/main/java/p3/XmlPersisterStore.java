package p3;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import tzeth.exhume.dom.DomParsers;
import tzeth.exhume.dom.Elements;
import tzeth.exhume.dom.XmlPrettyPrint;

public final class XmlPersisterStore extends PersisterStore<XmlPersisterStore> {
    // TODO: Key names may not be valid XML names. One option could be to enforce something 
    // like key names having to be valid java identifiers in the Persister class itself. 
    // Another option would be to implement some kind of escape mechanism, and (if necessary) 
    // include a translation table in the XML representation.

    private static final String PERSISTER_ELEMENT = "node";
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

    private XmlPersisterStore(Document doc) {
        this.document = requireNonNull(doc);
        this.element = doc.getDocumentElement();
        checkArgument(element.getNodeName().equals(PERSISTER_ELEMENT), "Expected root element name %s but got %s", PERSISTER_ELEMENT, element.getNodeName());
    }
    
    private XmlPersisterStore(Document doc, String name, @Nullable Element parent) {
        this.document = requireNonNull(doc);
        requireNonNull(name);
        this.element = createElement(PERSISTER_ELEMENT, parent);
        this.element.setAttribute(NAME_ATTR, name);
    }
    
    private Element createElement(String name, @Nullable Element parent) {
        Element e = document.createElement(name);
        if (parent != null) {
            parent.appendChild(e);
        } else {
            document.appendChild(e);
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
    
    public static Persister load(Document doc) {
        XmlPersisterStore store = new XmlPersisterStore(doc);
        return store.toPersister();
    }
    
    public static Persister load(File file) throws SAXException, IOException {
        return load(DomParsers.parseFile(file));
    }
    
    public static Persister load(InputStream in) throws SAXException, IOException {
        return load(DomParsers.parseStream(in));
    }
    
    public static Persister load(String content) throws SAXException {
        return load(DomParsers.parseXml(content));
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
    XmlPersisterStore newChild(String name) {
        requireNonNull(name);
        return new XmlPersisterStore(this.document, name, this.element);
    }
    
    @Override
    protected XmlPersisterStore self() {
        return this;
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
    
    @VisibleForTesting
    Persister toPersister() {
        Persister p = new Persister();
        loadPersister(p, this.element);
        return p;
    }

    private static void loadPersister(Persister p, Element parent) {
        for (Element e : Elements.under(parent)) {
            switch (e.getNodeName()) {
            case STRING_VALS:
                loadStrings(e, p);
                break;
            case INT_VALS:
                loadInts(e, p);
                break;
            case LONG_VALS:
                loadLongs(e, p);
                break;
            case DOUBLE_VALS:
                loadDoubles(e, p);
                break;
            case PERSISTER_ELEMENT:
                loadChild(e, p);
                break;
            default:
                throw new RuntimeException("Unexpected element encountered: " + e.getNodeName());
            }
        }
    }
    
    private static void loadStrings(Element stringVals, Persister p) {
        for (Element e : Elements.under(stringVals)) {
            String key = e.getNodeName();
            String value = e.getTextContent().trim();
            p.putString(key, value);
        }
    }
    
    private static void loadInts(Element intVals, Persister p) {
        for (Element e : Elements.under(intVals)) {
            String key = e.getNodeName();
            int value = Integer.parseInt(e.getTextContent().trim());
            p.putInt(key, value);
        }
    }
    
    private static void loadLongs(Element longVals, Persister p) {
        for (Element e : Elements.under(longVals)) {
            String key = e.getNodeName();
            long value = Long.parseLong(e.getTextContent().trim());
            p.putLong(key, value);
        }
    }
    
    private static void loadDoubles(Element doubleVals, Persister p) {
        for (Element e : Elements.under(doubleVals)) {
            String key = e.getNodeName();
            double value = Double.parseDouble(e.getTextContent().trim());
            p.putDouble(key, value);
        }
    }
    
    private static void loadChild(Element e, Persister p) {
        String name = e.getAttribute(NAME_ATTR);
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Invalid XML: <" + PERSISTER_ELEMENT + "> element without " + NAME_ATTR + " attribute.");
        }
        Persister child = p.newChild(name);
        loadPersister(child, e);
    }
}
