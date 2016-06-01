package com.github.rreinert.project1.controller.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;

import com.github.rreinert.project1.controller.columns.ColumnModel;

public class BaseDynamicEntity implements DynamicEntity, Map<String, Object>{
	
	private DynamicEntity dynamicEntity;
	private List<ColumnModel> columns;
	public DynamicEntity getDynamicEntity() {
		return dynamicEntity;
	}

	public void setDynamicEntity(DynamicEntity dynamicEntity) {
		this.dynamicEntity = dynamicEntity;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	private Map<String, Object> data;
	
	public BaseDynamicEntity(DynamicEntity dynamicEntity, List<ColumnModel> columns){
		this.dynamicEntity = dynamicEntity;
		this.columns = columns;
		
		data = new HashMap<String, Object>();
		for (ColumnModel column : columns) {
			String key = column.getProperty();
			Object value = get(key);
			data.put(key, value);
		}
	}

	@Override
	public <T> T get(String propertyName) throws DynamicException {
		return dynamicEntity.get(propertyName);
	}

	@Override
	public DynamicEntity set(String propertyName, Object value) throws DynamicException {
		return dynamicEntity.set(propertyName, value);
	}

	@Override
	public boolean isSet(String propertyName) throws DynamicException {
		return dynamicEntity.isSet(propertyName);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return data.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		dynamicEntity.set(key, value);
		return data.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return data.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		data.putAll(m);
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public Set<String> keySet() {
		return data.keySet();
	}

	@Override
	public Collection<Object> values() {
		return data.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return data.entrySet();
	}

}
