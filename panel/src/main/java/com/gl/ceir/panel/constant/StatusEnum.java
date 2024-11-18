package com.gl.ceir.panel.constant;

public enum StatusEnum {
	INACTIVE("0"), ACTIVE("1"), SUSPENDED("2"), LOCKED("3"), DELETED("4");

	public String status;

	StatusEnum(String status) {
		this.status = status;
	}

	public String status() {
		return this.status;
	}
}
