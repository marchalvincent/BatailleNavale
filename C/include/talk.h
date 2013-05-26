#define includeTalk

void messageServeur(GAME *game, char* message);
void talk(GAME *game, PLAYER *player, char *buffer);
void talkSpectator(GAME *game, char *buffer);
void talkServeur(GAME *game, char *buffer);
void talkWithName(GAME *game, char *buffer, char *name);
char *getMessageOfTalk(char *buffer);
char* echappementCaracteres(char *message);
int compterEchappement(char *message);
void sendToAll(GAME *game, char *message);
