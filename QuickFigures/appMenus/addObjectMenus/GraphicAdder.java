package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import selectedItemMenus.LayerSelector;

public interface GraphicAdder {
		public ZoomableGraphic add(GraphicLayer gc);
		public String getCommand();
		public String getMessage();
		//public void setDisplay(GraphicSetDisplayContainer cont) ;
		public void setSelector(LayerSelector selector);
		public Character getKey();
		public Icon getIcon();
}
