package ag;

import java.util.ArrayList;
import java.util.Iterator;

import net.renecura.voting.BallotBox;
import net.renecura.voting.Utility;
import net.renecura.voting.alternatives.Alternative;

public class NeighborsPreference implements Utility {

	private ArrayList<MultipleCriteriaAgent> neighbors;
	private BallotBox ballot;
	
	public NeighborsPreference(ArrayList<MultipleCriteriaAgent> neighbors){		
		this.neighbors = neighbors;
		this.ballot = new BallotBox();
	}
	
	public void perceptNeighbors(){
		
		this.ballot = new BallotBox();
		
		Iterator<MultipleCriteriaAgent> nIt = neighbors.iterator();
		MultipleCriteriaAgent n;
		
		while(nIt.hasNext()){
			n  = nIt.next();
			this.ballot.vote(n.chosen());
		}
	}
	
	public double utility(Alternative alt) {

		if (this.ballot == null) return 0;
		
		return this.ballot.utility(alt);
	}

}
