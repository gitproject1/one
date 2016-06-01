package com.github.gitproject1.one.controller;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.gitproject1.one.controller.columns.ColumnModel;
import com.github.gitproject1.one.controller.detail.DynaFormModelBuilder;
import com.github.gitproject1.one.controller.detail.FormControlBuilder;
import com.github.gitproject1.one.controller.model.BaseDynamicEntity;
import com.github.gitproject1.one.service.DynamicEntityService;

@Component("basicDetailExampleBean")
@Scope("session")
public class BasicDetailExampleBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private BaseDynamicEntity model;
	private Integer id;
	private Boolean disabled = false;
	private DynaFormModel formModel;
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
	
	public final void onPreRender(){
		try {
			
			columns = new ArrayList<ColumnModel>(0); 
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("name", currentClass);

			List<DynamicEntity> attributes = dynamicEntityService.findEntities("select a from Attribute a where a.entity.name = :name order by a.orderNo", parameters, 0, 50);
			for (DynamicEntity attribute : attributes) {
				ColumnModel columnDescriptor = new ColumnModel();
				
				String name = (String) attribute.get("name");
				Class<?> type = getType(attribute);
				
				columnDescriptor.setProperty(name);
				columnDescriptor.setHeader(StringUtils.capitalize(name));
				columnDescriptor.setType(type );
				columns.add(columnDescriptor);
			}
			
			this.formModel = new DynaFormModelBuilder(currentClass, dynamicEntityService).build();
			if(id != null && id.intValue() > 0) {
				DynamicEntity dynamicEntity = dynamicEntityService.findById(currentClass, id);
				this.model = new BaseDynamicEntity(dynamicEntity, columns);
			}else{
				DynamicEntity newInstance = dynamicEntityService.newInstance(currentClass);
				this.model = new BaseDynamicEntity(newInstance, columns);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	
	protected Comparator<PropertyDescriptor> getPropertyComparator() {
		return DynaFormModelBuilder.DEFAULT_PROPERTY_COMPARATOR;
	}

	protected Map<String, FormControlBuilder> getCustomBuilders() {
		//No Customm
		return new HashMap<String, FormControlBuilder>(0);
	}

	public final String save(){
		try {
			BaseDynamicEntity entity = this.model;
			dynamicEntityService.save(entity.getDynamicEntity());
			return "param.xhtml?class=" + currentClass + "&faces-redirect=true";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public DynaFormModel getFormModel() {
		return formModel;
	}
	
	public BaseDynamicEntity getModel() {
		return model;
	}

	public void setModel(BaseDynamicEntity model) {
		this.model = model;
	}

}
