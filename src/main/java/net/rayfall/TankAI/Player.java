package net.rayfall.TankAI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player {
	
	private String name;
	private int score;
	private JSONArray tanks;
	private boolean friendly;
	
	public Player(JSONObject player){
		try{
			this.name = player.getString("name");
			this.score = player.getInt("score");
			this.tanks = player.getJSONArray("tanks");
		}
		catch(JSONException e) {
			System.err.println("[Building Player Object] JSON Error");
		}
		if(this.name.equalsIgnoreCase("nuke")){
			friendly = true;
		}
		else{
			friendly = false;
		}
	}
	
	public String getTeamName(){
		return this.name;
	}
	public int getScore(){
		return this.score;
	}
	public boolean isFriendly(){
		return this.friendly;
	}
	public JSONArray getTankArray(){
		return this.tanks;
	}

}
