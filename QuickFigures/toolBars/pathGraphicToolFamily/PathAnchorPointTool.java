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
package pathGraphicToolFamily;

import java.util.ArrayList;

import graphicTools.GraphicTool;
import graphicalObjects_Shapes.PathGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.PathPoint;
import objectDialogs.PathPointOptionsDialog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import undo.*;

public class PathAnchorPointTool extends GraphicTool {

	{createIconSet("icons2/CurveLineIcon.jpg","icons2/LineIconPressed.jpg","icons2/LineIcon.jpg");
	this.realtimeshow=false;
	}
	
	int mode=2;
	boolean samecurveControlAsLast=false;
	private LocatedObject2D lastPath;
	
	{this.setSelectOnlyThoseOfClass(PathGraphic.class);}
	
	
	public void mousePressed() {
		super.mousePressed();
		
		if (getPrimarySelectedObject()==null) this.setPrimarySelectedObject(lastPath);;
		
		if (this.getPrimarySelectedObject()instanceof PathGraphic) {
			
			 PathGraphic p=(PathGraphic) this.getPrimarySelectedObject();
			 
			 /**will set i to the index of the pressed handle*/
			 int i=this.getSelectedHandleNumber();
			//	while(i>1000) i-=1000;
				
			/**determines if i has a valid index number*/	
			boolean valid =isIndexValid(p.getPoints(),i);
			 
			
			
			if (mode==0&&p!=null) {
				
				//p.setCurvemode(!altKeyDown());
				p.setSupercurvemode(!altKeyDown());
			}
			
			if (mode>0)this.setSelectedHandleNumber(i);
			
			PathEditUndo undo = new PathEditUndo(p);
			performActionOnPath(p, valid, i);
			
			undo.saveFinalPositions();
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
			
			
			lastPath = p;
		}
		//super.mousePressed();
		
	
	}
	
	
	void performActionOnPath(PathGraphic p,  boolean valid, int anchorPointIndex) {
		
		if (mode==1 && valid) {
				p.getPoints().remove(anchorPointIndex);
				p.updatePathFromPoints();
		}
		
		
		
		if (mode==2) {
					
		}
		
		
		
		if (mode==3&&valid) {
			PathPoint pi = p.getPoints().get(anchorPointIndex);
			p.getPoints().makeSPatternTypeCurveControl(pi);
			p.updatePathFromPoints();
		}
		
		if (mode==4&&valid) {
			PathPoint pi = p.getPoints().get(anchorPointIndex);
			PathPointOptionsDialog dialog = new PathPointOptionsDialog(p, pi);
			dialog.showDialog();
		}
		
		if (mode==5) {
			//p.setCurvemode(false);
			p.setSupercurvemode(false);
		}
		
		if (mode==6) {
			p.setHandleMode(PathGraphic.CURVE_CONTROL_HANDLES_LINKED);
			
		}
		
		if (mode==7) {
			if (valid) {
				PathPoint pi = p.getPoints().get(anchorPointIndex);
				if (!pi.isSelected())pi.select();
				
			}
			p.setHandleMode(PathGraphic.MOVE_ALL_SELECTED_HANDLES);
			
		}
	}

	private boolean isIndexValid(ArrayList<?> arr, int i) {
	if (i<=-1) return false;
	if (i> arr.size()) return false;
	return true;
	}
	
	@Override 
	public void showOptionsDialog() {
		StandardDialog sd = new StandardDialog();
		sd.add("mode", new ChoiceInputPanel("Mode", new String[] {"Curve Mode", "Remove point mode", "Add Point mode", "Autocalculate curve control", "Point options mode", "Anchor Point mode", "Curve Points Along Line", "Move All Selected Points"}, mode));
		sd.add("nc",new BooleanInputPanel( "new points are curved ",this.samecurveControlAsLast));
		sd.setModal(true);
		sd.showDialog();
		mode=sd.getChoiceIndex("mode");
		this.samecurveControlAsLast=sd.getBoolean("nc");
		if (mode>1&&mode!=6)this.realtimeshow=true; else 
			this.realtimeshow=false;
	}
	
	
	@Override
	public String getToolName() {
		return "Path Anchor Point Tool";
	}
	
	@Override
	public String getToolTip() {
			
			return "Manipulate Anchor Points";
		}
	
	/**Called when a tool is about to be switched away from (false) or switched to (true)*/
	@Override
	public void onToolChange(boolean b) {
		
			if (b&&lastToolsSelectedItem!=null) {
				lastPath=lastToolsSelectedItem;
				
			}
		super.onToolChange(b);
		
	}
	
}
