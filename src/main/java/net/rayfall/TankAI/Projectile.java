package net.rayfall.TankAI;

import org.json.JSONException;
import org.json.JSONObject;

public class Projectile {
	
	private String id;
	private Position position;
	private Number direction;
	private Number speed;
	private Number damage;
	private Number range;
	
	
	public Projectile(JSONObject proj){
		try{
			this.id = proj.getString("id");
			this.position = new Position(proj.getJSONArray("position")); 
			this.direction = proj.getDouble("direction");
			this.speed = proj.getDouble("speed");
			this.damage = proj.getDouble("damage");
			this.range = proj.getDouble("range");
		}
		catch(JSONException e) {
			System.err.println("[Building Projectile] JSON Error " + e);
		}
	}
	
	public String getID(){
		return this.id;
	}
	
	public Position getPosition(){
		return this.position;
	}
	
	public Number getDirection(){
		return this.direction;
	}
	
	public Number getSpeed(){
		return this.speed;
	}
	
	public Number getDamage(){
		return this.damage;
	}
	
	public Number getRange(){
		return this.range;
	}

}
