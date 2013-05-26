package fr.upmc.pc2r.bnr.model;

import java.util.List;

import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.interfaces.IModel;
import fr.upmc.pc2r.bnr.messages.CMESSaction;
import fr.upmc.pc2r.bnr.messages.EAction;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.sockets.Connction;
import fr.upmc.pc2r.bnr.vue.GamePan;
import fr.upmc.pc2r.bnr.vue.InfoPan;

public class ModelGame implements IModel 
{
	private List<Ship> ListShip;
	private Connction connection;
	
	public ModelGame(Connction connection, List<Ship> ListShip) 
	{
		this.ListShip = ListShip;
		this.connection = connection;
	}

	@Override
	public void startControler() 
	{
		GamePan currentPanel = (GamePan)ClientController.getClientController().getFenetre().getPanneauApplication();
		//Placement des bateaus sur la map
		currentPanel.addBoats(ListShip);
	
		InfoPan.addInfo("\n *** La partie Commence ! ***\n");
	}

	//Envoie du message : Action
	public void sendAction(List<EAction> listAction) 
	{
		CMESSaction action = new CMESSaction(EProtocole.ACTION, listAction);
		connection.sendMessage(action.messageToSend());
	}

}
