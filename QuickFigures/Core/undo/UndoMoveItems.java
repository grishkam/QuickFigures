package undo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.undo.AbstractUndoableEdit;

import animations.GroupsTranslationAnimation;
import animations.HasAnimation;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import utilityClassesForObjects.LocatedObject2D;
import animations.Animation;

/**An undoable edit for moving objects*/
public class UndoMoveItems extends AbstractUndoableEdit implements HasAnimation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<LocatedObject2D> list;
	HashMap<LocatedObject2D, Point2D> originalLocal=new HashMap<LocatedObject2D, Point2D>();
	HashMap<LocatedObject2D, Point2D> finalLocal=new HashMap<LocatedObject2D, Point2D>();
	
	public UndoMoveItems(LocatedObject2D... list2){
		this.list=new ArrayList<LocatedObject2D> ();
		for(LocatedObject2D l: list2) {list.add(l);}
		establishOriginalLocations();
	}
	
	public UndoMoveItems(ArrayList<LocatedObject2D> list, boolean layoutsInMind) {
		this.list=new ArrayList<LocatedObject2D> ();
		this.list.addAll(list);
		
		for(LocatedObject2D l: list) {
			if (l instanceof PanelLayoutGraphic) {
				((PanelLayoutGraphic) l).generateCurrentImageWrapper();
				ArrayList<LocatedObject2D> addons = ((PanelLayoutGraphic) l).getPanelLayout().getWrapper().getLocatedObjects();
				this.list.addAll(addons);
			}
		}
		
		establishOriginalLocations();
	}
	
	public UndoMoveItems(ArrayList<LocatedObject2D> list) {
		this(list, false);
	}


	private void establishOriginalLocations() {
		for(LocatedObject2D l: list) {
			Point2D point = l.getLocationUpperLeft();
			
			originalLocal.put(l, point);
		}
		
	}
	

	public void establishFinalLocations() {
		for(LocatedObject2D l: list) {
			Point2D point = l.getLocationUpperLeft();
			finalLocal.put(l, point);
		}
		
	}
	
	public void establishFinalState() {
		establishFinalLocations();
	}
	
	public void undo() {
		for(LocatedObject2D l: list) {
			
			
			Point2D pt = originalLocal.get(l);
			Point2D ptfinal = l.getLocationUpperLeft();
			double dx = pt.getX()-ptfinal.getX();
			double dy = pt.getY()-ptfinal.getY();
			l.moveLocation(dx, dy);
		}
	}
	
	public void redo() {
		for(LocatedObject2D l: list) {
			Point2D pt = finalLocal.get(l);
			Point2D ptfinal = l.getLocationUpperLeft();
			double dx = pt.getX()-ptfinal.getX();
			double dy = pt.getY()-ptfinal.getY();
			l.moveLocation(dx, dy);
		}
	}
	
	public boolean canRedo() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new GroupsTranslationAnimation( finalLocal, originalLocal);

	}

}
