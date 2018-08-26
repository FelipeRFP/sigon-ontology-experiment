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

	@Override
	public List<BridgeRule> callRules(){
    	Head head1 = Head.builder().context(this).clause("X").build();
    	Body body1 = Body.builder().context(CommunicationContextService.getInstance()).clause("sense(X)").build();   
    	
    	Head head2 = Head.builder().context(DesiresContextService.getInstance()).clause("safe(X)").build();
    	Body body2 = Body.builder().context(this).clause("isA(X, lemming)").build();
    	
    	BridgeRule rule1 = BridgeRule.builder().body(body1).head(head1).build();
    	BridgeRule rule2 = BridgeRule.builder().body(body2).head(head2).build();
    	
    	List<BridgeRule> list = new ArrayList<>();
    	list.add(rule1);
    	list.add(rule2);
    	
    	return list;
	}
	
	@Override
	public void appendFact(String fact) {
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
	

