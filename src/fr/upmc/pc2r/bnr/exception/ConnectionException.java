package fr.upmc.pc2r.bnr.exception;

public class ConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConnectionException() {
		super("Serveur distant non touvé !!");
	}

}
