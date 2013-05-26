package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSawinneris implements IMessage {

	
	
	private EProtocole protocole;
	private String winnerName;

	public CMESSawinneris(EProtocole protocole, String winnerName) 
	{
		this.protocole= protocole;
		this.winnerName = winnerName;
	}

	@Override
	public String getProtocole() 
	{
		return protocole.value();
	}

	@Override
	public void setProtocole(EProtocole protocole) 
	{
		this.protocole = protocole;
	}

	public String getWinnerName() {
		return winnerName;
	}

}
