package env;

import jason.asSyntax.Structure;
import jason.environment.TimeSteppedEnvironment;

import java.util.logging.Logger;

import ag.MultipleCriteriaAgent;

public class WorldEnv extends TimeSteppedEnvironment {

	protected WorldModel model;
	protected WorldView view;
	
	protected Logger logger = Logger.getLogger("Environment:"+WorldEnv.class.getName());
	
	@Override
	public void init(String[] args) {
		super.init(new String[] { "60000" } ); // set step timeout
        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);
	
		model = WorldModel.getInstance();
	}
	
	// Acciones al finalizar el paso.
	@Override
    protected void stepFinished(int step, long time, boolean timeout) {
        
        if (step == 1){
        	view = new WorldView(model);
        	view.start();
        	logger.info("Start view");
        }
        
        logger.info("step "+step+" finished in "+time);
        //view.setTitle("Multiple Criteria - Step "+step+" finished in "+time);
    }
	
	// Acciones de los agentes.
	@Override
	public boolean executeAction(String agName, Structure act) {
		
		String functor = act.getFunctor(); // Recupera el functor
		
		if(functor.equalsIgnoreCase("iam")){
			model.nameAgent(agName);			
			return true;
		}
		
		MultipleCriteriaAgent ag = model.getAgent(agName); // Recupera el agente
		ag.updatePercepts();
		
		if(functor.equalsIgnoreCase("search_neighbors")){
			
			ag.searchNeighbors();
			return true;
		}
		
		if(functor.equalsIgnoreCase("choose")){
			
			ag.choose();
			return true;
		}
		
		if(functor.equalsIgnoreCase("nothing")){
			return true;
		}
		
		return super.executeAction(agName, act);
	}
	
	public void updatePercepts(){
		
	}

}
