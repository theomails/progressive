package net.progressit.progressive;

public class PComponentException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public PComponentException(String message) {
		super(message);
	}
	public PComponentException(String message, Throwable t) {
		super(message, t);
	}
}