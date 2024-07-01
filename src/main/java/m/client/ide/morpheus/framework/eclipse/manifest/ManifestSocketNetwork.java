package m.client.ide.morpheus.framework.eclipse.manifest;

public class ManifestSocketNetwork extends ManifestNetwork {

	private String port;

	public ManifestSocketNetwork(String name, String path, String address, String timeout, String encoding, String port) {
		super(name, path, address, timeout, encoding);
		// TODO Auto-generated constructor stub
		this.port = port;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	@Override
	public String getNetworkTypeName() {
		return "SOCKET";
	}
}
