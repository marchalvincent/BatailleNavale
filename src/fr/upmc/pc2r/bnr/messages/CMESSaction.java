package fr.upmc.pc2r.bnr.messages;

import java.util.List;

import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSaction extends AbstractMessage implements IMessageSend
{
	private List<EAction> ListAction;
	
	public CMESSaction(EProtocole protocole,List<EAction> listA)
	{
		super(protocole);
		ListAction = listA;
	}
	
	@Override
	public byte[] messageToSend() 
	{
		String result = "ACTION/";
		for (int i = 0;  i < ListAction.size() ;i++)
		{
			result += ListAction.get(i).value() + "/";
		}
		result += "\n";
		return result.getBytes();
	}
}
