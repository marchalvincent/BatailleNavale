package fr.upmc.pc2r.bnr.messages;

import java.awt.Point;

import fr.upmc.pc2r.bnr.autre.IntToASCI;
import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.interfaces.IMessageSend;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSputship extends AbstractMessage implements IMessageSend
{
	private Ship ship;
	
	
	public CMESSputship (EProtocole protocole, Ship ship)
	{
		super(protocole);
		this.ship = ship;
	}
	
	@Override
	public byte[] messageToSend() 
	{
		ship.sort();
		
		String result = "PUTSHIP/";
		Point [] coord = ship.getCoordonnees();
		for (int i = 0;  i < coord.length ;i++)
		{
			result += coord[i].x + "/" + convertIntToAlphaB(coord[i].y) + "/";
		}
		result += "\n";
		return result.getBytes();
	}
	
	private String convertIntToAlphaB(int value)
	{
		//String chaine = new Character((char)value).toString();
		return IntToASCI.getASCIIwithInt(value);
	}
}
