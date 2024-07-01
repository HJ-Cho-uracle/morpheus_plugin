package m.client.ide.morpheus.framework.eclipse.manifest;

public class ManifestHttpNetwork extends ManifestNetwork {

	private ManifestNetworkType type;

	public ManifestHttpNetwork(String name, String path, String address, String timeout, String encoding, ManifestNetworkType type) {
		super(name, path, address, timeout, encoding);
		this.type = type;
	}

	public ManifestNetworkType getType() {
		return type;
	}

	public void setType(ManifestNetworkType type) {
		this.type = type;
	}

	@Override
	public String getNetworkTypeName() {
		return "HTTP";
	}
}
