package fr.upmc.pc2r.bnr.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import fr.upmc.pc2r.bnr.interfaces.IPanel;
import fr.upmc.pc2r.bnr.messages.CMESStalk;
import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.sockets.Connction;

public class SendMessageKeyListener implements KeyListener 
{

	/***
	 * Evenement répondant a l'envoi d'un message
	 */
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			//Récupération du message
			Connction connection = ClientController.getClientController().getConnection();
			String msg = ((IPanel)(ClientController.getClientController().getFenetre().getPanneauApplication())).getMessage();
			//Création du protocole
			CMESStalk talk = new CMESStalk(EProtocole.TALK, msg);
			//Envoie du message
			connection.sendMessage(talk.messageToSend());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
