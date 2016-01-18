package net.rayfall.TankAI;

import net.rayfall.TankAI.Enums.MovementDirection;

import org.json.JSONArray;
import org.json.JSONException;

public class Position {
	
	private Number x;
	private Number y;
	private MovementDirection lastDirection;
	
	public Position(Number x, Number y){
		this.x = x;
		this.y = y;
	}
	
	public Position(JSONArray pos){
		for(int i = 0; i < 2; i++){
			try{
			if(i == 0){
				this.x = pos.getDouble(i);
			}
			else if(i == 1){
				this.y = pos.getDouble(i);
			}
			}
			catch(JSONException e) {
				System.err.println("[Building Tank Position] JSON Error");
			}
		}
	}
	
	public Number getXPosition(){
		return this.x;
	}
	
	public Number getYPosition(){
		return this.y;
	}
	
	public void setXPosition(Number newPos){
		this.x = newPos;
	}
	
	public void setYPosition(Number newPos){
		this.y = newPos;
	}
	
	public String toString(){
		String r = "X Position: " + this.x + " Y Position: " + this.y; 
		return r;
	}
	
	public void setLastDirection(MovementDirection direction){
		this.lastDirection = direction;
	}
	
	public MovementDirection getLastDirection(){
		return this.lastDirection;
	}

}