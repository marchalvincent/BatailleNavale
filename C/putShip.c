#ifndef includeGlobal
#include "includeGlobal.c"
#endif

/*
 * Représente un thread qui gère les placements des bateaux d'un joueur.
 */
void placementShip(void* args) {
	PLAYER *player = (PLAYER*) args;
	GAME *game = player->partie;

	/*
	 * En fait le placement des bateaux se fait indépendamment de l'ordonnanceur.
	 * On ne se liera au scheduler qu'a la fin du placement,
	 * pour définir l'ordre de passage des joueurs, ou pour la gestion des erreurs.
	 */
	ft_thread_unlink();

	char *nbCase;
	char ok[4];
	memset(ok, 0, sizeof(ok));
	strcat(ok, "OK/\n");
	char wrong[7];
	memset(wrong, 0, sizeof(wrong));
	strcat(wrong, "WRONG/\n");
	int i, n, verif, envoyerShip = 1;
	for (i = 1; i <= NB_BATEAUX; i++) {

		// 1ère étape, on définit le nombre de cases du bateau
		nbCase = getNbCasesBateau(i);

		// 2e étape on envoie le SHIP et on attend la réponse
		if (envoyerShip) {
			if (!sendShip(nbCase, player)) return;
		}

		// reception du message client PUTSHIP ou TALK
		char buffer[1024];
		memset(buffer, 0, sizeof(buffer));
		n = recv(player->socket, buffer, (sizeof(buffer) - 1), 0);
		if (!checkMessageInGame(n, buffer, player->socket, player->partie, player->nom)) return;

		// si c'est un PUTSHIP
		if (!strncmp("PUTSHIP/", buffer, 8)) {
			// on parse le message recu
			verif = checkCasesCorrectes(player, buffer, atoi(nbCase));
			// si il est correcte, on envoie OK
			if (verif == 1) {
				if (!sendMessage(player->partie, player->socket, ok, player->nom)) return;
				// on envoie au SPECTATOR le message
				addPutShipMessageToGame(game, player->nom, buffer);
			}
			// sinon on envoie WRONG
			else {
				i--;
				if (!sendMessage(player->partie, player->socket, wrong, player->nom)) return;
			}

			// si on a recu un PUTSHIP, à l'iteration suivante de la boucle on demande un SHIP
			envoyerShip = 1;
		}
		else if (!strncmp("TALK/", buffer, 5)) {
			i--;
			talk(game, player, buffer);
			// si on a recu un TALK, à l'iteration suivante de la boucle on ne demande pas de SHIP
			envoyerShip = 0;
		}
		else {
			if (!byeOrPlay(buffer, game, player)) {
				// si ce n'est pas un BYE PLAYAGAIN PUTSHIP ou TALK
				puts("------NOT A PUTSHIP OR TALK MESSAGE.");
				killPlayer(player->partie, player->nom, 0, 0);
			}
			return;
		}
	}

	// et enfin si tout va bien, on renvoie le message ALLYOURBASE.
	char allYourBase[] = "ALLYOURBASE/\n";
	if (!sendMessage(player->partie, player->socket, allYourBase, player->nom)) return;

	// a la fin du placement, on se relie à l'ordonnanceur et on définit l'ordre de passage du joueur.
	ft_thread_link(game->ordonnanceur);
	player->ordrePassage = game->joueurPret;
	game->joueurPret ++;

	char mess[1024];
	memset(mess, 0, sizeof(mess));
	sprintf(mess, "Le joueur %s est pret et a le numéro %d", player->nom, player->ordrePassage);
	talkServeur(game, mess);

	// si le joueur est le dernier on lance la partie pour chacun des joueurs
	if ((player->ordrePassage + 1) == game->nbJoueurs) {

		PLAYER *cursor = game->joueurs;
		do {
			// on kill le thread qui écoute les messages TALK
			if (strcmp(cursor->nom, player->nom)) {
				pthread_cancel(ft_pthread(cursor->threadTalk));
			}
			// la fonction JouerPartie est dans le fichier game.c
			cursor->threadPartie = ft_thread_create(game->ordonnanceur, JouerPartie, NULL, cursor);
			cursor = cursor->suivant;
		} while (cursor != NULL);
	}
	// sinon, on lance un thread qui va écouter les TALK en attendant le début de partie
	else {
		player->threadTalk = ft_thread_create(game->ordonnanceur, ecouterTalk, NULL, (void *)player);
	}
}

/*
 * Renvoie le nombre de case du i-eme bateau
 */
char* getNbCasesBateau(int i) {
	// les bateaux de 1 case
	if (i <= BATEAU1) return "1";
	// les bateaux de 2 cases
	if (i <= BATEAU1 + BATEAU2) return "2";
	// les bateaux de 3 cases
	if (i<= BATEAU1 + BATEAU2 + BATEAU3) return "3";
	// les bateaux de 4 cases
	if (i <= BATEAU1 + BATEAU2 + BATEAU3 + BATEAU4) return "4";
	// par défaut, si les define sont incorrects on met des bateaux de 1 case
	return "1";
}

/*
 * Envoie le message SHIP correspondant au nombre de case passé en paramètre
 */
int sendShip(char *nbCase, PLAYER *player) {
	char putShip[8];
	memset(putShip, 0, sizeof(putShip));
	sprintf(putShip, "SHIP/%s/\n", nbCase);
	int verif = sendMessage(player->partie, player->socket, putShip, player->nom);
	return verif;
}

/*
 * Parse le message PUTSHIP et vérifie si le bateau du joueur est posable.
 * Renvoie 1 si c'est bon, -1 sinon.
 */
int checkCasesCorrectes(PLAYER *player, char *buffer, int nbCase) {

	int debug = 0;
	/*
	 * Avant de commencer a lire le buffer, on définit les cases disponibles.
	 * 0 correspond a une case disponible.
	 * 1 correspond a une case non disponible (pour le positionnement des 2e, 3e, etc.).
	 * 2 correspond a une case occupée partiellement par le bateau qui est en train d'être posé.
	 * 10 correspond a une case que le joueur occupe déjà.
	 */
	int casesDispos[16][16];
	int i, j;
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			casesDispos[i][j] = player->carte[i][j] * 10;
		}
	}

	// on commence au 9-ième caractère qui correspond au x de la première case
	int x, y, iteration = 0, size = strlen(buffer);
	for (i = 8; i < size && buffer[i] != '\n'; i++) {
		iteration++;

		// on récupère les coordonnées demandée par le client en x les nombres de 0 à 15
		char tmp[2];
		memset(tmp, 0, 2);
		tmp[0] = buffer[i];
		if (buffer[i+1] != '/') {
			tmp[1] = buffer[i+1];
		}
		x = atoi(tmp);
		// on avant jusqu'au prochain "/" pour parser le y en caractère de A à P
		do {
			i++;
		} while (buffer[i] != '/');
		i++;
		// on récupère y
		y = charToInt(buffer[i]);
		i++;

		printf("x='%d', y='%d', ", x, y);
		fflush(stdout);
		// 1ère étape, les cases sont-elles dans la map?
		if (!verifCaseInMap(x)) return -1;
		if (!verifCaseInMap(y)) return -1;

		// 2e étape, on vérifie que la case est disponible
		if (casesDispos[x][y] > 0) return -1;

		// 3e étape, on occupe partiellement cette case
		casesDispos[x][y] = 2;

		// Print de la map en ASCII art
		if (debug) {
			puts("");
			int k, l;
			for (l = 15; l >= 0; l--) {
				for (k = 0; k < 16; k++) {
					if (casesDispos[k][l] == 10)
						printf("%d ", casesDispos[k][l]);
					else
						printf(" %d ", casesDispos[k][l]);
				}
				puts("");
			}
		}

		// 4e étape, on définit les prochaines cases qui seront possibles
		if (iteration == 1) {
			definirCasesDisposAutour(casesDispos);
		}
		else if (iteration > 1){
			definirCasesEnLigne(casesDispos);
		}
	}
	puts("");

	/*
	 * Avant de poser le bateau on vérifie quand même que le client pose un bateau
	 * ayant le bon nombre de case. Sinon il pourrait poser un bateau de 10 cases o_O !
	 */
	if (iteration != nbCase) return -1;

	// Seulement ici on peut poser le bateau. On recherche les cases dont le flag est à 2.
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			if (casesDispos[i][j] == 2)
				player->carte[i][j] = 1;
		}
	}
	return 1;
}

/*
 * Version simplifiée lorsqu'on n'a posé encore qu'une seule case.
 * 1. Met le flag a 1 pour toutes les cases qui ne sont pas a 2 (posée partiellement)
 * ou 10 (déjà occupée par un autre bateau).
 * 2. Puis met des flags à 0 tout autour du 2 rencontré, sauf si on a déjà un 10.
 */
void definirCasesDisposAutour(int casesDispos[16][16]) {

	int a, b;
	// 1ère étape, on met toutes les cases libre (flag à 0) en mode occupée (flag à 1).
	for (a = 0; a < 16; a++) {
		for (b = 0; b < 16; b++) {
			if (casesDispos[a][b] == 0) {
				casesDispos[a][b] = 1;
			}
		}
	}
	// 2e étape, on cherche la case qui a le flag a 2 (dans cette méthode on ne doit en avoir qu'une)
	for (a = 0; a < 16; a++) {
		for (b = 0; b < 16; b++) {
			if (casesDispos[a][b] == 2) {
				// on met les flag à 0 seulement autour, si on n'a pas déjà de bateau
				// a gauche
				if (verifCaseInMap (a-1) && casesDispos[a-1][b] != 10)
					casesDispos[a-1][b] = 0;
				// a droite
				if (verifCaseInMap (a+1) && casesDispos[a+1][b] != 10)
					casesDispos[a+1][b] = 0;
				// en bas
				if (verifCaseInMap (b-1) && casesDispos[a][b-1] != 10)
					casesDispos[a][b-1] = 0;
				// en haut
				if (verifCaseInMap (b+1) && casesDispos[a][b+1] != 10)
					casesDispos[a][b+1] = 0;
			}
		}
	}
}

/*
 * Version qui définit les cases que le joueur pourra sélectionner pour déposer les autres
 * cases de son bateau. Il doit déjà en avoir sélectionner deux.
 * 1. Met le flag à 1 pour toutes les cases qui ne sont pas à 2 ou 10.
 * 2. Recherche la première case du bateau partiellement sélectionnée (2).
 * 3. Cherche le sens du bateau.
 * 2. Met des flags à 0 seulement pour les cases devant et derrière le bateau s'il n'y a pas
 * déjà de 10.
 */
void definirCasesEnLigne(int casesDispos[16][16]) {

	int x, y;
	// 1ère étape, on met toutes les cases libre (flag à 0) en mode non dispo (flag à 1).
	for (x = 0; x < 16; x++) {
		for (y = 0; y < 16; y++) {
			if (casesDispos[x][y] == 0) {
				casesDispos[x][y] = 1;
			}
		}
	}
	// 2e étape, on cherche la première case qui a le flag a 2
	int fini = 0;
	for (x = 0; x < 16 && !fini; x++) {
		for (y = 0; y < 16 && !fini; y++) {
			if (casesDispos[x][y] == 2) {
				fini = 1;
				// on recherche maintenant la deuxième case pour définir la direction du bateau
				/*
				 * A GAUCHE
				 * on ne devrait jamais tomber dans ce cas puisque la map est parcourue
				 * dans l'ordre croissant sur l'axe des x
				 */
				//if (verifCaseInMap (x-1) && casesDispos[x-1][y] == 2) {}
				// A DROITE
				if (verifCaseInMap (x+1) && casesDispos[x+1][y] == 2) {
					/*
					 * on met un flag dispo à gauche si la case est dans la map
					 * et que la case n'était pas déjà occupé par un vrai bateau (flag à 10)
					 */
					if (verifCaseInMap (x-1) && casesDispos[x-1][y] == 1) {
						casesDispos[x-1][y] = 0;
					}
					// puis on se déplace sur la droite jusqu'à tomber sur une case différente de 2
					int tmp = x+1;
					do {
						tmp ++;
					} while (verifCaseInMap (tmp) && casesDispos[tmp][y] == 2);
					/*
					 * et enfin si la case en question est dans la map et qu'on a pas déjà
					 * un bateau de posé
					 */
					if (verifCaseInMap (tmp) && casesDispos[tmp][y] == 1) {
						casesDispos[tmp][y] = 0;
					}
				}
				/*
				 * EN BAS
				 * on ne devrait jamais tomber dans ce cas puisque la map est parcourue
				 * dans l'ordre croissant sur l'axe des y
				 */
				//if (verifCaseInMap (y-1) && casesDispos[x][y-1] == 2) {}
				// EN HAUT
				if (verifCaseInMap (y+1) && casesDispos[x][y+1] == 2) {

					/*
					 * on met un flag dispo en bas si la case est dans la map
					 * et que la case n'était pas déjà occupé par un vrai bateau (flag à 10)
					 */
					if (verifCaseInMap (y-1) && casesDispos[x][y-1] == 1) {
						casesDispos[x][y-1] = 0;
					}
					// puis on se déplace vers le haut jusqu'à tomber sur une case différente de 2
					int tmp = y + 1;
					do {
						tmp ++;
					} while (verifCaseInMap (tmp) && casesDispos[x][tmp] == 2);
					/*
					 * et enfin si la case en question est dans la map et qu'on a pas déjà
					 * un bateau de posé
					 */
					if (verifCaseInMap (tmp) && casesDispos[x][tmp] == 1) {
						casesDispos[x][tmp] = 0;
					}
				}
			}
		}
	}
}
