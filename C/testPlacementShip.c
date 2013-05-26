#ifndef includeGlobal
#include "includeGlobal.c"
#endif

void printMap(int map[16][16]) {
	puts("");
	int k, l;
	for (l = 15; l >= 0; l--) {
		for (k = 0; k < 16; k++) {
			if (map[k][l] == 10)
				printf("%d ", map[k][l]);
			else
				printf(" %d ", map[k][l]);
		}
		puts("");
	}
	fflush(stdout);
}

int x;
PLAYER *p;

void test(char *message, int nbCases, int expected) {
	printf("Test n°%d : ", x);
	x++;
	int verif = checkCasesCorrectes(p, message, nbCases);
	printf(" -> : %d", verif);
	if (verif == expected)
		puts(". OK");
	else
		puts(". PAS NORMAL !");
	fflush(stdout);
}

void testplacementShip(void) {

	x = 1;
	puts("----------DEBUT DES TESTS PLACEMENT SHIP----------");
	p = (PLAYER*) malloc(sizeof(PLAYER));
	p->nom = "Vincent";
	int i, j;
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			p->carte[i][j] = 0;
		}
	}

	// tests des bateaux à 1 case
	test("PUTSHIP/0/A/\n", 1, 1); // 1
	test("PUTSHIP/0/A/\n", 1, -1);
	test("PUTSHIP/0/P/\n", 1, 1);
	test("PUTSHIP/15/P/\n", 1, 1);
	test("PUTSHIP/15/P/\n", 1, -1); // 5
	test("PUTSHIP/15/A/\n", 1, 1);
	test("PUTSHIP/14/O/\n", 1, 1);
	test("PUTSHIP/99/O/\n", 1, -1);
	test("PUTSHIP/99/Z/\n", 1, -1);
	test("PUTSHIP/9/AAA/\n", 1, -1); // 10
	test("PUTSHIP/A/2/\n", 1, -1);
	test("PUTSHIP/to/2/\n", 1, -1);

	// tests des bateaux à 2 cases
	test("PUTSHIP/2/A/3/A/\n", 2, 1);
	test("PUTSHIP/4/A/3/A/\n", 2, -1);
	test("PUTSHIP/5/A/4/A/\n", 2, 1); // 15
	test("PUTSHIP/6/C/6/D/\n", 2, 1);
	test("PUTSHIP/7/D/9/D/\n", 2, -1);
	test("PUTSHIP/15/D/16/D/\n", 2, -1);
	test("PUTSHIP/0/D/-1/D/\n", 2, -1);
	test("PUTSHIP/15/P/15/Q/\n", 2, -1);
	test("PUTSHIP/8/G/9/H/\n", 2, -1);

	// tests des bateaux à 3 cases
	// de bas en haut dans l'ordre
	test("PUTSHIP/3/G/3/H/3/I/\n", 3, 1);
	test("PUTSHIP/4/O/4/P/4/Q/\n", 3, -1);
	test("PUTSHIP/2/A/2/B/2/C/\n", 3, -1);
	test("PUTSHIP/5/A/5/C/5/D/\n", 3, -1);
	// de haut en bas dans l'ordre
	test("PUTSHIP/4/P/4/O/4/N/\n", 3, 1);
	test("PUTSHIP/4/P/4/N/4/M/\n", 3, -1);
	test("PUTSHIP/4/N/4/M/4/L/\n", 3, -1);
	// a la verticale dans le désordre
	test("PUTSHIP/7/J/7/K/7/I/\n", 3, 1);
	test("PUTSHIP/9/J/9/I/9/K/\n", 3, 1);
	test("PUTSHIP/10/A/10/C/10/B/\n", 3, -1);
	test("PUTSHIP/10/C/10/A/10/B/\n", 3, -1);
	// de gauche a droite
	test("PUTSHIP/3/F/4/F/5/F/\n", 3, 1);
	test("PUTSHIP/5/F/6/F/7/F/\n", 3, -1);
	test("PUTSHIP/14/O/15/O/16/O/\n", 3, -1);
	// de droite à gauche
	test("PUTSHIP/10/E/9/E/8/E/\n", 3, 1);
	test("PUTSHIP/8/E/7/E/6/E/\n", 3, -1);
	test("PUTSHIP/2/E/1/E/0/E/\n", 3, 1);
	// à l'horizontal dans le désordre
	test("PUTSHIP/14/C/15/C/13/C/\n", 3, 1);
	test("PUTSHIP/12/C/10/C/11/C/\n", 3, -1);
	test("PUTSHIP/10/C/12/E/11/C/\n", 3, -1);

	// on réinitialise sa map parce que je ne m'y repère plus !!
	for (i = 0; i < 16; i++) {
		for (j = 0; j < 16; j++) {
			p->carte[i][j] = 0;
		}
	}

	// tests des bateaux à 4 cases
	// de bas en haut dans l'ordre
	test("PUTSHIP/3/G/3/H/3/I/3/J/\n", 4, 1);
	test("PUTSHIP/4/O/4/P/4/Q/4/R/\n", 4, -1);
	test("PUTSHIP/2/A/2/B/2/C/2/D/\n", 4, 1);
	// de haut en bas dans l'ordre
	test("PUTSHIP/4/P/4/O/4/N/4/M/\n", 4, 1);
	test("PUTSHIP/4/P/4/N/4/M/4/L/\n", 4, -1);
	test("PUTSHIP/4/N/4/M/4/L/4/K/\n", 4, -1);
	// a la verticale dans le désordre
	test("PUTSHIP/7/J/7/K/7/I/7/L/\n", 4, 1);
	test("PUTSHIP/9/J/9/I/9/K/9/H/\n", 4, 1);
	test("PUTSHIP/10/A/10/C/10/B/10/D/\n", 4, -1);
	test("PUTSHIP/10/C/10/A/10/B/10/D/\n", 4, -1);
	// de gauche a droite
	test("PUTSHIP/3/F/4/F/5/F/6/F/\n", 4, 1);
	test("PUTSHIP/6/F/7/F/8/F/9/F/\n", 4, -1);
	test("PUTSHIP/14/O/15/O/16/O/17/O/\n", 4, -1);
	// de droite à gauche
	test("PUTSHIP/10/E/9/E/8/E/7/E/\n", 4, 1);
	test("PUTSHIP/8/E/7/E/6/E/5/E/\n", 4, -1);
	test("PUTSHIP/3/E/2/E/1/E/0/E/\n", 4, 1);
	// à l'horizontal dans le désordre
	test("PUTSHIP/14/C/15/C/13/C/12/C/\n", 4, 1);
	test("PUTSHIP/12/C/10/C/11/C/10/C/\n", 4, -1);
	test("PUTSHIP/9/C/11/E/10/C/8/C/\n", 4, -1);


	//printMap(p->carte);
	free(p);
}
