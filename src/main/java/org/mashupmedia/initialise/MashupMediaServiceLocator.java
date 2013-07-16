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

package org.mashupmedia.initialise;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.util.FileHelper;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MashupMediaServiceLocator {

	public DataSource createDataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.hsqldb.jdbcDriver");
		} catch (PropertyVetoException e) {
			throw new MashupMediaRuntimeException(e.getMessage());
		}
		String applicationFolderPath = FileHelper.getApplicationFolder().getAbsolutePath();
		dataSource.setJdbcUrl("jdbc:hsqldb:file:" + applicationFolderPath + "/db;shutdown=true;hsqldb.write_delay_millis=0;hsqldb.tx=mvcc");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		dataSource.setMinPoolSize(2);
		dataSource.setMaxPoolSize(5);
		dataSource.setMaxIdleTime(600);

		return dataSource;
	}

}
