package fr.upmc.pc2r.bnr.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.model.ModelConnection;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.vue.ConnectionPan;

public class InscriptionListener implements ActionListener {

	/***
	 * Apres le clique d'un bouton d'inscription on :
	 * Se connecte sur la socket
	 * On envoie le message de d'inscription : REGISTER
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ClientController currentClient = ClientController.getClientController();
		ConnectionPan currentPan = currentClient.getLoginPan();
		currentPan.setTextLoading("En attente d'une réponse du serveur");
		currentClient.connect();
		//Dernier argument determine si c'est une inscription
		ModelConnection contCon = new ModelConnection(currentClient.getConnection(), currentPan.getPseudo(),currentPan.getMdp(),ETypeConnection.REGISTER);
		currentClient.setConnect(contCon);
		contCon.startControler();
	}

}
