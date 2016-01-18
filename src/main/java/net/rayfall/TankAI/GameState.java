package net.rayfall.TankAI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState{
	
	public JSONObject map;
	public JSONArray players;
	private Number timeRemaining;
	private Number timestamp;
	
	
	public GameState(JSONObject gameState){
		//Parse game state into usable info
		String state = null;
		try{
			state = gameState.getString("comm_type");
		}
		catch(JSONException e) {
			System.err.println("[Building Game State Object] State JSON Error");
		}
		if(state.equals("GAMESTATE")){
			try{
				this.players = gameState.getJSONArray("players");
				this.map = gameState.getJSONObject("map");
				this.timeRemaining = (Number)gameState.get("timeRemaining");
				this.timestamp = (Number)gameState.get("timestamp");
			}
			catch(JSONException e) {
				System.err.println("[Building Game State Object] JSON Error" + e);
			}
		}
		else{
			System.out.println("Failed to build GAMESTATE! Refetching to try to build again!");
			updateGameState(Client.comm.getJSONGameState());
		}
	}
	


	public void updateGameState(JSONObject gameState){
		//Parse game state into usable info
		try{
			this.players = gameState.getJSONArray("players");
			this.map = gameState.getJSONObject("map");
			this.timeRemaining = (Number)gameState.get("timeRemaining");
			this.timestamp = (Number)gameState.get("timestamp");
		}
		catch(JSONException e) {
			System.err.println("[Updating Game State] JSON Error");
		}
	}
	
	public Number getTimeRemaining(){
		return this.timeRemaining;
	}
	
	public Number getTimestamp(){
		return this.timestamp;
	}
	
	public JSONArray getPlayersRaw(){
		return this.players;
	}

}
