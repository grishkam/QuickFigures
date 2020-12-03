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
package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class ReverseBasicCordinateConverter extends BasicCoordinateConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ReverseBasicCordinateConverter(double x2, double y2,
			double scale) {
		setX(x2);
		setY(y2);
		setMagnification(scale);
	}

	@Override
	public double transformX(double ox) {
		return ox/getMagnification()+getX();
	}

	@Override
	public double transformY(double oy) {
		return oy/getMagnification()+getY();
	}
	
	@Override
	public Font getScaledFont(Font font) {
		return font.deriveFont((float)(font.getSize()/getMagnification()));
	}

	@Override
	public BasicStroke getScaledStroke(BasicStroke stroke) {    
	        double mag = getMagnification();
	        if (mag!=1.0) {
	            float width = (float)(stroke.getLineWidth()/mag);
	            float[] oldDash = stroke.getDashArray();
	            float[] newDash=new float[stroke.getDashArray().length] ;
	            for(int i=0; i<newDash.length; i++) {newDash[i]=(float) (oldDash[i]/mag);}
	            //return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	            return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), newDash, stroke.getDashPhase());
	        } else
	            return stroke;
	    }
	
	public AffineTransform getAffineTransform() {
		AffineTransform af = AffineTransform.getScaleInstance(1/getMagnification(), 1/getMagnification());
		//af.translate(-transformX(0), -transformY(0));
		af.translate(-getX(), -getY()); 
		return af;
	}
	
	
	public static void main(String[] args) {
		ReverseBasicCordinateConverter bb = new ReverseBasicCordinateConverter(100,30,1);
		bb.testConsistency(new Point(50,40));
		bb.testConsistency(new Point(-50,40));
		bb.testConsistency(new Point(50,4));
	}
	
}
