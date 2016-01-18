package net.rayfall.TankAI.Enums;

public enum OutgoingControls {
	MOVE("MOVE"), ROTATE("ROTATE"), ROTATE_TURRET("ROTATE_TURRET"), FIRE("FIRE");
	
	private String key;
	
	private OutgoingControls(String key){
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}

}
