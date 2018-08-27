package agent;

import br.ufsc.ine.agent.context.communication.Actuator;
import java.util.List;

public class TrafficLight extends Actuator {

	protected static boolean isGreen = false;
	
	@Override
	public void act(List<String> args) {
			TrafficLight.isGreen = !TrafficLight.isGreen;
			
			if(TrafficLight.isGreen)
				Hear.envObservable.onNext("trafficLight(green).");
			else
				Hear.envObservable.onNext("trafficLight(red).");
		
	}
	
}
