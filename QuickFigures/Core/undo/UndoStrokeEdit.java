package undo;

import utilityClassesForObjects.StrokedItem;

public class UndoStrokeEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float[] iDash;
	private double iMiter;
	private int iCap;
	private int iJoin;
	private float iWidth;
	private StrokedItem item;
	private double fMiter;
	private int fCap;
	private float[] fDash;
	private int fJoin;
	private float fWidth;

	public UndoStrokeEdit(StrokedItem item) {
		this.item=item;
		iDash =   item.getDashes().clone();
		iMiter=   item.getMiterLimit();
		iCap  =   item.getStrokeCap();
		iJoin =   item.getStrokeJoin();
		iWidth=   item.getStrokeWidth();
	}

	
	public void establishFinalState() {
		fDash =   item.getDashes().clone();
		fMiter=   item.getMiterLimit();
		fCap  =   item.getStrokeCap();
		fJoin =   item.getStrokeJoin();
		fWidth=   item.getStrokeWidth();
	}
	
	public void redo() {
		item.setDashes(fDash);
		item.setMiterLimit(fMiter);
		item.setStrokeCap(fCap);
		item.setStrokeJoin(fJoin);
		item.setStrokeWidth(fWidth);
	}
	
	public void undo() {
		item.setDashes(iDash);
		item.setMiterLimit(iMiter);
		item.setStrokeCap(iCap);
		item.setStrokeJoin(iJoin);
		item.setStrokeWidth(iWidth);
	}
	
	
}
