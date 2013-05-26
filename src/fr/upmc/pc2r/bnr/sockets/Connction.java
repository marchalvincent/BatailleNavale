package fr.upmc.pc2r.bnr.sockets;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import fr.upmc.pc2r.bnr.exception.ProtocoleException;
import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.protocole.MessageFactory;

public class Connction {

	private static final int PORT = 2012;
	private Socket client;
	private DataInputStream input;
	private PrintStream output;
	private MessageFactory messFactory;

	public Connction () throws ConnectException
	{
		this ("127.0.0.1");
	}

	public Connction (final String parAdress) throws ConnectException
	{
		this (parAdress, PORT);
	}

	//Construteur : Crée la factory ainsi que la connexion
	public Connction (final String parAdress, final int parPort) throws ConnectException 
	{
		super();
		messFactory = new MessageFactory();
		try 
		{
			client = new Socket(parAdress, parPort);
			input = new DataInputStream(client.getInputStream());
			output = new PrintStream(client.getOutputStream(), true);
		} 
		catch (IOException e) 
		{
			if(client == null)
				throw new ConnectException();
			else
				try {
					client.close();
				} 
			catch (IOException e1) {
				System.err.println("Impossible de fermer la connexion.");
			}
			throw new ConnectException();
		}
	}

	/***
	 * Close les buffers et la socket
	 */
	public void closeConnexion() 
	{
		try {
			client.close();
			input.close();
			output.close();
		} catch (IOException e) {
			System.err.println("Impossible de fermer toutes les sockets.");
		}

	}

	/***
	 * Envoie un message au serveur
	 * @param message en byte (Utiliser la méthode getMessage de l'interface IMessageToSend)
	 */
	public void sendMessage (final byte[] message) {
		try {
			System.out.println("Envoi de  : "+ new String( message));
			output.write(message);
			output.flush();
		} catch (IOException e) {
			System.err.println("Impossible d'envoyer le message au serveur.");
		}
	}


	/**
	 * Reçoit un message byte par byte et construit l'objet {@link Message}.
	 */
	public List <IMessage> receiveMessage () throws ProtocoleException {
		try {
			// on récupère chaque caractère tant qu'on a pas le retour chariot.
			StringBuilder sb = new StringBuilder();
			int a;
			byte[] b = new byte[1024];
			do {
				a = input.read(b);
				sb.append(new String(b));
			} while (!sb.toString().contains("\n") || a == -1);

			System.out.println("Message brute " + sb.toString());
			//Renvoie une liste de Imessage dans le cas ou il y aurai plusieurs message en mm temps
			List <IMessage> listMess = new ArrayList<IMessage>();
			for (String mess : sb.toString().split("\n")) {
				if (mess.contains("/"))
				{
					System.out.println("les message factorisé : " + mess);
					listMess.add(messFactory.getMessage(mess));
				}
			}
			return listMess;

		}catch (IOException e) {
			System.err.println("Impossible de lire dans la socket.");
		}
		//TODO return null c'est moche...
		return null;
	}
}
