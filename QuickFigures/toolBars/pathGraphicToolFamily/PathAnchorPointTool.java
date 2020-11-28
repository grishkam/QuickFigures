package pathGraphicToolFamily;

import java.util.ArrayList;

import graphicTools.GraphicTool;
import graphicalObjects_BasicShapes.PathGraphic;
import objectDialogs.PathPointOptionsDialog;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;
import undo.*;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.PathPoint;

public class PathAnchorPointTool extends GraphicTool {

	{createIconSet("icons2/CurveLineIcon.jpg","icons2/LineIconPressed.jpg","icons2/LineIcon.jpg");
	this.realtimeshow=false;
	}
	
	int mode=2;
	boolean samecurveControlAsLast=false;
	private LocatedObject2D lastPath;
	
	{this.onlySelectThoseOfClass=PathGraphic.class;}
	
	
	public void mousePressed() {
		super.mousePressed();
		
		if (getSelectedObject()==null) this.setSelectedObject(lastPath);;
		
		if (this.getSelectedObject()instanceof PathGraphic) {
			
			 PathGraphic p=(PathGraphic) this.getSelectedObject();
			 
			 /**will set i to the index of the pressed handle*/
			 int i=this.getPressedHandle();
			//	while(i>1000) i-=1000;
				
			/**determines if i has a valid index number*/	
			boolean valid =isIndexValid(p.getPoints(),i);
			 
			
			
			if (mode==0&&p!=null) {
				
				//p.setCurvemode(!altKeyDown());
				p.setSupercurvemode(!altKeyDown());
			}
			
			if (mode>0)this.setSelectedHandleNum(i);
			
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
		sd.add("mode", new ComboBoxPanel("Mode", new String[] {"Curve Mode", "Remove point mode", "Add Point mode", "Autocalculate curve control", "Point options mode", "Anchor Point mode", "Curve Points Along Line", "Move All Selected Points"}, mode));
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
