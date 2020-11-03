package utilityClasses1;

import java.util.ArrayList;

public class ListFilter<ObjectType> {
	
	public boolean fitsCriteria(ObjectType o) {
		return true;
	}
	public void ifFits(ObjectType o) {
		
	}
	
	public ArrayList<ObjectType> filter(Iterable<ObjectType> ao) {
		ArrayList<ObjectType> output = new ArrayList<ObjectType>();
		for(ObjectType o: ao) {
			if (fitsCriteria( o)) {
				output.add(o);
				ifFits(o);
			}
		}
		
		return output;
		
	}

}
