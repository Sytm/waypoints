/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.wp.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class PaginationList<T> extends ArrayList<T> {

	private final int itemsPerPage;

	public PaginationList(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public int pageStart(int page) {
		if (page < 0 || page >= pages()) return -1;
		int start = page * itemsPerPage;
		if (start > size()) return -1;
		return start;
	}

	public int pageEnd(int page) {
		if (page < 0 || page >= pages()) return -1;
		int end = (page * itemsPerPage) + itemsPerPage;
		return Math.min(size(), end);
	}

	public int pages() {
		return (int) Math.ceil((double) size() / (double) itemsPerPage);
	}

	public List<T> page(int page) {
		int start = pageStart(page), end = pageEnd(page);
		if (start == -1 || end == -1) return null;
		return subList(start, end);
	}
}