package fr.upmc.pc2r.bnr.autre;

import java.awt.Point;
import java.util.Arrays;

public class Ship {


	private Point[] coordonnees;
	private int cpter = 0;


	public Ship (int nbPlace)
	{
		coordonnees = new Point[nbPlace];
	}
	public Ship ( Point[] coordonnees)
	{
		this.coordonnees = coordonnees;

	}

	public Point[] getCoordonnees() {
		return coordonnees;
	}

	public void addCoord (Point p)
	{
		coordonnees[cpter] = p;
		cpter ++;
	}

	public boolean isFull()
	{
		return cpter == coordonnees.length;
	}

	public void clear()
	{
		coordonnees = new Point[coordonnees.length];
		cpter = 0;
	}

	public String toString()
	{

		StringBuilder result = new StringBuilder();
		result.append("Positionnement de Ship"+coordonnees.length +"\n" );
		for (Point p : coordonnees) {
			result.append("["  + IntToASCI.getASCIIwithInt(p.x) + ", " + p.y + "] ");
		}

		return result.toString() ;
	}

	public void sort() 
	{
		if (coordonnees.length>1)
		{
			boolean isX = (coordonnees[0].x == coordonnees[1].x);
			int[] tmpArray = new int[coordonnees.length];
			for (int i = 0; i < coordonnees.length; i++) 
			{
				if (isX)
					tmpArray[i] = coordonnees[i].y;
				else
					tmpArray[i] = coordonnees[i].x;
			}
			Arrays.sort(tmpArray);
			
			for (int i = 0; i < coordonnees.length; i++) {
				if (isX)
					coordonnees[i] = new Point( coordonnees[i].x, tmpArray[i]);
				else
					coordonnees[i] = new Point( tmpArray[i],coordonnees[i].y );
			}
		}
	}
}
