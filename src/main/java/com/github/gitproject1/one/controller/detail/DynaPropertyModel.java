package com.github.rreinert.project1.controller.detail;

import java.io.Serializable;

public class DynaPropertyModel implements Serializable{
    
	private static final long serialVersionUID = 1L;
	
	private String name;
    
    public DynaPropertyModel() {}
    
    public DynaPropertyModel(String name) {
		super();
		this.name = name;
	}
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
