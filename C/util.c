#ifndef includeGlobal
#include "includeGlobal.c"
#endif

/*
 * Converti une lettre en entier selon le sujet de bataille navale.
 * Ici on n'a besoin que convertir que les lettres de A à P. Les autres sont fausses.
 */
int charToInt(char lettre) {
	if (lettre == 'A') return 0;
	if (lettre == 'B') return 1;
	if (lettre == 'C') return 2;
	if (lettre == 'D') return 3;
	if (lettre == 'E') return 4;
	if (lettre == 'F') return 5;
	if (lettre == 'G') return 6;
	if (lettre == 'H') return 7;
	if (lettre == 'I') return 8;
	if (lettre == 'J') return 9;
	if (lettre == 'K') return 10;
	if (lettre == 'L') return 11;
	if (lettre == 'M') return 12;
	if (lettre == 'N') return 13;
	if (lettre == 'O') return 14;
	if (lettre == 'P') return 15;
	return -1;
}

/*
 * Converti un entier en une lettre selon le sujet de bataille navale.
 * On ne devrait pas avoir d'autre entier que de 0 à 15.
 */
char* intToChar(int coordonnee) {
	if (coordonnee == 0) return "A";
	if (coordonnee == 1) return "B";
	if (coordonnee == 2) return "C";
	if (coordonnee == 3) return "D";
	if (coordonnee == 4) return "E";
	if (coordonnee == 5) return "F";
	if (coordonnee == 6) return "G";
	if (coordonnee == 7) return "H";
	if (coordonnee == 8) return "I";
	if (coordonnee == 9) return "J";
	if (coordonnee == 10) return "K";
	if (coordonnee == 11) return "L";
	if (coordonnee == 12) return "M";
	if (coordonnee == 13) return "N";
	if (coordonnee == 14) return "O";
	if (coordonnee == 15) return "P";
	return "Z";
}

/*
 * Vérifie que l'entier demandé est bien entre 0 et 15.
 */
int verifCaseInMap (int casee) {
	return (0 <= casee && casee <= 15);
}

/*
 * Tourne en boucle et écoute seulement les messages TALK.
 */
void ecouterTalk (void* p) {
	PLAYER *player = (PLAYER *) p;
	char buffer[1024];
	int n;
	while (1) {
		memset(buffer, 0, sizeof(buffer));
		ft_thread_unlink();
		n = recv(player->socket, buffer, (sizeof(buffer) - 1), 0);
		ft_thread_link(player->partie->ordonnanceur);
		if (!checkMessageInGame(n, buffer, player->socket, player->partie, player->nom)) return;
		// on ne récupère que les TALK
		if (!strncmp(buffer, "TALK/", 5)) {
			talk(player->partie, player, buffer);
		}
	}
}

/*
 * Tourne en boucle et écoute seulement les messages TALK pour un spectateur.
 */
void ecouterTalkSpectator (void *spec) {
	SPECTATOR *spectator = (SPECTATOR *) spec;
	char buffer[1024];
	int n;
	while (1) {
		memset(buffer, 0, sizeof(buffer));
		ft_thread_unlink();
		n = recv(spectator->socket, buffer, (sizeof(buffer) - 1), 0);
		ft_thread_link(spectator->partie->ordonnanceur);
		if (!checkMessageInGame(n, buffer, spectator->socket, spectator->partie, "(spectator)")) return;
		// on ne récupère que les TALK ou BYE à la fin
		if (!strncmp(buffer, "TALK/", 5)) {
			talkSpectator(spectator->partie, buffer);
		}
		else if (!strncmp(buffer, "BYE/", 4)) {
			killSpectator(spectator->partie, spectator->socket);
			return;
		}
	}
}
