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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;

import genericPlot.BasicDataSeriesGroup;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.RectangularGraphic;

public class FigureLegendShape extends RectangularGraphic implements SeriesLabelPositionAnchor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicDataSeriesGroup series;
	private BasicShapeGraphic line;
	transient PointModel pModel;
	
	private static int BAR_TYPE=0, POINT_TYPE=1, LINE_TYPE=2;
	private int type=0;

	
	public FigureLegendShape(BasicDataSeriesGroup genericDataSeriesGroup) {
		super(new Rectangle(0, 0, 12 ,12 ));
		this.series=genericDataSeriesGroup;
		line=new BasicShapeGraphic(new Line2D.Double(0, 0, 10, 0));
		//point=new BasicShapeGraphic(new Line2D.Double(0, 0, 10, 0));
		
	}
	
	void update() {
		if (series.getDataBar()!=null&&series.getDataBar().getBarType()==DataBarShape.Bar){
			this.type=BAR_TYPE;
			this.copyColorsFrom(series.getDataBar());
		} else 
		if (series.getScatterPoints()!=null) {
			copyColorsFrom(series.getScatterPoints());
			this.pModel=series.getScatterPoints().getPointModel();
			this.type=POINT_TYPE;
			} else
		if (series.getDataBar()!=null&&series.getDataBar().showsAsPoint()) {
			copyColorsFrom(series.getDataBar());
			this.pModel=series.getDataBar().getPointModel();
			this.type=POINT_TYPE;
		} else if (series.getLine()!=null) {
			
			this.copyColorsFrom(series.getLine());
			this.copyStrokeFrom(series.getLine());
			this.type=LINE_TYPE;
		} else if (series.getFunctionLine()!=null) {
			DataLineShape line2 = series.getFunctionLine();
			this.setFillColor(line2.getStrokeColor());
			this.setStrokeColor(Color.black);
			this.copyStrokeFrom(line2);
		}


		MeanLineShape l = series.getLine();
		if (l!=null) {
			line.copyColorsFrom(l);
			line.copyStrokeFrom(l);
			line.copyAttributesFrom(l);
		} else line.setHidden(true);
		
		line.setLocation(this.getLocation());
		//point.setLocation(this.getLocation());
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		this.update();
		//line.draw(g, cords);
		///point.draw(g, cords);
		super.draw(g, cords);
	}
	
	

	@Override
	public Rectangle getPlotLabelLocationShape() {
		// TODO Auto-generated method stub
		return this.getRectangle().getBounds();
	}
	
	@Override
	public Shape getShape() {
		if (this.type==POINT_TYPE && pModel!=null)  {
			RectangularGraphic cop = pModel.createBasShapeCopy();
			//cop.setAngle(pModel.ge);
			cop.setRectangle(getRectangle());
			return cop.getRotationTransformShape();
		} 
		if (this.type==LINE_TYPE )  {
			double ly = y+getObjectHeight()/2;
			Line2D.Double line =new Line2D.Double(x, ly, x+this.getObjectWidth(), ly);;
			return line;
		} 
		return getRectangle();
	}
	
	@Override
	public Shape getOutline() {
		return getRectangle();
	}

}
