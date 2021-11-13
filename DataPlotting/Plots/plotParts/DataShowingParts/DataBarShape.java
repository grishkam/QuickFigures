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
 * Date Modified: Mar 28, 2021
 * Version: 2021.2
 */
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

/**A shape for depiection of the the mean from a list of numbers*/
public class DataBarShape extends DataShowingShape implements SeriesLabelPositionAnchor {

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public final static int LINE_ONLY=0, DATA_BAR_SHAPE=1, GHOST=2, SINGLE_POINT=3;
	;{super.setName("Mean");}
	
	private int type=DATA_BAR_SHAPE;
	private PointModel pointModel=null;
	boolean finishStroke=true;

	
	public DataBarShape(DataSeries data, int type) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setBarType(type);
	}
	
	public DataBarShape(DataSeries data) {
		this(data, DATA_BAR_SHAPE);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(DataBarShape m) {
		this.type=m.getBarType();
		this.setBarWidth(m.getBarWidth());
		super.copyStrokeFrom(m);
	}
	
	/**creates a copy*/
	public DataBarShape copy() {
		DataBarShape output = new DataBarShape(this.getTheData());
		output.copyEveryThingFrom(this);
		return output;
	}
	
	/**changes the appearane of this shape to match the example*/
	public void copyEveryThingFrom(DataBarShape example) {
		copyTraitsFrom(example);
		copyStrokeFrom(example);
		copyColorsFrom(example);
		copyAttributesFrom(example);
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
	
	/**returns the bounds that anchor the location of a plot label*/
	public Rectangle getPlotLabelLocationShape() {
		return getShapeForDataPoint(getTheData().getIncludedValues().getMean(),(double)this.getPosistion(), DATA_BAR_SHAPE).getBounds();
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
		if (type==LINE_ONLY) {
			super.lineBetween(output, p1, p2);
		}
		
		if (type==GHOST) {
			super.lineBetween(output, p3, p4);
		}
		
		if (type==SINGLE_POINT) {
			java.awt.geom.Point2D.Double p5 = c.translate(position, valueMean+vOff, offset, 0);
			
			PointModel pointModel = this.getPointModel();
			RectangularGraphic cop = pointModel.createBasShapeCopy();
			cop.setLocation(p5.x, p5.y);
			return cop.getRotationTransformShape();
		}
		
		if (type==DATA_BAR_SHAPE) {
			super.lineBetween(output, p3, p1);
			output.lineTo(p2.x, p2.y);
			output.lineTo(p4.x, p4.y);
			if (this.finishStroke)output.lineTo(p3.x, p3.y);
			}
		return output;
		
	}

	
	
	public void showOptionsDialog() {
		new MeanBarDialog(this, false).showDialog();;
	}

	/**returns the type of object that this data bar appears as*/
	public int getBarType() {
		return type;
	}

	/**sets the type of object that this data bar appears as*/
	public void setBarType(int type) {
		this.type = type;
	}
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
public Shape getOutline() {
		if (type==DATA_BAR_SHAPE) return super.getOutline();
		if (type==SINGLE_POINT) return new Area(this.getShape());
		Rectangle b = super.getOutline().getBounds();
		if (this.onVertical()) b.height=2; else b.width=2;
		return b;
	}

@Override
public Shape createOutlineForShape(Shape s) {
	if (type==DATA_BAR_SHAPE) return s.getBounds();
	if (type==SINGLE_POINT) {
		Area out = new Area(s);
		out.add(new Area(new BasicStroke(3).createStrokedShape(s)));
		return out;
		};
	return super.createOutlineForShape(s);
}

/**returns true if the shape appears as a data point*/
public boolean showsAsPoint() {
	return type==SINGLE_POINT;
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
		if (type==GHOST) return true;
		return super.isHidden();
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new DataBarSmartHandleList(this);
		return SmartHandleList.combindLists(smartHandles,super.getButtonList());
	}

	/**
	 * 
	 */
	public void updatePlotArea() {
		// TODO Auto-generated method stub
		
	}
	

}
