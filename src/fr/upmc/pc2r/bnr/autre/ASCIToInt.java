package fr.upmc.pc2r.bnr.autre;

public class ASCIToInt {
	/***
	 * Methode permettant d'obtenir la lettre équivalent a la l'entier passé en parametre.
	 * @param value lettre qui doit etre converti en entier.
	 * @return le int équivalent a la lettre passé en parametre.
	 */
	public static int getIntwithASCI(String value)
	{
		int result;
		switch (value) {
		case "A":
			result = 0;
			break;
		case "B":
			result = 1;
			break;
		case "C":
			result = 2;
			break;
		case "D":
			result = 3;
			break;
		case "E":
			result = 4;
			break;
		case "F":
			result = 5;
			break;
		case "G":
			result = 6;
			break;
		case "H":
			result = 7;
			break;
		case "I":
			result = 8;
			break;
		case "J":
			result = 9;
			break;
		case "K":
			result = 10;
			break;
		case "L":
			result = 11;
			break;
		case "M":
			result = 12;
			break;
		case "N":
			result = 13;
			break;
		case "O":
			result = 14;
			break;
		case "P":
			result = 15;
			break;
		default:
			result = -1;
			break;
		}
		return result;
	}

}
