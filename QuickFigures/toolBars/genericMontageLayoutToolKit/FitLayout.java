/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.Icon;

import actionToolbarItems.AlignItem;
import actionToolbarItems.DistributeItems;
import genericMontageKit.BasicObjectListHandler;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.GraphicDisplayComponent;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;
import undo.UndoMoveItems;
import undo.UndoReorder;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdges;

public class FitLayout extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int cleanUp=5;
	
	boolean unique=true;
	int type=0;
	
	public FitLayout(int u) {
		type=u;
	}
	
	public FitLayout(boolean u) {
		unique=u;
	}
	
	@Override
	public String getMenuCommand() {
		if (isClenuptype()) return "Like Grid";
		if (unique) return "Fit Layout (multiple panel sizes)";
		return "Fit Layout";
	}

	private boolean isClenuptype() {
		return cleanUp==type;
	}
	
	@Override
	public String getMenuPath() {
	if(this.isClenuptype())
		return "Align";
	
	return "Align<Create Layout";
	}
	
	/**For certain versions of this operation, layouts are not allowed*/
	ArrayList<LocatedObject2D> getListOfObjectWithoutTheLayouts() {
		ArrayList<LocatedObject2D> layouts = new ArrayList<LocatedObject2D>();
		ArrayList<LocatedObject2D> objects = super.getAllObjects();
		for(LocatedObject2D o:objects ) {
			if(o instanceof PanelLayoutGraphic) {
				layouts.add(o);
			}
		}
		objects.removeAll(layouts);
		return objects;
	}
	
	/**gets the cols*/
	ArrayList<ArrayList<LocatedObject2D>> getCols(ArrayList<LocatedObject2D> objects)  {
		ArrayList<LocatedObject2D> remaining=new ArrayList<LocatedObject2D>();
		ArrayList<ArrayList<LocatedObject2D>> output = new ArrayList<ArrayList<LocatedObject2D>> ();
		remaining.addAll(objects);
		while (remaining.size()>0) {
			ArrayList<LocatedObject2D> row = this.getLeftMostColumn(remaining);
		
			output.add(row);
			remaining.removeAll(row);
		}
		return output;
	}
	
	/**gets the rows*/
	ArrayList<ArrayList<LocatedObject2D>> getRows(ArrayList<LocatedObject2D> objects)  {
		ArrayList<LocatedObject2D> remaining=new ArrayList<LocatedObject2D>();
		ArrayList<ArrayList<LocatedObject2D>> output = new ArrayList<ArrayList<LocatedObject2D>> ();
		remaining.addAll(objects);
		while (remaining.size()>0) {
			ArrayList<LocatedObject2D> row = getTopMostRow(remaining);
		
			output.add(row);
			remaining.removeAll(row);
		}
		return output;
	}

	/**returns the items that are in the left most column*/
	ArrayList<LocatedObject2D> getLeftMostColumn(ArrayList<LocatedObject2D> objects) {
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D> ();
	//	Rectangle bounds =ArrayObjectContainer.combineOutLines(objects).getBounds();		
		/**sort by distance from left edge*/
		Collections.sort(objects, new Comparator<LocatedObject2D>() {

			@Override
			public int compare(LocatedObject2D arg0, LocatedObject2D arg1) {
				return arg0.getBounds().x-arg1.getBounds().x;
			}
			
		});
		
		LocatedObject2D leftMost=null;
		
		
		for(LocatedObject2D l: objects) {
			if (leftMost==null){ leftMost=l; output.add(l); continue;}
			if (l.getBounds().getMinX()<leftMost.getBounds().getMaxX()) output.add(l);
		}
		
		return output;
	}
	
	
	ArrayList<LocatedObject2D> getTopMostRow(ArrayList<LocatedObject2D> objects) {
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D> ();
		
		/**sort by distance from top edge*/
		Collections.sort(objects, new Comparator<LocatedObject2D>() {

			@Override
			public int compare(LocatedObject2D arg0, LocatedObject2D arg1) {
				return arg0.getBounds().y-arg1.getBounds().y;
			}
			
		});
		
		
		LocatedObject2D topMost=objects.get(0);
		
		for(LocatedObject2D l: objects) {
			
			if (topMost==null){ topMost=l; output.add(l);continue;}
			if (l.getBounds().getMinY()<topMost.getBounds().getMaxY()) output.add(l);
		}
		
		return output;
	}
	
	/**Aligns and distributes items to fit a more grid like pattern
	 * @return */
	UndoMoveItems cleanUp(ArrayList<LocatedObject2D> objects, boolean targetPanels) {
		
		UndoMoveItems undo = new UndoMoveItems(objects);
		
		Rectangle bounds =ArrayObjectContainer.combineOutLines(objects).getBounds();		
		
		ArrayList<ArrayList<LocatedObject2D>> rows = this.getRows(objects);
		ArrayList<ArrayList<LocatedObject2D>> cols = this.getCols(objects);
		
		int nrows=rows.size();
		int ncols=cols.size();
		
		
		/**Vertical distribution*/
		DistributeItems dist = new DistributeItems(false);
		
		AlignItem align = new AlignItem(RectangleEdges.TOP);
		
		for(ArrayList<LocatedObject2D> row: rows) {
			if (row.size()>1) align.allignArray(row);//, ArrayObjectContainer.combineOutLines(row).getBounds());
			if (row.size()==ncols &&ncols>1) {
				if (!targetPanels)dist.distributeArray(row, bounds);
				else dist.distributeArray(row);
			}
		}
		
		/**Vertical distribution*/
		dist = new DistributeItems(true);
		AlignItem align2 = new AlignItem(RectangleEdges.LEFT);
		
		
		for(ArrayList<LocatedObject2D> col: cols) {
			if (col.size()>1)align2.allignArray(col);//, ArrayObjectContainer.combineOutLines(col).getBounds());
			if (col.size()==nrows &&nrows>1) {
				if (!targetPanels)dist.distributeArray(col, bounds);
				else dist.distributeArray(col);
			}
		}
		
		undo.establishFinalLocations();
		return undo;
		
	}
	
	/**Aligns and distributes items to fit a more grid like format
	 * @return */
	UndoMoveItems cleanUp2(ArrayList<LocatedObject2D> objects) {
		
		UndoMoveItems undo = new UndoMoveItems(objects);
		
		//Rectangle bounds =ArrayObjectContainer.combineOutLines(objects).getBounds();		
		
		ArrayList<ArrayList<LocatedObject2D>> rows = this.getRows(objects);
		ArrayList<ArrayList<LocatedObject2D>> cols = this.getCols(objects);
		
		int nrows=rows.size();
		int ncols=cols.size();
		
		
		/**Vertical distribution*/
		DistributeItems dist = new DistributeItems(false);
		
		AlignItem align = new AlignItem(RectangleEdges.TOP);
		
		for(ArrayList<LocatedObject2D> row: rows) {
			if (row.size()>1) align.allignArray(row, ArrayObjectContainer.combineOutLines(row).getBounds());
			if (row.size()==ncols &&ncols>1) {
				dist.distributeArray(row);
			}
		}
		
		/**Vertical distribution*/
		dist = new DistributeItems(true);
		AlignItem align2 = new AlignItem(RectangleEdges.LEFT);
		
		
		for(ArrayList<LocatedObject2D> col: cols) {
			if (col.size()>1)align2.allignArray(col, ArrayObjectContainer.combineOutLines(col).getBounds());
			if (col.size()==nrows &&nrows>1) {
				 dist.distributeArray(col);
			}
		}
		
		undo.establishFinalLocations();
		return undo;
		
	}
	
	static MontageLayoutGraphic getOldLayout(GraphicLayer l) {
		for(ZoomableGraphic item: l.getItemArray()) {
			if (item instanceof PanelLayoutGraphic) return (MontageLayoutGraphic) item;
		}
		return null;
	}
	
	@Override
	public void run() {
		
		ArrayList<LocatedObject2D> objects = super.getAllObjects();
		if (isClenuptype()) {
			
			UndoMoveItems undo = cleanUp(objects, fitsLayouts(objects));
			
			this.getUndoManager().addEdit(undo);
			
			return;
		}
		getUndoManager().addEdit(
				fitLayoutToObjects(getListOfObjectWithoutTheLayouts(), true)
		);
	}

	/**if the list is mostly layouts, returns false, otherwise true*/
	private boolean fitsLayouts(ArrayList<LocatedObject2D> objects) {
		double nLayouts=ArraySorter.getNOfClass(objects, PanelLayoutGraphic.class);
		double ratio = nLayouts/objects.size();
		if (ratio>0.9) return true;
		return false;
	}

	public CombinedEdit fitLayoutToObjects(ArrayList<LocatedObject2D> objects, boolean addLayout) {
		CombinedEdit edit = new CombinedEdit();
		edit.addEditToList(cleanUp(objects, false));//makes the positions more grid-like
		UndoMoveItems moveitems = new UndoMoveItems(objects);
		
		Rectangle bounds =ArrayObjectContainer.combineOutLines(objects).getBounds();		
		
		ArrayList<ArrayList<LocatedObject2D>> rows = this.getRows(objects);
		ArrayList<ArrayList<LocatedObject2D>> cols = this.getCols(objects);
		
		int nrows=rows.size();
		int ncols=cols.size();
		
		 LocatedObject2D leftMost = this.getLeftMostColumn(cols.get(0)).get(0);
		 LocatedObject2D secondLeftMost =null;
		 if (cols.size()>1) secondLeftMost =this.getLeftMostColumn(cols.get(1)).get(0);
		
		 LocatedObject2D topMost = this.getTopMostRow(rows.get(0)).get(0);
		 LocatedObject2D secondTopMost =null;
		 if (rows.size()>1)secondTopMost = this.getTopMostRow(rows.get(1)).get(0);
		 
		int width=(int) objects.get(0).getBounds().getWidth();
		int heigth=(int) objects.get(0).getBounds().getHeight();
		int borderh=0;
		int borderv=0; 
		
		if (nrows==1||borderv<0) borderv=0;
		else borderv=(int) (secondTopMost.getBounds().getMinY()-topMost.getBounds().getMaxY());
		
		
		if (ncols==1||borderh<0) borderh=0; 
		else borderh=(int) (secondLeftMost.getBounds().getMinX()-leftMost.getBounds().getMaxX());
		
		
		BasicMontageLayout layout = new BasicMontageLayout(ncols, nrows, width , heigth, borderv, borderh, true);
		
		if (this.unique) {
			int[] rowHeights=new int[nrows] ;
			for(int i=0; i<nrows; i++) {
				Rectangle bounds2 =ArrayObjectContainer.combineOutLines(rows.get(i)).getBounds();		
				rowHeights[i]=bounds2.height;
			}
			
			int[] colWidths=new int[ncols] ;
			for(int i=0; i<ncols; i++) {
				Rectangle bounds2 =ArrayObjectContainer.combineOutLines(cols.get(i)).getBounds();		
				colWidths[i]=bounds2.width;
			}
			
			layout.setIndividualColumnWidths(colWidths);
			layout.setIndividualRowHegihts(rowHeights);
		}
		
		
		MontageLayoutGraphic gra = new MontageLayoutGraphic(layout);
		gra.moveLocation(bounds.x, bounds.y);
		//gra.setAlwaysShow(true); gra.setUserLocked(0);
		
		/**determines the appropriate layer*/
		LocatedObject2D item = rows.get(0).get(0);
		GraphicLayer layer = selector.getSelectedLayer();
		if (item instanceof KnowsParentLayer) {
			KnowsParentLayer k=(KnowsParentLayer) item;
			layer=k.getParentLayer();
			if (layer instanceof MultichannelDisplayLayer) {
				layer=layer.getParentLayer();
			}
		}
		
		gra.getPanelLayout().resetPtsPanels();
		
		ZoomableGraphic oldLayout = getOldLayout(layer);
		if(!addLayout) oldLayout = null;
		UndoReorder undo3;
		if (oldLayout!=gra)
			{
				if (oldLayout instanceof MontageLayoutGraphic) {
					((MontageLayoutGraphic) oldLayout).getPanelLayout().setToMatch(layout);
					gra=(MontageLayoutGraphic) oldLayout;
				} else {
					UndoAddItem undo4 = new UndoAddItem(layer, gra);
					layer.add(gra);;
					edit.addEditToList(undo4);
					
					}
				undo3 = new UndoReorder(layer);
				layer.swapmoveObjectPositionsInArray(gra, layer.getItemArray().get(0));
				undo3.saveNewOrder(); edit.addEditToList(undo3);
			}
		
		
		 placeObjectsInUpperLeftCorners(gra.getPanelLayout(), objects);
		
		gra.generateCurrentImageWrapper();
		
		//gra.getEditor().shiftPanelContentsToEdge(gra.getPanelLayout());
		UndoLayoutEdit undo5 = new UndoLayoutEdit(gra.getPanelLayout());
		gra.getEditor().fitLabelSpacesToContents(gra.getPanelLayout());
		undo5.establishFinalLocations(); edit.addEditToList(undo5);
		
		moveitems.establishFinalLocations();
		edit.addEditToList(moveitems);
		
		gra.select();
		gra.updateDisplay();
		if(!addLayout) gra.getParentLayer().remove(gra);
		return edit;
	}
	

	/**puts each panel object in the upper left corner. If multiple objects are in that panel,
	  only moves teh first one*/
	public static void placeObjectsInUpperLeftCorners(BasicMontageLayout gra, ArrayList<LocatedObject2D> objects ) {
		ArrayList<LocatedObject2D> objects2=new  ArrayList<LocatedObject2D>();
		objects2.addAll(objects);
		
		/**puts each panel in the upper left corner*/
		for(Rectangle2D r: gra.getPanels()) {
			ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(r, new ArrayObjectContainer(objects2));
			if (items.size()>0) items.get(0).setLocationUpperLeft(r.getX(), r.getY());
			objects2.removeAll(items);
		}
	}
	

	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectanglesForIcon();
		Color[] colors=new Color[] {Color.red, Color.green, Color.blue,Color.orange, Color.cyan, Color.magenta, new Color((float)0.0,(float)0.0,(float)0.0, (float)0.5)};
		if (type!=cleanUp) {
			colors=new Color[] {Color.red.darker(), Color.red.darker(), Color.red.darker(),Color.magenta.darker(), Color.magenta.darker(), Color.magenta.darker(), Color.blue.darker(), new Color((float)0.0,(float)0.0,(float)0.0, (float)0.5)};
			
		}
		for(int i=0; i<rects.size(); i++ ) {
			Rectangle r=rects.get(i);
			
			RectangularGraphic rect = RectangularGraphic.blankRect(r, colors[i]);
			rect.setStrokeWidth(1);
			gg.getTheLayer().add(rect);
				}
		
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	
	private ArrayList<Rectangle> getRectanglesForIcon() {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		
				output.add(new Rectangle(0,2,4,4));
				output.add(new Rectangle(6,2,4,4));
				output.add(new Rectangle(12,2, 4,4));
				output.add(new Rectangle(0, 8,4,4));
				output.add(new Rectangle(6,8,4,4));
				output.add(new Rectangle(12,8,4,4));
		
				if(type!=cleanUp) {
					output.add(
							new Rectangle(0,0,18,15)
							);
				}
		return output;
	}
	
	
	public Icon getIcon() {
		return  getItemIcon(true);
	}


}
