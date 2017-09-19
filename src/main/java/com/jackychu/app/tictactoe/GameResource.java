package com.jackychu.app.tictactoe;

import java.util.Map;
import java.util.HashMap;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.inject.Inject;
import javax.inject.Provider;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;

/**
 * Game resource (exposed at "/api/v1.0/game" path)
 */
@Path("/api/v1.0/game")
public class GameResource {

	/**
	 * Inject http request object to get session data
	 */
	@Inject
    private Provider<Request> grizzlyRequestProvider;

    /**
     * Store whole games in current session.
     * The key is id of game.
     * The value is 
     */
    private Map<String, Game> games;

    /**
     * Get specific game status by id. <br>
     * If cannot find game with id, then return 404.<br>
     * If id == all, then return whole games in current session<br>
     * @param id the id of game
     * @return game(s)
     */
    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGame(@PathParam("id") String id) {
        //return all games in current session
        if (id.toLowerCase().equals("all")) {
            return Response.status(Response.Status.OK).entity(this.getGames()).build();
        }

    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	} else {
	        return Response.ok(g).build();
	    }
    }

    /**
     * Create a new game
     * @return Game object in json format
     */
    @POST
	@Produces({MediaType.APPLICATION_JSON})
    public Response createGame(){
    	Map<String, Game> games = this.getGames();

    	Game g = new Game();
    	games.put(g.getId(), g);
    	return Response.ok(g).build();
    }

    /**
     * Check and update game status.
     * @param id game id
     * @param currentGame latest game status
     * @return Game object in json format
     */
    @Path("{id}")
    @PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
    public Response updateGame(@PathParam("id") String id, Game currentGame) {
    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	}

		currentGame.updateTime();
		
    	if (currentGame.checkFull()) {
	    	currentGame.setStatus(Game.Status.END);
	    } else {
	    	currentGame.setStatus(Game.Status.PLAYING);
	    }
	    
	    int winner = currentGame.checkWinner();
	    if (winner != 0) {
	    	currentGame.setStatus(Game.Status.END);
	    }

		boolean validate = g.validate(currentGame);
		if (validate) {
			Map<String, Game> games = this.getGames();
			games.put(id, currentGame);
			return Response.ok(currentGame).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorMessage("Game status is invalidate")).build();
		}
    }

    /**
     * Delete game by id
     * @param id Game id
     * @return Response.OK
     */
    @Path("{id}")
    @DELETE
	@Produces({MediaType.APPLICATION_JSON})
    public Response deleteGame(@PathParam("id") String id) {
    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	}
    	
    	Map<String, Game> games = this.getGames();
    	games.remove(id);
    	
    	return Response.ok().build();
    }

    /**
     * Get current http session
     * @return Http session
     */
    private Session getSession() {
    	Request httpRequest = grizzlyRequestProvider.get();
    	Session session = httpRequest.getSession();
    	return session;
    }

    /**
     * Get games map object in session
     * @return games map
     */
    private Map<String, Game> getGames() {
    	Session session = this.getSession();
    	Map<String, Game> games = (Map<String, Game>)session.getAttribute("games");
        if (games == null) {
            games = new HashMap<>();
            session.setAttribute("games", games);
        }
    	return games;
    }

    /**
     * Get game object by id
     * @param id Game id
     * @return Game object
     */
    private Game getGameById(String id) {
    	Map<String, Game> games = this.getGames();
    	if (games == null) {
    		return null;
    	} else {
	    	return games.get(id);
	    }
    }
}
