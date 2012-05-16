package org.mashupmedia.controller;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.util.MessageHelper;
import org.mashupmedia.web.Breadcrumb;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	protected Breadcrumb getHomeBreadcrumb() {		
		Breadcrumb breadcrumb = new Breadcrumb(MessageHelper.getMessage("breadcrumb.home"), "/app/home");
		return breadcrumb;
	}
	
	@ModelAttribute("themePath")
	public String getThemePath() {
		return "/themes/default";
	}
	
	@ModelAttribute("breadcrumbs")
	public List<Breadcrumb> populateBreadcrumbs() {
		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		breadcrumbs.add(getHomeBreadcrumb());
		prepareBreadcrumbs(breadcrumbs);
		return breadcrumbs;
	}
	
	public abstract void prepareBreadcrumbs(List<Breadcrumb> breadcrumbs);
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(" \t\r\n\f", true));
    }
	
}
