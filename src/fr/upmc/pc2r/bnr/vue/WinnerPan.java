package fr.upmc.pc2r.bnr.vue;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import fr.upmc.pc2r.bnr.autre.ETypeConnection;
import fr.upmc.pc2r.bnr.listener.LeaveListener;
import fr.upmc.pc2r.bnr.listener.ReplayListener;
import fr.upmc.pc2r.bnr.listener.BackListener;
import fr.upmc.pc2r.bnr.model.ClientController;
import javax.swing.UIManager;

public class WinnerPan extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param winnerName 
	 */
	public WinnerPan(String winnerName) 
	{
		setLayout(null);
		JButton btnReplay;
		if( ClientController.getClientController().getTypeConnection() == ETypeConnection.SPECTATOR)
		{
			btnReplay = new JButton("Retour à l'accueil");
			btnReplay.setFont(new Font("Tahoma", Font.PLAIN, 16));
			btnReplay.setBounds(204, 475, 175, 73);
			btnReplay.addActionListener(new BackListener());
		}
		else
		{
			btnReplay = new JButton("Rejouer");
			btnReplay.setFont(new Font("Tahoma", Font.PLAIN, 16));
			btnReplay.setBounds(204, 475, 175, 73);
			btnReplay.addActionListener(new ReplayListener());
		}
		add(btnReplay);
		
		JButton btnStop = new JButton("Arreter");
		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnStop.setBounds(492, 475, 175, 73);
		btnStop.addActionListener(new LeaveListener());
		add(btnStop);
		
		JTextArea textArea = new JTextArea(winnerName);
		textArea.setEditable(false);
		textArea.setFont(new Font("Motorwerk", Font.PLAIN, 45));
		textArea.setWrapStyleWord(true);
		textArea.setBackground(UIManager.getColor("Button.background"));
		textArea.setLineWrap(true);
		textArea.setBounds(204, 169, 463, 250);
		textArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		textArea.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
		add(textArea);

	}
}
