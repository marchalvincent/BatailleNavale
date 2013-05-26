#define includeGame

#ifndef includeGlobal
#include "includeGlobal.c"
#endif

// le nombre de bateaux
#define NB_BATEAUX 7
// bateau(x) de 1 case
#define BATEAU1 2
// bateau(x) de 2 cases, etc.
#define BATEAU2 2
#define BATEAU3 2
#define BATEAU4 1

char* addPlayerToGame (GAME* g, SOCKET soc, char* playerName);
void killPlayer(GAME *game, char *player, int finPartie, int garderSocket);
void freePlayer(PLAYER *player, int garderSocket);
void startLastGame(GAME *game, int numPartie);
int checkHasWinnerAndNotify(GAME *game);
int hasWinner(GAME *game);
void JouerPartie(void* p);
int getNbAction(int map[16][16]);
void action(PLAYER *player, char *message);
void tirer(PLAYER *player);
void checkPlayerInGame(PLAYER *p);
void sendMessageWithCoordinates(PLAYER *p, char *mess, int x, int y);
int byeOrPlay(char *buffer, GAME *game, PLAYER *player);
void sendYourTurn(PLAYER *p);
void sendYourTurnToNext(GAME *game);
void stopOthersThread(GAME *game, char *playerName);
void sendAWinnerIs(GAME *game);
void byeBye(GAME *game, PLAYER *player, int garderSocket);

// la partie des messages SPECTATOR
void addMessageToGame(GAME *game, char *playersMessage, int sendSpectator);
void addPutShipMessageToGame(GAME *game, char *playerName, char *buffer);
void addActionMessageToGame(GAME *game, PLAYER *player);
void addOuchMessageToGame(GAME *game, char *playerName, int x, int y);
