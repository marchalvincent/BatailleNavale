server: util.o main.o game.o talk.o server.o putShip.o register.o serverHttp.o includeGlobal.o testPlacementShip.o
	gcc -Wall -L ./ft64/lib/ -O3 -o server includeGlobal.o main.o game.o putShip.o server.o util.o testPlacementShip.o talk.o register.o serverHttp.o -lfthread -pthread
	rm -f *.o *~
main.o: main.c includeGlobal.c testPlacementShip.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c main.c
includeGlobal.o: includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c includeGlobal.c
game.o: game.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c game.c
putShip.o: putShip.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c putShip.c
testPlacementShip.o: testPlacementShip.c putShip.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c testPlacementShip.c
server.o: server.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c server.c
util.o: util.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c util.c
talk.o: talk.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c talk.c
register.o: register.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c register.c
serverHttp.o: serverHttp.c includeGlobal.c
	gcc -Wall -I ./ft64/include/ -I ./include/ -c serverHttp.c

