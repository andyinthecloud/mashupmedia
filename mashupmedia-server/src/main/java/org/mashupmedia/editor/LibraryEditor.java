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

package org.mashupmedia.editor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.service.LibraryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LibraryEditor extends PropertyEditorSupport {

	@Autowired
	private LibraryManager libraryManager;

	@Override
	public void setAsText(String idValue) throws IllegalArgumentException {
		long libraryId = NumberUtils.toLong(idValue);
		if (libraryId == 0) {
			return;
		}

		Library library = libraryManager.getLibrary(libraryId);
		setValue(library);
	}

	@Override
	public String getAsText() {
		if (getSource() == null) {
			return "";
		}

		Library library = (Library) getSource();
		String value = String.valueOf(library.getId());
		return value;
	}
}
