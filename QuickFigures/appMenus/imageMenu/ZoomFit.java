package imageMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;

import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import standardDialog.GraphicDisplayComponent;
import standardDialog.StandardDialog;

public class ZoomFit implements MenuItemForObj {

String type="fit";

public ZoomFit() {
	
}

public ZoomFit(String type) {
	this.type=type;
}



public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
	if (diw==null) return;
	if (type.contains("Set")) {
		Double z = StandardDialog.getNumberFromUser("Set Zoom Level", diw.getZoomLevel());
		diw.setZoomLevel(z);
	} else
	if (type.contains("fit"))diw.zoomOutToFitScreen();
	else diw.zoom(type);
	diw.updateWindowSize();
	diw.updateDisplay();
}

public String getCommand() {return "Zoom out to fit"+type;}
public String getNameText() {
	if (type.startsWith("Out")) return "Out (press '-')";
	if (type.startsWith("In")) return "In (press '=' or '+')";
	if (type.startsWith("Set")) return "Set Zoom Level";
	return "View All";
	}
public String getMenuPath() {return "Image<Zoom";}
@Override
public Icon getIcon() {
	// TODO Auto-generated method stub
	return getItemIcon();
}

public GraphicDisplayComponent getItemIcon() {
	GraphicGroup gg=new GraphicGroup();
	
	
	
	
	RectangularGraphic oval2 = new RectangularGraphic(new Rectangle(8,6, 2, 9));
	oval2.setStrokeWidth(1);
	oval2.setStrokeColor(Color.black);
	oval2.setFillColor(Color.DARK_GRAY);
	oval2.setFilled(true);
	oval2.setAngle(Math.PI/4);
	gg.getTheLayer().add(oval2);
	oval2.setAntialize(true);
	
	CircularGraphic oval1 = new CircularGraphic(new Rectangle(0,0, 7, 7));
	oval1.setStrokeWidth((float)1.5);
	 oval1.setDashes(new float[] {});
	 oval1.setAntialize(true);
	oval1.setStrokeColor(Color.GRAY);
	gg.getTheLayer().add(oval1);
	
	
	TextGraphic tg=new TextGraphic(getCurrentLabel() );
	tg.setLocation(9, 11); 
	
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

String getCurrentLabel() {
	
	if (type.startsWith("Out")) return " -";
	if (type.startsWith("In")) return "+";

	if (type.toLowerCase()=="fit") return "[ ]";
	
	return null;
}


}