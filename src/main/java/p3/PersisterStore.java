package p3;

public abstract class PersisterStore<T extends PersisterStore<T>> {
    abstract void putString(String key, String value);
    abstract void putInt(String key, int value);
    abstract void putLong(String key, long value);
    abstract void putDouble(String key, double value);
    abstract PersisterStore<T> newChild(String name);
    
    /**
     * Stores the given Persister in this storage.
     * 
     * @return {@code this} storage.
     */
    public final T store(Persister persister) {
        persister.storeIn(this);
        return self();
    }
    
    protected abstract T self();
    
}
