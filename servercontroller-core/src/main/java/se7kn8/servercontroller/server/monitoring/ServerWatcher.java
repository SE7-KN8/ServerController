package se7kn8.servercontroller.server.monitoring;

/**
 * Created by se7kn8 on 30.01.2017.
 * Class to watch the current server monitor
 */
public class ServerWatcher implements Runnable {
	public static boolean running = true;

	@Override
	public void run() {/*FIXME
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
		}*/
	}
}
