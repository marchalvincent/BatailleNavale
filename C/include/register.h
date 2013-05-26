#define includeRegister

int loginPlayer(char *playerName, char *password);
int registerPlayer(char *playerName, char *password, int inscription);
void addPlayerToRegister(char *playerName, char *password);
char *getName(char line[]);
void misAJourBD(GAME *game);
void deserteur (char *deserteur);
char *getStatsBD();
