package com.hit.memoryunits;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RAM {

	private int m_InitialCapacity;
	private Map<Long, Page<byte[]>> m_Pages;

	RAM(int initialCapacity) {
		this.m_InitialCapacity = initialCapacity;
		m_Pages = new HashMap<>(m_InitialCapacity);
	}

	public Map<Long, Page<byte[]>> getm_Pages() {
		return m_Pages;
	}

	public void setPages(Map<Long, Page<byte[]>> m_Pages) {
		this.m_Pages = m_Pages;
	}

	public Page<byte[]> getPage(Long pageId) {
		if (m_Pages.containsKey(pageId)) {
			return m_Pages.get(pageId);
		} else {
			return null;
		}
	}

	public void addPage(Page<byte[]> addPage) {
		if (m_Pages.size() < m_InitialCapacity) {
			m_Pages.put(addPage.getM_Id(), addPage);
		}
	}

	public void removePage(Page<byte[]> removePage) {
		m_Pages.remove(removePage.getM_Id());
	}

	@SuppressWarnings("unchecked")
	public Page<byte[]>[] getPages(Long[] pageIds) {

		List<Page<byte[]>> result = new LinkedList<Page<byte[]>>();

		for (int i = 0; i < pageIds.length; i++) {
			if (m_Pages.containsKey(pageIds[i])) {
				result.add(m_Pages.get(pageIds[i]));
			}
		}
		return (Page<byte[]>[]) result.toArray();
	}

	public void addPages(Page<byte[]>[] addPages) {
		for (int i = 0; i < addPages.length; i++) {
			if (m_Pages.size() < m_InitialCapacity) {
				m_Pages.put(addPages[i].getM_Id(), addPages[i]);
			}
		}
	}

	public void removePages(Page<byte[]>[] removePages) {
		for (int i = 0; i < removePages.length; i++) {
			m_Pages.remove(getPage(removePages[i].getM_Id()));
		}
	}

	public int getInitialCapacity() {
		return m_InitialCapacity;
	}

	public int getCurrentRamSize() {
		return m_Pages.size();
	}

	public void setInitialCapacity(int m_InitialCapacity) {
		this.m_InitialCapacity = m_InitialCapacity;
	}
}
