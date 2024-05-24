package m.client.ide.morpheus.core.resource;


public enum ResourceType {
	EMULATOR(1), LIBRARY(2), TEMPLATE(3), PROJECT(4), UI_FRAMEWORK(5), UI_TEMPLATE(6), SNIPPET(7), EXAMPLE(8);

	private int value;

	ResourceType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public static ResourceType valueOf(int value) {
		switch (value) {
		case 1:
			return EMULATOR;
		case 2:
			return LIBRARY;
		case 3:
			return TEMPLATE;
		case 4:
			return PROJECT;
		case 5:
			return UI_FRAMEWORK;
		case 6:
			return UI_TEMPLATE;
		case 7:
			return SNIPPET;
		case 8:
			return EXAMPLE;
		default:
			throw new AssertionError("Unknown Resource Type : " + value);
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case EMULATOR:
			return "emulator";
		case LIBRARY:
			return "library";
		case TEMPLATE:
			return "template";
		case PROJECT:
			return "project";
		case UI_FRAMEWORK:
			return "ui-framework";
		case UI_TEMPLATE:
			return "ui-template";
		case SNIPPET:
			return "snippet";
		case EXAMPLE:
			return "example";
		default:
			throw new AssertionError("Unknown Resource Type : " + this);
		}
	}

	public static ResourceType fromString(String type) {
		String typeLower = type.toLowerCase();

		if (typeLower.equals("emulator"))
			return ResourceType.EMULATOR;
		else if (typeLower.equals("library"))
			return ResourceType.LIBRARY;
		else if (typeLower.equals("template"))
			return ResourceType.TEMPLATE;
		else if (typeLower.equals("project"))
			return ResourceType.PROJECT;
		else if (typeLower.equals("ui-framework"))
			return ResourceType.UI_FRAMEWORK;
		else if (typeLower.equals("ui-template"))
			return ResourceType.UI_TEMPLATE;
		else if (typeLower.equals("snippet"))
			return ResourceType.SNIPPET;
		else if (typeLower.equals("example"))
			return ResourceType.EXAMPLE;
		else
			throw new AssertionError("Unknown Resource Type : " + type);
	}

}
