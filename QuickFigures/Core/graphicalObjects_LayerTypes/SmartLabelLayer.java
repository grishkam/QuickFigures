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
 * Date Created: Oct 24, 2021
 * Date Modified: October 25, 2021
 * Version: 2021.2
 */
package graphicalObjects_LayerTypes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;

import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import figureOrganizer.PanelListElement;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.SmartLabelLayerDialog;
import popupMenusForComplexObjects.DonatesMenu;
import textObjectProperties.SmartLabelDataType;
import textObjectProperties.TextPattern;
import undo.CombinedEdit;
import undo.SimpleItemUndo;
import undo.SimpleTraits;
import undo.UndoAddItem;
import undo.UndoAddOrRemoveAttachedItem;
import utilityClasses1.ArraySorter;

/**
 Soemtimes a user wants an shape displayed over a few different parent panels
 but at the equivalent location in each panel.
 A special layer that contains items whose location is determined by a parent item
 Whenever a user changes the location of the parent item, the reflections will be updated.
 
 
 */
public class SmartLabelLayer extends GraphicLayerPane implements  HasUniquePopupMenu, DonatesMenu, SimpleTraits<SmartLabelLayer>{

	/**the list of which text items correspond to which panel*/
	private HashMap<ZoomableGraphic, TextGraphic> records=new  HashMap<ZoomableGraphic, TextGraphic>();

	/**the pattern that the labels take*/
	TextPattern textPattern=new  TextPattern(TextPattern.PatternType.ABC);
	
	
	
	boolean continuouseUpdate=true;
	
	
	/**
	 * @param name
	 */
	public SmartLabelLayer() {
		super("Label Sequence");
	}
	
	
	/**creates a single duplicate*/
	public SmartLabelLayer copy() {
		SmartLabelLayer output = new SmartLabelLayer();
		giveTraitsTo(output);
		
		return output;
	}


	/**
	 * @param output
	 */
	public void giveTraitsTo(SmartLabelLayer output) {
		output.continuouseUpdate=this.continuouseUpdate;
		output.textPattern=this.textPattern.copy();
	}
	
	public void addLabel(TextGraphic label, ZoomableGraphic parent) {
		records.put(parent, label);
		label.setUserEditable(false);
	}
	
	/**updates the labels to match the order of the panels.
	 * for example, labels might be set to A, B and C based on how panels are located right to left*/
	public void updateLabels() {
		ArrayList<ZoomableGraphic> anchorPanels=new ArrayList<ZoomableGraphic>();
		 anchorPanels.addAll(records.keySet());
		 
		 updatePanelLabelsBasedonOrder(anchorPanels);

		 
		 
	}


	/**updates the labels to be in a sequence matching the physical order of the image panels
	 * @param anchorPanels
	 */
	protected void updatePanelLabelsBasedonOrder(ArrayList<ZoomableGraphic> anchorPanels) {
		ArrayList<ZoomableGraphic> order = getInRowMajorOrder(anchorPanels);
		 
		 for(ZoomableGraphic panel: anchorPanels) {
			if(this.hasItem(records.get(panel)))
				continue;
			order.remove(panel);//if the text item is not longer stored in this layer, then it should not be considered
		 }
		 
		 for(int i=1; i<=order.size(); i++) {
			 TextGraphic textItem = records.get(order.get(i-1));//finds the text item for this round of the loop
			 int n = SmartLabelLayer.getIndexNumber(getTextPattern().getCurrentIndexSystem(), order, i);
			 String text=getTextPattern().getText(n);//update of the symbol
			 text = insertTandZintoNames(order, this.getTextPattern(), i, text);
			 
			 textItem.setText(text);
		 }
	}

	


	/** Adds time frame and slice index to the label and returns the new label
	 * @param order
	 * @param i
	 * @param text
	 * @return
	 */
	protected String insertTandZintoNames(ArrayList<ZoomableGraphic> order, TextPattern pattern, int i, String text) {
		if(order.get(i-1) instanceof ImagePanelGraphic) {
			 ImagePanelGraphic image=(ImagePanelGraphic) order.get(i-1); 
			 PanelListElement panel = image.getSourcePanel();
			 if(panel!=null) {
				 for(SmartLabelDataType code: SmartLabelDataType.values())
					 text=text.replace(code.getCode(),   pattern.getSymbol(getIndexNumber(code, order, i))+"");
			 }
		 }
		return text;
	}
	
	/**returns the number index of the panel (depends on the 
	 * @param theCode which type of number should be used
	 * ) */
	public static int getIndexNumber(SmartLabelDataType theCode, ArrayList<ZoomableGraphic> order, int i) {
			if (SmartLabelDataType.LOCATION_IN_FIGURE==theCode)
				return i;
			
			if(order.get(i-1) instanceof ImagePanelGraphic) {
				 ImagePanelGraphic image=(ImagePanelGraphic) order.get(i-1); 
				 Integer dataFromPanel = getDataFromPanel(theCode, image);
				 if(dataFromPanel!=null)
					 return dataFromPanel;
			 }
			return i;
	}


	/**returns a specific type of information about the panel
	 * @param theCode
	 * @param image
	 */
	public static Integer getDataFromPanel(SmartLabelDataType theCode, ImagePanelGraphic image) {
		PanelListElement panel = image.getSourcePanel();
		 if(panel!=null) {
			if(theCode==SmartLabelDataType.TIME)
				   return panel.targetFrameNumber;
			if(theCode==SmartLabelDataType.SLICE)
				   return panel.targetSliceNumber;
			if(theCode==SmartLabelDataType.CHANNEL)
				   return panel.targetChannelNumber;
			
		 }
		 return null;
	}
	
	
	/**updates the labels and draws the layer*/
	public void draw(Graphics2D graphics, CordinateConverter cords) { 
		if(this.isContinuouseUpdate())
			this.updateLabels();
		super.draw(graphics, cords);
	}





	
	
	
	/**when given a list of of objects, returns a version of the list
	  with the order that makes sense for parts ABCD of a scientific figure
	  algorithm is probably too comnplex. It scans the figure like a reader
	  of english scans a page and adds panels to the output list as it comes accross them */
	public static ArrayList<ZoomableGraphic> getInRowMajorOrder(ArrayList<ZoomableGraphic> inputObjects) {
		ArrayList<ZoomableGraphic> usable = new ArraySorter<ZoomableGraphic>().getThoseOfClass(inputObjects, LocatedObject2D.class);
		
		ArrayList<ZoomableGraphic> output=new ArrayList<ZoomableGraphic>();
		
		if(usable.size()<2)
			return usable;//sorting makes no sense for less than 2 objects
		
		int lowestX=Integer.MAX_VALUE;
		int lowestY=Integer.MAX_VALUE;
		int lowestWidth=Integer.MAX_VALUE;
		int lowestHeight=Integer.MAX_VALUE;
		int greatestX=0;
		int greatestY=0;
		
		for(ZoomableGraphic target: usable) {
			LocatedObject2D target2=(LocatedObject2D) target;
			Rectangle bounds = target2.getBounds();
			if(bounds.x<lowestX)
				lowestX=bounds.x;
			if(bounds.y<lowestY)
				lowestY=bounds.y;
			if(bounds.x+bounds.width>greatestX)
				greatestX=bounds.x+bounds.width;
			if(bounds.y+bounds.height>greatestY)
				greatestY=bounds.y+bounds.height;
			
			if(bounds.width<lowestWidth)
				lowestWidth=bounds.width;
			if(bounds.height<lowestHeight)
				lowestHeight=bounds.height;
		}
	
		
		int widthIncrement = lowestWidth/4;
		int heightIncrement =lowestHeight/4;
		
		/**the loops will be useless if there is progression*/
		if(widthIncrement<1||heightIncrement<1) {
			IssueLog.log("sort fail 1");
			return inputObjects;
			}
		
		/**the loops will be useless if there is no logical end point*/
		if(greatestX==0||greatestY==0) {
			IssueLog.log("sort fail 2");
			return inputObjects;
			}
		
		/**the loops will be useless if there is no logical start point*/
		if(lowestX==Integer.MAX_VALUE||lowestY==Integer.MAX_VALUE)
			{
			IssueLog.log("sort fail 3");
			return inputObjects;
			}
		
		int y=lowestY;
		int x=lowestX;
		int cycle=00;
		
		Rectangle scanArea=new Rectangle(lowestX, lowestY,widthIncrement ,heightIncrement  );
		
		while (y<greatestY){
			while(x<greatestX) {
				cycle++;
				if(cycle>100000)
					break;//to prevent infinite loops
				
				scanArea=new Rectangle(x, y,widthIncrement ,heightIncrement  );
				
				for(ZoomableGraphic l: usable) {
					LocatedObject2D locatedObject=(LocatedObject2D) l;
					if(output.contains(l)) 
						continue;//an object need not be added to the output twice.
					if (locatedObject.getBounds().intersects(scanArea))
						output.add(l);
				}
				
				
				x+=widthIncrement;
			}
			
			x=lowestX;
			y+=heightIncrement;
		}
		
		
	
		return output;
	}

	public TextPattern getTextPattern() {
		return textPattern;
	}

	public void setTextPattern(TextPattern textPattern) {
		this.textPattern = textPattern;
	}

	
	/**creates a menu for this item*/
	public PopupMenuSupplier getMenuSupplier() {
		
		return new MenuItemExecuter(this);
	}

	/**returns the mirror submenu if the argument is one of the reflected objects*/
	@Override
	public JMenu getDonatedMenuFor(Object requestor) {
		if (hasItem((ZoomableGraphic) requestor)) {
			JMenu jMenu = new MenuItemExecuter(this).getJMenu();
			jMenu.setText("Smart Label");
			return jMenu;
		}
		
		return null;
	}
	
	
	
	@MenuItemMethod(menuActionCommand = "Constantly Update Labels?", menuText ="Constantly Update Labels", orderRank=9, iconMethod="isContinuouseUpdate")
	public void turnUpdateOnOff() {
		this.setContinuouseUpdate(!isContinuouseUpdate());
	}

	@MenuItemMethod(menuActionCommand = "Select All", menuText ="Select All", orderRank=10)
	public void selectAll() {
		for(TextGraphic i:records.values()) {
			i.select();
		}
	}
	
	/**Shows a modal options dialog for the item, and returns an undoable edit*/
	@MenuItemMethod(menuActionCommand = "options", menuText ="Show Options", orderRank=4)
	public SimpleItemUndo<SmartLabelLayer> showOptions() {
		SimpleItemUndo<SmartLabelLayer> undo = new SimpleItemUndo<SmartLabelLayer>(this);
		new SmartLabelLayerDialog(this, true).showDialog();
		undo.establishFinalState();
		return undo;
	}
	
	
	/**adds labels for the selected panels (or selected layouts)
	 * @return */
	@MenuItemMethod(menuActionCommand = "selected", menuText ="Add Labels To Selected Objects", orderRank=5)
	public CombinedEdit addLabelsToSelected() {
		CombinedEdit e=new CombinedEdit();
		
		GraphicLayer topLevelParentLayer = this.getTopLevelParentLayer();
		ArrayList<ZoomableGraphic> items = topLevelParentLayer.getAllGraphics();
		ArraySorter.removeNonSelectionItems(items);
		
		for(ZoomableGraphic item: items) {
			if(records.keySet().contains(item)&&topLevelParentLayer.hasItem(records.get(item)))
				continue;//if the item is already being tracked and already has a label in the figure
			if(item instanceof TakesAttachedItems) {
				TextGraphic example=null; for(TextGraphic text:records.values()) {example=text; if(example!=null)break;}
				TextGraphic newLabel = example.copy();
				newLabel.setAttachmentPosition(example.getAttachmentPosition());
				TakesAttachedItems itemTaker=(TakesAttachedItems) item;
				this.addLabel(newLabel, item);
				this.addItemToLayer(newLabel);
				itemTaker.addLockedItem(newLabel);
				
				e.addEditToList(
						new UndoAddItem(this, newLabel));
				e.addEditToList(
						new  UndoAddOrRemoveAttachedItem(itemTaker, newLabel, false)
						);
				
			}
		}
		this.updateLabels();
		return e;
	}


	public boolean isContinuouseUpdate() {
		return continuouseUpdate;
	}

	public void setContinuouseUpdate(boolean continuouseUpdate) {
		this.continuouseUpdate = continuouseUpdate;
	}
	
	
	
	@Override
	public SmartLabelLayer self() {
		return this;
	}


	private static final long serialVersionUID = 1L;
	

	
	


}
