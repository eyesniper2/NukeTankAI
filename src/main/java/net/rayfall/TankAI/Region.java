package net.rayfall.TankAI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Region {
	
	private Number cornerX;
	private Number cornerY;
	private Number sizeX;
	private Number sizeY;
	
	public Region(JSONObject box){
		try{
			JSONArray hold = box.getJSONArray("corner");
			for(int i = 0; i < 2; i++){
				if(i == 0){
					this.cornerX = hold.getDouble(i);
				}
				else if(i == 1){
					this.cornerY = hold.getDouble(i);
				}
			}
			JSONArray hold2 = box.getJSONArray("size");
			for(int i = 0; i < 2; i++){
				if(i == 0){
					this.sizeX = hold2.getDouble(i);
				}
				else if(i == 1){
					this.sizeY = hold2.getDouble(i);
				}
			}
		}
		catch(JSONException e) {
			System.err.println("[Building region] JSON Error " + e);
		}
	}
	
	public Number getCornerX(){
		return this.cornerX;
	}
	public Number getCornerY(){
		return this.cornerY;
	}
	public Number getSizeX(){
		return this.sizeX;
	}
	public Number getSizeY(){
		return this.sizeY;
	}

}
