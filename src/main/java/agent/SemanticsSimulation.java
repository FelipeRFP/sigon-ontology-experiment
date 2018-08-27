package agent;

import java.util.ArrayList;
import java.util.List;

public class SemanticsSimulation {
	
	String[][] paths = {  
			{"AE", "AI", "BE"}, 
			{"AE", "AI", "BI", "CE"}, 
			{"AE", "AI", "BI", "DE"}, 
			{"AE", "AI", "BI", "CI", "EE"}, 
			{"AE", "AI", "BI", "CI", "FE"}, 
			{"BE", "AI", "BI", "CE"}, 
			{"BE", "AI", "BI", "DE"}, 
			{"BE", "AI", "BI", "CI","EE"}, 
			{"BE", "AI", "BI", "CI","FE"}, 
			{"CE", "BI", "DE"}, 
			{"CE", "BI", "CI", "EE"}, 
			{"CE", "BI", "CI", "FE"}, 
			{"DE", "BI", "CI", "EE"}, 
			{"DE", "BI", "CI", "FE"}, 
			{"EE", "CI", "FE"}, 
			{"GE", "DI", "HE"}
			};	
	
	public void run() {
		
		List<Lemming> lemmings = new ArrayList<>();
		List<Car> car = new ArrayList<>();
		
		int cicle = 0;
		while(cicle<100) {
			if(cicle % 10 == 0);
				
				
			
			
		}
	}
	
	class Lemming{
		public boolean inverse;
		
		public String[] path;
		
		public String startPoint; 
		public String finalPoint; 
		public String actualPoint;
		
		public int velocity;   //Cicles per action
		public int actionStatus;
		
		public void walk() {
			actionStatus++;
			if(actionStatus == velocity) {
				actionStatus = 0;
				nextPoint();
			}
			
			
		}
		
		public void nextPoint() {
			
		}
		
		Lemming(String[] path, int velocity, boolean inverse){
			this.velocity = velocity;
			this.inverse = inverse;
			this.path = path;
			if(!inverse) {
				this.startPoint = path[0];
				this.finalPoint = path[path.length - 1];
			}else {
				this.startPoint = path[path.length - 1];
				this.finalPoint = path[0];
			}
		}
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class Car{
		public String startPoint; 
		public String finalPoint; 
		public String velocity;   //Cicles per action
		public String location;
			
		Car(String startPoint, String finalPoint, String velocity, String location){
			this.startPoint = startPoint;
			this.finalPoint = finalPoint;
			this.velocity = velocity;
			this.location = location;
		}	
	}
	
}
