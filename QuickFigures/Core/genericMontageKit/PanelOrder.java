package genericMontageKit;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import channelMerging.ChannelUseInstructions;
import channelMerging.PanelStackDisplay;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.MontageSpaces;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;

/**the methods in this class determine the channel order of a figure and returns
 * information about it*/
public class PanelOrder  implements Serializable, MontageSpaces{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FigureOrganizingLayerPane figure;

	public PanelOrder(FigureOrganizingLayerPane f) {
		this.figure=f;
	}
	
	
	public ArrayList<PanelListElement> getOrderedPanelList() {
		MontageLayoutGraphic g = figure.getMontageLayoutGraphic();
		g.generateCurrentImageWrapper();
		BasicMontageLayout layout = g.getPanelLayout();
		
		ArrayList<PanelListElement> output = new ArrayList<PanelListElement>();
		for(int i=1; i<=layout.nPanels(); i++) {
			Rectangle2D rect = layout.getPanel(i);
			addElementsInPanel(output, rect);
		}
	
	return output;
	}
	
	public ArrayList<ArrayList<PanelListElement>> getOrderedPanelList(int type) {
		MontageLayoutGraphic g = figure.getMontageLayoutGraphic();
		g.generateCurrentImageWrapper();
		BasicMontageLayout layout = g.getPanelLayout();
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


	public void addElementsInPanel(ArrayList<PanelListElement> output, Rectangle2D rect) {
		ArrayList<LocatedObject2D> inPanel = new BasicObjectListHandler().getAllClickedRoi(figure, rect.getCenterX(), rect.getCenterY(), ImagePanelGraphic.class);
		addPanelListElements(output, inPanel);
	}


	public void addPanelListElements(ArrayList<PanelListElement> output, ArrayList<LocatedObject2D> inPanel) {
		for(LocatedObject2D imagePanel: inPanel) {
			PanelListElement sPanel = ((ImagePanelGraphic)imagePanel).getSourcePanel();
			if (sPanel!=null)output.add(sPanel);
			}
	}
	
	/**Checks either rows or columns to see if each one displays only a single channel*/
	public boolean singleChannelPer(int type) {
		return getChannelOrder(type)!=null;
	}

	ChannelUseInstructions determineChannelOrder() {
		if (singleChannelPer(ROWS)) {
			return getChannelOrder(ROWS);
		} else
		if (singleChannelPer(COLS)) {
			return getChannelOrder(COLS);
		}
		PanelStackDisplay pm = figure.getPrincipalMultiChannel();
		if(figure.getMultiChannelDisplaysInOrder().size()==1 &&pm.getMultichanalWrapper().nFrames()==1&&pm.getMultichanalWrapper().nSlices()==1)
			return getChannelOrder(PANELS);
		
		return null;
	}
	

	/**checks either the rows of columns of the figure depending on the argument
	  returns the channel order or null if each row or column contains multiple channels*/
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
			boolean sameChannel = panelListElement.originalChanNum==panelListElement2.originalChanNum;
			
			
			
			if(!sameChannel) return null;
			
		}
		if (l.size()==0) continue;
		PanelListElement panelListElement = l.get(0);
		/**if this is the first channel panel of the row/col, adds the chan number to the list*/
		if(!panelListElement.isTheMerge()&&!added) {
			order.add(panelListElement.originalChanNum);
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
	 output.getChanPanelReorder().setOrder(order);
	 output.MergeHandleing=whereIsMerge;
		return output;
	}
	
	public int channelIndexAt(int type, int rowIndex) {
		ArrayList<ArrayList<PanelListElement>> list = getOrderedPanelList(type);
		ArrayList<PanelListElement> l = list.get(rowIndex-1);
		if(l.size()>0)
			return l.get(0).originalChanNum;

		return -1;
	}


	public void updateChannelOrder() {
		
		ChannelUseInstructions cOrder = determineChannelOrder();
		
		for(ChannelUseInstructions c: figure.getChannelUseInfo())
			try {
					c.MergeHandleing=cOrder.MergeHandleing;
					c.getChanPanelReorder().setOrder(cOrder.getChanPanelReorder());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
	}


	public void updateChanOrder(int type) {
		if (singleChannelPer(type) ) {
			updateChannelOrder();
		}
	}

	

	public imageOrderComparator getImageOrderUpdate() {
		return new imageOrderComparator(getDisplaysInLayoutImageOrder());
	}
	
	public ArrayList< PanelStackDisplay> getDisplaysInLayoutImageOrder() {
		ArrayList<PanelListElement> list = getOrderedPanelList();
		ArrayList<PanelStackDisplay> allContainedDisplays = figure.getMultiChannelDisplaysInOrder();
		ArrayList< PanelStackDisplay> displays=new 	ArrayList<PanelStackDisplay>();
		
		for(PanelListElement l: list) {
			if (allContainedDisplays.contains(l.getPanelGraphic().getParentLayer())&&!displays.contains(l.getPanelGraphic().getParentLayer())) 
			{
				PanelStackDisplay parentLayer = (PanelStackDisplay) l.getPanelGraphic().getParentLayer();
				displays.add(parentLayer);
				parentLayer.getSetter().startPoint=list.indexOf(l)+1;
			};
			
			
		}
		return displays;
	}

	public static class imageOrderComparator implements Comparator<PanelStackDisplay> {
		private ArrayList<PanelStackDisplay> newOrder;

		public imageOrderComparator(ArrayList< PanelStackDisplay> newOrder) {
			this.newOrder=newOrder;
		}

		@Override
		public int compare(PanelStackDisplay o1, PanelStackDisplay o2) {
			int indexOf = newOrder.indexOf(o1);
			int indexOf2 = newOrder.indexOf(o2);
			if (indexOf==-1) return 1;
			if (indexOf2==-1) return -1;
		//	IssueLog.log("Comparing "+o1+" "+o2);
		//	IssueLog.log("Comparing "+indexOf+" "+indexOf2);
			return indexOf-indexOf2;
			/**if (indexOf<indexOf2)
					return 1;
			if (indexOf>indexOf2)
				return 1;
			*/
			//return 0;
		}
		
	}
	
}
