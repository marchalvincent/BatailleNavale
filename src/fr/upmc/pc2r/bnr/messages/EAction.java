package fr.upmc.pc2r.bnr.messages;

public enum EAction {

	UP("U"),
	DOWN("D"),
	LEFT("L"),
	RIGHT("R"),
	FIRE("E");
	
	private final String action;
	
	private EAction (String a) {
		action = a;
	}
	
	public String value() {
		return action;
	}
}

