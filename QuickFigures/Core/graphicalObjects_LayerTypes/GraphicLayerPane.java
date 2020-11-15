package graphicalObjects_LayerTypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import fieldReaderWritter.SVGExportable;
import fieldReaderWritter.SVGExporter;
import fieldReaderWritter.SVGExporter_GraphicLayer;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.CordinateConverter;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.LayerStructureChangeListenerList;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.KnowsSetContainer;
import graphicalObjects.KnowsTree;
import graphicalObjects.LayerSpecified;
import iconGraphicalObjects.IconUtil;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import layersGUI.LayerStructureChangeListener;
import layersGUI.HasTreeBranchIcon;
import logging.IssueLog;
import objectDialogs.LayerPaneDialog;
import undo.UndoManagerPlus;
import utilityClasses1.ArraySorter;
import utilityClasses1.ItemSwapper;
import utilityClassesForObjects.Hideable;
import utilityClassesForObjects.Keyed;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.Mortal;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.ShowsOptionsDialog;

public class GraphicLayerPane implements GraphicLayer, ZoomableGraphic, Serializable, IllustratorObjectConvertable, ShowsOptionsDialog, ObjectContainer, KnowsSetContainer, Mortal, HasTreeBranchIcon,KnowsTree, SVGExportable, ItemSwapper<ZoomableGraphic> {

	/**
	 * 
	 */
	
	private LayerStructureChangeListenerList listenerlist=new LayerStructureChangeListenerList();
	private static final long serialVersionUID = 1L;
	public String name="base layer";
	public Object key=0;
	protected ArrayList<ZoomableGraphic> theGraphics=new ArrayList<ZoomableGraphic>();
	transient LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>  tree;
	private GraphicLayer parent;
	transient GraphicSetDisplayContainer graphicSetContainer;
	transient boolean dead=false;
	protected String description= "A Normal Layer";
	protected String notes=null;
	
	
	
	public GraphicLayerPane(String name) {
		this.name=name;
		key=generateRandomKey();
	}
	
	public String generateRandomKey() {
		int i=(int)(Math.random()*1000000);
		char c=(char)(Math.random()*1000000);
		char c2=(char)(Math.random()*1000000);
		return ""+c+i+c2;
	}
	
	public void addLayerStructureChangeListener(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> listener) {
		if (!getListenerlist().contains(listener));
		getListenerlist().add(listener);
	}
	
	public void removeLayerStructureChangeListener(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> listener) {
		getListenerlist().remove(listener);
	}
	
	public void setTree(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>  t) {
		tree=t;
		for(GraphicLayer l:this.getSubLayers()) {
			l.setTree(t);
		}
		for(ZoomableGraphic l:this.getItemArray()) {
			if (l instanceof KnowsTree) {
			((KnowsTree) l).setTree(t);
			}
		}
	}
	
	public LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>  getTree() {
		return tree;
	}
	
	/**the internal array that stores the direct contents*/
	protected ArrayList<ZoomableGraphic> getGraphicsSync() {
		return theGraphics;
	}
	
	/**swaps the array positions of the items*/
	public void swapItemPositions(ZoomableGraphic z1, ZoomableGraphic z2) {
		ArraySorter<ZoomableGraphic> sorter = new ArraySorter<ZoomableGraphic>();
		sorter.swapObjectPositionsInArray(z1, z2, getGraphicsSync());
		//if (tree.isTreeDebugMode()) {IssueLog.log("Item "+z1+" swapped places with "+z2, "layer "+this+" will report change to tree");}
		if (tree!=null) tree.itemsSwappedInContainer(this, z1,z2);
		getListenerlist().itemsSwappedInContainer(this, z1, z2);
	}
	
	public void moveItemForward(ZoomableGraphic z1) {
		ArraySorter<ZoomableGraphic> sorter = new ArraySorter<ZoomableGraphic>();
		ZoomableGraphic z2 = sorter.getItemAfter(z1, getGraphicsSync());
		swapItemPositions(z1,z2);
	}
	public void moveItemBackward(ZoomableGraphic z1) {
		ArraySorter<ZoomableGraphic> sorter = new ArraySorter<ZoomableGraphic>();
		ZoomableGraphic z2 = sorter.getItemBefore(z1, getGraphicsSync());
		swapItemPositions(z1,z2);
	}
	
	/**Reorders the objects in the layer to match a given order*/
	public void setOrder(ArrayList<ZoomableGraphic> neworder) {
		new ArraySorter<ZoomableGraphic>().setOrder(getGraphicsSync(), neworder, this);
	}

	
	public GraphicLayerPane(String name, Object key) {
		this.name=name;
		this.key=key;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setKey(String key) {
		this.key=key;
	}
	
	/**returns the array of graphics. not including those of the
	  sublayers*/
	public ArrayList<ZoomableGraphic> getItemArray() {
		 return theGraphics;
	}
	
	/**returns all the drawn graphics, including those inside of sub-layers
	 * but not the sub-layers themselves. does not include those inside of groups*/
	@Override
	public ArrayList<ZoomableGraphic> getAllGraphics() {
		ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic>();
		for(ZoomableGraphic z:getGraphicsSync()) {
			addGraphicToArray(z, output);
		}
		
		return output;
	}
	
	/**if an the item is a graphic layer, this method adds its contents to the array
	  otherwise, it simply adds the item to the array*/
	public static void addGraphicToArray(ZoomableGraphic z, ArrayList<ZoomableGraphic> array ) {
		if (z instanceof GraphicLayer) {
			array.addAll(((GraphicLayer)z).getAllGraphics());
		}
		else array.add(z);
		
		/**some objects hold others inside of them*/
		if (z instanceof GraphicHolder) {
			array.addAll(((GraphicHolder)z).getAllHeldGraphics());
		}
		/**this next part implements the select in group the select in group*/
		if (GraphicGroup.treatGroupsLikeLayers && z instanceof ZoomableGraphicGroup) {
			ZoomableGraphicGroup z2=(ZoomableGraphicGroup) z; 
			array.remove(z);
			array.addAll(z2.getTheLayer().getAllGraphics());
		}
	}


	@Override
	public ArrayList<ZoomableGraphic> getGraphics(Object... key) {
		return getAllGraphics();
	}
	

	@Override
	public void add(ZoomableGraphic z) {
		if (z==this||z==null) return;
		
		if (z instanceof GraphicLayer) {
			
			GraphicLayer gl=(GraphicLayer) z;
			
			gl.setTree(tree);
			if (gl.hasItem(this)) return;
		}
		
		if (z instanceof ZoomableGraphicGroup) {
			ZoomableGraphicGroup gl=(ZoomableGraphicGroup) z;
			gl.getTheLayer().setTree(tree);
			if (gl.getTheLayer().hasItem(this)) return;
		}
		
		if (z instanceof KnowsTree) {
			KnowsTree k=(KnowsTree) z;
			k.setTree(tree);
		}
		
		//no longer used
		/**
		if (z instanceof layerSpecified) {
			layerSpecified l=(layerSpecified)z;
			Object wantedLayerKey = l.getLayerKey();
			
			ZoomableGraphic sublayer = this.getItemWithKey(wantedLayerKey );
			
			if (sublayer instanceof GraphicLayer) {
				GraphicLayer gl=(GraphicLayer) sublayer;
				gl.add(z);
				if (tree!=null) tree.itemAddedToContainer(gl, z); 
				getListenerlist().itemAddedToContainer(gl, z);
				return;
			}
			
			
		}*/
		
		
		addItemToLayer(z);
		
	}
	
	public void addItemToLayer(ZoomableGraphic z) {
		assignKey(z);
		if (theGraphics.contains(z)) return;
		if (z instanceof KnowsParentLayer) {
			KnowsParentLayer l=(KnowsParentLayer) z;
			l.setParentLayer(this);
		}
		if (z instanceof KnowsSetContainer) {
			KnowsSetContainer kn=(KnowsSetContainer) z;
			kn.setGraphicSetContainer(this.getGraphicSetContainer());
		}
		addItemToArray(z);
		if (tree!=null) tree.itemAddedToContainer(this, z); 
		getListenerlist().itemAddedToContainer(this, z);
	}
	
	protected void addItemToArray(ZoomableGraphic z) {
		theGraphics.add(z);
		
	}
	


	public void removeItemFromLayer(ZoomableGraphic z) {
		if (z instanceof KnowsParentLayer) {
			KnowsParentLayer l=(KnowsParentLayer) z;
			l.setParentLayer(null);
		}
		removeItemFromArray(z);
		
		if (tree!=null) tree.itemRemovedFromContainer(this, z);//new location will move to after this was called if fails
		getListenerlist().itemRemovedFromContainer(this, z);
	}
	
	protected void removeItemFromArray(ZoomableGraphic z) {
		while (theGraphics.contains(z)) theGraphics.remove(z);
	}


	void assignKey(ZoomableGraphic z) {
		if (z instanceof LayerSpecified) {
			LayerSpecified l=(LayerSpecified)z;
			l.setLayerKey(getKey());
		}
	}
	

	@Override
	public synchronized void remove(ZoomableGraphic z) {
		/**removes it if it is inside any subsection*/
		for(ZoomableGraphic z2:this.getSubLayers()) {
			if (z2 instanceof GraphicLayer) {
				GraphicLayer gc = (GraphicLayer)z2;
				
				if (gc.hasItem(z)) {
				gc.remove(z);
			//	IssueLog.log("removal done on sublayer.removed item "+z);
				if (tree!=null) tree.itemRemovedFromContainer(gc, z);
				getListenerlist().itemRemovedFromContainer(gc, z);
				}
				
				
			}	
		}
		removeItemFromLayer(z);
		//IssueLog.log("removal done inside "+this+ "  removed: "+z);
		
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
	
		for(ZoomableGraphic z: getGraphicsSync()) try {
			if (z==null||isHidden(z)) continue;
			assignKey(z);
			z.draw(graphics, cords);
		}
		catch (Throwable t) {IssueLog.log(t);}
		
		
	}
	
	boolean isHidden(ZoomableGraphic z) {
		if (!(z instanceof Hideable)) {return false;}
		Hideable h=(Hideable) z;
		return h.isHidden();
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public String toString() {
		return ""+getName();
	}
	
	@Override
	public Object getKey() {
		// TODO Auto-generated method stub
		return key;
	}
	@Override
	public void setKey(Object key) {
		this.key=key;
	}
	
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		ArtLayerRef sub = aref.createSubRef();
		sub.setName(getName());
		for(ZoomableGraphic layer: getGraphicsSync()) try{
			if (layer instanceof  IllustratorObjectConvertable) {
				IllustratorObjectConvertable ills = ( IllustratorObjectConvertable)layer;
				ills.toIllustrator(sub);
			}
		}catch (Throwable t) {
			
		}
		return sub;
	}

	@Override
	public void showOptionsDialog() {
		// TODO Auto-generated method stub
		new LayerPaneDialog(this);
	}

	/**Called when the user tries to move objects between layers*/
	public boolean canAccept(ZoomableGraphic z) {
		if (z==this) return false;
		if (this.getParentLayer()!=null&&!getParentLayer().canAccept(z)) {
			return false;//returns false if a parent of this layer rejects the item
		}
		return true;
	}

	/**returns true is the item is either in this layer or in a sublayer or subgroup*/
	@Override
	public boolean hasItem(ZoomableGraphic z) {
		if (theGraphics.contains(z)) return true;
		for(ZoomableGraphic layer: getSubLayers()) {
			if (layer instanceof GraphicLayer ){
				if (((GraphicLayer)layer).hasItem(z)) return true;
			} 
		}
		return false;
	}

	
	/**not yet implemented*/
	@Override
	public boolean hasItemWithKey(Object key) {
		return getItemWithKey( key)!=null;
	}

	/**returns the item inside with a given key*/
	@Override
	public ZoomableGraphic getItemWithKey(Object key) {
		
		for(ZoomableGraphic item:getGraphicsSync()) {
			if (item instanceof Keyed) {
				Keyed k=(Keyed) item;
				if (k.getKey().equals(key)) return item;
			}
		}
		
		for(ZoomableGraphic item:this.getSubLayers()) {
			if (item instanceof GraphicLayer) {
				GraphicLayer k=(GraphicLayer) item;
				ZoomableGraphic item2 = k.getItemWithKey(key);
				if (item2!=null) return item2;
			}
			
		}
		return null;
	}

	
	/**returns all sublayers. this includes those inside of groups and other sublayers*/
	public ArrayList<GraphicLayer> getSubLayers() {
		ArrayList<GraphicLayer> output = new ArrayList<GraphicLayer>();
		for(ZoomableGraphic g: this.getItemArray()) {
			if (g instanceof ZoomableGraphicGroup) {
				 ZoomableGraphicGroup g2=(ZoomableGraphicGroup) g;
				 g=g2.getTheLayer();
			}
			
			if (g instanceof GraphicLayer) {
				GraphicLayer gl=(GraphicLayer) g;
				output.add(gl);
				output.addAll(gl.getSubLayers());
				
			}
			
		}
		return output;
	}
	
	/**gets all the items inside including sublayers*/
	public ArrayList<ZoomableGraphic> getObjectsAndSubLayers() {
		ArrayList<ZoomableGraphic>  output = new 	ArrayList<ZoomableGraphic>();
		output.addAll(getSubLayers());
		output.addAll(getAllGraphics());
		return output;
	}

	@Override
	public void takeFromImage(LocatedObject2D roi) {
		if (roi instanceof ZoomableGraphic) {
			remove((ZoomableGraphic) roi);
		}
	}

	@Override
	public void addRoiToImage(LocatedObject2D roi) {
		if (roi instanceof ZoomableGraphic) {
			add((ZoomableGraphic) roi);
		}
	}

	@Override
	public void addRoiToImageBack(LocatedObject2D roi) {
		if (roi instanceof ZoomableGraphic) {
			add((ZoomableGraphic) roi);
		}
		
	}

	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		ArrayList<LocatedObject2D> output = new  ArrayList<LocatedObject2D>();
		ArrayList<ZoomableGraphic> graphics = getAllGraphics();
		for(ZoomableGraphic item :graphics) {
			if (item instanceof LocatedObject2D) {
				if (output.contains(item)) continue;
				output.add((LocatedObject2D)item);
			}
		}
		return output;
	}

	@Override
	public LocatedObject2D getSelectionObject() {
		if (this.getTree()!=null) {
			ZoomableGraphic item = getTree().getSelectedLayer();
			if (item instanceof LocatedObject2D) return (LocatedObject2D) item;
			}
		return null;
	}
	
	
	public GraphicLayer getSelectedContainer() {
		if (this.getTree()!=null) {
			GraphicLayer item = getTree().getSelectedLayer();
			if (item!=null) return (GraphicLayer) item;
			}
		return this;
	}
	
	public void treeEliminated() {
		this.setTree(null);
		for (GraphicLayer l:this.getSubLayers()) {
			l.setTree(null);
		}
	}

	/**Within this layer, puts object il at the given index. starts from 0*/
	public void moveItemToIndex(ZoomableGraphic il, int index) {
		if (index>=theGraphics.size()||index<0) { 
			swapmoveObjectPositionsInArray(il, theGraphics.get(theGraphics.size()-1));
			return;
			}
		swapmoveObjectPositionsInArray(il, theGraphics.get(index));
	}
	
	/**performs swaps until the position of i1 in the array is at that of i2*/
	public void swapmoveObjectPositionsInArray(ZoomableGraphic i1,ZoomableGraphic i2) {
		int ind1 = theGraphics.indexOf(i1);
		int ind2 = theGraphics.indexOf(i2);
		if (ind1<0||ind2<0) return;
		if (ind1>ind2) {
			while (ind1>ind2) {
				ZoomableGraphic i3=theGraphics.get(ind1-1);
				while (i3==null) {ind1--; i3=theGraphics.get(ind1-1);}
				swapItemPositions(i1, i3);
				 ind1--;
				 if (i3==i2) return;
			}
			
		}
		
		else {
			while (ind1<ind2) {
				ZoomableGraphic i3=theGraphics.get(ind1+1);
				while (i3==null) {ind1++; i3=theGraphics.get(ind1+1);}
				swapItemPositions(i1, i3);
				 ind1++;
				 if (i3==i2) return;
			}
			
		}
		
		//theGraphics.set(ind1, i2);
		//theGraphics.set(ind2, i1);
		
	}

	public GraphicLayer getParentLayer() {
		return parent;
	}

	public void setParentLayer(GraphicLayer parent) {
		this.parent = parent;
	}

	

	@Override
	public void kill() {
		for(ZoomableGraphic i: this.getItemArray()) {
			if (i instanceof Mortal) {
				Mortal m=(Mortal) i;
				m.kill();
			}
		}
		dead=true;
	}


	@Override
	public boolean isDead() {
		return dead;
	}

	public LayerStructureChangeListenerList getListenerlist() {
		if (listenerlist==null) listenerlist=new LayerStructureChangeListenerList ();
		return listenerlist;
	}

	public void setListenerlist(LayerStructureChangeListenerList listenerlist) {
		this.listenerlist = listenerlist;
	}
	
	static Color  folderColor= new Color(140,180, 200);
	public static Icon createDefaultTreeIcon(boolean open) {
		return IconUtil.createFolderIcon(open, folderColor);
	}
	
	@Override
	public Icon getTreeIcon(boolean open) {
		
		return createDefaultTreeIcon(open);
	/**
		if (open) return defaultLeaf;// TODO Auto-generated method stub
		return defaultLeaf2;*/
	}

	
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	
	public void setDescription(String nextString) {
		description=nextString;
		
	}
	


	@Override
	public SVGExporter getSVGEXporter() {
		// TODO Auto-generated method stub
		return new SVGExporter_GraphicLayer(this);
	}
	
	
	
	public GraphicSetDisplayContainer getGraphicSetContainer() {
		
		return graphicSetContainer;
	}
	
	@Override
	public void setGraphicSetContainer(GraphicSetDisplayContainer gc) {
		graphicSetContainer=gc;
		for(ZoomableGraphic g: this.getItemArray()) {
			if (g instanceof KnowsSetContainer) {
				KnowsSetContainer g2=(KnowsSetContainer) g;
				g2.setGraphicSetContainer(gc);
			}
		}
	}
	
	@Override
	public void updateDisplay() {
		if( this.graphicSetContainer==null) return;
		graphicSetContainer.updateDisplay();
	}

	@Override
	public boolean canRelease(ZoomableGraphic z) {
		return true;
	}
	
	
	public UndoManagerPlus getUndoManager() {
		return new CurrentFigureSet().getCurrentlyActiveDisplay().getUndoManager();
	}

	/**returns the top level parent layer (the one with no parent of its own)*/
	@Override
	public GraphicLayer getTopLevelParentLayer() {
		GraphicLayer output = this;
		while(output.getParentLayer()!=null) output=output.getParentLayer();
		return output;
	}
	
}

	
		
	


