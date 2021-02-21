/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Version: 2021.1
 */
package testing;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;

/**iterates through a sequence of example images*/
public class TestProvider {
	
	private static final int TEST_SHAPES = 0;
	int type=	TEST_SHAPES;
	private int parameter1;
	

	static TestProvider createShapeTestProvider(int argument) {
		 TestProvider out = new TestProvider();
		out.type=	TEST_SHAPES;
		 out.parameter1=argument;
		 return out;
	}

	
	public DisplayedImage createExample() {
		if ( type==TEST_SHAPES) 
		return TestShapes.createExample(parameter1);
		
		return null;
	}
	
	/**returns every example for shape tests*/
	public static ArrayList<TestProvider> getStandardExportTestsImages() {
		ArrayList<TestProvider> output = new ArrayList<TestProvider>();
		for(int i: TestShapes.each)
			output.add(createShapeTestProvider(i));
		
		return output;
	}
	
	
}
