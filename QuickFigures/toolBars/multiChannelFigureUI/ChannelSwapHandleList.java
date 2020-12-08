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
package multiChannelFigureUI;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.ChannelEntry;
import channelMerging.ImageDisplayLayer;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import utilityClassesForObjects.LocatedObject2D;

public class ChannelSwapHandleList extends SmartHandleList {

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



	public void innitializeHandles() {
		createHandles();
		updateLocations();
		updateColors();
	}
	

	
	int getPressChannel(CanvasMouseEvent e) {
		SmartHandle h = this.getHandleForClickPoint(new Point2D.Double(e.getClickedXScreen(), e.getClickedYScreen()));
		if (h!=null)
			return h.getHandleNumber()-800;
		
		return -1;
	}
	
	private void createHandles() {
		for(ChannelEntry chan:channels) {
			add(new ChannelSwapHandle(chan.getOriginalChannelIndex()));
		}
		
	}
	
	public void updateList(ArrayList<ChannelEntry> hashChannel) {
		this.channels=hashChannel;
		this.clear();
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
				handle.setUpChannelColor();
			}
		}
	}
	
	public class ChannelSwapHandle extends SmartHandle {
		
		public void draw(Graphics2D g, CordinateConverter<?> cords) {
			updateLocations();
			updateColors();
			
			super.draw(g, cords);
			
		}
		
		int channelNumber=1;
		private ChannelEntry entry;

		public ChannelSwapHandle(int index) {
			super(0, 0);
			channelNumber=index;
			this.setHandleNumber(800+index);
			this.handlesize=6;
		}
		
		public ChannelSwapHandle(int x, int y) {
			super(x, y);
			
		}
		

		void setUpChannelColor() {
			for(ChannelEntry chan:channels) {
				if(chan.getOriginalChannelIndex()==channelNumber) {
					this.setHandleColor(chan.getColor());
				try {
					this.message=(
							theDisplayLayer.getMultiChannelImage().getRealChannelName(channelNumber));
					if(message==null) message="Channel #"+chan.getOriginalChannelIndex();
					this.entry=chan;
				} catch (Throwable t) {}
					
					this.messageColor=chan.getColor().darker();
				}
			}
		}
	
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			pressHandleIndex=getPressChannel(canvasMouseEventWrapper);
		}
		
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		int relHandleIndex = getPressChannel(canvasMouseEventWrapper);
		int pressHandleIndex2 = pressHandleIndex;
		if (pressHandleIndex2==relHandleIndex)return;
		
		
		if (swapType==SWAP_IN_INSTRUCTIONS) {
			swapImageChannelOrder(relHandleIndex,pressHandleIndex2);
		}
		else 
			{swapSourceImageChannels(relHandleIndex, pressHandleIndex2);}
	
	}
	
	/**
	swaps the two channels given
	 */
	protected void swapImageChannelOrder(int relHandleIndex, int pressHandleIndex2) {
		if (theDisplayLayer.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) theDisplayLayer.getParentLayer() ;
			for(ImageDisplayLayer item: f.getMultiChannelDisplaysInOrder()) {
				item.getPanelManager().performChannelSwap(relHandleIndex, pressHandleIndex2);
			}
		}
		else if(pressHandleIndex2!=relHandleIndex) {
			theDisplayLayer.getPanelManager().performChannelSwap(relHandleIndex, pressHandleIndex2);
		}
	}

	/**
	swaps the two channels given
	 */
	protected void swapSourceImageChannels(int relHandleIndex, int pressHandleIndex2) {
		if (theDisplayLayer.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) theDisplayLayer.getParentLayer() ;
			for(ImageDisplayLayer item: f.getMultiChannelDisplaysInOrder()) {
				item.getMultiChannelImage().getChannelSwapper().swapChannelsOfImage(relHandleIndex, pressHandleIndex2);
				item.updatePanels();
			}
		}
		else if(pressHandleIndex2!=relHandleIndex) {
			theDisplayLayer.getMultiChannelImage().getChannelSwapper().swapChannelsOfImage(relHandleIndex, pressHandleIndex2);
			theDisplayLayer.updatePanelsAndLabelsFromSource();
		}
	}
	
	public JPopupMenu getJPopup() {
		if(entry!=null) {
			
			
			SmartPopupJMenu output = new SmartPopupJMenu ();
			
			ChannelPanelEditingMenu out = new ChannelPanelEditingMenu((ImagePanelGraphic)anchorObject, entry);
			if (theDisplayLayer.getParentLayer() instanceof FigureOrganizingLayerPane) {
				figure=(FigureOrganizingLayerPane) theDisplayLayer.getParentLayer();
			}
			
			
			if (figure.getMultiChannelDisplaysInLayoutOrder().size()==1) {out.addChannelRelevantMenuItems(output);} 
			else {
				SmartJMenu jEveryImage=new SmartJMenu("For Each Image");
				out.addChannelRelevantMenuItems(jEveryImage);
				
				
				output.add(jEveryImage);
				
				SmartJMenu jOneImage=new SmartJMenu("Just This Image's Panels");
				jOneImage.setText("Only For "+theDisplayLayer.getTitle());
				
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

	public void setDisplayLayer(MultichannelDisplayLayer display) {
		this.theDisplayLayer=display;
	}

	

}
