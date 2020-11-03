package uiForAnimations;

import animations.KeyFrameCompatible;
import animations.BasicKeyFrame;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.ZoomableGraphic;
import standardDialog.NumberInputPanel;

public class KeyFrameMove extends BasicTimeLineOperator{
	
	int motion=0;
	
	

	@Override
	public void run() {
		
		motion= (int) NumberInputPanel.getNumber("How many frames forward (- numbers for back)", 0, 1, false, null);
		
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
			
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Move Key Frame";
	}
	
	/**removes the selected item, period. it the item is mortal,
	   it will call its kill() method which should let some listeners know
	   of its demise*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		
		
		
		
		if (selectedItem instanceof KeyFrameCompatible ) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			int frame = new CurrentSetInformerBasic().getCurrentlyActiveDisplay().getCurrentFrame();
			if (m.getAnimation()==null) return;
			BasicKeyFrame frame2 = m.getOrCreateAnimation().isKeyFrame(frame);
			frame2.setFrame(frame+motion);
		}
		
		
		
		
	}
	

}
