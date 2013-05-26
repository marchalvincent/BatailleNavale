package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESStalk extends AbstractMessage implements IMessageSend 
{

	private String message;
	public CMESStalk(EProtocole protocole, String message) 
	{
		super(protocole);
		this.message = message.replace("\\", "\\\\");
		this.message = this.message.replace("/", "\\/");
	}

	@Override
	public byte[] messageToSend() 
	{
		return new String("TALK/" + message + "/\n" ).getBytes();
	}

}
