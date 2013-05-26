package fr.upmc.pc2r.bnr.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.model.ModelConnection;
import fr.upmc.pc2r.bnr.vue.ConnectionPan;

public class ConnectKeyListener implements KeyListener {

	/***
	 * Apres avoir fait enter pour une connexion on :
	 * Se connecte sur la socket
	 * On envoie le message de connexion : LOGIN
	 */
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			ClientController currentClient = ClientController.getClientController();
			ConnectionPan currentPan = currentClient.getLoginPan();
			currentPan.setTextLoading("En attente d'une réponse du serveur");
			currentClient.connect();
			ModelConnection contCon = new ModelConnection(currentClient.getConnection(), currentPan.getPseudoAno(), ETypeConnection.CONNECT);
			currentClient.setConnect(contCon);
			contCon.startControler();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
