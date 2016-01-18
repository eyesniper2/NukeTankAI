package net.rayfall.TankAI;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map {
	
	private Number mapSizeX;
	private Number mapSizeY;
	private ArrayList<Terrain> terrainList;

	public Map(JSONObject map) {
		try{
			JSONArray hold = map.getJSONArray("size");
			for(int i = 0; i < 2; i++){
				if(i == 0){
					this.mapSizeX = hold.getDouble(i);
				}
				else if(i == 1){
					this.mapSizeY = hold.getDouble(i);
				}
			}
			JSONArray Terrain = map.getJSONArray("terrain");
			terrainList = new ArrayList<Terrain>();
			for(int i = 0; i < Terrain.length(); i++){
				terrainList.add(new Terrain(Terrain.getJSONObject(i)));
			}
			
		}
		catch(JSONException e) {
			System.err.println("[Building Map] JSON Error " + e + ". Raw JSON: " + map.toString());
		}
	}
	
	public Number getMapSizeX(){
		return this.mapSizeX;
	}
	public Number getMapSizeY(){
		return this.mapSizeY;
	}
	public ArrayList<Terrain> getTerrainList(){
		return this.terrainList;
	}
	


}
