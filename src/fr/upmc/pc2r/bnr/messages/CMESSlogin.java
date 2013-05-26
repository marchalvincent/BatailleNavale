package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSlogin extends CMESSconnect implements IMessageSend 
{

	private String playerName;
	private String mdp;
	
	
	public CMESSlogin(EProtocole protocole, String playerName, String mdp) 
	{
		super(protocole, playerName);
		this.playerName = playerName;
		this.mdp = mdp;
	}
	
	@Override
	public byte[] messageToSend() 
	{
		String result = "LOGIN/" + playerName + "/" + mdp +"/\n";
		return result.getBytes();
	}

}
