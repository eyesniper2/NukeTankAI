package net.rayfall.TankAI;

import org.json.JSONException;
import org.json.JSONObject;

public class Match {
	
	private int gameNumber;
	private Number timestamp;
	private String gameName;
	private int gameCount;
	
	
	public Match(JSONObject start){
		try {
			this.gameName = start.getString("game_name");
			this.timestamp = (Number)start.get("timestamp");
			this.gameNumber = start.getInt("game_num");
			this.gameCount = start.getInt("game_count");
		} catch(JSONException e) {
			System.err.println("[Match] couldn't parse data");
		}
	}
	
	public Match(){
		this.gameName = "Game in progress";
		this.timestamp = 0;
		this.gameNumber = 0;
		this.gameCount = 5;

	}
	
	public void updateMatch(JSONObject match){
		try {
			this.gameName = match.getString("game_name");
			this.timestamp = (Number)match.get("timestamp");
			this.gameNumber = match.getInt("game_num");
			this.gameCount = match.getInt("game_count");
		} catch(JSONException e) {
			System.err.println("[Match] couldn't parse data");
		}
	}
	
	public int getGameNumber(){
		return this.gameNumber;
	}
	
	public int getGameCount(){
		return this.gameCount;
	}
	
	public Number getTimestamp(){
		return this.timestamp;
	}
	
	public String getGameName(){
		return this.gameName;
	}

}
