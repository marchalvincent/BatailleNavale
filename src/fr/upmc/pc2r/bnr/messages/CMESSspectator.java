package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSspectator extends AbstractMessage implements IMessageSend {

	public CMESSspectator(EProtocole p) 
	{
		super(p);
	}

	@Override
	public byte[] messageToSend() 
	{
		return "SPECTATOR/\n".getBytes();
	}

}
