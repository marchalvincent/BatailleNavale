package fr.upmc.pc2r.bnr.vue;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.List;

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
import fr.upmc.pc2r.bnr.listener.ActionGameListener;
import fr.upmc.pc2r.bnr.listener.SendMessageKeyListener;
import fr.upmc.pc2r.bnr.messages.EAction;

public class GamePan extends JPanel implements IPanel{

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
	private Icon icoTaget;
	private int nbActionAlreadyUse;
	private int nbActions;
	private Point target;
    private List<EAction> listAction;
	//private final List<JButton > listBtnGreen;
    
    private JTextArea txtNbCoupsRestant;
	private JPanel pnlMap;
	private JButton btn_Top,btn_Left,btn_Right,btn_Bot,btn_Fire;
	private JTextField txtMessage;


	public GamePan(InfoPan infoPan) 
	{
		setLayout(null);

		pnlMap = new JPanel();
		pnlMap.setBounds(230, 11, 610, 550);
		pnlMap.setLayout(new GridLayout(16, 16, 0, 0));
		add(pnlMap);

		btn_Top = new JButton("↑");
		btn_Top.setBounds(467, 567, 50, 50);
		btn_Top.setName("btn_Top");
		btn_Top.addActionListener(new ActionGameListener());
		add(btn_Top);

		btn_Left = new JButton("←");
		btn_Left.setBounds(418, 616, 50, 50);
		btn_Left.setName("btn_Left");
		btn_Left.addActionListener(new ActionGameListener());
		add(btn_Left);

		btn_Right = new JButton("→");
		btn_Right.setBounds(516, 616, 50, 50);
		btn_Right.setName("btn_Right");
		btn_Right.addActionListener(new ActionGameListener());
		add(btn_Right);

		btn_Bot = new JButton("↓");
		btn_Bot.setBounds(467, 665, 50, 50);
		btn_Bot.setName("btn_Bot");
		btn_Bot.addActionListener(new ActionGameListener());
		add(btn_Bot);

		btn_Fire = new JButton("X");
		btn_Fire.setBounds(467, 616, 50, 50);
		btn_Fire.setName("btn_Fire");
		btn_Fire.addActionListener(new ActionGameListener());
		add(btn_Fire);


		JPanel pnlWhater = new JPanel();
		pnlWhater.setBackground(Color.BLUE);
		pnlWhater.setBounds(230, 615, 30, 30);
		add(pnlWhater);

		JPanel panlMiss = new JPanel();
		panlMiss.setBackground(Color.WHITE);
		panlMiss.setBounds(591, 685, 30, 30);
		add(panlMiss);

		JPanel pnlFire = new JPanel();
		pnlFire.setBackground(new Color(255, 0, 0));
		pnlFire.setBounds(591, 567, 30, 30);
		add(pnlFire);

		JPanel pnlboat = new JPanel();
		pnlboat.setBackground(new Color(211, 211, 211));
		pnlboat.setBounds(591, 629, 30, 30);
		add(pnlboat);

		JTextArea txtMiss = new JTextArea();
		txtMiss.setEditable(false);
		txtMiss.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtMiss.setBackground(UIManager.getColor("Button.background"));
		txtMiss.setLineWrap(true);
		txtMiss.setWrapStyleWord(true);
		txtMiss.setText("Tir dans l'eau.");
		txtMiss.setBounds(631, 685, 209, 41);
		add(txtMiss);

		JTextArea txtWhater = new JTextArea();
		txtWhater.setEditable(false);
		txtWhater.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtWhater.setBackground(UIManager.getColor("Button.background"));
		txtWhater.setWrapStyleWord(true);
		txtWhater.setText("Case vierge.");
		txtWhater.setLineWrap(true);
		txtWhater.setBounds(270, 615, 100, 30);
		add(txtWhater);

		JTextArea txtFire = new JTextArea();
		txtFire.setEditable(false);
		txtFire.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtFire.setWrapStyleWord(true);
		txtFire.setText("Vous avez touche un adversaire");
		txtFire.setLineWrap(true);
		txtFire.setBackground(UIManager.getColor("Button.background"));
		txtFire.setBounds(631, 567, 209, 57);
		add(txtFire);

		JTextArea txtBoat = new JTextArea();
		txtBoat.setEditable(false);
		txtBoat.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtBoat.setWrapStyleWord(true);
		txtBoat.setText("Case representant une partie d'un de vos bateaux.");
		txtBoat.setLineWrap(true);
		txtBoat.setBackground(UIManager.getColor("Button.background"));
		txtBoat.setBounds(631, 629, 209, 50);
		add(txtBoat);
		
		JPanel pnlInfo = infoPan;
		pnlInfo.setBounds(10, 11, 210, 601);
		add(pnlInfo);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 165, 0));
		panel.setBounds(230, 672, 30, 30);
		add(panel);
		
		JTextArea txtrCaseDunDe = new JTextArea();
		txtrCaseDunDe.setEditable(false);
		txtrCaseDunDe.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtrCaseDunDe.setWrapStyleWord(true);
		txtrCaseDunDe.setText("Un adversaire vous a touche.");
		txtrCaseDunDe.setLineWrap(true);
		txtrCaseDunDe.setBackground(UIManager.getColor("Button.background"));
		txtrCaseDunDe.setBounds(270, 659, 138, 67);
		add(txtrCaseDunDe);
		
		txtNbCoupsRestant = new JTextArea();
		txtNbCoupsRestant.setEditable(false);
		txtNbCoupsRestant.setFont(new Font("Motorwerk", Font.PLAIN, 16));
		txtNbCoupsRestant.setWrapStyleWord(true);
		txtNbCoupsRestant.setText("Coups restants : 0");
		txtNbCoupsRestant.setLineWrap(true);
		txtNbCoupsRestant.setBackground(UIManager.getColor("Button.background"));
		txtNbCoupsRestant.setBounds(240, 572, 178, 30);
		add(txtNbCoupsRestant);
		
		JLabel lblEnter = new JLabel("ENTER pour envoyer le message");
		lblEnter.setBounds(35, 654, 166, 14);
		add(lblEnter);
		
		txtMessage = new JTextField();
		txtMessage.setColumns(10);
		txtMessage.setBounds(10, 629, 210, 20);
		txtMessage.addKeyListener(new SendMessageKeyListener());
		add(txtMessage);

		tabBtn = new JButton [NBCOL][NBLIG];
		icoTaget = new ImageIcon("images/target4.png");
		nbActionAlreadyUse = 0;
		listAction = new ArrayList<EAction>();
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
				pnlMap.add(btnCase);
				tabBtn[j][i] = btnCase;
			}
		}

		setActionButtonEnabled(false);
	}

	//Placement des bateaux sur la map
	public void addBoats(List <Ship> listShip)
	{
		for (Ship ship : listShip) 
		{
			for (Point pt : ship.getCoordonnees()) 
			{
				tabBtn[pt.x][pt.y].setBackground(Color.GRAY);
			}
		}
	}


	//Rend accessible les bouton d'action
	public void setActionButtonEnabled(Boolean enabled)
	{
		btn_Right.setEnabled(enabled);
		btn_Left.setEnabled(enabled);
		btn_Top.setEnabled(enabled);
		btn_Bot.setEnabled(enabled);
		btn_Fire.setEnabled(enabled);
		if(enabled)
			setActionButtonEnabled();
	}
	
	private void setActionButtonEnabled()
	{
		if (target.x == NBCOL-1)
			btn_Right.setEnabled(false);
		else
			btn_Right.setEnabled(true);
		
		if (target.x == 0)
			btn_Left.setEnabled(false);
		else
			btn_Left.setEnabled(true);
		
		if (target.y == NBLIG-1)
			btn_Top.setEnabled(false);
		else
			btn_Top.setEnabled(true);
		
		if (target.y == 0)
			btn_Bot.setEnabled(false);
		else
			btn_Bot.setEnabled(true);
	}

	public void doAction(String action)
	{
		switch (action.split("_")[1]) 
		{
		case "Left":
			setTarget(new Point(-1,0));
			listAction.add(EAction.LEFT);
			break;
		case "Top":
			setTarget(new Point(0,1));
			listAction.add(EAction.UP);
			break;
		case "Bot":
			setTarget(new Point(0,-1));
			listAction.add(EAction.DOWN);
			break;
		case "Right":
			setTarget(new Point(1,0));
			listAction.add(EAction.RIGHT);
			break;
		case "Fire":
			listAction.add(EAction.FIRE);
			//Je suppose qu'apres un tire on n'a plus d'actions disponible
			nbActionAlreadyUse = nbActions;
			txtNbCoupsRestant.setText("Coups restants : " + 0);
			break;

		default:
			break;
		}
		setActionButtonEnabled();

	}

	public void setTarget(Point p)
	{
		tabBtn[target.x][target.y].setIcon(null);
		target = new Point( target.x + p.x, target.y + p.y);
		tabBtn[target.x][target.y].setIcon(icoTaget);
		setNbActionAlreadyUse();

	}
	
	public void putTarget(Point p)
	{
		if(target != null)
		{
			tabBtn[target.x][target.y].setIcon(null);
		}
		target = new Point( p.x, p.y);
		tabBtn[p.x][p.y].setIcon(icoTaget);
		setActionButtonEnabled();
		
	}

	public int getNbActionAlreadyUse() 
	{
		return nbActionAlreadyUse;
	}

	public void setNbActionAlreadyUse() 
	{
		this.nbActionAlreadyUse += 1;
		txtNbCoupsRestant.setText("Coups restants : " + (nbActions - nbActionAlreadyUse) );
	}

	public void setNbAction(int nbActions) 
	{
		this.nbActions = nbActions;
		txtNbCoupsRestant.setText("Coups restants : " + nbActions );
	}

	public boolean isLastAction() 
	{
		if (nbActionAlreadyUse == nbActions)
		{
			txtNbCoupsRestant.setText("Coups restants : 0");
			nbActionAlreadyUse = 0;
			return true;
		}
		return false;
	}

	public List<EAction> getAction() 
	{
		return listAction;
	}

	public void clearActions() 
	{
		listAction.clear();	
		nbActionAlreadyUse = 0;
		nbActions = 0;
	}

	public void setMapMiss(Point pt) 
	{
		tabBtn[pt.x][pt.y].setBackground(Color.WHITE);
	}

	public void setMapOuch(Point pt) 
	{
		tabBtn[pt.x][pt.y].setBackground(Color.ORANGE);
	}

	public void setMapTouche(Point pt) 
	{
		if ( tabBtn[pt.x][pt.y].getBackground() != Color.ORANGE)
			tabBtn[pt.x][pt.y].setBackground(Color.RED);
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
