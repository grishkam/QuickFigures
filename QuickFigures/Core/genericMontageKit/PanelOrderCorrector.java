package genericMontageKit;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.LayoutSpaces;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;

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
	private FigureOrganizingLayerPane figure;

	public PanelOrderCorrector(FigureOrganizingLayerPane f) {
		this.figure=f;
	}
	
	/**Returns the panel list elements in an order that is determined by 
	 * their location within the layout (Panel #1,2,3) rather than any list
	  */
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
	
	/**returns an ordered set of panel list elements from each row or columns.
	 * The type given may be ROWS, COLUMNS or PANELS @see MontageLayoutSpaces*/
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

	/**finds the panel list elements with panels inside of the given rectangle and adds them to the arraw*/
	public void addElementsInPanel(ArrayList<PanelListElement> output, Rectangle2D rect) {
		ArrayList<LocatedObject2D> inPanel = new BasicObjectListHandler().getAllClickedRoi(figure, rect.getCenterX(), rect.getCenterY(), ImagePanelGraphic.class);
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

	ChannelUseInstructions determineChannelOrder() {
		if (singleChannelPer(ROWS)) {
			return getChannelOrder(ROWS);
		} else
		if (singleChannelPer(COLS)) {
			return getChannelOrder(COLS);
		}
		ImageDisplayLayer pm = figure.getPrincipalMultiChannel();
		if(figure.getMultiChannelDisplaysInOrder().size()==1 &&pm.getMultiChannelImage().nFrames()==1&&pm.getMultiChannelImage().nSlices()==1)
			return getChannelOrder(PANELS);
		
		return null;
	}
	

	/**checks either the rows, panels or columns of the figure depending on the argument
	  returns a set of channel use instructions containing the order of the channels
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
	 output.getChanPanelReorder().setOrder(order);
	 output.MergeHandleing=whereIsMerge;
		return output;
	}
	
	/**looks for panel list elements that are present in the row, column or panel viven*/
	public int channelIndexAt(int type, int rowIndex) {
		ArrayList<ArrayList<PanelListElement>> list = getOrderedPanelList(type);
		ArrayList<PanelListElement> l = list.get(rowIndex-1);
		if(l.size()>0)
			return l.get(0).targetChannelNumber;

		return -1;
	}


	/**determines how the channel panels actually appear in the figure
	  updates the channel use instructions for the figure to match the actual locations of
	  the channel panels*/
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

	

	public ImageOrderComparator getImageOrderUpdate() {
		return new ImageOrderComparator(getDisplaysInLayoutImageOrder());
	}
	
	public ArrayList< ImageDisplayLayer> getDisplaysInLayoutImageOrder() {
		ArrayList<PanelListElement> list = getOrderedPanelList();
		ArrayList<ImageDisplayLayer> allContainedDisplays = figure.getMultiChannelDisplaysInOrder();
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
			/**if (indexOf<indexOf2)
					return 1;
			if (indexOf>indexOf2)
				return 1;
			*/
			//return 0;
		}
		
	}
	
}
