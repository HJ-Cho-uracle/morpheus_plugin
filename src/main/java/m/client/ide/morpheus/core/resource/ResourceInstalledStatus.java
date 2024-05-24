package m.client.ide.morpheus.core.resource;

import m.client.ide.morpheus.core.messages.CoreMessages;

public enum ResourceInstalledStatus {
	NOT_INSTALLED(1), INSTALLED(2), UPDATE_AVAILABLE(99);

	private final int value;

	ResourceInstalledStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ResourceInstalledStatus valueOf(int value) {
		switch (value) {
		case 1:
			return ResourceInstalledStatus.NOT_INSTALLED;
		case 2:
			return ResourceInstalledStatus.INSTALLED;
		case 99:
			return ResourceInstalledStatus.UPDATE_AVAILABLE;
		default:
			throw new AssertionError("Unknown Resource Status : " + value);
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case NOT_INSTALLED:
			return CoreMessages.get(CoreMessages.LABEL_NOT_INSTALLED);
		case INSTALLED:
			return CoreMessages.get(CoreMessages.LABEL_INSTALLED);
		case UPDATE_AVAILABLE:
			return CoreMessages.get(CoreMessages.LABEL_UPDATE_AVAILABLE);
		default:
			throw new AssertionError("Unknown Resource Status : " + this);
		}
	}
}
