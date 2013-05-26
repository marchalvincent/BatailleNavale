#ifndef includeGlobal
#include "includeGlobal.c"
#endif
/*
 * Je suis conscient que ce n'est pas la plus optimisée des bases de données...
 */


/*
 * Vérifie que le joueur existe et que son mot de passe est correcte.
 * Si les 2 conditions sont vérifiées, renvoie 1. Renvoie 0 sinon.
 */
int loginPlayer(char *playerName, char *password) {

	FILE *fichier = fopen("register", "r");
	if(fichier == NULL) {
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return 0;
	}

	char chaine[1024], *nom, *mdp;
	memset(chaine, 0, sizeof(chaine));
	while (fgets(chaine, sizeof(chaine), fichier) != NULL) {

		// on parse le fichier, si on tombe sur le joueur on check le mot de passe
		nom = getName(chaine);
		if (!strcmp(nom, playerName)) {
			mdp = strtok(NULL, "|");
			fclose(fichier);
			printf("on compare %s et %s \n", mdp, password);
			printf("%d\n", !strcmp(mdp, password));
			return !strcmp(mdp, password);
		}
	}
	// on n'a pas trouvé le joueur en base
	fclose(fichier);
	return 0;
}

/*
 * Vérifie que le joueur n'est pas en base.
 * Si oui, renvoie 0.
 * Si non, renvoie 1.
 * Si le flag inscription est à 1, inscrit le joueur en base.
 */
int registerPlayer(char *playerName, char *password, int inscription) {

	FILE *fichier = fopen("register", "r");
	if(fichier == NULL) {
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return 0;
	}

	char chaine[1024], *nom;
	memset(chaine, 0, sizeof(chaine));
	while (fgets(chaine, sizeof(chaine), fichier) != NULL) {

		// on parse le fichier, si on a déjà le nom du joueur, on renvoie FALSE
		nom = getName(chaine);
		if (!strcmp(nom, playerName)) {
			fclose(fichier);
			return 0;
		}
	}
	fclose(fichier);

	if (inscription) {
		// ici, on ajoute le joueur avec son mot de passe
		addPlayerToRegister(playerName, password);
	}
	return 1;
}

/*
 * Ajoute un nouvel utilisateur dans la base de données
 */
void addPlayerToRegister(char *playerName, char *password) {

	FILE *fichier = fopen("register", "a+");
	if(fichier == NULL){
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return;
	}

	int sizeLine = 6 + strlen(playerName) + strlen(password);
	char line[sizeLine];
	memset(line, 0, sizeof(line));
	sprintf(line, "%s|%s|00000|00000\n", playerName, password);
	fputs(line, fichier);

	fclose(fichier);
}

/**
 * renvoie le nom correspondant à la ligne passée en paramètre
 */
char *getName(char line[]) {
	return strtok(line, "|");
}

/*
 * Met +1 en BD aux joueurs de la partie en fonction de s'il ont gagné ou perdu.
 */
void misAJourBD(GAME *game) {

	if(game==NULL||game->joueurs==NULL)return;

	FILE *fichier = fopen("register", "r+");
	if(fichier == NULL) {
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return;
	}

	PLAYER *cursor;
	// on parcours le fichier
	long fileCursor = 0;
	int trouve;
	char chaine[1024], *playerName, *mdp, newLine[1024];
	char *victoireString, *defaiteString, newVictoire[5], newDefaite[5];
	memset(chaine, 0, sizeof(chaine));
	while (NULL != fgets(chaine, sizeof(chaine), fichier)) {
		trouve = 0;
		playerName = getName(chaine);

		// à chaque ligne, on regarde si on trouve un joueur de la partie
		cursor = game->joueurs;
		do {
			// si on a trouvé en BD le nom d'un joueur
			if (!strcmp(playerName, cursor->nom)) {
				trouve = 1;

				mdp = strtok(NULL, "|");
				memset(newVictoire, 0, sizeof(newVictoire));
				memset(newDefaite, 0, sizeof(newDefaite));
				victoireString = strtok(NULL, "|");
				defaiteString = strtok(NULL, "|");
				// si le joueur a gagné
				if (!strcmp(cursor->nom, game->gagnant)) {
					sprintf(newVictoire, "%05d", (atoi(victoireString) + 1) );
					sprintf(newDefaite, "%s", defaiteString );
				}
				// sinon
				else {
					sprintf(newDefaite, "%05d", (atoi(defaiteString) + 1) );
					sprintf(newVictoire, "%s", victoireString );
				}

				memset(newLine, 0, sizeof(newLine));
				sprintf(newLine, "%s|%s|%s|%s", playerName, mdp, newVictoire, newDefaite);

				// on se replace au début de la ligne
				fseek(fichier, fileCursor, SEEK_SET);
				//fputs(newLine, fichier);
				if (fputs(newLine, fichier) == EOF) {
					return;
				}
			}
			cursor = cursor->suivant;
		} while (cursor != NULL && !trouve);

		// on mémorise cet endroit pour pouvoir y revenir
		fileCursor = ftell(fichier);
	}
}

/*
 * Met +5 parties perdues au joueur.
 */
void deserteur (char *deserteur) {

	FILE *fichier = fopen("register", "r+");
	if(fichier == NULL) {
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return;
	}

	// on parcours le fichier
	long fileCursor = 0;
	char chaine[1024], *playerName, *mdp, newLine[1024];
	char *victoireString, *defaiteString, newDefaite[5];
	memset(chaine, 0, sizeof(chaine));
	while (NULL != fgets(chaine, sizeof(chaine), fichier)) {
		playerName = getName(chaine);

		// à chaque ligne, on regarde si on a le deserteur
		if (!strcmp(playerName, deserteur)) {

			puts("On applique la centense du deserteur !!!");
			mdp = strtok(NULL, "|");
			memset(newDefaite, 0, sizeof(newDefaite));
			victoireString = strtok(NULL, "|");
			defaiteString = strtok(NULL, "|");
			// on applique +10 à la défaite
			sprintf(newDefaite, "%05d", (atoi(defaiteString) + 10) );

			memset(newLine, 0, sizeof(newLine));
			sprintf(newLine, "%s|%s|%s|%s", playerName, mdp, victoireString, newDefaite);

			// on se replace au début de la ligne
			fseek(fichier, fileCursor, SEEK_SET);
			if (fputs(newLine, fichier) == EOF) {
				return;
			}
		}
		// on mémorise cet endroit pour pouvoir y revenir
		fileCursor = ftell(fichier);
	}
}

/*
 * Renvoie les stats de la BD pour le site HTTP
 */
char *getStatsBD() {

	FILE *fichier = fopen("register", "r+");
	if(fichier == NULL) {
		puts("IMPOSSIBLE D'OUVRIR LE FICHIER 'register'");
		return 0;
	}

	char *retour = malloc (sizeof(char) * 4096);
	memset(retour, 0, sizeof(retour));
	char chaine[1024], *nom, *win, *loose, ligne[1024];
	memset(chaine, 0, sizeof(chaine));
	while (fgets(chaine, sizeof(chaine), fichier) != NULL) {
		memset(ligne, 0, sizeof(ligne));

		// on récupère le nom le nombre de win et le nombre de loose
		nom = getName(chaine);
		// on saute le mot de passe car osef
		win = strtok(NULL, "|");
		win = strtok(NULL, "|");
		loose = strtok(NULL, "|");
		sprintf(ligne, "<tr><td>%s</td><td>%s</td><td>%s</td></tr>", nom, win, loose);
		strcat(retour, ligne);
	}
	// on n'a pas trouvé le joueur en base
	fclose(fichier);
	return retour;
}
