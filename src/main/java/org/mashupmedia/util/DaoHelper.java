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

package org.mashupmedia.util;

import java.util.List;

public class DaoHelper {

	public static String convertToHqlParameters(List<? extends Number> numbers) {
		StringBuilder hqlBuilder = new StringBuilder();

		for (Number number : numbers) {
			if (hqlBuilder.length() > 0) {
				hqlBuilder.append(",");
			}
			hqlBuilder.append(number);
		}

		return hqlBuilder.toString();
	}

	/**
	 * Assumes that the hql object Song s has already been declared
	 * 
	 * @param queryBuilder
	 * @param groupIds
	 */
	public static void appendGroupFilter(StringBuilder queryBuilder, List<? extends Number> groupIds) {
		if (groupIds == null || groupIds.isEmpty()) {
			return;
		}

		String keyword = "where";
		if (queryBuilder.toString().toLowerCase().matches("\\bwhere\\s.*")) {
			keyword = "and";
		}

		String groupHqlText = convertToHqlParameters(groupIds);
		queryBuilder.append(" " + keyword + " s.library.groups.id in (" + groupHqlText + ")");
	}

}
