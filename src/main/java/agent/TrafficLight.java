package agent;

import br.ufsc.ine.agent.context.communication.Actuator;
import java.util.List;

public class TrafficLight extends Actuator {

	protected static boolean isGreen = false;
	
	@Override
	public void act(List<String> args) {
			TrafficLight.isGreen = !TrafficLight.isGreen;
			
			if(TrafficLight.isGreen) {
				System.out.println("trafficLight(green).");
				Hear.envObservable.onNext("trafficLight(green).");
			}
			else {
				System.out.println("trafficLight(red).");
				Hear.envObservable.onNext("trafficLight(red).");
			}
	}
	
}
