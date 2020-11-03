package selectedItemMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import iconGraphicalObjects.IconUtil;
import standardDialog.GraphicDisplayComponent;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;

public class SelectAllButton extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D targetType;
	@Override
	public String getMenuCommand() {
		return "Select Same Type (Press 'a')";
	}
	
	public SelectAllButton(LocatedObject2D typeSpeci) {
		this.targetType=typeSpeci;
	}

	@Override
	public void run() {
		
		
		ArrayList<ZoomableGraphic> selItems = this.getSelector().getSelecteditems();
		ArrayList<LocatedObject2D> all = this.getSelector().getImageWrapper().getLocatedObjects();
		if(targetType!=null) {
			ArrayObjectContainer.selectAllOfType(all, targetType);
			return;
		}
		
		for(ZoomableGraphic sel: selItems) {
			if(sel==null) continue;
			if(sel instanceof LocatedObject2D)
				ArrayObjectContainer.selectAllOfType(all, (LocatedObject2D) sel);
		}

	}
	
	public Icon getIcon() {
		return new GraphicDisplayComponent(IconUtil.createAllIcon("all")  );
	}
	


}
