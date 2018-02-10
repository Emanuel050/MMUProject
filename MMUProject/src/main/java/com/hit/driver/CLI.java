package com.hit.driver;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Scanner;

import com.hit.view.View;

public class CLI extends Observable implements Runnable,View {

	private static String NFU = "NFU";
	private static String LRU = "LRU";
	private static String RANDOM = "RANDOM";
	private static String START = "start";
	private static String STOP = "stop";
	private Scanner in;
	private PrintWriter out;

	public CLI(InputStream in, OutputStream out) {
		this.in = new Scanner(in);
		this.out = new PrintWriter(out);
	}

	public void write(String string) {
		out.write(string + "\n");
		out.flush();
	}
	
	@Override
	public void run() {
		start();
	
	}

	@Override
	public void start() {
		String userInput;
		boolean goodInput = true;
		String[] userInputSplited;
		String requstedAlgorithm = null;
		Integer ramCapacity = 0;
		String[] command = new String[2];
		
		while(true)
		{
			write("Please enter start or stop");
			userInput = in.nextLine().toLowerCase();
			
			if (userInput.equals(START)) {
				do {
					write("Please enter required algorithm and ram capacity");
					userInput = in.nextLine().toUpperCase();
					userInputSplited = userInput.split(" ");
					if (userInputSplited.length == 2) {
						if (userInputSplited[0].equals(NFU) || userInputSplited[0].equals(LRU)
								|| userInputSplited[0].equals(RANDOM)) {
							requstedAlgorithm = userInputSplited[0];
							try {
								ramCapacity = Integer.valueOf(userInputSplited[1]);
								if (ramCapacity < 1) {
									write("Invalid Input: Ram capacity must be greater than 0");
									continue;
								}
								goodInput = false;
							} catch (NumberFormatException e) {
								write("Invalid Input: Ram capacity must be numerical");
							}
						} else {
							write("Invalid algorithm");
							continue;
						}
					} else {
						write("Invalid input: must be like:'lru 3'");
						continue;
					}
				} while (goodInput);
				
				command[0] = requstedAlgorithm;
				command[1] = Integer.toString(ramCapacity);
				setChanged();
				notifyObservers(command);			
			}
			else if(userInput.equals(STOP))
			{
				write("Thank you.");
				in.close();
				out.close();
				break;	
			}
			else
			{
				write("Invalid Input: must enter 'start' or 'stop'");
				continue;
			} 
		}
	}
}
