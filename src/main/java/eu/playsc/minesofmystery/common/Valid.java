package eu.playsc.minesofmystery.common;

import eu.playsc.minesofmystery.common.exception.ValidException;

public class Valid {
	public static void checkNotNull(Object object) throws ValidException {
		if (object == null) {
			throw new ValidException("Object " + object + " cannot be null");
		}
	}

	public static void checkNotNull(Object object, String messageIfNull) throws ValidException {
		if (object == null) {
			throw new ValidException(messageIfNull);
		}
	}
}
