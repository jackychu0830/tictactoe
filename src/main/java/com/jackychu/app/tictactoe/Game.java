package com.jackychu.app.tictactoe;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement
public class Game implements Serializable {
	private String id;
	private int dimension = 3;
	private int[][] grid;
	private Status status;
	private int winner;
	private long lastUpdateTime;
	
	public static enum Status {
		START, PLAYING, END
	}
	
	public Game() {
		this(3);
	}
	
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
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

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

	public void updateTime() {
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}
	
	public String toJson() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(this);
		return jsonString;
	}

	public static Game fromJson(String jsonString) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		Game g = mapper.readValue(jsonString, Game.class);
		return g;
	}
	
	public boolean validate(Game latestGame) {
		if (this.id != latestGame.getId()) return false;
		if (this.dimension != latestGame.getDimension()) return false;
		if (this.lastUpdateTime > latestGame.getLastUpdateTime()) return false;
		if (this.status == Status.END && latestGame.getStatus() != Status.END) return false;
		if (this.status == Status.PLAYING && latestGame.getStatus() != Status.PLAYING) return false;
		
		int[][] grid = this.grid;
		int[][] latestGrid = latestGame.getGrid();
		
		int count1 = 0, count2 = 0;
		int latestCount1 = 0, latestCount2 = 0;
		boolean differenceFound = false;
		int difference = 0;
		
		for (int i=0; i<this.dimension; i++) {
			for (int j=0; j<this.dimension; j++) {
				if (grid[i][j] == 1) count1++;
				if (grid[i][j] == 2) count2++;
				if (latestGrid[i][j] == 1) latestCount1++;
				if (latestGrid[i][j] == 2) latestCount2++;
				
				if (grid[i][j] != latestGrid[i][j]) {
					if (differenceFound) return false; //Only allow one difference
					difference = latestGrid[i][j];
					differenceFound = true;
				}
			}
		}
		
		if(!differenceFound) return false;
		
		System.out.println(difference);
		if (difference == 1) {
			if (!((count1 + 1 == latestCount1) && (count2 == latestCount2) && (latestCount1 -1 == latestCount2))) return false;
		} else {
			if (!((count1 == latestCount1) && (count2 + 1 == latestCount2) && (latestCount1 == latestCount2))) return false;
		}
		
		return true;
	}
	
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
			//if (!win) continue;
			//win = win && grid[i][d-2] == grid[i][d-1];
			
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
			//if (!win) continue;
			//win = win && grid[d-2][j] == grid[d-1][j];
			
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