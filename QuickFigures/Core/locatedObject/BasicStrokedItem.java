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
 * Version: 2022.1
 */
package locatedObject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;

/**A simple implementation of the stroked item interface
 * Also contains static methods that are used by other items*/
public class BasicStrokedItem implements StrokedItem , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Color strokeColor=Color.white;
	Color fillColor=Color.white;
	boolean filled=false;
	
	protected float strokeWidth=1;
	float[] dash=new float[]{4,4};
	
	int end=BasicStroke.CAP_BUTT;
	int join=BasicStroke.JOIN_BEVEL;
	
	double miterLimit=1;
	


	public float[] getDashes() {return dash;}
	public void setDashes(float[] dash) {this.dash=dash;}
	
	public static void copyStrokeProps(StrokedItem recipient, StrokedItem source) {
		recipient.setStrokeColor(source.getStrokeColor());
		recipient.setStrokeJoin(source.getStrokeJoin());
		recipient.setStrokeCap(source.getStrokeCap());
		recipient.setStrokeWidth(source.getStrokeWidth());
		recipient.setDashes(source.getDashes());
		recipient.setMiterLimit(source.getMiterLimit());
		
	}
	
	public static void scaleStrokeProps(StrokedItem recipient, double mag) {
		recipient.setStrokeWidth( (float) (recipient.getStrokeWidth()*mag));
		scaleDashes(recipient, mag);
		
	recipient.setMiterLimit(recipient.getMiterLimit()*mag);
	}
	protected static void scaleDashes(StrokedItem recipient, double mag) {
		float[] d = recipient.getDashes();
		if(d==null)
			return;
		float[] d2 = new float[recipient.getDashes().length];
		for(int i=0; i<d.length; i++) {
			d2[i]=(float) (d[i]*mag);
		}
		recipient.setDashes(d2);
	}
	
	public void setMiterLimit(double miter) {
		 miterLimit=miter;
	}
	public double getMiterLimit() {
		return  miterLimit;
	}
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	@Override
	public Color getStrokeColor() {
		return strokeColor;
	}
	
	@Override
	public BasicStroke getStroke() {
		float width = getStrokeWidth();
		float limit = (float) getMiterLimit();
		float[] d = this.getDashes();
		if (limit<1) limit=1;
		if (width<0) width=0;
		if (d==null) d=new float[] {100000};
		return new BasicStroke(width, end, join, limit, d, 2);
	}
	
	@Override
	public void setStroke(BasicStroke stroke) {
		end=stroke.getEndCap();
		join=stroke.getLineJoin();
		dash=stroke.getDashArray();
		miterLimit=stroke.getMiterLimit();
		setStrokeWidth(stroke.getLineWidth());
	}
	
	@Override
	public float getStrokeWidth() {
		return strokeWidth;
	}

	@Override
	public void setStrokeColor(Color c) {
		strokeColor=c;
		
	}
	
	public int getStrokeJoin() {
		return join;
	}
	public int getStrokeCap(){
		return end;
	}
	
	public void setStrokeJoin(int j) {
		join=j;
	}
	public void setStrokeCap(int e){
		end=e;
	}

}
