package org.mashupmedia.editor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.Group;
import org.mashupmedia.service.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupEditor extends PropertyEditorSupport {

	@Autowired
	private AdminManager adminManager;

	@Override
	public void setAsText(String idValue) throws IllegalArgumentException {
		long groupId = NumberUtils.toLong(idValue);
		if (groupId == 0) {
			return;
		}

		Group group = adminManager.getGroup(groupId);
		setValue(group);
	}

	@Override
	public String getAsText() {
		if (getSource() == null) {
			return "";
		}

		Group group = (Group) getSource();
		String value = String.valueOf(group.getId());
		return value;
	}
}
