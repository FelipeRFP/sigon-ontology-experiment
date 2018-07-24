package agentManager;

import alice.tuprolog.InvalidTheoryException;
import br.ufsc.ine.agent.context.custom.CustomContext;

public class SemanticsContextService extends CustomContext{

	public SemanticsContextService() {
		super("semantics");
	}

	@Override
	public void appendFact(String fact) {
        try {
            prologEnvironment.appendFact(fact);
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
    }
	
}
