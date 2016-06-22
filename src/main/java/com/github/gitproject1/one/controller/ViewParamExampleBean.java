package com.github.gitproject1.one.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.gitproject1.one.controller.columns.ColumnModel;
import com.github.gitproject1.one.service.DynamicEntityService;

@Component("viewParamExampleBean")
@Scope("session")
public class ViewParamExampleBean implements Serializable{
	
	private static final long serialVersionUID = 7686383676574961864L;
	
	private List<ColumnModel> columns = new ArrayList<ColumnModel>(0);
	private String currentClass = "Issues";

	@Autowired
	private DynamicEntityService dynamicEntityService;
	
	public DynamicEntityService getDynamicEntityService() {
		return dynamicEntityService;
	}

	public void setDynamicEntityService(DynamicEntityService dynamicEntityService) {
		this.dynamicEntityService = dynamicEntityService;
	}

	public void onPreRender() {
		
		columns = new ArrayList<ColumnModel>(0);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", currentClass);

		String hql = "select a from Attribute a where a.entity.name = :name order by a.orderNo";
		List<DynamicEntity> attributes = dynamicEntityService.findEntities(hql, parameters, 0, 50);
		for (DynamicEntity attribute : attributes) {
			ColumnModel columnDescriptor = new ColumnModel();
			
			String name = (String) attribute.get("name");
			Class<?> type = getType(attribute);
			
			columnDescriptor.setProperty(name);
			columnDescriptor.setHeader(StringUtils.capitalize(name));
			columnDescriptor.setType(type );
			columns.add(columnDescriptor);
		}
	}

	private Class<?> getType(DynamicEntity attribute) {
		
		Class<?> ret = String.class;
		
		String dataType = (String) attribute.get("dataType");
		if ("String".equalsIgnoreCase(dataType)){
			ret = String.class;
		} else if ("Boolean".equalsIgnoreCase(dataType)){
			ret = Boolean.class;
		} else if ("Integer".equalsIgnoreCase(dataType)){
			ret = Integer.class;
		} else if ("Double".equalsIgnoreCase(dataType)){
			ret = Double.class;
		} else if ("Date".equalsIgnoreCase(dataType)){
			ret = Date.class;
		}
		
		return ret;
	}

	public List<Object> getData(){
		try {
			List<Object> ret = new ArrayList<Object>();
			List<DynamicEntity> data = dynamicEntityService.findAll(currentClass, 0, 50);
			for (DynamicEntity obj : data) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (ColumnModel column : columns) {
					String key = column.getProperty();
					Object value = obj.get(key);
					map.put(key, value);
				}
				ret.add(map);
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}
	
	public String getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}
}
