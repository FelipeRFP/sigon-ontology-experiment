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
import br.ufsc.ine.agent.context.communication.CommunicationContextService;
import br.ufsc.ine.agent.context.custom.CustomContext;
import br.ufsc.ine.agent.context.plans.PlansContextService;
import br.ufsc.ine.agent.context.desires.DesiresContextService;

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
    	
    	BridgeRule rule3 = createRedTrafficlight();
        		
    	
    	List<BridgeRule> list = new ArrayList<>();
    	list.add(rule1);
    	list.add(rule2);
    	list.add(rule3);
//    	list.add(rule4);
    	
    	return list;
	}
	
	private BridgeRule createRedTrafficlight() {
		//plan(safe(Lemming), [action(trafficLight(), trafficLight(green), trafficLight(red))], 
		//[isOn(Lemming, crossWalkB), isA(Lemming, lemming), isA(Car, car), isOn(Car, streetA)], 
		//trafficLight(red)).
		
		Body action1 = Body.builder().context(PlansContextService.getInstance()).clause("action(Action, _, _)").build();
		Body action1member = Body.builder().context(PlansContextService.getInstance()).clause("member(action(Action, _, _), Actions)").and(action1).build();
		
		Body precondition1 = Body.builder().context(this).clause("isOn(Lemming, crossWalkB)").and(action1member).build();
		Body precondition1member = Body.builder().context(PlansContextService.getInstance()).clause("member(isOn(Lemming, crossWalkB), Preconditions)").and(precondition1).build();
		
		Body precondition2 = Body.builder().context(this).clause("isA(Lemming, lemming)").and(precondition1member).build();
		Body precondition2member = Body.builder().context(PlansContextService.getInstance()).clause("member(isA(Lemming, lemming), Preconditions)").and(precondition2).build();
		
		Body precondition3 = Body.builder().context(this).clause("isA(Car, car)").and(precondition2member).build();
		Body precondition3member = Body.builder().context(PlansContextService.getInstance()).clause("member(isA(Car, car), Preconditions)").and(precondition3).build();
		
		Body precondition4 = Body.builder().context(this).clause("isOn(Car, streetA)").and(precondition3member).build();
		Body precondition4member = Body.builder().context(PlansContextService.getInstance()).clause("member(isOn(Car, streetA), Preconditions)").build();
		
		Body plan = Body.builder().context(PlansContextService.getInstance()).clause("plan(safe(Lemming), Actions, Preconditions, trafficLight(red))")
				.and(precondition4member)
				.build();
		
		
		Head head = Head.builder().context(CommunicationContextService.getInstance()).clause("act(Action)").build();
		
		return BridgeRule.builder().body(plan).head(head).build();
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
	

