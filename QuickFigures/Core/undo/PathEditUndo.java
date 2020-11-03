package undo;

import graphicalObjects_BasicShapes.PathGraphic;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class PathEditUndo extends AbstractUndoableEdit2 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PathPoint point =null;
	PathGraphic graph=null;
	private PathPointList points;
	private PathPointList pointsAfterEdit;
	
	boolean iClosed=false;
	boolean fClosed=false;
	
	  public boolean isMyObject(Object o) {
		  if (o==graph) return true;
		  if (o==point) return true;
		  return false;
	  }
		
	
	public PathEditUndo(PathGraphic gra) {
		if (gra==null||gra.getPoints()==null) return;
		this.points=gra.getPoints().copy();
		iClosed=gra.isClosedShape();
		graph=gra;
	}
	
	public void establishFinalState() {saveFinalPositions();}
	
	public void saveFinalPositions() {
	pointsAfterEdit = graph.getPoints().copy();
		fClosed=graph.isClosedShape();
	}

	
	public void undo() {
		graph.setPoints(points);
		graph.setClosedShape(iClosed);
		graph.updatePathFromPoints();
		graph.updateDisplay();
	}
	
	public void redo() {
		graph.setPoints(pointsAfterEdit);
		graph.setClosedShape(fClosed);
		graph.updatePathFromPoints();
		graph.updateDisplay();
	}
	
	
}