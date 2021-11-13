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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D.Double;

import dataSeries.DataSeries;
import dataSeries.KaplanMeierDataSeries;
import plotParts.Core.PlotCordinateHandler;
import plotParts.DataShowingParts.AbstractDataLineShape;
import plotParts.DataShowingParts.RegressionLineShape;

/**A data shape that shows the survival curve for a kaplan meier plot*/
public class KaplanMeierLineShape extends  AbstractDataLineShape {

	/**if only a line is to be drawn*/
	public static final int BASIC_LINE=0;
	
	/**if a semitransparent spread is drawn with the line. not yet implemented*/
	public static final int LINE_WITH_SPREAD=1;
	
	;{super.setName("Line");}
	
	int lineType=BASIC_LINE;
	
	KaplanMeierDataSeries dKap=null;
	
	public KaplanMeierLineShape(KaplanMeierDataSeries data, int type) {
		super(data);
		dKap=data;
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setKaplanLineType(type);
	}
	
	public KaplanMeierLineShape(KaplanMeierDataSeries data) {
		this(data, BASIC_LINE);
	}
	
	
	
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(RegressionLineShape m) {
		this.lineType=m.getLineType();
		super.copyStrokeFrom(m);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		if (dKap==null||getTheData().getAllPositions().length==1||area==null) {
			if (area==null)currentDrawShape= new Rectangle(0,0,10,10);return;
		}
		
		PlotCordinateHandler c = super.getCordinateHandler();
			
		java.awt.geom.Path2D.Double path = new Path2D.Double();
		double[] all = dKap.getAllPositions();
		
		double lastPercent=1;
			
		for(int i=0; i<all.length; i++) {
			double indX = all[i];
			double depY = dKap.getEstimatorAtTime(indX);
			Double point = c.translate(indX, depY, 0,0);
			Double point2 = c.translate(indX, lastPercent, 0,0);//the previous height
			if (!area.getPlotArea().contains(point)) {
				//IssueLog.log("Plot axis problem");
				continue;
			}
			if (i==0) {
				path.moveTo(point.getX(), point.getY());
				lastPercent=depY;
				continue;
			};
			
			path.lineTo(point2.getX(), point2.getY());
			if (point.getY()!=point2.getY()) path.lineTo(point.getX(), point.getY());
			lastPercent=depY;
		}
		
		currentDrawShape=path;
	}
	

	public int getKaplanLineType() {
		return lineType;
	}

	public void setKaplanLineType(int type) {
		this.lineType = type;
	}
	

	/**kaplan meier plots range from 0 to 1, returns 1 as the maximun*/
	@Override
	public double getMaxNeededValue() {return 1;}
	
	/**returns the data series that this object drew at a given xy coordinate
	 */
	public DataSeries getPartialSeriesDrawnAtLocation(double dx, double dy) {
		return dKap;
	}
	
	/**returns the bounds of the line*/
	public Shape getPartialShapeAtLocation(double dx, double dy) {
		return this.getBounds();
	}
	
	
}
