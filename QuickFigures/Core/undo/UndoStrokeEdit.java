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
 * Date Modified: Nov 27, 2021
 * Version: 2023.1
 */
package undo;

import locatedObject.StrokedItem;

/**an undo for changes to the stroke properties of a shape object
 * except for the stroke color*/
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
		if(item.getDashes()!=null)
		iDash =   item.getDashes().clone();
		
		iMiter=   item.getMiterLimit();
		iCap  =   item.getStrokeCap();
		iJoin =   item.getStrokeJoin();
		iWidth=   item.getStrokeWidth();
	}

	
	public void establishFinalState() {
		if(item.getDashes()!=null)
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
