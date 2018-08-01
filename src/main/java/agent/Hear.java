package agent;

import br.ufsc.ine.agent.context.communication.Sensor;
import rx.subjects.PublishSubject;
 
public class Hear extends Sensor {

	public static final PublishSubject<String> envObservable = PublishSubject.create();
	
	@Override
	public void run() {
		envObservable.subscribe(super.getPublisher());
	}

}
