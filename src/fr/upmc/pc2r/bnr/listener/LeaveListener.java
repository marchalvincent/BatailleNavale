package fr.upmc.pc2r.bnr.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.upmc.pc2r.bnr.model.ClientController;

public class LeaveListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ClientController currentClient = ClientController.getClientController();
		currentClient.leave();
	}

}
