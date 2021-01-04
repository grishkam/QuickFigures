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
 * Version: 2021.1
 */
package imageMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.ZoomOptions;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import storedValueDialog.StoredValueDilaog;

/**This class implements the functions for a menu item that allows the user to zoom in or out*/
public class ZoomFit extends BasicMenuItemForObj {

	public static final String USER_SET="Set", SCREEN_FIT="fit", IN="In", OUT="Out", OPTIONS="options";
	
String type=SCREEN_FIT;


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
		Double z = StandardDialog.getNumberFromUser("Set Zoom Level", diw.getZoomLevel(), false, ZoomOptions.current);
		diw.setZoomLevel(z/100);
	} else
	if (type.contains(SCREEN_FIT))diw.zoomOutToDisplayEntireCanvas();
	else diw.zoom(type);
	
	/**updates the window to account for the new zoom level. unless the zoom options have been changed*/
	diw.updateWindowSize();
	diw.updateDisplay();
}

public String getCommand() {return "Zoom out to fit"+type;}
public String getNameText() {
	if (type.equals(OPTIONS)) return "Zoom Options";
	if (type.startsWith(OUT)) return "Out (press '-')";
	if (type.startsWith(IN)) return "In (press '=')";
	if (type.startsWith(USER_SET)) return "Set Zoom Level";
	return "View All";
	}
public String getMenuPath() {return "Edit<Zoom";}


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
	if (type.equals(SCREEN_FIT)) { 
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
	
	if (type.startsWith(OUT)) return " -";
	if (type.startsWith(IN)) return "+";

	if (type.toLowerCase().contentEquals(SCREEN_FIT)) return "[ ]";
	
	return null;
}




}