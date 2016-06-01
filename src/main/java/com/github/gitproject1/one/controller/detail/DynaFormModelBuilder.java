package com.github.gitproject1.one.controller.detail;

import java.beans.PropertyDescriptor;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;

import com.github.gitproject1.one.service.DynamicEntityService;

public class DynaFormModelBuilder {
	
	private String modelClass;
	private DynamicEntityService dynamicEntityService;
	
	public static Comparator<PropertyDescriptor> DEFAULT_PROPERTY_COMPARATOR = new Comparator<PropertyDescriptor>() {
		public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	public DynaFormModelBuilder(String modelClass, DynamicEntityService dynamicEntityService ) {
		this.modelClass = modelClass;
		this.dynamicEntityService = dynamicEntityService;
	}
	
	public DynaFormModel build(){
		DynaFormModel formModel = new DynaFormModel();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", modelClass);

		List<DynamicEntity> attributes = dynamicEntityService.findEntities("select a from Attribute a where a.entity.name = :name order by a.orderNo", parameters, 0, 50);
		for (DynamicEntity attribute : attributes) {
			String name = (String) attribute.get("name");
			String dataType = (String) attribute.get("dataType");
			if (dataType.equalsIgnoreCase("date")){
				continue; //TODO
			}
			
			DynaFormRow row = formModel.createRegularRow();

			DynaFormLabel label = row.addLabel(name);
	        DynaFormControl input = row.addControl(new DynaPropertyModel(name), dataType.toLowerCase());  
	        label.setForControl(input);
		}
		
		return formModel;
	}
	
	public DynamicEntityService getDynamicEntityService() {
		return dynamicEntityService;
	}

	public void setDynamicEntityService(DynamicEntityService dynamicEntityService) {
		this.dynamicEntityService = dynamicEntityService;
	}

}
