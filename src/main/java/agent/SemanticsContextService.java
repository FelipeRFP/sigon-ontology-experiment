package agent;

import java.util.ArrayList;
import java.util.List;

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
		super("_semantics");
	}

	@Override
	public List<BridgeRule> callRules(){
    	Head head1 = Head.builder().context(this).clause("").build();
    	Body body1 = Body.builder().context(CommunicationContextService.getInstance()).clause("sense(X)").build();
    	
    	BridgeRule rule1 = BridgeRule.builder().body(body1).head(head1).build();
    	
    	Head head2 = Head.builder().context(BeliefsContextService.getInstance()).clause("X").build();
    	Body body2 = Body.builder().context(this).clause("X").build();
    	
    	BridgeRule rule2 = BridgeRule.builder().body(body2).head(head2).build();
    	
    	
    	Head head3 = Head.builder().context(DesiresContextService.getInstance()).clause("safe(X)").build();
    	Body body3 = Body.builder().context(this).clause("isA(X, lemming)").build();
    	
    	BridgeRule rule3 = BridgeRule.builder().body(body3).head(head3).build();
    	
    	List<BridgeRule> list = new ArrayList<>();
    	list.add(rule1);
    	list.add(rule2);
    	list.add(rule3);
    	
    	return list;
	}
	
	@Override
	public void appendFact(String fact) {
        try {
        	System.out.println("printing fact: "+fact);
        	ontology.ontologyAssert(fact);
        	ontology.reason();
        	for(String term: ontology.getAxioms())
        		prologEnvironment.appendFact(term);
        	
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
    }
	
}
