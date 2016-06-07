package com.github.gitproject1.one.controller;

import java.io.Serializable;
import java.util.List;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.gitproject1.one.service.DynamicEntityService;

@Component("indexBean")
@Scope("session")
public class IndexBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4657044539385787585L;

	@Autowired
	private DynamicEntityService dynamicEntityService;

    private MenuModel model; 

	public MenuModel getModel() {
		return model;
	}

	public void setModel(MenuModel model) {
		this.model = model;
	}

	public DynamicEntityService getDynamicEntityService() {
		return dynamicEntityService;
	}

	public void setDynamicEntityService(DynamicEntityService dynamicEntityService) {
		this.dynamicEntityService = dynamicEntityService;
	}

	public void onPreRender() {
       model = new DefaultMenuModel(); 
        
        List<DynamicEntity> entities = dynamicEntityService.findAll("Entity");
        for (DynamicEntity dynamicEntity : entities) {
			
		}
        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("Dynamic Submenu");
 
        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setUrl("ui/param?faces-redirect=true&amp;class=Issues");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);
 
        model.addElement(firstSubmenu);
 
        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Dynamic Actions");
 
        item = new DefaultMenuItem("Save");
        item.setIcon("ui-icon-disk");
        item.setCommand("#{indexBean.save}");
        secondSubmenu.addElement(item);
 
        item = new DefaultMenuItem("Delete");
        item.setIcon("ui-icon-close");
        item.setCommand("#{indexBean.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);
 
        item = new DefaultMenuItem("Redirect");
        item.setIcon("ui-icon-search");
        item.setCommand("#{indexBean.redirect}");
        secondSubmenu.addElement(item);
 
        model.addElement(secondSubmenu);
    	
    } 
	
}
