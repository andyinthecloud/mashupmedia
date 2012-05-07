package org.mashupmedia.editor;

import java.beans.PropertyEditorSupport;

import org.mashupmedia.model.Group;
import org.mashupmedia.service.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupEditor extends PropertyEditorSupport{

	@Autowired
	private AdminManager adminManager;
	
	@Override
	public void setAsText(String idName) throws IllegalArgumentException {
		Group group = adminManager.getGroup(idName);
		setValue(group);
	}
}
