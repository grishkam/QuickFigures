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
package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;

import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.SmartHandleList;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.StrokedItem;

public class BasicShapeGraphic extends ShapeGraphic implements Scales, HasSmartHandles{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Shape shape;
	

	public static BasicShapeGraphic createFilled(Color c, Shape s) {
		BasicShapeGraphic output = new BasicShapeGraphic(s);
		output.setFilled(true);
		output.setFillColor(c);
		output.setStrokeWidth(-1);
		return output;
	}
	
	public static BasicShapeGraphic createStroked(StrokedItem ss, Shape s) {
		BasicShapeGraphic output = new BasicShapeGraphic(s);
		output.setFilled(false);
		BasicStrokedItem.copyStrokeProps(output, ss);
		return output;
	}
	
	
	public BasicShapeGraphic(Shape shape2) {
		setShape(shape2);
	}

	@Override
	public BasicShapeGraphic copy() {
		// TODO Auto-generated method stub
		BasicShapeGraphic out = new BasicShapeGraphic(getShape());
		copyColorAttributeTo(out);
		return  out;
	}
	
	

	

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return getShape().getBounds();
	}

	

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return shape;
	}

	public void setShape(Shape shape) {
		if (!(shape instanceof Serializable)) {
		//	IssueLog.log("non serializable shape", shape.toString());
		}
		this.shape = shape;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		if (!(shape instanceof Serializable)) {
			this.shape=null;
		}
		out.defaultWriteObject();
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		Point2D p2 = this.getLocationUpperLeft();
		AffineTransform af = new AffineTransform();
		af.scale(mag, mag);
		p2=scaleAbout(p2, p,mag,mag);
		shape=af.createTransformedShape(shape);
		this.setLocationUpperLeft(p2);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		
	}
	
	@Override
	public int handleNumber(double x, double y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	@Override
	public SmartHandleList getSmartHandleList() {
		return super.getButtonList();//needs update for lines. fill color not appropriate for lines
	}
}
