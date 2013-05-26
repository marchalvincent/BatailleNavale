#ifndef includeGlobal
#include "includeGlobal.c"
#endif

/*
 * Printf le message pour le debug.
 * Type est censé être soit "Envoi" soit "Reception".
 */
void printMessage(char *type, char *string, char* playerName) {
	printf("----->%s du message ", type);
	if (playerName != NULL)
		printf("(%s) ", playerName);
	printf(": '");
	int i, n = strlen(string) - 1;
	for(i = 0; i < n; i++) {
		printf("%c", string[i]);
	}
	puts("'");
}

/*
 * Envoie un message dans la socket.
 * Si une erreur est survenue, ferme la socket et kill le joueur de sa partie.
 */
int sendMessage (GAME *g, SOCKET s, char *mess, char* playerName) {

	printMessage("Envoi", mess, playerName);
	int n = send(s, mess, strlen(mess), 0);
	printf("Après l'envoi dans la socket %d, n=%d\n", s, n);
	if (n == SOCKET_ERROR) {
		printf("//!\\\\Attention, impossible d'envoyer le message '%s'", mess);
		// on ferme la socket et on kill le joueurs de sa partie
		closesocket(s);
		killPlayer(g, playerName, 0, 1);
		return 0;
	}
	return 1;
}

/*
 * Vérifie que le message à bien été reçu et ferme la socket en cas d'erreur.
 */
int checkMessage(int n, char *mess, SOCKET socket) {

	printMessage("Reception", mess, NULL);
	// en général si le buffer est vide c'est que le client a close sa connexion
	if (strlen(mess) == 0) return -1;
	if (n == SOCKET_ERROR) {
		// on ferme la connexion avec le client et on stop l'execution du thread
		puts("//!\\\\Attention, impossible de lire dans la socket cliente. Fermeture de la socket");
		accessDenied(socket);
		return 0;
	}
	return 1;
}

/*
 * Vérifie que le message à bien été reçu.
 * En cas d'erreur, supprime le joueur de la partie si elle était lancée puis
 * ferme la socket.
 */
int checkMessageInGame(int n, char *mess, SOCKET socket, GAME *g, char *playerName) {

	printMessage("Reception", mess, playerName);
	// en général si le buffer est vide c'est que le client a close sa connexion
	if (!strlen(mess) || n == SOCKET_ERROR) {
		// on ferme la connexion avec le client et on stop l'execution du thread
		printf("//!\\\\Attention, impossible de lire dans la socket cliente. "
				"Suppression de '%s' de la partie.\n", playerName);
		// si c'est un spectator on a un traitement a part a faire
		if (!strncmp("(spectator)", playerName, 11)) {
			killSpectator(g, socket);
		}
		else {
			deserteur(playerName);
			killPlayer(g, playerName, 0, 0);
		}
		return 0;
	}
	return 1;
}

/*
 * Alloue et créé la structure serveur
 */
void initServer() {
	SERVER* s = (SERVER*) malloc(sizeof(SERVER));
	server = *s;
	server.scheduler = ft_scheduler_create();
	ft_scheduler_start(server.scheduler);
	server.waitPlayer = 0;
	server.games = NULL;
	server.nbPartiesAttente = 0;
	server.nbParties = 0;
	numeroPartie = 0;
}

/*
 * Renvoie la dernière partie créée
 */
GAME* getLastGame() {

	if (server.games == NULL) return NULL;
	GAME *cursor = server.games;
	while (cursor->suivant != NULL) {
		cursor = cursor->suivant;
	}
	return cursor;
}

/*
 * Suppression d'un élément d'une liste chainée
 */
void removeGame(GAME *game) {

	puts("--------------------------------------debut du remove GAME");
	printf("On supprime la partie n°%d\n", game->numeroPartie);
	if (server.games == NULL) return;

	int suppression = 0;
	GAME *cursor = server.games;
	if (cursor->numeroPartie == game->numeroPartie) {
		// si le serveur attendait d'autre joueurs pour cette partie il faut lui dire de ne plus attendre
		if (server.nbPartiesAttente == cursor->numeroPartie && server.waitPlayer == 1) {
			server.waitPlayer = 0;
			// on kill également le timer
			pthread_cancel(ft_pthread(server.threadWaiting));
		}
		server.games = cursor->suivant;
		freeTheGame(cursor);
	}
	else {
		GAME *precedent;
		do {
			precedent = cursor;
			cursor = cursor->suivant;
			if (cursor->numeroPartie == game->numeroPartie) {
				// si le serveur attendait d'autre joueurs pour cette partie il faut lui dire de ne plus attendre
				if (server.nbPartiesAttente == cursor->numeroPartie && server.waitPlayer == 1) {
					server.waitPlayer = 0;
					// on kill également le timer
					pthread_cancel(ft_pthread(server.threadWaiting));
				}
				precedent->suivant = cursor->suivant;
				freeTheGame(cursor);
				suppression = 1;
			}
		} while (!suppression && cursor != NULL);
	}
	puts("--------------------------------------fin du remove GAME");
}

/*
 * Libère la mémoire d'une GAME et de tout ce qui s'en suit
 */
void freeTheGame(GAME *game) {
	// joueur spectateur messages
	freeAllPlayers(game->joueurs);
	freeAllSpectators(game->spectateurs);
	freeAllMessages(game->messages);
}

/*
 * Supprime une liste chainée de PLAYER
 */
void freeAllPlayers(PLAYER *player) {

	PLAYER *current = player, *next;
	while (current != NULL) {
		next = current->suivant;
		// on kill les threads et on libère le nom et la structure
		closesocket(current->socket);
		pthread_cancel(ft_pthread(current->threadTalk));
		free(current->nom);
		free(current);
		current = next;
	}
}

/*
 * Supprime une liste chainée de SPECTATOR
 */
void freeAllSpectators(SPECTATOR *spectator) {

	SPECTATOR *current = spectator, *next;
	while (current != NULL) {
		next = current->suivant;
		// on ferme la socket, kill le thread et on libère la structure
		closesocket(current->socket);
		pthread_cancel(ft_pthread(current->threadTalk));
		free(current);
		current = next;
	}
}

/*
 * Libère la mémoire d'une liste chainée de MESSAGE
 */
void freeAllMessages(MESSAGE *message) {

	MESSAGE *current = message, *next;
	while (current != NULL) {
		next = current->suivant;
		// on libère le string, puis la structure
		free(current->message);
		free(current);
		current = next;
	}
}

/*
 * Renvoie un message ACCESSDENIED et ferme la socket.
 */
void accessDenied(SOCKET socket) {
	char retour[14];
	memset(retour, 0, sizeof(retour));
	strcat(retour, "ACCESSDENIED/\n");
	printMessage("Envoi", retour, NULL);
	send(socket, retour, strlen(retour), 0);
	closesocket(socket);
}

/*
 * Lance le thread qui gère les nouveaux clients.
 */
void newClient(SOCKET *csock) {
	ft_thread_create(server.scheduler, newClientThread, NULL, (void*) csock);
}

/*
 * Si une partie est sur le point de démarrer, ajoute le client à celle ci.
 * Sinon créé une nouvelle partie.
 */
void newClientThread(void* sock) {
	SOCKET socket = *((SOCKET *) sock);
	int n;

	char buffer[1024];
	memset(buffer, 0, sizeof(buffer));
	n = recv(socket, buffer, (sizeof(buffer) - 1), 0);
	if (!checkMessage(n, buffer, socket)) return;
	buffer[n] = 0;

	char *playerName;
	if (!strncmp("CONNECT/", buffer, 8)) {
		playerName = getPack(buffer, 8);
		// on test si le joueur n'existe pas déjà en base
		if (!registerPlayer(playerName, "", 0)) {
			playerName = "";
		}
	}
	else if (!strncmp("REGISTER/", buffer, 9)) {
		playerName = parseRegisterLoginMessage(buffer, n, 9);
	}
	else if (!strncmp("LOGIN/", buffer, 6)) {
		playerName = parseRegisterLoginMessage(buffer, n, 6);
	}
	else if (!strncmp("SPECTATOR/", buffer, 10)) {
		addSpectator(socket);
		return;
	}
	else {
		// gestion des erreurs
		puts("------NOT A CONNECT, REGISTER OR LOGIN MESSAGE.");
		accessDenied(socket);
		return;
	}

	if (!strcmp(playerName, "")) {
		accessDenied(socket);
		puts("ACCESSDENIED");
		return;
	}
	addNewPlayer(socket, playerName);
	// playerName est alloué dans getPack() ou parseRegisterLoginMessage(), ne pas oublier de free
	free(playerName);
}

/*
 * Renvoie la chaine de caractère contenue entre position et le prochain / non échappé
 */
char *getPack(char *buffer, int position) {
	char *pack = malloc(sizeof(char) * (strlen(buffer) - position));
	memset(pack, 0, sizeof(pack));
	int i = position, j = 0;
	while (buffer[i] != '/') {
		if (buffer[i] != '\\') {
			pack[j] = buffer[i];
			j++;
		}
		else {
			// on doit avoir un / ou un \ en position i+1
			if (buffer[i+1] == '/' || buffer[i+1] == '\\') {
				pack[j] = buffer[i+1];
				j++;
				i++; // on incrémente i pour ne pas retomber sur le caractère échappé
			}
			else {
				// On a ici un problème
				puts("------PROBLEME D'ÉCHAPPEMENT DES CARACTÈRES.");
				free(pack);
				return "";
			}
		}
		i++;
	}
	return pack;
}

/*
 * Parse un message REGISTER ou LOGIN, et agit en fonction. Renvoie un string vide
 * si le REGISTER ou LOGIN est impossible.
 */
char *parseRegisterLoginMessage(char *buffer, int nbCarac, int debut) {

	// on parse d'abord le pseudo du joueur
	char *playerName = getPack(buffer, debut);
	if (!strcmp(playerName, "")) {
		puts("Le pseudo est vide...");
		free(playerName);
		return "";
	}

	// et ensuite le mot de passe
	int indicePassword = debut + strlen(playerName) + compterEchappement(playerName) + 1;
	char *password = getPack(buffer, indicePassword);
	if (!strcmp(password, "")) {
		puts("Le mot de passe est vide...");
		free(playerName);
		free(password);
		return "";
	}

	// le cas du REGISTER
	if (debut == 9) {
		int x = registerPlayer(playerName, password, 1);
		free(password);
		if (x == 1) {
			// si le REGISTER a réussit on renvoie le pseudo
			return playerName;
		}
		else if (x == 0) {
			free(playerName);
			return "";
		}
	}
	// le cas du LOGIN
	else if (debut == 6) {
		int x = loginPlayer(playerName, password);
		free(password);
		if (x == 1) {
			// si le LOGIN a réussit on renvoie le pseudo
			return playerName;
		}
		else if (x == 0) {
			free(playerName);
			return "";
		}
	}

	// si on est ici, on a une erreur. On renvoie un pseudo vide, sa sera géré plus tard.
	free(playerName);
	free(password);
	return "";
}

/*
 * Ajoute un spectateur à la partie la plus récente, ou en créé une s'il n'y en avait pas
 */
void addSpectator(SOCKET socket) {

	// le spectateur doit rejoindre la dernière partie, mais s'il n'y en a aucune, on la créé
	GAME *lastGame = getLastGame();
	if (lastGame == NULL) {
		// si il n' y aucune partie, il faut la créer
		server.nbPartiesAttente ++;
		server.waitPlayer = 1;
		numeroPartie ++;

		// on alloue la mémoire pour la nouvelle partie qu'on ajoute à la fin de la liste
		GAME *newGame = (GAME *) malloc(sizeof(GAME)*1);
		newGame->suivant = NULL;
		newGame->numeroPartie = numeroPartie;
		newGame->ordonnanceur = ft_scheduler_create();
		ft_scheduler_start(newGame->ordonnanceur);
		newGame->joueurs = NULL;
		newGame->nbJoueurs = 0;
		newGame->joueurPret = 0;
		newGame->gagnant = NULL;
		newGame->numeroQuiJoue = 0;
		newGame->spectateurs = NULL;
		newGame->messages = NULL;
		newGame->dernierMessage = NULL;

		server.games = newGame;
		lastGame = newGame;
	}

	// on alloue maintenant la mémoire pour le spéctateur
	SPECTATOR *spec = malloc(sizeof(SPECTATOR));
	spec->socket = socket;
	spec->partie = lastGame;
	spec->threadTalk = NULL;
	spec->suivant = NULL;

	// on l'ajoute à la partie
	if (lastGame->spectateurs == NULL) {
		lastGame->spectateurs = spec;
	}
	else {
		SPECTATOR *cursor = lastGame->spectateurs;
		while (cursor->suivant != NULL){
			cursor = cursor->suivant;
		}
		cursor->suivant = spec;
	}

	// un petit message pour dire qu'un spectateur est arrivé
	char messageServeur[] = "Un spectateur se joint à la partie!";
	talkServeur(lastGame, messageServeur);

	// on lance le thread d'écoute TALK
	spec->threadTalk = ft_thread_create(lastGame->ordonnanceur, ecouterTalkSpectator, NULL, (void *)spec);

	// puis on lui envoie le message WELCOME
	char welcome[] = "WELCOME/(spectator)/\n";
	if (!sendMessage(lastGame, spec->socket, welcome, "(spectator)")) return;

	// ici il faut lui envoyer tous les messages qu'il n'aurait pas pu avoir s'il est en retard
	MESSAGE *mess = lastGame->messages;
	while (mess != NULL) {
		puts("=====On envoit un message !");
		if (!sendMessage(lastGame, spec->socket, mess->message, "(spectator)")) return;
		mess = mess->suivant;
		// on attend ou tout petit peu car trop de message d'un coup fesait planter par moment le client
		ft_thread_unlink();
		usleep(200000);
		ft_thread_link(lastGame->ordonnanceur);
	}
}

/*
 * Supprime un spectateur d'une partie.
 */
void killSpectator(GAME *game, SOCKET socket) {

	printf("On supprime le spectateur avec la socket %d --->", socket);
	fflush(stdout);
	if (game == NULL || game->spectateurs == NULL) return;
	SPECTATOR *cursor = game->spectateurs;
	// si c'est le premier
	if (cursor->socket == socket) {
		game->spectateurs = cursor->suivant;
		closesocket(cursor->socket);
		free(cursor);
		puts("OK");
	}
	else {
		SPECTATOR *precedent;
		do {
			precedent = cursor;
			cursor = cursor->suivant;
			if (cursor->socket == socket) {
				precedent->suivant = cursor->suivant;
				closesocket(cursor->socket);
				free(cursor);
				puts("OK");
				break;
			}
			cursor = cursor->suivant;
		} while (cursor != NULL);
	}

	// si la partie n'a plus de joueur et de spectateurs, on la supprime
	if (game->joueurs == NULL && game->spectateurs == NULL) {
		printf("On supprime la partie n°%d\n", game->numeroPartie);
		removeGame(game);
	}
}

/*
 * Ajoute un joueur à une partie à partir de sa socket et son nom.
 */
void addNewPlayer(SOCKET socket, char *playerName) {
	int startCounterMessage = 0;
	// si on n'attendait pas encore de joueur...
	if (server.waitPlayer == 0) {
		server.nbPartiesAttente ++;
		server.waitPlayer = 1;
		numeroPartie ++;

		// on alloue la mémoire pour la nouvelle partie qu'on ajoute à la fin de la liste
		GAME *newGame = (GAME *) malloc(sizeof(GAME)*1);
		newGame->suivant = NULL;
		newGame->numeroPartie = numeroPartie;
		newGame->ordonnanceur = ft_scheduler_create();
		ft_scheduler_start(newGame->ordonnanceur);
		newGame->joueurs = NULL;
		newGame->nbJoueurs = 0;
		newGame->joueurPret = 0;
		newGame->gagnant = NULL;
		newGame->numeroQuiJoue = 0;
		newGame->spectateurs = NULL;
		newGame->messages = NULL;
		newGame->dernierMessage = NULL;

		if (server.games == NULL) {
			server.games = newGame;
		} else {
			GAME *cursor = server.games;
			while (cursor->suivant != NULL) {
				cursor = cursor->suivant;
			}
			cursor->suivant = newGame;
		}

		/*
		 * La variable solo sert pour les tests avec 1 seul joueur, c'est pourquoi
		 * on lance le compteur juste après la création de la partie dans le cas d'un test solo.
		 */
		if (solo) {
			puts("------//!\\\\ Attention, la partie est sur le point de démarrer !");
			server.threadWaiting = ft_thread_create(server.scheduler, startCounter, NULL, NULL);
		}
	}
	// si le serveur attend des joueurs
	else {
		// si on a le 2e joueur, on lance le counter
		if (!solo && getLastGame()->nbJoueurs == 1) {
			startCounterMessage = 1;
			server.threadWaiting = ft_thread_create(server.scheduler, startCounter, NULL, NULL);
		}
	}

	GAME *partieEnCours = getLastGame();
	// puis on ajoute le client à la partie
	playerName = addPlayerToGame(partieEnCours, socket, playerName);

	//on ne répond WELCOME qu'après l'ajout du joueur au cas ou on avait changé son nom.
	char *newPlayerName = echappementCaracteres(playerName);
	int sizeRetour = 10 + strlen(newPlayerName);
	char retour[sizeRetour];
	memset(retour, 0, sizeof(retour));
	sprintf(retour, "WELCOME/%s/\n", newPlayerName);
	if (!sendMessage(partieEnCours, socket, retour, playerName)) return;
	free(newPlayerName);

	if (startCounterMessage) {
		// on envoie un message pour dire qu'un nouveau joueur est arrivé
		char messageServeur[128];
		memset(messageServeur, 0, sizeof(messageServeur));
		strcat(messageServeur, "\\/!\\\\ Attention, la partie est sur le point de démarrer !");
		talkServeur(getLastGame(), messageServeur);
	}

	// si on a nos 4 joueurs, on arrête le counter et on lance la partie
	if (partieEnCours->nbJoueurs == 4) {
		pthread_cancel(ft_pthread(server.threadWaiting));
		server.waitPlayer = 0;
		startLastGame(partieEnCours, numeroPartie);
		server.nbParties++;
	}
}

/*
 * Fonction qui va créer la partie, lancer le counter, se détacher du scheduler
 * et attendre n secondes. Ensuite il se relie au scheduler, et lance la partie
 * s'il y a plus d'1 joueur.
 */
void startCounter(void* args) {
	// on se délie du scheduler car il faut que les autres clients puissent se connecter
	ft_thread_unlink();
	sleep(WAIT_BEFORE_GAME);
	ft_thread_link(server.scheduler);
	server.waitPlayer = 0;
	startLastGame(getLastGame(), numeroPartie);
	server.nbParties++;
}
