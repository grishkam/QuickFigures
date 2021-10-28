/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Version: 2021.2
 */
package testing;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;

/**iterates through a sequence of example images*/
public class TestProvider {
	
	private static final int TEST_SHAPES = 0;
	int type=	TEST_SHAPES;
	protected TestExample parameter1;
	

	static TestProvider createShapeTestProvider(TestExample argument) {
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
	public static ArrayList<TestProvider> getStandardExportTestsAndImages() {
		ArrayList<TestProvider> output = new ArrayList<TestProvider>();
		for(TestExample i: TestShapes.each)
			output.add(createShapeTestProvider(i));
		
		return output;
	}
	
	public TestExample getType() {
		return parameter1;
	}
	
	/**return both shape and figure tests
	 * @return
	 */
	public static ArrayList<TestProvider> getTestProviderListWithfigures() {
		ArrayList<TestProvider> testsCases = getStandardExportTestsAndImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider t: PlotTester.getTests()) {
			testsCases.add(t);
		}
		
		return testsCases;
	}
	
	
}
