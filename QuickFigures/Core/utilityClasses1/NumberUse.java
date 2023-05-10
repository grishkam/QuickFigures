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
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2023.2
 */
package utilityClasses1;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;


public class NumberUse {
	

	
	/**when given one number and a list of numbers, returns the number from the list that is nearest to the input*/
	public static double findNearest(Number n, double[] numbers) {
		
		int closest=0;
		double diff=Double.MAX_VALUE;
		
		for(int i=0; i<numbers.length; i++) {
			double diff2=Math.abs(n.doubleValue()-numbers[i]);
			if (diff2<diff) {
				diff=diff2;
				closest=i;
			}
		}
		return numbers[closest];
		
	}
	
	/**when given one number and a list of numbers, returns the number from the list that is nearest to the input*/
	public static Double findNearest(Number n, ArrayList<Double> numbers) {
		
		int closest=0;
		double diff=Double.MAX_VALUE;
		
		for(int i=0; i<numbers.size(); i++) {
			double diff2=Math.abs(n.doubleValue()-numbers.get(i));
			if (diff2<diff) {
				diff=diff2;
				closest=i;
			}
		}
		return numbers.get(closest);
		
	}
	
	public static double findMax(double[] numbers) {
		double closest=0;
		
		for(int i=0; i<numbers.length; i++) {
			if (numbers[i]>closest)
			{
				closest=numbers[i];
			}
		}
		return closest;
		
	}
	
	/**returns the largest number in the array
	 * always returns either a positive number or 0*/
	public static double findMax(ArrayList<Double> d) {
		double output=0;
		for(Double numb: d){
			if (numb!=null&&numb>output)
			{
				output=numb;
			}
		}
		return output;
		
	}
	
	/**turns an input string into an array*/
	public static ArrayList<Integer> integersFromString(String st) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		if (st.toLowerCase().contains("x")&& !st.contains(",")) { 
			addNumbersToArray(output,intArrayFromFormulaX(st));
		}
		
		if (st.contains("-") && !st.contains(",")) 
			{addNumbersToArray(output, intArrayFromDashed(st));}
		
		String[] eachnumber=st.split(",");
			for(String s2: eachnumber) {
				addNumbersToArray(output, intArrayFromString1(s2));
			}
		
		return output;
}

	/**
	Adds the numbers in the array to the ArrayList
	 */
	private static void addNumbersToArray(ArrayList<Integer> output, int[] i1) {
		for (int i: i1) {output.add(i); }
	}
	
	/**Based on the content of a string, returns an array of integers*/
	public static  int[] intArrayFromString1(String st) {
		if (st.toLowerCase().contains("x")) { 
			return intArrayFromFormulaX(st);
		}
		
		if (st.contains("-") && !st.contains(",")) {return intArrayFromDashed(st);}
		
		String[] eachnumber=st.split(",");
		int[] output=new int[eachnumber.length];
		for (int i=0; i<output.length; i++){
			try {output[i]=Integer.parseInt(eachnumber[i].trim());} catch (NumberFormatException ne) {output[i]=0;}
		}
		Arrays.sort(output);
		return output;
	}
	
	
	 /**when given either one number or numbers separated by a dash, return the int array with 
	  * */
	private static int[] intArrayFromDashed(String st) {
		String[] eachnumber=st.split("-");
		try {
		int num1=Integer.parseInt(eachnumber[0].trim());
		if (eachnumber.length<2) return new int[] {num1};
		int num2=Integer.parseInt(eachnumber[1].trim());
		if (num1>num2) {int t=num1; num1=num2; num2=t;}
		int[] output=new int[num2-num1+1];
		for (int i=num1; i<=num2; i++){
			try {output[i-num1]=i;} catch (NumberFormatException ne) {output[i-num1]=0;}
		}
		Arrays.sort(output);

		return output;
		} catch (NumberFormatException ne) {return new int[] {};}
	}
	
	 /**when given either one number or numbers separated by an X, * or x,
	  * uses ths formula a X b, to create an array of b length with every a number
	  * */
	private static int[] intArrayFromFormulaX(String st) {
		
		st=st.toLowerCase();
		String regex = "x";
		String[] eachnumber=st.split(regex);
		
		try {
			
		int num1=Integer.parseInt(eachnumber[0].trim());
		if (eachnumber.length<2) return new int[] {num1};
		int num2=Integer.parseInt(eachnumber[1].trim());
		
		if (eachnumber.length<3) {
				int[] output=new int[num2];
				for (int i=1; i<=num2; i++){
					 {
						output[i-1]=num1*i;
					}
				}
				Arrays.sort(output);
				return output;
		}
		int num3=Integer.parseInt(eachnumber[2].trim());
				int[] output=new int[num3];
				for (int i=0; i<num3; i++){
					 {
						output[i]=num1+num2*i;
					}
				}
				Arrays.sort(output);
				return output;
		
		
		} catch (Exception ne) {return new int[] {};}
	}
	

	
	/**when given a centerpoint, the radians and degrees, returns a point.
	 Note, in java geometry has positive y as down and negative y as up
	 so if one imagines a cartessean plane for this.
	 */
	public static Point2D.Double getPointFromRadDeg(Point2D zeroPoint, double radians, double degrees) {
		while (degrees>Math.PI*2) degrees-=Math.PI*2;
		double x=radians*Math.cos(degrees);
		double y=-radians*Math.sin(degrees);
		
		
		x+=zeroPoint.getX();
		y+=zeroPoint.getY();
		return new Point2D.Double(x, y);
	}
	

	/**returns the coordinates of the point of interest in radians and degrees assuming the given orgin point
	  is the center of the coordinate spaced*/
	public static double[] getPointAsRadianDegree(Point2D origin, Point2D pointOfInterest) {
		return new double[] {
				Math.abs(origin.distance(pointOfInterest)), distanceFromCenterOfRotationtoAngle(origin,pointOfInterest)
				};
	}
	
	/**returns true if all of the int vales are the same*/
	public static boolean allSame(int[] numbers) {
		for(int i=1; i<numbers.length; i++) {
			if (numbers[i-1]!=numbers[i]) return false;
		}
		
		return true;
	}
	
	/**When given two points, returns the angle of the line between them.
	  Simple trigonometry*/
	public static double distanceFromCenterOfRotationtoAngle(Point2D pcent, Point2D p2) {

		double xc2=p2.getX()-pcent.getX();
		double yc2=p2.getY()-pcent.getY();
		
		double angle=-Math.atan((yc2)/(xc2));
		if (!Double.isNaN(angle)) {
			if (xc2<0) angle+=Math.PI;
			while (angle<0) angle+=Math.PI*2;
			return angle;
			}
		return 0;
	}
	
	/**When given two points, returns the angle of the line between them. however, assumes that rotation does in 
	 the opposite direction*/
	public static double getAngleBetweenPoints(Point2D p1, Point2D p2) {
		double angle=Math.atan(((double)(p2.getY()-p1.getY()))/(p2.getX()-p1.getX()));
		if (!java.lang.Double.isNaN(angle)) {
			if (p2.getX()-p1.getX()<0) angle+=Math.PI;
			}
		return angle;
	}
	
	
	
}
