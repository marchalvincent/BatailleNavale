package fr.upmc.pc2r.bnr.vue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoPan extends JPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JTextArea textArea;
	private static String info;

	/**
	 * Create the panel.
	 */
	public InfoPan() 
	{
		setLayout(null);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane zoneScrolable = new JScrollPane(textArea);
		textArea.setBounds(0, 0, 210, 601);
		zoneScrolable.setBounds(0, 0, 210, 601);
		add(zoneScrolable);
		info = new String();
		textArea.setText(info.toString());

	}

	public static void addInfo(String text)
	{
		Format formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("hh:mm:ss");
		
		info = "[" + formatter.format(date) + "] : " + text + "\n" + info;
		textArea.setText(info.toString());
	}
}
