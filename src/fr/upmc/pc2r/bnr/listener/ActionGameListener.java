package fr.upmc.pc2r.bnr.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.vue.GamePan;

public class ActionGameListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Récupération du bouton cliqué
		JButton btn = (JButton) e.getSource();
	
		//Récupération de la game et appel de l'action
		ClientController currentClient = ClientController.getClientController();
		GamePan currentPanel = (GamePan)currentClient.getFenetre().getPanneauApplication();
		currentPanel.doAction(btn.getName());
		
		//Dans le vas ou le joueur n'a plus d'action a réaliser
		if (currentPanel.isLastAction())
		{
			//On bloque l'accé au boutons d'actions
			currentPanel.setActionButtonEnabled(false);
			//On envoie l'action au server
			currentClient.getGame().sendAction(currentPanel.getAction());
			//On vide le nbre d'action réalisé
			currentPanel.clearActions();
		}
		
	}

}
