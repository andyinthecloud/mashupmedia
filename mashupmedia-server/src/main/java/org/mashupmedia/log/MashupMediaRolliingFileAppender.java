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

package org.mashupmedia.log;

import java.io.File;

import org.mashupmedia.util.FileHelper;
import ch.qos.logback.core.rolling.RollingFileAppender;


public class MashupMediaRolliingFileAppender<T> extends RollingFileAppender<T> {

	@Override
	public void setFile(String fileName) {
		File file = new File(FileHelper.getApplicationFolder(), "logs/" + fileName);
		super.setFile(file.getAbsolutePath());
	}

}
