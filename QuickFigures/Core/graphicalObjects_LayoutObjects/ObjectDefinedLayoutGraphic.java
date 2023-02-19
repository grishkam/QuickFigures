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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package graphicalObjects_LayoutObjects;

import java.util.ArrayList;

import applicationAdapters.GenericImage;
import applicationAdapters.ImageWorkSheet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.LayerStructureChangeListener;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.plasticPanels.ObjectListPanelLayout;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.ObjectPanelLayoutPanelMenu;

/**A layout whose panels are determined the the locations of actual objects.
 * */
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
	

	public ImageWorkSheet generateStandardImageWrapper() {
	ArrayList<ZoomableGraphic> parent2 = new ArrayList<ZoomableGraphic>();
	if (getParentLayer()!=null) parent2=this.getParentLayer().getAllGraphics();
		GenericImage wrapper = new GenericImage(new ArrayObjectContainer(parent2));
		this.getPanelLayout().setVirtualWorkSheet(wrapper);
		this.getPanelLayout().getVirtualWorksheet().takeFromImage(this);
		for(LocatedObject2D loc: this.getPanelLayout().getArray()) {
			getPanelLayout().getVirtualWorksheet().takeFromImage(loc);
		}
		return wrapper;
	}
	
	public ImageWorkSheet generateEditNonpermissiveWrapper() {
			GenericImage wrapper = new GenericImage(new ArrayObjectContainer(new ArrayList<ZoomableGraphic>()));
			this.getPanelLayout().setVirtualWorkSheet(wrapper);
			this.getPanelLayout().getVirtualWorksheet().takeFromImage(this);
			for(LocatedObject2D loc: this.getPanelLayout().getArray()) {
				getPanelLayout().getVirtualWorksheet().takeFromImage(loc);
			}
			return wrapper;
		}
	
	public ImageWorkSheet generateRemovalPermissiveImageWrapper() {
			if (this.getParentLayer() instanceof ObjectContainer)
			{
				GenericImage wrapper = new GenericImage((ObjectContainer) this.getParentLayer());
				this.getPanelLayout().setVirtualWorkSheet(wrapper);
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


