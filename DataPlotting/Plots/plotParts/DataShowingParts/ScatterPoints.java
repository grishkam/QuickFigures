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
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataPoint;
import dataSeries.DataSeries;
import dialogs.ScatterPointsDialog;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.RectangularGraphic;

public class ScatterPoints extends DataShowingShape {

	public static int EXCLUDE_WITHIN15IQR=1, NO_Exclusion;
	private PointModel pointModel=new PointModel();
	private ArrayList<plotPoint> plottingShapes;
	public boolean needsPlotPointUpdate=true;
	public boolean needsJitterUpdate=true;
	private boolean doesJitter=true;

	private int exclusion=0;
	
	{super.setStrokeWidth(0);}{super.setName("Scatter Points");}

	public ScatterPoints(Shape shape2) {
		super(shape2);
		// TODO Auto-generated constructor stub
	}
	

	
	public ScatterPoints(DataSeries data, boolean jitter) {
		super(data);
		this.doesJitter=jitter;
	}
	
	public ScatterPoints copy() {
		ScatterPoints o = new ScatterPoints(getTheData(), doesJitter);
		o.copyEverythingFrom(this);
		return o;
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(ScatterPoints m) {
		this.exclusion=m.exclusion;
		this.setBarWidth(m.getBarWidth());
		doesJitter=m.doesJitter;
		this.getPointModel().setPointSize(m.getPointModel().getPointSize());
		super.copyStrokeFrom(m);
	}
	
	public void copyEverythingFrom(ScatterPoints m) {
		this.copyAttributesFrom(m);
		this.copyColorsFrom(m);
		this.copyStrokeFrom(m);
		this.copyTraitsFrom(m);
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		double sizep = getPointModel().getPointSize();
		getPointModel().setPointSize(sizep*mag);
		this.requestShapeUpdate();
	}



	protected void updateShape() {
		
		//plottingShapes=createPlotPointShapeList();
		if (this.needsJitterUpdate&&isDoesJitter()) {
			updateListWithJitter(getPlotingShapes());
			needsJitterUpdate=false;
		}
		currentDrawShape=this.combineShapes(getPlotingShapes(), true);;
		
	}

	
	
	
	private Shape combineShapes(ArrayList<plotPoint> list, boolean complete) {
		Area output = new Area();
		
		for(plotPoint d: list) {
			if (this.getExclusion()==EXCLUDE_WITHIN15IQR) {
				if (this.getTheData().getIncludedValues().isWith15IQR(d.value)) continue;
			}
			if (complete) {
				output.add(new Area(d.getShapePostJitter()));
			} else
			output.add(new Area(d.getPointBoundsPostJitter()));
		}
		
		return output;
	}
	
	public ArrayList<plotPoint> getPlotingShapes() {
		if (this.plottingShapes==null||needsPlotPointUpdate) {
			plottingShapes=createPlotPointShapeList();
			needsPlotPointUpdate=false;
			needsJitterUpdate=false;
		}
		if (needsJitterUpdate) {}
		
		return plottingShapes;
	}
	
	/**returns the point list. */
	private ArrayList<plotPoint> createPlotPointShapeList() {
		ArrayList<plotPoint> shapes=new ArrayList<plotPoint>();
		for(int i=0; i<getTheData().length() ;i++) {
			if (area==null) continue;
			plotPoint pt = this.getPlottingPointFor(i);
			if (pt!=null && this.area.getPlotArea().contains(pt.position))	{
				pt.graphic=getPointModel().getShapeGraphicForCordinatePoint(pt);
				shapes.add(pt);
				}
		}
		return shapes;
	}
	
	/**returns the shapes for the scatter points. If some overlap, it shifts them over
	 * @return */
	private ArrayList<plotPoint> updateListWithJitter(ArrayList<plotPoint> plottingShapes) {
		//ArrayList<plotPoint> plottingShapes=createPlotPointShapeList();
		
		/**Sorts based on their y axis position*/
		Collections.sort(plottingShapes, new Comparator<plotPoint>() {
			@Override
			public int compare(plotPoint arg0, plotPoint arg1) {
				 return (int) (arg0.value-arg1.value);
				
			}});
		
		//ArrayList<Shape> outputShape = new ArrayList<Shape> ();
		
		int direction=1;
		for(int i=1; i<plottingShapes.size(); i++)  {
			plotPoint shape1=plottingShapes.get(i);
			ArrayList<plotPoint> list = createListUpTo(plottingShapes, shape1);
			Rectangle2D b = shape1.graphic.getBounds();
			boolean moved=false;
			while(combineShapes(list, false).intersects(shape1.getPointBoundsPostJitter().getBounds2D())  ) {
				
				AffineTransform tf;
				if (super.onVertical())
						 tf= AffineTransform.getTranslateInstance((b.getWidth()*.2)*direction, 0);
					else tf= AffineTransform.getTranslateInstance(0, (b.getWidth()*.2)*direction);
				
				shape1.add(tf);
				//shape1=tf.createTransformedShape(shape1);
				//b = shape1.getBounds2D();
				moved=true;
				
			}
			//shapes.set(i, shape1);
			
			if (moved) direction=-direction;//changes direction after moving the shape
			//outputShape.add(shape1);//if the shape has moved, a new shape is put down.
		}
		
		return plottingShapes ;
	}
	

	
	private ArrayList<plotPoint> createListUpTo(ArrayList<plotPoint> shapes, plotPoint shape1) {
		ArrayList<plotPoint> output = new ArrayList<plotPoint>();
		for(plotPoint s: shapes) {
			if (s==shape1) break;
			output.add(s);
		}
		return output;
	}

	

	
	
	plotPoint getPlottingPointFor(int i) {
		DataPoint dataPoint = this.getTheData().getDataPoint(i);
		if (dataPoint.isExcluded()) return null;
		plotPoint point = new plotPoint();
		double d=dataPoint.getValue();
		double p=dataPoint.getPosition();
		point.setDataValues(p,d);
		if (area!=null)
			point.setXYCordPoint();
		return point;
	
	}
	

public class plotPoint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RectangularGraphic graphic;
	private double in;
	private double value;
	public Point2D.Double position;//XY cordinate of plotting
	AffineTransform jitterTransform=new AffineTransform();
	
	public plotPoint() {}
	
	public void add(AffineTransform tf) {
		jitterTransform.concatenate(tf);
	}

	void setDataValues(double independant, double dependant) {
		this.in=independant;
		this.value=dependant;
	}
	
	void setXYCordPoint() {
		 position=calculateXYCordPoint();
	}
	
	Point2D.Double calculateXYCordPoint() {
		return getCordinateHandler().translate(in, value, getTheData().getPositionOffset(), 0)  ;
		/**if (onVertical()) {
			return  new Point2D.Double(area.transformX(in)+getTheData().getPositionOffset(),area.transformY(value));
		}
		return new Point2D.Double(area.transformX(value),area.transformY(in)+getTheData().getPositionOffset());
	*/
		}
	
	/**Returns the rectangular graphic bounds after jitter transform*/
	Shape getPointBoundsPostJitter() {
		setXYCordPoint();
		RectangularGraphic c = graphic;
		c.setLocation(position);
		return jitterTransform.createTransformedShape(c.getBounds());
	}
	
	/**Returns the rectangular graphic bounds after jitter transform*/
	Shape getShapePostJitter() {
		setXYCordPoint();
		RectangularGraphic c = graphic;
		c.setLocation(position);
		return jitterTransform.createTransformedShape(c.getRotationTransformShape());
	}
	
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 3645956010435911232L;



	
	public void showOptionsDialog() {
		new ScatterPointsDialog(this, false).showDialog();
	}

	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		
		super.draw(g, cords);
	}
	
	/**thickly strokes the shapes, to create an outline somewhat larger than the 
	 * actual shapes*/
	public Shape getOutline() {
		Area a = new Area(super.getOutline());
		a.add(new Area(new BasicStroke(3).createStrokedShape(a)));
		//return super.getOutline().getBounds();
		return a;
	}
	
	


	
	
	

	/**so other code does not mess up the angle*/
	public void setAngle(double angle) {
		getPointModel().getModelShape().setAngle(angle);
	}
	
	public double getAngle() {
		return 0;
	}



	public PointModel getPointModel() {
		return pointModel;
	}



	public void setPointModel(PointModel pointModel) {
		this.pointModel = pointModel;
	}



	public ArrayList<plotPoint> getPlottingShapes() {
		return plottingShapes;
	}



	public void setPlottingShapes(ArrayList<plotPoint> plottingShapes) {
		this.plottingShapes = plottingShapes;
	}



	public boolean isDoesJitter() {
		return doesJitter;
	}



	public void setDoesJitter(boolean doesJitter) {
		this.doesJitter = doesJitter;
	}



	public int getExclusion() {
		return exclusion;
	}



	public void setExclusion(int exclusion) {
		this.exclusion = exclusion;
	}

	@Override
	public double getMaxNeededValue() {
		Basic1DDataSeries data = this.getTheData().getIncludedValues();
		return data.getMax();

	}



	
	


	



}
