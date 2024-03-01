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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

public class DaoHelper {

	public static int MAX_CHARACTER_LENGTH = 65535;

	public static String convertToHqlParameters(Collection<? extends Number> numbers) {
		StringBuilder hqlBuilder = new StringBuilder();

		for (Number number : numbers) {
			if (hqlBuilder.length() > 0) {
				hqlBuilder.append(",");
			}
			hqlBuilder.append(number);
		}

		return hqlBuilder.toString();
	}

	// /**
	//  * Assumes that the hql object Track has already been declared
	//  * 
	//  * @param queryBuilder
	//  * @param groupIds
	//  */
	// public static void appendGroupFilter(StringBuilder queryBuilder, Collection<? extends Number> groupIds) {
	// 	if (groupIds == null || groupIds.isEmpty()) {
	// 		List<Integer> emptyGroupIds = new ArrayList<Integer>();
	// 		emptyGroupIds.add(-1);
	// 		groupIds = new ArrayList<Number>(emptyGroupIds);
	// 	}

	// 	int whereIndex = queryBuilder.toString().toLowerCase().indexOf(" where");
	// 	String keyword = "and";
	// 	if (whereIndex < 0) {
	// 		keyword = "where";
	// 	}

	// 	String groupHqlText = convertToHqlParameters(groupIds);
	// 	queryBuilder.append(" " + keyword + " g.id in (" + groupHqlText + ")");
	// }

	/**
	 * Assumes that the hql object Track has already been declared
	 * 
	 * @param queryBuilder
	 * @param userId
	 */
	public static void appendUserIdFilter(StringBuilder queryBuilder, Long userId) {

		Assert.notNull(userId, "Expecting a userId");

		int whereIndex = queryBuilder.toString().toLowerCase().indexOf(" where");
		String keyword = "and";
		if (whereIndex < 0) {
			keyword = "where";
		}

		// String groupHqlText = convertToHqlParameters(groupIds);

		queryBuilder.append(" " + keyword + " ");
		queryBuilder.append(" ( ");
		queryBuilder.append(String.format(" l.user.id = %d ", userId));
		queryBuilder.append(" or");
		queryBuilder.append(String.format(" u.id = %d", userId) );
		queryBuilder.append(" ) ");
	}

	public static <T> List<T> removeDuplicateItems(List<T> items) {
		return items.stream()
				.distinct()
				.collect(Collectors.toList());
	}

}
