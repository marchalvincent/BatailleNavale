package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSconnect extends AbstractMessage implements IMessageSend
{
	private String parPlayerName;
	
	public CMESSconnect(EProtocole protocole, String parPlayerName)
	{
		super(protocole);
		this.parPlayerName = parPlayerName.replace("\\", "\\\\").replace("/", "\\/");
	}

	@Override
	public byte[] messageToSend()
	{
		return new String("CONNECT/" + parPlayerName + "/\n").getBytes();
	}
	
	public String getPlayerName() {
		return parPlayerName;
	}

}
