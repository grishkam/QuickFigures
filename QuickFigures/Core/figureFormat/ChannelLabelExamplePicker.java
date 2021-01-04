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
package figureFormat;

import channelLabels.ChannelLabelTextGraphic;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.ColorDimmer;
import undo.AbstractUndoableEdit2;
import undo.ChannelLabelPropertiesUndo;
import undo.CombinedEdit;

/**A subclass of label picker that selects only channel labels*/
public class ChannelLabelExamplePicker extends LabelExamplePicker {

	public ChannelLabelExamplePicker(TextGraphic model) {
		super(model, 0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns true if the object is a channel label with an attachment position*/
	public boolean isDesirableItem(Object o) {
		if (!(o instanceof ChannelLabelTextGraphic))
			return false;
		TextGraphic tg=(TextGraphic) o;
		if (tg.getAttachmentPosition()==null) return false;
		
		return true;
	}
	
	/**when given a display layer. modifies the example label to make it more appropriate 
	  for the display layer*/
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		super.setToStandardFor(wrap);
		if (getModelItem()==null) return;
		this.getModelItem().setDimming(ColorDimmer.NORMAL_DIM);
	}
	
	/**Sets the attachment position for the label to the default*/
	protected void setModelAttachmentPositionDefault() {
		if (getModelItem()!=null)
		getModelItem().setAttachmentPosition(AttachmentPosition.defaultColSide());
	}
	
	public String getOptionName()  {
		return "Pick Channel Label";
	}
	
	/**if the target object is a channel label.
	  alters the object's properties to match the model (the example label)*/
	@Override
	public AbstractUndoableEdit2 applyProperties(Object item) {
		
		if (this.getModelItem()==null) return null;
		
		if (!this.isDesirableItem(item)) return null;
		
		if (item instanceof ChannelLabelTextGraphic) {
			
			ChannelLabelTextGraphic c=(ChannelLabelTextGraphic) item;
			ChannelLabelTextGraphic cModel=(ChannelLabelTextGraphic) getModelItem();
			
			CombinedEdit undo = new CombinedEdit();
			undo.addEditToList(super.applyProperties(item));
			undo.addEditToList(c.provideUndoForDialog());
			undo.addEditToList(new ChannelLabelPropertiesUndo(c.getChannelLabelProperties()));
			c.getChannelLabelProperties().setMergeText(cModel.getChannelLabelProperties().getMergeText());
			c. setParaGraphToChannels();
			
			return undo;
		}
		return null;
		
	}

}
