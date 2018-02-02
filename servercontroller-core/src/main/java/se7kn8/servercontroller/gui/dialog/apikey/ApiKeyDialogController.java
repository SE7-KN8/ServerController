package se7kn8.servercontroller.gui.dialog.apikey;

import se7kn8.servercontroller.gui.dialog.apikey.edit.ApiKeyEditDialog;
import se7kn8.servercontroller.rest.RestServer;
import se7kn8.servercontroller.rest.authentication.permission.Permission;
import se7kn8.servercontroller.rest.authentication.permission.node.NamedPermissionNode;
import se7kn8.servercontroller.rest.authentication.permission.node.PermissionNode;
import se7kn8.servercontroller.util.GUIUtil;
import se7kn8.servercontroller.util.I18N;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;

import java.util.List;
import java.util.stream.Collectors;

public class ApiKeyDialogController {

	private RestServer server;
	private ApiKeyEditDialog editDialog;

	public ApiKeyDialogController(RestServer server) {
		this.server = server;
		editDialog = new ApiKeyEditDialog(server);
	}

	@FXML
	private ToolBar toolbar;

	@FXML
	private ListView<String> listView;

	@FXML
	private void initialize() {
		listView.setItems(FXCollections.observableArrayList(server.getApiKeyManager().getKeys()));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/add.png").toExternalForm(), e -> {
			editDialog.showDialog();
			if (editDialog.getController().getCheckedPermissions().size() > 0) {
				List<String> permissionList = editDialog.getController().getCheckedPermissions();
				List<Permission> permissions = permissionList.stream().map(permission -> {
					String[] splitPermission = permission.split("\\.");
					PermissionNode[] nodes = new PermissionNode[splitPermission.length];
					for (int i = 0; i < splitPermission.length; i++) {
						nodes[i] = new NamedPermissionNode(splitPermission[i]);
					}
					return new Permission(nodes);
				}).collect(Collectors.toList());
				listView.getItems().add(server.getApiKeyManager().generateApiKey(permissions));
			}
		}, I18N.translate("tooltip_add_api_key"));

		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/remove.png").toExternalForm(), e -> {
			if (listView.getSelectionModel().getSelectedItem() != null) {
				server.getApiKeyManager().deleteApiKey(listView.getSelectionModel().getSelectedItem());
				listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
			}
		}, I18N.translate("tooltip_remove_api_key"));

		/*TODO
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/edit.png").toExternalForm(), e -> {
			if (listView.getSelectionModel().getSelectedItem() != null) {
				editDialog.getController().setPermissions(server.getApiKeyManager().getPermissions(listView.getSelectionModel().getSelectedItem()));
				editDialog.showDialog();
			}
		}, I18N.translate("tooltip_edit_api_key"));*/
	}

}
