package com.hit.processes;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.memoryunits.Page;
import com.hit.util.MMULogger;

/*
 * This class simulates a process in the real operation system environment. 
 * it should run in separate java thread in order to achieve its own completely independent
 */
public class Process implements java.util.concurrent.Callable<Boolean> {

	private int id;
	private MemoryManagementUnit mmu;
	private ProcessCycles processCycles;
	private MMULogger logger = MMULogger.getInstance();

	// This constructor represents a process constructor, which gets 3 configure
	// parameters to simulate real process
	public Process(int id, MemoryManagementUnit mmu, ProcessCycles processCycles) {
		this.id = id;
		this.mmu = mmu;
		this.processCycles = processCycles;
	}

	@Override
	// The process business logic method
	public Boolean call() throws Exception {

		Page<byte[]>[] pagesFromMMU = null;
		List<ProcessCycle> proCycles = processCycles.getProcessCycles();

		for (ProcessCycle pc : proCycles) {
			synchronized (mmu) {
				try {
					pagesFromMMU = mmu.getPages(pc.getPages().toArray(new Long[1]));
					for (int i = 0; i < pagesFromMMU.length; i++) {
						pagesFromMMU[i].setM_content(pc.getData().get(i));
						
						logger.write(MessageFormat.format("GP:P{0} {1} {2}{3}{3}", getId(), pagesFromMMU[i].getM_Id().toString(),
								Arrays.toString(pagesFromMMU[i].getM_content()), System.lineSeparator()),
								Level.INFO);
					}
				
					Thread.sleep(pc.getSleepMs());
					
				} catch (InterruptedException e) {
					logger.write(e.getMessage(), Level.SEVERE);
					return false;
				} catch (IOException ie) {
					logger.write(ie.getMessage(), Level.SEVERE);
					return false;
				}
			}
		}

		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
