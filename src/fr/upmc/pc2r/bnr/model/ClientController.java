package fr.upmc.pc2r.bnr.model;

import java.net.ConnectException;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.messages.CMESSbye;
import fr.upmc.pc2r.bnr.messages.CMESSplayagain;
import fr.upmc.pc2r.bnr.protocole.EProtocole;
import fr.upmc.pc2r.bnr.sockets.Connction;
import fr.upmc.pc2r.bnr.thread.ThreadExecutor;
import fr.upmc.pc2r.bnr.thread.ThreadListener;
import fr.upmc.pc2r.bnr.vue.ConnectionPan;
import fr.upmc.pc2r.bnr.vue.Fenetre;
import fr.upmc.pc2r.bnr.vue.GamePan;
import fr.upmc.pc2r.bnr.vue.InfoPan;
import fr.upmc.pc2r.bnr.vue.PutShipsPan;
import fr.upmc.pc2r.bnr.vue.SpectatorPan;
import fr.upmc.pc2r.bnr.vue.WinnerPan;

public class ClientController 
{

	private Fenetre fenetre;
	private InfoPan infoPan;
	private ConnectionPan connPan;
	private GamePan gamePan;
	private PutShipsPan putShipsPan;
	private SpectatorPan spectatorPan;
	private WinnerPan winPan;


	private Connction connection;

	private static ClientController clientController;

	private ModelPutShips cPutShip;
	private ModelGame cGame;
	private ModelSPectator cSpectator;
	
	private ETypeConnection typeConnection;

	private String ip;
	private int port;

	private ThreadListener listener;
	private ModelConnection conConnection;
	private ThreadExecutor executor;


	//Constructeur
	public ClientController(String ip,String port)
	{
		infoPan = new InfoPan();
		this.ip = ip;
		this.port = Integer.parseInt(port);
		this.fenetre = new Fenetre();
		connPan = new ConnectionPan(ip,port);
		this.fenetre.setPanneau(connPan);
		
	}

	//Affichage de la page de connexion sur la frame
	public void showConnectionPage()
	{
		this.fenetre.setPanneau(connPan);
		connPan.accessConnection(true);
	}

	//Connexion et creation de la socket
	public void connect()
	{
		this.ip = connPan.getIp();
		this.port = connPan.getPort();
		try 
		{
			connection = new Connction(ip,port);
			listener = new ThreadListener(connection);
			listener.start();
			executor = new ThreadExecutor(listener);
			executor.start();
			connPan.accessConnection(false);
		}
		catch (ConnectException e) 
		{
			connPan.sendMessage("La connexion à échoué, essayez encore !");
		}
	}
	
	//Reference le type de connexion
	public void setTypeConnection (ETypeConnection typeConnection)
	{
		this.typeConnection = typeConnection;
	}

	//Début du placement des bateaux
	public void putships()
	{
		this.putShipsPan = new PutShipsPan(infoPan);
		getFenetre().setPanneau(putShipsPan);
		cPutShip = new ModelPutShips(connection, listener);
	}

	//Debut de la partie
	public void game()
	{
		this.gamePan = new GamePan(infoPan);
		getFenetre().setPanneau(gamePan);
		cGame = new ModelGame(connection,cPutShip.getListShip());
		cGame.startControler();
	}
		
	public void spectator() 
	{
		this.spectatorPan = new SpectatorPan(infoPan);
		getFenetre().setPanneau(spectatorPan);
		cSpectator = new ModelSPectator();
		executor.setSpectator(cSpectator);
	}
	
	//Affichage du winner
	public void winner(String winnerName) 
	{
		//Affichage du panel du winner
		winPan = new WinnerPan(winnerName);
		getFenetre().setPanneau(winPan);
	}

	/***
	 * Méthode replay permet d'envoyer au server le message replay
	 * et d'afficher l'écran de connexion.
	 */
	public void rePlay() 
	{
		CMESSplayagain mess = new CMESSplayagain(EProtocole.PLAYAGAIN);
		connection.sendMessage(mess.messageToSend());
		this.fenetre.setPanneau(connPan);
		connPan.accessConnection(false);
	}
	
	public void backToConnection() 
	{
		CMESSbye mBye = new CMESSbye(EProtocole.BYE);
		connection.sendMessage(mBye.messageToSend());
		if (executor.isAlive())
		{
			executor.stopThread();
			executor.stop();
		}
		if (listener.isAlive())
			listener.stop();
		showConnectionPage();
	}
	
	//Fermeture de la socket et des thread pour l'arret de l'application.
	public void leave() 
	{
		CMESSbye mBye = new CMESSbye(EProtocole.BYE);
		connection.sendMessage(mBye.messageToSend());
		if (executor.isAlive())
		{
			executor.stopThread();
			executor.stop();
		}
		if (listener.isAlive())
			listener.stop();
		fenetre.dispose();
		System.exit(0);
	}

	public Fenetre getFenetre() {
		return fenetre;
	}

	public void setFenetre(Fenetre fenetre) {
		this.fenetre = fenetre;
	}

	public Connction getConnection() {
		return connection;
	}

	public void setConnection(Connction connection) {
		this.connection = connection;
	}

	public ThreadListener getListener() {
		return this.listener;
	}

	public ModelConnection getConnect()
	{
		return this.conConnection;
	}

	public void setConnect(ModelConnection conConnection)
	{
		this.conConnection = conConnection;
	}

	public static ClientController getClientController() {
		return clientController;
	}

	public static void setClientController(ClientController clientController) {
		ClientController.clientController = clientController;
	}

	public ModelPutShips getPutShips() 
	{
		return cPutShip;
	}

	public ModelGame getGame() 
	{
		return cGame;
	}

	public ConnectionPan getLoginPan() 
	{
		return connPan;
	}
	
	public ETypeConnection getTypeConnection() 
	{
		return typeConnection;
	}

	public ModelSPectator getSpectator() 
	{
		return cSpectator;
	}

}
