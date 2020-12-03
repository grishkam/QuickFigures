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
package figureFormat;

import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import gridLayout.LayoutSpaces;
import logging.IssueLog;
import undo.AbstractUndoableEdit2;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.AttachmentPosition;

/**A subclass of graphical item picker for labels.
  Includes/excludes text from its category based on the attachment position (row, column or panel).
  does not select channel labels*/
public class LabelExamplePicker extends GraphicalItemPicker<TextGraphic> implements LayoutSpaces {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int[] typicalTypes=new int[] {LayoutSpaces.ROW_OF_PANELS, LayoutSpaces.COLUMN_OF_PANELS, LayoutSpaces.PANELS};
	private int desiredGridSnapType=LayoutSpaces.ROW_OF_PANELS;
	
	
	/**Creates a label picker for labels with the given grid attachment position*/
	public LabelExamplePicker(TextGraphic model, int desiredAttachmentPosition) {
		super(model);
		this.setDesiredGridSnapType(desiredAttachmentPosition);

	}
	
	/**returns the option name for this picker*/
	@Override
	public String getOptionName() {
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ROW_OF_PANELS) return "Pick Row Label";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.COLUMN_OF_PANELS) return "Pick Column Label";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.PANELS) return "Pick Panel Label";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ALL_MONTAGE_SPACE) return "Pick Title Label";
		
		return optionname;
	}
	
	
	public String getTypeName() {
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ROW_OF_PANELS) return "Row Labels";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.COLUMN_OF_PANELS) return "Column Labels";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.PANELS) return "Panel Labels";
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ALL_MONTAGE_SPACE) return "Title Label";
		return optionname;
	}
	
	/**returns true if the object is a TextGraphi with the desired snap type*/
	public boolean isDesirableItem(Object o) {
		if (modelItem instanceof BarGraphic.BarTextGraphic) return false;
		if (!(o instanceof TextGraphic))
			return false;
		if (o instanceof ChannelLabelTextGraphic) 
			return false;
		TextGraphic tg=(TextGraphic) o;
		if (tg.getAttachmentPosition()==null) return false;
		
		if (AttachmentPosition. getGridchoices()[tg.getAttachmentPosition().getGridLayoutSnapType()]!=getDesiredGridAttachmentLocation()) {
		
		return false;
		}
		
		return true;
	}
	
	
	/**if the object given is a text graphic of the right type, alters the 
	  text to match the example item*/
	@Override
	public AbstractUndoableEdit2 applyProperties(Object item) {
		if (!this.isDesirableItem(item)) 
			return null;
		if (this.getModelItem()==null) 
			return null;
		
		if (item instanceof TextGraphic) {
			TextGraphic item2=(TextGraphic) item;
			AbstractUndoableEdit2 undo = item2.provideUndoForDialog();
			item2.copyAttributesFrom(super.getModelItem());
		
			if (getModelItem().getAttachmentPosition()!=null)
				item2.setAttachmentPosition(getModelItem().getAttachmentPosition().copy());
			return undo;
		}
		return null;
	}
	
	/**Sets the model label for this picker. if the given label
	 * does not fit the criteria to be in this pickers category, it is not used*/
	public void setModelItem(Object modelItem) {
			
		if (!this.isDesirableItem(modelItem)) return;
		try{this.modelItem = (TextGraphic)modelItem;} catch (Exception e) {
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}

	/**Which grid attachment type, this picker targets*/
	private int getDesiredGridAttachmentLocation() {
		return desiredGridSnapType;
	}

	/**Set which grid attachment type, this picker targets*/
	private void setDesiredGridSnapType(int desiredGridSnapType) {
		this.desiredGridSnapType = desiredGridSnapType;
	}
	
	public boolean isInRowOrColumnOrPanel() {
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ROW_OF_PANELS) return true;
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.COLUMN_OF_PANELS) return true;
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.PANELS) return true;
		return false;
	}
	

	
	/**Alters the font size of the model label to fit the image in the multi channel display layer
	 */
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		
		/**Calculation to get a comfortable font size for the panel*/
		float h2=(float) (wrap.getPanelList().getHeight()*wrap.getPanelManager().getPanelLevelScale());
		float h=h2/5;
		h=(float)NumberUse.findNearest(h, new double[] {0,2,4,6,8,10,12,14,16,20, 18,24,28, 32,36,40});
		
		
		if (this.getModelItem()!=null) {
			
			getModelItem().setFont(getModelItem().getFont().deriveFont(h));
			setModelAttachmentPositionDefault();
			
		}
	}
	
	/** returns the attachment position that is default for the target type of label*/
	public AttachmentPosition getDefaultAttachmentPosition() {
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ROW_OF_PANELS) return AttachmentPosition .defaultRowLabel() ;
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.COLUMN_OF_PANELS) return AttachmentPosition.defaultColLabel();
		
		 return AttachmentPosition.defaultPanelLabel();
	}
	
	/**sets the attachment position of the model label to the default*/
	protected void setModelAttachmentPositionDefault() {
		if (this.getModelItem()==null)
			return;
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ROW_OF_PANELS) 	getModelItem().setAttachmentPosition(AttachmentPosition.defaultRowSide());
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.COLUMN_OF_PANELS)	getModelItem().setAttachmentPosition(AttachmentPosition.defaultColLabel());
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.PANELS) getModelItem().setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
		if (getDesiredGridAttachmentLocation()==LayoutSpaces.ALL_MONTAGE_SPACE) getModelItem().setAttachmentPosition(AttachmentPosition.defaultPlotTitle());
		
	
	}
	
	public TextGraphic getModelItem() {
		return modelItem;
	}
	
}
