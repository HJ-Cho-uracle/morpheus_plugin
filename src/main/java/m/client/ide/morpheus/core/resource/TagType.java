package m.client.ide.morpheus.core.resource;


public enum TagType {
	LIBRARY(1), TEMPLATE(2), EMULATOR(3), PROJECT(4), UI_FRAMEWORK(5), UI_TEMPLATE(6), SNIPPET(7), EXAMPLE(8);

	private final int value;

	TagType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static TagType valueOf(int value) {
		switch (value) {
		case 1:
			return TagType.LIBRARY;
		case 2:
			return TagType.TEMPLATE;
		case 3:
			return TagType.EMULATOR;
		case 4:
			return TagType.PROJECT;
		case 5:
			return TagType.UI_FRAMEWORK;
		case 6:
			return TagType.UI_TEMPLATE;
		case 7:
			return TagType.SNIPPET;
		case 8:
			return TagType.EXAMPLE;
		default:
			throw new AssertionError("Unknown Tag Type : " + value);
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case LIBRARY:
			return "library";
		case TEMPLATE:
			return "template";
		case EMULATOR:
			return "emulator";
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
			throw new AssertionError("Unknown Tag Type : " + this);
		}
	}
}
