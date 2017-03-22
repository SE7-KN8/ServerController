package sebe3012.servercontroller.server.monitoring;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;

/**
 * Created by Sebe3012 on 30.01.2017.
 * Class to watch the current server monitor
 */
public class ServerWatcher implements Runnable {
	public static boolean running = true;

	@Override
	public void run() {
		while (running) {
			BasicServer server = Tabs.getCurrentServer();

			if (server != null && server.getMonitor() != null && server.isRunning()) {

				ServerMonitor.MonitorResult result = server.getMonitor().update(500);

				FrameHandler.cpuData.add(result.getCpuPercent());
				FrameHandler.ramData.add(result.getRamPercent());

			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
