package m.client.ide.morpheus.framework.eclipse;

public class ApplicationAndroidProject {
	private String projectName;
	private String applicationName;
	private String packageName;
	private String compileSdk;

	public ApplicationAndroidProject() {
	}

	public ApplicationAndroidProject(String projectName, String applicationName, String packageName, String compileSdk) {
		this.projectName = projectName;
		this.applicationName = applicationName;
		this.packageName = packageName;
		this.compileSdk = compileSdk;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCompileSdk() {
		return compileSdk;
	}

	public void setCompileSdk(String compileSdk) {
		this.compileSdk = compileSdk;
	}

}
