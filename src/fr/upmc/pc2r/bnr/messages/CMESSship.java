package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSship extends AbstractMessage {
	
	
	private int nbPlaces;

	public CMESSship (EProtocole p, int nbPlaces)
	{
		super(p);
		System.out.println("Demande d'ajout d'un ship de " + nbPlaces);
		this.nbPlaces = nbPlaces;
	}
	
	public int getNbPlace() {
		return nbPlaces;
	}
}
