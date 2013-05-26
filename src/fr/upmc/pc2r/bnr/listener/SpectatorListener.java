package fr.upmc.pc2r.bnr.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.model.ModelConnection;
import fr.upmc.pc2r.bnr.vue.ConnectionPan;

public class SpectatorListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ClientController currentClient = ClientController.getClientController();
		ConnectionPan currentPan = currentClient.getLoginPan();
		currentPan.setTextLoading("En attente d'une réponse du serveur");
		currentClient.connect();
		ModelConnection contCon = new ModelConnection(currentClient.getConnection(), currentPan.getPseudo(),currentPan.getMdp(), ETypeConnection.SPECTATOR);
		currentClient.setConnect(contCon);
		contCon.startControler();

	}

}
