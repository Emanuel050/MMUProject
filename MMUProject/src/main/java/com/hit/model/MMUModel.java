package com.hit.model;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.hit.algorithm.IAlgoCache;
import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.algorithm.NFUAlgoCacheImpl;
import com.hit.algorithm.RandomAlgoCacheImpl;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.Process;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;

public class MMUModel extends Observable implements Model {

	private int numProcesses = 0;
	private int ramCapacity = 0;
	private static MMULogger logger ;
	private static String[] command;
	private List<String> logData;

	public MMUModel() {
		super();
	}

	public static void runProcesses(List<Process> applications) {
		ExecutorService executor = Executors.newCachedThreadPool();

		for (Process process : applications) {
			executor.submit(process);
		}
		executor.shutdown();

		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.write(e.getMessage(), Level.SEVERE);
			logger.close();
		}
	}

	public static List<Process> createProcesses(List<ProcessCycles> appliocationsScenarios, MemoryManagementUnit mmu) {
		List<Process> processes = new ArrayList<>();
		ProcessCycles currentProcessCycles;

		for (int i = 0; i < appliocationsScenarios.size(); i++) {
			currentProcessCycles = appliocationsScenarios.get(i);
			processes.add(new Process(i+1, mmu, currentProcessCycles));
		}

		return processes;
	}

	public static RunConfiguration readConfigurationFile() {
		RunConfiguration runConfiguration = null;
		FileReader configurationFile = null;

		try {
			configurationFile = new FileReader("src/main/resources/com/hit/config/Configuration.json");
			Gson g = new Gson();
			runConfiguration = g.fromJson(configurationFile, RunConfiguration.class);
		} catch (FileNotFoundException e) {
			logger.write("FileNotFoundException:" + e.getMessage(), Level.SEVERE);
		} catch (JsonIOException e) {
			logger.write("JsonIOException:" + e.getMessage(), Level.SEVERE);
		} catch (JsonSyntaxException e) {
			logger.write("JsonSyntaxException:" + e.getMessage(), Level.SEVERE);
		}
		return runConfiguration;
	}

	public void setConfiguratio(List<String> configuration) {
		command = configuration.toArray(new String[configuration.size()]);
	}

	public int getNumProcesses() {
		return numProcesses;
	}

	public void setNumProcesses(int numProcesses) {
		this.numProcesses = numProcesses;
	}

	public int getRamCapacity() {
		return ramCapacity;
	}

	public void setRamCapacity(int ramCapacity) {
		this.ramCapacity = ramCapacity;
	}

	public void start() {
		IAlgoCache<Long, Long> algo = null;
		MMULogger.restartLogger();
		logger = MMULogger.getInstance();
		
		this.ramCapacity = Integer.parseInt(command[1]);

		switch (command[0]) {
		case "LRU":
			algo = new LRUAlgoCacheImpl<>(ramCapacity);
			break;
		case "NFU":
			algo = new NFUAlgoCacheImpl<>(ramCapacity);
			break;

		case "RANDOM":
			algo = new RandomAlgoCacheImpl<>(ramCapacity);
			break;
		}

		logger.write(MessageFormat.format("RC:{0}{1}", ramCapacity, System.lineSeparator()), Level.INFO);
		MemoryManagementUnit mmu = new MemoryManagementUnit(ramCapacity, algo);
		RunConfiguration runConfig = readConfigurationFile();
		List<ProcessCycles> processCycles = runConfig.getProcessesCycles();
		List<Process> processes = createProcesses(processCycles, mmu);
		numProcesses = processes.size();
		logger.write(MessageFormat.format("PN:{0}{1}{1}", numProcesses, System.lineSeparator()), Level.INFO);
		runProcesses(processes);
		System.out.println("Done.");
		readLogData();
		setChanged();
		notifyObservers();
		clearChanged();
		logger.close();	
	}
	
	public void readLogData() {
		
		logData = new ArrayList<>();
		try {
			this.logData = Files.readAllLines(Paths.get(MMULogger.DEFAULT_FILE_NAME));
		} catch (IOException e) {
			logger.write("IOException:"+e.getMessage(), Level.SEVERE);
			logger.close();
		}
	}

	public List<String> getLogData() {
		return logData;
	}

	public void setLogData(List<String> logData) {
		this.logData = logData;
	}
}
