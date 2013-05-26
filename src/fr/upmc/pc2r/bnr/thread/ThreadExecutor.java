package fr.upmc.pc2r.bnr.thread;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.messages.CMESSaccessdenied;
import fr.upmc.pc2r.bnr.messages.CMESSallyourbase;
import fr.upmc.pc2r.bnr.messages.CMESSawinneris;
import fr.upmc.pc2r.bnr.messages.CMESSdeath;
import fr.upmc.pc2r.bnr.messages.CMESSdrawgame;
import fr.upmc.pc2r.bnr.messages.CMESSheylisten;
import fr.upmc.pc2r.bnr.messages.CMESSmiss;
import fr.upmc.pc2r.bnr.messages.CMESSok;
import fr.upmc.pc2r.bnr.messages.CMESSouch;
import fr.upmc.pc2r.bnr.messages.CMESSplayermove;
import fr.upmc.pc2r.bnr.messages.CMESSplayerouch;
import fr.upmc.pc2r.bnr.messages.CMESSplayers;
import fr.upmc.pc2r.bnr.messages.CMESSplayership;
import fr.upmc.pc2r.bnr.messages.CMESSship;
import fr.upmc.pc2r.bnr.messages.CMESStouche;
import fr.upmc.pc2r.bnr.messages.CMESSwelcome;
import fr.upmc.pc2r.bnr.messages.CMESSwrong;
import fr.upmc.pc2r.bnr.messages.CMESSyourturn;
import fr.upmc.pc2r.bnr.model.ClientController;
import fr.upmc.pc2r.bnr.model.ModelConnection;
import fr.upmc.pc2r.bnr.model.ModelPutShips;
import fr.upmc.pc2r.bnr.model.ModelSPectator;
import fr.upmc.pc2r.bnr.vue.InfoPan;
import fr.upmc.pc2r.bnr.vue.SpectatorPan;

public class ThreadExecutor extends Thread {


	private ThreadListener listener;
	private boolean stop;
	private ClientController client;
	private boolean isNotConnectionPan;
	private ModelSPectator cSpectator;

	public ThreadExecutor (ThreadListener listener)
	{
		this.listener = listener;
		client = ClientController.getClientController();
		isNotConnectionPan = false;
	}

	/***
	 * Bouucle infini qui récupère le message dans le queue du ThreadListener
	 */
	@Override
	public void run ()
	{
		IMessage currentMess;
		stop = false;
		while (! stop)
		{
			try 
			{
				currentMess = listener.getFirstIMessage();
				getActionToMessage(currentMess);
			} 
			catch (InterruptedException e) 
			{
				stopThread();
			}

		}
	}


	//Crée un thread pour chaque action a réaliser
	private void getActionToMessage(IMessage currentMess)
	{

		if(currentMess instanceof CMESSwelcome)
		{
			ModelConnection connection = client.getConnect();
			connection.welcome(currentMess);
		}
		else if(currentMess instanceof CMESSaccessdenied)
		{
			System.out.println("Message Access denied");
			
			if(isNotConnectionPan)
				InfoPan.addInfo("Vous avez reçu un message Access denied !\n");
			else
				client.getLoginPan().sendMessage("Vous avez reçu un message Access denied !\n");
			//arret des threads d'écoute et d'éxcecution
			stop = true;
			listener.stop();
			//Retour a la page de connection
			client.showConnectionPage();

		}
		else if(currentMess instanceof CMESSplayers)
		{
			CMESSplayers players = (CMESSplayers) currentMess;
			InfoPan.addInfo("Les joueurs présents dans cette partie sont : "+ players.getPlayers() +"\n");
			if (client.getTypeConnection() == ETypeConnection.SPECTATOR)
				client.spectator();
			else
				client.putships();
		}
		else if (currentMess instanceof CMESSship)
		{
			if (! isNotConnectionPan)
				isNotConnectionPan = true;
			ModelPutShips putShip = client.getPutShips();
			putShip.startControler(currentMess);
		}
		else if (currentMess instanceof CMESSallyourbase)
		{
			client.game();
		}
		else if (currentMess instanceof CMESSwrong)
		{
			InfoPan.addInfo("Possisionnement Refusé\n");
			System.out.println("Message WRONG !");

		}
		else if (currentMess instanceof CMESSok)
		{
			System.out.println("Message OK");
		}

		else if(currentMess instanceof CMESSyourturn)
		{
			//DEVROUILLER les btn de deplacement
			CMESSyourturn mYourTurn = (CMESSyourturn) currentMess;
			ThreadAction action = new ThreadAction(mYourTurn.getpLaser(), mYourTurn.getNbActions());
			action.run();

		}
		else if(currentMess instanceof CMESStouche)
		{
			CMESStouche mTouche = (CMESStouche) currentMess;
			ThreadLazer lazer = new ThreadLazer(mTouche.getPoint(),mTouche.getProtocole());
			lazer.run();
		}
		else if(currentMess instanceof CMESSouch)
		{
			CMESSouch mouch = (CMESSouch) currentMess;
			ThreadLazer lazer = new ThreadLazer(mouch.getPoint(),mouch.getProtocole());
			lazer.run();
		}
		else if(currentMess instanceof CMESSmiss)
		{
			CMESSmiss mMiss = (CMESSmiss) currentMess;
			ThreadLazer lazer = new ThreadLazer(mMiss.getPoint(),mMiss.getProtocole());
			lazer.run();
		}
		else if(currentMess instanceof CMESSawinneris)
		{
			CMESSawinneris mAwinneris = (CMESSawinneris) currentMess;
			ClientController client = ClientController.getClientController();
			client.winner("THE WINNER IS : " + mAwinneris.getWinnerName());
			isNotConnectionPan = false;
		}

		else if(currentMess instanceof CMESSdeath)
		{
			CMESSdeath mDeath = (CMESSdeath) currentMess;
			InfoPan.addInfo("Le joueur : " + mDeath.getName() + " est mort\n");
		}

		else if(currentMess instanceof CMESSdrawgame)
		{
			ClientController client = ClientController.getClientController();
			client.winner("MATCH NULL !!");
			isNotConnectionPan= false;
		}

		else if(currentMess instanceof CMESSheylisten)
		{
			CMESSheylisten mHeyListen = (CMESSheylisten) currentMess;

			if(isNotConnectionPan)
				InfoPan.addInfo(mHeyListen.getNameSender() +" : " + mHeyListen.getMessage());
			else
				client.getLoginPan().sendMessage(mHeyListen.getNameSender() +" : " + mHeyListen.getMessage());
		}

		else if(currentMess instanceof CMESSplayership)
		{
			if (! isNotConnectionPan)
				isNotConnectionPan = true;
			CMESSplayership mPlayerShip = (CMESSplayership) currentMess;
			cSpectator.puship(mPlayerShip);
		}

		else if(currentMess instanceof CMESSplayermove)
		{
			CMESSplayermove mPlayermove = (CMESSplayermove) currentMess;
			cSpectator.move(mPlayermove);
		}

		else if(currentMess instanceof CMESSplayerouch)
		{
			CMESSplayerouch mPlayerouch = (CMESSplayerouch) currentMess;
			cSpectator.ouch(mPlayerouch);
		}


	}

	public void setSpectator(ModelSPectator cSpec)
	{
		this.cSpectator = cSpec;
	}


	//Méthode d'arret du thread
	public void stopThread() 
	{
		System.out.println("ARRET DE L EXECUTOR");
		stop = true;
	}
}
