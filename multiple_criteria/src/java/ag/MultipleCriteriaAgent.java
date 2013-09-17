package ag;

import jason.RevisionFailedException;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.environment.grid.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.Random;

import net.renecura.voting.VotingMethod;
import net.renecura.voting.alternatives.Alternative;
import net.renecura.voting.alternatives.AlternativeSet;
import net.renecura.voting.alternatives.rgbcolor.RgbColorAlternative;
import net.renecura.voting.alternatives.rgbcolor.RgbColorDistancePreference;
import env.WorldModel;

public class MultipleCriteriaAgent extends Agent {

	private WorldModel model;
	private Location l;

	protected VotingMethod method;
	protected AlternativeSet altSet;
	protected RgbColorDistancePreference personalPref;
	protected NeighborsPreference neighborsPref;
	protected Alternative chosen;
	protected ArrayList<MultipleCriteriaAgent> neighbors;

	// Crea el agente y lo registra en el modelo.
	public MultipleCriteriaAgent() {
		super();
	}

	// Inicializa el agente, en este punto ya cuenta con una bb
	public void initAg() {
		super.initAg();

		// Añade el agente al modelo.
		model = WorldModel.getInstance();
		this.l = model.getFreeLocation();
		this.setLocation(l.x, l.y);		
		model.addAgent(this);
		
		// Inicia comportamiento del agente.
		Random rand = new Random();
		this.altSet = model.getAlternativeSet();
		int altId = rand.nextInt(altSet.size());
		this.chosen = this.altSet.get(altId);
		this.personalPref = new RgbColorDistancePreference(
				((RgbColorAlternative) this.chosen).getColor());

		this.addBelief("choosen(" + this.chosen.toString() + ")");

		initMethod();
	}

	@SuppressWarnings("unchecked")
	public void initMethod() {
		String vmClass = model.getMethod();

		Constructor<VotingMethod> c;
		try {
			c = (Constructor<VotingMethod>) Class.forName(vmClass)
					.getConstructor(AlternativeSet.class);
			method = c.newInstance(altSet);
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new MissingResourceException(e.getMessage(), vmClass,
					AlternativeSet.class.getName());
		}

		// Add preferences.
		method.addPreference(personalPref);
		method.addPreference(model.getGeneralPreference());
		if (neighborsPref != null)
			method.addPreference(neighborsPref);
	}

	public Alternative choose() {
		this.delBelief("choosen(" + this.chosen.toString()	+ ")");
		this.chosen = this.method.chooseOne();
		this.addBelief("choosen(" + this.chosen.toString() + ")");
		
		return this.chosen;
	}

	public Alternative chosen() {
		return this.chosen;
	}

	public void searchNeighbors() {
		this.neighbors = this.model.getNeighbors(this);
		this.neighborsPref = new NeighborsPreference(this.neighbors);
		this.method.addPreference(this.neighborsPref);
		// this.bb.add(Literal.parseLiteral("neighbors("+this.neighbors.toString()+")"));
	}

	public void updatePercepts() {
		if (this.neighborsPref != null)
			this.neighborsPref.perceptNeighbors();
	}

	private void addBelief(String b){
		Literal l = Literal.parseLiteral(b);		
		try {
			this.addBel(l);
		} catch (RevisionFailedException e) {
			e.printStackTrace();
		}
	}
	
	private void delBelief(String b){
		Literal l = Literal.parseLiteral(b);		
		try {
			this.delBel(l);
		} catch (RevisionFailedException e) {
			e.printStackTrace();
		}
	}
		
	// Setters y getters.
	public void setLocation(int x, int y) {
		this.l.x = x;
		this.l.y = y;
		addBelief("location(" + x + "," + y + ")");
		//this.bb.add(Literal.parseLiteral("location(" + x + "," + y + ")"));
	}

	public Location getLocation() {
		return l;
	}
	
	@Override
	public String toString() {
		return "Ag:" + l.toString();
	}

}
