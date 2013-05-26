package fr.upmc.pc2r.bnr.interfaces;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public interface IMessage 
{
	public String getProtocole();

	public void setProtocole(EProtocole protocole);
}
