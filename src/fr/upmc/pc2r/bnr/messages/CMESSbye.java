package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSbye extends AbstractMessage implements IMessageSend {

	public CMESSbye(EProtocole p) 
	{
		super(p);
	}

	@Override
	public byte[] messageToSend() 
	{
		return new String("BYE/\n").getBytes();
	}

}
