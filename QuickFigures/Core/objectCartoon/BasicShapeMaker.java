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
 * Version: 2021.1
 */
package objectCartoon;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import applicationAdapters.ToolbarTester;
import graphicalObjects_Shapes.PathGraphic;
import imageDisplayApp.StandardWorksheet;
import locatedObject.PathPointList;
import imageDisplayApp.ImageWindowAndDisplaySet;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import storedValueDialog.ReflectingFieldSettingDialog;

public abstract class BasicShapeMaker  implements ShapeMaker, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	public void addHorizontalFlippedIn(PathPointList path, double x) {
		
		PathPointList tobeAdded = path;
		tobeAdded=path.getTransformedCopy(createHFlip(x)).getOrderFlippedCopy();
		path.concatenate(tobeAdded);
	}
	
	
	public static AffineTransform createHFlip(double x) {
		AffineTransform out = AffineTransform.getTranslateInstance(x, 0);
		out.concatenate(AffineTransform.getScaleInstance(-1, 1));
		out.concatenate(AffineTransform.getTranslateInstance(-x, 0));
		return out;
	}
	
	public static AffineTransform createVFlip(double y) {
		AffineTransform out = AffineTransform.getTranslateInstance(0, y);
		out.concatenate(AffineTransform.getScaleInstance(1, -1));
		out.concatenate(AffineTransform.getTranslateInstance(0, -y));
		return out;
	}
	

	protected static void showShape(ShapeMaker  cpc) {
		StandardWorksheet gl=new StandardWorksheet();
		
		
		
		PathGraphic  pg=createDefaultCartoon(cpc);
		gl.addItemToImage(pg);
		gl.setWidth(900);
		gl.setHeight(700);
		 ImageWindowAndDisplaySet.show(gl);
		 ToolbarTester.showToolSet();
		 
		 createUpdatingDialog(pg, cpc);
			
	}
	
	public static PathGraphic createDefaultCartoon(ShapeMaker  cpc) {
		PathGraphic pg = new PathGraphic(cpc.getPathPointList() );
		
		pg.setStrokeColor(Color.gray);
		pg.setDashes(new float[]{0});
		pg.setAntialize(true);
		pg.setFillColor(Color.green);
		pg.setClosedShape(true);
		return pg;
	}
	
	public static void createUpdatingDialog(PathGraphic pg,ShapeMaker  cpc ) {
		 ReflectingFieldSettingDialog fsd = new ReflectingFieldSettingDialog(cpc);
			fsd.addDialogListener(new shapeUpdater(cpc, pg));
			fsd.showDialog();
	}
	
	static class shapeUpdater implements StandardDialogListener {

		private ShapeMaker sm;
		private PathGraphic pg;

		public  shapeUpdater(ShapeMaker sm, PathGraphic pg) {
			this.sm=sm;
			this.pg=pg;
		} 
		
		@Override
		public void itemChange(DialogItemChangeEvent event) {
			if (sm!=null&&pg!=null) {
				pg.setPoints(sm.getPathPointList());
				pg.updatePathFromPoints();
				pg.updateDisplay();
			}
			
		}
		
	}
	
	public void moveAllIntoView(PathPointList  p) {
		Rectangle bound = p.createPath(true).getBounds();
		double x=0;
		double y=0;
			 x=bound.x;
			y=bound.y;
			AffineTransform aft = AffineTransform.getTranslateInstance(-x, -y);
			p.applyAffine(aft);
	}



	

}
