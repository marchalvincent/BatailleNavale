package fr.upmc.pc2r.bnr.messages;

import java.awt.Point;

import fr.upmc.pc2r.bnr.autre.ASCIToInt;
import fr.upmc.pc2r.bnr.protocole.EProtocole;


public class CMESSplayermove extends AbstractMessage 
{

	private String name;
	private Point position;
	
	public CMESSplayermove(EProtocole p, String name, String coordX, String coordY) 
	{
		super(p);
		this.name = name;
		this.position = new Point(Integer.parseInt(coordX),ASCIToInt.getIntwithASCI(coordY));
	}
	
	public String getName()
	{
		return name;
	}

	public Point getPosition()
	{
		return position;
	}

}
