package testing;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;

/**iterates through a sequence of example images*/
/**
 * Author: Greg Mazo
 * Date Modified: Dec 2, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
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
	
	
	public static ArrayList<TestProvider> getStandardExportTestsImages() {
		ArrayList<TestProvider> output = new ArrayList<TestProvider>();
		for(int i: TestShapes.each)
			output.add(createShapeTestProvider(i));
		
		return output;
	}
	
	
	class LittleTest {}
}
