package sebe3012.servercontroller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
		printlnInternal(x);
	}

	private void printlnInternal(String x) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[3];

		String append = "[" + stack.getFileName() + ":" + stack.getLineNumber() + "] ";

		log.log(level, append + x);
	}

	@Override
	public void println(boolean x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(char x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println() {
		printlnInternal("");
	}

	@Override
	public void println(int x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(long x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(float x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(double x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(@NotNull char[] x) {
		printlnInternal(String.valueOf(x));
	}

	@Override
	public void println(Object x) {
		printlnInternal(String.valueOf(x));
	}
}
