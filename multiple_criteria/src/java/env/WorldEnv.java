package env;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.environment.TimeSteppedEnvironment;

import java.util.Iterator;
import java.util.logging.Logger;

public class WorldEnv extends TimeSteppedEnvironment {

	private WorldModel model;
	private WorldView view;
	
	private Logger logger = Logger.getLogger("Environment:"+WorldEnv.class.getName());
	
	@Override
	public void init(String[] args) {
		super.init(new String[] { "3000" } ); // set step timeout
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
    }
	
	// Acciones de los agentes.
	@Override
	public boolean executeAction(String agName, Structure act) {
		
		String functor = act.getFunctor(); // Recupera el functor
		
		if(functor.equalsIgnoreCase("iam")){
			model.nameAgent(agName);			
			return true;
		}
		
		WorldAgent ag = model.getAgent(agName); // Recupera el agente
		
		if(functor.equalsIgnoreCase("choose")){
			
			Iterator<WorldAgent> nit = model.getNeighbors(ag);
			Iterator<Literal> bbit;
			WorldAgent auxag = null;
			Literal nchoice;
			int nalt, i;
			
			int alternatives[] = new int[8];
						
			while(nit.hasNext()){
				auxag = nit.next();				
				bbit = auxag.getBB().getCandidateBeliefs(Literal.parseLiteral("chosen_alt(_)"),null);
				
				if (bbit.hasNext()) {
					nchoice = bbit.next();
					nalt = (int)((NumberTerm)(nchoice.getTerm(0))).solve();				
					alternatives[nalt]++;
				}
				
			}
			
			int max = 0;
			//String alters = "[0:"+alternatives[0];
			if (auxag != null)
			for(max = 0, i = 1; i < 8; i++){
				//alters += ";"+i+":"+alternatives[i];
				max = (alternatives[max] >= alternatives[i])? max: i; 
			} else
			{
				bbit = ag.getBB().getCandidateBeliefs(Literal.parseLiteral("personal_alt(_)"),null);
				if (bbit.hasNext()) {
					nchoice = bbit.next();
					max = (int)((NumberTerm)(nchoice.getTerm(0))).solve();
				}
			}
			
			//logger.info(ag+" -> "+alters+"] "+max);
			this.clearPercepts(agName);
			this.addPercept(agName, Literal.parseLiteral("social_alt("+max+")"));
		
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
