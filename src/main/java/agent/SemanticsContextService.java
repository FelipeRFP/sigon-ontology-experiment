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
    	
    	Head head3 = Head.builder().context(CommunicationContextService.getInstance()).clause("act(trafficLight())").build();
    	Body body3Term1 = Body.builder().context(PlansContextService.getInstance()).clause
    			("plan(_,_,[isOn(Lemming, crossWalkB), isA(Lemming, lemming), isA(Car, car), isOn(Car, streetA)],_)").build();
    	Body body3Term2 = Body.builder().context(this).clause("isOn(Lemming, crossWalkB)").build();
    	Body body3Term3 = Body.builder().context(this).clause("isOn(Lemming, crossWalkC)").or(body3Term2).build();
    	Body body3Term4 = Body.builder().context(this).clause("isA(Lemming, lemming)").build();
    	Body body3Term5 = Body.builder().context(this).clause("isA(Car, car)").build();
    	Body body3Term6 = Body.builder().context(this).clause("isOn(Car, streetA)")
    			.and(body3Term1)
    			.and(body3Term3)
    			.and(body3Term4)
    			.and(body3Term5)
    			.build();
    	
    	BridgeRule rule3 = BridgeRule.builder().body(body3Term6).head(head3).build();
    	
    	Head head4 = Head.builder().context(CommunicationContextService.getInstance()).clause("act(trafficLight())").build();
    	Body body4Term1 = Body.builder().context(PlansContextService.getInstance()).clause
    			("plan(_,_,[trafficLight(red),  not isOn(Lemming, sideWalkB),  not isOn(Lemming, sideWalkC), isA(Lemming, lemming)],_)").build();
    	Body body4Term2 = Body.builder().context(BeliefsContextService.getInstance()).clause("trafficLight(red)").build();
    	Body body4Term3 = Body.builder().context(this).notClause("isOn(Lemming, sideWalkB)").build();
    	Body body4Term4 = Body.builder().context(this).notClause("isOn(Lemming, sideWalkC)").build();
    	Body body4Term5 = Body.builder().context(this).clause("isA(Lemming, lemming))")
    			.and(body4Term1)
    			.and(body4Term2)
    			.and(body4Term3)
    			.and(body4Term4)
    			.build();
    	
    	BridgeRule rule4 = BridgeRule.builder().body(body4Term5).head(head4).build();
    	
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
	

