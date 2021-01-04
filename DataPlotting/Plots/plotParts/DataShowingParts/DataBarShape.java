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
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.HashMap;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import dialogs.MeanBarDialog;
import graphicalObjects_Shapes.RectangularGraphic;
import handles.SmartHandleList;
import plotParts.Core.PlotCordinateHandler;

public class DataBarShape extends DataShowingShape implements SeriesLabelPositionAnchor {

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static int LineOnly=0, Bar=1, Ghost=2, SinglePoint=3;
	;{super.setName("Mean");}
	
	private int type=Bar;
	private PointModel pointModel=null;
	boolean finishStroke=true;
	private transient BarSmartHandleList smartHandles;
	
	public DataBarShape(DataSeries data, int type) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setBarType(type);
	}
	
	public DataBarShape(DataSeries data) {
		this(data, Bar);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(DataBarShape m) {
		this.type=m.getBarType();
		this.setBarWidth(m.getBarWidth());
		super.copyStrokeFrom(m);
	}
	
	public DataBarShape copy() {
		DataBarShape output = new DataBarShape(this.getTheData());
		output.copyEveryThingFrom(this);
		return output;
	}
	
	public void copyEveryThingFrom(DataBarShape t) {
		copyTraitsFrom(t);
		copyStrokeFrom(t);
		copyColorsFrom(t);
		copyAttributesFrom(t);
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		
		super.partialShapes=new HashMap<Shape, DataSeries> ();
		
	/**	if (getTheData().getAllPositions().length==1)
		{
			Basic1DDataSeries datai = getTheData().getIncludedValues();
			currentDrawShape=getShapeForDataPoint(datai.getMean(), (double)getPosistion(), type);
			partialShapes.put(currentDrawShape, datai);
		}
		else*/ {
			
			/**Combines output shapes for each position in the series*/
			Path2D outputShape=new Path2D.Double();
			double[] pos = getTheData().getAllPositions();
			
			/**appends the error bars for all positions with enough points for a bar*/
			for(int i=0; i<pos.length; i++) {
				DataSeries datai = getTheData().getValuesForPosition(pos[i]);
				if (isDataSeriesInvalid(datai)) continue;
				Shape pShape = createPartialShapeForData(datai.getIncludedValues());
				partialShapes.put(pShape, datai);
				outputShape.append(pShape, false);
						}
			currentDrawShape=outputShape;
		}
	}
	
	public Rectangle getPlotLabelLocationShape() {
		return getShapeForDataPoint(getTheData().getIncludedValues().getMean(),(double)this.getPosistion(), Bar).getBounds();
	}
	
	/**creates a shape for the data point d*/
	Shape createPartialShapeForData(Basic1DDataSeries datai) {
		return getShapeForDataPoint(datai.getMean(), datai.getPosition(0), type);
	}
	
	/**creates a shape for the data point d*/
	Shape getShapeForDataPoint(Double mean, Double position, int type) {
		if (area==null) return new Rectangle(0,0,20,10);
		Path2D output = new Path2D.Double();
		double offset = this.getTheData().getPositionOffset();
		double vOff = super.getValueOffset(position);
		
		PlotCordinateHandler c = getCordinateHandler();
		
		double value0=0;
		if (c.getDependantVariableAxis().getAxisData().getMinValue()>0) {
			value0=c.getDependantVariableAxis().getAxisData().getMinValue();
		}
		double valueMean=mean;

		java.awt.geom.Point2D.Double p1 = c.translate(position, valueMean+vOff, offset-getBarWidth(), 0);
		java.awt.geom.Point2D.Double p2 = c.translate(position, valueMean+vOff, offset+getBarWidth(), 0);
		java.awt.geom.Point2D.Double p3 = c.translate(position, value0+vOff, offset-getBarWidth(), 0);
		java.awt.geom.Point2D.Double p4 = c.translate(position, value0+vOff, offset+getBarWidth(), 0);
		
		
		/**A line accross the mean*/
		if (type==LineOnly) {
			super.lineBetween(output, p1, p2);
		}
		
		if (type==Ghost) {
			super.lineBetween(output, p3, p4);
		}
		
		if (type==SinglePoint) {
			java.awt.geom.Point2D.Double p5 = c.translate(position, valueMean+vOff, offset, 0);
			
			PointModel pointModel = this.getPointModel();
			RectangularGraphic cop = pointModel.createBasShapeCopy();
			cop.setLocation(p5.x, p5.y);
			return cop.getRotationTransformShape();
		}
		
		if (type==Bar) {
			super.lineBetween(output, p3, p1);
			output.lineTo(p2.x, p2.y);
			output.lineTo(p4.x, p4.y);
			if (this.finishStroke)output.lineTo(p3.x, p3.y);
			}
		return output;
		
	}

	public void updatePlotArea() {
		// TODO Auto-generated method stub
		
	}
	
	public void showOptionsDialog() {
		new MeanBarDialog(this, false).showDialog();;
	}

	public int getBarType() {
		return type;
	}

	public void setBarType(int type) {
		this.type = type;
	}
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
public Shape getOutline() {
		if (type==Bar) return super.getOutline();
		if (type==SinglePoint) return new Area(this.getShape());
		Rectangle b = super.getOutline().getBounds();
		if (this.onVertical()) b.height=2; else b.width=2;
		return b;
	}

@Override
public Shape createOutlineForShape(Shape s) {
	if (type==Bar) return s.getBounds();
	if (type==SinglePoint) {
		Area out = new Area(s);
		//double cx = s.getBounds().getCenterX();
		//double cy = s.getBounds().getCenterY();
		out.add(new Area(new BasicStroke(3).createStrokedShape(s)));
		return out;
		};
	return super.createOutlineForShape(s);
}

public boolean showsAsPoint() {
	return type==SinglePoint;
}

	public PointModel getPointModel() {
		if (pointModel==null) {
			pointModel=new PointModel();
			pointModel.setPointSize(6);
		}
		return pointModel;
	}

	public void setPointModel(PointModel pointModel) {
		this.pointModel = pointModel;
	}
	
	boolean hasPointModel() {
		return pointModel!=null;
	}
	
	@Override
	public
	boolean isHidden() {
		if (type==Ghost) return true;
		return super.isHidden();
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new BarSmartHandleList(this);
		return SmartHandleList.combindLists(smartHandles,super.getButtonList());
	}
	

}
