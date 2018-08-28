package agent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SemanticsSimulation {
	
	protected String[][] lemmingPaths = {  
			{"EdgeA", "InternalA", "EdgeB"}, 
			{"EdgeA", "InternalA", "InternalB", "EdgeC"}, 
			{"EdgeA", "InternalA", "InternalB", "EdgeD"}, 
			{"EdgeA", "InternalA", "InternalB", "InternalC", "EdgeE"}, 
			{"EdgeA", "InternalA", "InternalB", "InternalC", "EdgeF"}, 
			{"EdgeB", "InternalA", "InternalB", "EdgeC"}, 
			{"EdgeB", "InternalA", "InternalB", "EdgeD"}, 
			{"EdgeB", "InternalA", "InternalB", "InternalC","EdgeE"}, 
			{"EdgeB", "InternalA", "InternalB", "InternalC","EdgeF"}, 
			{"EdgeC", "InternalB", "EdgeD"}, 
			{"EdgeC", "InternalB", "InternalC", "EdgeE"}, 
			{"EdgeC", "InternalB", "InternalC", "EdgeF"}, 
			{"EdgeD", "InternalB", "InternalC", "EdgeE"}, 
			{"EdgeD", "InternalB", "InternalC", "EdgeF"}, 
			{"EdgeE", "InternalC", "EdgeF"}, 
			{"EdgeG", "InternalD", "EdgeH"}
			};	
	
	protected String[][] carPaths = {
			{"EdgeA", "InternalA", "EdgeB"},
			{"EdgeA", "InternalA", "EdgeC"},
			{"EdgeA", "InternalA", "EdgeD"},
			{"EdgeB", "InternalA", "EdgeC"},
			{"EdgeB", "InternalA", "EdgeD"},
			{"EdgeD", "InternalA", "EdgeC"}			
	};
	
	public void run() {
		Random random = new Random();
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("mmddss");
			
		List<Lemming> lemmings = new ArrayList<>();
		List<Car> cars = new ArrayList<>();
		
		int cycle = 0;
		while(cycle<5) {
			cycle++;
			System.out.println(cycle);
			
			try {
			lemmings.forEach(Lemming::walk);
			cars.forEach(Car::walk);
			}catch(Exception e) {
				cars.forEach(System.out::println);
			}
			
			date = new Date();
			if(cycle % 10 == 0);
				cars.add(new Car(carPaths[random.nextInt(6)],(random.nextInt(2)+1),"car"+dateFormat.format(date)));
			
			date = new Date();
			if(cycle % ((random.nextInt(5))+1) == 0)
				lemmings.add(new Lemming(lemmingPaths[random.nextInt(16)],(random.nextInt(4)+1),random.nextBoolean(),"lemming"+dateFormat.format(date)));
				
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
				
		}
	}
	
	protected class Lemming{
		public String name;
		
		public boolean inverse;
		public boolean remove = false;
		
		public String[] path;
		public String currentPoint;
		
		int pointer;

		public int velocity;   //Cycles per action
		public int actionStatus;
		
		public void walk() {
			if(remove)
				return;
			
			actionStatus++;
			
			if(actionStatus == velocity) {
				if((inverse & pointer == 0)|(!inverse & pointer == (this.path.length-1))) {
					Hear.envObservable.onNext("isOn(" + name + ",nowhere).");
					remove = true;
					return;
				}
				actionStatus = 0;
				nextPoint();
			}
		}
		
		public void nextPoint() {
			pointer = inverse ? pointer-1 : pointer+1;
			currentPoint = path[pointer];
			
			Hear.envObservable.onNext("isOn(" + name + "," + "pointSideWalk" + currentPoint + ").");
			
			
		}
		
		Lemming(String[] path, int velocity, boolean inverse, String name){
			this.name = name;
			this.velocity = velocity;
			this.inverse = inverse;
			this.path = path;
			
			if(!inverse) 
				this.pointer = 0;
			else 
				this.pointer = path.length - 1;
			
			this.currentPoint = path[pointer];
			
			Hear.envObservable.onNext("isA(" + name + ",lemming).");
			Hear.envObservable.onNext("isOn(" + name + "," + "pointSideWalk" + currentPoint + ").");
			
		}
	}

	protected class Car{
		public String name;
		
		public boolean remove = false;
		public boolean inverse;
		
		public String[] path;
		public String currentPoint;
		
		int pointer;

		public int velocity;   //Cycles per action
		public int actionStatus;
		
		public void walk() {
			if(remove)
				return;
			
			actionStatus++;
			
			if(path[2].equals("EdgeC") & !TrafficLight.isGreen) {
				if(actionStatus == velocity)
					actionStatus--;
				return;
			}
			
			if(actionStatus == velocity) {
				if(pointer == (this.path.length-1)){
					Hear.envObservable.onNext("isOn(" + name + ",nowhere).");
					remove = true;
					return;
				}
				actionStatus = 0;
				nextPoint();
			}
		}
		
		public void nextPoint() {
			pointer++;
			currentPoint = path[pointer];
			
			Hear.envObservable.onNext("isOn(" + name + "," + "pointStreet" + currentPoint + ").");
			
			
		}
		
		Car(String[] path, int velocity, String name){
			this.name = name;
			this.velocity = velocity;
			this.path = path;
			
			this.pointer = 0;
			
			this.currentPoint = path[pointer];
			
			Hear.envObservable.onNext("isA(" + name + ",car).");
			Hear.envObservable.onNext("isOn(" + name + "," + "pointStreet" + currentPoint + ").");
			
		}
	}
	
}
