package com.jmedemos.astar_pathfinder;

import com.jmedemos.astar_pathfinder.world.Mover;
import com.jmedemos.astar_pathfinder.world.WorldMap;

/**
 * The description for the data we're pathfinding over. 
 */
public class GameMap implements WorldMap{
	
	/** The map width in tiles */
	public static final int WIDTH = 30;
	/** The map height in tiles */
	public static final int HEIGHT = 30;
	
	/** The terrain settings for each tile in the map */
	private int[][] terrain = new int[WIDTH][HEIGHT];
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited = new boolean[WIDTH][HEIGHT];
	
	
	/**
	 * Create a new test map with some default configuration
	 */
	public GameMap() {
		fillArea(0,0,5,5,TERRAIN_WATER);
		fillArea(0,5,3,10,TERRAIN_WATER);
		fillArea(0,5,3,10,TERRAIN_WATER);
		fillArea(0,15,7,15,TERRAIN_WATER);
		fillArea(7,26,22,4,TERRAIN_WATER);
		
		fillArea(17,5,10,3,TERRAIN_TREES);
		fillArea(20,8,5,3,TERRAIN_TREES);
		
		fillArea(8,2,7,3,TERRAIN_TREES);
		fillArea(10,5,3,3,TERRAIN_TREES);
		
	}
	
	
	/**
	 * Fill an area with a given terrain type
	 * 
	 * @param x The x coordinate to start filling at
	 * @param y The y coordinate to start filling at
	 * @param width The width of the area to fill
	 * @param height The height of the area to fill
	 * @param type The terrain type to fill with
	 */
	private void fillArea(int x, int y, int width, int height, int type) {
		for (int xp=x;xp<x+width;xp++) {
			for (int yp=y;yp<y+height;yp++) {
				terrain[xp][yp] = type;
			}
		}
	}
	
	
	/**
	 * Get the cost of moving through the given tile. This can be used to 
	 * make certain areas more desirable. A simple and valid implementation
	 * of this method would be to return 1 in all cases.
	 * 
	 * @param unit The unit that is trying to move across the tile
	 * @param sx The x coordinate of the tile we're moving from
	 * @param sy The y coordinate of the tile we're moving from
	 * @param tx The x coordinate of the tile we're moving to
	 * @param ty The y coordinate of the tile we're moving to
	 * @return The relative cost of moving across the given tile
	 */
	public float getCost(Unit unit, int sx, int sy, int tx, int ty) {
		return 1;
	}
	
	
	public void clearVisited() {
		for (int x=0;x<getWidth();x++) {
			for (int y=0;y<getHeight();y++) {
				visited[x][y] = false;
			}
		}
	}
	
	
	public boolean isVisited(int x, int y) {
		return visited[x][y];
	}
	
	
	public void setVisited(int x, int y) {
		visited[x][y] = true;
	}
	
	
	public int getTerrain(int x, int y) {
		return terrain[x][y];
	}
	
	
	public boolean isBlocked(Mover unit, int x, int y) {
		/* 
		   if theres a unit at the location, then it's blocked
		   TODO: add checking for clocking units
			if (getUnit(x,y) != 0) {
				return true;
			}
		*/
		
		int unitType = ((Unit) unit).getType();
		
		// planes can move anywhere
		if (unitType == PLANE) {
			return false;
		}
		// tanks can only move across grass
		if (unitType == TANK) {
			return terrain[x][y] != TERRAIN_GRASS;
		}
		// boats can only move across water
		if (unitType == BOAT) {
			return terrain[x][y] != TERRAIN_WATER;
		}
		
		// unknown unit so everything blocks
		return true;
	}
	

	public int getHeight() {
		return WIDTH;
	}


	public int getWidth() {
		return HEIGHT;
	}


}
