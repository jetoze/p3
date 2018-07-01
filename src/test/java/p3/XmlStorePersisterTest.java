package p3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class XmlStorePersisterTest {

    @Test
    public void hierarchicalPersister() throws Exception {
        Persister p = new Persister();
        p.putInt("x", 42);
        p.putInt("y", 84);
        p.putString("x", "xylophone");
        p.putString("z", "zebra");
        p.putLong("now", System.currentTimeMillis());
        p.putDouble("x", 0.25);
        p.putDouble("w", 12345.6789);
        Persister c1 = p.newChild("c");
        c1.putInt("x", 32);
        c1.putString("fruit", "apple");
        Persister c2 = p.newChild("c");
        c2.putInt("x", 16);
        c2.putString("fruit", "banana");
        Persister c21 = c2.newChild("c2");
        c21.putString("color", "yellow");
        p.newChild("empty");
        
        XmlPersisterStore xmlStore = XmlPersisterStore.newInstance();
        xmlStore.store(p);
        String xml = xmlStore.getXml();
        
        Persister restored = XmlPersisterStore.load(xml);
        assertEquals(p, restored);
    }
}
