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
 * Date Modified: Jan 7, 2021
 * Version: 2021.2
 */
package undoForPlots;

import plotParts.DataShowingParts.Boxplot;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.ScatterPoints;
import undo.AbstractUndoableEdit2;

/**An undoable edit for changes to the traits of the shape objects 
 * that dipict ploted data*/
public class DataShapeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private DataShowingShape item;
	private static final long serialVersionUID = 1L;
	private ErrorBarShowingShape eI;
	private ErrorBarShowingShape eF;
	private DataBarShape bI;
	private DataBarShape bF;
	private Boxplot boxI;
	private Boxplot boxF;
	private ScatterPoints sI;
	private ScatterPoints sF;

	
	public DataShapeUndo(DataShowingShape item) {
		this.item=item;
		if (item instanceof ErrorBarShowingShape) {
			 eI=(ErrorBarShowingShape) item.copy();
		}
		if (item instanceof DataBarShape) {
			 bI=(DataBarShape) item.copy();
		}
		if (item instanceof Boxplot) {
			 boxI=(Boxplot) item.copy();
		}
		if (item instanceof ScatterPoints) {
			 sI=(ScatterPoints) item.copy();
		}
	}
	
	public void establishFinalState() {
		if (item==null) return;
		if (item instanceof ErrorBarShowingShape) {
			 eF=(ErrorBarShowingShape) item.copy();
		}
		if (item instanceof DataBarShape) {
			 bF=(DataBarShape) item.copy();
		}
		if (item instanceof Boxplot) {
			 boxF=(Boxplot) item.copy();
		}
		if (item instanceof ScatterPoints) {
			 sF=(ScatterPoints) item.copy();
		}
		item.requestShapeUpdate();
	}
	
	public void redo() {
		if (item instanceof  ErrorBarShowingShape) {
			 ErrorBarShowingShape item2=(ErrorBarShowingShape) item;
			item2.copyEverythingFrom(eF);
		}
		if (item instanceof  DataBarShape) {
			DataBarShape item2=(DataBarShape) item;
			item2.copyEveryThingFrom(bF);
		}
		if (item instanceof Boxplot) {
			Boxplot box2 = (Boxplot) item;
			 box2.copyEverythingFrom(boxF);
		}
		if (item instanceof ScatterPoints) {
			ScatterPoints box2 = (ScatterPoints) item;
			 box2.copyEverythingFrom(sF);
		}
		item.requestShapeUpdate();
	}
	
	public void undo() {
		if (item instanceof  ErrorBarShowingShape) {
			 ErrorBarShowingShape item2=(ErrorBarShowingShape) item;
			item2.copyEverythingFrom(eI);
		}
		if (item instanceof  DataBarShape) {
			DataBarShape item2=(DataBarShape) item;
			item2.copyEveryThingFrom(bI);
		}
		if (item instanceof Boxplot) {
			Boxplot box2 = (Boxplot) item;
			 box2.copyEverythingFrom(boxI);
		}
		if (item instanceof ScatterPoints) {
			ScatterPoints box2 = (ScatterPoints) item;
			 box2.copyEverythingFrom(sI);
		}
	}
}
