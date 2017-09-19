# Tic-Tac-Toe RESTful Service

This is a RESTful service for play tic-tac-toe ganme. It implements by Java with [Jersey framwork](https://jersey.github.io) and [Grizzy web framwork](https://javaee.github.io/grizzly/).

## Getting Start 
### * Running with docker
You don't need clone this project. Just pull the docker image then can use it.
```
  docker pull jackychu/study:tic-tac-toe
```

Then run the image
```
  docker run -p 9090:8080 -t -i jackychu/study:tic-tac-toe
```

The url will be
```
  http://localhost:9090/tictactoe/api/v1.0/game
```

### * GET, POST, PUT and DELETE
This RESTful service is statful. Which mean each new session will store every game status. You can use [curl](https://curl.haxx.se/docs/manpage.html) command or [postman](https://www.getpostman.com) app to use this service. Strong suggest to using postman. It will be easy to handle cookies.
All response data is in json format. There are two different types:

* Game data example:
```json
{"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[0,0,0],[0,0,0],[0,0,0]],"status":"START","winner":0,"lastUpdateTime":1505793050913}
```

* Error massage example:
```json
{"message":"Game not found for ID: 6344700d-62e3-4870-9676-7149ef5e925f"}
```

1. Start new session

    If you just start the service, there is no session data in the system. You can use GET ALL api to start a new session.
    HTTP GET Request URL: http://localhost:9090/tictactoe/api/v1.0/game/all

	curl command:
	
	curl --cookie-jar headers.txt -H "Content-Type: application/json" http://localhost:9090/tictactoe/api/v1.0/game/all
	
	!!!! Only use --cookie-jar at first time !!!!
	
2. Get all games

	http://localhost:9090/tictactoe/api/v1.0/game/all
	
	| Method | URL Parameter | Request Payload | Response Body        |
	|--------|---------------|-----------------|----------------------|
	|  GET   | game id       | Empty           | games map json object|

	Response Example:

	```json
	{"163ee5c9-8e1c-4ef1-b982-cf467354cf9c": {"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[0,0,0],[0,0,0],[0,0,0]],"status":"START","winner":0,"lastUpdateTime":1505793050913}}
	```

	curl command:
	
	curl -b headers.txt -H "Content-Type: application/json" http://localhost:9090/tictactoe/api/v1.0/game/all

3. Create a new game

	http://localhost:9090/tictactoe/api/v1.0/game
	
	| Method | URL Parameter | Request Payload | Response Body   |
	|--------|---------------|-----------------|-----------------|
	|  POST  | None          | Empty           | game json object|
	
	Response Example:
	
	```json
	{"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[0,0,0],[0,0,0],[0,0,0]],"status":"START","winner":0,"lastUpdateTime":1505793050913}|
	```
	curl command:
	
	curl -b headers.txt -H "Content-Type: application/json" -X POST http://localhost:9090/tictactoe/api/v1.0/game

4. Get game

	http://localhost:9090/tictactoe/api/v1.0/game/163ee5c9-8e1c-4ef1-b982-cf467354cf9c
	
	| Method | URL Parameter | Request Payload | Response Body   |
	|--------|---------------|-----------------|-----------------|
	|  GET   | game id       | Empty           | game json object|

	Response Example:

	```json
	{"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[0,0,0],[0,0,0],[0,0,0]],"status":"START","winner":0,"lastUpdateTime":1505793050913}
	```

	curl command:
	
	curl -b headers.txt -H "Content-Type: application/json" http://localhost:9090/tictactoe/api/v1.0/game/163ee5c9-8e1c-4ef1-b982-cf467354cf9c

5. Update game

	http://localhost:9090/tictactoe/api/v1.0/game/163ee5c9-8e1c-4ef1-b982-cf467354cf9c
	
	| Method | URL Parameter | Request Payload | Response Body   |
	|--------|---------------|-----------------|-----------------|
	|  PUT   | game id       | game json object| game json object| 

	Request Payload Example:

	```json
	{"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[1,0,0],[0,0,0],[0,0,0]],"status":"START","winner":0}
	```
		
	Response Example:
	
	```json
	{"id":"163ee5c9-8e1c-4ef1-b982-cf467354cf9c","dimension":3,"grid":[[1,0,0],[0,0,0],[0,0,0]],"status":"PLAYING","winner":0","lastUpdateTime":1505793051888}
	```

	curl command:
	
	curl -b headers.txt -H "Content-Type: application/json" -X PUT -d '{"id":"f7f2eda8-d548-4940-a45b-9728da36cccd","dimension":3,"grid":[[2,1,1],[0,0,0],[0,0,0]]}' http://localhost:9090/tictactoe/api/v1.0/game/f7f2eda8-d548-4940-a45b-9728da36cccd

6. Update game and game end

	There are two situations the game will end. The game status value will be END.
	
	* The game board full
	
		If no winner, then the winner value is 0.
	
	* Has winner
	
		The winner value will be 1 (player 1) or 2 (player 2).

7. Delete game
	
	http://localhost:9090/tictactoe/api/v1.0/game/163ee5c9-8e1c-4ef1-b982-cf467354cf9c
	
	| Method | URL Parameter | Request Payload | Response Body   |
	|--------|---------------|-----------------|-----------------|
	| DELETE | game id       | Empty           | Empty           |


	curl command:

	curl -b headers.txt -H "Content-Type: application/json" -X DELETE  http://localhost:9090/tictactoe/api/v1.0/game/163ee5c9-8e1c-4ef1-b982-cf467354cf9c

### * Build and run locally
You welcome to clone the code to your PC. After you clone out the project. You can use maven to build and run it.

* Build

mvn install

* Run

mvn exec:java

* JavaDoc

The javadoc already includes in the repository. 

mvn javadoc:javadoc

* Test JavaDoc

You also can generate test-javadoc for Test classes.

mvn javadoc:test-javadoc
