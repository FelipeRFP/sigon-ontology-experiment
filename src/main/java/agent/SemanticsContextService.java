package agent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import alice.tuprolog.InvalidTheoryException;
import br.ufsc.ine.agent.bridgerules.Body;
import br.ufsc.ine.agent.bridgerules.BridgeRule;
import br.ufsc.ine.agent.bridgerules.Head;
import br.ufsc.ine.agent.context.beliefs.BeliefsContextService;
import br.ufsc.ine.agent.context.communication.CommunicationContextService;
import br.ufsc.ine.agent.context.custom.CustomContext;
import br.ufsc.ine.agent.context.desires.DesiresContextService;
import br.ufsc.ine.agent.context.plans.PlansContextService;

public class SemanticsContextService extends CustomContext{
	
	protected Manager ontology = new Manager();
	
	public SemanticsContextService() {
		super("semantics");
		try {
			ontology.loadOntology("baseOntology.owl");
			ontology.reason();
		} catch (OWLOntologyCreationException e) {
			
		}
	}
	
	public void save() {
		try {
			ontology.saveOntology("baseOntology.owl");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public List<BridgeRule> callRules(){
    	Head head1 = Head.builder().context(this).clause("X").build();
    	Body body1 = Body.builder().context(CommunicationContextService.getInstance()).clause("sense(X)").build();   
    	
    	Head head2 = Head.builder().context(DesiresContextService.getInstance()).clause("safe(X)").build(); 	
    	Body body2Term2 = Body.builder().context(DesiresContextService.getInstance()).notClause("safe(X)").build();
    	Body body2Term1 = Body.builder().context(this).clause("isA(X, lemming)").and(body2Term2).build();
    	
    	BridgeRule rule1 = BridgeRule.builder().body(body1).head(head1).build();
    	BridgeRule rule2 = BridgeRule.builder().body(body2Term1).head(head2).build();
    	
        
        String desire = "safe(Lemming)"

        String varActions = "Actions";
        String action = "action(Action, _, _)";

        String varPreconditions = "W";
        String[] preconditions = { "isOn(Lemming, crossWalkB)", "isA(Lemming, lemming)", "isA(Car, car)", "isOn(Car, trackStreetAEAI)"};

        String plan = "plan(" + desire + "," + varActions + "," + varPreconditions + "," + postCondition +")";


        Body body3 = Body.builder().context(PlansContextService).clause(plan).

    	
    	List<BridgeRule> list = new ArrayList<>();
    	list.add(rule1);
    	list.add(rule2);
    	list.add(rule3);
    	list.add(rule4);
    	
    	return list;
	}
	
	@Override
	public void appendFact(String fact) {
		if(fact.startsWith("trafficLight"))
			return;
		
		fact = fact.replaceAll(" ", "");
		
		if(this.verify(fact))
			return;

        try {
        	ontology.ontologyAssert(fact);
        	ontology.reason();
        	for(String term: ontology.getAxioms()) {
        		if(this.verify(term))
        			continue;
        		prologEnvironment.appendFact(term);
        	}
        	
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
       
      
	}
        
}
	

