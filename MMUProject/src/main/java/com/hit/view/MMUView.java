package com.hit.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MMUView extends java.util.Observable implements View {

	private Integer pfCount;
	private Integer realPfCount;
	private Integer prCount;
	private Shell shell;
	private Composite tableArea;
	private Composite processSelect;
	private Composite Operations;
	private Table pageTable;
	private Label pageFaultText;
	private Label pageReplacementText;
	private Button playButton;
	private Button playAllButton;
	private org.eclipse.swt.widgets.List processChoose;
	private Button resetButton;
	private Set<String> selectedProcess;
	private Iterator<String> commandsIterator;
	private List<String> commands;
	private TableColumn[] tableCols;
	private TableItem[] tableRows;
	private List<String> prCommands;
	private List<Integer> memoryMap;
	private List<Integer> lastMemoryMap;
	private Stack<State> undoStack;
	private Stack<State> redoStack;
	private static int bytesInPage;
	private static int numMMUPages;
	private static Integer numProcess;

	public MMUView() {
		prCommands = new ArrayList<String>();
		pfCount = 0;
		realPfCount = 0;
		prCount = 0;
		memoryMap = new LinkedList<Integer>();
		lastMemoryMap = new LinkedList<Integer>();

	}

	private void createAndShowGui() {

		final boolean ALLOW_SPAN_HORIZONAL = true;
		final boolean ALLOW_SPAN_VERTICAL = true;

		GridData gridData;
		GridLayout gridLayout;
		Display display = new Display();

		Rectangle screenSize = display.getPrimaryMonitor().getClientArea();

		shell = new Shell(display);
		// color
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		GridLayout layout = new GridLayout(2, false);

		shell.setLayout(layout);
		int width = 630;
		int height = 350;

		shell.setBounds((screenSize.width / 2) - (width / 2), (screenSize.height / 2) - (height / 2), width, height);
		shell.setText("MMU Simulator");

		
		tableArea = new Composite(shell, SWT.NO_FOCUS);

		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int) (width * (8.0 / 11));
		gridData.heightHint = (int) (height * (4.0 / 6));

		tableArea.setLayoutData(gridData);
		tableArea.setLayout(new GridLayout(1, true));

		
		processSelect = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int) (width * (3.0 / 11));
		gridData.heightHint = (int) (height * (4.0 / 6));
		processSelect.setLayoutData(gridData);
		processSelect.setLayout(new GridLayout(1, true));

		
		Operations = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 2, 1);
		gridData.widthHint = width;
		gridData.heightHint = (int) (height * (2.0 / 6));
		Operations.setLayoutData(gridData);
		Operations.setLayout(new GridLayout(2, false));

		// Table area components
		pageTable = new Table(tableArea, SWT.MULTI | SWT.BORDER);
		// color
		pageTable.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		pageTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageTable.setLinesVisible(true);
		pageTable.setHeaderVisible(true);

		setConfiguration();

		// Processes Selection area components
		Label processesLabel = new Label(processSelect, SWT.CENTER);
		processesLabel.setLayoutData(new GridData());
		processesLabel.setText("Processes:");
		processChoose = new org.eclipse.swt.widgets.List(processSelect, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		processChoose.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));

		for (Integer i = 1; i <= numProcess; i++) {
			processChoose.add("Process " + i.toString());
		}

		// Operations area components
		// Play buttons components
		Composite buttons = new Composite(Operations, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		buttons.setLayout(gridLayout);
		buttons.setLayoutData(gridData);
		buttons.setBackground(display.getSystemColor(SWT.COLOR_DARK_RED));

		playButton = new Button(buttons, SWT.PUSH | SWT.CENTER);
		playButton.setText("Play");
		// color
		playButton.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridData.widthHint = 50;
		playButton.setEnabled(false);
		playButton.setLayoutData(gridData);

		playAllButton = new Button(buttons, SWT.PUSH);
		playAllButton.setText("Play All");
		// color
		playAllButton.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		playAllButton.setEnabled(false);
		playAllButton
				.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1));

		resetButton = new Button(buttons, SWT.PUSH | SWT.CENTER);
		resetButton.setText("Reset");
		// color
		resetButton.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		resetButton.setEnabled(false);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridData.widthHint = 50;
		resetButton.setLayoutData(gridData);

		// Statistics data area components
		Composite statistics = new Composite(Operations, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 20;
		gridLayout.verticalSpacing = 15;
		gridLayout.marginTop += 5;
		statistics.setLayout(gridLayout);
		statistics.setLayoutData(gridData);

		Label pageFault = new Label(statistics, SWT.CENTER);
		pageFault.setText("Page Fault Amounts:");
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		pageFault.setLayoutData(gridData);
		pageFaultText = new Label(statistics, SWT.CENTER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridData.widthHint = 50;
		pageFaultText.setLayoutData(gridData);
		pageFaultText.setText(((Integer) pfCount).toString());

		Label pageReplacement = new Label(statistics, SWT.CENTER);
		pageReplacement.setText("Page Replacement Amounts:");
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		pageReplacement.setLayoutData(gridData);
		pageReplacementText = new Label(statistics, SWT.CENTER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
		gridData.widthHint = 50;
		pageReplacementText.setLayoutData(gridData);
		pageReplacementText.setText(((Integer) prCount).toString());

		processChoose.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				selectedProcess = new HashSet<String>();
				String[] processesNumbers = processChoose.getSelection();
				for (int i = 0; i < processesNumbers.length; i++) {
					selectedProcess.add(processesNumbers[i].split(" ")[1]);
				}
				if (selectedProcess.size() > 0) {
					playButton.setEnabled(true);
					playAllButton.setEnabled(true);
				} else {
					playButton.setEnabled(false);
					playAllButton.setEnabled(false);
				}
			}
		});

		// Events for pressing buttons and selecting processes.
		playButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (redoStack.size() > 0) {
					undoStack.push(saveCurrenState());
					// doASavedStateStep(redoStack.pop());
				} else
					oneStep();
				enableResetButton(true);
			}
		});

		playAllButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				while (redoStack.size() > 0) {
					undoStack.push(saveCurrenState());
					// doASavedStateStep(redoStack.pop());
				}
				while (commandsIterator.hasNext()) {
					oneStep();
				}
				enableResetButton(true);
			}
		});

		resetButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				resetAll();
				enableResetButton(false);
				enablePlayButton(true);
			}
		});
		shell.open();
		shell.forceActive();
		shell.forceFocus();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// resetAllCounters
		pfCount = 0;
		realPfCount = 0;
		this.prCount = 0;
		this.bytesInPage = 0;
		this.numMMUPages = 0;
		this.tableCols = null;
		this.memoryMap.clear();
		this.lastMemoryMap.clear();

		display.dispose();
	}

	public void setPreConfiguration(List<String> commands) {
		this.commands = commands;
		commandsIterator = this.commands.iterator();
		String command;
		String subCommand;
		command = commandsIterator.next();
		numMMUPages = Integer.parseInt(command.split("RC:")[1]);
		command = commandsIterator.next();
		numProcess = Integer.parseInt(command.split("PN:")[1]);
		while (commandsIterator.hasNext()) {
			command = commandsIterator.next();
			if (command.startsWith("GP:")) {
				subCommand = command.substring(command.indexOf("["), command.indexOf("]"));
				bytesInPage = (subCommand.split(" ")).length;
				break;
			}
		}
		commandsIterator = this.commands.iterator();
		undoStack = new Stack<State>();
		redoStack = new Stack<State>();
	}

	public void setConfiguration() {

		tableCols = new TableColumn[numMMUPages];
		tableRows = new TableItem[bytesInPage];
		// building page table
		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			for (int i = 0; i < numMMUPages; i++) {
				tableCols[i] = new TableColumn(pageTable, SWT.CENTER);
				tableCols[i].setText("");
				tableCols[i].setWidth(50);
			}

			for (int i = 0; i < bytesInPage; i++) {
				tableRows[i] = new TableItem(pageTable, SWT.CENTER);
			}
		}
	}

	// reset all data
	private void resetAll() {
		pfCount = 0;
		realPfCount = 0;
		prCount = 0;
		prCommands.clear();

		memoryMap.clear();
		lastMemoryMap.clear();
		setPreConfiguration(this.commands);

		updateFieldsStatistics();

		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			for (int i = 0; i < numMMUPages; i++) {
				tableCols[i].setText("");
				for (int j = 0; j < bytesInPage; j++) {
					tableRows[j].setText(i, "");
				}
			}
		}
	}

	private State saveCurrenState() {
		State lastState = new State();

		lastState.setLastMemoryMap(lastMemoryMap);
		lastState.setMemoryMap(memoryMap);
		lastState.setPfAmount(pfCount);
		lastState.setPrAmount(prCount);
		lastState.setRealPfAmount(realPfCount);
		lastState.setPrCommands(prCommands);
		lastState.setTableInfo(tableCols, tableRows);

		return lastState;
	}

	private void oneStep() {
		String curCommand;
		String curProcess;
		String pageId;
		String curCommandFixed;
		String pageToRam = "";
		List<String> pageData = new LinkedList<String>();

		undoStack.push(saveCurrenState());

		while (commandsIterator.hasNext()) {
			curCommand = commandsIterator.next();
			if (curCommand.startsWith("PF:")) {
				pfCount++;
			} else if (curCommand.startsWith("PR:")) {
				prCommands.add(curCommand);
			} else if (curCommand.startsWith("GP:")) {
				if (realPfCount < pfCount) {
					realPfCount++;
				}
				curProcess = curCommand.substring(4, curCommand.length()).split(" ")[0];
				pageId = curCommand.split(" ")[1];
				if (prCommands.size() > 0) {
					String pr = prCommands.get(0);
					String pageToHd = pr.split(" ")[1];
					Integer pageToHdInt = Integer.parseInt(pageToHd);
					pageToRam = pr.split(" ")[3];
					if (pageId.equals(pageToRam)) {

						if (memoryMap.contains(pageToHdInt)) {
							memoryMap.remove(pageToHdInt);
						}

						prCount++;
						prCommands.remove(0);
					}
				} else {
					pageToRam = pageId;
				}
				if (selectedProcess.contains(curProcess)) {
					Integer pageIdInt = Integer.parseInt(pageId);
					// if the page id of the current GP command is not already
					// in table, add it to memory map
					if (!memoryMap.contains(pageIdInt)) {
						memoryMap.add(pageIdInt);
					}
					pageData.add(pageId);
					// Reassemble page id and page data into a list of String
					// with length of BYTES_IN_PAGE +1

					curCommandFixed = curCommand.replaceAll("[,\\[\\]]", "");
					for (int i = 0; i < bytesInPage; i++) {
						pageData.add(curCommandFixed.split(" ")[2 + i]);
					}
					updateTable(pageData);

				} else {
					updateTable(null);
				}
				break;
			} else {
				continue;
			}
		}
		if (!commandsIterator.hasNext()) {
			enablePlayButton(false);
		}
		// update PR and PF labels
		updateFieldsStatistics();
	}

	private void updateTable(List<String> data) {

		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {

			Integer pagePos;

			// Redraw table by new RAM memory map.
			// Removing pages which are removed from memory map and left
			// aligning all the pages that was positioned right to the removed
			// pages.
			if (lastMemoryMap.size() > 0) {
				for (Integer pageId : memoryMap) {
					pagePos = memoryMap.indexOf(pageId);
					if (lastMemoryMap.contains(pageId)) {
						Integer pageLastPos = lastMemoryMap.indexOf(pageId);
						if (pagePos != pageLastPos) {
							tableCols[pagePos].setText(pageId.toString());
							for (int i = 0; i < bytesInPage; i++) {
								tableRows[i].setText(pagePos, tableRows[i].getText(pageLastPos));
							}
						}
					}
				}
				// blanking all columns that should not be in use
				for (int i = memoryMap.size(); i < numMMUPages; i++) {
					tableCols[i].setText("");
					for (int j = 0; j < bytesInPage; j++) {
						tableRows[j].setText(i, "");
					}
				}
			}
			lastMemoryMap.clear();
			lastMemoryMap.addAll(memoryMap); // update last memory map to be the
												// current memory map

			if (data != null) {
				Iterator<String> dataIter = data.iterator();
				String pageId = dataIter.next();
				Integer pageIdInt = Integer.parseInt(pageId);
				Integer pageColIndex = memoryMap.indexOf(pageIdInt);
				String curBite;

				// if there is a page to add, add it
				// in data, first string is column name, and the others string
				// representing page data in bytes.

				tableCols[pageColIndex].setText(pageId);
				for (int i = 0; i < bytesInPage; i++) {
					curBite = dataIter.next();
					tableRows[i].setText(pageColIndex, curBite);
				}
				pageColIndex = 0;
			}
		}
	}

	private void enablePlayButton(boolean flag) {
		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			playButton.setEnabled(flag);
			playAllButton.setEnabled(flag);
		}
	}

	private void enableResetButton(boolean flag) {
		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			resetButton.setEnabled(flag);
		}
	}

	private void updateFieldsStatistics() {

		if (shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			pageFaultText.setText(realPfCount.toString());
			pageReplacementText.setText(prCount.toString());
		}
	}

	@Override
	public void start() {
		createAndShowGui();
	}

	// A class used to save States of running program.
	private class State {
		private List<String> prCommands;
		private List<Integer> memoryMap;
		private List<Integer> lastMemoryMap;
		private Integer pfAmount;
		private Integer realPfAmount;
		private Integer prAmount;
		private ArrayList<List<String>> tableInfo;

		public ArrayList<List<String>> getTableInfo() {
			return tableInfo;
		}

		// read the current page table info and store it in a matrix.
		public void setTableInfo(TableColumn[] tableCols, TableItem[] tableRows) {
			for (int i = 0; i < numMMUPages; i++) {
				(tableInfo.get(i)).add(tableCols[i].getText());
				for (int j = 0; j < bytesInPage; j++) {
					(tableInfo.get(i)).add(tableRows[j].getText(i));
				}
			}
		}

		public State() {
			prCommands = new ArrayList<String>();
			memoryMap = new LinkedList<Integer>();
			lastMemoryMap = new LinkedList<Integer>();
			tableInfo = new ArrayList<List<String>>(numMMUPages);
			for (int i = 0; i < numMMUPages; i++) {
				tableInfo.add(new LinkedList<String>());
			}
		}

		public List<String> getPrCommands() {
			return prCommands;
		}

		public void setPrCommands(List<String> prCommands) {
			this.prCommands.clear();
			if (prCommands != null)
				this.prCommands.addAll(prCommands);
		}

		public List<Integer> getMemoryMap() {
			return memoryMap;
		}

		public void setMemoryMap(List<Integer> memoryMap) {
			this.memoryMap.clear();
			if (memoryMap != null)
				this.memoryMap.addAll(memoryMap);
		}

		public List<Integer> getLastMemoryMap() {
			return lastMemoryMap;
		}

		public void setLastMemoryMap(List<Integer> lastMemoryMap) {
			this.lastMemoryMap.clear();
			if (lastMemoryMap != null)
				this.lastMemoryMap.addAll(lastMemoryMap);
		}

		public Integer getPfAmount() {
			return pfAmount;
		}

		public void setPfAmount(Integer pfAmount) {
			this.pfAmount = pfAmount;
		}

		public Integer getRealPfAmount() {
			return realPfAmount;
		}

		public void setRealPfAmount(Integer realPfAmount) {
			this.realPfAmount = realPfAmount;
		}

		public Integer getPrAmount() {
			return prAmount;
		}

		public void setPrAmount(Integer prAmount) {
			this.prAmount = prAmount;
		}
	}
}
