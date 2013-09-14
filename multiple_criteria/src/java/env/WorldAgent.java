package env;

import jason.asSemantics.Agent;
import jason.environment.grid.Location;


public class WorldAgent extends Agent{

	private WorldModel model;
	private Location l;
	
	// Crea el agente y lo registra en el modelo.
	public WorldAgent(){
		super();
		
		model = WorldModel.getInstance();
		this.l = model.getFreeLocation();
		
		model.addAgent(this);		
	}
	
	// Setters y getters. 
	public void setLocation(int x, int y){
		this.l.x = x;
		this.l.y = y;
	}
	
	public Location getLocation() {
		return l;
	}
	
	@Override
	public String toString() {
		return "Ag:"+l.toString();
	}
	
}
