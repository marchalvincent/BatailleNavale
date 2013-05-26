package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public abstract class AbstractMessage implements IMessage {

	private EProtocole protocole;
	
	public AbstractMessage(EProtocole p) {
		super();
		this.protocole = p;
	}

	@Override
	public String getProtocole() {
		return protocole.value();
	}

	@Override
	public void setProtocole(EProtocole protocole) {
		this.protocole = protocole;
	}

}
