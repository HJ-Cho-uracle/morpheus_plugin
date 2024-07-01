package m.client.ide.morpheus.framework.eclipse.manifest;


import m.client.ide.morpheus.core.constants.Const;

public class Prevention {
	private String serverAdress;
	private String rooting;
	private String androidHash;
	private String androidVersion;
	private String iosHash;
	private String iosVersion;
	private boolean useDebug;
	
	public Prevention() {
		this.serverAdress = Const.EMPTY_STRING;
		this.rooting = Const.EMPTY_STRING;
		this.androidHash = Const.EMPTY_STRING;
		this.androidVersion = Const.EMPTY_STRING;
		this.iosHash = Const.EMPTY_STRING;
		this.iosVersion = Const.EMPTY_STRING;
		this.useDebug = false;
	}

	public String getServerAdress() {
		return serverAdress;
	}

	public void setServerAdress(String serverAdress) {
		this.serverAdress = serverAdress;
	}

	public String getRooting() {
		return rooting;
	}

	public void setRooting(String rooting) {
		this.rooting = rooting;
	}
	
	public boolean isUseDebug() {
		return useDebug;
	}

	public void setUseDebug(boolean useDebug) {
		this.useDebug = useDebug;
	}

	public String getAndroidHash() {
		return androidHash;
	}

	public void setAndroidHash(String androidHash) {
		this.androidHash = androidHash;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getIosHash() {
		return iosHash;
	}

	public void setIosHash(String iosHash) {
		this.iosHash = iosHash;
	}

	public String getIosVersion() {
		return iosVersion;
	}

	public void setIosVersion(String iosVersion) {
		this.iosVersion = iosVersion;
	}
}
