#ifndef includeGlobal
#include "includeGlobal.c"
#endif
#include "fthread.h"

/*
 * Ajoute un nouveau joueur a une partie.
 * Créer la partie si le flag newGame est a 1.
 */
char* addPlayerToGame (GAME* g, SOCKET soc, char* playerName) {

	PLAYER *p = (PLAYER*) malloc(sizeof(PLAYER));
	p->socket = soc;
	p->nom = malloc(sizeof(char)*(strlen(playerName) + 1));
	memset(p->nom, 0, sizeof(p->nom));
	strcpy(p->nom, playerName);
	p->suivant = NULL;
	p->partie = g;
	int i, j;
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			p->carte[i][j] = 0;
		}
	}
	p->ordrePassage = 0;
	// pour définir la 1ère position aléatoire du drone on utilise rand()
	srand(time(NULL));
	p->position[0] = rand()%16;
	p->position[1] = rand()%16;
	p->mort = 0;

	// si c'est le premier joueur, pas de problème
	if (g->nbJoueurs == 0) {
		g->joueurs = p;
	}
	// sinon on parcours la liste chainée n fois et on ajoute le joueur a la fin
	else {
		PLAYER *temp = g->joueurs, *dernier;
		do {
			dernier = temp;
			// au passage on vérifie que le nom n'existe pas déjà
			if (!strcmp(temp->nom, p->nom)) {
				// si oui, on ajoute par exemple le numéro du joueur
				char buf[1];
				sprintf(buf,"%d", (g->nbJoueurs + 1));
				strcat(p->nom, buf);
			}
			temp = temp->suivant;
		} while (temp != NULL);
		// on ajoute le joueur (p) à la fin de la liste chainée
		dernier->suivant = p;
	}
	g->nbJoueurs ++;

	// on lance le thread qui va écouter en boucle les message TALK
	p->threadTalk = ft_thread_create(g->ordonnanceur, ecouterTalk, NULL, (void *)p);

	// on envoie un message pour dire qu'un nouveau joueur est arrivé
	char *nomEchappe = echappementCaracteres(p->nom);
	int s = 21 + strlen(nomEchappe);
	char messageServeur[s];
	memset(messageServeur, 0, sizeof(messageServeur));
	sprintf(messageServeur, "%s se join à la partie.", nomEchappe);
	talkServeur(g, messageServeur);
	free(nomEchappe);

	// on renvoie le nouveau nom au cas ou on l'a changé, pour que le serveur
	// puisse répondre le WELCOME avec le bon nom
	return p->nom;
}

/*
 * Supprime un joueur d'une partie. Par exemple dans le cas où la connexion est
 * interrompue.
 */
void killPlayer(GAME *game, char *player, int finPartie, int garderSocket) {

	printf("Suppression du joueur %s de la partie n°%d -->", player, game->numeroPartie);
	int suppression = 0;
	PLAYER *cursor = game->joueurs;
	// pour éviter les vieux bugs pourris, on test si c'est null
	if (cursor == NULL) return;

	// si c'est le premier joueur qu'il faut supprimer, on le free
	if (!strcmp(cursor->nom, player)) {
		game->joueurs = cursor->suivant;
		freePlayer(cursor, garderSocket);
		suppression = 1;
	}
	else {
		PLAYER *precedent;
		do {
			precedent = cursor;
			cursor = cursor->suivant;
			// si on trouve le nom a supprimer, suppression d'un element d'une liste chainée banale
			if (cursor != NULL && !strcmp(cursor->nom, player)) {
				precedent->suivant = cursor->suivant;
				freePlayer(cursor, garderSocket);
				suppression = 1;
			}
			// la condition d'arret est lorsqu'on a trouvé le joueur, ou lorsqu'on a parcouru toute la liste chainée
		} while (!suppression && cursor != NULL);
	}

	// si on a effectué une suppression
	if (suppression) {
		puts("OK");
		game->nbJoueurs --;
		if (!finPartie) {
			checkHasWinnerAndNotify(game);
		}

		// si la partie n'a plus de joueur et de spectateurs, on la supprime
		if (game->joueurs == NULL && game->spectateurs == NULL) {
			printf("On supprime la partie n°%d\n", game->numeroPartie);
			removeGame(game);
		}
	}
	else {
		puts("INTROUVABLE");
	}
}

/*
 * Supprime et libère toute mémoire allouée pour un PLAYER
 */
void freePlayer(PLAYER *player, int garderSocket) {
	// on close la socket, kill les threads et libère la mémoire du nom et de la structure
	if (!garderSocket) closesocket(player->socket);
	pthread_cancel(ft_pthread(player->threadTalk));
	free(player->nom);
	free(player);
}

/*
 * Créer le message PLAYERS et lance les thread qui placent les bateaux.
 */
void startLastGame(GAME *game, int numPartie) {

	// on commence en 1er par killer les threads d'écoute TALK qui fesaient patienter les joueurs
	PLAYER *cursor = game->joueurs;
	do {
		pthread_cancel(ft_pthread(cursor->threadTalk));
		cursor = cursor->suivant;
	} while (cursor != NULL);


	// on compte le nombre de caractère dont on va avoir besoin
	int i = 0, nbCaracterePlayers = 9; // 9 pour le "PLAYERS/...\n"
	cursor = game->joueurs;
	do {
		nbCaracterePlayers += strlen(cursor->nom); // le pseudo du joueur
		nbCaracterePlayers += compterEchappement(cursor->nom); // pour les échappements
		nbCaracterePlayers += 1; // pour le / a la fin du pseudo
		cursor = cursor->suivant;
	} while (cursor != NULL);

	// on construit le message PLAYERS
	char playersMessage[nbCaracterePlayers];
	memset(playersMessage, 0, sizeof(playersMessage));
	strcat(playersMessage, "PLAYERS/");
	cursor = game->joueurs;
	do {
		i++;
		printf("Joueur %d partie %d : %s\n", i, game->numeroPartie, cursor->nom);
		char *nom = echappementCaracteres(cursor->nom);
		strcat(playersMessage, nom);
		strcat(playersMessage, "/");
		cursor = cursor->suivant;
		free(nom);
	} while (cursor != NULL);
	strcat(playersMessage, "\n");

	printf("------//!\\\\ Lancement de la partie n°%d\n", game->numeroPartie);
	// parcours de la liste chainée pour l'envoie des messages PLAYERS
	cursor = game->joueurs;
	do {
		// envoie du message
		if (!sendMessage(game, cursor->socket, playersMessage, cursor->nom)) return;

		// lancement des threads qui vont se détacher de l'ordonnanceur pour le placement des bateaux
		printf("Lancement du thread PlacementShip pour le joueur '%s'\n", cursor->nom);
		// la fonction placementShip est dans le fichier putShip.c
		ft_thread_create(game->ordonnanceur, placementShip, NULL, (void*) cursor);
		cursor = cursor->suivant;
	} while (cursor != NULL);

	// on ajoute le message PLAYER à la liste de tous les messages pour les spectateurs retardataires
	addMessageToGame(game, playersMessage, 1);

}

/*
 * Vérifie si la partie est finie, et envoie un message aux clients en fonction de
 * la situation.
 */
int checkHasWinnerAndNotify(GAME *game) {
	int hw = hasWinner(game);
	if (hw == -1) {
		char drawGame[10];
		memset(drawGame, 0, sizeof(drawGame));
		strcat(drawGame, "DRAWGAME/\n");
		sendToAll(game, drawGame);
		addMessageToGame(game, drawGame, 0);
	}
	else if (hw == 1) {
		sendAWinnerIs(game);
		misAJourBD(game);
	}
	return hw;
}

/*
 * Renvoie 1 si la partie possède un gagnant, et remplie le champ gagnant de la
 * struct GAME avec le nom du joueur.
 * Renvoie -1 si il n'y a aucun gagnant, tout le monde a perdu.
 * Revoie 0 si personne n'a gagné mais qu'il reste encore des joueurs.
 */
int hasWinner(GAME *game) {

	// s'il n'y a plus aucun joueur dans la partie
	if (game->joueurs == NULL) return -1;

	// si on est en mode test solo on ne met pas fin à la partie
	if (solo) return 0;

	// on parcours tous les joueurs
	int findShip, i, j, nbInGame = 0, allZero;
	PLAYER *cursor = game->joueurs;
	char *winnerName;

	/* On vérifie quand même qu'il y a plus (+) d'un joueur (dans le cas où les
	 * autres auraient quitté la partie.
	 */
	if (cursor->suivant == NULL) {
		game->gagnant = cursor->nom;
		return 1;
	}
	// on parcours tous les joueurs et on compte combien ont au moins 1 case pleine
	do {
		findShip = 0;
		allZero = 1;
		for (i = 0; i < 16 && !findShip; i++) {
			for (j = 0; j < 16 && !findShip; j++) {
				if (cursor->carte[i][j] == 1) {
					nbInGame++;
					findShip = 1;
					winnerName = cursor->nom;
				}
				if (cursor->carte[i][j] != 0) {
					allZero = 0;
				}
			}
		}

		/*
		 * un joueur qui a que des zéro sur sa map est un joueur qui n'a posé
		 * aucun bateau. Il n'est donc pas mort !
		 */
		if (allZero) {
			nbInGame++;
			winnerName = cursor->nom;
		}

		cursor = cursor->suivant;
	} while (cursor != NULL);

	// la partie est finie mais il n'y a aucun gagnant
	if (nbInGame == 0) {
		return -1;
	}
	else if (nbInGame == 1) {
		game->gagnant = winnerName;
		return 1;
	}
	else {
		return 0;
	}
}

/*
 * Représente le thread qui écoutera et communiquera avec le client tout au long d'une partie
 */
void JouerPartie(void* p) {
	PLAYER *player = (PLAYER *) p;
	GAME * game = player->partie;
	char buffer[1024];
	int hasWin = 0, firstTurn = 1, n;
	do {
		/*
		 * Comme au premier tour tous les threads arrivent en même temps, on a besoin de
		 * savoir qui doit jouer, d'où ce test. Les prochains messages YOURTURN seront
		 * envoyé juste après une ACTION d'un autre joueur avec la commande sendYourTurnToNext.
		 */
		if (firstTurn && game->numeroQuiJoue == player->ordrePassage) {
			sendYourTurn(player);
			firstTurn = 0;
		}

		// on se délie du scheduler pour pouvoir écouter les messages du client
		ft_thread_unlink();

		// on se met en écoute sur la socket cliente. Ici on peut recevoir ACTION, TALK, BYE ou PLAYAGAIN
		memset(buffer, 0, sizeof(buffer));
		n = recv(player->socket, buffer, (sizeof(buffer) - 1), 0);
		if (!checkMessageInGame(n, buffer, player->socket, player->partie, player->nom)) return;

		// puis on se relie au scheduler pour pouvoir agir en fonction du message
		ft_thread_link(game->ordonnanceur);

		// Parsing du message
		if (!strncmp(buffer, "TALK/", 5)) {
			talk(game, player, buffer);
		}
		else if (!strncmp(buffer, "ACTION/", 7)) {
			// si c'est une action, il faut tester que c'est le tour du joueur au cas ou
			if (player->ordrePassage == game->numeroQuiJoue) {
				action(player, buffer);
				addActionMessageToGame(game, player);
				// checkHasWinnerAndNotify renvoie 0 s'il y a encore au moins 2 personnes en jeu
				hasWin = checkHasWinnerAndNotify(game);
				/*
				 * Comme la fonction action a incrémenté le "numeroQuiJoue", on peut
				 * envoyer le YOURTURN au prochain joueur.
				 */
				if (!hasWin) {
					sendYourTurnToNext(game);
				}
			}
			else {
				printf("//!!\\\\ ATTENTION, le joueur %s essaye de jouer qu'alors "
						"ce n'est pas son tour !\n", player->nom);
			}
		}
		/*
		 * Si ce n'est pas le thread du client qui à fini la partie, on peut recevoir des
		 * messages BYE ou PLAYAGAIN à ce moment là. Dans ce cas il faut arréter
		 * directement le thread puisque la partie est finie.
		 */
		else {
			if (!byeOrPlay(buffer, game, player)) {
				puts("------NOT A ACTION OR TALK MESSAGE.");
				killPlayer(player->partie, player->nom, 0, 0);
			}
			return;
		}
	} while (!hasWin);

	// A partir d'ici on ne devrait parser que le message du joueur qui a fini la partie.
	//on attend le message BYE ou PLAYAGAIN.
	char reponse[12];
	memset(reponse, 0, sizeof(reponse));
	n = recv(player->socket, reponse, (sizeof(reponse) - 1), 0);
	if (!checkMessageInGame(n, reponse, player->socket, player->partie, player->nom)) return;

	if (!byeOrPlay(reponse, game, player)) {
		puts("------NOT A BYE OR PLAYAGAIN MESSAGE.");
		killPlayer(player->partie, player->nom, 0, 0);
	}
}

/*
 * Fonction qui factorise du code. Fait les traitements du BYE ou PLAYAGAIN.
 */
int byeOrPlay(char *buffer, GAME *game, PLAYER *player) {
	if (!strncmp(buffer, "BYE/", 4)) {
		printf("Bye bye %s, merci d'avoir joué avec nous.\n", player->nom);
		byeBye(game, player, 0);
		return 1;
	}
	else if (!strncmp(buffer, "PLAYAGAIN/", 10)) {
		printf("Le joueur %s en redemande ! C'est partit !\n", player->nom);
		// on ajoute le joueur dans une autre partie
		SOCKET s = player->socket;
		char *name = malloc(sizeof(char)*strlen(player->nom));
		memset(name, 0, sizeof(name));
		strcpy(name, player->nom);
		byeBye(game, player, 1);
		addNewPlayer(s, name);
		return 1;
	}
	return 0;
}

/*
 * Envoie le message YOURTURN à un joueur.
 */
void sendYourTurn(PLAYER *p) {
	// on récupère le nombre d'action que peut faire le joueur puis on le convertit en char*
	int nbaction = getNbAction(p->carte);
	char charAction[2];
	sprintf(charAction, "%d", nbaction);

	// on convertit x en chaine de caractère
	char charX[2];
	sprintf(charX, "%d", p->position[0]);

	// on convertit y en chaine de caractère
	char *charY = intToChar(p->position[1]);

	char yourTurn[18];
	memset(yourTurn, 0, sizeof(yourTurn));
	sprintf(yourTurn, "YOURTURN/%s/%s/%s/\n", charX, charY, charAction);
	if (!sendMessage(p->partie, p->socket, yourTurn, p->nom)) return;
}

/*
 * Cherche le joueur qui doit jouer et lui envoie un message YOURTURN.
 */
void sendYourTurnToNext(GAME *game) {
	if (game->joueurs == NULL) return;
	PLAYER *cursor = game->joueurs;
	do {
		if (cursor->ordrePassage == game->numeroQuiJoue) {
			// si le joueur est mort, on incrémente le numeroQuiJoue de la partie
			if (cursor->mort == 1) {
				game->numeroQuiJoue = (game->numeroQuiJoue + 1) % game->nbJoueurs;
			}
			// sinon on lui demande de jouer
			else {
				printf("send yourturn a %s\n", cursor->nom);
				sendYourTurn(cursor);
			}
		}
		cursor = cursor->suivant;
	} while (cursor != NULL);
}

/*
 * Renvoie le nombre d'action possible pour une carte donnée
 */
int getNbAction(int map[16][16]) {

	int countShip = 0, i, j;
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			if (map[i][j] == 1) countShip++;
		}
	}
	// on renvoie le nombre de cases totale moins le nombre de cases que le joueur possède
	return (BATEAU1 + BATEAU2*2 + BATEAU3*3 + BATEAU4*4 + 1 - countShip);
}

/*
 * fait le traitement nécessaire pour traiter un message ACTION.
 */
void action(PLAYER *player, char *message) {

	// cet entier sert a vérifier que le joueur ne joue pas plus de coup que voulu
	int nbaction = getNbAction(player->carte);
	GAME *game = player->partie;
	int i, taille = strlen(message), continuer = 1;
	char action;
	// on commence au 7e caractère qui correspond à la première action
	for (i = 7; i < taille && nbaction && continuer; i++) {
		action = message[i];

		// U -> y+1, D -> y-1, L -> x-1, R -> x+1
		if (action == 'U') player->position[1]++;
		else if (action == 'D') player->position[1]--;
		else if (action == 'L') player->position[0]--;
		else if (action == 'R') player->position[0]++;
		else if (action == 'E') {
			tirer(player);
		}
		// tous les autres types (dont le '\n' et le '/') de caractère on passe.
		else continue;

		nbaction --;
		// ajouter une condition pour le cheat code
		if (0) {
			player->position[0] = player->position[0] % 16;
			player->position[1] = player->position[1] % 16;
		}

		// on test si le joueur est sorti de la map... ce qui ne DEVRAIT pas être le cas
		if (!verifCaseInMap(player->position[0])) {
			// on lui fait passer son tour parce qu'il a cheaté, et on annule son coup
			continuer = 0;
			if (player->position[0] > 15) player->position[0] = 15;
			else if (player->position[0] < 0) player->position[0] = 0;
		}
		else if (!verifCaseInMap(player->position[1])) {
			continuer = 0;
			if (player->position[1] > 15) player->position[1] = 15;
			else if (player->position[1] < 0) player->position[1] = 0;
		}
	}

	if (!continuer) {
		printf("//!!\\\\ ATTENTION, le joueur %s essaye de cheater en sortant de "
				"la map !\n", player->nom);
	}

	// on incrémente le numéro du joueur qui doit jouer
	game->numeroQuiJoue = (game->numeroQuiJoue + 1) % game->nbJoueurs;
}

/*
 * Effectue un tire de laser par le joueur en paramètre et envoie les message correspondants.
 */
void tirer(PLAYER *player) {
	// on parcours tous les joueurs
	int touche = 0, x = player->position[0], y = player->position[1];
	PLAYER *cursor = player->partie->joueurs;
	do {
		// si le curseur à son flag à 1 ou 2 (donc si le joueur a été touché)
		if (cursor->carte[x][y]) {
			if (touche == 0) {
				sendMessageWithCoordinates(player, "TOUCHE", x, y);
				touche = 1;
			}
			sendMessageWithCoordinates(cursor, "OUCH", x, y);
			addOuchMessageToGame(player->partie, cursor->nom, x, y);

			/*
			 * Une fois le message OUCH envoyé, il faut vérifier que le joueur est
			 * toujours dans la partie. Si son flag est à 1 on le met à 2 et il faut
			 * vérifier qu'il lui reste encore au moins 1 bateau.
			 */
			if (cursor->carte[x][y] == 1) {
				cursor->carte[x][y] = 2;
				checkPlayerInGame(cursor);
			}
		}
		cursor = cursor->suivant;
	} while (cursor != NULL);

	if (touche == 0) sendMessageWithCoordinates(player, "MISS", x, y);
}

/*
 * Vérifie qu'un joueur est toujours dans la partie.
 * Si ce n'est pas le cas, envoie un message DEATH à tous les joueurs.
 */
void checkPlayerInGame(PLAYER *p) {

	int i, j;
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			// dès qu'on rencontre une case avec le flag 1, le joueur est toujours dans la partie.
			if (p->carte[i][j] == 1) {
				return;
			}
		}
	}

	/*
	 * ici on n'a rencontré aucun flag à 1, c'est a dire que le joueur est mort.
	 * Le flag mort devient 1 pour qu'il ne puisse plus jouer.
	 */
	p->mort = 1;

	// on construit le message DEATH, puis on l'envoie aux joueurs de la partie
	char *nom = echappementCaracteres(p->nom);
	int sizeMessage = 8 + strlen(nom);
	char message[sizeMessage];
	memset(message, 0, sizeof(message));
	sprintf(message, "DEATH/%s/\n", nom);
	sendToAll(p->partie, message);
	free(nom);
}

/*
 * Envoie un message "mess" pour la position (x, y) au joueur en paramètre.
 */
void sendMessageWithCoordinates(PLAYER *p, char *mess, int x, int y) {
	// on convertit les coordonnées
	char charX[2];
	sprintf(charX, "%d", x);

	char *charY;
	charY = intToChar(y);

	int sizeMessage = strlen(mess) + 7;
	char message[sizeMessage];
	memset(message, 0, sizeof(message));
	sprintf(message, "%s/%s/%s/\n", mess, charX, charY);
	if (!sendMessage(p->partie, p->socket, message, p->nom)) return;
}

/*
 * Envoie a tous les joueurs de la partie le message AWINNERIS.
 */
void sendAWinnerIs(GAME *game) {
	char *nomGagnant = echappementCaracteres(game->gagnant);
	int sizeMessage = 12 + strlen(nomGagnant);
	char message[sizeMessage];
	memset(message, 0, sizeof(message));
	sprintf(message, "AWINNERIS/%s/\n", nomGagnant);
	sendToAll(game, message);
	addMessageToGame(game, message, 0);
	free(nomGagnant);
}

/*
 * Supprime un joueur et libère la mémoire de la partie s'il y en a plus.
 */
void byeBye(GAME *game, PLAYER *player, int garderSocket) {
	killPlayer(game, player->nom, 1, garderSocket);
}

/*
 * Envoie un message à tous les spectateurs et l'ajoute à la GAME pour ceux qui
 * arriveront en retard.
 */
void addMessageToGame(GAME *game, char *mess, int sendSpectator) {

	if (sendSpectator) {
		// on parcours la liste des spectateurs pour l'envoie du message
		SPECTATOR *spectator = game->spectateurs;
		while (spectator != NULL) {
			if (!sendMessage(game, spectator->socket, mess, "(spectator)")) return;
			spectator = spectator->suivant;
		}
	}

	// on construit la structure pour l'ajouter à la GAME
	MESSAGE *message = malloc(sizeof(MESSAGE));
	message->message = malloc(sizeof(char)*strlen(mess));
	memset(message->message, 0, sizeof(message->message));
	strcpy(message->message, mess);
	message->suivant = NULL;

	// on ajoute le message à la fin
	if (game->messages == NULL) {
		game->messages = message;
		game->dernierMessage = message;
	}
	else {
		game->dernierMessage->suivant = message;
		game->dernierMessage = message;
	}
}

/*
 * Envoie aux spectateurs et ajoute un message PUTSHIP à la partie
 */
void addPutShipMessageToGame(GAME *game, char *playerName, char *buffer) {

	// on avance le pointer de buffer pour ne garder que les coordonnées
	buffer = buffer + 7;
	char *nom = echappementCaracteres(playerName);

	int size = 11 + strlen(nom) + strlen(buffer);
	char playerShip[size];
	memset(playerShip, 0, sizeof(playerShip));
	sprintf(playerShip, "PLAYERSHIP/%s%s", nom, buffer);

	addMessageToGame(game, playerShip, 1);
	free(nom);
}

/*
 * Envoie aux spectateurs et ajoute un message MOVE à la partie
 */
void addActionMessageToGame(GAME *game, PLAYER *player) {

	char *nom = echappementCaracteres(player->nom);
	// on convertit x en chaine de caractère
	char charX[2];
	sprintf(charX, "%d", player->position[0]);
	// on convertit y en chaine de caractère
	char *charY = intToChar(player->position[1]);

	int size = 18 + strlen(nom);
	char playerMove[size];
	memset(playerMove, 0, sizeof(playerMove));
	sprintf(playerMove, "PLAYERMOVE/%s/%s/%s/\n", nom, charX, charY);

	addMessageToGame(game, playerMove, 1);
	free(nom);
}

/*
 * Envoie aux spectateurs et ajoute un message OUCH à la partie
 */
void addOuchMessageToGame(GAME *game, char *playerName, int x, int y) {

	char *nom = echappementCaracteres(playerName);
	// on convertit x en chaine de caractère
	char charX[2];
	sprintf(charX, "%d", x);
	// on convertit y en chaine de caractère
	char *charY = intToChar(y);

	int size = 18 + strlen(nom);
	char playerOuch[size];
	memset(playerOuch, 0, sizeof(playerOuch));
	sprintf(playerOuch, "PLAYEROUCH/%s/%s/%s/\n", nom, charX, charY);

	addMessageToGame(game, playerOuch, 1);
	free(nom);
}
