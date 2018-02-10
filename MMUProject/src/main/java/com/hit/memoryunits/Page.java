package com.hit.memoryunits;

public class Page<T> implements java.io.Serializable {
	private Long m_Id;
	private T m_content;
	
	Page(Long id, T content)
	{
		this.m_Id = id;
		this.m_content = content;
	}

	public Long getM_Id() {
		return m_Id;
	}

	public void setM_Id(Long m_Id) {
		this.m_Id = m_Id;
	}


	public void setM_content(T m_content) {
		this.m_content = m_content;
	}
	
	public T getM_content() {
		return m_content;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		
		return "Page[pageId=" + m_Id + ",content =" + m_content + "]";
	}
	
	@Override
	public int hashCode() {
		return m_Id.intValue();
	}
}
