package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSwelcome extends AbstractMessage  
{
	private String playerName;
	
	public CMESSwelcome(EProtocole protocole, String playerName)
	{
		super(protocole);
		this.setPlayerName(playerName);
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
