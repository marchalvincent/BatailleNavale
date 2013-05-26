#define includeServer

#ifndef includeGlobal
#include "includeGlobal.c"
#endif

void printMessage(char *type, char *string, char *playerName);
int sendMessage (GAME *g, SOCKET s, char *mess, char* playerName);
int checkMessage(int n, char *mess, SOCKET socket);
int checkMessageInGame(int n, char *mess, SOCKET socket, GAME *g, char *playerName);
void accessDenied(SOCKET socket);
void initServer();
GAME* getLastGame();
void removeGame(GAME *game);
void freeTheGame(GAME *game);
void freeAllPlayers(PLAYER *player);
void freeAllSpectators(SPECTATOR *spectator);
void freeAllMessages(MESSAGE *message);
void newClient(SOCKET *csock);
void newClientThread(void* sock);
char *getPack(char *buffer, int position);
char *parseRegisterLoginMessage(char *buffer, int nbCarac, int debut);
void addSpectator(SOCKET socket);
void killSpectator(GAME *g, SOCKET socket);
void addNewPlayer(SOCKET socket, char *playerName);
void startCounter(void* args);
