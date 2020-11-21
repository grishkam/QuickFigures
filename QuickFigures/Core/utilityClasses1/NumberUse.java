package utilityClasses1;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class NumberUse {
	
	public static void main(String[] args) {
		testCase(0);
		testCase(0.25);
		testCase(0.5);
		testCase(0.75);
		testCase(1);
		testCase(1.25);
		testCase(1.5);
		testCase(2.5);
	}
	
	public static void testCase(double input) {
		java.awt.geom.Point2D.Double d=getPointFromRadDeg(new Point(0,0), 1, Math.PI*input);
		
	}
	
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
	
	public static double findMax(ArrayList<Double> d) {
		double closest=0;
		
		for(Double numb: d){
			if (numb>closest)
			{
				closest=numb;
			}
		}
		return closest;
		
	}
	
	public static ArrayList<Integer> integerVectFromString(String st) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		int[] i1 =intArrayFromString1(st);
		for (int i: i1) {output.add(i); }
		return output;
}
	
	public static  int[] intArrayFromString1(String st) {
		if (st.contains("-") && !st.contains(",")) {return intArrayFromString2(st);}
		String[] eachnumber=st.split(",");
		int[] output=new int[eachnumber.length];
		for (int i=0; i<output.length; i++){
			try {output[i]=Integer.parseInt(eachnumber[i].trim());} catch (NumberFormatException ne) {output[i]=0;}
		}
		Arrays.sort(output);
		return output;
	}
	
	
	public static  float[] floatArrayFromString1(String st) {
		String[] eachnumber=st.split(",");
		float[] output=new float[eachnumber.length];
		for (int i=0; i<output.length; i++){
			try {output[i]=Float.parseFloat(eachnumber[i].trim());} catch (NumberFormatException ne) {output[i]=0;}
		}
	
		return output;
	}
	
	public static String floatArrayToString(float[] ia) {
		String output="";
		if (ia.length==0) return "";
		
		int intcast=(int) (ia[0]*100);
		float recast= ((float)(intcast))/100;
		
		output+=recast;
		if (ia.length==1) return output;
		for (int i=1; i<ia.length; i++) output+=","+ia[i];
		return output;
	}
	
	
	public static String stringfromIntarray(int [] ia) {
		String output="";
		if (ia.length==0) return "";
		output+=ia[0];
		if (ia.length==1) return output;
		for (int i=1; i<ia.length; i++) output+=","+ia[i];
		return output;
	}
	
	 public static String  stringfromIntarray(ArrayList<Integer> ia) {
		String output="";
		if (ia.size()==0) return "";
		output+=ia.get(0);
		if (ia.size()==1) return output;
		for (int i=1; i<ia.size(); i++) output+=","+ia.get(i);
		return output;
	}
	
	public static int[] intArrayFromString2(String st) {
		String[] eachnumber=st.split("-");
		try {
		int num1=Integer.parseInt(eachnumber[0]);
		if (eachnumber.length<2) return new int[] {num1};
		int num2=Integer.parseInt(eachnumber[1]);
		if (num1>num2) {int t=num1; num1=num2; num2=t;}
		int[] output=new int[num2-num1+1];
		for (int i=num1; i<=num2; i++){
			try {output[i-num1]=i;} catch (NumberFormatException ne) {output[i-num1]=0;}
		}
		Arrays.sort(output);

		return output;
		} catch (NumberFormatException ne) {return new int[] {};}
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
	

	
	
	public static double[] getPointAsRadianDegree(Point2D origin, Point2D pointOfInterest) {
		return new double[] {
				Math.abs(origin.distance(pointOfInterest)), distanceFromCenterOfRotationtoAngle(origin,pointOfInterest)
				};
	}
	
	public static boolean allSame(int[] numbers) {
		for(int i=1; i<numbers.length; i++) {
			if (numbers[i-1]!=numbers[i]) return false;
		}
		
		return true;
	}
	
	public static double distanceFromCenterOfRotationtoAngle(Point2D pcent, Point2D p2) {
		//Point2D pcent=getCenterOfRotation();
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
	
	public static double getAngleBetweenPoints(double x, double y, double x2, double y2) {
		double angle=Math.atan(((double)(y2-y))/(x2-x));
		if (!java.lang.Double.isNaN(angle)) {
			if (x2-x<0) angle+=Math.PI;
			//this.setAngle(angle);
			}
		return angle;
	}
	
	public static double getAngleBetweenPoints(Point2D p1, Point2D p2) {
		double angle=Math.atan(((double)(p2.getY()-p1.getY()))/(p2.getX()-p1.getX()));
		if (!java.lang.Double.isNaN(angle)) {
			if (p2.getX()-p1.getX()<0) angle+=Math.PI;
			}
		return angle;
	}
	
	
	
}
