package m.client.ide.morpheus.core.resource;

import m.client.ide.morpheus.core.messages.CoreMessages;


public enum LibraryType {
	CORE(1), ADDON(2), PLUGIN(3), THIRD_PART_ANDROID(10), THIRD_PART_IOS(11), UNKNOWN(100);

	private final int value;

	LibraryType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case THIRD_PART_ANDROID:
			return "third_android";
		case THIRD_PART_IOS:
			return "third_ios";

		default:
			return super.toString().toLowerCase();
		}
	}
	
	public String getName() {
		switch (this) {
		case CORE:
			return "Core" + " " + CoreMessages.get(CoreMessages.TITLE_LIBRARY);
		case ADDON:
			return "Addon" + " " + CoreMessages.get(CoreMessages.TITLE_LIBRARY);
		case PLUGIN:
			return "Plugin" + " " + CoreMessages.get(CoreMessages.TITLE_LIBRARY);
		case THIRD_PART_ANDROID:
			return "3rd Part - Android";
		case THIRD_PART_IOS:
			return "3rd Part - iOS";
		default:
			return "Unknown";
		}
	}

	public static LibraryType valueOf(int value) {
		switch (value) {
		case 1:
			return LibraryType.CORE;
		case 2:
			return LibraryType.ADDON;
		case 3:
			return LibraryType.PLUGIN;
		case 10:
			return LibraryType.THIRD_PART_ANDROID;
		case 11:
			return LibraryType.THIRD_PART_IOS;
		default:
			return LibraryType.UNKNOWN;
		}
	}

	public static LibraryType fromString(String type) {
		String typeLower = type.toLowerCase();
		if (typeLower.equals("core"))
			return LibraryType.CORE;
		else if (typeLower.equals("addon"))
			return LibraryType.ADDON;
		else if (typeLower.equals("plugin"))
			return LibraryType.PLUGIN;
		else if (typeLower.equals("third_android"))
			return LibraryType.THIRD_PART_ANDROID;
		else if (typeLower.equals("third_ios"))
			return LibraryType.THIRD_PART_IOS;
		else
			return LibraryType.UNKNOWN;
	}

	public static boolean isLibraryType(String value) {
		if (value.equals(LibraryType.ADDON.toString()))
			return true;
		if (value.equals(LibraryType.CORE.toString()))
			return true;
		if (value.equals(LibraryType.PLUGIN.toString()))
			return true;
		if (value.equals(LibraryType.THIRD_PART_ANDROID.toString()))
			return true;
		if (value.equals(LibraryType.THIRD_PART_IOS.toString()))
			return true;
		return false;
	}
	
	public String getDisplayName() {
		switch (value) {
		case 1:
			return "Core";
		case 2:
			return "Addon";
		case 3:
			return "Plugin";
		case 10:
			return "3rd Part - Android";
		case 11:
			return "3rd Part - iOS";
		default:
			return "Unknown";
		}
	}
	
}
