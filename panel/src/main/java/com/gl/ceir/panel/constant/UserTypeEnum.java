package com.gl.ceir.panel.constant;

public enum UserTypeEnum {
	PUBLIC("END_USER"), CUSTOMER_CARE("REGISTERED"), TICKET_SUPPORT("REGISTERED"), ADMIN("admin");

	public String type;

	UserTypeEnum(String type) {
		this.type = type;
	}

	public String type() {
		return this.type;
	}
}
