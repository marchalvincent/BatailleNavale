package fr.upmc.pc2r.bnr.messages;

import java.awt.Point;

import fr.upmc.pc2r.bnr.autre.ASCIToInt;
import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSplayership extends AbstractMessage
{

	private String name;
	private Ship ship;
	
	
	public CMESSplayership(EProtocole p, String messContent) 
	{
		super(p);
		String [] tabMess = messContent.split("/");
		name = tabMess[1];
		
		ship = new Ship(tabMess.length - 2);
		Point tmpPoint = new Point();
		for (int i=2; i< tabMess.length ; i ++)
		{
			if(i %(2) == 0)
			{
				tmpPoint.x = Integer.parseInt(tabMess[i]);
			}
			else
			{
				tmpPoint.y = ASCIToInt.getIntwithASCI(tabMess[i]);
				ship.addCoord(tmpPoint);
				tmpPoint = new Point();
			}		
		}
	}
	
	public String getName() {
		return name;
	}

	public Ship getShip() {
		return ship;
	}


}
