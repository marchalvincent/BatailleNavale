package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSdrawgame implements IMessage 
{

	private EProtocole protocole;

	public CMESSdrawgame(EProtocole protocole)
	{
		this.protocole = protocole;
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

}
