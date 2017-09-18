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
 * Root resource (exposed at "game" path)
 */
@Path("game")
public class GameResource {
   	
   	@Inject
    private Provider<Request> grizzlyRequestProvider;
    
    private Map<String, Game> games;
    
    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGame(@PathParam("id") String id) {
    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	} else {
	        return Response.ok(g).build();
	    }
    }
    
    @POST
	@Produces({MediaType.APPLICATION_JSON})
    public Response createGame() throws Exception {
    	Map<String, Game> games = this.getGames();
    	if (games == null) {
    		games = new HashMap<String, Game>();
	    	Session session = this.getSession();
    		session.setAttribute("games", games);
    	}
    	
    	Game g = new Game();
    	games.put(g.getId(), g);
    	return Response.ok(g).build();
    }

    @Path("{id}")
    @PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
    public Response updateGame(@PathParam("id") String id, Game currentGame) throws Exception {
    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	}

    	if (currentGame.checkFull()) {
	    	currentGame.setStatus(Game.Status.END);
	    } else {
	    	currentGame.setStatus(Game.Status.PLAYING);
	    }
	    
	    int winner = currentGame.checkWinner();
	    if (winner != 0) {
	    	currentGame.setStatus(Game.Status.END);
	    }

		Map<String, Game> games = this.getGames();
		games.put(id, currentGame);
    	return Response.ok(currentGame).build();
    }

    @Path("{id}")
    @DELETE
	@Produces({MediaType.APPLICATION_JSON})
    public Response deleteGame(@PathParam("id") String id) throws Exception {
    	Game g = this.getGameById(id);
    	if (g == null) {
    		return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage("Game not found for ID: " + id)).build();
    	}
    	
    	Map<String, Game> games = this.getGames();
    	games.remove(id);
    	
    	return Response.ok().build();
    }
    
    private Session getSession() {
    	Request httpRequest = grizzlyRequestProvider.get();
    	Session session = httpRequest.getSession();
    	return session;
    }
    
    private Map<String, Game> getGames() {
    	Session session = this.getSession();
    	Map<String, Game> games = (Map<String, Game>)session.getAttribute("games");
    	return games;
    }
    
    private Game getGameById(String id) {
    	Map<String, Game> games = this.getGames();
    	if (games == null) {
    		return null;
    	} else {
	    	return games.get(id);
	    }
    }
}
