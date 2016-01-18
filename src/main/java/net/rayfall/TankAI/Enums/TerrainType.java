package net.rayfall.TankAI.Enums;

public enum TerrainType {
	
	SOLID, IMPASSIBLE;
	
	public static TerrainType getFromString(String test){
		if(test.equalsIgnoreCase("solid")){
			return SOLID;
		}
		else if(test.equalsIgnoreCase("impassable")){
			return IMPASSIBLE;
		}
		else 
			return null;
	}

}
