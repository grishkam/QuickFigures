package graphicalObjects_LayoutObjects;

import java.util.ArrayList;

import applicationAdapters.GenericImage;
import applicationAdapters.ImageWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.LayerStructureChangeListener;
import menuUtil.PopupMenuSupplier;
import plasticPanels.ObjectListPanelLayout;
import popupMenusForComplexObjects.ObjectPanelLayoutPanelMenu;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;

public class ObjectDefinedLayoutGraphic extends SpacedPanelLayoutGraphic implements LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	{layout=new ObjectListPanelLayout();}
	public ObjectListPanelLayout getPanelLayout() {
		if (this.layout instanceof ObjectListPanelLayout) return (ObjectListPanelLayout) this.layout;
	return null;	
	}
	@Override
	public void itemsSwappedInContainer(GraphicLayer gc, ZoomableGraphic z1,
			ZoomableGraphic z2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void itemRemovedFromContainer(GraphicLayer gc, ZoomableGraphic z) {
		if (z instanceof ImagePanelGraphic) {
			LocatedObject2D l=(LocatedObject2D) z;
			this.getPanelLayout().removeObject(l);
		}
	}
	@Override
	public void itemAddedToContainer(GraphicLayer gc, ZoomableGraphic z) {
		if (z instanceof ImagePanelGraphic) {
			LocatedObject2D l=(LocatedObject2D) z;
			this.getPanelLayout().addObject(l);
		}
		
	}
	@Override
	public GraphicLayer getSelectedLayer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setParentLayer(GraphicLayer l) {
		removeItemsInlayerFromList(this.getParentLayer());
		if (this.getParentLayer()!=null) {
			getParentLayer().removeLayerStructureChangeListener(this);
		}
		this.addItemsInlayerToList(l);
		if (l!=null) l.addLayerStructureChangeListener(this);
		super.setParentLayer(l);
	}
	
	
	public void removeItemsInlayerFromList(GraphicLayer g) {
		if (g==null) return;
		for(ZoomableGraphic gg: g.getItemArray()) {
			if (gg instanceof ImagePanelGraphic)
			this.getPanelLayout().removeObject((LocatedObject2D) gg);
		}
	}
	
	public void addItemsInlayerToList(GraphicLayer g) {
		if (g==null) return;
		for(ZoomableGraphic gg: g.getItemArray()) {
			if (gg instanceof ImagePanelGraphic)
			this.getPanelLayout().addObject((LocatedObject2D) gg);
		}
	}
	

	public ImageWrapper generateStandardImageWrapper() {
	ArrayList<ZoomableGraphic> parent2 = new ArrayList<ZoomableGraphic>();
	if (getParentLayer()!=null) parent2=this.getParentLayer().getAllGraphics();
		GenericImage wrapper = new GenericImage(new ArrayObjectContainer(parent2));
		this.getPanelLayout().setWrapper(wrapper);
		this.getPanelLayout().getWrapper().takeFromImage(this);
		for(LocatedObject2D loc: this.getPanelLayout().getArray()) {
			getPanelLayout().getWrapper().takeFromImage(loc);
		}
		return wrapper;
	}
	
	public ImageWrapper generateEditNonpermissiveWrapper() {
			GenericImage wrapper = new GenericImage(new ArrayObjectContainer(new ArrayList<ZoomableGraphic>()));
			this.getPanelLayout().setWrapper(wrapper);
			this.getPanelLayout().getWrapper().takeFromImage(this);
			for(LocatedObject2D loc: this.getPanelLayout().getArray()) {
				getPanelLayout().getWrapper().takeFromImage(loc);
			}
			return wrapper;
		}
	
	public ImageWrapper generateRemovalPermissiveImageWrapper() {
			if (this.getParentLayer() instanceof ObjectContainer)
			{
				GenericImage wrapper = new GenericImage((ObjectContainer) this.getParentLayer());
				this.getPanelLayout().setWrapper(wrapper);
				if (!this.getEditor().getObjectHandler().getNeverRemove().contains(this))
				this.getEditor().getObjectHandler().getNeverRemove().add(this);
				for(LocatedObject2D loc: this.getPanelLayout().getArray()) {
					getEditor().getObjectHandler().getNeverRemove().add(loc);
				}
				return wrapper;
			}
			return null;
	}
	
	
	public PopupMenuSupplier getMenuSupplier(){
		return new  ObjectPanelLayoutPanelMenu(this);
	}

	

}


