package m.client.ide.morpheus.framework.eclipse.manifest;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ManifestNetworkType {
	REST(1), NORMAL(2);
	
	private final int value;

	ManifestNetworkType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ManifestNetworkType valueOf(int value) {
		switch (value) {
		case 1:
			return ManifestNetworkType.REST;
		case 2:
			return ManifestNetworkType.NORMAL;
		default:
			throw new AssertionError("Unknown Network Type : " + value);
		}
	}
	
	public static ManifestNetworkType fromString(@NotNull String type) {
		String typeLower = type.toLowerCase();

		if (typeLower.equals("rest"))
			return ManifestNetworkType.REST;
		else if (typeLower.equals("normal"))
			return ManifestNetworkType.NORMAL;
		else
			throw new AssertionError("Unknown Network Type : " + type);
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		switch (this) {
		case REST:
			return "rest";
		case NORMAL:
			return "normal";
		default:
			throw new AssertionError("Unknown Network Type : " + this);
		}
	}
}
