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
 * Date Modified: Jan 10, 2021
 * Version: 2022.1
 */
package multiChannelFigureUI;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.LocatedObject2D;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;

/**A set of handles that provide a way for the user to re-order channels*/
public class ChannelSwapHandleList extends SmartHandleList {

	/**
	 * 
	 */
	private static final int HANDLE_CODE_FOR_SWAP_HANDLE = 800;
	/**
	 * 
	 */
	
	public static final int SWAP_IN_INSTRUCTIONS=0, SWAP_SOURCE_IMAGE_CHANNELS=1;
	int swapType=SWAP_IN_INSTRUCTIONS;
	
	private static final long serialVersionUID = 1L;
	private FigureOrganizingLayerPane figure;
	private ArrayList<ChannelEntry> channels;
	LocatedObject2D anchorObject=null;
	
	
	int pressHandleIndex=0;
	
	private MultichannelDisplayLayer theDisplayLayer;
	
	
	public ChannelSwapHandleList(FigureOrganizingLayerPane f, ArrayList<ChannelEntry> chans, LocatedObject2D anchorObject) {
		this.figure=f;
		this.channels=chans;
		this.anchorObject=anchorObject;
		
		innitializeHandles();
	}


	/**Creates channel swap handles. Also sets the locations and colors for the handes*/
	public void innitializeHandles() {
		createHandles();
		updateLocations();
		updateColors();
	}
	
	/**returns the index of the channel handle at the given click location*/
	int getPressChannel(CanvasMouseEvent e) {
		SmartHandle h = this.getHandleForClickPoint(new Point2D.Double(e.getClickedXScreen(), e.getClickedYScreen()));
		if (h!=null)
			return h.getHandleNumber()-HANDLE_CODE_FOR_SWAP_HANDLE;
		
		return -1;
	}
	
	private void createHandles() {
		
		for(ChannelEntry chan:channels) {
			add(new ChannelSwapHandle(chan));
		}
		
	}
	
	/**Sets up handles for the channelentry list*/
	public void updateList(ArrayList<ChannelEntry> hashChannel) {
		this.channels=hashChannel;
		this.clear();//elemintates the old handles 
		innitializeHandles();
	}

	void updateLocations() { 
		if (anchorObject!=null) {
			double x = anchorObject.getBounds().getMaxX()+8;
			double y = anchorObject.getBounds().getY();
			double step=anchorObject.getBounds().getHeight()/this.size();
			for(SmartHandle h: this) {
				h.setCordinateLocation(new Point2D.Double(x,y));
				y+=step;
			}
		}
	}
	
	
	void updateColors() {
		for(SmartHandle c: this) {
			if (c instanceof ChannelSwapHandle) 
			{
				ChannelSwapHandle handle =(ChannelSwapHandle) c;
				handle.setUpChannelColorAndText();
			}
		}
		
	}
	
	public class ChannelSwapHandle extends SmartHandle {
		
		int channelNumber=1;
		private ChannelEntry entry;
		private boolean draged=false;
		
		

		public ChannelSwapHandle(ChannelEntry entry) {
			
			this.entry=entry;
			this.setHandleNumber(HANDLE_CODE_FOR_SWAP_HANDLE+this.getChannelNumber());
			this.handlesize=6;
		}
		
		public void draw(Graphics2D g, CordinateConverter cords) {
			updateLocations();
			updateColors();
			
			super.draw(g, cords);
			
		}
		
	

		/**sets the color and message of the handle to the channel entry*/
		void setUpChannelColorAndText() {
				try {
					this.message=(
							getTheDisplayLayer().getMultiChannelImage().getRealChannelName(getChannelNumber()));
					if(message==null) message="Channel #"+entry.getOriginalChannelIndex();
					this.setHandleColor(entry.getColor());
					this.messageColor=entry.getColor().darker();
				} catch (Throwable t) {}
					
					
				
		}



		/**
		 returns the channel number used by this handle
		 */
		protected int getChannelNumber() {
			return entry.getOriginalChannelIndex();
		}
	
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			pressHandleIndex=getPressChannel(canvasMouseEventWrapper);
			draged=false;
		}
		
		public void handleDrag(CanvasMouseEvent canvasMouseEventWrapper) {
			
			draged=true;
		}
		
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		if(!draged) return;
		
		int relHandleIndex = getPressChannel(canvasMouseEventWrapper);
		int pressHandleIndex2 = pressHandleIndex;
		if (pressHandleIndex2==relHandleIndex)return;
		if (relHandleIndex==-1) {
			ShowMessage.showOptionalMessage("Channel Reordering handle", true, "To swap channels, drag one channel handle into another channel handle and release");
		}
		
		performChannelSwap(relHandleIndex, pressHandleIndex2);
	
	}

	/**
	performs a channel swap between the two channels with the given original channel indices (their locations in the multichannel image)
	 */
	public void performChannelSwap(int relHandleIndex, int pressHandleIndex2) {
		if (swapType==SWAP_IN_INSTRUCTIONS) {
			swapImageChannelOrder(relHandleIndex,pressHandleIndex2);
		}
		else {swapSourceImageChannels(relHandleIndex, pressHandleIndex2);}
	}
	
	/**
	swaps the two channels given. does not alter the source image file
	 */
	protected void swapImageChannelOrder(int relHandleIndex, int pressHandleIndex2) {
		if (getTheDisplayLayer().getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) getTheDisplayLayer().getParentLayer() ;
			for(ImageDisplayLayer item: f.getMultiChannelDisplaysInOrder()) {
				item.getPanelManager().performChannelSwap(relHandleIndex, pressHandleIndex2);
			}
		}
		else if(pressHandleIndex2!=relHandleIndex) {
			getTheDisplayLayer().getPanelManager().performChannelSwap(relHandleIndex, pressHandleIndex2);
		}
	}

	/**
	swaps the two channels given
	 */
	protected void swapSourceImageChannels(int relHandleIndex, int pressHandleIndex2) {
		if (getTheDisplayLayer().getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) getTheDisplayLayer().getParentLayer() ;
			for(ImageDisplayLayer item: f.getMultiChannelDisplaysInOrder()) {
				item.getMultiChannelImage().getChannelSwapper().swapChannelsOfImage(relHandleIndex, pressHandleIndex2);
				item.updatePanels();
			}
		}
		else if(pressHandleIndex2!=relHandleIndex) {
			getTheDisplayLayer().getMultiChannelImage().getChannelSwapper().swapChannelsOfImage(relHandleIndex, pressHandleIndex2);
			getTheDisplayLayer().updatePanelsAndLabelsFromSource();
		}
	}
	
	/**returns a popup menu*/
	public JPopupMenu getJPopup() {
		if(entry!=null) {
			
			
			SmartPopupJMenu output = new SmartPopupJMenu ();
			
			figure=FigureOrganizingLayerPane.findFigureOrganizer(getTheDisplayLayer());
			
			ChannelPanelEditingMenu out;
			if(anchorObject instanceof ImagePanelGraphic)
				out= new ChannelPanelEditingMenu((ImagePanelGraphic)anchorObject, entry);
			else out=new ChannelPanelEditingMenu(figure, ChannelUseInstructions.NONE_SELECTED);
			if (figure.getMultiChannelDisplaysInLayoutOrder().size()==1) {out.addChannelRelevantMenuItems(output);} 
			else {
				SmartJMenu jEveryImage=new SmartJMenu("For Each Image");
				out.addChannelRelevantMenuItems(jEveryImage);
				
				
				output.add(jEveryImage);
				
				SmartJMenu jOneImage=new SmartJMenu("Just This Image's Panels");
				jOneImage.setText("Only For "+getTheDisplayLayer().getTitle());
				
				out = new ChannelPanelEditingMenu((ImagePanelGraphic)anchorObject, entry);
				out.setScope(0);
				out.addChannelRelevantMenuItems(jOneImage);
				output.add(jOneImage);
			}
			
		return output;
		}
		return null;
	}
	
		private static final long serialVersionUID = 1L;}

	
	
	/**Sets the multichannel display that is the main target of this handle list*/
	public void setDisplayLayer(MultichannelDisplayLayer display) {
		this.theDisplayLayer=display;
		
	}

	/**Returns the multichannel display that is the main target of this handle list*/
	public MultichannelDisplayLayer getTheDisplayLayer() {
		if(theDisplayLayer==null && anchorObject instanceof ImagePanelGraphic)
			{
			ImagePanelGraphic panel=(ImagePanelGraphic) anchorObject;
			theDisplayLayer=	MultichannelDisplayLayer.findMultiChannelForGraphic(panel.getParentLayer(), panel);
			
			}
		return theDisplayLayer;
	}

	

}
