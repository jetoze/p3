package p3;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public final class PersisterTest {
    private Persister p;
    
    @Before
    public void setup() {
        p = new Persister();
    }
    
    @Test
    public void putAndGetInt() {
        p.putInt("x", 42);
        assertEquals(42, p.getInt("x"));
    }
    
    @Test
    public void defaultValueIgnoreWhenIntValueIsPresent() {
        p.putInt("x", 42);
        assertEquals(42, p.getInt("x", 99));
    }
    
    @Test
    public void absentInt() {
        assertFalse(p.checkInt("x").isPresent());
    }
    
    @Test
    public void defaultInt() {
        assertEquals(42, p.getInt("x", 42));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void missingInt() {
        p.getInt("x");
    }
    
    @Test
    public void overwriteIntKey() {
        p.putInt("x", 1);
        p.putInt("x", 2);
        assertEquals(2, p.getInt("x"));
    }
    
    @Test(expected = NullPointerException.class)
    public void nullIntKeyIsRejected() {
        p.putInt(null, 42);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullStringKeyIsRejected() {
        p.putString(null, "hello");
    }
    
    @Test(expected = NullPointerException.class)
    public void nullStringValueIsRejected() {
        p.putString("x", null);
    }
    
    @Test
    public void keysForDifferentValueTypesDoNotOverwriteEachOther() {
        p.putInt("x", 42);
        p.putString("x", "xylophone");
        p.putLong("x", 1234L);
        p.putDouble("x", 0.25);
        
        assertEquals(42, p.getInt("x"));
        assertEquals("xylophone", p.getString("x"));
        assertEquals(1234L, p.getLong("x"));
        assertEquals(0.25, p.getDouble("x"), 0.0000001);
    }
    
    @Test
    public void emptyChildren() {
        assertTrue(p.getChildren("child").isEmpty());
    }
    
    @Test
    public void listOfChildren() {
        Persister c1 = p.newChild("child");
        Persister c2 = p.newChild("child");
        assertEquals(ImmutableList.of(c1, c2), p.getChildren("child"));
    }
    
    @Test
    public void singleChild() {
        Persister child = p.newChild("child");
        assertSame(child, p.getChild("child"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void askingForSingleChildFailsWhenNoChildren() {
        p.getChild("child");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void askingForSingleChildFailsWhenMultipleChildren() {
        p.newChild("child");
        p.newChild("child");
        p.getChild("child");
    }
    
    @Test
    public void store() {
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
        
        Persister backer = new Persister();
        MockStore store = new MockStore(backer);
        store.store(p);
        
        assertEquals(p, backer);
    }
    
    
    private static class MockStore extends PersisterStore {
        private final Persister backer;
        
        public MockStore(Persister backer) {
            this.backer = backer;
        }

        @Override
        void putString(String key, String value) {
            backer.putString(key, value);
        }

        @Override
        void putInt(String key, int value) {
            backer.putInt(key, value);
        }

        @Override
        void putLong(String key, long value) {
            backer.putLong(key, value);
        }

        @Override
        void putDouble(String key, double value) {
            backer.putDouble(key, value);
        }

        @Override
        PersisterStore newChild(String name) {
            return new MockStore(backer.newChild(name));
        }
    }
}
