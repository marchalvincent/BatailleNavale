package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSplayagain extends AbstractMessage implements IMessageSend {

	public CMESSplayagain(EProtocole p) 
	{
		super(p);
	}

	@Override
	public byte[] messageToSend() 
	{
		return new String("PLAYAGAIN/\n").getBytes();
	}

}
