package sebe3012.servercontroller.client;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

//java -jar ServerControllerClient.jar 12345 lobby localhost
public class ServerControllerClient {

	public static void main(String[] args) {

		String ip = "localhost";

		if (args[2] != null) {
			ip = args[2];
		}

		try {
			Socket s = new Socket(ip, Integer.valueOf(args[0]));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write("Restart Server;id=" + args[1] + "\n");
			writer.flush();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
