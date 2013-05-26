package fr.upmc.pc2r.bnr.autre;

public enum ETypeConnection {
	
	CONNECT("CONNECT"),
	REGISTER("REGISTER"),
	LOGIN("LOGIN"),
	SPECTATOR("SPECTATOR");
	
	private final String message;
	
	private ETypeConnection (String m) {
		message = m;
	}
	
	public String value() {
		return message;
	}
	

}
