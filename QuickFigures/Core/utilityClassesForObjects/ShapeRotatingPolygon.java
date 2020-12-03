/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package utilityClassesForObjects;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

import layersGUI.GraphicTreeUI;
import logging.IssueLog;
import standardDialog.AngleInputPanel;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberArrayInputPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import utilityClasses1.GeometryLineUtil;



/**This class creates rotates and transforms polygons.
 its most recent uses would be the creation of irregular shapes
 Its original use was to move a set of vertexes that was given to it
 but later I adapted it to simply create a shape with a bunch of parameters
 */
public class ShapeRotatingPolygon implements Serializable, ShapeMaker  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double startingangle=0;
	private double limitAngle=Math.PI*2;
	private boolean randomizeArray=false;
	/**This Point will contain the cordinates of the object's center
	   that it will rotate around.
	   Will rotate the Object by resetting allVertrices and creating a 
	   new shape*/
	public Point2D centerOfRotation= new Point2D.Double(0, 0);
	
	/**This is the Point that will be the geomentric center of regular polygons
	   if the polygon is not regular this will be the same as the center of Rotation*/
	public Point2D centerOfPosition= new Point(0, 0);
	
	
	/**This maintains a polygon that is inside the compound*/
	private PathPointList pathPointList= new PathPointList();
	
	/**This maintains the vetices of the object. They can be changed*/
	public Point2D[] allVertices;
	
	/**This maintains the distances between the centroid and each vertex.
	  I do not plan for these values to ever change*/
	public double[] allVertexDisplacements;
	
	/**This maintains the angle from the centroid to each Vertex as part of a 
	  mini polar co-ordiante system. These will change with the rotation*/
	public double[] allVertexAngles;
	
	private float [] allIrregularDistances=new float [] {};
	
	/**maintains the number of vertices*/
	private int nVertex=7;
	private boolean alternateDistorts=false;
	
	private float[] moveCurveInward=new float[] {1};
	
	
	private double rotateInCurve=0;
	
	private double standardDisplacement=1;

	private double postangle;

	private boolean midpointCurve;

	private int curveDistype=0;
	private double xfactor=1;
	private double yfactor=1;
	
	public ShapeRotatingPolygon() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ShapeRotatingPolygon(Point2D[] a) {
	
		allVertices=a;
		setnVertex(allVertices.length);
		
		centerOfPosition=calGeoCenter();
		centerOfRotation=centerOfPosition;
		setVertexAnglesBasedonVertexPositions();
		setVertexDisplacements();
		
		resetPolygonFromVertices();
		//addMouseMotionListener(this);
		// TODO Auto-generated constructor stub
	}
	
	public ShapeRotatingPolygon(double x, double y, Point2D[] a) {
	
		allVertices=a;
		setnVertex(allVertices.length);
		setCenterRot(x, y);//setCentroid();
		centerOfPosition=calGeoCenter();

		
		resetPolygonFromVertices();
		
		// TODO Auto-generated constructor stub
	}
	
	
	/**This constructor has the user define parameters to create a regular polygon that rotates around a given Point*/
	public ShapeRotatingPolygon(int x, int y, double displacement, int vertexNum){
		centerOfRotation=new Point(x, y);
		setUpRegularVertexes(displacement, vertexNum);
		
	
		centerOfPosition=calGeoCenter();
		setVertexAnglesBasedonVertexPositions();
		setVertexDisplacements();
		resetPolygonFromVertices();
		//addMouseMotionListener(this);
	}
	
	
	
	public void setUpRegularVertexes(double displacement, int vertexNum) {
		 setStandardDisplacement(displacement);
		 nVertex=vertexNum;
		 setUpRegularVertexes();
	}
	
		 public void setUpRegularVertexes() {
			
		Point2D[] a = getRegularVertices(centerOfPosition, getStandardDisplacement(),  nVertex);

		allVertices=a;
		//setnVertex(allVertices.length);
		setVertexAnglesBasedonVertexPositions();
		setVertexDisplacements();
		resetPolygonFromVertices();
		
	}
	
/**This moves the center of rotation to a specified piont
   precondition: the allVertices field should already be defined*/
	public void setCenterRot(double x, double y){
		centerOfRotation=new Point2D.Double(x, y);
		setVertexAnglesBasedonVertexPositions();
		setVertexDisplacements();
	}
	
	/**When calculates the position of a GPoint that is at the geomentric center of the polygon
	   precondition: the allVertices field should already be defined*/
	public Point2D calGeoCenter() {
		double x=0;
		double y=0;
		for (int i= 0; i<getnVertex() ;i++) {
			x=x+allVertices[i].getX();
			y=y+allVertices[i].getY();	
		}
		return new Point2D.Double(x/getnVertex(), y/getnVertex());
	}
	
	/**method will calculate the distance between each Vertix and the Centroid
	   and store them in the field allVertexdisplacements.
	   precondition: the center of rotation and allVertices must already be defined*/
	public void setVertexDisplacements() {
		allVertexDisplacements= new double[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			//compute the distance between given point and centriod
			allVertexDisplacements[i]= allVertices[i].distance(centerOfRotation);//GMath.distance(centerOfRotation.getX(), centerOfRotation.getY(), allVertices[i].getX(), allVertices[i].getY());
		}	
	}
	
	/**method will calculate the distance between each Vertix and the Point
	   and store them in the field allVertexdisplacements.
	   precondition: the center of rotation and allVertices must already be defined*/
	public void setVertexDisplacements(Point2D center) {
		allVertexDisplacements= new double[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			//compute the distance between given point center
			allVertexDisplacements[i]= center.distance( allVertices[i]);//GMath.distance(center.getX(), center.getY(), allVertices[i].getX(), allVertices[i].getY());
		}	
	}
	
	/**sets the allVertexDisplacements to a specified double.*/
	public void setVertexDisplacements(double allSameDisplacement) {
		allVertexDisplacements= new double[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			//compute the distance between given point and centriod
			allVertexDisplacements[i]= allSameDisplacement;
		}	
	}
	
	/**This method will calculate the angle between the centroid, Vertices and baseline to give
	   the radial cordinate of the object. 
	   precondition: allVertices and center of rotation must already be defined.*/
	public void setVertexAnglesBasedonVertexPositions() {
		allVertexAngles= new double[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			double distanceX= allVertices[i].getX()-centerOfRotation.getX();
			double distanceY= allVertices[i].getY()-centerOfRotation.getY();	
			if (distanceX>0) allVertexAngles[i]= fixAngle(Math.atan(distanceY/distanceX));//computes the angle in radians of the polar position of the vertex relative to the centroid.
			if (distanceX<0) allVertexAngles[i]= fixAngle(Math.atan(distanceY/distanceX)+Math.PI);//computes the angle in radians of the polar position of the vertex relative to the centroid.
		    if (distanceX==0 && distanceY>0) allVertexAngles[i]= Math.PI;
		    if (distanceX==0 && distanceY<0) allVertexAngles[i]= Math.PI*3/2;
		    if (distanceX==0 && distanceY==0) allVertexAngles[i]=0;
		}		
	}
	
	/**This method will set the vertex angles as needed for a regular polygon with the given number of verteces*/
	/**private void setVertexAngles(int numberOfVertex) {
		allVertexAngles= new double[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			 allVertexAngles[i]=2*Math.PI*i/numberOfVertex;
		}		
	}*/
	
	/**This method will set up the vertices according to the location of the centroid, angles and distances
	   Precondition: centerOfRoation, allVertexDisplacement and allvertex angles must be defined.*/
	private void setVerticesBasedOnAngles() {
		    allVertices= new Point2D[getnVertex()];
		for (int i= 0; i<getnVertex() ;i++) {
			
			allVertices[i]= new Point2D.Double(
					centerOfRotation.getX()    +allVertexDisplacements[i]     *Math.cos(  allVertexAngles[i] )       ,
			        centerOfRotation.getY()    +allVertexDisplacements[i]     *Math.sin(  allVertexAngles[i] ) 
			);
		}		
		
	}
	
	
	/**This method will alter the points angles positions depending on the angle.
	   changes allVertexAngle field.
	   precondition: allVertexAngles must be defined.*/
	public void rotatedAngle(double ang){	
		for (int i= 0; i<getnVertex() ;i++) {
			
			allVertexAngles[i]=fixAngle(allVertexAngles[i]+ang);
		
		}	
		setVerticesBasedOnAngles();
	}
	
	/**Replaces the polygon with an updated one.
	   precondition: AllVertex field must be defined*/
	public void resetPolygonFromVertices(){
		//remove(part);
		
		if (allVertices.length!=getnVertex())setVerticesBasedOnAngles() ;
		setPathPointList(new PathPointList());//new Polygon();
		for (int i= 0; i<getnVertex()&&i<allVertices.length; i++){
			getPathPointList().addPoint(allVertices[i].getX(), allVertices[i].getY());
		}
		
			Point2D p0 = this.centerOfRotation;//.calGeoCenter();
		Point2D[] pts = getPathPointList().getAnchors();
		if (this.isMidpointCurve()) pts=this.getPathPointList().getMidpointsOfAnchors();
			
		for (int i= 0; i<getPathPointList().size(); i++){
			Point2D p=p0;
			
			if (this.getCurveDistype()==1) try{
				Line2D bisect = GeometryLineUtil.perpendicularBisector(getPathPointList().getLineToPoint(i));
				if (bisect.getP1().distance(p0)<bisect.getP2().distance(p0)) p=bisect.getP1(); else p=bisect.getP2();
				
			}
			catch (Throwable t) {
				IssueLog.logT(t);
			}
			if (this.getCurveDistype()==2) {
				p=new Point2D.Double(pts[i].getX(), p.getY());
			}
			
			if (this.getCurveDistype()==0) {p=p0;}
			
				moveCurvePointToward(p,pts[i],i);
			}
		
		if (getPostangle()!=0) {
			getPathPointList().applyAffine(AffineTransform.getRotateInstance(getPostangle(), p0.getX(), p0.getY()));;
		}
		if (stretch!=1||stretchv!=1) {
			getPathPointList().applyAffine(AffineTransform.getScaleInstance(stretch, stretchv));;
		}
		if (this.randomizeArray) {
			try {
			//	getPathPointList().randomizePointPositions();
			} catch (Throwable e) {
				IssueLog.logT(e);
			}
		}
		
		
		//add(part);
		
	}
	
private int getCurveDistype() {
		// TODO Auto-generated method stub
		return curveDistype;
	}


public void moveCurvePointToward(Point2D towardthis, Point2D startingpoint, int i) {
	double dx=towardthis.getX()-startingpoint.getX();
	double dy=towardthis.getY()-startingpoint.getY();
	double angle = this.getRotateInCurve();
	double factor=getMoveCurveInward(i);
	
	AffineTransform af = AffineTransform.getRotateInstance(angle, towardthis.getX(), towardthis.getY());
	
	dx*=factor;
	dy*=factor;
	
	Double pnew = new Point2D.Double(towardthis.getX()-dx, towardthis.getY()-dy);
	Double pnew2 = new Point2D.Double(towardthis.getX()-dx, towardthis.getY()-dy);
	af.transform(pnew, pnew2);
	getPathPointList().get(i).setCurveControl1(pnew2);
}
	
	



	/**Rotates the GCompund graphic*/
	public void rotate(double ang){
		rotatedAngle(ang);
		resetPolygonFromVertices();
	}

	
	/**This will move the object's center to a new location. 
	   it will also move the center or rotation and vertices with it
	public void setCenterLocation(double x, double y){
		setLocation(x-centerOfPosition.getX(),   y-centerOfPosition.getY());
	}*/
	
	/**If an angle becomes out of the range between 0 and 2PI this will fix it*/
	public double fixAngle(double angle) {
		if (angle> 2*Math.PI) return angle-2*Math.PI;
		if (angle<0) return angle+2*Math.PI;
		return angle;
	}
	
	/**when given a center, number of vertices and a size will return the vertices of a regular polygons*/
	public Point2D[] getRegularVertices(Point2D centerOfPosition2, double radian, int vertexNum){
		Point2D[] output=new Point2D[vertexNum];
		for (int i=0;i<vertexNum;i++){
			double factor=getRadianExtensionFactorForVertex(i);
			double angle=getLimitAngle()/vertexNum+getAngleDistortionForVertex(i);
			double a = i*angle+this.getStartingangle();
		output[i]=new Point2D.Double(
				centerOfPosition2.getX()   + radian     *Math.cos( a ) *factor *getXfactor()     ,
		        centerOfPosition2.getY()   + radian     *Math.sin(  a ) *factor *getYfactor()  
		);
		
	
		
	}
		return output;
	}
	
	private float[] angleDistorts=new 	float[]{};

	public double stretch=1;

	public double stretchv=1;
	
	private double getAngleDistortionForVertex(int i) {
		if (getAngleDistorts()==null) return 0;
		if (getAngleDistorts().length==0)return 0;
		while(i>=getAngleDistorts().length) i-=getAngleDistorts().length;
		if (i<getAngleDistorts().length)
		return getAngleDistorts()[i];
		// TODO Auto-generated method stub
		return 0;
	}


	public double getRadianExtensionFactorForVertex(int i) {
		if (getAllIrregularDistances()==null ||getAllIrregularDistances().length==0) return 1;
		while (i>=getAllIrregularDistances().length) i-=getAllIrregularDistances().length;
		
		if (i>-1) {
		
			float pot = getAllIrregularDistances()[i];
			if (pot==0) return 1;
			 return pot;
			
			}
		return 1;
	}



	public float [] getAllIrregularDistances() {
		return allIrregularDistances;
	}

	public void setAllIrregularDistances(float [] allIrregularDistances) {
		this.allIrregularDistances = allIrregularDistances;
	}
	
	public void setAllIrregularDistances(double... allIrregularDistances) {
		float[] in = new float[allIrregularDistances.length] ;
		for(int i=0;i<allIrregularDistances.length;i++) {
			in[i]=(float)allIrregularDistances[i];
		}
		this.allIrregularDistances = in;
	}
	
	static float[] fromDouble(double[] d) {
		float[] in = new float[d.length] ;
		for(int i=0;i<d.length;i++) {
			in[i]=(float)d[i];
		}
		return in;
	}

	public PathPointList getPathPointList() {
		return pathPointList;
	}

	public void setPathPointList(PathPointList pathPointList) {
		this.pathPointList = pathPointList;
	}


	public double getMoveCurveInward(int i) {
		
		
		
		//if (moveCurveInward.length>i) {
		
			try {
				if (moveCurveInward == null || moveCurveInward.length == 0)
					return 1;
				while (i >= moveCurveInward.length)
					i -= moveCurveInward.length;
				float pot = moveCurveInward[i ];
				//if (pot==0) return 1;
				return pot;
			} catch (Throwable e) {
				IssueLog.logT(e);
				return 1;
			}
			
		//	}
		//return 1;
		
	}


	public void setMoveCurveInward(double... moveCurveInward) {
		
		this.moveCurveInward = fromDouble(moveCurveInward);
	}
	
	public int getnVertex() {
		return nVertex;
	}


	public void setnVertex(int nVertex) {
		this.nVertex = nVertex;
	}
	
	public void showDialog(PathObject p) {
		diaLog d = new diaLog(this,p);
		d.showDialog();
		if (p==null) {
		d.setToFields() ;
		this.setUpRegularVertexes();
		}
	}

	public double getStandardDisplacement() {
		return standardDisplacement;
	}


	public void setStandardDisplacement(double standardDisplacement) {
		this.standardDisplacement = standardDisplacement;
	}

	public double getRotateInCurve() {
		return rotateInCurve;
	}


	public void setRotateInCurve(double rotateInCurve) {
		this.rotateInCurve = rotateInCurve;
	}

	public boolean isAlternateDistorts() {
		return alternateDistorts;
	}


	public void setAlternateDistorts(boolean alternateDistorts) {
		this.alternateDistorts = alternateDistorts;
	}

	public class diaLog extends StandardDialog{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ShapeRotatingPolygon shape;
		boolean realtome=false;
		PathObject p=null;
		
		public diaLog(ShapeRotatingPolygon s, PathObject p) {
			this.p=p;
			if (p!=null) {this.realtome=true;
			this.setModal(false);
			}else setModal(true);
			this.shape=s;
			
			add("Vert", new NumberInputPanel("N Vertex ",shape.getnVertex()));
			add("distance", new NumberInputPanel("Size ",shape.getStandardDisplacement(),2));
			
			NumberArrayInputPanel irinput4=new NumberArrayInputPanel(6,2);{irinput4.setLabel("CurveDistorts");}
			irinput4.setArray(shape.getMoveCurveInward());
			add("curve distort factor", irinput4);
			//add("curve distort factor2", new NumberInputPanel("Curve Distort",shape.getMoveCurveInward(1), 3));
			add("curve distort angle", new AngleInputPanel("Curve Distort angle",shape.getRotateInCurve(), true));
			add("limit angle", new AngleInputPanel("Range angle",shape.getLimitAngle(), true));
			add("alternate d", new BooleanInputPanel("Midpoint Curve", shape.isMidpointCurve()));
			NumberArrayInputPanel irinput=new NumberArrayInputPanel(6,2);{irinput.setLabel("Irregular Length Extension Factors");}
			NumberArrayInputPanel irinput2=new NumberArrayInputPanel(6,2);{irinput2.setLabel("Angle distort factors");}
			add("stretch",  new NumberInputPanel("Curve Stretch Horozontal",shape.stretch, 3));
			add("stretchv",  new NumberInputPanel("Curve Stretch Vertical",shape.stretchv, 3));
			add("posta",  new AngleInputPanel("post form rotation",shape.getPostangle(), true));
			irinput.setArray(shape.getAllIrregularDistances());
			irinput2.setArray(shape.getAngleDistorts());
			add("arr", irinput);
			add("arr2", irinput2);
			add("dType", new ComboBoxPanel("Curve Distort Type", new String[] {"to center", "perpendicular to local line", "up/down"}, shape.getCurveDistype()));
			//add("distance displacements", new NumberArrayInputPanel("Distance Distort Factors ", shape.allIrregularDistances)); 
		}
		
		
		public void setToFields() {
			shape.setnVertex(this.getNumberInt("Vert"));
			shape.setStandardDisplacement(this.getNumber("distance"));
			shape.setMoveCurveInward(this.getNumberArray("curve distort factor"));
			
			shape.setLimitAngle(this.getNumber("limit angle"));
			shape.setRotateInCurve(this.getNumber("curve distort angle"));
			shape.setAllIrregularDistances(this.getNumberArray("arr"));
			shape.setMidpointCurve(this.getBoolean("alternate d"));
			shape.setAngleDistorts(this.getNumberArray("arr2"));
			shape.stretch=this.getNumber("stretch");
			shape.stretchv=this.getNumber("stretchv");
			shape.setPostangle(this.getNumber("posta"));
			shape.setCurveDistype(this.getChoiceIndex("dType"));
			shape.setUpRegularVertexes();
			
		}
		
		protected void afterEachItemChange() {
			if (!this.realtome)  return;
				
				this.setToFields() ;
			shape.setUpRegularVertexes();
			shape.resetPolygonFromVertices();
			if (p!=null) {
				p.setPoints(shape.getPathPointList());
				p.updateDisplay();
			
			}
		}
		
	}
	
	public static void main(String[] args) {
		GraphicTreeUI.main(args);
	}




	public float[] getMoveCurveInward() {
		// TODO Auto-generated method stub
		return this.moveCurveInward;
	}


	public void setMoveCurveInward(float[] numberArray) {
		// TODO Auto-generated method stub
		this.moveCurveInward=numberArray;
	}


	public float[] getAngleDistorts() {
		return angleDistorts;
	}


	public void setAngleDistorts(float[] angleDistorts) {
		this.angleDistorts = angleDistorts;
	}


	public double getPostangle() {
		return postangle;
	}


	public void setPostangle(double d) {
		this.postangle = d;
	}


	public boolean isMidpointCurve() {
		return midpointCurve;
	}


	public void setMidpointCurve(boolean midpointCurve) {
		this.midpointCurve = midpointCurve;
	}


	public void setCurveDistype(int curveDistype) {
		this.curveDistype = curveDistype;
	}


	public double getXfactor() {
		return xfactor;
	}


	public void setXfactor(double xfactor) {
		this.xfactor = xfactor;
	}


	public double getYfactor() {
		return yfactor;
	}


	public void setYfactor(double yfactor) {
		this.yfactor = yfactor;
	}


	public double getStartingangle() {
		return startingangle;
	}


	public void setStartingangle(double startingangle) {
		this.startingangle = startingangle;
	}


	public double getLimitAngle() {
		return limitAngle;
	}


	public void setLimitAngle(double limitAngle) {
		if (limitAngle==0||limitAngle<Math.PI/180) {
			this.limitAngle=Math.PI*2;
			return;
		}
		this.limitAngle = limitAngle;
	}


	public boolean isRandomizeArray() {
		return randomizeArray;
	}


	public void setRandomizeArray(boolean randomizeArray) {
		this.randomizeArray = randomizeArray;
	}

}
