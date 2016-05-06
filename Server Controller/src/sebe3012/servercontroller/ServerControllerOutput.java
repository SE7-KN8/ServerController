package sebe3012.servercontroller;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerControllerOutput extends PrintStream {

	public ServerControllerOutput(PrintStream original) {
		super(original);
	}

	@Override
	public void println(String line) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		Calendar cal = Calendar.getInstance();

		StackTraceElement caller = stack[2];
		super.println(
				new SimpleDateFormat("HH:mm:ss").format(cal.getTime()) + "  " + caller.toString() + ": " + line);
	}

}
