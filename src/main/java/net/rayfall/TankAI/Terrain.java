package net.rayfall.TankAI;

import net.rayfall.TankAI.Enums.TerrainType;

import org.json.JSONException;
import org.json.JSONObject;

public class Terrain {
	
	private Region region;
	private TerrainType type;
	
	public Terrain(JSONObject ter){
		try{
			this.region = new Region(ter.getJSONObject("boundingBox"));
			this.type = TerrainType.getFromString(ter.getString("type"));
		}
		catch(JSONException e) {
			System.err.println("[Building Terrain] JSON Error " + e);
		}
	}
	
	public Region getRegion(){
		return this.region;
	}
	
	public TerrainType getTerrainType(){
		return this.type;
	}
	

}
