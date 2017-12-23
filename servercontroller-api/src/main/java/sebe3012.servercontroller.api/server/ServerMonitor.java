package sebe3012.servercontroller.api.server;

/**
 * Created by Sebe3012 on 30.01.2017.
 * This class control the monitoring from a server
 */
public class ServerMonitor {

	public class MonitorResult {

		private float cpu;
		private float ram;
		private long totalRam;

		public MonitorResult(float cpu, float ram, long totalRam) {
			this.cpu = cpu;
			this.ram = ram;
			this.totalRam = totalRam;
		}

		public float getCpuPercent() {
			return cpu;
		}

		public float getRamPercent() {
			return ram;
		}

		public long getTotalRam() {
			return totalRam;
		}
	}
/*
	private static final SystemInfo system = new SystemInfo();
	private static final HardwareAbstractionLayer hardware = system.getHardware();
	private static final OperatingSystem os = system.getOperatingSystem();*/

	private int pid = -1;

	public void setPid(int pid) {
		this.pid = pid;
	}

	public MonitorResult update(long sleepTime) {

		float cpuPercent = 0;
		float ramPercent = 0;
		long totalRam = 0;

		/*if (pid != -1) {
			OSProcess process = ServerMonitor.os.getProcess(pid);
			if (process != null) {

				long startTime = System.currentTimeMillis();
				long startCpu = process.getUserTime() + process.getKernelTime();

				ramPercent = 100f * process.getResidentSetSize() / ServerMonitor.hardware.getMemory().getTotal();
				totalRam = process.getResidentSetSize();

				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				process = ServerMonitor.os.getProcess(pid);

				long endCpu = startCpu;
				long endTime = System.currentTimeMillis();

				if (process != null) {
					endCpu = process.getUserTime() + process.getKernelTime();
				}


				long time = endTime - startTime;
				long cpu = endCpu - startCpu;


				cpuPercent = 100f * ((float) cpu / (float) time);
			} else {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		return new MonitorResult(cpuPercent, ramPercent, totalRam);
	}

}
