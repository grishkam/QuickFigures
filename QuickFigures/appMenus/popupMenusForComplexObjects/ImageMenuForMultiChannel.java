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
package popupMenusForComplexObjects;

import javax.swing.JMenu;

import channelMerging.MultiChannelSlotDialog;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import iconGraphicalObjects.ChannelUseIcon;

public class ImageMenuForMultiChannel extends MenuForMultiChannelDisplayLayer {

	public ImageMenuForMultiChannel(String name, MultichannelDisplayLayer panel,
			PanelList list) {
		super(name, panel, list, panel.getChannelLabelManager());
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void generateLabelMenuItems() {
		createSetScaleItem() ;
		createCropOption();
		
		JMenu mm = new JMenu("Expert Options") ;
		createSaveImageItem(mm) ;
		createSetImageItem(mm) ;
		createMultiChanOpsions(mm) ;
		createChannelUseItem(mm);
		createShowImageItem(mm);
		this.add(mm);
		
	}
	
	private SmartMenuItem2 createMultiChanOpsions(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Saving Options  ") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				MultiChannelSlotDialog dia = new MultiChannelSlotDialog(display.getSlot()) ;
				dia.showDialog();
			}
			
		};
		thi.add(out);
		return out;
		
		
	}
	



	
	
	SmartMenuItem2 createShowImageItem(JMenu t) {
		SmartMenuItem2 out=new SmartMenuItem2("Show Image") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction() {
				display.getSlot().showImage();
			}
			
		};
		t.add(out);
		return out;
	}
	
	
	
	


	
	SmartMenuItem2 createSaveImageItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Save Image") {
						private static final long serialVersionUID = 1L;
						public void onAction() {
							display.getSlot().saveImageEmbed();
						}	};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createSetImageItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Set Image") {
			private static final long serialVersionUID = 1L;
			public void onAction() {
				display.getSlot().setImageDialog();
			}
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createChannelUseItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Channel Use ") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction() {
				display. showStackOptionsDialog();
			}
		};
		out.setIcon(new ChannelUseIcon(display.getMultiChannelImage().getChannelEntriesInOrder()));
		thi.add(out);
		return out;
	}
}
