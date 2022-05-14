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
 * Date Modified: April 25, 2021
 * Version: 2022.1
 */
package figureOrganizer;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.BasicObjectListHandler;
import layout.PanelContentExtract;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import utilityClasses1.ArraySorter;

/**As the user makes edits to a figure, the channel panels that appear
 * in the figure may no longer match the channel use instructions perfectly. @see ChannelUseInstructions
 * A discrepancy will have a visible impact if the user attempts to add a new image
 * to the figure or recreate the panels (then the stored instructions will be used).
 * This class contains methods that fix the most glaring discrepancies.
 * the methods in this class determine the channel order of a figure and returns
 * information about it. determines the order based on locations of panels.
 * Used by multiple tools to update the stored channel order
 * */
public class PanelOrderCorrector  implements Serializable, LayoutSpaces{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CollectivePanelManagement panelManagement;

	/***/
	public PanelOrderCorrector(FigureOrganizingLayerPane f) {

		panelManagement=new PanelManagementGroup(f);
	}
	
	/**
	 * @param panelManagement2
	 */
	public PanelOrderCorrector(CollectivePanelManagement panelManagement2) {
		panelManagement=panelManagement2;
	}

	/**Returns the panel list elements in an order that is determined by 
	 * their location within the layout (Panel #1,2,3) rather than any stored
	 * list
	  */
	public ArrayList<PanelListElement> getOrderedPanelList() {
		DefaultLayoutGraphic g = panelManagement.getTargetLayout();
		g.generateCurrentImageWrapper();
		BasicLayout layout = g.getPanelLayout();
		
		ArrayList<PanelListElement> output = new ArrayList<PanelListElement>();
		for(int i=1; i<=layout.nPanels(); i++) {
			Rectangle2D rect = layout.getPanel(i);
			addElementsInPanel(output, rect);
		}
	
	return output;
	}


	
	/**returns an ordered set of panel list elements from each row or columns.
	 * The type given may be ROWS, COLUMNS or PANELS @see MontageLayoutSpaces*/
	public ArrayList<ArrayList<PanelListElement>> getOrderedPanelList(int type) {
		DefaultLayoutGraphic g = panelManagement.getTargetLayout();
		g.generateCurrentImageWrapper();
		BasicLayout layout = g.getPanelLayout();
		layout=layout.makeAltered(type);
		ArrayList<PanelContentExtract> read = g.getEditor().cutStack(layout);
		ArrayList<ArrayList<PanelListElement>> output=new ArrayList<ArrayList<PanelListElement>>();
		for(PanelContentExtract content: read) {
			ArrayList<PanelListElement> part = new ArrayList<PanelListElement>();
			ArrayList<LocatedObject2D> olist = content.getObjectList();
			olist=new ArraySorter<LocatedObject2D>().getThoseOfClass(olist, ImagePanelGraphic.class);
			addPanelListElements(part, olist);
			
			output.add(part);
		}
		
		 g.getEditor().pasteStack(layout, read);
	
		 return output;
	}

	/**finds the panel list elements with panels inside of the given rectangle and adds them to the array*/
	public void addElementsInPanel(ArrayList<PanelListElement> output, Rectangle2D rect) {
		ArrayList<LocatedObject2D> inPanel = new BasicObjectListHandler().getAllClickedRoi(panelManagement.getTargetLayer(), rect.getCenterX(), rect.getCenterY(), ImagePanelGraphic.class);
		addPanelListElements(output, inPanel);
	}
	
	

	/**when given a list of located objects, determines whether those objects are display panels for panel list elements
	  and adds the elements to the list*/
	public void addPanelListElements(ArrayList<PanelListElement> output, ArrayList<LocatedObject2D> inPanel) {
		for(LocatedObject2D imagePanel: inPanel) {
			PanelListElement sPanel = ((ImagePanelGraphic)imagePanel).getSourcePanel();
			if (sPanel!=null)output.add(sPanel);
			}
	}
	
	/**Checks either rows or columns to see if each one displays only a single channel's channel panels*/
	public boolean singleChannelPer(int type) {
		return getChannelOrder(type)!=null;
	}
	
	/**attempts to generate a channel use instructions with the same order as the panels in the figure.
	 * May return null if the current order does not fit with a set of instructions*/
	public ChannelUseInstructions determineChannelOrder() {
		if (singleChannelPer(ROWS)) {
			return getChannelOrder(ROWS);
		} else
		if (singleChannelPer(COLS)) {
			return getChannelOrder(COLS);
		}
		
		if(isEachPanelADifferentChannel())
			return getChannelOrder(PANELS);
		
		return null;
	}
	
	/**returns true if each channel in the image is represented by only one image panel*/
	boolean isEachPanelADifferentChannel() {
		ImageDisplayLayer pm = panelManagement.getMultichannel();
		if(getDisplaysInOrder().size()==1 &&pm.getMultiChannelImage().nFrames()==1&&pm.getMultiChannelImage().nSlices()==1)
			return true;
		
		return false;
	}

	
	
	/**returns the kind of layout, returns null if the layout does not match 
	   the expected patterns */
	public Integer determineChannelLayout() {
		if (singleChannelPer(ROWS)) {
			return ROWS;
		} else
		if (singleChannelPer(COLS)) {
			return COLS;
		}
		if(isEachPanelADifferentChannel())
			return PANELS;
		
		return null;
	}

	/**checks either the rows, panels or columns of the figure depending on the argument
	  returns channel use instructions containing the order of the channels
	  with the channels ordered in the same way that the panel that actually appear in the figure
	   or null if each row or column contains multiple channels*/
	public ChannelUseInstructions getChannelOrder(int type) {
		ArrayList<ArrayList<PanelListElement>> list = getOrderedPanelList(type);
		ArrayList<Integer> order=new ArrayList<Integer>();
		int whereIsMerge=ChannelUseInstructions.NO_MERGE_PANELS;
		
		for(int rowIndex=1; rowIndex<=list.size(); rowIndex++) {
					boolean added=false;
				ArrayList<PanelListElement> l = list.get(rowIndex-1);
				for(int i=1; i<l.size(); i++) {
					PanelListElement panelListElement = l.get(i);
					PanelListElement panelListElement2 = l.get(i-1);
					boolean sameChannel = panelListElement.targetChannelNumber==panelListElement2.targetChannelNumber;
					
					
					
					if(!sameChannel) return null;
					
				}
				if (l.size()==0) continue;
				PanelListElement panelListElement = l.get(0);
				/**if this is the first channel panel of the row/col, adds the chan number to the list*/
				if(!panelListElement.isTheMerge()&&!added) {
					order.add(panelListElement.targetChannelNumber);
					added=true;
				}
				
				if(panelListElement.isTheMerge()&&rowIndex==1) 
					whereIsMerge=ChannelUseInstructions.MERGE_FIRST;
				if(panelListElement.isTheMerge()&&rowIndex==list.size() )
					whereIsMerge=ChannelUseInstructions.MERGE_LAST;
		}
		
		if (order.size()==0)
			whereIsMerge=ChannelUseInstructions.ONLY_MERGE_PANELS;
	 ChannelUseInstructions output = new ChannelUseInstructions();
	 if (order.size()!=0)
		 output.getChanPanelReorder().setOrder(order);
	 output.MergeHandleing=whereIsMerge;
	 
		return output;
	}
	
	/**looks for panel list elements that are present in the row, column or panel given*/
	public int channelIndexAt(int type, int rowIndex) {
		ArrayList<ArrayList<PanelListElement>> list = getOrderedPanelList(type);
		ArrayList<PanelListElement> l = list.get(rowIndex-1);
		if(l.size()>0) {
			PanelListElement panelListElement = l.get(0);
			if (panelListElement!=null &&panelListElement.isChannelPanel())
			return panelListElement.targetChannelNumber;
			}

		return PanelListElement.NONE;
	}
	
	/**returns the indices of where in the layout the given channels are stored
	
	 * */
	public ArrayList<Integer> indexOfChannel( int channel, int type) {
		ArrayList<Integer> indices=new ArrayList<Integer>();
		BasicLayout layout = panelManagement.getUsedLayout().makeAltered(type);
		for(int i=1; i<=layout.nPanels(); i++) {
			if (channel==channelIndexAt(type, i))
				indices.add(i);
		}
		return indices;
	}



	/**determines how the channel panels actually appear in the figure
	  updates the channel use instructions for the figure to match the actual locations of
	  the channel panels*/
	public void updateChannelOrder() {
		
		ChannelUseInstructions cOrder = determineChannelOrder();
		if (cOrder!=null)
		for(ChannelUseInstructions c: panelManagement.getChannelUserInformation())
			try {
					c.MergeHandleing=cOrder.MergeHandleing;
					c.getChanPanelReorder().setOrder(cOrder.getChanPanelReorder());
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
	}

	

	/**if the channels are layed out in the form given,
	  updates the channel order*/
	public void updateChanOrder(int type) {
		if (singleChannelPer(type) ) {
			updateChannelOrder();
		}
	}

	/**returns a comparator for ordering the images*/
	public ImageOrderComparator getImageOrderUpdate() {
		return new ImageOrderComparator(getDisplaysInLayoutImageOrder());
	}
	
	/**returns a list of the display layers in layout order*/
	public ArrayList< ImageDisplayLayer> getDisplaysInLayoutImageOrder() {
		ArrayList<PanelListElement> list = getOrderedPanelList();
		ArrayList<? extends ImageDisplayLayer> allContainedDisplays = getDisplaysInOrder();
		ArrayList< ImageDisplayLayer> displays=new 	ArrayList<ImageDisplayLayer>();
		
		for(PanelListElement l: list) {
			if (allContainedDisplays.contains(l.getPanelGraphic().getParentLayer())&&!displays.contains(l.getPanelGraphic().getParentLayer())) 
			{
				ImageDisplayLayer parentLayer = (ImageDisplayLayer) l.getPanelGraphic().getParentLayer();
				displays.add(parentLayer);
				parentLayer.getSetter().startPoint=list.indexOf(l)+1;
			};
			
			
		}
		return displays;
	}
	
	
	/**returns a list of the panel managers in layout order. work in progress*/
	public ArrayList< PanelManager> getPanelManagersInLayoutImageOrder() {
		ArrayList<PanelListElement> list = getOrderedPanelList();
		ArrayList<? extends PanelManager> allContainedManagers = panelManagement.getPanelManagers();;
		ArrayList< PanelManager> displays=new 	ArrayList<PanelManager>();
		
		for(PanelListElement l: list) {
			PanelManager managerForL=null;
			for(PanelManager m: allContainedManagers) {
				if(m.getPanelList().getPanels().contains(l))
					managerForL=m;
			}
			
			if (allContainedManagers.contains(managerForL)&&!displays.contains(managerForL)) 
			{
				displays.add(managerForL);
				
			};
			
			
		}
		return displays;
	}

	
	/**compares the order*/
	public static class ImageOrderComparator implements Comparator<ImageDisplayLayer> {
		private ArrayList<ImageDisplayLayer> newOrder;

		public ImageOrderComparator(ArrayList< ImageDisplayLayer> newOrder) {
			this.newOrder=newOrder;
		}

		@Override
		public int compare(ImageDisplayLayer o1, ImageDisplayLayer o2) {
			int indexOf = newOrder.indexOf(o1);
			int indexOf2 = newOrder.indexOf(o2);
			if (indexOf==-1) return 1;
			if (indexOf2==-1) return -1;
		
			return indexOf-indexOf2;
		}
		
	}


	/**
	 * @return
	 */
	public ArrayList<? extends ImageDisplayLayer> getDisplaysInOrder() {
		return panelManagement.getDisplaysInOrder();
	}
	
	

}
