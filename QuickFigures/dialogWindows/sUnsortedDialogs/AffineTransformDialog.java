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
 * Date Modified: Jan 6, 2021
 * Version: 2021.2
 */
package sUnsortedDialogs;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import standardDialog.StandardDialog;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.PointInputPanel;

/**shows a dialog that allows the user to define the traits of a tranform*/
public class AffineTransformDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int ROTATES=0, SCALED=1, SHEARED=2, TRANSLATED=3;

	private int type;
	
		AffineTransformDialog( double theta, Point2D about) {
		super("Transform", true);
		this.type=ROTATES;
		if (type==ROTATES) {
			this.add("theta", new AngleInputPanel("rotate", theta, true));
			this.add("about", new PointInputPanel("about ", about));
			this.setTitle("Rotate");
		}
		
	}
	
	public static AffineTransform showRotation(double theta, Point2D about) {
		AffineTransformDialog affineTransformDialog =	new AffineTransformDialog( theta, about);
		affineTransformDialog.showDialog();
		return affineTransformDialog.getTransform();
	}
	
	public AffineTransformDialog( int type, double theta, double about) {
		super("Transform", true);
		this.type=type;
		if (type==SCALED) {
			this.add("xy scale", new PointInputPanel("Scale X,Y", new Point2D.Double(theta, about)));
		}
		if (type==SHEARED) {
			this.add("xy shear", new PointInputPanel("Shear ", new Point2D.Double(theta, about)));
		}
		if (type==TRANSLATED) {
			this.add("xy translate", new PointInputPanel("Translate XY", new Point2D.Double(theta, about)));
		}
	}
	
	
	
	public AffineTransform getTransform() {
		
		if (type==SCALED) {
			float[] ar = this.getNumberArray("xy scale");
			return AffineTransform.getScaleInstance(ar[0],ar[1]);
			
		}
		if (type==SHEARED) {
			float[] ar = this.getNumberArray("xy shear");
			return AffineTransform.getShearInstance(ar[0],ar[1]);
			
		}
		if (type==TRANSLATED) {
			float[] ar = this.getNumberArray("xy translate");
			return AffineTransform.getTranslateInstance(ar[0],ar[1]);
			
		}
		
		if (type==ROTATES) {
			float[] ar = this.getNumberArray("about");
			double theta=this.getNumber("theta");
			return AffineTransform.getRotateInstance(-theta, ar[0],ar[1]);//.getTranslateInstance(ar[0],ar[1]);
			
		}
		
		return new AffineTransform();
			
	}

	public static AffineTransform showScale(Point2D point) {
		AffineTransformDialog affineTransformDialog = new AffineTransformDialog(SCALED, point.getX(), point.getY());
		affineTransformDialog.showDialog();
		return affineTransformDialog.getTransform();
	}
}
