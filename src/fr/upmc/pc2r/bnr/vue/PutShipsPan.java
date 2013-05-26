package fr.upmc.pc2r.bnr.vue;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.upmc.pc2r.bnr.autre.Ship;
import fr.upmc.pc2r.bnr.interfaces.IPanel;
import fr.upmc.pc2r.bnr.listener.PutShipListener;
import fr.upmc.pc2r.bnr.listener.SendMessageKeyListener;

public class PutShipsPan extends JPanel implements IPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int NBCOL = 16;
	private final int NBLIG = 16;
	private final JButton[][] tabBtn;
	private final List<JButton > listBtnGreen;
	private final List<JButton > listBtnRed;
	private int currentNbPlaceShip;

	JLabel lblShip1,lblShip2,lblShip3,lblShip4;
	private JTextField txtMessage;


	/**
	 * Create the panel.
	 */
	public PutShipsPan(InfoPan infoPan) 
	{
		
		//initialisation du tableau de btn
		tabBtn = new JButton[NBLIG][NBCOL];

		listBtnGreen = new ArrayList<JButton>();
		listBtnRed = new ArrayList<JButton>();

		setForeground(Color.WHITE);
		setLayout(null);

		JPanel panGrid = new JPanel();
		panGrid.setBackground(Color.WHITE);
		panGrid.setBounds(230, 11, 610, 550);
		add(panGrid);
		panGrid.setLayout(new GridLayout(16, 16, 0, 0));

		for (int i = NBLIG -1; i >=0 ; i--) 
		{
			for (int j = 0; j < NBCOL ; j++) 
			{
				JButton btnCase = new JButton();
				btnCase.setToolTipText(j+"_"+i);
				btnCase.setName("btn_"+j+"_"+i);
				btnCase.addActionListener(new PutShipListener());
				btnCase.setBackground(Color.BLUE);
				panGrid.add(btnCase);
				tabBtn[j][i] = btnCase;
			}

		}

		JLabel lblNameShip1 = new JLabel("Bateaux de 1 place");
		lblNameShip1.setBounds(255, 572, 125, 20);
		add(lblNameShip1);
		ImageIcon ico = new ImageIcon("images/scoutboat.png");
		ImageIcon ico2 = new ImageIcon("images/corvette.png");
		ImageIcon ico3 = new ImageIcon("images/destroyer.png");
		ImageIcon ico4 = new ImageIcon("images/battleship.png");

		lblShip1 = new JLabel(ico);
		lblShip1.setEnabled(false);
		lblShip1.setBounds(279, 595, 80, 80);
		add(lblShip1);


		JLabel lblNameShip2 = new JLabel("Bateaux de 2 places");
		lblNameShip2.setBounds(398, 572, 125, 20);
		add(lblNameShip2);

		lblShip2 = new JLabel(ico2);
		lblShip2.setEnabled(false);
		lblShip2.setBounds(420, 595, 80, 80);
		add(lblShip2);


		JLabel lblNameShip3 = new JLabel("Bateaux de 3 places");
		lblNameShip3.setBounds(544, 572, 125, 20);
		add(lblNameShip3);

		lblShip3 = new JLabel(ico3);
		lblShip3.setEnabled(false);
		lblShip3.setBounds(564, 595, 80, 80);
		add(lblShip3);


		JLabel lblNameShip4 = new JLabel("Bateaux de 4 palces");
		lblNameShip4.setBounds(690, 572, 125, 20);
		add(lblNameShip4);

		lblShip4 = new JLabel(ico4);
		lblShip4.setEnabled(false);
		lblShip4.setBounds(703, 595, 80, 80);
		add(lblShip4);
		
		JPanel pnlInfo = infoPan;
		pnlInfo.setBounds(10, 11, 210, 601);
		add(pnlInfo);
		
		JLabel lblEnter = new JLabel("ENTER pour envoyer le message");
		lblEnter.setBounds(35, 661, 166, 14);
		add(lblEnter);
		
		txtMessage = new JTextField();
		txtMessage.setColumns(10);
		txtMessage.setBounds(10, 636, 210, 20);
		txtMessage.addKeyListener(new SendMessageKeyListener());
		add(txtMessage);


	}

	public void setPutingShip(int nbPlaces)
	{
		lblShip1.setEnabled(false);
		lblShip2.setEnabled(false);
		lblShip3.setEnabled(false);
		lblShip4.setEnabled(false);
		switch (nbPlaces) {
		case 1:
			lblShip1.setEnabled(true);
			currentNbPlaceShip = 1;
			break;
		case 2:
			lblShip2.setEnabled(true);
			currentNbPlaceShip = 2;
			break;
		case 3:
			lblShip3.setEnabled(true);
			currentNbPlaceShip = 3;
			break;
		case 4:
			lblShip4.setEnabled(true);
			currentNbPlaceShip = 4;
			break;

		default:
			break;
		}
	}

	public void setPanAlreadyUseCase(Point p)
	{
		tabBtn[p.x][p.y].setBackground(Color.GRAY);
		if (listBtnGreen.contains(tabBtn[p.x][p.y]))
			listBtnGreen.remove(tabBtn[p.x][p.y]);
	}

	public void setPanPossibleCase(Ship currentShip)
	{
		
		Point[] p = currentShip.getCoordonnees();
		int nbAlreadyPut = -1;
		for (Point point : p) 
		{
			if (point != null)
				nbAlreadyPut ++;
		}
		//Je propose les cases autours
		if(nbAlreadyPut == 0)
		{
			cleanGreen();
			int x = p[0].x;
			int y = p[0].y;
			boolean isPossible = isPossibleCol(p[0],p.length);
			isPossible = isPossibleRow(p[0],p.length) || isPossible;
			if (!isPossible)
			{
				setToRed(tabBtn[x][y]);
				currentShip.clear();
				System.out.println("Impossible de cliquer sur cette case !!!");
			}
		}
		//Je propose aux extremitées
		else
		{
			cleanGreen();
			//GERER le plus grand et le plus petit

			Point pFirst = getFirst(p);
			Point pLast = getLast(p);

			int xFirst = pFirst.x;
			int yFirst = pFirst.y;
			int xLast = pLast.x;
			int yLast = pLast.y;

			//Verifie si c'est en ligne 
			if (xFirst == xLast)
			{

				if(yFirst > yLast)
				{
					if (yFirst + 1 < 16)
					{
						setToGreen(tabBtn[xFirst][yFirst+1]);
					}
					if (yLast - 1 >= 0)
					{
						setToGreen(tabBtn[xFirst][yLast-1]);
					}
				}
				else
				{
					if (yLast + 1 < 16)
					{
						setToGreen(tabBtn[xFirst][yLast+1]);
					}
					if (yFirst - 1 >= 0)
					{
						setToGreen(tabBtn[xFirst][yFirst-1]);
					}
				}
			}
			//Ou en colonne
			else
			{
				if(xFirst > xLast)
				{
					if (xFirst + 1 < 16)
					{
						setToGreen(tabBtn[xFirst+1][yFirst]);
					}
					if (xLast - 1 >= 0)
					{
						setToGreen(tabBtn[xLast-1][yFirst]);
					}
				}
				else
				{
					if (xLast + 1 < 16)
					{
						setToGreen(tabBtn[xLast+1][yFirst]);
					}
					if (xFirst - 1 >= 0)
					{
						setToGreen(tabBtn[xFirst-1][yFirst]);
					}
				}
			}

		}
	}
	
	private boolean isPossibleRow(Point p,int nbCase)
	{
		int cptLeft = 0, cptRight = 0;
		
		for(int left = 1 ; left < nbCase ; left++)
		{
			
			if(p.x + left >= NBLIG || tabBtn[p.x+left][p.y].getBackground() == Color.GRAY || tabBtn[p.x+left][p.y].getBackground() == Color.RED)
			{
				break;
			}
			cptLeft++;
		}
		
		for(int right = 1 ; right < nbCase ; right++)
		{
			
			if(p.x - right < 0 || tabBtn[p.x-right][p.y].getBackground() == Color.GRAY || tabBtn[p.x-right][p.y].getBackground() == Color.RED)
			{
				break;
			}
			cptRight++;
		}

		if ((cptLeft + cptRight + 1 ) >= nbCase)
		{
			if(cptLeft >0)
				setToGreen(tabBtn[p.x+1][p.y]);
			if(cptRight >0)
				setToGreen(tabBtn[p.x-1][p.y]);
		}
		
		return (cptLeft + cptRight + 1 ) >= nbCase ;
	}
	
	private boolean isPossibleCol(Point p,int nbCase)
	{
		int  cptTop = 0, cptBot = 0;
		
		for(int top = 1 ; top < nbCase ; top++)
		{
			if(p.y + top >= NBLIG || tabBtn[p.x][p.y + top].getBackground() == Color.GRAY || tabBtn[p.x][p.y+top].getBackground() == Color.RED)
			{
				break;
			}
			cptTop++;
		}
		
		for(int bot = 1 ; bot < nbCase ; bot++)
		{
			
			if(p.y - bot < 0 || tabBtn[p.x][p.y-bot].getBackground() == Color.GRAY || tabBtn[p.x][p.y-bot].getBackground() == Color.RED)
			{
				break;
			}
			cptBot++;
		}
		if ((cptTop + cptBot + 1 ) >= nbCase)
		{
			if(cptTop >0)
				setToGreen(tabBtn[p.x][p.y+1]);
			if(cptBot >0)
				setToGreen(tabBtn[p.x][p.y-1]);
		}
		
		return (cptTop + cptBot + 1 ) >= nbCase ;
	}

	private Point getLast(Point[] tabP) 
	{
		Point result = new Point(-1,-1);
		for (Point point : tabP) 
		{
			if(point != null)
			{
				if(result.x < point.x)
					result.x = point.x;
				if(result.y < point.y)
					result.y = point.y;
			}
			else
				break;
		}

		return result;
	}

	private Point getFirst(Point[] tabP)
	{
		Point result = new Point(100,100);
		for (Point point : tabP) 
		{
			if(point != null)
			{
				if(result.x > point.x)
					result.x = point.x;
				if(result.y > point.y)
					result.y = point.y;
			}
			else
				break;
		}

		return result;
	}

	private void setToGreen(JButton btn)
	{
		if (btn.getBackground() != Color.GRAY && btn.getBackground() != Color.RED)
		{
			btn.setBackground(Color.GREEN);
			listBtnGreen.add(btn);
		}
	}
	
	private void setToRed(JButton btn)
	{
		btn.setBackground(Color.RED);
		listBtnRed.add(btn);
		
	}
	
	public void cleanGreen()
	{
		for (JButton tmpBtn : listBtnGreen) {
			tmpBtn.setBackground(Color.BLUE);
		}
		listBtnGreen.clear();
	}

	public boolean listGreenCaseIsEmpty()
	{
		return listBtnGreen.isEmpty();
	}
	
	public String getMessage()
	{
		String currentMessage = txtMessage.getText();
		txtMessage.setText("");
		return currentMessage;
	}

	public int getNbPlaceShip() {
		return currentNbPlaceShip;
	}

	@Override
	public void sendMessage(String msg) 
	{
		InfoPan.addInfo(msg);
	}
}
