package se7kn8.servercontroller.rest;

import io.javalin.Javalin;
import io.javalin.security.Role;
import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.api.rest.ServerControllerVersion;
import se7kn8.servercontroller.rest.authentication.ApiKeyManager;
import se7kn8.servercontroller.rest.authentication.permission.PermissionRole;
import se7kn8.servercontroller.server.ServerManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RestServer implements Runnable {

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
		//apiKeyManager.generateApiKey(Arrays.asList(new Permission(new GlobNode())));
		javalin = Javalin.create();
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
			ctx.status(401).result("Unauthorized");
		});
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

	public void createVersionEndpoint() {
		ServerControllerVersion version = new ServerControllerVersion();
		version.setVersion(ServerController.VERSION);
		javalin.get(basePath + "/version", ctx -> ctx.json(version), createRoleForPermission("servercontroller.version"));
	}

	public void createAddonsEndpoint() {
		List<ServerControllerAddons.ServerControllerAddonInfo> addonInfoList = new ArrayList<>();
		AddonLoader.ADDONS.forEach((id, addon) -> {
			ServerControllerAddons.ServerControllerAddonInfo info = new ServerControllerAddons.ServerControllerAddonInfo();
			info.setId(addon.getAddonInfo().getId());
			info.setName(addon.getAddonInfo().getName());
			info.setAuthors(addon.getAddonInfo().getAuthors());
			info.setVersion(addon.getAddonInfo().getVersion().toString());
			addonInfoList.add(info);
		});
		javalin.get(basePath + "/addons", ctx -> ctx.json(addonInfoList), createRoleForPermission("servercontroller.addons"));
	}

	@NotNull
	private List<Role> createRoleForPermission(@NotNull String permission) {
		return Role.roles(new PermissionRole(permission));
	}
}
