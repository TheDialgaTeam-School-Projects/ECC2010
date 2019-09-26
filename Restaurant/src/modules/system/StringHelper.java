package modules.system;

public final class StringHelper {
	public static final Boolean isNullOrEmpty(String value) {
		if (value == null || value.isEmpty())
			return true;
		else
			return false;
	}
}
