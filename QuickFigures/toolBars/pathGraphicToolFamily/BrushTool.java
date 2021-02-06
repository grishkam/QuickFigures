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
 * Version: 2021.1
 */
package pathGraphicToolFamily;


import java.awt.Color;
import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.PathGraphic;
import icons.TreeIconWrappingToolIcon;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.numbers.NumberInputPanel;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.PathEditUndo;

/**A tool that allows the user to draw a line
 * TODO: improve algorithm to something more akin to that used by Jhot draw*/
public class BrushTool extends PathGraphicTool {

	public BrushTool(boolean fill, int ar) {
		super(fill, ar);
	}


	PathGraphic p=null;
	double pathSimplificatoinTolerance=0.95;
	int nearbyPointCull=5;
	boolean randomize=true;
	private PathEditUndo undo;
	
	{model= new PathGraphic(new Point(0,0));
		model.setStrokeColor(Color.black); model.setStrokeWidth(2); 
		model.setDashes(new float[] {}); model.setAntialize(true);
		}
	
	{super.iconSet=TreeIconWrappingToolIcon.createIconSet(model);}


	
	
	@Override
	public void mousePressed() {
		super.mousePressed();
		if(super.getSelectedHandleNumber()>-1) return;
		if(this.clickCount()>1)
			onDoubleclick();
		
	
		
		
		p=new PathGraphic(new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
		p.setNArrows(nArrow);
		p.copyAttributesFrom(model);
		p.copyColorsFrom(model);
		p.select();
		super.setPrimarySelectedObject(p);
		
		GraphicLayer setcur = this.getImageClicked().getTopLevelLayer();
		setcur.add(p);
		
		undo = new PathEditUndo(p);
		CombinedEdit undo2 = new CombinedEdit(new UndoAddItem(setcur, p), undo);
	
		getImageDisplayWrapperClick().getUndoManager().addEdit(undo2);
		
		super.getImageClicked().updateDisplay();

		
	}

	private void onDoubleclick() {
		p=null;
	}
	
	@Override
	public void mouseReleased() {
		if(super.getSelectedHandleNumber()>-1) {
			super.mouseReleased();;
			return;
		}
		if (p!=null)  {
			
			if (this.shiftDown())new simplifyDialog(p).showDialog();
			else simplifyPath(p, pathSimplificatoinTolerance, randomize);
			
			p=null;
			this.setPrimarySelectedObject(null);
			super.finishPath();
		}
		onDoubleclick();
	}
	
	void simplifyPath(PathGraphic p, double tolerance, boolean random) {
		if (p!=null) {
			
			
			for(int i=0; i<100; i++) p.getPoints().cullCloseByPoints(nearbyPointCull, 8,  random);
			//p.getPoints().cullCloseByPoints(nearbyPointCull, 1,  false);
			//while(p.getPoints().hasCloseByPoints(8)) p.getPoints().cullCloseByPoints(12);
			//while(p.getPoints().hasCloseByPoints(8)) p.getPoints().cullCloseByPoints(12);
			
			
			//while(p.getPoints().hasCloseByPoints(8)) p.getPoints().cullCloseByPoints(8);
			//p.getPoints().cullUselessPoints(0.99, false, 1);
			p.getPoints().cullUselessPoints(tolerance, true, 1, random);
			p.getPoints().smoothCurve();
			p.updatePathFromPoints();
			p.updateDisplay();
		}
	}
	
	@Override
	public void mouseDragged() {
		
		if(super.getSelectedHandleNumber()>-1) {
			super.mouseDragged();
			return;
		}
		undo = new PathEditUndo(p);
		
		Point newp = new Point(this.getDragCordinateX(), this.getDragCordinateY());
		if (p!=null ) {
			p.addPoint(newp);
			
		}
		
		if (p!=null ) {
			try {
				p.transform().createInverse().transform(newp, newp);
			} catch (NoninvertibleTransformException e) {
				IssueLog.logT(e);
				
			}
			p.setHandleMode(PathGraphic.THREE_HANDLE_MODE);

			
			p.updatePathFromPoints();
			
		}
		
		
		getImageClicked().updateDisplay();
		
		
	}
	
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
	}
	
	@Override
	public String getToolName() {
		
		return "Brush Curve Tool";
	}
	
	@Override
	public String getToolTip() {
			
			return "Draw Curves";
		}
	

	class simplifyDialog extends StandardDialog {  
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PathGraphic p1;
		private PathGraphic p2;

		public simplifyDialog(PathGraphic p1) {
			this.p1 = p1;
			this.p2=p1.copy();
			p2.select();
			p2.setHandleMode(PathGraphic.THREE_HANDLE_MODE);
			p1.getParentLayer().add(p2);p2.moveLocation(10,10);
			add("tolerance",new  NumberInputPanel("tolerance", pathSimplificatoinTolerance*1000, 800, 1000 ));
			add("range",new  NumberInputPanel("Eliminate Nearby Points", nearbyPointCull, 0, 30 ));
			add("r", new BooleanInputPanel("randomize cull order", randomize));
		}
		
		protected void afterEachItemChange() {
			p2.setPoints(p1.getPoints().copy());
			pathSimplificatoinTolerance=this.getNumber("tolerance")/1000;
			nearbyPointCull=(int) this.getNumber("range");;
			randomize=this.getBoolean("r");
			simplifyPath(p2, pathSimplificatoinTolerance, randomize);
			p1.updateDisplay();
		}
	}
	

}
