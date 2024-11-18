package com.gl.ceir.panel.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeautreMenuDto implements Serializable{
	private static final long serialVersionUID = -3464006604944378156L;
	private String name;
	private String icon;
	private String link;
	private String key;
	private int displayOrder;
}
