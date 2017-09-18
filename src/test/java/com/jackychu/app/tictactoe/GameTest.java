package com.jackychu.app.tictactoe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

public class GameTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToJson() {
    	Game g = new Game();
    	g.setId("12345");
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});
    	String jsonString = "{\"id\":\"12345\",\"dimension\":3,\"grid\":[[0,1,2],[2,1,0],[1,0,2]],\"status\":\"START\",\"winner\":0,\"lastUpdateTime\":" + g.getLastUpdateTime() +"}";
    	
    	try {
	    	String jsonString2 = g.toJson();
	    	assertEquals(jsonString, jsonString2);
	    } catch (Exception e) {
	    	fail(e.toString());
	    }
    }
    
    @Test
    public void testFromJson() {
    	int[][] grid = new int[][]{{0,1,2},{2,1,0},{1,0,2}};
    	String jsonString = "{\"dimension\":3,\"grid\":[[0,1,2],[2,1,0],[1,0,2]],\"id\":\"12345\",\"status\":\"START\",\"winner\":0,\"lastUpdateTime\":1}";

    	try {
    		Game g = Game.fromJson(jsonString);
			assertEquals("12345", g.getId());
			assertEquals(3, g.getDimension());
			assertEquals(Game.Status.START, g.getStatus());
			assertEquals(1, g.getLastUpdateTime());
			
			int[][] grid2 = g.getGrid();
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					assertEquals(grid[i][j], grid2[i][j]);
				}
			}
    	} catch (Exception e) {
    		fail(e.toString());
    	}
		
    }

    @Test(expected=Exception.class)
    public void testFromJsonFail() throws Exception {
    	String jsonString = "{\"dimension\":3,\"grid\":[[0,1,2],[2,1,0][1,0,2]],\"id\":\"12345\",\"status\":\"START\",\"winner\":0,\"lastUpdateTime\":1}";
		Game g = Game.fromJson(jsonString);
    }

    @Test
    public void testCheckNotFull() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});
    	boolean full = g.checkFull();
    	assertEquals(false, full);
    }

    @Test
    public void testCheckFull() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{1,2,1},{2,1,2},{1,2,1}});
    	boolean full = g.checkFull();
    	assertEquals(true, full);
    }

    @Test
    public void testNoWinner() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});
    	int winner = g.checkWinner();
    	assertEquals(0, winner);
    }

    @Test
    public void testCheckWinnerAtColumn() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,1,2}});
    	int winner = g.checkWinner();
    	assertEquals(1, winner);
    }

    @Test
    public void testCheckWinnerAtRow() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{2,2,2},{1,1,0},{1,2,0}});
    	int winner = g.checkWinner();
    	assertEquals(2, winner);
    }

    @Test
    public void testCheckWinnerAtDiagonals() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{1,2,2},{0,1,0},{0,0,1}});
    	int winner = g.checkWinner();
    	assertEquals(1, winner);
    }

    @Test
    public void testCheckWinnerAtDiagonals2() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{1,1,2},{1,2,0},{2,0,0}});
    	int winner = g.checkWinner();
    	assertEquals(2, winner);
    }

	//move 1
    @Test
    public void testValidate() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});

    	Game g2 = new Game();
    	g2.setId(g.getId());
    	g2.updateTime();
    	g2.setGrid(new int[][]{{0,1,2},{2,1,1},{1,0,2}});
    	
    	boolean validate = g.validate(g2);
    	assertEquals(true, validate);
    }

	//move 2
    @Test
    public void testValidate2() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,0}});

    	Game g2 = new Game();
    	g2.setId(g.getId());
    	g2.updateTime();
    	g2.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});
    	
    	boolean validate = g.validate(g2);
    	assertEquals(true, validate);
    }

	//Cannot 1 move twice
    @Test
    public void testValidateFail() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,0}});

    	Game g2 = new Game();
    	g2.setId(g.getId());
    	g2.updateTime();
    	g2.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,1}});
    	
    	boolean validate = g.validate(g2);
    	assertEquals(false, validate);
    }

	//Cannot 2 move twice
    @Test
    public void testValidateFail2() {
    	Game g = new Game();
    	g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});

    	Game g2 = new Game();
    	g2.setId(g.getId());
    	g2.updateTime();
    	g2.setGrid(new int[][]{{0,1,2},{2,1,0},{1,2,2}});
    	
    	boolean validate = g.validate(g2);
    	assertEquals(false, validate);
    }


	//value not 0, 1, 2
	@Test
	public void testValidateWrongValue() {
		Game g = new Game();
		g.setGrid(new int[][]{{0,1,2},{2,1,0},{1,0,2}});

		Game g2 = new Game();
		g2.setId(g.getId());
		g2.updateTime();
		g2.setGrid(new int[][]{{0,1,2},{2,1,3},{1,0,2}});

		boolean validate = g.validate(g2);
		assertEquals(false, validate);
	}
}
