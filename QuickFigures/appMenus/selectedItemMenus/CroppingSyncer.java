package selectedItemMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ImagePanelGraphic;
import objectDialogs.CroppingDialog;

public class CroppingSyncer extends BasicMultiSelectionOperator{


	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Set Cropping Of Image";
	}



	@Override
	public void run() {	
		CroppingDialog cd = new CroppingDialog();
		cd.setArray(array);
		ArrayList<ImagePanelGraphic> pan = cd.getImagepanels();
		if (pan.size()==0) return;
		cd.showDialog(pan.get(0));
		
	}
	public Icon getIcon() {
		return ImagePanelGraphic.createImageIcon();
	}
	
	public String getMenuPath() {
		
		return "Advanced";
	}

}
