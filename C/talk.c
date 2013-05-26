#ifndef includeGlobal
#include "includeGlobal.c"
#endif

/*
 * Envoie un message HEYLISTEN a tous les joueurs de la partie sous le nom "(serveur)"
 */
void messageServeur(GAME *game, char* message) {

	char *mess = echappementCaracteres(message);
	talkServeur(game, mess);
	free(mess);
}

/*
 * Récupère un message TALK et envoie a tout le monde le HEYLISTEN
 */
void talk(GAME *game, PLAYER *player, char *buffer) {

	char *message = getMessageOfTalk(buffer);
	char *nom = echappementCaracteres(player->nom);
	talkWithName(game, message, nom);
	// on libère la mémoire, on a fait un malloc dans la fonction getMessageOfTalk()
	free(message);
	free(nom);
}

/*
 * Récupère un message TALK d'un spectateur et l'envoie a tout le monde
 */
void talkSpectator(GAME *game, char *buffer) {
	char *message = getMessageOfTalk(buffer);
	talkWithName(game, message, "(spectator)");
	// on libère la mémoire, on a fait un malloc dans la fonction getMessageOfTalk()
	free(message);
}

/*
 * Envoie un message a tout le monde de la part du serveur
 */
void talkServeur(GAME *game, char *buffer) {
	talkWithName(game, buffer, "(serveur)");
}

/*
 * Envoie un message a tout le monde avec un nom spécifié en paramètre
 */
void talkWithName(GAME *game, char *buffer, char *name) {

	if (game == NULL || (game->joueurs == NULL && game->spectateurs == NULL)) return;
	int sizelisten = 13 + strlen(buffer) + strlen(name);
	char heyListen[sizelisten];
	memset(heyListen, 0, sizeof(heyListen));
	sprintf(heyListen, "HEYLISTEN/%s/%s/\n", name, buffer);
	sendToAll(game, heyListen);
}

/*
 * Récupère le contenu d'un message TALK
 */
char *getMessageOfTalk(char *buffer) {

	// TALK/salut a tous/
	int i, j = 0, size = strlen(buffer);
	int sizeMessage = size - 7;
	char *retour = malloc(sizeof(char) * sizeMessage);
	memset(retour, 0, sizeof(retour));
	for (i = 5; i < (size - 2); i++) {
		retour[j] = buffer[i];
		j++;
	}
	return retour;
}

/*
 * Échappe tous les / et \ de la chaine de caractère passée en paramètre.
 */
char* echappementCaracteres(char *message) {
	int sizeRetour = strlen(message) + compterEchappement(message);
	char *retour = malloc(sizeof(char) * sizeRetour);
	memset(retour, 0, sizeof(retour));
	int i, j;
	for (i = 0, j = 0; i < strlen(message); i++, j++) {
		// si on a un / ou un \ il faut rajouter un \ .
		if (message[i] == '/' || message[i] == '\\') {
			retour[j] = '\\';
			j++;
		}
		retour[j] = message[i];
	}
	return retour;
}

/*
 * Renvoie le nombre d'échappements à faire
 */
int compterEchappement(char *message) {
	int i = 0;
	char *pch = strchr(message,'/');
	while (pch != NULL) {
		i++;
		pch = strchr(pch+1,'/');
	}
	pch = strchr(message,'\\');
	while (pch != NULL) {
		i++;
		pch = strchr(pch+1,'\\');
	}
	return i;
}

/*
 * Envoie un message a tous les joueurs d'une partie.
 */
void sendToAll(GAME *game, char *message) {
	// on envoie d'abord aux joueurs
	PLAYER *cursor = game->joueurs;
	if (cursor != NULL) {
		// cette variable sert dans la gestion des erreurs. Cf : gros paté de commentaire suivant.
		PLAYER *suivant = cursor->suivant;
		do {
			/*
			 * Si on a bien envoyé le message, on déplace le curseur.
			 * Sinon, si on n'a pas réussit, le code fait qu'on supprime le joueur de la partie.
			 * La variable cursor vaut NULL dans le else, (Cf. suppresion d'un élément
			 * d'une liste chainée), il faut donc passer directement au suivant grâce au
			 * pointeur "suivant".
			 */
			if (sendMessage(cursor->partie, cursor->socket, message, cursor->nom)) {
				cursor = cursor->suivant;
			}
			else {
				cursor = suivant;
			}
			// si la variable cursor n'est pas nulle on met a jour le suivant
			if (cursor != NULL) suivant = cursor->suivant;
		} while (cursor != NULL);
	}

	// puis on envoie aux spectateurs
	SPECTATOR *cursorSpec = game->spectateurs;
	if (cursorSpec != NULL) {
		// cette variable sert dans la gestion des erreurs. Cf : gros paté de commentaire suivant.
		SPECTATOR *suivant = cursorSpec->suivant;
		do {
			/*
			 * Si on a bien envoyé le message, on déplace le curseur.
			 * Sinon, si on n'a pas réussit, le code fait qu'on supprime le joueur de la partie.
			 * La variable cursor vaut NULL dans le else, (Cf. suppresion d'un élément
			 * d'une liste chainée), il faut donc passer directement au suivant grâce au
			 * pointeur "suivant".
			 */
			if (sendMessage(cursorSpec->partie, cursorSpec->socket, message, "(spectator)")) {
				cursorSpec = cursorSpec->suivant;
			}
			else {
				cursorSpec = suivant;
			}
			// si la variable cursor n'est pas nulle on met a jour le suivant
			if (cursorSpec != NULL) suivant = cursorSpec->suivant;
		} while (cursorSpec != NULL);
	}
}
