package sebe3012.servercontroller.server.monitoring;

import javafx.application.Platform;
import sebe3012.servercontroller.gui.FrameHandler;

public class ChartsUpdater extends Thread {

	public static boolean run = true;

	@Override
	public void run() {
		ServerMonitoring.startMonitoring();
		while (run) {

			double ramUsed = ServerMonitoring.ramUsed;
			double ramInstalled = ServerMonitoring.installedRam;
			double cpuPercent = ServerMonitoring.cpuUsed;
			double assignedRam = ServerMonitoring.assignedRam;
			
			double passiveCpu = 10d - cpuPercent;

			Platform.runLater(() -> {
				FrameHandler.assignedRam.setPieValue(assignedRam);
				FrameHandler.ramUsed1.setPieValue(ramUsed);
				FrameHandler.ramUsed2.setPieValue(ramUsed);
				FrameHandler.totalRam.setPieValue(ramInstalled);
				FrameHandler.totelCpu.setPieValue(passiveCpu);
				FrameHandler.usedCpu.setPieValue(cpuPercent);
			});

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ServerMonitoring.stopMonitoring();

	}

	public static void stopUpdate() {
		run = false;
	}

}
