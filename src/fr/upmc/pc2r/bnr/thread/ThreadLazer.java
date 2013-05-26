package fr.upmc.pc2r.bnr.thread;

import java.awt.Point;

import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.vue.GamePan;

public class ThreadLazer extends Thread 
{
	private Point pt;
	private String protocole;
	private GamePan currentPanel;
	
	public ThreadLazer(Point point, String protocole)
	{
		this.pt = point;
		this.protocole = protocole;
		this.currentPanel = (GamePan)ClientController.getClientController().getFenetre().getPanneauApplication();
	}
	
	@Override
	public void run()
	{
		switch (protocole) {
		case "MISS":
			currentPanel.setMapMiss(pt);
			break;
		case "OUCH":
			currentPanel.setMapOuch(pt);
			break;
		case "TOUCHE":
			currentPanel.setMapTouche(pt);
			break;

		default:
			break;
		}
	}

}
