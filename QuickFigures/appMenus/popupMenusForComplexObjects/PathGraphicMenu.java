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
 * Date Modified: Dec 4, 2021
 * Version: 2021.2
 */
package popupMenusForComplexObjects;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import menuUtil.SmartPopupJMenu;
import objectCartoon.BasicShapeMaker;
import objectDialogs.ArrowSwingDialog;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import menuUtil.SmartJMenu;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import sUnsortedDialogs.AffineTransformDialog;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoAbleEditForRemoveItem;
import undo.PathEditUndo;

/**a popup meny for a path*/
public class PathGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String OPTIONS_DIALOG="Options", MOVE_ENDPOINT="Move Endpoint", ROTATE="Rotate", SCALE="Scale", 
			VERTICAL_FLIP="Flip Vertical", HORIZONTAL_FLIP="Flip Horizontal", SPLIT_LOOSE_PARTS="Split Up Parts", 
			SWITCH_HANDLE_MODES="Switch Handle Modes", UNCURVED_MIMIC="Make Uncurved Copy";//, backGroundShap="Outline Shape";
	static final String CROSSING_MARKS="Show Crossing Path Lines", NORMAL_MARKS="show vectors",
			ELIMINATE_UNNEEDED_POINTS="Eliminate extra points", SMOOTH="Smooth", ADD_POINT="Add Point";
	;private static final String ADD_ARROW_HEAD_1="Arrow Head At Start", ADD_ARROW_HEAD_2="Arrow Head At End";
	protected static final String BREAK_ARROWS_OFF="Detach arrow heads";
	
	
	PathGraphic pathForMenuG;
	
	
	HashMap<String, SmartJMenu> subMenus=new HashMap<String, SmartJMenu> ();
	private SmartJMenu getSubMenu(String st) {
		SmartJMenu out = subMenus.get(st);
		if(out==null) {
			out=new SmartJMenu(st);
			subMenus.put(st, out);
			this.add(out);
		}
		
		return out;
	}
	
	
	public PathGraphicMenu(PathGraphic textG) {
		super();
		this.pathForMenuG = textG;
		addAllMenuItems();
	}


	/**Adds every meny item to the menu
	 * @param textG
	 */
	public void addAllMenuItems() {
		
		this.addAllMenuItems(new ShapeGraphicMenu(pathForMenuG).createMenuItems());
		
		 addItem(ADD_POINT);
		
		
		 String tFormMenu = "Transform";
		 
		addItem(tFormMenu, ROTATE);
		 addItem(tFormMenu, SCALE);
		 
		 
		 addItem(tFormMenu, HORIZONTAL_FLIP);
		 addItem(tFormMenu, VERTICAL_FLIP);
		 
		
		
		// addItem(addNormIndicators);
		 addItem(SWITCH_HANDLE_MODES);
		String subMenuName = "Expert options";
		
		 addItem(tFormMenu, UNCURVED_MIMIC);
		addItem(tFormMenu,SMOOTH);
		addItem(tFormMenu,SPLIT_LOOSE_PARTS);
		addItem(subMenuName, getAddArrowHead2MenuCommand());
		 addItem(subMenuName, getAddArrowHead1MenuCommand());
		 addItem(subMenuName, ELIMINATE_UNNEEDED_POINTS);
		 addItem(subMenuName, MOVE_ENDPOINT);
		 addItem(subMenuName, BREAK_ARROWS_OFF);
	}
	
	
	public void addItem(String subMenuName, String st) {
		this.getSubMenu(subMenuName).add(createItem(st));
	}
	
	public void addItem(String st) {
		add(createItem(st));
	}
	
	public JMenuItem createItem(String st) {
		JMenuItem o=new BasicSmartMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		
		AbstractUndoableEdit2 undo = new PathEditUndo(pathForMenuG);
		
		if (com.equals(OPTIONS_DIALOG)) {
			pathForMenuG.showOptionsDialog();
		}
		if (com.equals(MOVE_ENDPOINT)) {
			pathForMenuG.getPoints().moveEnd();
		}
		if (com.equals(ROTATE)) {
			Double angle = StandardDialog.getNumberFromUser("input angle", 0, true);
			if(angle!=null)
				pathForMenuG.rotateAbout(pathForMenuG.getCenterOfRotation(), -angle);
			//AffineTransform showRotation = AffineTransformDialog.showRotation(0, pathForMenuG.getCenterOfRotation());
			//pathForMenuG.getPoints().applyAffine(showRotation);
		}
		
		if (com.equals(SCALE)) {
			AffineTransform scaleTransform = AffineTransformDialog.showScale(new Point(1,1));
			
			pathForMenuG.scaleAbout( pathForMenuG.getCenterOfRotation(), scaleTransform.getScaleX(), scaleTransform.getScaleY());
		}
		

		
		if (com.equals(SPLIT_LOOSE_PARTS)) {
			PathIterator pi = pathForMenuG.getPath().getPathIterator(new AffineTransform());
			
		/**PathGraphic textG2 = (PathGraphic) textG.copy();
		textG.getParentLayer().add(textG2);*/
		
		CombinedEdit undo2 = new CombinedEdit();
		
			ArrayList<PathPointList> arrayOfSec = PathPointList.createFromIterator(pi).createAtCloseSubsections();
			for(PathPointList arr: arrayOfSec) {
				if (arr==null ) continue;
				PathGraphic textG2 = (PathGraphic) pathForMenuG.copy();
				undo2.addEditToList(Edit.addItem(pathForMenuG.getParentLayer(), textG2));
				
				
				textG2.setPoints(arr);
				
			}
			undo2.addEditToList(new UndoAbleEditForRemoveItem(pathForMenuG.getParentLayer(), pathForMenuG));
			pathForMenuG.getParentLayer().remove(pathForMenuG);
			undo=undo2;
			
			;
		}
		
		if (com.equals(UNCURVED_MIMIC)) {
			PathGraphic newpath = pathForMenuG.break10(20);
		
			newpath.select();
			undo=Edit.addItem(pathForMenuG.getParentLayer(), newpath);

		}
		
		ArrowGraphic h1 = pathForMenuG.getArrowHead1();
		ArrowGraphic h2 = pathForMenuG.getArrowHead2();
		if (com.equals(BREAK_ARROWS_OFF)) {
			
			GraphicGroup newpath = pathForMenuG.createCopyWithDetachedHeads();
			newpath.select();
			
			AbstractUndoableEdit2 undo2 = Edit.addItem(pathForMenuG.getParentLayer(), newpath);
			AbstractUndoableEdit2 undo3 = Edit.removeItem(pathForMenuG.getParentLayer(),pathForMenuG );
			undo=new CombinedEdit(undo2, undo3);

		}
		
		if (com.equals(HORIZONTAL_FLIP)) {
			pathForMenuG.getPoints().applyAffine(BasicShapeMaker.createHFlip(pathForMenuG.getPoints().createPath(true).getBounds().getCenterX()));
		}
		
		if (com.equals(VERTICAL_FLIP)) {
			pathForMenuG.getPoints().applyAffine(BasicShapeMaker.createVFlip(pathForMenuG.getPoints().createPath(true).getBounds().getCenterY()));
		}
		if (com.equals(SWITCH_HANDLE_MODES)) {
			if (pathForMenuG.getHandleMode()!=PathGraphic.ANCHOR_HANDLE_ONLY_MODE) {
				
				pathForMenuG.setHandleMode(PathGraphic.ANCHOR_HANDLE_ONLY_MODE);
					} 
				else pathForMenuG.setHandleMode(PathGraphic.THREE_HANDLE_MODE);
		}
		
		if (com.equals(CROSSING_MARKS)) {pathForMenuG.setUseArea(!pathForMenuG.isUseArea());}
		
		if (com.equals(ELIMINATE_UNNEEDED_POINTS)) {
			pathForMenuG.getPoints().cullUselessPoints(0.98, false, 2, true);
		}
		
		if (com.equals(SMOOTH)) {
			for(PathPoint l:pathForMenuG.getPoints() ) {
				l.evenOutAngleOfCurveControls(0.5);
				pathForMenuG.updatePathFromPoints();
			}
			
		}
		
		if (com.equals(NORMAL_MARKS)) {
			ArrayList<Point2D[]> vectors = pathForMenuG.getPoints().getTangentVectors();//.getDiffVectors();
			 pathForMenuG.getPoints().getMidpionts(0.5);
			for(int it=0; it<vectors.size(); it++) {
				PathGraphic pt = PathGraphic.blackLine(vectors.get(it));pt.setStrokeWidth(2);
	
				pt.moveLocation(pathForMenuG.getLocation().getX(), pathForMenuG.getLocation().getY());
				pathForMenuG.getParentLayer().add(pt);
			}
		}
		
		if (com.equals(ADD_POINT)) {
			new AddRemoveAnchorPointTool(false).addOrRemovePointAtLocation(pathForMenuG, false, super.getMemoryOfMouseEvent().getCoordinatePoint());
		}
		
		if (com.equals(getAddArrowHead1MenuCommand())) {
			if (!pathForMenuG.hasArrowHead1())pathForMenuG.addArrowHeads(1);
			else if (h1!=null)
				new ArrowSwingDialog(h1, 1).showDialog();;
		}
				if (com.equals(getAddArrowHead2MenuCommand())) {
				if (!pathForMenuG.hasArrowHead2())pathForMenuG.addArrowHeads(2);
				else if (h2!=null)
					new ArrowSwingDialog(h2, 1).showDialog();;
					
		}
		
		pathForMenuG.updatePathFromPoints();
		undo.establishFinalState();
		if (getUndoManager()!=null) getUndoManager().addEdit(undo);
		pathForMenuG.updateDisplay();
	}


	public String getAddArrowHead1MenuCommand() {
		if(!pathForMenuG.hasArrowHead1()) return "Add Arrow Head To Start";
		return ADD_ARROW_HEAD_1;
	}


	public  String getAddArrowHead2MenuCommand() {
		if(!pathForMenuG.hasArrowHead2()) return "Add Arrow Head To End";
		return ADD_ARROW_HEAD_2;
	}
	
	
	
}
	


