package fr.upmc.pc2r.bnr.messages;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSplayers extends AbstractMessage 
{
	private StringBuilder players;
	
	public String getPlayers() {
		return players.toString();
	}
	
	public CMESSplayers(EProtocole protocole,String [] tabName)
	{
		super(protocole);
		players = new StringBuilder();
		System.out.println("Les joueurs présent dans cette game sont : ");
		
		for(int i = 1 ; i < tabName.length ; i++)
			players.append(tabName[i] + " ");
		
		
		
	}
}
