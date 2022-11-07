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
 * Version: 2022.2
 */
package locatedObject;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import graphicalObjects_Shapes.ShapeGraphic;
import utilityClasses1.ArraySorter;

/**A list of points that form a Path with many methods
 * to edit the list
 *   
 * @see PathPoint
 * @see Path2D
 * */
public class PathPointList extends ArrayList<PathPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PathPointList copy() {
		PathPointList o = new PathPointList();
		for(PathPoint p:this) {
			o.add(p.copy());
		}
		
		return o;
	}
	
	public PathPointList getTransformedCopy(AffineTransform at) {
		PathPointList out= copy() ;
		out.applyAffine(at);
		return out;
	}
	
	/**returns a copy with the order of points flipped*/
	public PathPointList getOrderFlippedCopy() {
		PathPointList o = new PathPointList();
		for(int i=this.size()-1; i>=0; i--) {
			
			PathPoint oldp = this.get(i);
			if (oldp==null) continue;
			PathPoint newp = oldp.copy();
			o.add(newp);
			newp.setCurveControl1(oldp.getCurveControl2());
			newp.setCurveControl2(oldp.getCurveControl1());
		}
		return o;
	}
	 
	/**Adds the points in the given list to this one*/
	public void concatenate(PathPointList other) {
		for(PathPoint p:other) {
			add(p.copy());
		}
	}
	
	/**Adds the points in the given list to this one*/
	public void concatenateClosed(PathPointList other) {
		other.get(0).setClosePoint(true);
		concatenate(other);
	}
	
	/**Scrambles the list*/
	public void randomizePointPositions() {
		new ArraySorter<PathPoint>().randomizePointPositions(this);
	}
	

	/**Creates a copy with the anchor points but not the curve control points*/
	public PathPointList copyUncurved() {
		PathPointList o = new PathPointList();
		for(PathPoint p:this) {
			o.add(new PathPoint(p.getAnchor()));
		}
		
		return o;
	}
	
	/**creates a subset with two points*/
	private PathPointList createSubSection(int i) {
		
		PathPoint pp1 = get(i);
		PathPoint pp2 = this.getNextPointForCurve(pp1);
	
		
		PathPointList o = new PathPointList();
		o.add(pp1.copy());
		o.add(pp2.copy());
		for(PathPoint op:o) {
			op.setClosePoint(false);
		}
	
	
		return o;
	}
	
	
	public void makeSPatternTypeCurveControl(PathPoint p) {
		PathPoint pre = this.getPreviousPoint(p);
		double xd = pre.getAnchor().x-pre.getCurveControl1().x;
		double yd = pre.getAnchor().y-pre.getCurveControl1().y;
		p.setCurveControl1(new Point2D.Double(pre.getAnchor().x+xd, pre.getAnchor().y+yd));
	}
	
	/**Generates a series of subsection*/
	public ArrayList<PathPointList> subSections() {
		ArrayList<PathPointList> out = new ArrayList<PathPointList>();
		for(int i=0;i<size();i++) {
			out.add(createSubSection(i));
		}
		return out;
	}
	
	public PathPointList nearestSubSegment(Point2D p) {
		ArrayList<PathPointList> subs = subSections();
		if (subs.size()==1) return subs.get(0);
		if (subs.size()==0) return null;
		double dist = Double.MAX_VALUE;
		PathPointList nearest = subs.get(0);
		for(PathPointList sec: subs) {
			Path2D path = sec.createPath(false);
			//if (new BasicStroke(4).createStrokedShape(path).contains(p)) return sec;
			java.awt.geom.Point2D.Double center = new Point2D.Double(path.getBounds().getCenterX(), path.getBounds().getCenterY());
			double d = p.distance(center);
			if (d<dist) {
				dist=d;
				nearest=sec;
			}
		}
		return nearest;
	}
	
	public PathPointList containingSubSegment(Point2D p) {
		ArrayList<PathPointList> subs = subSections();
		if (subs.size()==1) return subs.get(0);
		if (subs.size()==0) return null;
		
		for(PathPointList sec: subs) {
			Path2D path = sec.createPath(false);
			if (new BasicStroke(7).createStrokedShape(path).contains(p)) return sec;
			
		}
		return null;
	}
	
	public void moveEnd() {
		if (this.size()<=1) return;
		PathPoint p = this.get(0);
		remove(p);
		add(p);
	}
	
	public PathPoint addPoint(Point2D p) {
		PathPoint addedP = new PathPoint(p.getX(), p.getY());
		add(addedP);
		return addedP;
	}
	
	public PathPoint addPoint(Point2D p, int position) {
		PathPoint addedP = new PathPoint(p.getX(), p.getY());
		add(position, addedP);
		return addedP;
	}
	
	public void addPoint(double x, double y) {
		addPoint(new Point2D.Double(x, y));
	
	}
	
	public PathPoint getNearest(double x, double y, PathPoint exclude) {
		double distance=Double.MAX_VALUE;
		PathPoint nearest=null;
		for(PathPoint pp:this) {
			if (pp==null||pp==exclude) continue;
			double d = pp.getAnchor().distance(x, y);
			if (d<distance) {
				distance=d;
				nearest=pp;
			}
		}
		
		return nearest;
		
	}
	public PathPoint getNearest(Point2D p) {
		return getNearest(p.getX(),p.getY(),null);
	}
	public PathPoint getNearest(double x, double y) {
		return getNearest(x,y,null);
	}
	public PathPoint get2dNearest(double x, double y) {
		PathPoint pp = getNearest(x,y);
		return getNearest(x,y,pp);
	}
	
	public int indexOfPoint(double x, double y) {
		PathPoint p1 = getNearest(x,y);
		PathPoint p2 = get2dNearest(x,y);
		int i1 = this.indexOf(p1);
		int i2 = this.indexOf(p2);
		if (Math.abs(i1-i1)<2) {
		if (i1<i2) return i1; else return i2;}
		return i1;
	}
	
	public Point2D[] getMidpointsOfAnchors() {
		Point2D[] o = new Point2D[this.size()] ;
		for(int i=0; i<o.length;i++) {
			PathPoint o1 = this.get(i);
			PathPoint o2 = this.getPreviousPoint(o1);
			o[i]=new Point2D.Double((o1.getAnchor().getX()+o2.getAnchor().getX())/2, (o1.getAnchor().getY()+o2.getAnchor().getY())/2);
		} 
		return o;
	}
	
	public void setCurvePoints(Point2D[]  in) {
		for(int i=0; i<in.length&&i<this.size(); i++) {
			Point2D potential = in[i];
			if ( potential==null)  potential=get(i).getAnchor();
			if ( potential!=null) this.get(i).setCurveControl1(new Point2D.Double(potential.getX(), potential.getY()));
			//else this.get(i).p1=this.get(i).anchor.clone();
		}
	}
	
	public Point2D[] getAnchors() {
		Point2D[] o = new Point2D[this.size()] ;
		for(int i=0; i<o.length;i++) {
			PathPoint o1 = this.get(i);
		
			o[i]=o1.getAnchor();
		} 
		return o;
	}
	
	

	public PathPoint getNextPoint(PathPoint last) {
		int i=this.indexOf(last);
		if (i==-1) return null;
		if (i==this.size()-1) return get(0);
		else return get(i+1);
	}
	
	public PathPoint getPreviousPoint(PathPoint last) {
		int i=this.indexOf(last);
		if (i==-1) return null;
		if (i==0) return get(this.size()-1);
		else return get(i-1);
	}
	
	public void applyAffine(AffineTransform f) {
		for(PathPoint p: this) {
			p.applyAffine(f);
		}
	}
	
	public Line2D getLineToPoint(PathPoint p) {
		PathPoint pp = this.getPreviousPoint(p);
		return new Line2D.Double(pp.getAnchor(), p.getAnchor());
	}
	
	public Line2D getLineToPoint(int i) {
		return getLineToPoint(get(i));
	}
	
	public Path2D  createPath(boolean close) {
		return createPath(this, close);
	}
	
	private static transient PathPoint lastMoveTo;
	
	
	public static void movePathTo(Path2D path2, PathPoint p2) {
		lastMoveTo=p2;
		Point2D p=lastMoveTo.getAnchor();
		path2.moveTo(p.getX(), p.getY());
	}
	
	
	/**Adds a curved segment to the path that is between the two points. curves from pPrecious to p2*/
	public static void curveTo(Path2D path2, PathPoint p2, PathPoint pPrevious) {
		path2.curveTo(  pPrevious.getCurveControl2().x,  pPrevious.getCurveControl2().y,p2.getCurveControl1().x, p2.getCurveControl1().y, p2.getAnchor().x, p2.getAnchor().y);
	}
	
	public static PathPointList createFromIterator(PathIterator pi) {
		PathPointList output = new PathPointList();
		
		double[] d=new double[6];
		
		PathPoint lastpp=null;
		PathPoint lasomoveTo;
		
		while (!pi.isDone()) {
			int type=pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			
			
			
			PathPoint newpp = new PathPoint(new Point2D.Double(d[0], d[1]));
			int lastType=-90;
			
			if (type==PathIterator.SEG_CLOSE) {
				if (lastpp!=null) {
					lastpp.setCurveControl2(lastpp.getAnchor());
				}
				
				
				
				newpp.setClosePoint(true);
				
					pi.next();
					if (!pi.isDone())
					{
					type=pi.currentSegment(d);
					newpp.setAnchorPoint(new Point2D.Double(d[0], d[1]));
					newpp.setCurveControl(new Point2D.Double(d[0], d[1]));
					newpp.setCurveControl2(new Point2D.Double(d[0], d[1]));
					
					
				} else break;
			}
			
			if (type==PathIterator.SEG_CUBICTO) {
				newpp.setCurveControl1(new Point2D.Double(d[2],d[3]));
				newpp.setAnchor(new Point2D.Double(d[4],d[5]));
				newpp.setCurveControl2(new Point2D.Double(d[4],d[5]));//temporary exit point will be reset by next
				
				if (lastpp!=null) {
					lastpp.setCurveControl2(new Point2D.Double(d[0],d[1]));
				}
				if (lastpp.getAnchor().equals(newpp.getAnchor())) {
					//lastpp.p2=lastpp.anchor;
					//lastpp.curveControl1=lastpp.anchor;
				}
			
			}
			
			/**I dont have the pathpoint list do quad directly but assume
			  that a cubic with the two control points being equal is 
			  very close in appearance to a quad curve. Approximation and not sure
			  how good that guess is. images look wrong export packages have error messages when quads are included in paths */
			if (type==PathIterator.SEG_QUADTO) {
				java.awt.geom.Point2D.Double anchor = new Point2D.Double(d[2],d[3]);
				newpp.setAnchor(anchor);
				Point2D curveControl = new Point2D.Double(d[0],d[1]);
				
				newpp.setCurveControl1(ShapeGraphic.betweenPoint(curveControl, anchor, 0.666666666));//green one
				
				newpp.select();
				if (lastpp!=null) {//edited on nov 28 2021 TODO: finally get this to work right
					lastpp.setCurveControl2(ShapeGraphic.betweenPoint( curveControl,lastpp.getAnchor(),0.6666666));
				}
				
			
			}
			
			
			
			
			
			if (type==PathIterator.SEG_LINETO||type==PathIterator.SEG_MOVETO) {
				newpp.setCurveControl1(new Point2D.Double(d[0],d[1]));
				newpp.setCurveControl2(new Point2D.Double(d[0],d[1]));
				if (lastpp!=null) {
					lastpp.setCurveControl2(lastpp.getAnchor());
				}
				
				if (type==PathIterator.SEG_MOVETO) {
					lasomoveTo=newpp;
					lasomoveTo.setClosePoint(true);
				}else {/**IssueLog.log("doing line to");*/}
			}
			
			
			
			 output.add(newpp);
			
			
			pi.next();
			lastpp=newpp;
			lastType=type;
		}
		
		return output;
		
	}
	
	public ArrayList<PathPointList> createAtCloseSubsections() {
		ArrayList<PathPointList> out=new ArrayList<PathPointList>();
		PathPointList current=null;
		
		for(PathPoint p: this) {
			if (current==null||p.isClosePoint()) {
				current=new PathPointList();;
				out.add(current);
				PathPoint next = p.copy();
				next.setClosePoint(false);
				current.add(next);
			} else
			current.add(p.copy());
		}
		return out;
	}
	
	/**Creates a Path2d object from the points*/
	public static Path2D createPath(PathPointList points, boolean close) {
		Path2D.Float path2 = new Path2D.Float();
		if (points.size()==0) return path2;
		
		movePathTo(path2, points.get(0));
		
		PathPoint pPrevious=points.get(0);
		for(int i=1; i<points.size(); i++) {
			pPrevious = points.get(i-1);
			PathPoint p2 = points.get(i);//.anchor;
			//path2.lineTo((float)p2.getX(), (float)p2.getY());

				
				if (p2.isClosePoint()) {
					/**if this is the close point then the curve to sends it back*/
					curveTo(path2,points. getLastClosePoint(i-1), pPrevious);
					path2.closePath();
					
					/**if there is a subsequent point, this moved the path to 
					 	*/
					if (i+1<points.size()) {
								movePathTo(path2, p2);
									}
					}
				else 	{
					
					curveTo(path2, p2, pPrevious);
				}
				
		
		}
		
		
		if (close) {
			//pathPoint p2=points.get(0);
			pPrevious = points.get(points.size()-1);
			curveTo(path2, lastMoveTo, pPrevious);
			path2.closePath();//not sure if needed TODO: Figure out if this has any unintended effects
			}
		
		return path2;

}
	
	private PathPoint getLastClosePoint(int i) {
		for(int j=i; j>=0; j--) {
			PathPoint checkpoint = this.get(j);
			if (checkpoint.isClosePoint()) return checkpoint;
		}
		
		return this.get(0);
		
	}
	
	public PathPoint getLastPoint() {
		return this.get(this.size()-1);
	}
	
	public PathPointList getSelectedPointsOnly() {
		PathPointList ppl = new PathPointList();
		for(PathPoint p: this) {
			if (p!=null&&p.isSelected())
				ppl.add(p);
		}
		return ppl;
	}
	
	
	
	
	
	
	
	
	
	
	public Point2D.Double[] getBezierControlPointsForOutgoing(PathPoint point1, PathPoint point2) {

		Point2D.Double[] output=new Point2D.Double[4];
		
		output[0]=(java.awt.geom.Point2D.Double) point1.getAnchor().clone();
		output[1]=point1.getCurveControl2();
		
		output[2]=point2.getCurveControl1();
		output[3]=point2.getAnchor();
		
		return output;	
	}
	
	
	/**returns the point of the next curve to*/
	private PathPoint getNextPointForCurve(PathPoint last) {
	PathPoint output;
		int i=this.indexOf(last);
		if (i==this.size()-1) {
			output= get(0);
			}
		else output= get(i+1);
		if (output.isClosePoint()) {
			output=
					this.getLastClosePoint(i);
		}
		
		if (i==this.size()-1) {
			output=this.getLastClosePoint(i-1);
		}
		return output;
	}
	
	/**Splits the path at the given segment number. uses t to determine to position of the new point
	 * Its an implementation oc De Casteljau's Algorith*/
	public void splitPath(int segnu, double t ) {
		if (segnu<0) segnu+=this.size();
		PathPoint p1 = this.get(segnu);
		PathPoint p2 = this.getNextPointForCurve(p1);
		
		Point2D mfinal=interPolatePlaceOnCurve(p1, p2, t);//the new anchor point
		
		java.awt.geom.Point2D.Double[] curve = getBezierControlPointsForOutgoing(p1, p2);
		
		
		
		
		Point2D m1 = intermediatePoint(curve[0], curve[1], t);
		Point2D m2 = intermediatePoint(curve[1], curve[2], t);
		Point2D m3 = intermediatePoint(curve[2], curve[3], t);
		
		
		Point2D m1B = intermediatePoint(m1, m2,t);
		Point2D m2B = intermediatePoint(m2, m3,t);
		
		
		//midPoint(m1B, m2B, t);
		
		PathPoint pnew=new PathPoint(mfinal);
		pnew.setCurveControl1(m1B);
		pnew.setCurveControl2(m2B);
		this.add(this.indexOf(p2), pnew);
		
		p1.setCurveControl2(m1);
		p2.setCurveControl1(m3);
		//for(Point2D.Double p: curve) {this.addPoint(p);this.getLastPoint().setClosePoint(true);}
		
		
	}
	
	
	/**Undoes the split path
	 * Its a reverse implementation oc De Casteljau's Algorith
	 * This works when one wants to reverse the split path function*/
	public void reverseSplitPath(int segnu, double t ) {
		if (segnu<0) segnu+=this.size();
		PathPoint p1 = this.get(segnu-1);
		PathPoint pnew = this.getNextPointForCurve(p1);//the intermediate anchor point
		PathPoint p2 = this.getNextPointForCurve(pnew);
		
		/**tries to calculate the new points. uses curve control points to estimate the best intermediate*/
		double hypot=0;
		java.awt.geom.Point2D.Double m4 = pnew.getCurveControl1();
		java.awt.geom.Point2D.Double m5 = pnew.getCurveControl2();
		java.awt.geom.Point2D.Double m1=p1.getCurveControl2();
		java.awt.geom.Point2D.Double m3=p2.getCurveControl1();

				hypot=estimateT(m4,m3,m1, m5);
				//IssueLog.log("T estimate is "+hypot);
				if (hypot<1&&hypot>0) t=hypot;
				
				
		java.awt.geom.Point2D[] curveold =new java.awt.geom.Point2D[4];
		
		
		/**figues out the old curve control points*/
		curveold[0]=p1.getAnchor();
		curveold[1]=followingIntPoint(p1.getAnchor(), p1.getCurveControl2(),t );//finds the old second curve control point
		curveold[2]= followingIntPoint2(p2.getAnchor(), p2.getCurveControl1(),t );//finds the old second curve control point//these is a bug here
		curveold[3]=p2.getAnchor();
		
	
		
		//Point2D m1B = intermediatePoint(m1, m2,t);
		//Point2D m2B = intermediatePoint(m2, m3,t);
		
		
		//midPoint(m1B, m2B, t);
		
		
		this.remove(pnew);
		
		p1.setCurveControl2(curveold[1]);
		p2.setCurveControl1(curveold[2]);
		//for(Point2D.Double p: curve) {this.addPoint(p);this.getLastPoint().setClosePoint(true);}
		
		
	}
	
	/**tries to compute the ideal t that would have split a larger curve into two.
	 * I solved the equations to reverse that operation above. It finally passed the
	 * test cases and works great*/
	double estimateT(java.awt.geom.Point2D.Double m4, java.awt.geom.Point2D.Double m3, java.awt.geom.Point2D.Double m1, java.awt.geom.Point2D.Double m5) {
		double t=0.5;
		Point2D a=subtract(m1, m3);
		Point2D b = add(m4,m5); b=add(b, multiply(m1,-2));
		Point2D c=subtract(m1, m4);
		Point2D[] resuls = quadratic(a,b,c);
		/**
		Point2D minfourAC = multiply(a,m5);minfourAC=multiply(minfourAC, 4);
		Point2D root = sqrt(add(multiply(b,b), minfourAC));
		Point2D numberator1 = add(multiply(b,-1),root); 
		Point2D numberator2 = subtract(multiply(b,-1),root);
		Point2D denom=multiply(a, 2);
		
		Point2D t1 = divide(numberator1,denom);
		Point2D t2 = divide(numberator2,denom);
		*/
		Point2D t1 = resuls[0]; //divide(numberator1,denom);
		Point2D t2 = resuls[1]; //divide(numberator2,denom);
		
		/**deduces which of the four numbers is the answer.*/
		if(areClose(t1.getX(),t1.getY())) t=(t1.getX()+t1.getY())/2;
		if(areClose(t1.getX(),t2.getY())) t=(t1.getX()+t2.getY())/2;
		if(areClose(t2.getX(),t2.getY())) t=(t2.getX()+t2.getY())/2;
		if(areClose(t2.getX(),t1.getY())) t=(t2.getX()+t1.getY())/2;
		if (t<1&&t>0) return t;
		
		return 0.5;
		
	}
	
	/**a closeness estimate for a and b*/
	private boolean areClose(double a, double b) {
		int a1=(int) Math.round((20*a));
		int b1=(int) Math.round((20*b));
		return a1==b1;
		
	}
	
	
	/**its just the quadratic formula*/
private static 	Point2D[] quadratic(Point2D a, Point2D b, Point2D c) {
	Point2D minfourAC = multiply(a,c);minfourAC=multiply(minfourAC, -4);
	Point2D root = sqrt(add(multiply(b,b), minfourAC));
	Point2D numberator1 = add(multiply(b,-1),root); 
	Point2D numberator2 = subtract(multiply(b,-1),root);
	Point2D denom=multiply(a, 2);
	Point2D t1 = divide(numberator1,denom);
	Point2D t2 = divide(numberator2,denom);
	
	return new 	Point2D[] {t1,t2};
}

	
	
	/**when given two points find a point in between the two*/
	static Point2D intermediatePoint(Point2D p1, Point2D p2, double t) {
		return add(multiply(p2, t), multiply(p1, 1-t));
	}
	
	/**reverses the intermediate point formula to find p2*/
	static Point2D followingIntPoint(Point2D p1, Point2D p3, double t) {
		return add(multiply(p3, 1/t), multiply(p1, -(1-t)/t));
	}

	
	/**reverses the intermediate point formula to find p1*/
	static Point2D followingIntPoint2(Point2D p2, Point2D p3, double t) {
		return add(multiply(p3, 1/(1-t)), multiply(p2, -t/(1-t)  ));
	}
	
	
	
	/**multiplies the point's by x and y by t and returns the result*/
	public static Point2D multiply(Point2D p1, double t) {
		return new Point2D.Double(t*p1.getX(), t*p1.getY());
	}
	/**finds the square root of the points x and y*/
	static Point2D sqrt(Point2D p1) {
		return new Point2D.Double(Math.sqrt(p1.getX()), Math.sqrt(p1.getY()));
	}
	/**multiplies the points x and y*/
	static Point2D multiply(Point2D p1, Point2D p2) {
		return new Point2D.Double(p2.getX()*p1.getX(), p2.getY()*p1.getY());
	}
	/**divides the points x and y*/
	static Point2D divide(Point2D p1, Point2D p2) {
		return new Point2D.Double(p1.getX()/p2.getX(), p1.getY()/p2.getY());
	}
	/**divides the points x and y by p2*/
	static Point2D divide(Point2D p1, double p2) {
		return new Point2D.Double(p1.getX()/p2, p1.getY()/p2);
	}
	
	public static Point2D.Double add(Point2D p1, Point2D p2) {
		return new Point2D.Double(p2.getX()+p1.getX(), p2.getY()+p1.getY());
	}
	static Point2D.Double subtract(Point2D p1, Point2D p2) {return new Point2D.Double(-p2.getX()+p1.getX(), -p2.getY()+p1.getY());}

	static double dot(Point2D p1, Point2D p2) {
		return p2.getX()*p1.getX()+ p2.getY()*p1.getY();
	}
	
	/**performs a reflection transform of point about a line defined by the other two points*/
	public static Point2D.Double reflectPointAboutLine(Point2D p1, Point2D line1, Point2D line2) {
		
		Point2D vectLine = subtract(line2, line1);
		Point2D vectPoint = subtract(p1, line1);
		
		Point2D vprojection = multiply(vectLine, dot(vectLine , vectPoint)/(dot(vectLine ,vectLine )));
		
		
		//subtracts the orthogonal line 
		Point2D vorthogonal = subtract(vectPoint, vprojection);
		Point2D.Double vout = subtract(vectPoint, vorthogonal ); 
		vout = subtract(vout, vorthogonal );
				
		return add(vout, line1);
	}
	
	/**projects a point onto the line between two other points*/
	public static Point2D projectPointOntoLine(Point2D p1, Point2D line1, Point2D line2) {
		Point2D vectLine = subtract(line2, line1);
		Point2D vectPoint = subtract(p1, line1);
		
		Point2D vprojection = multiply(vectLine, dot(vectLine , vectPoint)/(dot(vectLine ,vectLine )));
		
		return add(vprojection, line1);
		
	}
	
	
	/**Assuming a cubic Bezier curve, finds the point at position t*/
	static Point2D cubic(Point2D[] pts, double t) {
		if (pts.length==4) {
			return cubic(pts[0], pts[1], pts[2], pts[3], t);
			
		}
		else {throw new IllegalArgumentException();}
	}
	
	
	
	
	
	/**gets the cordinates of a point at t. Bezier curve
	  p0, anchor point1
	  p1 curve control exit of p0
	  p2 curve control entry of p3
	  p3 anchor point2
	  */
	 static final Point2D cubic(Point2D p0, Point2D p1, Point2D p2, Point2D p3, double t) {
	    double x = Math.pow(1-t, 3) * p0.getX() + 3 * Math.pow(1-t,2) * t * p1.getX() + 3 * Math.pow(1-t,1) * Math.pow(t,2) * p2.getX() + Math.pow(t, 3) * p3.getX();
	    double y = Math.pow(1-t, 3) * p0.getY() + 3 * Math.pow(1-t,2) * t * p1.getY() + 3 * Math.pow(1-t,1) * Math.pow(t,2) * p2.getY() + Math.pow(t, 3) * p3.getY();

	    return new Point2D.Double(x, y);
	}
	 /**given two path points and a t (between 0 and 1), returns a spot on the curve*/
	 public static Point2D interPolatePlaceOnCurve(PathPoint previois, PathPoint pp, double t) {
		 return cubic(previois.getAnchor(), previois.getCurveControl2(), pp.getCurveControl1(), pp.getAnchor() , t);
	}
	 
	/**not sure if correct but should compute the tangent to a point*/
	 static final Point2D tangentAtPointCubic(Point2D p0, Point2D p1, Point2D p2, Point2D p3, double t) {
		   // double x = -3* Math.pow(1+t, 2) * p0.getX() + 3 * (3*t*t+ -4*t+1 ) * p1.getX() + 3 * (2-3*t)*t* p2.getX() + 3*Math.pow(t, 2) * p3.getX();
		  //  double y = -3* Math.pow(1-t, 3) * p0.getY() + 3 * (3*t*t+ -4*t+1 )* p1.getY() + 3 * (2-3*t)*t * p2.getY() + 3* Math.pow(t, 2) * p3.getY();
		   double x = -3*Math.pow(1-t, 2) * p0.getX() + -6 * Math.pow(1-t,1) * t * p1.getX() -6 * Math.pow(1-t,1) * p2.getX() -3*Math.pow(t,2) * p2.getX() + 3*Math.pow(t, 2) * p3.getX();
		    double y = -3*Math.pow(1-t, 2) * p0.getY() + -6 * Math.pow(1-t,1) * t * p1.getY() -6 * Math.pow(1-t,1) * p2.getY()-3* Math.pow(t,2) * p2.getY() + 3* Math.pow(t, 2) * p3.getY();

		 
		    return new Point2D.Double(x, y);
		}
	 public static Point2D interPolateTangentOfPlaceOnCurve(PathPoint previois, PathPoint pp, double t) {
		 return tangentAtPointCubic(previois.getAnchor(), previois.getCurveControl2(), pp.getCurveControl1(), pp.getAnchor() , t);
	}
	 
	/**Adds a line to the path*/
	public void lineTo(double x1, double y1) {
        add(new PathPoint(x1, y1));
    }

	
	
	/**Adds a curve to the path. x3 and y3 are the anchor, 2, curve control 1, 0 is curvecontrol or previous bpoint outgoing*/
	public void curveTo(double x, double y, double x2, double y2, double x3,
			double y3) {
		this.getLastPoint().setCurveControl2(new Point2D.Double(x, y));
		
		
		PathPoint newp = new PathPoint(x3,y3);
		newp.setCurveControl1(new Point2D.Double(x2, y2));
		
	}
	
	/**not actually the normals but returns the direction vectors of each point 
	 * that correspond to the direction from one point to the next*/
	public Point2D[] getDiffVectors() {
		Point2D[] output = new  Point2D[this.size()-1];
		for(int i=1; i<this.size(); i++) {
			java.awt.geom.Point2D.Double diff = subtract(get(i).getAnchor(), get(i-1).getAnchor());
			
			double length = Math.sqrt(dot(diff, diff));
			output[i-1]=divide(diff, length);
		}
		return output;
		
	}
	
	/**Simplifies the path by removing 1/n-th of points that are too close to their neigbors*/
	public void cullCloseByPoints(double tolerance, int n, boolean random) {
		ArrayList<PathPoint> toCull=new ArrayList<PathPoint> ();
			
		for(int i=1; i<this.size()-1; i++) {
			java.awt.geom.Point2D.Double diff = subtract(get(i).getAnchor(), get(i-1).getAnchor());
			
			double length = Math.sqrt(dot(diff, diff));
			
java.awt.geom.Point2D.Double diff2 = subtract(get(i+1).getAnchor(), get(i).getAnchor());
			
			double length2 = Math.sqrt(dot(diff2, diff2));
			
			
			if (length<tolerance&&length2<tolerance) toCull.add(get(i));
		}
		if (random)Collections.shuffle(toCull);
		for(int i=0; i<toCull.size(); i+=n) {
			PathPoint out = toCull.get(i);
			if (this.get(0)==out) continue;
			if (this.get(size()-1)==out) continue;
			remove(out);
		}
		
	
	}
	
	public boolean hasCloseByPoints(double tolerance) {
		
		for(int i=1; i<this.size(); i++) {
			java.awt.geom.Point2D.Double diff = subtract(get(i).getAnchor(), get(i-1).getAnchor());
			
			double length = Math.sqrt(dot(diff, diff));
			if (length<tolerance) return true;
		}
	return false;
	
	}
	
	/**removes a point and adjusts the curve control points of nearby points to make a similar looking bezier curve*/
	public void cullPointAndAdjustCurvature(PathPoint p) {
		if (p==null||this.get(0)==p) return;
		if (this.get(size()-1)==p) return;
		
		PathPoint previous = this.getPreviousPoint(p);
		if (previous==null){return;}
		Point2D pointe = PathPointList.intermediatePoint(previous.getCurveControl2(), p.getCurveControl1(), 1.25);
		previous.setCurveControl2(pointe);
		
		PathPoint next = this.getNextPointForCurve(p);
		if (next==null){return;}
	    pointe= PathPointList.intermediatePoint(next.getCurveControl1(), p.getCurveControl2(), 1.25);
		 next.setCurveControl1(pointe);
		 this.remove(p);
	}
	
	/**removes points that have little impact on the appearance of the line*/
	public void cullUselessPoints(double tolerance, boolean curve, int frac, boolean random) {
		Point2D[] directions = getDiffVectors();
		
		ArrayList<PathPoint> toCull=new ArrayList<PathPoint> ();
		
		for(int i=1; i<directions.length-1; i++) {
			double similarity = Math.abs(dot(directions[i], directions[i+1]));
			if (similarity >tolerance) toCull.add(this.get(i+1));
		}
		
		
		
		if (random)Collections.shuffle(toCull);
		
		for(int j=0; j<toCull.size(); j+=3) {
			 PathPoint p= toCull.get(j);
			 if (curve)cullPointAndAdjustCurvature(p);
			 this.remove(p); 
		}
		for(int j=0; j<toCull.size(); j+=2) {
			 PathPoint p= toCull.get(j);
			 if (curve)cullPointAndAdjustCurvature(p);
			 this.remove(p); 
		}
		
		for(int j=0; j<toCull.size(); j+=1) {
			 PathPoint p= toCull.get(j);
			 if (curve)cullPointAndAdjustCurvature(p);
			 this.remove(p); 
		}
		
		
	}
	
	/**a bunch of point arrays that define lines that are tangent to the curve*/
	public ArrayList<Point2D[]> getTangentVectors() {
		 ArrayList<Point2D[]> output = new  ArrayList<Point2D[]>();
		for(int i=1; i<this.size(); i++) {
			output.add(tangentOfPath(i-1, 0.5));
		}
		return output;
		
	}
	
	/**a bunch of points in equivalent positions along the curve*/
	public Point2D[] getMidpionts(double t) {
		Point2D[] output = new  Point2D[this.size()-1];
		for(int i=1; i<this.size(); i++) {
			java.awt.geom.Point2D diff =interPolatePlaceOnCurve(this.get(i-1),get(i), t);
			
			output[i-1]=diff;
		}
		return output;
		
	}
	
	
	/**returns a point along the curve
	public Point2D.Double getCurve(double T, pathPoint p1, pathPoint p2) {
		double x=Math.pow(T, 3)*p1.getAnchor().x+3*Math.pow(T, 2)*Math.pow(1-T, 1)+3*p1.getCurveControl2().x+Math.pow(T, 1)*Math.pow(1-T, 2)*p2.getCurveControl1().x+Math.pow(1-T, 2)*p2.getAnchor().x;
		double y=Math.pow(T, 3)*p1.getAnchor().y+3*Math.pow(T, 2)*Math.pow(1-T, 1)+3*p1.getCurveControl2().y+Math.pow(T, 1)*Math.pow(1-T, 2)*p2.getCurveControl1().y+Math.pow(1-T, 2)*p2.getAnchor().y;
		
		return new Point2D.Double(x, y);
	}*/
	
	
	/**tangent of a path at the given segment number. uses t to determine to position of the new point
	 * Its an implementation of De Casteljau's Algorith*/
	public Point2D[] tangentOfPath(int segnu, double t ) {
		if (segnu<0) segnu+=this.size();
		PathPoint p1 = this.get(segnu);
		PathPoint p2 = this.getNextPointForCurve(p1);
		
		java.awt.geom.Point2D.Double[] curve = getBezierControlPointsForOutgoing(p1, p2);
		
		
		
		
		Point2D m1 = intermediatePoint(curve[0], curve[1], t);
		Point2D m2 = intermediatePoint(curve[1], curve[2], t);
		Point2D m3 = intermediatePoint(curve[2], curve[3], t);
		
		
		Point2D m1B = intermediatePoint(m1, m2,t);
		Point2D m2B = intermediatePoint(m2, m3,t);
		return new Point2D[] {m1B, m2B};
	};
	
	public void smoothCurve() {
		for(PathPoint l:this ) {
		l.evenOutAngleOfCurveControls(0.5);
		
	}
	
	}

	/**de-selects the points*/
	public void deselectAll() {
		for(locatedObject.PathPoint p: this) {
			p.deselect();
		}
	}
	
	
	/**returns a version of the shape that lacks quad curves
	 * @param path2
	 * @return
	 */
	public static Path2D convertToCubicShape(Shape path2) {
		PathPointList pa = createFromIterator(path2.getPathIterator(AffineTransform.getTranslateInstance(0, 0)));
		Path2D it2 = pa.createPath(true);
		return it2;
	}
}
