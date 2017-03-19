package sebe3012.servercontroller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

/**
 * Created by Sebe3012 on 14.01.2017.
 * This class catch all {@code System.out.println("")}  calls and send it to the logger
 */
public class ConsoleLog extends PrintStream {

	private Logger log;
	private Level level;

	public ConsoleLog(String name, PrintStream old, Level level) {
		super(old);
		log = LogManager.getLogger(name);
		this.level = level;
	}

	@Override
	public void println(String x) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[2];

		String append = "[" + stack.getFileName() + ":" + stack.getLineNumber() + "] ";

		log.log(level, append + x);
	}

}
