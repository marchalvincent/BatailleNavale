package fr.upmc.pc2r.bnr.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.messages.CMESSputship;
import fr.upmc.pc2r.bnr.messages.CMESSship;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.sockets.Connction;
import fr.upmc.pc2r.bnr.thread.ThreadListener;
import fr.upmc.pc2r.bnr.vue.InfoPan;
import fr.upmc.pc2r.bnr.vue.PutShipsPan;

public class ModelPutShips
{

	private List<Ship> listShip;
	private Ship currentShip;
	private Connction conn;

	public ModelPutShips(Connction conn,ThreadListener listener)
	{
		this.conn = conn;
		listShip = new ArrayList<Ship>();
	}

	public void startControler(IMessage currentMessage) 
	{
		CMESSship messShip = (CMESSship) currentMessage;
		//Récupération de notre panel
		PutShipsPan currentPanel = (PutShipsPan)ClientController.getClientController().getFenetre().getPanneauApplication();
		currentPanel.setPutingShip(messShip.getNbPlace());
		currentShip = new Ship(messShip.getNbPlace());
	}

	//Méthode permettant d'envoyer le message PUTSHIP
	public void sendPutShip()
	{
		//Envoie du message PUTSHIP
		listShip.add(currentShip);
		CMESSputship messPutShip = new CMESSputship(EProtocole.PUTSHIP, currentShip);
		conn.sendMessage(messPutShip.messageToSend());

	}

	//Méthode permettant de collecter les casses d'un bateau
	public void addCoordToCurrentShip(Point p)
	{
		currentShip.addCoord(p);
		PutShipsPan currentPan = (PutShipsPan) ClientController.getClientController().getFenetre().getPanneauApplication();
		currentPan.setPanAlreadyUseCase(p);

		if (currentShip.isFull())
		{
			currentPan.cleanGreen();
			sendPutShip();
		}
		else
			currentPan.setPanPossibleCase(currentShip);
	}

	public List<Ship> getListShip() 
	{
		return listShip;
	}

}
