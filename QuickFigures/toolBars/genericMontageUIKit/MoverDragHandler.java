package genericMontageUIKit;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

import addObjectMenus.FileImageAdder;
import addObjectMenus.ImageAndlayerAdder;
import appContext.CurrentAppContext;
import appContext.ImageDPIHandler;
import channelMerging.PanelStackDisplay;
import externalToolBar.BasicDragHandler;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import imageDisplayApp.ImageAndDisplaySet;
import layersGUI.GraphicSetDisplayTree;
import layersGUI.GraphicTreeTransferHandler;
import logging.IssueLog;
import selectedItemMenus.SVG_GraphicAdder2;
import ultilInputOutput.ForDragAndDrop;
import undo.AbstractUndoableEdit2;
import undo.CompoundEdit2;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.Selectable;
import utilityClassesForObjects.TakesLockedItems;

/***/
public class MoverDragHandler extends BasicDragHandler {
	private BasicToolBit tool ;

	public MoverDragHandler(BasicToolBit roi_Mover) {
		this.tool=roi_Mover;
	}

	public void drop(ImageAndDisplaySet displaySet, DropTargetDropEvent arg0) {
		AbstractUndoableEdit2 undo=null;
		ArrayList<File> file = ForDragAndDrop.dropedFiles(arg0);
		if (file!=null) undo=handleFileListDrop(displaySet, arg0.getLocation(), file);
		else
		try{
			
		Transferable t = arg0.getTransferable();
		GraphicSetDisplayTree tree=null;
		
		
		IssueLog.log("Warning: When moving items between trees, also move the linked items ");
		
		tree = (GraphicSetDisplayTree) t.getTransferData(GraphicTreeTransferHandler.selectedItemListFlavor);
	
		ArrayList<ZoomableGraphic> items = tree.getSelecteditems();
		
			if (items instanceof ArrayList<?> ) {
				ArrayList<?> items2=(ArrayList<?>) items;
				for(Object item: (ArrayList<?>)items) {
					if (item instanceof ZoomableGraphic) {
						moveItemToSet((ZoomableGraphic) item, displaySet);
					}
					
					if (item instanceof TakesLockedItems) {
						TakesLockedItems taker=(TakesLockedItems) item;
						for(LocatedObject2D i: taker.getLockedItems()) {
							if (items2.contains(i)) continue;
							moveItemToSet((ZoomableGraphic) i, displaySet);
						}
					}
				}
				
				
			}
			
			if (tree!=null) {
				
				tree.getSetDisplayContainer().updateDisplay();;
				}
		
		}
		catch (Throwable t) {IssueLog.log(t);
		
		}
		
		new CurrentSetInformerBasic().addUndo(undo);;
	}
	
	public void select(ZoomableGraphic z) {
		if (z instanceof Selectable) ((Selectable) z).select();
		if (z instanceof GraphicLayer) {
			
			for(ZoomableGraphic l: ((GraphicLayer) z).getAllGraphics()) {
				select(l);
			};
		}
	}
	
	void moveItemToSet(ZoomableGraphic z, ImageAndDisplaySet displaySet) {
		select(z);
		if (displaySet.getImageAsWrapper().getGraphicLayerSet().hasItem(z))
					return;
		
		
		GraphicLayer oldparent = z.getParentLayer();
		GraphicLayer newLayer = displaySet.getImageAsWrapper().getGraphicLayerSet().getSelectedContainer();
		
		if (newLayer.canAccept(z)) {
			oldparent.remove(z);
			newLayer.add(z);
			
		}
		
	}
	
	public void dragOver(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		super.dragOver(displaySet, arg0);
		
	
		
		LocatedObject2D roi2 = getObjectAtPoint(displaySet, position);
	
		
		if (roi2 instanceof PanelLayoutGraphic) {
			PanelLayoutGraphic p=(PanelLayoutGraphic) roi2;
			int i = p.getPanelLayout().getNearestPanelIndex(position.getX(),  position.getY());
			Rectangle2D panel = p.getPanelLayout().getPanel(i);
		
			
			displaySet.getImageAsWrapper().getSelectionManagger().setSelectionGraphic(p);
			displaySet.getImageAsWrapper().getSelectionManagger().select(panel, 1);
			
		}  else {
			displaySet.getImageAsWrapper().getSelectionManagger().removeSelections();
			
		}
		displaySet.updateDisplay();
		
	}

	private LocatedObject2D getObjectAtPoint(ImageAndDisplaySet displaySet, Point2D position) {
		return tool.getObject(displaySet.getImageAsWrapper(), (int)position.getX(), (int) position.getY());
	}
	
	
	/**What to do if the user drops a file list on a certain canvas location.
	  If there is a readable multichannel. Creates the montage.
	  If there are single images. Adds them to the 
	 * @return */
	
	public CompoundEdit2 handleFileListDrop(ImageAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {
		Point2D location2 = imageAndDisplaySet.getConverter().unTransformP(location);
		LocatedObject2D roi2 = getObjectAtPoint(imageAndDisplaySet, location2);
		PanelLayoutGraphic layout=null;
		int startIndex=-1;
		if (roi2 instanceof PanelLayoutGraphic) {
			layout=(PanelLayoutGraphic) roi2;
			if (layout instanceof MontageLayoutGraphic)
			startIndex=((BasicMontageLayout) layout.getPanelLayout()).getPanelIndex((int)location2.getX(), (int)location2.getY());
			}
		
		
		
		
		
		GraphicLayer layer = imageAndDisplaySet.getImageAsWrapper().getGraphicLayerSet();
		ArrayList<ImagePanelGraphic> addedPanels=new ArrayList<ImagePanelGraphic>();
		
		if(roi2 instanceof KnowsParentLayer) {
			KnowsParentLayer km=(KnowsParentLayer) roi2;
			if(km.getParentLayer()!=null) layer=km.getParentLayer();
		}
		
		
		
		boolean multiChannelOpen=true;
		CompoundEdit2 undo = new CompoundEdit2();
		for(File f: file) {
			
			
			if (ForDragAndDrop.getExtension(f).toLowerCase().equals("svg")) {
				new SVG_GraphicAdder2().addFromFile(f, layer);
				continue;
			}
			
			/**marks all non-tif files as non multichannels. */
			if (!ForDragAndDrop.getExtension(f).equals("tif")) {
				multiChannelOpen=false;
			}
			
			if (ForDragAndDrop.getExtension(f).equals("tiff")) {
				multiChannelOpen=true;
			}
			if (isMicroscopeFormat(f)) {
				multiChannelOpen=true;
			}
			else if (layer instanceof PanelStackDisplay) {
				multiChannelOpen=true;
			}
			else if (layer instanceof FigureOrganizingLayerPane) {
				multiChannelOpen=true;
			}
			else
			if (multiChannelOpen&&!isMicroscopeFormat(f)) {
				
				MultichannelDisplayLayer testOpen = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromUserSelectedImage(true, f.getAbsolutePath());
				//IssueLog.log("perform test open "+testOpen);
				if(testOpen==null||testOpen.getMultichanalWrapper()==null ) {
					multiChannelOpen=false;
				}
				else
				if (testOpen.getMultichanalWrapper().getStackSize()>1 ||testOpen.getMultichanalWrapper().getPixelWrapperForSlice(1,1,1).getBitsPerPixel()==16){} 
				else {multiChannelOpen=false;}
				if (testOpen!=null)testOpen.getSlot().hideImage();
					
			}
			
		
			if (multiChannelOpen) {
				AbstractUndoableEdit2 handleMultiChannelStackDrop = handleMultiChannelStackDrop(f,imageAndDisplaySet, layer, location2);
				undo.addEditToList(
				handleMultiChannelStackDrop
				);
				
			} else 
			{
				ImagePanelGraphic imageadded = handleImageFileDrop(layer, f, location2, f.getAbsolutePath().equals("PNG"));
				if(imageadded==null) {
					MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(true, f.getAbsolutePath());
					for(ZoomableGraphic i:item.getAllGraphics()) {
						if(i instanceof ImagePanelGraphic) imageadded=(ImagePanelGraphic) i;
					}
				}
				if (layout!=null &&imageadded!=null) {
					Rectangle2D rect = layout.getPanelLayout().getNearestPanel( location2.getX(),  location2.getY());
					imageadded.setLocation(new Point2D.Double(rect.getX(), rect.getY()));
				}
				if(imageadded!=null) addedPanels.add(imageadded);
				}
			
			imageAndDisplaySet.getImageAsWrapper().getSelectionManagger().removeSelections();
		}
		
		
		/**If adding many single image panels, takes care of the issue*/
		if ((addedPanels.size()>1|| ((roi2 instanceof MontageLayoutGraphic)&&(addedPanels.size()>0))) &&(!multiChannelOpen)) {
			Rectangle rect = addedPanels.get(0).getBounds();
			boolean moveToNewLayer=false;
			GraphicLayer newLayer=null;
			int startingPanelindex = 1;
				BasicMontageLayout ml;
				MontageLayoutGraphic mlg;
				if (roi2 instanceof MontageLayoutGraphic) {
					 mlg=(MontageLayoutGraphic) roi2;
					 ml=mlg.getPanelLayout();
					 startingPanelindex = ml.getPanelIndex((int)location2.getX(), (int)location2.getY());
				} else {
					moveToNewLayer=true;
					newLayer=new GraphicLayerPane("new layer for panels");
					layer.add(newLayer);
					
					ml = new BasicMontageLayout(addedPanels.size(),1,  rect.width, rect.height, 2,2, true);
					
					
					ml.setLabelSpaces(10, 5, 10, 5);
					mlg = new MontageLayoutGraphic(ml);
					mlg.moveLocation(location2.getX(), location2.getY());
					//layer.add(mlg);
					//layer.moveItemToIndex(mlg, 0);
					newLayer.add(mlg);
					
					
				}
			
			for(int i=0; i<addedPanels.size(); i++) {
				
				double x = ml.getPanel(i+startingPanelindex).getMinX();
				double y = ml.getPanel(i+startingPanelindex).getMinY();
				ImagePanelGraphic z = addedPanels.get(i);
				z.setLocation(x, y);
				if(moveToNewLayer) {
					layer.removeItemFromLayer(z);
					newLayer.add(z);
				}
			} 
			mlg.resizeLayoutToFitContents();
		}
		
		tool.resizeCanvas(imageAndDisplaySet);
		imageAndDisplaySet.updateDisplay();
		
		return undo;
	}

	public static boolean isMicroscopeFormat(File f) {
		String extension = ForDragAndDrop.getExtension(f);
		String[] forms = new String[] {"zvi", "czi", "lif", "dv", "lei"};
		for(String form: forms) {
			if(extension.toLowerCase().equals(form))
				return true;
		}
		return false;
	}
	
	public static boolean isImageFormat(File f) {
		String extension = ForDragAndDrop.getExtension(f);
		String[] forms = new String[] {"tif", "tiff", "jpeg", "png", "jpg"};
		for(String form: forms) {
			if(extension.toLowerCase().equals(form))
				return true;
		}
		return false;
	}
	
	private AbstractUndoableEdit2 handleMultiChannelStackDrop(File f, ImageAndDisplaySet imageAndDisplaySet, GraphicLayer layer , Point2D location2) {
		CompoundEdit2 undo=new CompoundEdit2();
		layer = findValidLayer(layer);
		int startIndex=-1;
		if (layer instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane l=(FigureOrganizingLayerPane) layer;
			MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(true, f.getAbsolutePath());
			BasicMontageLayout ml = l.getLayout();
			l.getMontageLayoutGraphic().generateCurrentImageWrapper();
			startIndex=l.getMontageLayout().getPanelIndex((int)location2.getX(), (int) location2.getY());
			if (item==null) return null;
			if (startIndex>0)
				{
				//IssueLog.log("Start index clicked is "+startIndex);
				//IssueLog.log("Start index clicked is "+l.getMontageLayout().getEditor().indexOfFirstEmptyPanel(ml, startIndex, startIndex));
				int numberOfEmptyNeeded = l.getPrincipalMultiChannel().getPanelManager().getStack().getChannelUseInstructions().estimageNPanels(item.getMultichanalWrapper());
				//IssueLog.log("need x empty "+numberOfEmptyNeeded);
				startIndex=l.getMontageLayout().getEditor().indexOfFirstEmptyPanel(ml, numberOfEmptyNeeded, startIndex-1);
				//if(startIndex<0) startIndex=ml.nPanels()-1;
				}
			//IssueLog.log("Empty index is "+startIndex);
			undo = l.nextMultiChannel(item, startIndex);
			
			for(ZoomableGraphic g:item.getAllGraphics()) {
				select(g);
			}
			l.getMontageLayoutGraphic().select();
			l.getMontageLayoutGraphic().generateCurrentImageWrapper();
			return undo;
		}
		
		
		FigureOrganizingLayerPane aa = new ImageAndlayerAdder(true).add(layer, f.getAbsolutePath());
		aa.getMontageLayoutGraphic().moveLayoutAndContents(location2.getX(), location2.getY());
		
		imageAndDisplaySet.updateDisplay();
		aa.fixLabelSpaces();
		
		return new UndoAddItem(layer, aa);
		//return aa;
	}

	/**finds a layer that can accept a figure organizing layer panel*/
	private GraphicLayer findValidLayer(GraphicLayer layer) {
		FigureOrganizingLayerPane layer2 = FigureOrganizingLayerPane.findFigureOrganizer(layer);
		if (layer2==null) return layer;
		return layer2;
		/**if (layer instanceof MultichannelImageDisplay) { 
			if (layer.getParentLayer() instanceof FigureOrganizingLayerPane) layer=layer.getParentLayer();
			if (layer.getParentLayer() instanceof FigureOrganizingLayerPane) layer=layer.getParentLayer();
			if (layer.getParentLayer() instanceof FigureOrganizingLayerPane) layer=layer.getParentLayer();
			if (layer.getParentLayer() instanceof FigureOrganizingLayerPane) layer=layer.getParentLayer();
			if (layer.getParentLayer() instanceof FigureOrganizingLayerPane) layer=layer.getParentLayer();
		}
		return layer;*/
	}
	
	ImagePanelGraphic handleImageFileDrop(GraphicLayer layer, File f,Point2D location2, boolean hasAlpha) {
		ImagePanelGraphic image = new FileImageAdder(hasAlpha).getImage(f);
		if (image==null)return null;
		image.setScale(ImageDPIHandler.ratioFor300DPI());
		layer.add(image);
		image.setLocation(location2);
		return image;
	}
	
	
	public void dragEnter(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		super.dragEnter(displaySet, arg0);
		
		
	}
}
