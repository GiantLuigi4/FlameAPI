package com.tfc.utils;

import java.util.HashMap;
import java.util.Map;

public class TriHashMap<V, K, T> extends HashMap<V, HashMap<K, T>> {
	public TriHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	public TriHashMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	public TriHashMap() {
	}
	
	public TriHashMap(Map<? extends V, ? extends HashMap<K, T>> m) {
		super(m);
	}
	
	public T add(V key1, K key2, T value) {
		HashMap<K, T> map = this.getOrDefault(key1, new HashMap<>());
		if (!map.containsKey(key2)) map.put(key2, value);
		else map.replace(key2, value);
		if (!this.containsKey(key1)) this.put(key1, map);
		return value;
	}
	
	public T get(V key1, K key2) {
		HashMap<K, T> map = this.getOrDefault(key1, new HashMap<>());
		return map.get(key2);
	}
	
	public boolean contains(V key1, K key2) {
		HashMap<K, T> map = this.getOrDefault(key1, new HashMap<>());
		return map.containsKey(key2);
	}
}
