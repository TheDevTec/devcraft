package net.minestom.server.fastutil;

import java.util.function.IntFunction;

@FunctionalInterface
public interface Int2ObjectFunction<V> extends Function<Integer, V>, IntFunction<V> {
  default V apply(int operand) {
    return get(operand);
  }
  
  default V put(int key, V value) {
    throw new UnsupportedOperationException();
  }
  
  default V remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default V put(Integer key, V value) {
    int k = key.intValue();
    boolean containsKey = containsKey(k);
    V v = put(k, value);
    return containsKey ? v : null;
  }
  
  @Deprecated
  default V get(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    V v = get(k);
    return (v != defaultReturnValue() || containsKey(k)) ? v : null;
  }
  
  @Deprecated
  default V remove(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    return containsKey(k) ? remove(k) : null;
  }
  
  default boolean containsKey(int key) {
    return true;
  }
  
  @Deprecated
  default boolean containsKey(Object key) {
    return (key == null) ? false : containsKey(((Integer)key).intValue());
  }
  
  default void defaultReturnValue(V rv) {
    throw new UnsupportedOperationException();
  }
  
  default V defaultReturnValue() {
    return null;
  }
  
  default Int2ObjectFunction<V> composeInt(Int2IntFunction before) {
    return k -> get(before.get(k));
  }
  
  V get(int paramInt);
}
