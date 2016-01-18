package net.rayfall.TankAI;

import java.util.ArrayList;

import net.rayfall.TankAI.Position;
import net.rayfall.TankAI.Projectile;
import net.rayfall.TankAI.Enums.MovementDirection;
import net.rayfall.TankAI.Enums.OutgoingControls;
import net.rayfall.TankAI.Enums.RotationDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tank {
	
	private Number speed;
	private String id;
	private Number health;
	private Number hitRadius;
	private Number collisionRadius;
	private String type;
	private Position position;
	private boolean alive;
	private Number tracks;
	private Number turrent;
	private ArrayList<Projectile> projectileList;
	
	
	public Tank(JSONObject tank){
		try{
			this.id = tank.getString("id");
			this.health = tank.getInt("health");
			this.hitRadius = (Number) tank.get("hitRadius");
			this.collisionRadius = (Number) tank.get("collisionRadius");
			this.type = tank.getString("type");
			this.alive = tank.getBoolean("alive");
			this.position = new Position(tank.getJSONArray("position")); 
			this.tracks = tank.getDouble("tracks");
			this.turrent = tank.getDouble("turret");
			this.speed = tank.getDouble("speed");
			JSONArray ProjList = tank.getJSONArray("projectiles");
			projectileList = new ArrayList<Projectile>();
			for(int i = 0; i < ProjList.length(); i++){
				try {
					if(ProjList.isNull(i)){
						continue;
					}
					projectileList.add(new Projectile(ProjList.getJSONObject(i)));
				} catch(JSONException e) {
					System.err.println("[Projectile List] JSON Error" + e);
				}
			}
			
		}
		catch(JSONException e) {
			System.err.println("[Building Object] JSON Error " + e);
		}
		
	}
	
	/**
	 * Send a command to the game server to move the tank.
	 *
	 * @param direction Use the MovementDirection enum in order to safely define the direction for the tank to move.
	 * @param distance The distance to move the tank in meters.
	 *
	 * @author eyesniper2
	 *
	 */
	public void moveTank(MovementDirection direction, Number distance){
		JSONObject move = new JSONObject();
		try {
			move.put("tank_id", this.id);
			move.put("comm_type", OutgoingControls.MOVE.getKey());
			move.put("direction", direction.getKey());
			move.put("distance", distance);
			move.put("client_token", Client.clientToken);
		} catch(JSONException e) {
			System.err.println("[Command move] couldn't create command");
		}
		Client.comm.sendCMD(move.toString(), Client.clientToken);
		
	}
	/**
	 * Send a command to the game server to rotate the tank.
	 * 
	 * @param direction Use the RotationDirection enum in order to safely define the direction for the tank to rotate. 
	 * @param rotateValue The value of the rotation in rads from [0,2*Pi]
	 * 
	 * @author eyesniper2
	 *
	 */
	public void rotateTank(RotationDirection direction, Number rotateValue){
		JSONObject move = new JSONObject();
		try {
			move.put("tank_id", this.id);
			move.put("comm_type", OutgoingControls.ROTATE.getKey());
			move.put("direction", direction.getKey());
			move.put("rads", rotateValue);
			move.put("client_token", Client.clientToken);
		} catch(JSONException e) {
			System.err.println("[Command rotate] couldn't create command");
		}
		Client.comm.sendCMD(move.toString(), Client.clientToken);
	}
	/**
	 * Send a command to the game server to rotate the tanks turret.
	 * 
	 * @param direction Use the RotationDirection enum in order to safely define the direction for the tanks turret to rotate. 
	 * @param rotateValue The value of the rotation in rads from [0,2*Pi]
	 * 
	 * @author eyesniper2
	 *
	 */
	public void rotateTurret(RotationDirection direction, Number rotateValue){
		JSONObject move = new JSONObject();
		try {
			move.put("tank_id", this.id);
			move.put("comm_type", OutgoingControls.ROTATE_TURRET.getKey());
			move.put("direction", direction.getKey());
			move.put("rads", rotateValue);
			move.put("client_token", Client.clientToken);
		} catch(JSONException e) {
			System.err.println("[Command rotate turrent] couldn't create command");
		}
		Client.comm.sendCMD(move.toString(), Client.clientToken);
	}
	/**
	 * Send a command to the game server to fire a single shot from the tank.
	 *
	 * @author eyesniper2
	 *
	 */
	public void fire(){
		JSONObject move = new JSONObject();
		try {
			move.put("tank_id", this.id);
			move.put("comm_type", OutgoingControls.FIRE.getKey());
			move.put("client_token", Client.clientToken);
		} catch(JSONException e) {
			System.err.println("[Command fire] couldn't create command");
		}
		Client.comm.sendCMD(move.toString(), Client.clientToken);
	}
	/**
	 * Send a command to the game server to halt a current action from a tank.
	 * 
	 * @param actionToStop Use the OutgoingControls enum in order to safely define the action to stop. 
	 * 
	 * @author eyesniper2
	 *
	 */
	public void stop(OutgoingControls actionToStop){
		JSONObject move = new JSONObject();
		try {
			move.put("tank_id", this.id);
			move.put("comm_type", "STOP");
			move.put("control", actionToStop.getKey());
			move.put("client_token", Client.clientToken);
		} catch(JSONException e) {
			System.err.println("[Command Stop] couldn't create command");
		}
		Client.comm.sendCMD(move.toString(), Client.clientToken);
	}
	
	/**
	 * Get the position of the tank
	 * 
	 * @return The position of the tank
	 * 
	 * @author eyesniper2
	 */
	public Position getPosition(){
		return this.position;
	}
	/**
	 * Get health of the tank
	 * 
	 * @return The current health of the tank
	 * 
	 * @author eyesniper2
	 */
	public Number getHealth(){
		return this.health;
	}
	/**
	 * Get the speed of the tank
	 * 
	 * @return The speed of the tank
	 * 
	 * @author eyesniper2
	 */
	public Number getSpeed(){
		return this.speed;
	}
	/**
	 * Get the ID of the tank
	 * 
	 * @return The ID of the tank
	 * 
	 * @author eyesniper2
	 */
	public String getTankID(){
		return this.id;
	}
	/**
	 * Get the type of the tank
	 * 
	 * @return The type of the tank
	 * 
	 * @author eyesniper2
	 */
	public String getTankType(){
		return this.type;
	}
	/**
	 * Gets the radius around the tank used to detect a projectile collision.
	 * 
	 * @return The radius in metres
	 * 
	 * @author eyesniper2
	 */
	public Number getHitRadius(){
		return this.hitRadius;
	}
	/**
	 * Gets the radius around the tank used to detect a collision with a stationary object.
	 * 
	 * @return The radius in metres
	 * 
	 * @author eyesniper2
	 */
	public Number getCollisionRadius(){
		return this.collisionRadius;
	}
	/**
	 * Check if the tank is still alive.
	 * 
	 * @return If the tank is alive.
	 * 
	 * @author eyesniper2
	 */
	public boolean isAlive(){
		return this.alive;
	}
	/**
	 * Gets the angle of travel for tank relative to the (1,0) unit vector.
	 * 
	 * @return Tank angle in radians
	 * 
	 * @author eyesniper2
	 */
	public Number getTankRotation(){
		return this.tracks;
	}
	/**
	 * Gets the angle the tanks turret relative to the (1,0) unit vector.
	 * 
	 * @return Turret's angle in radians
	 * 
	 * @author eyesniper2
	 */
	public Number getTurretRotation(){
		return this.turrent;
	}
	/**
	 * Gets the list of Projectiles
	 * 
	 * @return The list of Projectiles
	 * 
	 * @author eyesniper2
	 */
	public ArrayList<Projectile> getProjectiles(){
		return this.projectileList;
	}
	

}
