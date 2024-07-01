package m.client.ide.morpheus.framework.eclipse;

public class ApplicationXcodeProject {
	
	public enum IOSPgmLanguageType {
		OBJECTIVEC(1), SWIFT(2);

		private final int value;

		IOSPgmLanguageType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static IOSPgmLanguageType valueOf(int value) {
			switch (value) {
			case 1:
				return IOSPgmLanguageType.OBJECTIVEC;
			case 2:
				return IOSPgmLanguageType.SWIFT;
			default:
				throw new AssertionError("Unknown Tag Type : " + value);
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case OBJECTIVEC:
				return "ObjectiveC";
			case SWIFT:
				return "Swift";
			default:
				throw new AssertionError("Unknown Programe Language Type : " + this);
			}
		}
	}
	
	private String projectName;
	private String applicationName;
	private String bundleId;
	private String deploymentTarget;
	
	private IOSPgmLanguageType pgmLanguageType;

	public ApplicationXcodeProject() {
	}

	public ApplicationXcodeProject(String projectName, String applicationName, String bundleId) {
		this.projectName = projectName;
		this.applicationName = applicationName;
		this.bundleId = bundleId;

		this.pgmLanguageType = IOSPgmLanguageType.OBJECTIVEC;
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

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getDeploymentTarget() {
		return deploymentTarget;
	}

	public void setDeploymentTarget(String deploymentTarget) {
		this.deploymentTarget = deploymentTarget;
	}
	public IOSPgmLanguageType getPgmLanguageType() {
		return pgmLanguageType;
	}

	public void setPgmLanguageType(IOSPgmLanguageType pgnLanguageType) {
		this.pgmLanguageType = pgnLanguageType;
	}


}
