package com.hit.memoryunits;

import java.io.IOException;
import org.junit.Test;
import com.hit.algorithm.LRUAlgoCacheImpl;
import org.junit.Assert;
public class MemoryManagementUnitTest {
	@Test
	public void test() throws ClassNotFoundException, IOException {

		LRUAlgoCacheImpl<Long, Long> lruTest = new LRUAlgoCacheImpl<>(5);
		MemoryManagementUnit mmuTest = new MemoryManagementUnit(5, lruTest);
		Long[] pagesIdsTest1 = { 1L, 2L, 3L };
		Long[] pagesIdsTest2 = { 2L, 3L, 4L };
		Long[] pagesIdsTest3 = { 4L, 5L, 6L };

		Page<byte[]>[] pagesReturnedRam = null;

		pagesReturnedRam = mmuTest.getPages(pagesIdsTest1);
		for (Integer i = 0; i < 3; i++) {
			Assert.assertEquals(pagesReturnedRam[i].getM_Id(), new Long(i + 1));
		}
		pagesReturnedRam = mmuTest.getPages(pagesIdsTest2);
		for (Integer i = 0; i < 3; i++) {
			Assert.assertEquals(pagesReturnedRam[i].getM_Id(), new Long(i + 2));
		}
		pagesReturnedRam = mmuTest.getPages(pagesIdsTest3);
		for (Integer i = 0; i < 3; i++) {
			Assert.assertEquals(pagesReturnedRam[i].getM_Id(), new Long(i + 4));
		}
	}
}
