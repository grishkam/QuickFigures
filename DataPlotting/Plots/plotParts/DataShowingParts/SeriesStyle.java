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
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JColorChooser;

import genericPlot.BasicDataSeriesGroup;
import locatedObject.ColorDimmer;

public class SeriesStyle implements Serializable {

	/**
	 * 
	 */
	
	double angle=0;
	int nSides=0;
	private Color color=Color.red;
	private int nSeries=3;
	private int dimming=0;
	private Color[] possibleColors=new Color[] {Color.DARK_GRAY, Color.red, Color.green, Color.blue, Color.yellow.darker(),  Color.magenta, Color.cyan, Color.lightGray, Color.orange};
	
	boolean colorBarStroke=true;
	private boolean dimColor=true;
	
	private static final long serialVersionUID = 1L;
	
	public static ArrayList<SeriesStyle> getStyles(int s1, int s2) {
		ArrayList<SeriesStyle> output=new ArrayList<SeriesStyle>();
		for(int i=s1; i<=s2; i++) output.add(new SeriesStyle(i));
		return output;
	}
	
	
	public static ArrayList<SeriesStyle> getGreyStyles(int s1, int s2) {
		ArrayList<SeriesStyle> output=new ArrayList<SeriesStyle>();
		Color[] c = new Color[] {Color.white, Color.gray, Color.darkGray.darker(), Color.lightGray.brighter(),  Color.darkGray.darker().darker().darker(), Color.darkGray, Color.lightGray.brighter().brighter().brighter(), Color.lightGray};
		for(int i=s1; i<=s2; i++) output.add(new SeriesStyle(i, c, false));
		return output;
	}
	
	public static ArrayList<SeriesStyle> getStyles(int s1, int s2, Color[] c, boolean cBar) {
		ArrayList<SeriesStyle> output=new ArrayList<SeriesStyle>();
		for(int i=s1; i<=s2; i++) output.add(new SeriesStyle(i, c, cBar));
		return output;
	}
	
	public SeriesStyle(int n) {
		nSides=n+2;
		nSeries=n;
		setColor(possibleColors[n%possibleColors.length]);
	}
	
	public SeriesStyle(int n, Color[] colors, boolean cBar) {
		possibleColors=colors;
		nSides=n+2;
		nSeries=n;
		this.colorBarStroke=cBar;
		setColor(possibleColors[n%possibleColors.length]);
	}
	
	public void applyTo(DataShowingShape shape) {
		
		int nVertex = nSides%6;
		PointModel points=null;
		
			if (shape instanceof ScatterPoints) {
				ScatterPoints scatter=(ScatterPoints) shape;
				points=scatter.getPointModel();
				scatter.setFillColor(getEffectiveColor());
			
			}
			
			if (shape instanceof Boxplot) {
				Boxplot scatter=(Boxplot) shape;
				scatter.setStrokeColor(getEffectiveColor().darker().darker().darker());
			}
			
			if (shape instanceof MeanLineShape) {
				shape.setStrokeColor(getEffectiveColor().darker().darker());
				shape.setFillColor(new Color(255,255,255, 0));
			}
			if (shape instanceof DataLineShape) {
				shape.setStrokeColor(getEffectiveColor().darker().darker());
				shape.setFillColor(new Color(255,255,255, 0));
			}
		
			
			if (shape instanceof DataBarShape) {
				DataBarShape shape2=(DataBarShape) shape;
				shape2.setFillColor(getEffectiveColor());
				if (this.colorBarStroke) 	shape2.setStrokeColor(getEffectiveColor().darker().darker().darker());
				//if (shape2.getBarType()==MeanShowingShape.SinglePoint) {
					points=shape2.getPointModel();
			//	}
			}
			
			if (shape instanceof ErrorBarShowingShape) {
				ErrorBarShowingShape shape2=(ErrorBarShowingShape) shape;
				if (this.colorBarStroke) 	shape2.setStrokeColor(getEffectiveColor().darker().darker().darker().darker());

			}
			
			
			if (points!=null)  {
				points.setNVertex(nVertex);
				points.getModelShape().setAngle(Math.PI/2);
				if (nSeries>4)points.getModelShape().setAngle(3*Math.PI/2);
				if (nVertex==2)points.getModelShape().setAngle(Math.PI/4);
			}
	}
	
	public void applyTo(BasicDataSeriesGroup group) {
		
			group.setGroupColor(getEffectiveColor());
			group.setStyle(this);
		
			for(DataShowingShape d: group.getDataShapes()) {
				applyTo(d);
			}
		
	}

	public Color getEffectiveColor() {
		if (dimColor) {
			if (dimming==4) {
				double cn = 0.5*color.getRed() +0.25*color.getBlue()+0.1*color.getGreen();
				int nc=(int) cn;
				return new Color(nc, nc, nc);
			}
			if (dimming==5) {
				double cn = 0.1*color.getRed() +0.25*color.getBlue()+0.5*color.getGreen();
				int nc=(int) cn;
				return new Color(nc, nc, nc);
			}
			
			return ColorDimmer.modifyColor(color, dimming, true);
		}
		return color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void colorSetDialog() {
		setColor(JColorChooser.showDialog(null, "Color", getEffectiveColor()));
	}


	public int getDimming() {
		return dimming;
	}


	public void setDimming(int dimming) {
		this.dimming = dimming;
	}


	public boolean isDimColor() {
		return dimColor;
	}


	public void setDimColor(boolean dimColor) {
		this.dimColor = dimColor;
	}


	public void setColor(Color color) {
		this.color = color;
	}

}
