#ifndef includeGlobal
#include "includeGlobal.c"
#endif

void startServerHttp(void* args) {
	int portHttp = 2092;

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
		return;
	}
	printf("La socket %d est maintenant ouverte en mode TCP/IP.\n", sock);

	// pour pouvoir écouter sur la même adresse/port en cas d'arret brutal du serveur
	int optval = 1;
	setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof optval);

	// Configuration
	sin.sin_addr.s_addr = htonl(INADDR_ANY);  // Adresse IP automatique
	sin.sin_family = AF_INET;                 // Protocole familial (IP)
	//htons(PORT)
	sin.sin_port = ntohs(portHttp);           // Listage du port
	sock_err = bind(sock, (SOCKADDR*)&sin, recsize);

	// Si le bind ne fonctionne pas
	if(sock_err == SOCKET_ERROR) {
		perror("Erreur du bind.");
		closesocket(sock);
		return;
	}

	// Démarrage du listage (mode server)
	sock_err = listen(sock, 1);
	printf("Listage du port %d...\n", portHttp);

	// Si le listage ne fonctionne pas
	if(sock_err == SOCKET_ERROR) {
		perror("Erreur du listage.");
		closesocket(sock);
		return;
	}

	char *buffer;
	while (1) {
		printf("Attente d'une connexion cliente sur le port %d...\n", portHttp);
		csock = accept(sock, (SOCKADDR*)&csin, &crecsize);

		int pidf = fork();
		if(pidf<0) perror("fork()");
		else if (pidf == 0) {

			buffer = malloc(sizeof(char)*498);
			memset(buffer, 0, sizeof(buffer));
			int n = read(csock, buffer, 498);
			if(n < 0) perror("read()");

			char request[32], target[64], protocol[16];
			sprintf(request,"%s",strtok(buffer," "));
			sprintf(target,"%s",strtok(NULL," "));
			sprintf(protocol,"%s",strtok(NULL," "));

			free(buffer);

			printf("Request = %s\n", request);
			printf("Target = %s\n", target);

			// si on veut charger le fichier html
			if (!strcmp(target,"/")) {

				// on charge le header
				FILE *file = fopen("http/index.html", "r+");
				char page[8192], ligne[512];
				memset(page, 0, sizeof(page));
				memset(ligne, 0, sizeof(ligne));
				while (fgets(ligne, sizeof(ligne), file) != NULL) {
					strcat(page, ligne);
					memset(ligne, 0, sizeof(ligne));
				}
				fclose(file);

				// on charge le tableau avec les stats en dynamique
				char *stats = getStatsBD();
				strcat(page, stats);
				// stats est alloué dans getStatsBD(), ne pas oublier de free !
				free(stats);

				// puis on charge le footer
				file = fopen("http/footer.html", "r+");
				while (fgets(ligne, sizeof(ligne), file) != NULL) {
					strcat(page, ligne);
					memset(ligne, 0, sizeof(ligne));
				}
				fclose(file);

				// et on écrit le tout
				if(write(csock, page, sizeof(page)) < 0) perror("IO Error");
				close(csock);
			}
			// si on demande a charger l'image
			else if (!strcmp(target,"/ban.jpg")){
				FILE *myFile;
				if ((myFile = fopen("http/ban.jpg", "rb")) == NULL)	exit(0);
				char bufferIOF[1024];
				int len=0;
				do{
					len = fread(bufferIOF,1,1,myFile);
					if(write(csock,bufferIOF,len)<0) perror("IO Error");
				}while(len>0);
			}
			close(csock);
			exit(0);
		}
		else {
			close(csock);
		}
	}

	closesocket(sock);
}
