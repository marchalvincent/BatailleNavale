package fr.upmc.pc2r.bnr.vue;

import java.awt.Dimension;
import java.awt.Font;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.upmc.pc2r.bnr.interfaces.IPanel;
import fr.upmc.pc2r.bnr.listener.ConnectKeyListener;
import fr.upmc.pc2r.bnr.listener.ConnectionAnoListener;
import fr.upmc.pc2r.bnr.listener.ConnectionListener;
import fr.upmc.pc2r.bnr.listener.InscriptionListener;
import fr.upmc.pc2r.bnr.listener.SendMessageKeyListener;
import fr.upmc.pc2r.bnr.listener.SpectatorListener;

public class ConnectionPan extends JPanel implements IPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txtPseudo,txtIp,txtPort , txtPseudoAno , txtMessage;
	private JPasswordField txtmdp;
	private JLabel lblLogin,lblLoading,lblIp, lblPort ,lblmdp;
	private JButton btnConnection, btnInscription,btnConnectionAno,btnSpectator;
	private JPanel pnlAno;
	private JPanel pnlIpPort;
	private JLabel pnlBattleShip;
	
	//Message 
	private String chat;
	private JTextArea txtMessagerie;
	

	/**
	 * Constructeur par défaut
	 * Permet d'initialiser tous les composants
	 * Et de les placer
	 * @param port 
	 * @param ip 
	 */
	public ConnectionPan(String ip, String port) 
	{
		setLayout(null);

		chat = new String();
		
		JPanel pnlConnection = new JPanel();
		pnlConnection.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlConnection.setBounds(334, 285, 506, 198);
		add(pnlConnection);
		pnlConnection.setLayout(null);


		ImageIcon ban = new ImageIcon("images/ban.jpg");

		//Boutons
		btnConnection = new JButton("Connexion");
		btnConnection.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		btnConnection.setBounds(106, 148, 143, 37);
		btnConnection.addActionListener(new ConnectionListener());
		pnlConnection.add(btnConnection);
		
		lblLoading = new JLabel("Pour les personnes\r\n ayant un compte :");
		lblLoading.setFont(new Font("Motorwerk", Font.PLAIN, 25));
		lblLoading.setBounds(10, 11, 486, 50);
		pnlConnection.add(lblLoading);

		//Champs de texte
		txtPseudo = new JTextField();
		txtPseudo.setBounds(223, 72, 242, 20);
		pnlConnection.add(txtPseudo);

		//Dimension des éléments
		txtPseudo.setPreferredSize(new Dimension(150, 20));

		//Labels
		lblLogin = new JLabel("Pseudo : ");
		lblLogin.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		lblLogin.setBounds(60, 61, 114, 37);
		pnlConnection.add(lblLogin);
		lblmdp = new JLabel("Mot de passe :");
		lblmdp.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		lblmdp.setBounds(60, 115, 153, 22);
		pnlConnection.add(lblmdp);
		btnInscription = new JButton("Inscription");
		btnInscription.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		btnInscription.setBounds(300, 147, 143, 38);
		pnlConnection.add(btnInscription);
		txtmdp = new JPasswordField();
		txtmdp.setBounds(223, 117, 242, 20);
		pnlConnection.add(txtmdp);
		txtmdp.setPreferredSize(new Dimension(150, 20));

		pnlAno = new JPanel();
		pnlAno.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlAno.setBounds(334, 494, 319, 152);
		add(pnlAno);
		pnlAno.setLayout(null);

		JLabel lblPseudoAno = new JLabel("Pseudo : ");
		lblPseudoAno.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		lblPseudoAno.setBounds(10, 59, 101, 37);
		pnlAno.add(lblPseudoAno);

		txtPseudoAno = new JTextField();
		txtPseudoAno.setBounds(99, 70, 193, 20);
		pnlAno.add(txtPseudoAno);
		txtPseudoAno.setPreferredSize(new Dimension(150, 20));

		JLabel lblAnonyme = new JLabel("Anonyme");
		lblAnonyme.setFont(new Font("Motorwerk", Font.PLAIN, 25));
		lblAnonyme.setBounds(99, 11, 121, 37);
		pnlAno.add(lblAnonyme);

		btnConnectionAno = new JButton("Connexion");
		btnConnectionAno.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		btnConnectionAno.setBounds(111, 107, 146, 34);
		btnConnectionAno.addActionListener(new ConnectionAnoListener());
		pnlAno.add(btnConnectionAno);

		pnlIpPort = new JPanel();
		pnlIpPort.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlIpPort.setBounds(334, 657, 506, 57);
		add(pnlIpPort);
		pnlIpPort.setLayout(null);
		lblIp = new JLabel("Ip : ");
		lblIp.setFont(new Font("Motorwerk", Font.PLAIN, 25));
		lblIp.setBounds(39, 11, 50, 35);
		pnlIpPort.add(lblIp);
		txtIp = new JTextField();
		txtIp.setBounds(99, 23, 226, 20);
		pnlIpPort.add(txtIp);
		txtIp.setPreferredSize(new Dimension(150, 20));
		lblPort = new JLabel("Port : ");
		lblPort.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		lblPort.setBounds(335, 20, 81, 20);
		pnlIpPort.add(lblPort);
		txtPort= new JTextField();
		txtPort.setBounds(412, 23, 60, 20);
		pnlIpPort.add(txtPort);
		txtPort.setPreferredSize(new Dimension( 50, 20));

		pnlBattleShip = new JLabel(ban);
		pnlBattleShip.setBounds(10, 11, 830, 261);
		add(pnlBattleShip);
		
		JPanel pnlChat = new JPanel();
		pnlChat.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlChat.setBounds(10, 285, 313, 429);
		add(pnlChat);
		pnlChat.setLayout(null);
		
		txtMessagerie = new JTextArea();
		txtMessagerie.setEditable(false);
		txtMessagerie.setLineWrap(true);
		txtMessagerie.setBounds(10, 11, 293, 350);
		JScrollPane zoneScrolable = new JScrollPane(txtMessagerie);
		zoneScrolable.setBounds(10, 11, 293, 350);
		pnlChat.add(zoneScrolable);
		
		txtMessage = new JTextField();
		txtMessage.setEnabled(false);
		txtMessage.setBounds(10, 372, 293, 20);
		txtMessage.addKeyListener(new SendMessageKeyListener());
		txtMessage.setColumns(10);
		pnlChat.add(txtMessage);
		
		JLabel label = new JLabel("ENTER pour envoyer le message");
		label.setBounds(20, 404, 231, 14);
		pnlChat.add(label);
		
		JPanel pnlSpectateur = new JPanel();
		pnlSpectateur.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlSpectateur.setBounds(663, 494, 177, 152);
		add(pnlSpectateur);
		pnlSpectateur.setLayout(null);
		
		JLabel lblSpectateur = new JLabel("Spectateur");
		lblSpectateur.setBounds(24, 28, 143, 29);
		lblSpectateur.setFont(new Font("Motorwerk", Font.PLAIN, 25));
		pnlSpectateur.add(lblSpectateur);
		
		btnSpectator = new JButton("Connexion");
		btnSpectator.setFont(new Font("Motorwerk", Font.PLAIN, 20));
		btnSpectator.setBounds(10, 87, 157, 33);
		btnSpectator.addActionListener(new SpectatorListener());
		pnlSpectateur.add(btnSpectator);

		txtPort.addKeyListener(new ConnectKeyListener());
		txtIp.addKeyListener(new ConnectKeyListener());
		btnInscription.addActionListener(new InscriptionListener());
		txtPseudo.addKeyListener(new ConnectKeyListener());




		/*******************/
		setIp(ip);
		setPort(port);
	}



	public String getPseudoAno() 
	{
		return this.txtPseudoAno.getText();
	}
	
	public String getPseudo() 
	{
		return this.txtPseudo.getText();
	}

	public boolean fieldEmpty() 
	{
		return txtPseudo.getText().isEmpty();
	}

	public void setTextLoading(String text)
	{
		lblLoading.setText(text);
	}

	public void setIp(String ip)
	{
		txtIp.setText(ip);
	}

	public String getIp()
	{
		return txtIp.getText();
	}

	public void setPort(String port)
	{
		txtPort.setText(port);
	}

	public int getPort()
	{
		return Integer.parseInt(txtPort.getText());
	}

	public String getMdp() 
	{
		try 
		{
			MessageDigest m;
			String result;
			m = MessageDigest.getInstance("MD5");

			String tmp  = new String(txtmdp.getPassword());
			m.update(tmp.getBytes(), 0, tmp.length());
			result = new BigInteger(1,m.digest()).toString(16);
			System.out.println("Mdp : " + tmp + " MD5 : " + result );
			
			return result;
		} 
		catch (NoSuchAlgorithmException e) 
		{
			System.err.println("Problème avec le MD5.");
			return null;
		}

	}

	public void accessConnection(boolean isClose)
	{
		btnConnection.setEnabled(isClose);
		btnConnectionAno.setEnabled(isClose);
		btnInscription.setEnabled(isClose);
		btnSpectator.setEnabled(isClose);
		txtMessage.setEnabled(!isClose);
	}

	@Override
	public String getMessage() {
		String result =  txtMessage.getText();
		txtMessage.setText("");
		return result;
	}

	@Override
	public void sendMessage(String msg) 
	{
		Format formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("hh:mm:ss");

		chat ="[" + formatter.format(date) + "] : " + msg + "\n" + chat;
		txtMessagerie.setText(chat); 
	}
}
