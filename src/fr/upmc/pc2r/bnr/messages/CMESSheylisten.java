package fr.upmc.pc2r.bnr.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.upmc.pc2r.bnr.protocole.EProtocole;

public class CMESSheylisten extends AbstractMessage
{

	private String nameSender;
	private String message;
	public CMESSheylisten(EProtocole p, String nameSender, String message) 
	{
		super(p);
		Pattern datePatt = Pattern.compile("[A-Z]*[a-z]*/[^/]*/(.*)/");
		Matcher m = datePatt.matcher(message);

		if (m.matches())
		{
			this.message = m.group(1);
			this.message = this.message.replace( "\\/","/");
			this.message = this.message.replace("\\\\","\\");
			this.nameSender = nameSender;
		}
		else
			message = "";
	}

	public String getNameSender()
	{
		return nameSender;
	}

	public String getMessage()
	{
		return message;
	}

}
