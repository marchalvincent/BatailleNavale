package fr.upmc.pc2r.bnr.protocole;

import java.awt.Point;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import fr.upmc.pc2r.bnr.autre.ASCIToInt;
import fr.upmc.pc2r.bnr.exception.ProtocoleException;
import fr.upmc.pc2r.bnr.interfaces.IMessage;
import fr.upmc.pc2r.bnr.messages.CMESSaccessdenied;
import fr.upmc.pc2r.bnr.messages.CMESSallyourbase;
import fr.upmc.pc2r.bnr.messages.CMESSawinneris;
import fr.upmc.pc2r.bnr.messages.CMESSconnect;
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


/**
 * Construit les {@link Message} depuis un tableau de bytes (dans le cas où on reçoit un message)
 * ou inversement (si on souhaite en envoyer un).
 *
 */
public class MessageFactory {

	public MessageFactory() {
		super();
	}

	/**
	 * Construit un Imessage à partir d'une chaine de bytes.
	 * @param parBytes
	 * @return
	 * @throws ProtocoleException
	 */
	public IMessage getMessage(byte[] parBytes) {
		return this.getMessage(new String(parBytes));
	}

	public IMessage getMessage(String string)
	{
		System.out.println("parse de " + string);
		// on split le message pour séparer l'énumération des paramètres
		final String[] split = string.split("/");
		if (split.length > 0)
		{
			switch (split[0]) 
			{
			case "CONNECT":
				return  new CMESSconnect(EProtocole.CONNECT,split[1]);

			case "WELCOME":
				return  new CMESSwelcome(EProtocole.WELCOME,split[1]);

			case "PLAYERS":
				return new CMESSplayers(EProtocole.PLAYERS, split);

			case "NOPLAYERS":
				return new CMESSaccessdenied(EProtocole.ACCESSDENIED);

			case "SHIP":
				final String nbCasesShip = split[1];
				return new CMESSship(EProtocole.PUTSHIP, Integer.parseInt(nbCasesShip));

			case "WRONG":
				return new CMESSwrong(EProtocole.WRONG);

			case "OK":
				return new CMESSok(EProtocole.OK);

			case "ALLYOURBASE":
				return new CMESSallyourbase(EProtocole.ALLYOURBASE);

			case "YOURTURN":
				return new CMESSyourturn(EProtocole.YOURTURN, new Point(Integer.parseInt(split[1]),ASCIToInt.getIntwithASCI(split[2])), Integer.parseInt(split[3]));

			case "TOUCHE":
				return new CMESStouche(EProtocole.TOUCHE, new Point(Integer.parseInt(split[1]),ASCIToInt.getIntwithASCI(split[2])));

			case "OUCH":
				return new CMESSouch(EProtocole.OUCH, new Point(Integer.parseInt(split[1]),ASCIToInt.getIntwithASCI(split[2])));

			case "MISS":
				return new CMESSmiss(EProtocole.MISS, new Point(Integer.parseInt(split[1]),ASCIToInt.getIntwithASCI(split[2])));

			case "AWINNERIS":
				return new CMESSawinneris(EProtocole.AWINNERIS,split[1]);

			case "DEATH":
				return new CMESSdeath(EProtocole.DEATH,split[1]);

			case "DRAWGAME":
				return new CMESSdrawgame(EProtocole.DRAWGAME);

			case "HEYLISTEN":			
				return new CMESSheylisten(EProtocole.HEYLISTEN, split[1],string);
				
			case "ACCESSDENIED":
				return new CMESSaccessdenied(EProtocole.ACCESSDENIED);

			case "PLAYERSHIP":			
				return new CMESSplayership(EProtocole.PLAYERSHIP, string);
				
			case "PLAYERMOVE":
				return new CMESSplayermove(EProtocole.PLAYERMOVE,split[1],split[2],split[3]);
				
			case "PLAYEROUCH":
				return new CMESSplayerouch(EProtocole.PLAYEROUCH,split[1],split[2],split[3]);
				

			default :
				System.out.println("L'implémentation du protocole n'est pas spécifié, nom:" + split[0] + ".");
				return null;
			}

			//throw new ProtocoleException("L'implémentation du protocole n'est pas spécifié, nom:" + split[0] + ".");
			
		}
		else
		{
			System.out.println("Le server a envoyé un message vide !!");
			//throw new ProtocoleException("Message envoyé non reconnu !");
			return null;
		}
		
	}

}
