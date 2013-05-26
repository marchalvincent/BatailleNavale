package fr.upmc.pc2r.bnr.thread;

import java.awt.Point;

import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.vue.GamePan;

public class ThreadAction extends Thread 
{

	private Point pLazer;

	private GamePan currentPanel;
	
	public ThreadAction(Point pLaser, int nbActions) 
	{
		this.pLazer = pLaser;
		currentPanel = (GamePan)ClientController.getClientController().getFenetre().getPanneauApplication();
		currentPanel.setNbAction(nbActions);
	}

	@Override
	public void run()
	{
		//Placer le pointeur du lazer sur la map
		currentPanel.putTarget(pLazer);
		currentPanel.setActionButtonEnabled(true);
	}

}
