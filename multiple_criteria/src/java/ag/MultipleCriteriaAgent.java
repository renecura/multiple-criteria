package ag;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.Random;

import net.renecura.voting.VotingMethod;
import net.renecura.voting.alternatives.Alternative;
import net.renecura.voting.alternatives.AlternativeSet;
import net.renecura.voting.alternatives.rgbcolor.RgbColorAlternative;
import net.renecura.voting.alternatives.rgbcolor.RgbColorDistancePreference;
import net.renecura.voting.methods.PluralityMethod;
import env.WorldModel;


public class MultipleCriteriaAgent extends Agent{

	private WorldModel model;
	private Location l;
	
	protected VotingMethod method;
	protected AlternativeSet altSet;
	protected int altId;
	protected RgbColorDistancePreference personalPref;
	protected NeighborsPreference neighborsPref;
	protected Alternative chosen;
	protected ArrayList<MultipleCriteriaAgent> neighbors;
	
	// Crea el agente y lo registra en el modelo.
	public MultipleCriteriaAgent(){
		super();
		
		model = WorldModel.getInstance();
		this.l = model.getFreeLocation();
		
		//Inicia comportamiento del agente.
		Random rand = new Random();
		this.altSet = model.getAlternativeSet();
		this.altId = rand.nextInt(altSet.size());
		this.chosen = this.altSet.get(this.altId);
		this.personalPref = new RgbColorDistancePreference( ((RgbColorAlternative)this.chosen).getColor() );
		 
		this.method = new PluralityMethod(this.altSet);		
		//this.method = new BordaCountMethod(this.altSet);
		this.method.addPreference(this.personalPref);
		this.method.addPreference(this.model.getGeneralPreference());
		// Preferencia de los vecinos
		
		model.addAgent(this);		
	}
	
	public void initAg(){
		super.initAg();
		this.bb.add(Literal.parseLiteral("choosen("+this.chosen.toString()+")"));
	}
	
	public Alternative choose(){
		this.bb.remove(Literal.parseLiteral("choosen("+this.chosen.toString()+")"));
		this.chosen = this.method.chooseOne();
		this.bb.add(Literal.parseLiteral("choosen("+this.chosen.toString()+")"));
		return this.chosen;
	}
	
	
	public Alternative chosen(){
		return this.chosen;
	}
	
	public void searchNeighbors(){
		this.neighbors = this.model.getNeighbors(this);
		this.neighborsPref = new NeighborsPreference(this.neighbors);
		this.method.addPreference(this.neighborsPref);
		//this.bb.add(Literal.parseLiteral("neighbors("+this.neighbors.toString()+")"));
	}

	public void updatePercepts(){
		if (this.neighborsPref != null) this.neighborsPref.perceptNeighbors();
	}
	
	// Setters y getters. 
	public void setLocation(int x, int y){
		this.l.x = x;
		this.l.y = y;
		this.bb.add(Literal.parseLiteral("location("+x+","+y+")"));
	}
	
	public Location getLocation() {
		return l;
	}
	
	@Override
	public String toString() {
		return "Ag:"+l.toString();
	}
	
}
