package m.client.ide.morpheus.framework.eclipse.manifest;

public abstract class ManifestNetwork {

	private String name;
	private String path;
	private String address;
	private String timeout;
	private String encoding;

	public ManifestNetwork(String name, String path, String address, String timeout, String encoding) {
		this.name = name;
		this.path = path;
		this.address = address;
		this.timeout = timeout;
		this.encoding = encoding;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public abstract String getNetworkTypeName();


}
