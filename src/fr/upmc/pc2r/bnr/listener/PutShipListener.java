package fr.upmc.pc2r.bnr.listener;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.vue.InfoPan;
import fr.upmc.pc2r.bnr.vue.PutShipsPan;

public class PutShipListener implements ActionListener{

	/***
	 * Apres le clique d'un bouton de la grille
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//Récupération des coordonnées du bouton cliqué sur la grille
		JButton btn = (JButton) e.getSource();
		System.out.println(btn.getName());
		ClientController currentClient = ClientController.getClientController();
		PutShipsPan currentPan = (PutShipsPan)currentClient.getFenetre().getPanneauApplication();
		
		String [] tabCoord = btn.getName().split("_");
		Point currentCase = new Point(Integer.parseInt(tabCoord[1]), Integer.parseInt(tabCoord[2]));
		
		//Si on a rien a poser sur la carte
		if (currentPan.getNbPlaceShip() == 0 && btn.getBackground() != Color.GREEN )
		{
			InfoPan.addInfo("En attente de server.\n");
		}
		//Sinon si on a un bateau a poser et qu'on peux le poser : on le pose
		else if((currentPan.listGreenCaseIsEmpty() && btn.getBackground() != Color.GRAY && btn.getBackground() != Color.RED) || btn.getBackground() == Color.GREEN )
		{
			currentClient.getPutShips().addCoordToCurrentShip(currentCase);
		}
		//Sinon on cette case n'est pas selectionnable
		else
		{
			InfoPan.addInfo("Vous ne pouvez que selectionner les une des cases vertes présentes!\n");
		}
	}

}