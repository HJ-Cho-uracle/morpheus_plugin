package m.client.ide.morpheus.core.npm;

import java.io.File;

public class NpmConstants {
	
	public static final String FRONTEND_FOLDER = "frontend";
	public static final String NODE_MODULES_FOLDER = "node_modules";
	public static final String PACKAGE_FILE = "package.json";
	public static final String NPMRC_FILE = ".npmrc";
	public static final String CONFIG_JSON_FILE = "morpheus.config.json";
	public static final String PACKAGE_PATH = FRONTEND_FOLDER + File.separator + PACKAGE_FILE;
	// public static final String PACKAGE_PATH = PACKAGE_FILE;

	public static final String INSTALL_COMMAND = "install";
	public static final String RUN_COMMAND = "run";
	public static final String SCRIPTS = "scripts";
	
	public static final String NODE_PREFERENCE_ID = "org.nodeclipse.ui.preferences.NodePreferencePage";

    public static final String NPM_AUDIT = "audit";
	public static final String NPM_FIX = "fix";
	public static final String NPM_FORCE = "--force";
}