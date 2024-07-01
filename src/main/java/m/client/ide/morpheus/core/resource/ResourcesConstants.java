package m.client.ide.morpheus.core.resource;

import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.utils.CommonUtil;


public class ResourcesConstants {
	
	public static final String COMMON = "common";
	
	public static final String EMULATOR = "emulator";
	
	public static final String LIBRARY = "library";
	
	public static final String TEMP = "temp";

	public static final String TEMPLATE = "template";
	
	public static final String PROJECT = "project";
	
	public static final String UI_FRAMEWORK = "ui-framework";
	
	public static final String UI_TEMPLATE = "ui-template";
	
	public static final String UI_FRAMEWORK_JSCSS = "jscss";
	
	public static final String UI_FRAMEWORK_SCRIPT = "script";
	
	public static final String SNIPPET = "snippet";
	
	public static final String SNIPPET_API = "api";
	
	public static final String SNIPPET_CUSTOM_API = "custom_api";
	
	public static final String SNIPPET_SERVER_API = "server_api";
	
	public static final String SNIPPET_COMPONENT = "component";
	
	public static final String EXAMPLE = "example";
	
	public static final String EXAMPLE_PROJECT = "project";
	
	public static final String BACKUP = "backup";
	
	public static final String SDK_LIST = "sdkpref";
	
	public static final String STORE = "store";
	
	public static final String MSDK_PREF = "sdkpref";

	public static final String EMULATOR_CONFIG_FILE_NAME = "emulator.xml";

	public static final String LIBRARY_CONFIG_FILE_NAME = "library.xml";

	public static final String TEMPLATE_CONFIG_FILE_NAME = "template.xml";
	
	public static final String PROJECT_CONFIG_FILE_NAME = "project.xml";
	
	public static final String UI_FRAMEWORK_CONFIG_FILE_NAME = "ui-framework.xml";
	
	public static final String UI_TEMPLATE_CONFIG_FILE_NAME = "ui-template.xml";

	public static final String EMULATOR_LIST_CONFIG_FILE_NAME = "emulators.xml";

	public static final String LIBRARY_LIST_CONFIG_FILE_NAME = "libraries.xml";
	public static final String LIBRARY_UNAPPLY_FILE_NAME = "unapplyInfo.xml";

	public static final String LIBRARY_3RD_LIST_CONFIG_FILE_NAME = "libraries_3rd.xml";

	public static final String TEMPLATE_LIST_CONFIG_FILE_NAME = "templates.xml";
	
	public static final String PROJECT_LIST_CONFIG_FILE_NAME = "projects.xml";
	
	public static final String SNIPPET_CONFIG_FILE_NAME = "snippet.xml";
	
	public static final String UI_FRAMEWORK_LIST_CONFIG_FILE_NAME = "ui-frameworks.xml";
	
	public static final String UI_TEMPLATE_LIST_CONFIG_FILE_NAME = "ui-templates.xml";
	
	public static final String SNIPPET_LIST_CONFIG_FILE_NAME = "snippets.xml";
	
	public static final String EXAMPLE_CONFIG_FILE_NAME = "example.xml";
	
	public static final String EXAMPLE_LIST_CONFIG_FILE_NAME = "examples.xml";
	public static final String DEFAULT = "default";

    public static final String LIBRARY_LOCATION = CommonUtil.getPathString(CoreSettingsState.getInstance().getMSdkPath(), LIBRARY);
}
