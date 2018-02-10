package com.hit.processes;

import java.util.List;

/*
 *This class represents the configuration of all threads, it contains list of ProcessCycles that's associated with each thread 
 */

public class RunConfiguration {
	private List<ProcessCycles> processesCycles;

	RunConfiguration(List<ProcessCycles> processesCycles) {
		this.processesCycles = processesCycles;
	}

	public List<ProcessCycles> getProcessesCycles() {
		return processesCycles;
	}

	public void setProcessesCycles(List<ProcessCycles> processesCycles) {
		this.processesCycles = processesCycles;
	}

	@Override
	public String toString() {

		return processesCycles.toString();
	}
}
