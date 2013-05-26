package fr.upmc.pc2r.bnr.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.upmc.pc2r.bnr.messages.CMESSplayermove;
import fr.upmc.pc2r.bnr.messages.CMESSplayerouch;
import fr.upmc.pc2r.bnr.messages.CMESSplayership;
import fr.upmc.pc2r.bnr.vue.SpectatorPan;

public class ModelSPectator 
{

	private SpectatorPan currentPanel;
	private List<Color> listColor;
	private HashMap<String, String> listPlayer;
	private int cptPlayer = 0;
	private boolean legendUpdated = false;

	public ModelSPectator() 
	{
		currentPanel = (SpectatorPan)ClientController.getClientController().getFenetre().getPanneauApplication();
		listColor = new ArrayList<Color>();
		listPlayer = new HashMap<String, String>();
	}

	public void puship(CMESSplayership mess)
	{
		//Si le joueur n'existe aps dans la liste des joueur on l'ajoute
		if (!listPlayer.containsKey(mess.getName()))
		{
			listPlayer.put(mess.getName(), String.valueOf(cptPlayer));
			currentPanel.putShip(mess.getShip(), String.valueOf(cptPlayer));
			cptPlayer++;
			
		}
		else
			//Placer le bateau du joueur
			currentPanel.putShip(mess.getShip(), listPlayer.get(mess.getName()));

	}

	public void move(CMESSplayermove mess)
	{
		//Deplacer la target
		currentPanel.setTarget(mess.getPosition(),listPlayer.get(mess.getName()));
		if (! legendUpdated)
		{
			currentPanel.setTabLegend(listPlayer);
			legendUpdated = true;
		}
	}

	public void ouch(CMESSplayerouch mess)
	{
		//changer la couleur du bateau
		currentPanel.setMapOuch(mess.getPosition());
	}

}
