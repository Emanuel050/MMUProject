package com.hit.memoryunits;
import java.util.LinkedList;
import java.util.List;




import com.hit.algorithm.IAlgoCache;


public class MemoryManagementUnit {
	
	private RAM m_Ram;
	private IAlgoCache<Long, Long> m_Algo;
	
	public MemoryManagementUnit(int ramCapacity,IAlgoCache<Long,Long> algo)
	{
		m_Ram = new RAM(ramCapacity);
		this.m_Algo = algo;
	}
	
	//This method is the main method which returns array of pages that are requested from the user
	@SuppressWarnings("unchecked")
	
	public Page<byte[]> [] getPages(Long[] pageIds)throws java.io.IOException, ClassNotFoundException
	{
		HardDisk hd = HardDisk.getInstance();
		List<Page<byte[]>> requestedPages = new LinkedList<Page<byte[]>>();	
		Page<byte[]> newPage;
		Long removePageId;
		Page<byte[]> pageToRemoveFromRam;
		Page<byte[]> pageToRam;
		
		for (Long pageId : pageIds) {
			
			//page not exists in ram
			if(m_Algo.getElement(pageId) == null)
			{
				//if RAM not full
				if(m_Ram.getCurrentRamSize() < m_Ram.getInitialCapacity() )
				{
					//page exists in HardDisk
					newPage = hd.pageFault(pageId);
					if(newPage != null)
					{		
						m_Ram.addPage(newPage);	
						m_Algo.putElement(pageId, pageId);	
						requestedPages.add(newPage);
					}		
				}
				//RAM is full
				else
				{
					//get the Id of the page to remove(using IAlgo algorithms to decide which)
					removePageId = m_Algo.putElement(pageId, pageId);
					pageToRemoveFromRam = m_Ram.getPage(removePageId); // get the Page to remove from the ram
					m_Ram.removePage(pageToRemoveFromRam); // remove the page from the RAM
					pageToRam = hd.pageReplacement(pageToRemoveFromRam, pageId); // move the Page to HD(from the RAM), and gets the page to move to the RAM
					m_Ram.addPage(pageToRam); // move the page to the RAM
					requestedPages.add(pageToRam);	
				}
			}
			else{
				requestedPages.add(m_Ram.getPage(pageId));		
			}
		}
		return requestedPages.toArray((Page<byte[]>[]) new Page[requestedPages.size()]);
	}	
}
