package imageMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import imageDisplayApp.ZoomOptions;
import standardDialog.GraphicDisplayComponent;
import standardDialog.StandardDialog;
import storedValueDialog.StoredValueDilaog;

/**This class represents a menu item for the user to zoom in or out*/
public class ZoomFit implements MenuItemForObj {

	public static final String USER_SET="Set", SCREEN_FIT="fit", IN="In", OUT="Out", OPTIONS="options";
	
String type="fit";


public ZoomFit() {
	
}

public ZoomFit(String type) {
	
	this.type=type;
}



public void performActionDisplayedImageWrapper(DisplayedImage diw) {
	if (diw==null) return;
	if (type.equals(OPTIONS)) {
		new StoredValueDilaog(ZoomOptions.current).showDialog();
		return;
	}
	
	if (type.contains(USER_SET)) {
		Double z = StandardDialog.getNumberFromUser("Set Zoom Level", diw.getZoomLevel());
		diw.setZoomLevel(z/100);
	} else
	if (type.contains(SCREEN_FIT))diw.zoomOutToDisplayEntireCanvas();
	else diw.zoom(type);
	
	/**updates the window to account for the new zoom level*/
	diw.updateWindowSize();
	diw.updateDisplay();
}

public String getCommand() {return "Zoom out to fit"+type;}
public String getNameText() {
	if (type.equals(OPTIONS)) return "Zoom Options";
	if (type.startsWith("Out")) return "Out (press '-')";
	if (type.startsWith("In")) return "In (press '=' or '+')";
	if (type.startsWith("Set")) return "Set Zoom Level";
	return "View All";
	}
public String getMenuPath() {return "Image<Zoom";}


@Override
public Icon getIcon() {
	return getItemIcon();
}
/**creates an icon for the zoom level menu items*/
public GraphicDisplayComponent getItemIcon() {
	GraphicGroup gg=new GraphicGroup();
	
	
	
	
	RectangularGraphic oval2 = new RectangularGraphic(new Rectangle(9,7, 2, 9));
	oval2.setStrokeWidth(1);
	oval2.setStrokeColor(Color.black);
	oval2.setFillColor(Color.DARK_GRAY);
	oval2.setFilled(true);
	oval2.setAngle(Math.PI/4);
	gg.getTheLayer().add(oval2);
	oval2.setAntialize(true);
	
	CircularGraphic oval1 = new CircularGraphic(new Rectangle(1,2, 7, 7));
	oval1.setStrokeWidth((float)1.5);
	 oval1.setDashes(new float[] {});
	 oval1.setAntialize(true);
	oval1.setStrokeColor(Color.GRAY);
	gg.getTheLayer().add(oval1);
	
	
	TextGraphic tg=new TextGraphic(getCurrentLabel() );
	tg.setLocation(10, 13); 
	
	tg.setFont(tg.getFont().deriveFont((float) 14).deriveFont(Font.BOLD));
	if (type.equals("fit")) { 
		tg.setFont(tg.getFont().deriveFont((float) 10).deriveFont(Font.BOLD));
		tg.moveLocation(2,0);
	}
	gg.getTheLayer().add(tg);
	
	 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
	 output.setRelocatedForIcon(false);
	
	 return output;
}

/**The text that will appear on the zoom level icon*/
String getCurrentLabel() {
	
	if (type.startsWith("Out")) return " -";
	if (type.startsWith("In")) return "+";

	if (type.toLowerCase()=="fit") return "[ ]";
	
	return null;
}


}