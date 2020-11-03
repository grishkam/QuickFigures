package addObjectMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import iconGraphicalObjects.IconUtil;
import utilityClasses1.ArraySorter;

public class GroupAdder extends BasicGraphicAdder {

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicGroup gg = new GraphicGroup();
		ArrayList<ZoomableGraphic> i = this.selector.getSelecteditems();
		gc.add(gg);
	
		ArraySorter.removeThoseOfClass(i, GraphicLayer.class);
		int index = -1;
		for(ZoomableGraphic item:i) {
			if (item instanceof KnowsParentLayer) {
				KnowsParentLayer it=(KnowsParentLayer) item;
				
				if(it.getParentLayer()!=null)
					{
					index=gc.getItemArray().indexOf(item);
					it.getParentLayer().remove(item);
					
					}
				if(gg.getParentLayer()!=null)
				gg.getTheLayer().add(item);
			}
		
		}
		if (index>-1) gc.moveItemToIndex(gg, index);
		return gg;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "add group";
	}

	@Override
	public String getMessage() {
		return "Add Shape Group";
	}
	
	public Icon getIcon() {
		return IconUtil.createFolderIcon(false, GraphicGroup.defaultFolderColor);
	}

}
