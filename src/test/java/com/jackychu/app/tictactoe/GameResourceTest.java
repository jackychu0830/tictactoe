package com.jackychu.app.tictactoe;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GameResourceTest {

    private HttpServer server;
    private WebTarget target;
    private NewCookie cookie;

    /**
     * Start web server and request a new session of games.
     * @throws Exception start server up fail
     */
    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client client = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        //client.getConfiguration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = client.target(Main.BASE_URI);

        //New session, get session id in cookie
        Response res = target.path("api/v1.0/game/all").request().get();
        Map<String, NewCookie> cookies = res.getCookies();
        cookie  = cookies.get("JSESSIONID");
    }

    /**
     * Shutdown the server
     * @throws Exception Shutdown server fail
     */
    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Test get all games.<br>
     * At begin, no games.<br>
     * Then create two games and check size is 2
     */
    @Test
    public void testGetAllGames() {
        //Empty list
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game/all").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).get();
        String body = res.readEntity(String.class);
        assertEquals("{}", body);

        //Create new Game 1
        invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).post(Entity.text(""));

        //Create new Game 2
        invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).post(Entity.text(""));

        invocationBuilder = target.path("api/v1.0/game/all").request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).get();
        Map<String, Game> games = res.readEntity(Map.class);
        int count = games.keySet().size();
        assertEquals(count, 2);
    }

    /**
     * Get the game with id which doesn't exist.
     */
    @Test
    public void testGetGameNotFound() {
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game/abc").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).get();
		assertEquals("Should return status 404", 404, res.getStatus());
    }

    /**
     *  Get the game with id.
     */
    @Test
    public void testGetGame() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        //Get it
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).get();
        Game g = res.readEntity(Game.class);
        assertNotNull(g);
        assertEquals(g.getId(), gameForTest.getId());
    }

    /**
     * Test create new game and game status is START
     */
    @Test
    public void testCreateNewGame() {
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);
        assertNotNull(gameForTest);
        assertEquals(Game.Status.START, gameForTest.getStatus());
    }

    /**
     * Test update game status. The game is valid and status is PLAYING
     */
    @Test
    public void testUpdateGame() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        //Update it
        gameForTest.getGrid()[0][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));
        Game g = res.readEntity(Game.class);
        assertNotNull(g);
        assertEquals(g.getId(), gameForTest.getId());
        assertEquals(g.getStatus(), Game.Status.PLAYING);
    }

    /**
     * Test game winner is player 1
     */
    @Test
    public void testUpdateGameAndWin1() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        //Update it
        gameForTest.getGrid()[0][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[0][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[2][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        Game g = res.readEntity(Game.class);
        assertEquals(g.getId(), gameForTest.getId());
        assertEquals(g.getWinner(), 1);
        assertEquals(g.getStatus(), Game.Status.END);
    }

    /**
     * Test game winner is player 2
     */
    @Test
    public void testUpdateGameAndWin2() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        //Update it
        gameForTest.getGrid()[0][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[0][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[0][2] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[2][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        Game g = res.readEntity(Game.class);
        assertEquals(g.getId(), gameForTest.getId());
        assertEquals(g.getWinner(), 2);
        assertEquals(g.getStatus(), Game.Status.END);
    }

    /**
     * Test game with draw
     */
    @Test
    public void testUpdateGameAndDraw() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        //Update it
        gameForTest.getGrid()[0][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[0][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][0] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][1] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[0][2] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[1][2] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[2][2] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[2][0] = 2;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        gameForTest.getGrid()[2][1] = 1;
        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).put(Entity.json(gameForTest));

        Game g = res.readEntity(Game.class);
        assertEquals(g.getId(), gameForTest.getId());
        assertEquals(g.getWinner(), 0);
        assertEquals(g.getStatus(), Game.Status.END);
    }

    /**
     * Test delete game with id, but id doesn't exist.
     */
    @Test
    public void testDeleteGameNotFound() {
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game/abc").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).delete();
		assertEquals("Should return status 404", 404, res.getStatus());
    }

    /**
     * Test delete game success.
     */
    @Test
    public void testDeleteGame() {
        //Create new Game
        Invocation.Builder invocationBuilder = target.path("api/v1.0/game").request(MediaType.APPLICATION_JSON);
        Response res = invocationBuilder.cookie(cookie).post(Entity.text(""));
        Game gameForTest = res.readEntity(Game.class);

        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).delete();
        assertEquals("Should return status 200",200, res.getStatus());

        invocationBuilder = target.path("api/v1.0/game/" + gameForTest.getId()).request(MediaType.APPLICATION_JSON);
        res = invocationBuilder.cookie(cookie).get();
        assertEquals("Should return status 404", 404, res.getStatus());
    }
}
