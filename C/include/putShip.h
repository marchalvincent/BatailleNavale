#define includePutShip

#ifndef includeGlobal
#include "includeGlobal.c"
#endif

void placementShip(void* args);
char* getNbCasesBateau(int i);
int sendShip(char *nbCase, PLAYER *player);
int checkCasesCorrectes(PLAYER *player, char *buffer, int nbCase);
void definirCasesDisposAutour(int casesDispos[16][16]);
void definirCasesEnLigne(int casesDispos[16][16]);
