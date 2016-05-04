package sebe3012.servercontroller.server.monitoring;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;

public class ServerMonitoring {

	public static boolean run = true;

	public static double ramUsed = 1;
	public static double installedRam = 1;
	public static double cpuUsed = 1;
	public static double assignedRam = 1;

	private final static Sigar sigar = new Sigar();
	private static final Thread monitoringThread = new Thread(new ServerMontior());

	public static void startMonitoring() {
		monitoringThread.setName("server-monitoring-thread-2");
		monitoringThread.start();
	}

	public static void stopMonitoring() {
		run = false;
		sigar.close();
	}

	private static class ServerMontior extends Thread {
		@Override
		public void run() {
			while (run) {

				if (!FrameHandler.mainPane.getSelectionModel().isEmpty()) {
					if (Tabs.servers.get(((ServerTab) FrameHandler.mainPane.getSelectionModel().getSelectedItem())
							.getTabContent().getId()) != null) {
						if (Tabs.servers.get(((ServerTab) FrameHandler.mainPane.getSelectionModel().getSelectedItem())
								.getTabContent().getId()).hasServer()) {
							BasicServer js = Tabs.servers
									.get(((ServerTab) FrameHandler.mainPane.getSelectionModel().getSelectedItem())
											.getTabContent().getId())
									.getServer();

							if (js.isRunning()) {
								try {
									ProcMem pm = new ProcMem();
									pm.gather(sigar, js.getPID());
									ServerMonitoring.ramUsed = pm.getSize() / 1024D / 1024D;
									Mem m = sigar.getMem();
									ServerMonitoring.installedRam = m.getTotal() / 1024D / 1024D;
									ServerMonitoring.assignedRam = Integer.valueOf(
											/* TODO Use runtime value */1);
									ProcCpu pc = new ProcCpu();
									pc.gather(sigar, js.getPID());
									ServerMonitoring.cpuUsed = pc.getPercent();
								} catch (SigarException e1) {
									e1.printStackTrace();
								}
							} else {
								resetValues();
							}
						} else {
							resetValues();
						}
					} else {
						resetValues();
					}
				} else {
					resetValues();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void resetValues() {
		ServerMonitoring.ramUsed = 1;
		ServerMonitoring.cpuUsed = 1;
		ServerMonitoring.installedRam = 1;
		ServerMonitoring.assignedRam = 1;
	}
}
