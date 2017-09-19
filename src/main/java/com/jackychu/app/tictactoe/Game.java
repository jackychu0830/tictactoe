package com.jackychu.app.tictactoe;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  This class store the tic-tac-toe (3x3) game status.<br>
 *  Each cell on the board is one of three kind of values: 0 for no mark, 1 for player 1, 2 for player 2.<br>
 *  Players who is O or X depends on UI representation<br>
 *  TODO: Enhance to support dimension larger than 3
 */
@XmlRootElement
public class Game implements Serializable {
	/**
	 * UUID of games instance
	 */
	private String id;

	/**
	 * The game board dimension.
	 * Default is 3x3. Can extend to 5x5, 7x7 in the future.
	 */
	private int dimension = 3;

	/**
	 * The game board. 0 for no mark, 1 for mark O, 2 for mark X.
	 */
	private int[][] grid;

	/**
	 * The status of Game. START, PLAYING and END
	 */
	private Status status;

	/**
	 * The winner in this game. 0 for no winner. 1 or 2 for Winner
	 */
	private int winner;

	/**
	 * The time for check game playing sequence.
	 */
	private long lastUpdateTime;

	/**
	 * The status of game. START, PLAYING and END
	 */
	public enum Status {
		START, PLAYING, END
	}

    /**
     * Default create 3x3 game.
     */
	public Game() {
		this(3);
	}

    /**
     * Create nxn game with input dimension. <br>
     * TODO: If want to extend dimension larger than 3, need check the dimension is odd number
     * @param dimension The dimension of game board
     */
	public Game(int dimension) {
		this.grid = new int[dimension][dimension];
		this.id = java.util.UUID.randomUUID().toString();
		this.status = Status.START;
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public int getDimension() {
		return this.dimension;
	}

	// After game object created. The dimension cannot change
	//public void setDimension(int dimension) {
	//	this.dimension = dimension;
	//}

	public int getWinner() {
		return this.winner;
	}
	
	public void setWinner(int winner) {
		this.winner = winner;
	}

	public int[][] getGrid() {
		return this.grid;
	}
	
	public void setGrid(int[][] grid) {
		this.grid = grid;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status s) {
		this.status = s;
	}

    /**
     * The lastUpdateTime cannot change by user directly.<br>
     * When call this method. The lastUpdateTime will be set to current time millis.<br>
     * To avoid bad time format and time sequence.
     */
	public void updateTime() {
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

    /**
     * Convert game instance to JSON format.
     * @return json string of game
     * @throws Exception Convert fail
     */
	public String toJson() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(this);
		return jsonString;
	}

    /**
     * Override toString method. Use json format represent game object.
     * @return json string os this game object
     */
    @Override
    public String toString(){
	    try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(this);
            return jsonString;
	    } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Build game object with json string
     * @param jsonString The json string which present game object
     * @return Game object
     * @throws Exception Convert fail
     */
	public static Game fromJson(String jsonString) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		Game g = mapper.readValue(jsonString, Game.class);
		return g;
	}

    /**
     * Compare current game with lastest game.<br>
     * 1. The id should be the same<br>
     * 2. the dimension should be the same<br>
     * 3. lastest game's lastUpdateTime should be greater then current game<br>
     * 4. Game status check<br>
     * 5. grid data check<br>
     *
     * @param latestGame latest game object
     * @return true for the latest game is the next status of current game.
     */
	public boolean validate(Game latestGame) {
		if (!this.id.equals(latestGame.getId())) return false;
		if (this.dimension != latestGame.getDimension()) return false;
		if (this.lastUpdateTime > latestGame.getLastUpdateTime()) return false;
		if (this.status == Status.END && latestGame.getStatus() != Status.END) return false;
		if (this.status == Status.PLAYING &&
                !(latestGame.getStatus() == Status.PLAYING || latestGame.getStatus() == Status.END)) return false;
		
		int[][] grid = this.grid;
		int[][] latestGrid = latestGame.getGrid();
		
		int count1 = 0, count2 = 0;
		int latestCount1 = 0, latestCount2 = 0;
		boolean differenceFound = false;
		int difference = 0;
		
		for (int i=0; i<this.dimension; i++) {
			for (int j=0; j<this.dimension; j++) {
			    // The grid value only 0, 1, 2
				if (latestGrid[i][j] != 0 && latestGrid[i][j] != 1 && latestGrid[i][j] != 2) return false;
				if (grid[i][j] == 1) count1++;
				if (grid[i][j] == 2) count2++;
				if (latestGrid[i][j] == 1) latestCount1++;
				if (latestGrid[i][j] == 2) latestCount2++;

				// Only one grid cell difference with current game
                // In other word, only one move after last game status
				if (grid[i][j] != latestGrid[i][j]) {
					if (differenceFound) return false; //Only allow one difference
					difference = latestGrid[i][j];
					differenceFound = true;
				}
			}
		}

        // Two games are the same status
		if(!differenceFound) return false;

        // If the difference is 1, then check player 1 has one more move than player 2
		if (difference == 1) {
			if (!((count1 + 1 == latestCount1) && (count2 == latestCount2) && (latestCount1 -1 == latestCount2))) return false;
		} else {
            // If the difference is 2, then check player 1 has same count of move of player 2
			if (!((count1 == latestCount1) && (count2 + 1 == latestCount2) && (latestCount1 == latestCount2))) return false;
		}
		
		return true;
	}

    /**
     * Check who is the winner
     * @return 0 for no winner (maybe draw or the game still playing). 1 for player 1, 2 for player 2.
     */
	public int checkWinner() {
		int[][] grid = this.grid;
		int d = this.dimension;
		
		//Check Columns
		for (int i=0; i<d; i++) {
			boolean win = true;
			for(int j=0; j<d-1; j++) {
				win = win && grid[i][j] != 0 && grid[i][j] == grid[i][j+1];
				if (!win) break;
			}

			if (win) {
				this.winner = grid[i][0];
				return this.winner;
			}
		}
		
		//Check Rows
		for (int j=0; j<d; j++) {
			boolean win = true;
			for(int i=0; i<d-1; i++) {
				win = win && grid[i][j] != 0 && grid[i][j] == grid[i+1][j];
				if (!win) break;
			}

			if (win) {
				this.winner = grid[0][j];
				return this.winner;
			}
		}
		
		//Check Diagonals
		boolean win = true;
		for (int i=0; i<d-1; i++) {
			win = win && grid[i][i] != 0 && grid[i][i] == grid[i+1][i+1];
			if (!win) break;
		}

		if (win) {
			this.winner = grid[0][0];
			return this.winner;
		}

		//Check Diagonals2
		win = true;
		for (int i=0; i<d-1; i++) {
			int j = d - 1 - i;
			win = win && grid[i][j] != 0 && grid[i][j] == grid[i+1][j-1];
			if (!win) break;
		}

		if (win) {
			this.winner = grid[0][d-1];
			return this.winner;
		}
		
		return 0;
	}

    /**
     * Check the game board's cells were be placed.
     * @return true for game board cells were all placed.
     */
	public boolean checkFull() {
		int d = this.dimension;
		
		for (int i=0; i<d; i++) {
			for(int j=0; j<d; j++) {
				if (this.grid[i][j] == 0) return false;
			}
		}
		
		return true;
	}
}