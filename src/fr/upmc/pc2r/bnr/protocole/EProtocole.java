package fr.upmc.pc2r.bnr.protocole;


/***
 * Enumeration des différent message possible pour ce protocole. 
 */
public enum EProtocole {

	CONNECT("CONNECT"),
	WELCOME("WELCOME"),
	PLAYERS("PLAYER"),
	ACCESSDENIED("ACCESSDENIED"),
	PUTSHIP("PUTSHIP"),
	WRONG("WRONG"),
	OK("OK"),
	SHIP("SHIP"),
	ALLYOURBASE("ALLYOURBASE"),
	YOURTURN("YOURTURN"),
	TOUCHE("TOUCHE"),
	OUCH("OUCH"),
	MISS("MISS"),
	ACTION("ACTION"),
	DRAWGAME("DRAWGAME"),
	AWINNERIS("AWINNERIS"),
	PLAYAGAIN("PLAYAGAIN"),
	BYE("BYE"), 
	HEYLISTEN("HEYLISTEN"),
	TALK("TALK"),
	DEATH("DEATH"), 
	REGISTER("REGISTER"),
	LOGIN("LOGIN"),
	SPECTATOR("SPECTATOR"),
	PLAYERSHIP("PLAYERSHIP"),
	PLAYERMOVE("PLAYERMOVE"),
	PLAYEROUCH("PLAYEROUCH"),
	;
	

	/**
	 * Ce code permet de faire un switch lors de la construction des messages.
	 */
	private final String message;
	
	private EProtocole (String m) {
		message = m;
	}
	
	public String value() {
		return message;
	}
}
