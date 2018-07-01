package p3;

public abstract class PersisterStore {
    abstract void putString(String key, String value);
    abstract void putInt(String key, int value);
    abstract void putLong(String key, long value);
    abstract void putDouble(String key, double value);
    abstract PersisterStore newChild(String name);
    
    public final void store(Persister persister) {
        persister.storeIn(this);
    }
    
}
