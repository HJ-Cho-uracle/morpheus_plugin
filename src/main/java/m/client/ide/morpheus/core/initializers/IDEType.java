package m.client.ide.morpheus.core.initializers;

public enum IDEType {
	NORMAL, ACADEMY;
	
	public static IDEType fromString(String type) {
		String str = type.toLowerCase();
		if (str.equals(NORMAL.toString()))
			return NORMAL;
		else if (str.equals(ACADEMY.toString()))
			return ACADEMY;
		else
			throw new AssertionError("Unknown IDE Type");
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		switch (this) {
		case NORMAL:
			return "normal";
		case ACADEMY:
			return "academy";
		default:
			throw new AssertionError("Unknown IDE Type");
		}
	}
	
	
}
