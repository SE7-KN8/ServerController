package sebe3012.servercontroller.server;

public interface ServerListener {
	void serverStoped(int code);
	void serverReturnMessage(String message);
}
