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
 * Date Modified: Oct 24, 2021
 * Version: 2021.2
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;

import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.SmartLabelLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.TextPatternDialog;
import textObjectProperties.TextPattern;
import undo.CombinedEdit;
import undo.UndoAddItem;
import utilityClasses1.ArraySorter;

/**Adds text objects to selected image panels*/
class TextItemAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean simple=false;
	
	TextPattern pattern=new TextPattern();
	boolean addSmartLabels=false;
	SmartLabelLayer smartLayer =null;
	
	String[] addingClasses=new String[] {"Panels and Layouts", "Panels only", "Layouts only"};
	Class<?>[] addingClasses2= new Class<?>[] {ZoomableGraphic.class, ImagePanelGraphic.class, PanelLayoutGraphic.class};
	
	
	
	
	/**constructor for  text item adder. */
	public TextItemAdder(boolean isSimple) {
		simple=isSimple;
		pattern.setPrefix("Panel ");
	}

	
	
	

	/**constructor for  text item adder. */
	public TextItemAdder(boolean isSimple, TextPattern p, String prefix, boolean smart) {
		this(isSimple);
		this.pattern=p;
		pattern.setPrefix(prefix);
		addSmartLabels=smart;
		
	}
	
	
	
	private CombinedEdit undo;
	
	/**A text item to show as an icon. along with the menu item*/
	TextGraphic iconText=new TextGraphic();
	 {
		iconText.setFont(iconText.getFont().deriveFont((float) 42));	
		iconText.setFillBackGround(true);
		iconText.getBackGroundShape().copyAttributesFrom(RectangularGraphic.blankRect(new Rectangle(),iconText.getTextColor()));
		iconText.getBackGroundShape().setStrokeColor(iconText.getTextColor());
		iconText.setText("text");
		iconText.setLocationUpperLeft(0, 0);
		
	}
	
	 /**Adds one or more text items, will add one to each selected image panel if possible
	   if not, just adds one text item to the figure*/
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		TextGraphic out = new TextGraphic();
		if(!simple) out=new ComplexTextGraphic();
		
		TextPattern input=this.pattern;
		
		this.pattern=TextPatternDialog.getPatternFromUser(input);
		
		out.setLocationUpperLeft(50, 50);
		
		if(addSmartLabels) {
			 smartLayer = new SmartLabelLayer();
			 smartLayer.setTextPattern(this.pattern);
			gc.getTopLevelParentLayer().add(smartLayer);
		}
		else 
			smartLayer =null;
		
		ArrayList<TextGraphic> list = addLockedItemToSelectedImages(out);
		if (list.size()<1
				) {
				gc.add(out);
				out.showOptionsDialog();
		}
		
		return out;
		
	}

	/**Adds many copies of the text item to the selected images. Attaches each text to 
	  an image panel. font size is decreased if panels are too small*/
	public ArrayList<TextGraphic> addLockedItemToSelectedImages(TextGraphic ag) {
		undo=new CombinedEdit();
		ArrayList<ZoomableGraphic> possibleTargets =  SmartLabelLayer.getInRowMajorOrder(getSelectedItems());
		
		Class<?> targetClass = ZoomableGraphic.class;
		
		if(ArraySorter.getNOfClass(possibleTargets, addingClasses2[1])>0 && ArraySorter.getNOfClass(possibleTargets, addingClasses2[2])>0)
			{
			ShowMessage.showOptionalMessage("You have selected both layouts and panels", false, "You have selected both layouts and image panels", "labels can be added to either panels or layouts", "If you select a mixture, labels will be added to image panels only");
			targetClass=ImagePanelGraphic.class;
			ArraySorter.removeThoseNotOfClass(possibleTargets, targetClass);
			}
		
		
		boolean output=false;//true if at least one object has been added
		ArrayList<TextGraphic> added=new ArrayList<TextGraphic>();
		
		
		int count = 1;
		for(ZoomableGraphic item :possibleTargets) {
				if (item instanceof ImagePanelGraphic) {
					
					attachTextToSelectedImagePanel(ag, output, added, count, item);
					
					output=true;
					count++;//progress the loop
				}
			if (item instanceof PanelLayoutGraphic) {
			
				 attachTextToSelectedLayout(ag, output, added, count, item);
				
				output=true;
				count++;//progress the loop
			}
		
		}
		if (added.size()==0) undo=null;
		return added;
	}

	/**
	 * @param exampleTextItem the model text item, properties of each text item in the series is based on this one
	 * @param labelListStartedAlready has another text item already been added
	 * @param listOfLabels a list of the text items that have been added by the loop
	 * @param count the count within the loop
	 * @param imagePanel the imagepanel that the text is being attached to 
	 */
	protected void attachTextToSelectedImagePanel(TextGraphic exampleTextItem, boolean labelListStartedAlready, ArrayList<TextGraphic> listOfLabels,
			int count, ZoomableGraphic imagePanel) {
		ImagePanelGraphic it = (ImagePanelGraphic) imagePanel;
		
		TextGraphic  ag2 = exampleTextItem;
		
		if (labelListStartedAlready) {
			ag2=exampleTextItem.copy();
			ag2.setAttachmentPosition(exampleTextItem.getAttachmentPosition());
		} else {
			exampleTextItem.setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
			while (exampleTextItem.getBounds().width>0.9*it.getObjectWidth()) {exampleTextItem.setFontSize(exampleTextItem.getFont().getSize()-1);}
		}
		
		
		TakesAttachedItems taker=it;
		
		processItemAttachment(listOfLabels, count, it, ag2, taker);
		
		
		ag2.setTextColor(Color.white);
		
	}
	
	/**
	 * @param exampleTextItem the model text item, properties of each text item in the series is based on this one
	 * @param labelListStartedAlready has another text item already been added
	 * @param listOfLabels a list of the text items that have been added by the loop
	 * @param count the count within the loop
	 * @param layoutForAttachment the layout that the text is being attached to 
	 */
	protected void attachTextToSelectedLayout(TextGraphic exampleTextItem, boolean labelListStartedAlready, ArrayList<TextGraphic> listOfLabels,
			int count, ZoomableGraphic layoutForAttachment) {
		PanelLayoutGraphic it = (PanelLayoutGraphic) layoutForAttachment;
		
		TextGraphic  ag2 = exampleTextItem;
		
		if (labelListStartedAlready) {
			ag2=exampleTextItem.copy();
			ag2.setAttachmentPosition(exampleTextItem.getAttachmentPosition());
		} else {
			exampleTextItem.setAttachmentPosition(AttachmentPosition.defaultColLabel());
			exampleTextItem.getAttachmentPosition().setLocationTypeExternal(AttachmentPosition.ABOVE_AT_LEFT);
			//int newGrid = Arrays.binarySearch(AttachmentPosition.getGridchoices(), LayoutSpaces.ALL_MONTAGE_SPACE);
			exampleTextItem.getAttachmentPosition().setGridLayoutSnapType(4);
			exampleTextItem.getAttachmentPosition().setHorizontalOffset(-50);
			
			while(ag2.getFont().getSize()<it.getBounds().height/6) {
				ag2.setFontSize(ag2.getFont().getSize()+1);
			}
			
		}
		
		
		TakesAttachedItems taker=it;
		
		processItemAttachment(listOfLabels, count, it, ag2, taker);
		
		
		ag2.setTextColor(Color.black);
		
	}

	/**
	 * @param listOfLabels
	 * @param count
	 * @param attachmentLocation
	 * @param ag2
	 * @param taker
	 */
	protected void processItemAttachment(ArrayList<TextGraphic> listOfLabels, int count, ZoomableGraphic attachmentLocation,
			TextGraphic ag2, TakesAttachedItems taker) {
		String text = pattern.getText(count);
		ag2.setText(text); 
		 if (ag2 instanceof ComplexTextGraphic) {
			 ((ComplexTextGraphic) ag2).getParagraph().get(0).get(0).setText(text);
		 }
		
		
		listOfLabels.add(ag2);
		if(taker!=null)
			taker.addLockedItem(ag2);
		GraphicLayer p = attachmentLocation.getParentLayer();
		if (p instanceof MultichannelDisplayLayer) {p=p.getParentLayer();}
		if(addSmartLabels) {
			p=this.smartLayer;
			smartLayer.addLabel(ag2, attachmentLocation);
		}
		p.addItemToLayer(ag2);
		undo.addEditToList(new UndoAddItem(p, ag2));
	}

	/**
	 * @return
	 */
	protected ArrayList<ZoomableGraphic> getSelectedItems() {
		return selector.getSelecteditems();
	}
	
	@Override
	public String getCommand() {
		return "addText";
	}

	@Override
	public String getMenuCommand() {
		String output = "Add Text  ";
		if(!simple) 
			output= "Add Rich Text ";
		if(addSmartLabels) {
			output="Smart Label Sequence ";
		}
			output=output+"("+pattern.getSummary()+")   ";
		
		return output;
	}
	
	public Icon getIcon() {
		if(!simple) return  ComplexTextGraphic.createImageIcon();
		return TextGraphic.createImageIcon();
	}
	
	/**performs the action.*/
	public void run() {
		GraphicLayer l = null;
		if(selector!=null &&selector.getSelectedLayer()!=null)l=selector.getSelectedLayer();
	
		ZoomableGraphic item = this.add(l);
		
		if(undo!=null) getUndoManager().addEdit(undo) ;
		else
		if(item!=null) {
			this.getUndoManager().addEdit(new UndoAddItem(l, item));
		}
	}
	
	@Override
	public String getMenuPath() {
		String mainMenu = "To selected panels";
		
		return mainMenu;
	}

	
	

	
	

	
}
