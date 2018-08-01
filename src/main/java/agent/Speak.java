package agent;

import br.ufsc.ine.agent.context.communication.Actuator;
import java.util.List;

public class Speak extends Actuator {

	@Override
	public void act(List<String> args) {
		System.out.println(args.get(0));		
	}
	
}
