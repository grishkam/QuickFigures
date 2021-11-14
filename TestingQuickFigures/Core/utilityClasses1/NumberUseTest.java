/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 
 * 
 */
package utilityClasses1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Test;



/**
 
 * 
 */
public class NumberUseTest {

	@Test
	public void test() {
		
		
		testFindMax();
		
		double at45=Math.cos(Math.PI/4);
		/**tests to make sure each returned point is in the right quadrant*/
		testCase(0, new Point2D.Double(1,0));
		testCase(0.25,  new Point2D.Double(at45,-at45));
		testCase(0.5,  new Point2D.Double(0,-1));
		testCase(0.75, new Point2D.Double(-at45,-at45));
		testCase(1, new Point2D.Double(-1,0));
		testCase(1.25, new Point2D.Double(-at45,at45));
		testCase(1.5, new Point2D.Double(0,1));
		testCase(2.5,  new Point2D.Double(0,-1));
		
		/**tests the displacement*/
		testCase2(new Point2D.Double(34,45), 1.25, new Point2D.Double(34-at45,45+at45));
		
		
		/**Tests the angle between points. These methods are used when a user rotates an object.
		  Obviously more test cases could be placed here but any irregularity in there methods would be instantly visible to the user */
		testAngleBetweenPoints(new Point2D.Double(0,0), new Point2D.Double(at45,-at45), Math.PI/4);
		testAngleBetweenPoints(new Point2D.Double(0,0), new Point2D.Double(0,1), Math.PI*1.5);
		
		testAngleBetweenPoints2(new Point2D.Double(0,0), new Point2D.Double(at45,-at45),- Math.PI/4);
		testAngleBetweenPoints2(new Point2D.Double(0,0), new Point2D.Double(0,1), Math.PI*0.5);
		
		
		assert(NumberUse.allSame(new int[] {5,5,5,5,5,}));
		assert(NumberUse.allSame(new int[] {1,1}));
		assert(NumberUse.allSame(new int[] {16}));
		assert(!NumberUse.allSame(new int[] {5,5,5,5,-5,90}));
		
		/**tests the int array read*/
		int[] expectedArray = new int[] {1,2,3,4,5};
		testArrayRead("1-5", expectedArray);
		testArrayRead("7-5", new int[] {5,6,7});
		
		/**tests the read for lists again*/
		testArrayRead("1,7, 45, 100", new int[] {1,7, 45, 100});
		/**tests the sorting*/
		testArrayRead("7,5,15", new int[] {5,7,15});
		
		/**tests the x functionality*/
		testArrayRead("3 x 3", new int[] {3,6,9});
		testArrayRead("2 x 5", new int[] {2,4,6,8,10});
		testArrayRead(" 2 x 5 ", new int[] {2,4,6,8,10});
		
		testArrayRead("0 x 3", new int[] {0,0,0});
		testArrayRead("20 x 3", new int[] {20,40,60});
		testArrayRead("-20 x 3", new int[] {-60,-40,-20});
		
		testArrayRead("2x 3 x 4", new int[] {2,5,8,11});
		testArrayRead("1x 3 x 2", new int[] {1,4});
		testArrayRead("20 x 5 x 3", new int[] {20,25,30});
		
		/**what happens to invalid array sizes*/
		testArrayRead("5 x 0", new int[] {});
		testArrayRead("5 x -1", new int[] {});
		
		/**tests the version for more complicated formulas*/
		testArrayListRead("3, 8-10, 23-24, 45", new int[] {3, 8, 9, 10, 23, 24, 45} );
		
	}

	/**
	
	 */
	public void testArrayRead(String st, int[] expectedArray) {
		
		int[] numString = NumberUse.intArrayFromString1(st);
		System.out.println("");
		System.out.print(numString +"length"+numString.length+" [");
		if(numString.length!= expectedArray.length) fail("arrays not same ");
		
		for(int i=0; i<numString.length; i++) {
			System.out.print(" "+numString[i]);
			assertEquals(numString[i],expectedArray[i]);
		}
		System.out.print(" "+"]");
		
	}
	
/**
	
	 */
	public void testArrayListRead(String input, int[] expectedArray) {
		
		ArrayList<Integer> numString = NumberUse.integersFromString(input);
		System.out.println("");
		System.out.print(numString +"length"+numString.size()+" [");
		if(numString.size()!= expectedArray.length) fail("arrays not same ");
		
		for(int i=0; i<numString.size(); i++) {
			int n = numString.get(i);
			System.out.print(" "+n);
			assertEquals(n,expectedArray[i]);
		}
		System.out.print(" "+"]");
		
	}

	/**
	 * 
	 */
	public void testFindMax() {
		/**test the find nearest*/
		double[] d = new double[] {34, 12.12, 800, 5031, 1.2, 0, -24, 5000};
		assert(NumberUse.findMax(d)==5031);
		assert(NumberUse.findNearest(5, d)==1.2);
		assert(NumberUse.findNearest(4000, d)==5000);
		assert(NumberUse.findNearest(-6, d)==0);
		assert(NumberUse.findNearest(0.7, d)==1.2);
		assert(NumberUse.findNearest(-15, d)==-24);
		assert(NumberUse.findNearest(7000000, d)==5031);
	}
	
	/**
	 * 
	 */
	private void testAngleBetweenPoints(Point2D p1, Point2D p2, double expected) {
		assert(expected-NumberUse.distanceFromCenterOfRotationtoAngle(p1, p2)<0.00001);
		
	}
	
	/**
	 * 
	 */
	private void testAngleBetweenPoints2(Point2D p1, Point2D p2, double expected) {
		assert(expected-NumberUse.getAngleBetweenPoints(p1, p2)<0.00001);
		
	}

	/**tests to see if the desired point is expected*/
	public static void testCase(double input, Point2D expected) {
		java.awt.geom.Point2D.Double d=NumberUse.getPointFromRadDeg(new Point(0,0), 1, Math.PI*input);
		
		assert(d.x-expected.getX()<0.0001);
		assert(d.y-expected.getY()<0.0001);
	}
	
	/**tests to see if the desired point is expected*/
	public static void testCase2(Point2D center, double input, Point2D expected) {
		java.awt.geom.Point2D.Double d=NumberUse.getPointFromRadDeg(center, 1, Math.PI*input);
		System.out.println(d);
		assert(d.x-expected.getX()<0.0001);
		assert(d.y-expected.getY()<0.0001);
	}

}
