package eu.playsc.minesofmystery.common.exception;

public class ValidException extends RuntimeException {
	public ValidException(String message) {
		super(message);
	}

	public ValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidException(Throwable cause) {
		super(cause);
	}

	public ValidException() {
		super();
	}
}
