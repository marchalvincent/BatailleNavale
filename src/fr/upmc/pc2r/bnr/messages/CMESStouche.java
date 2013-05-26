package fr.upmc.pc2r.bnr.messages;

import java.awt.Point;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESStouche extends AbstractMessage{

	private Point point;
	
	public CMESStouche(EProtocole protocole, Point point) 
	{
		super(protocole);
		this.point = point;
	}

	public Point getPoint() {
		return point;
	}

}
