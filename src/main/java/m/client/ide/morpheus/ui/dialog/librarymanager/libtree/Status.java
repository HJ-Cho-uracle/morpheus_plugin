package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

public enum Status {
	NOTAPPLIED(0), APPLIED(1), UPDATABLE(2);

	private final int value;

	Status(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Status valueOf(int value) {
		switch (value) {
		case 0:
			return Status.NOTAPPLIED;
		case 1:
			return Status.APPLIED;
		case 2:
			return Status.UPDATABLE;
		default:
			throw new AssertionError("Unknown Progress : " + value);
		}
	}

	public static Status fromString(String  text) {
		if("사용중".equals(text)) {
			return Status.APPLIED;
		} else if(text.startsWith("업데이트")) {
			return Status.UPDATABLE;
		}
		return Status.NOTAPPLIED;
	}
	
	@Override
	public String toString() {
		String text = "";
		switch (getValue()) {
		case 0:
			text = "미사용";
			break;
		case 1:
			text = "사용중";
			break;
		case 2:
			text = "업데이트";
			break;

		default:
			break;
		}
		return text;
	}
}
