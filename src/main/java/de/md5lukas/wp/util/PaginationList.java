package de.md5lukas.wp.util;

import java.util.ArrayList;
import java.util.List;

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