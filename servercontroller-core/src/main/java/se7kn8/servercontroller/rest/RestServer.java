package se7kn8.servercontroller.rest;

import io.javalin.Context;
import io.javalin.Javalin;
import io.javalin.security.Role;
import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.api.rest.ServerControllerError;
import se7kn8.servercontroller.api.rest.ServerControllerMessage;
import se7kn8.servercontroller.api.rest.ServerControllerPermissions;
import se7kn8.servercontroller.api.rest.ServerControllerServerState;
import se7kn8.servercontroller.api.rest.ServerControllerServers;
import se7kn8.servercontroller.api.rest.ServerControllerVersion;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.rest.authentication.ApiKeyManager;
import se7kn8.servercontroller.rest.authentication.permission.Permission;
import se7kn8.servercontroller.rest.authentication.permission.PermissionRole;
import se7kn8.servercontroller.rest.authentication.permission.node.GlobNode;
import se7kn8.servercontroller.server.ServerManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RestServer implements Runnable {

	public static final String API_VERSION = "1.0";

	private final String ERROR_NOT_FOUND = "Not found";
	private final String UNAUTHORIZED = "Unauthorized";
	private final String SUCCESSFUL = "Successful";

	private Javalin javalin;
	private int port;
	private String basePath;
	private ServerManager manager;
	private ApiKeyManager apiKeyManager;

	public RestServer(int port, String basePath, ServerManager manager) {
		this.port = port;
		this.basePath = basePath;
		this.manager = manager;
	}

	public void start() {
		Thread javalinInitializer = new Thread(this);
		javalinInitializer.setName("Javalin-Initializer-Thread");
		javalinInitializer.start();
	}

	public void stop() {
		javalin.stop();
		javalin.stop();
	}

	@Override
	public void run() {
		apiKeyManager = new ApiKeyManager();
		//TODO add gui to generate api keys
		if (ServerController.DEBUG) {
			apiKeyManager.generateApiKey(Arrays.asList(new Permission(new GlobNode())));
		}
		javalin = Javalin.create();
		javalin.contextPath(basePath);
		javalin.accessManager((handler, ctx, permittedRoles) -> {
			String apiToken = ctx.header("token");
			if (permittedRoles.size() > 1) {
				throw new IllegalArgumentException("Path: " + ctx.path() + " has more than one permission. Currently this is not allowed");
			}
			if (permittedRoles.size() < 1) {
				handler.handle(ctx);
				return;
			}

			if (apiToken != null) {
				PermissionRole role = (PermissionRole) permittedRoles.get(0);
				if (apiKeyManager.canExecute(apiToken, role.getPermission())) {
					handler.handle(ctx);
					return;
				}
			}
			sendError(UNAUTHORIZED, 401, ctx);
		});
		javalin.error(404, ctx -> sendError(ERROR_NOT_FOUND, 404, ctx));
		/*javalin.embeddedServer(new EmbeddedJettyFactory(() -> {

			Server server = new Server();

			HttpConfiguration http_config = new HttpConfiguration();
			http_config.setSecureScheme("https");
			http_config.setSecurePort(8443);
			http_config.setOutputBufferSize(32768);
			http_config.setRequestHeaderSize(8192);
			http_config.setResponseHeaderSize(8192);
			http_config.setSendServerVersion(true);
			http_config.setSendDateHeader(false);
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			http_config.addCustomizer(new SecureRequestCustomizer());

			ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(getSslContextFactory(), HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(https_config));
			sslConnector.setPort(443);
			ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(http_config));
			connector.setPort(80);
			server.setConnectors(new Connector[]{sslConnector, connector});

			return server;
		}));*/
		javalin.port(port);
		javalin.start();
		createVersionEndpoint();
		createAddonsEndpoint();
		createServersEndpoint();
		createServerStateEndpoint();
		createServerStartEndpoint();
		createServerStopEndpoint();
		createServerRestartEndpoint();
		createServersStartEndpoint();
		createServersStopEndpoint();
		createServersRestartEndpoint();
		createUserPermissionsEndpoint();
	}

	/*private static SslContextFactory getSslContextFactory() {
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(ClassLoader.getSystemResource("keystore/keystore.ks").toExternalForm());
		sslContextFactory.setKeyStorePassword("123456");
		sslContextFactory.setTrustStorePath(ClassLoader.getSystemResource("keystore/keystore.ks").toExternalForm());
		sslContextFactory.setTrustStorePassword("123456");
		sslContextFactory.setExcludeCipherSuites(
				"SSL_RSA_WITH_DES_CBC_SHA",
				"SSL_DHE_RSA_WITH_DES_CBC_SHA",
				"SSL_DHE_DSS_WITH_DES_CBC_SHA",
				"SSL_RSA_EXPORT_WITH_RC4_40_MD5",
				"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
				"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
				"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
		return sslContextFactory;

	}*///TODO add way to load user created certificates

	private void createVersionEndpoint() {
		ServerControllerVersion version = new ServerControllerVersion();
		version.setVersion(ServerController.VERSION);
		version.setApiVersion(RestServer.API_VERSION);
		javalin.get("/version", ctx -> ctx.json(version), createRoleForPermission("servercontroller.version"));
	}

	private void createAddonsEndpoint() {
		List<ServerControllerAddons.ServerControllerAddonInfo> addonInfoList = new ArrayList<>();
		AddonLoader.ADDONS.forEach((id, addon) -> {
			ServerControllerAddons.ServerControllerAddonInfo info = new ServerControllerAddons.ServerControllerAddonInfo();
			info.setId(addon.getAddonInfo().getId());
			info.setName(addon.getAddonInfo().getName());
			info.setAuthors(addon.getAddonInfo().getAuthors());
			info.setVersion(addon.getAddonInfo().getVersion().toString());
			addonInfoList.add(info);
		});
		ServerControllerAddons addons = new ServerControllerAddons();
		addons.setAddons(addonInfoList);
		javalin.get("/addons", ctx -> ctx.json(addons), createRoleForPermission("servercontroller.addons"));
	}

	private void createServersEndpoint() {
		javalin.get("/servers", ctx -> {
			List<ServerControllerServers.ServerControllerServer> serverList = new ArrayList<>();

			manager.getServerList().forEach(basicServerHandler -> {
				ServerControllerServers.ServerControllerServer server = new ServerControllerServers.ServerControllerServer();
				server.setName(basicServerHandler.getServer().getName());
				server.setServerCreatorInfo(basicServerHandler.getServer().getAddonID() + ":" + basicServerHandler.getServer().getServerCreatorID());
				server.setServerInformation(basicServerHandler.getServer().getServerInformation());
				serverList.add(server);
			});

			ServerControllerServers servers = new ServerControllerServers();
			servers.setServerList(serverList);
			ctx.json(servers);
		}, createRoleForPermission("servercontroller.servers"));
	}

	private void createServerStateEndpoint() {
		javalin.get("/server/:id/state", ctx -> {
			Optional<BasicServerHandler> handler = manager.findServerByID(ctx.param("id"));
			if (handler.isPresent()) {
				ServerControllerServerState state = new ServerControllerServerState();
				state.setState(handler.get().getServer().getState().name());
				ctx.json(state);
			} else {
				sendError(ERROR_NOT_FOUND, 404, ctx);
			}
		}, createRoleForPermission("servercontroller.server.state"));
	}

	private void createServerStartEndpoint() {
		javalin.post("/server/:id/start", ctx -> {
			Optional<BasicServerHandler> handler = manager.findServerByID(ctx.param("id"));
			if (handler.isPresent()) {
				handler.get().startServer();
				sendMessage(SUCCESSFUL, 200, ctx);
			} else {
				sendError(ERROR_NOT_FOUND, 404, ctx);
			}
		}, createRoleForPermission("servercontroller.server.start"));
	}

	private void createServerStopEndpoint() {
		javalin.post("/server/:id/stop", ctx -> {
			Optional<BasicServerHandler> handler = manager.findServerByID(ctx.param("id"));
			if (handler.isPresent()) {
				handler.get().stopServer();
				sendMessage(SUCCESSFUL, 200, ctx);
			} else {
				sendError(ERROR_NOT_FOUND, 404, ctx);
			}
		}, createRoleForPermission("servercontroller.server.stop"));
	}

	private void createServerRestartEndpoint() {
		javalin.post("/server/:id/restart", ctx -> {
			Optional<BasicServerHandler> handler = manager.findServerByID(ctx.param("id"));
			if (handler.isPresent()) {
				handler.get().restartServer();
				sendMessage(SUCCESSFUL, 200, ctx);
			} else {
				sendError(ERROR_NOT_FOUND, 404, ctx);
			}
		}, createRoleForPermission("servercontroller.server.restart"));
	}

	private void createServersStartEndpoint() {
		javalin.post("/servers/start", ctx -> {
			manager.startAllServers();
			sendMessage(SUCCESSFUL, 200, ctx);
		}, createRoleForPermission("servercontroller.servers.start"));
	}

	private void createServersStopEndpoint() {
		javalin.post("/servers/stop", ctx -> {
			manager.stopAllServers();
			sendMessage(SUCCESSFUL, 200, ctx);
		}, createRoleForPermission("servercontroller.servers.stop"));
	}

	private void createServersRestartEndpoint() {
		javalin.post("/servers/restart", ctx -> {
			manager.restartAllServers();
			sendMessage(SUCCESSFUL, 200, ctx);
		}, createRoleForPermission("servercontroller.servers.restart"));
	}

	private void createUserPermissionsEndpoint() {
		javalin.get("/user/permissions", ctx -> {
			String apiKey = ctx.header("token");
			if (apiKey == null) {
				apiKey = "";
			}
			List<Permission> permissions = apiKeyManager.getPermissions(apiKey);
			ServerControllerPermissions serverControllerPermissions = new ServerControllerPermissions();
			List<ServerControllerPermissions.ServerControllerPermission> permissionList = new ArrayList<>();
			for (Permission permission : permissions) {
				ServerControllerPermissions.ServerControllerPermission serverControllerPermission = new ServerControllerPermissions.ServerControllerPermission();
				serverControllerPermission.setName(permission.getPermission());
				permissionList.add(serverControllerPermission);
			}
			serverControllerPermissions.setPermissionList(permissionList);
			ctx.json(serverControllerPermissions);
		}, createRoleForPermission("servercontroller.user.permissions"));
	}

	private void sendError(String message, int code, Context ctx) {
		ServerControllerError error = new ServerControllerError();
		error.setErrorMessage(message);
		ctx.status(code).json(error);
	}

	private void sendMessage(String message, int code, Context ctx) {
		ServerControllerMessage serverControllerMessage = new ServerControllerMessage();
		serverControllerMessage.setMessage(message);
		ctx.status(code).json(message);
	}

	@NotNull
	private List<Role> createRoleForPermission(@NotNull String permission) {
		return Role.roles(new PermissionRole(permission));
	}
}
