package hu.futureofmedia.task.contactsapi.exceptions;

public class ContactPersonNotFoundException extends Exception{
	private static final long serialVersionUID = -370783042187360500L;

	public ContactPersonNotFoundException(String message) {
		super(message);
	}
}
