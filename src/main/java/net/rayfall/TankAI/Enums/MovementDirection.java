package net.rayfall.TankAI.Enums;

public enum MovementDirection {
	FWD("FWD"), REV("REV");
	
	private String key;
	
	private MovementDirection(String key){
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}

}
