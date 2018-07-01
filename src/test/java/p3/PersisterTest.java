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
}
