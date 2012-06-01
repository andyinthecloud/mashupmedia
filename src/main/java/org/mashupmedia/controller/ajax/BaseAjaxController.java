package org.mashupmedia.controller.ajax;

import org.mashupmedia.constants.MashUpMediaConstants;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseAjaxController {

	@ModelAttribute(MashUpMediaConstants.MODEL_KEY_THEME_PATH)
	public String getThemePath() {
		return "/themes/default";
	}

}
