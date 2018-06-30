package p3;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

public final class Persister {
    private final Map<String, String> stringVals = new HashMap<>();
    private final Map<String, Integer> intVals = new HashMap<>();
    private final Map<String, Long> longVals = new HashMap<>();
    
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
}
