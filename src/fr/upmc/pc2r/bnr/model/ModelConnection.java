package fr.upmc.pc2r.bnr.model;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.interfaces.IModel;
import fr.upmc.pc2r.bnr.messages.CMESSconnect;
import fr.upmc.pc2r.bnr.messages.CMESSlogin;
import fr.upmc.pc2r.bnr.messages.CMESSregister;
import fr.upmc.pc2r.bnr.messages.CMESSspectator;
import fr.upmc.pc2r.bnr.messages.CMESSwelcome;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.sockets.Connction;
import fr.upmc.pc2r.bnr.vue.InfoPan;

public class ModelConnection implements IModel {

	private String userName;
	private Connction conn;
	private String mdp;
	private ETypeConnection typeConnection;

	public ModelConnection(Connction conn, String userName, ETypeConnection typeConnection)
	{
		this.userName = userName;
		this.conn = conn;
		this.typeConnection = typeConnection;
	}

	public ModelConnection(Connction conn, String userName,String mdp, ETypeConnection typeConnection)
	{
		this.userName = userName;
		this.conn = conn;
		this.mdp = mdp;
		this.typeConnection = typeConnection;
	}

	/***
	 * Cette méthode gère la connexion au server :
	 * Elle créer un message de connexion ou d'inscription
	 */
	@Override
	public void startControler() 
	{
		if (conn != null)
		{
			switch (typeConnection) 
			{
			//Si je suis en mode anonyme
			case CONNECT:
				CMESSconnect messConn = new CMESSconnect(EProtocole.CONNECT ,this.userName);
				conn.sendMessage(messConn.messageToSend());
				break;

				//Sinon je suis en mode register
			case REGISTER:
				CMESSregister messReg = new CMESSregister(EProtocole.REGISTER ,this.userName, this.mdp);
				conn.sendMessage(messReg.messageToSend());
				break;

				//Sinon je suis en mode login	
			case LOGIN:
				CMESSlogin messLog = new CMESSlogin(EProtocole.LOGIN ,this.userName, this.mdp);
				conn.sendMessage(messLog.messageToSend());
				break;

			case SPECTATOR:
				CMESSspectator messSpect = new CMESSspectator(EProtocole.SPECTATOR);
				conn.sendMessage(messSpect.messageToSend());
				break;

			default:
				break;
			}
			
			ClientController.getClientController().setTypeConnection(typeConnection);
		}
		else
			System.out.println("La valeur de la variable connexion est null !");

	}


	//Reception de la connexion et affichages a l'utilisateur
	public void welcome(IMessage message)
	{
		CMESSwelcome welcome = (CMESSwelcome) message;
		if (!welcome.getPlayerName().equals(userName))
		{
			userName = welcome.getPlayerName();
			System.out.println("Un utilisateur a déjà ce pseudo, votre pseudo est donc à présent :" +  userName);
		}
		System.out.println(this.userName + " vous etes connecté !");
		InfoPan.addInfo("Bienvenu agent " + userName + " dans le monde impitoyable de la Bataille Navale Royale !\n\n");

	}

	public void sendInscription(CMESSregister cRegister) 
	{
		conn.sendMessage(cRegister.messageToSend());
	}

}
