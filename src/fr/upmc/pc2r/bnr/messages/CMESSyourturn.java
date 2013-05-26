package fr.upmc.pc2r.bnr.messages;

import java.awt.Point;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSyourturn extends AbstractMessage {

	private Point pLaser;
	private int nbActions;
	
	public CMESSyourturn(EProtocole p, Point pLaser, int nbActions) {
		super(p);
		this.setpLaser(pLaser);
		this.setNbActions(nbActions);
	}

	public int getNbActions() {
		return nbActions;
	}

	private void setNbActions(int nbActions) {
		this.nbActions = nbActions;
	}

	public Point getpLaser() {
		return pLaser;
	}

	private void setpLaser(Point pLaser) {
		this.pLaser = pLaser;
	}
	
	

}
