package net.minestom.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleLoggerFormatter extends Formatter {
	protected static SimpleDateFormat s= new SimpleDateFormat("[HH:mm:ss]");
	
	protected static boolean l = true;
	
	@Override
	public String format(LogRecord record) {
		return s.format(new Date())+(l?" ["+Thread.currentThread().getName()+"] ":" ")+record.getMessage()+System.lineSeparator();
	}
}
