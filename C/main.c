#define includeMain

#ifndef includeGlobal
#include "includeGlobal.c"
#endif

void testplacementShip(void);

int main(int argc, char *argv[]) {
	// tests sur le placement des bateaux
	testplacementShip();

	//lancement du serveurHTTP
	pthread_t serverHttp;
	puts("Start serveur HTTP...");
	pthread_create(&serverHttp, NULL, (void *)startServerHttp, NULL);

	// sert pour les tests en solo, accepte les parties à 1 joueur
	if (argc == 2 && strcmp(argv[1], "solo") == 0) {
		WAIT_BEFORE_GAME = 1;
		solo = 1;
	}
	else {
		WAIT_BEFORE_GAME = 15;
		solo = 0;
	}

	// Socket et contexte d'adressage du serveur
	SOCKADDR_IN sin;
	SOCKET sock;
	socklen_t recsize = sizeof(sin);

	// Socket et contexte d'adressage du client
	SOCKADDR_IN csin;
	SOCKET csock;
	socklen_t crecsize = sizeof(csin);

	int sock_err;

	// Création de la socket qui écoutera les connexions clientes
	sock = socket(AF_INET, SOCK_STREAM, 0);

	// Si la socket n'est pas valide
	if(sock == INVALID_SOCKET) {
		perror("Impossible de créer la socket d'écoute.");
		return 1;
	}
	printf("La socket %d est maintenant ouverte en mode TCP/IP.\n", sock);

	// pour pouvoir écouter sur la même adresse/port en cas d'arret brutal du serveur
	int optval = 1;
	setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof optval);

	// Configuration
	sin.sin_addr.s_addr = htonl(INADDR_ANY);  // Adresse IP automatique
	sin.sin_family = AF_INET;                 // Protocole familial (IP)
	sin.sin_port = htons(PORT);               // Listage du port
	sock_err = bind(sock, (SOCKADDR*)&sin, recsize);

	// Si le bind ne fonctionne pas
	if(sock_err == SOCKET_ERROR) {
		perror("Erreur du bind.");
		closesocket(sock);
		return 1;
	}

	// Démarrage du listage (mode server)
	sock_err = listen(sock, 10);
	printf("Listage du port %d...\n", PORT);

	// Si le listage ne fonctionne pas
	if(sock_err == SOCKET_ERROR) {
		perror("Erreur du listage.");
		closesocket(sock);
		return 1;
	}

	// initialisation du serveur
	initServer();

	// Boucle qui accepte les connexions clientes
	while (1) {
		printf("Attente d'une connexion cliente sur le port %d...\n", PORT);
		csock = accept(sock, (SOCKADDR*)&csin, &crecsize);
		printf("Un client se connecte avec la socket %d de %s:%d\n", csock,
				inet_ntoa(csin.sin_addr), htons(csin.sin_port));

		// On lance la fonction qui créera le thread pour l'accueil d'un nouveau joueur
		newClient(&csock);
	}

	// Fermeture de la socket client et de la socket serveur
	printf("Fermeture de la socket client\n");
	closesocket(csock);
	printf("Fermeture de la socket serveur\n");
	closesocket(sock);
	printf("Fermeture du serveur terminée\n");

	return EXIT_SUCCESS;
}
