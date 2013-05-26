package fr.upmc.pc2r.bnr.vue;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.interfaces.IPanel;
import fr.upmc.pc2r.bnr.listener.SendMessageKeyListener;

public class SpectatorPan extends JPanel implements IPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	private final int NBCOL = 16;
	private final int NBLIG = 16;
	private final JButton[][] tabBtn;
	private final Icon[][] tabColor;
	private Icon icoTarget,icoTarget2,icoTarget3,icoTarget4,icoTarget5;
	private Icon icoSup1,icoSup2,icoSup3,icoSup12,icoSup23,icoSup13,icoSup123,icoSup4,icoSup14,icoSup24,icoSup34,icoSup1234,icoSup134,icoSup124,icoSup234;
	private HashMap<String, Point> tabTarget;
	private JPanel pnlMap;
	private JTextField txtMessage;


	public SpectatorPan(InfoPan infoPan) 
	{
		setLayout(null);

		pnlMap = new JPanel();
		pnlMap.setBounds(230, 11, 610, 550);
		pnlMap.setLayout(new GridLayout(16, 16, 0, 0));
		add(pnlMap);


		JPanel pnlWhater = new JPanel();
		pnlWhater.setBackground(Color.BLUE);
		pnlWhater.setBounds(230, 580, 30, 30);
		add(pnlWhater);



		JPanel pnlboat = new JPanel();
		pnlboat.setBackground(new Color(211, 211, 211));
		pnlboat.setBounds(230, 664, 30, 30);
		add(pnlboat);

		JTextArea txtWhater = new JTextArea();
		txtWhater.setEditable(false);
		txtWhater.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtWhater.setBackground(UIManager.getColor("Button.background"));
		txtWhater.setWrapStyleWord(true);
		txtWhater.setText("Case vierge.");
		txtWhater.setLineWrap(true);
		txtWhater.setBounds(270, 580, 100, 30);
		add(txtWhater);



		JTextArea txtBoat = new JTextArea();
		txtBoat.setEditable(false);
		txtBoat.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtBoat.setWrapStyleWord(true);
		txtBoat.setText("Case representant une partie d'un bateaux.");
		txtBoat.setLineWrap(true);
		txtBoat.setBackground(UIManager.getColor("Button.background"));
		txtBoat.setBounds(270, 664, 209, 51);
		add(txtBoat);

		JPanel pnlInfo = infoPan;
		pnlInfo.setBounds(10, 11, 210, 601);
		add(pnlInfo);

		JLabel lblEnter = new JLabel("ENTER pour envoyer le message");
		lblEnter.setBounds(35, 656, 166, 14);
		add(lblEnter);

		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.setBounds(230, 621, 30, 30);
		add(panel);

		JTextArea txtrCaseDunDe = new JTextArea();
		txtrCaseDunDe.setEditable(false);
		txtrCaseDunDe.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtrCaseDunDe.setWrapStyleWord(true);
		txtrCaseDunDe.setText("Un bateau a été touche");
		txtrCaseDunDe.setLineWrap(true);
		txtrCaseDunDe.setBackground(UIManager.getColor("Button.background"));
		txtrCaseDunDe.setBounds(270, 621, 173, 30);
		add(txtrCaseDunDe);

		txtMessage = new JTextField();
		txtMessage.setBounds(10, 631, 210, 20);
		txtMessage.addKeyListener(new SendMessageKeyListener());
		add(txtMessage);
		txtMessage.setColumns(10);

		tabBtn = new JButton [NBCOL][NBLIG];
		tabColor = new Icon [NBCOL][NBLIG];
		icoTarget = new ImageIcon("images/target.png");
		icoTarget2 = new ImageIcon("images/target2.png");
		icoTarget3 = new ImageIcon("images/target3.png");
		icoTarget4 = new ImageIcon("images/target5.png");

		icoSup1 = new ImageIcon("images/m02.png");
		icoSup2 = new ImageIcon("images/m03.png");
		icoSup3 = new ImageIcon("images/m04.png");
		icoSup4 = new ImageIcon("images/m05.png");
		icoSup12 = new ImageIcon("images/m2-3.png");
		icoSup23 = new ImageIcon("images/m3-4.png");
		icoSup13 = new ImageIcon("images/m2-4.png");
		icoSup14 = new ImageIcon("images/m5-2.png");
		icoSup24 = new ImageIcon("images/m5-3.png");
		icoSup34 = new ImageIcon("images/m4-5.png");
		icoSup123 = new ImageIcon("images/m2-3-4.png");
		icoSup134 = new ImageIcon("images/m2-4-5.png");
		icoSup124 = new ImageIcon("images/m2-3-5.png");
		icoSup234 = new ImageIcon("images/m3-4-5.png");
		icoSup1234 = new ImageIcon("images/m2-3-4-5.png");
		buildPanel();
	}

	private void buildPanel()
	{
		for (int i = NBLIG -1; i >=0 ; i--) 
		{
			for (int j = 0; j < NBCOL ; j++) 
			{
				JButton btnCase = new JButton();
				btnCase.setToolTipText(j+"_"+i);
				btnCase.setName("btn_"+j+"_"+i);
				btnCase.setBackground(Color.BLUE);
				btnCase.setFont(new Font("Motorwerk", Font.PLAIN, 7));
				pnlMap.add(btnCase);
				tabBtn[j][i] = btnCase;
			}
		}

		tabTarget = new HashMap<String, Point>();
	}

	public void setTabLegend (HashMap< String, String> tabLegend)
	{
		int marge = 40;
		int cpt = 0;
		JButton btnLegende;
		JTextArea txtLegende;

		for (String name : tabLegend.keySet()) 
		{
			btnLegende = new JButton();
			btnLegende.setIcon(getTarget(tabLegend.get(name)));
			btnLegende.setBounds(589, 580 + cpt * marge, 30, 30);
			add(btnLegende);

			txtLegende = new JTextArea();
			txtLegende.setEditable(false);
			txtLegende.setFont(new Font("Motorwerk", Font.PLAIN, 16));
			txtLegende.setWrapStyleWord(true);
			txtLegende.setText(name);
			txtLegende.setLineWrap(true);
			txtLegende.setBackground(UIManager.getColor("Button.background"));
			txtLegende.setBounds(629, 580 + cpt * marge, 209, 30);
			add(txtLegende);

			cpt++;
			this.repaint();
		}
	}

	public void putShip(Ship ship, String name) 
	{
		String res = getCorrectNum(name);
		for (Point pt : ship.getCoordonnees()) 
		{
			if (pt != null)
			{
				if (tabBtn[pt.x][pt.y].getText() == "")
					tabBtn[pt.x][pt.y].setText(String.valueOf(Integer.parseInt(res)));
				else
					tabBtn[pt.x][pt.y].setText(String.valueOf((Integer.parseInt(tabBtn[pt.x][pt.y].getText()) * Integer.parseInt(res))));
				selectCorrectIco(tabBtn[pt.x][pt.y].getText() , tabBtn[pt.x][pt.y]);
				tabColor[pt.x][pt.y] = tabBtn[pt.x][pt.y].getIcon();
				
			}

		}
	}

	private void selectCorrectIco(String texte, JButton btn) 
	{
		switch (texte) {
		case "3"://0
			btn.setIcon(icoSup1);
			break;
		case "5"://1
			btn.setIcon(icoSup2);
			break;
		case "7"://2
			btn.setIcon(icoSup3);
			break;
		case "11"://3
			btn.setIcon(icoSup4);
			break;
		case "15"://01
			btn.setIcon(icoSup12);
			break;
		case "21"://02
			btn.setIcon(icoSup13);
			break;
		case "33"://03
			btn.setIcon(icoSup14);
			break;
		case "35"://12
			btn.setIcon(icoSup23);
			break;
		case "55"://31
			btn.setIcon(icoSup24);
			break;
		case "77"://23
			btn.setIcon(icoSup34);
			break;
		case "105"://012
			btn.setIcon(icoSup123);
			break;
		case "385"://123
			btn.setIcon(icoSup234);
			break;
		case "231"://230
			btn.setIcon(icoSup134);
			break;
		case "165"://301
			btn.setIcon(icoSup124);
			break;
		case "1155"://0123
			btn.setIcon(icoSup1234);
			break;
		}
		System.out.println("Couleur : " + texte);
	}

	public void setTarget(Point p, String name)
	{
		if (!tabTarget.containsKey(name))
		{
			tabTarget.put(name,new Point(p.x,p.y));
			tabBtn[p.x][p.y].setIcon(getTarget(name));
		}
		else
		{
			Point tmp = tabTarget.get(name);
			//null si l'icon n'existe pas
			tabBtn[tmp.x][tmp.y].setIcon(tabColor[tmp.x][tmp.y]);
			tabTarget.put(name,new Point(p.x,p.y));
			tabBtn[p.x][p.y].setIcon(getTarget(name));
			putAllTargets();
		}
	}

	private void putAllTargets()
	{
		for (String name : tabTarget.keySet()) {
			Point tmp = tabTarget.get(name);
			tabBtn[tmp.x][tmp.y].setIcon(getTarget(name));
		}
	}

	private Icon getTarget(String name)
	{
		switch (getCorrectNum(name)) {
		case "3":
			return icoTarget;
		case "5":
			return icoTarget2;
		case "7":
			return icoTarget3;
		default :
			return icoTarget4;
		}
	}

	private String getCorrectNum(String name)
	{
		switch (name) {
		case "0":
			return "3";
		case "1":
			return "5";
		case "2":
			return "7";
		default :
			return "11";
		}
	}
	
	public void setMapOuch(Point pt) 
	{
		tabBtn[pt.x][pt.y].setBackground(Color.RED);
		tabBtn[pt.x][pt.y].setIcon(null);
	}

	public String getMessage()
	{
		String currentMessage = txtMessage.getText();
		txtMessage.setText("");
		return currentMessage;
	}

	@Override
	public void sendMessage(String msg) 
	{
		InfoPan.addInfo(msg);
	}
}
