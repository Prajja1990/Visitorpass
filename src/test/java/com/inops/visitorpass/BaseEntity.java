package com.inops.visitorpass;

public class BaseEntity<T> {
	
	private T value;

	public BaseEntity(T value) {
		super();
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	
}
