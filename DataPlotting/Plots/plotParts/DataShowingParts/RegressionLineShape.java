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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D.Double;

import dataSeries.DataSeries;
import dataSeries.XYDataSeries;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import plotParts.Core.PlotAxes;
import plotParts.Core.PlotCordinateHandler;

public class RegressionLineShape extends AbstractDataLineShape{

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static int LineOnly=0;
	;{super.setName("Line");}
	
	int type=LineOnly;
	private double[] regressionResults;
	private ComplexTextGraphic tg=new ComplexTextGraphic();
	
	public RegressionLineShape(DataSeries data, int type) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setLineType(type);
	}
	
	public RegressionLineShape(DataSeries data) {
		this(data, LineOnly);
	}
	
	public java.lang.Double getRSquared() {
		if ( regressionResults==null) return null;
		double rs = regressionResults[3];
		rs=roundToPlace(rs, 3);
		return rs;
	}
	
	public static double roundToPlace(double rs, int places) {
		double p = Math.pow(10, places);
		rs=Math.round(rs*p)/p;
		return rs;
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(RegressionLineShape m) {
		this.type=m.getLineType();
		super.copyStrokeFrom(m);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		if (getTheData().getAllPositions().length==1||area==null) {
			if (area==null)currentDrawShape= new Rectangle(0,0,10,10);
		}
		
		else if (getTheData() instanceof XYDataSeries) {
			regressionResults = ((XYDataSeries) getTheData()) .getLeastSquareLine();
			PlotCordinateHandler cords = getCordinateHandler();
			PlotAxes xAxis = cords.getInDependantVariableAxis().getAxisData();
			double minp = xAxis.getMinValue();
			double maxp = xAxis.getMaxValue();
			double minv=regressionResults[0]*minp+regressionResults[1];
			double maxv=regressionResults[0]*maxp+regressionResults[1];
			
		
			
			Double p1 = cords.translate(minp, minv, getPosistion(), 0);
			Double p2 = cords.translate(maxp, maxv, getPosistion(), 0);
			
			PlotAxes yAxis = cords.getDependantVariableAxis().getAxisData();
			/**if line attempt dips below the y axis*/
			if (minv<yAxis.getMinValue()) {
				double nv = (yAxis.getMinValue()-regressionResults[1])/regressionResults[0];
				p1 = cords.translate(nv, yAxis.getMinValue(), getPosistion(), 0);
			}
			if (minv>yAxis.getMaxValue()) {
				double nv = (yAxis.getMaxValue()-regressionResults[1])/regressionResults[0];
				p1 = cords.translate(nv, yAxis.getMaxValue(), getPosistion(), 0);
			}
			if (maxv>yAxis.getMaxValue()) {
				double nv = (yAxis.getMaxValue()-regressionResults[1])/regressionResults[0];
				p2 = cords.translate(nv, yAxis.getMaxValue(), getPosistion(), 0);
			}
			if (maxv<yAxis.getMinValue()) {
				double nv = (yAxis.getMinValue()-regressionResults[1])/regressionResults[0];
				p2 = cords.translate(nv, yAxis.getMinValue(), getPosistion(), 0);
			}
			
			
			this.currentDrawShape=new Line2D.Double(p1, p2);
			
			
			
			
			String name="y = "+roundToPlace(regressionResults[0],3)+"x + "+roundToPlace(regressionResults[1],3);
			setName(name);
			
			ComplexTextGraphic tg = new ComplexTextGraphic("R");
			tg.getParagraph().addText("2").makeSuperScript();
			tg.getParagraph().addText("= "+getRSquared());
			
			
			this.getInformationText().setParagraph(tg.getParagraph());
			getInformationText().setLocation(p2);

			getInformationText().moveLocation(-90, 0);
			
		}
	}
	

	public int getLineType() {
		return type;
	}

	public void setLineType(int type) {
		this.type = type;
	}
	

	public ComplexTextGraphic getInformationText() {
		return tg;
	}

	public void setInformationText(ComplexTextGraphic tg) {
		this.tg = tg;
	}




	

}
