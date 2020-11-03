package utilityClasses1;

import java.io.Serializable;
import java.util.ArrayList;

public class ParameterText< T1, T2 extends Serializable> {

	public void doSome(ArrayList<? extends T1> arr) {}
	
	public static void main(String[] args) {
		
	}
}
