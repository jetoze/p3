package p3;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

public final class Persister {
    private final Map<String, String> stringVals = new HashMap<>();
    private final Map<String, Integer> intVals = new HashMap<>();
    private final Map<String, Long> longVals = new HashMap<>();
    private final ListMultimap<String, Persister> children = ArrayListMultimap.create();
    
    public void putString(String key, String value) {
        stringVals.put(requireNonNull(key), requireNonNull(value));
    }
    
    public Optional<String> checkString(String key) {
        requireNonNull(key);
        return Optional.ofNullable(stringVals.get(key));
    }
   
    public String getString(String key) {
        return checkString(key).orElseThrow(() -> new IllegalArgumentException("No such string: " + key));
    }
    
    public String getString(String key, String defVal) {
        return checkString(key).orElse(defVal);
    }
    
    public void putInt(String key, int value) {
        intVals.put(requireNonNull(key), value);
    }
    
    public OptionalInt checkInt(String key) {
        requireNonNull(key);
        Integer val = intVals.get(key);
        return (val == null)
                ? OptionalInt.empty()
                : OptionalInt.of(val.intValue());
    }
    
    public int getInt(String key) {
        return checkInt(key).orElseThrow(() -> new IllegalArgumentException("No such int: " + key));
    }
    
    public int getInt(String key, int defVal) {
        return checkInt(key).orElse(defVal);
    }
    
    public void putLong(String key, long value) {
        longVals.put(requireNonNull(key), value);
    }
    
    public OptionalLong checkLong(String key) {
        requireNonNull(key);
        Long val = longVals.get(key);
        return (val == null)
                ? OptionalLong.empty()
                : OptionalLong.of(val.longValue());
    }
    
    public long getLong(String key) {
        return checkLong(key).orElseThrow(() -> new IllegalArgumentException("No such long: " + key));
    }
    
    public long getLong(String key, long defVal) {
        return checkLong(key).orElse(defVal);
    }
    
    public Persister newChild(String name) {
        requireNonNull(name);
        Persister child = new Persister();
        children.put(name, child);
        return child;
    }
    
    public ImmutableList<Persister> getChildren(String name) {
        requireNonNull(name);
        return ImmutableList.copyOf(children.get(name));
    }
    
    public Persister getChild(String name) {
        requireNonNull(name);
        List<Persister> list = children.get(name);
        checkArgument(list.size() == 1, "Expected 1 child with name %s but found %s", name, list.size());
        return list.get(0);
    }
}
