/*
 *  This file is part of MashupMedia.
 *
 *  MashupMedia is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MashupMedia is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MashupMedia.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mashupmedia.controller.ajax;

import org.mashupmedia.task.EncodeMediaItemTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ajax/encode")
public class AjaxEncodeMediaItemController {

	public static final String MODEL_KEY_IS_SUCCESSFUL = "isSuccessful";

	@Autowired
	private EncodeMediaItemTaskManager encodeMediaItemTaskManager;

	@RequestMapping(value = "/{mediaItemId}", method = RequestMethod.GET)
	public String handleEncodeOgg(@PathVariable Long mediaItemId, Model model) {
		encodeMediaItemTaskManager.encodeMediaItem(mediaItemId);
		model.addAttribute(MODEL_KEY_IS_SUCCESSFUL, true);
		return "ajax/message";
	}

}
