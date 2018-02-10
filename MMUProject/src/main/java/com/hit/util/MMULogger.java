package com.hit.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MMULogger {
	public final static String DEFAULT_FILE_NAME = "./logs/log.txt";
	private FileHandler handler;
	private static MMULogger mmuLogger;

	private MMULogger() {
		try {
			handler = new FileHandler(DEFAULT_FILE_NAME);
			handler.setFormatter(new OnlyMessageFormatter());
		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
	}

	public synchronized void write(String command, Level level) {
		LogRecord logRecord = new LogRecord(level, command);
		handler.publish(logRecord);
	}

	public static MMULogger getInstance() {
		if (mmuLogger == null) {
			mmuLogger = new MMULogger();
		}

		return mmuLogger;
	}
	
	public static void restartLogger()
	{
		mmuLogger = null;
	}
	
	public void close()
	{
		if(handler != null)
		{
			handler.close();
			handler = null;
		}
	}

	public class OnlyMessageFormatter extends Formatter {

		public OnlyMessageFormatter() {
			super();
		
		}

		@Override
		public String format(final LogRecord record) {
			
			return record.getMessage();
		}

	}
}
