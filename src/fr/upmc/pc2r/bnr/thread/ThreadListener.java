package fr.upmc.pc2r.bnr.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.sockets.Connction;

public class ThreadListener extends Thread 
{

	private Connction connection;
	private LinkedBlockingQueue<IMessage> currentQue;

	public ThreadListener (Connction connection)
	{
		this.connection = connection;
		currentQue = new LinkedBlockingQueue<>();
	}

	/***
	 * Thread qui lie en permanence sur la socket et remplie la queue.
	 */
	@Override
	public void run ()
	{
		List<IMessage> currentMess = new ArrayList<IMessage>();
		while (true)
		{
			try 
			{
				currentMess = connection.receiveMessage();

				//Ajoute le message à la que
				for (IMessage iMessage : currentMess) {
					currentQue.add(iMessage);
				}
			} 
			catch (Exception  ex) 
			{
				stopThread();
				// Très important de réinterrompre
				Thread.currentThread().interrupt();
				// Sortie de la boucle infinie
				break; 
			}
		}
	}

	//récupère le dernier message dans la que // BLOQUANT
	public IMessage getFirstIMessage() throws InterruptedException
	{
		IMessage mess = null;

		mess = currentQue.take();
		System.out.println("Message récupéré : " + mess.getProtocole().toString());

		return mess;
	}

	//Méthode d'arret du thread
	private void stopThread() 
	{
		currentQue.clear();
		connection.closeConnexion();
	}


}
