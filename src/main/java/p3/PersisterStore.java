package p3;

public abstract class PersisterStore {
    public abstract void putString(String key, String value);
    public abstract void putInt(String key, int value);
    public abstract void putLong(String key, long value);
    public abstract PersisterStore newChild(String name);
    
    public final void store(Persister persister) {
        persister.storeIn(this);
    }
    
}
