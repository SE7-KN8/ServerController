package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.settings.Settings;

import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerConsoleTab implements TabEntry<BasicServerHandler>, BasicServer.MessageListener, BasicServer.StopListener {

	private BasicServerHandler serverHandler;
	private OutputFormatter formatter;
	private List<String> commandList;
	private final int MAX_COMMAND_HISTORY = (int) Settings.readSetting(Settings.Constants.MAX_COMMAND_HISTORY);
	private int currentListIndex;

	private ServerConsoleTab(BasicServerHandler serverHandler) {
		this.serverHandler = serverHandler;
		this.serverHandler.getServer().addMessageListener(this);
		this.serverHandler.getServer().addStopListener(this);
	}

	@FXML
	private BorderPane rootContent;

	@FXML
	private CodeArea consoleOutputArea;

	@FXML
	private TextField consoleInputField;

	@FXML
	private void onKeyPressed(KeyEvent event) {
		switch (event.getCode()) {
			case ENTER:
				sendCommand();
				break;
			case UP:
				if (currentListIndex < commandList.size() - 1) {
					currentListIndex++;
				}
				consoleInputField.setText(commandList.get(currentListIndex));
				break;
			case DOWN:
				if (currentListIndex > 0) {
					currentListIndex--;
					consoleInputField.setText(commandList.get(currentListIndex));
				} else {
					consoleInputField.setText("");
				}
				break;
		}
	}

	@FXML
	private void onSendClicked(ActionEvent event) {
		sendCommand();
	}

	private void sendCommand() {

		String command = consoleInputField.getText().trim();

		if (command.length() >= 1) {
			serverHandler.sendCommand(command);
			consoleInputField.setText("");
			if (commandList.size() > MAX_COMMAND_HISTORY) {
				commandList.remove(commandList.size() - 1);
			}
			if (!(commandList.size() >= 1 && commandList.get(0).equals(command))) {
				commandList.add(0, command);
			}

			currentListIndex = -1;
		}
	}

	@NotNull
	@Override
	public BasicServerHandler getItem() {
		return serverHandler;
	}

	@Override
	public void setItem(@NotNull BasicServerHandler item) {
		this.serverHandler = item;
	}

	@NotNull
	@Override
	public String getTitle() {
		return serverHandler.getServer().getName() + "-Console"; //TODO to localize
	}

	@NotNull
	@Override
	public Node getContent() {
		return rootContent;
	}

	@Override
	public boolean isCloseable() {
		return false;
	}

	@Override
	public void onMessage(String message) {
		String newMessage;

		if (message.endsWith("\n")) {
			newMessage = message;
		} else {
			newMessage = message + "\n";
		}

		Platform.runLater(() -> {
			consoleOutputArea.appendText(newMessage);
			consoleOutputArea.showParagraphAtBottom(consoleOutputArea.getCurrentParagraph());
		});
	}

	@Override
	public void onStop(int code) {
		Platform.runLater(() -> {
			consoleOutputArea.appendText("Server '" + serverHandler.getServer().getName() + "' has stopped with code " + code + "\n");
			consoleOutputArea.showParagraphAtBottom(consoleOutputArea.getCurrentParagraph());
		});
	}

	@Override
	public boolean onClose() {
		formatter.close();
		serverHandler.getServer().removeMessageListener(this);
		return true;
	}

	@FXML
	void initialize() {
		formatter = new OutputFormatter();
		formatter.start(consoleOutputArea);
		commandList = new ArrayList<>();
	}

	public static ServerConsoleTab createServerConsoleTab(BasicServerHandler server) {
		ServerConsoleTab consoleTab;

		try {
			consoleTab = new ServerConsoleTab(server);
			FXMLLoader loader = new FXMLLoader();
			loader.setController(consoleTab);
			loader.setResources(I18N.getDefaultBundle());
			loader.setLocation(ClassLoader.getSystemResource("fxml/tab/ServerConsoleTab.fxml"));
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return consoleTab;
	}

}
