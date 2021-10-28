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
 * Date Modified: Jan 7, 2021
 * Version: 2021.2
 */
package kaplanMeierPlots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D.Double;

import dataSeries.DataSeries;
import dataSeries.KaplanMeierDataSeries;
import dialogs.CensorMarkDialog;
import graphicalObjects_Shapes.RectangularGraphic;
import plotParts.Core.PlotCordinateHandler;
import plotParts.DataShowingParts.DataLineShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.PointModel;

/**A data shape that shows the censor marks for a kaplan meier plot*/
public class KaplanMeierCensorShower extends  DataShowingShape implements DataLineShape{

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static final int LINE_ONLY=0, CROSSING_LINE=1, PLUS_MARK=2, CIRCLE=3, OTHER_SHAPE_MARK=4;;
	;{super.setName("Censored");
	super.setBarWidth(3);
	}
	
	int markType=LINE_ONLY;
	PointModel pointModel=new PointModel();
	KaplanMeierDataSeries dKap=null;
	
	public KaplanMeierCensorShower(KaplanMeierDataSeries data, int type) {
		super(data);
		dKap=data;
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setMarkType(type);
	}
	
	public KaplanMeierCensorShower(KaplanMeierDataSeries data) {
		this(data, LINE_ONLY);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(DataLineShape m) {

		super.copyStrokeFrom(m);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**updates the shape to fit the data and settings*/
	@Override
	protected void updateShape() {
		if (dKap==null||getTheData().getAllPositions().length==1||area==null) {
			if (area==null)currentDrawShape= new Rectangle(0,0,10,10);return;
		}
		
		PlotCordinateHandler c = super.getCordinateHandler();
			
		java.awt.geom.Path2D.Double path = new Path2D.Double();
		double[] all = dKap.getAllPositions();
		
			
		for(int i=0; i<all.length; i++) {
			double indX = all[i];
			double depY = dKap.getEstimatorAtTime(indX);
			double censor=dKap.getNumberCensoredAtTime(indX);
			if (censor==0) continue;
			
			Double point = c.translate(indX, depY, 0,0);
			double barWidth = getBarWidth();
			Double point2 = c.translate(indX, depY, 0,-barWidth);//the previous height
			
			
			if (markType==CIRCLE) {
				java.awt.geom.Ellipse2D.Double e = new Ellipse2D.Double(point.getX()-barWidth, point.getY()-barWidth,  2*barWidth, 2*barWidth);
				path.append(e, false); continue;
			}
			if (markType==OTHER_SHAPE_MARK) {
				RectangularGraphic cop = pointModel.createBasShapeCopy();
				cop.setWidth(2*barWidth);cop.setHeight(2*barWidth);
				cop.setLocation(point);
				path.append(cop.getRotationTransformShape(), false); continue;
			}
			
			if (markType==CROSSING_LINE||markType==PLUS_MARK) {
				 point = c.translate(indX, depY, 0,barWidth);
			}
			
			path.moveTo(point.getX(), point.getY());
			path.lineTo(point2.getX(), point2.getY());
			
			if (markType==PLUS_MARK) {
				 point = c.translate(indX, depY, barWidth, 0);
				 point2 = c.translate(indX, depY, -barWidth, 0);
				 path.moveTo(point.getX(), point.getY());
				 path.lineTo(point2.getX(), point2.getY());
			}
			
			
			//if (point.getY()!=point2.getY()) path.lineTo(point.getX(), point.getY());
			
		}
		
		currentDrawShape=path;
	}
	

	
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
	@Override
	public Shape getOutline() {
	 return	new BasicStroke(3).createStrokedShape(this.getShape());

	}

	/**kaplan meier plots range from 0 to 1, returns 1 as the maximun*/
	@Override
	public double getMaxNeededValue() {return 1;}

	/**returns style of mark to be used */
	public int getMarkType() {
		return markType;
	}
	
	/**sets the style of mark to be used */
	public void setMarkType(int choiceIndex) {
		markType=choiceIndex;
	}
	
	public void updatePlotArea() {
		area.fullPlotUpdate();
		
	}

	/**return true if the mark type is an unspecified shape*/
	public boolean showsAsCustomMarkPoint() {
		return markType==OTHER_SHAPE_MARK;
	}

	
	
	public void showOptionsDialog() {
		new CensorMarkDialog(this, false).showDialog();;
	}
	
	public PointModel getPointModel() {
		return pointModel;
	}
	
	
	/**returns the data series that this object drew at a given xy coordinate
	 */
	public DataSeries getPartialSeriesDrawnAtLocation(double dx, double dy) {
		return dKap;
	}
	
	public Shape getPartialShapeAtLocation(double dx, double dy) {
		return this.getBounds();
	}
}
