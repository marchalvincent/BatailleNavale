package fr.upmc.pc2r.bnr.vue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Fenetre extends JFrame 
{
	/***
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * le JPanel principal de l'applciation c'est celui-ci qui 	 * est modifié lors d'un changement de page
	 */
	private JPanel pnlApplication;
	
	/**
	 * Constructeur par défaut
	 * @param actif permet de définir si on active ou non
	 * le menu et les boutons d'action du Jpanel.
	 * Ceci dans le but de bloquer toute action si la
	 * connexion avec la BDD n'est pas établie
	 */
	public Fenetre() 
	{
		//Définit un titre pour votre fenêtre
		this.setTitle("Bataille Navale Royale : Client");
	    //Définit la taille de la jFrame
		this.setSize(new Dimension(850,750));
	    //Nous allons maintenant dire à notre objet de se positionner au centre
		this.setLocationRelativeTo(null);
	    //Terminer le processus lorsqu'on clique sur "Fermer"
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //Bloquer le redimentionnement de la fenetre
		this.setResizable(false);
		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/ico.jpg"));
		//On utilisera un BorderLayout
	    this.setLayout(new BorderLayout());
		
	    pnlApplication = new JPanel();
	
	    this.add("Center",pnlApplication);
		
		//On rend la fenetre visible
		this.setVisible(true);
	}

	/**
	 * Permet de modifier le panneau principal de la fenetre
	 * @param pnlApplication
	 */
	public void setPanneau(JPanel pnlApplication) 
	{
		//On enleve l'ancient JPanel
		this.getContentPane().removeAll();
		this.repaint();
		//On met à jour le pnlApplication avec le nouveau
		//JPanel fourni en paramètre
		this.pnlApplication = pnlApplication;
		//On met en place le nouveau
		this.getContentPane().add("Center", pnlApplication);
		//et on le rend visible
		this.setVisible(true);
	}
	
	/**
	 * Permet de récupérer le panneau principal de la fenetre
	 * @return le JPanel principal de la fentre
	 */
	public JPanel getPanneauApplication()
	{
		return pnlApplication;
	}
}