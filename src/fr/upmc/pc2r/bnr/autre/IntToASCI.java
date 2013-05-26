package fr.upmc.pc2r.bnr.autre;

public class IntToASCI {
	
	/***
	 * Methode permettant d'obtenir l'entier équivalent a la lettre passé en parametre.
	 * @param value entier qui doit etre converti en lettre.
	 * @return la lettre équivalent au int passé en parametre.
	 */
	public static String getASCIIwithInt(int value)
	{
		String result;
		switch (value) {
		case 0:
			result = "A";
			break;
		case 1:
			result = "B";
			break;
		case 2:
			result = "C";
			break;
		case 3:
			result = "D";
			break;
		case 4:
			result = "E";
			break;
		case 5:
			result = "F";
			break;
		case 6:
			result = "G";
			break;
		case 7:
			result = "H";
			break;
		case 8:
			result = "I";
			break;
		case 9:
			result = "J";
			break;
		case 10:
			result = "K";
			break;
		case 11:
			result = "L";
			break;
		case 12:
			result = "M";
			break;
		case 13:
			result = "N";
			break;
		case 14:
			result = "O";
			break;
		case 15:
			result = "P";
			break;
		default:
			result = "error";
			break;
		}
		return result;
	}

}
