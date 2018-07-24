package agent;

import alice.tuprolog.InvalidTheoryException;
import br.ufsc.ine.agent.context.custom.CustomContext;

public class SemanticsContextService extends CustomContext{

	protected Manager ontology = new Manager();
	
	public SemanticsContextService() {
		super("_semantics");
	}

	@Override
	public void appendFact(String fact) {
        try {
            prologEnvironment.appendFact(fact);
        	ontology.ontologyAssert(fact);
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
    }
	
}
