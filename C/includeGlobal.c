#define includeGlobal

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <signal.h>
#include "fthread.h"

#define INVALID_SOCKET -1
#define SOCKET_ERROR -1
#define closesocket(s) close(s)
#define PORT 2012

typedef int SOCKET;
typedef struct sockaddr_in SOCKADDR_IN;
typedef struct sockaddr SOCKADDR;

typedef struct server {
	// le scheduler qui gèrera les connexions clientes et le timer
	ft_scheduler_t scheduler;
	// Booléen qui dit si une partie attend des nouveaux joueurs
	int waitPlayer;
	// thread qui lancera le timer
	ft_thread_t threadWaiting;
	// réprésente les parties du serveur
	struct game *games;
	/*
	 * Le nombre de parties en attente. Cette variable sert pour corriger un bug
	 * car le nbParties n'est pas incrémenté dès la connexion d'un client et cela
	 * pouvait provoquer un bug si le client se déconnectait directement après...
	 */
	int nbPartiesAttente;
	// le nombre de parties
	int nbParties;

} SERVER;

SERVER server;
int numeroPartie;

typedef struct game {
	// la partie suivante
	struct game *suivant;
	// le numéro de la partie actuelle
	int numeroPartie;
	// le scheduler qui va gérer les joueurs
	ft_scheduler_t ordonnanceur;
	// représente la liste des joueurs
	struct player *joueurs;
	// le nombre de joueurs dans la partie
	int nbJoueurs;
	// le nombre de joueurs qui ont placé leurs bateaux
	int joueurPret;
	// le nom du gagnant, non initialisé tant qu'il n'y en a pas
	char *gagnant;
	// le numéro du joueur qui doit jouer à un instant T
	int numeroQuiJoue;
	// la liste chainée des spectateurs
	struct spectator *spectateurs;
	// la liste chainée de tous les messages qu'il s'est passé durant la partie à partie du PLAYERS
	struct message *messages;
	// le dernier message, juste pour éviter de reparcourir toute la chaine a chaque fois
	struct message *dernierMessage;
} GAME;

typedef struct player {
	SOCKET socket;
	char* nom;
	// le suivant dans la liste chainée
	struct player *suivant;
	// représente la partie a laquelle appartient le joueur
	struct game *partie;
	/*
	 * La map des bateaux, 1 pour une case pleine, 0 pour une case vide
	 * Si (une partie d')un bateau a été touché, on met le flag à 2. C'est
	 * pour pouvoir définir plus rapidement la fin de la partie d'un joueur.
	 */
	int carte[16][16];
	// représente le numéro d'ordre de passage du client. -1 s'il n'a plus de bateau
	int ordrePassage;
	// la position du drone du joueur en indice 0 -> x, en 1 -> y
	int position[2];
	/*
	 * Représente le thread qui gérera la partie d'un joueur.
	 */
	ft_thread_t threadPartie;
	/*
	 * Représente le thread qui écoute les potentiels TALK entre la connexion et le
	 * "putship" et entre le "putship" et le déroulement de la partie.
	 */
	ft_thread_t threadTalk;
	// un flag pour dire si un joueur est mort ou non
	int mort;
} PLAYER;

typedef struct spectator {
	SOCKET socket;
	// la partie à laquelle est lié le spectateur
	struct game *partie;
	// le thread qui sera sans cesse en écoute sur les messages TALK
	ft_thread_t threadTalk;
	struct spectator *suivant;
} SPECTATOR;

typedef struct message {
	char *message;
	struct message *suivant;
} MESSAGE;

#ifndef includeServer
#include "server.h"
#endif
#ifndef includeGame
#include "game.h"
#endif
#ifndef includePutShip
#include "putShip.h"
#endif
#ifndef includeUtil
#include "util.h"
#endif
#ifndef includeTalk
#include "talk.h"
#endif
#ifndef includeRegister
#include "register.h"
#endif
#ifndef includeServerHttp
#include "serverHttp.h"
#endif

/*
 * Ces deux variables globales servent pour les tests
 */
int WAIT_BEFORE_GAME;
int solo;
