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
 * Date Created: Aug 16, 2026
 * Date Modified: Aug 16, 2026
 * Version: 2026.1
 */
package addObjectMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayerTypes.LaneLabelLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.AttachmentPosition;
import utilityClasses1.TagConstants;

/**
 Implements an option to copy a series of lane labels
 */
public class LaneLabelCopy extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane addedLayer = new LaneLabelLayer();
		
		ArrayList<ZoomableGraphic> listedItems = gc.getAllGraphics();
		DefaultLayoutGraphic the_layout_copy=null;
		ArrayList<TextGraphic> new_text = new ArrayList<TextGraphic>();
		AttachmentPosition newAttachment=null;
		
		for(ZoomableGraphic i : listedItems) {
			if(i instanceof TextGraphic) {
				TextGraphic ag=(TextGraphic) i;
				TextGraphic ag2 = ag.copy();
				
				Object index_value = ag.getTagHashMap().get(TagConstants.INDEX);
				if(index_value!=null) {
						ag2.getTagHashMap().put(TagConstants.INDEX,index_value);//makes the lane index the same
						if(newAttachment==null) {
							newAttachment=ag.getAttachmentPosition().copy();
							ag2.setAttachmentPosition(newAttachment);
						} else  {
							ag2.setAttachmentPosition(newAttachment);
						}
				}
				addedLayer.add(ag2);
				new_text.add(ag2);
			}
			if(i instanceof DefaultLayoutGraphic) {
				the_layout_copy=((DefaultLayoutGraphic) i).copy();
				LaneLabelAdder.makeIntoLaneLabelLayout(the_layout_copy);
				addedLayer.add(the_layout_copy);
				
			}
			
			
		}
		
		if(the_layout_copy!=null)
			for(TextGraphic text: new_text) {
				the_layout_copy.addLockedItem(text);
			}
		gc.getParentLayer().add(addedLayer);
		
		the_layout_copy.moveLayoutAndContents(the_layout_copy.getBounds().width, 0);
		
		return(addedLayer);
		
	}

	@Override
	public String getCommand() {
		return "addText";
	}

	@Override
	public String getMenuCommand() {
		return "Copy Lane labels";
	}
	
	@Override
	public String getMenuPath() {
		return "To selected panels";
	}
	

}
