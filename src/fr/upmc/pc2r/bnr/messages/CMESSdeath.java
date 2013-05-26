package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSdeath implements IMessage 
{
	
	private EProtocole protocole;
	private String name;

	public CMESSdeath(EProtocole protocole, String name) 
	{
		this.protocole= protocole;
		this.name = name;
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

	public String getName() {
		return name;
	}


}
