package net.rayfall.TankAI.Enums;

public enum RotationDirection {
	
	CW("CW"),
	CCW("CCW");
	
	private String key;

	
	private RotationDirection(String key){
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}
}
