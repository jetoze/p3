package p3;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

public interface Persister {
    void putString(String key, String value);
    Optional<String> checkString(String key);
    default String getString(String key) {
        return checkString(key).orElseThrow(() -> new IllegalArgumentException("No such string: " + key));
    }
    default String getString(String key, String defVal) {
        return checkString(key).orElse(defVal);
    }
    
    void putInt(String key, int value);
    OptionalInt checkInt(String key);
    default int getInt(String key) {
        return checkInt(key).orElseThrow(() -> new IllegalArgumentException("No such int: " + key));
    }
    default int getInt(String key, int defVal) {
        return checkInt(key).orElse(defVal);
    }
    
    void putLong(String key, long value);
    OptionalLong checkLong(String key);
    default long getLong(String key) {
        return checkLong(key).orElseThrow(() -> new IllegalArgumentException("No such long: " + key));
    }
    default long getLong(String key, long defVal) {
        return checkLong(key).orElse(defVal);
    }
}
