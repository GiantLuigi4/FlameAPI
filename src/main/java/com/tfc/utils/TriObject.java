package com.tfc.utils;

public class TriObject<V, T, C> {
	private final V obj1;
	private final T obj2;
	private final C obj3;
	
	public TriObject(V obj1, T obj2, C obj3) {
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.obj3 = obj3;
	}
	
	public V getObj1() {
		return obj1;
	}
	
	public T getObj2() {
		return obj2;
	}
	
	public C getObj3() {
		return obj3;
	}
}
