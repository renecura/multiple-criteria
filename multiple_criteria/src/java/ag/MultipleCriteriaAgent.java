package ag;

import env.WorldModel;
import jason.asSemantics.Agent;
import jason.environment.grid.Location;


public class MultipleCriteriaAgent extends Agent{

	private WorldModel model;
	private Location l;
	
	// Crea el agente y lo registra en el modelo.
	public MultipleCriteriaAgent(){
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
