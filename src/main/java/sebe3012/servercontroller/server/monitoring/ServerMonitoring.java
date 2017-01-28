package sebe3012.servercontroller.server.monitoring;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class ServerMonitoring {

	public static boolean run = true;

	public static double ramUsed = 1;
	public static double installedRam = 1;
	public static double cpuUsed = 1;
	public static double assignedRam = 1;
	public static double totalCpu = 1;

	@Deprecated
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
					if (Tabs.getCurrentServer() != null) {
						BasicServer server = Tabs.getCurrentServer();

						if (server != null && server.isRunning()) {
							try {
								ProcMem pm = new ProcMem();
								pm.gather(sigar, server.getPID());

								ServerMonitoring.ramUsed = pm.getSize() / 1024D / 1024D;
								Mem m = sigar.getMem();
								ServerMonitoring.installedRam = m.getTotal() / 1024D / 1024D;
								ServerMonitoring.assignedRam = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
								ProcCpu pc = new ProcCpu();
								pc.gather(sigar, server.getPID());
								ServerMonitoring.cpuUsed = pc.getTotal();

								Cpu cpu = new Cpu();
								cpu.gather(sigar);
								ServerMonitoring.totalCpu = 0;

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

				try {
					Thread.sleep(100);
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
		ServerMonitoring.totalCpu = 1;
	}
}