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
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package icons;


import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import logging.IssueLog;

/**maintains a list of icons. 
  in most cases this will contain 
   a normal button icon, button pressed icon, and a button rollover icon  */
public class IconSet {
	
	public static final int ROLLOVER_ICON = 2, PRESSED_ICON = 1, BUTTON_ICON = 0;
	private Image[] icons;//for icons that consist of images
	 private Icon[] icons2;
	 
	 private Image defaultIcon=null;
	 
	
	 /**Given the name of a  file in the same .jar package as this file,
	  returns an image */
	 public Image loadImage(String name) {
		return loadLocalImage(name);
		
	}
	 
	
	public IconSet() {}
	public IconSet(String...strings ) {
		setIcons(strings );
	}
	public IconSet(Icon...strings ) {
		setIcons(strings );
	}
	
	
	private void setIcons(Icon[] strings) {
		// TODO Auto-generated method stub
		icons2=strings;
		for(int i=0; i<strings.length; i++) {
			setIcon( i,strings[i]);
		}
	}


	public void setIcons(String...strings ) {
		icons=new Image[strings.length];
		icons2=new Icon[strings.length] ;
		for(int i=0; i<strings.length; i++) try {
			setIcon( i,strings[i]);
		} catch (Throwable t) {IssueLog.logT(t);}
	}
	
	/**sets the icon based on a string that tells the code how to find the icon*/
	public void setIcon(int i, String st){
		icons[i]=loadImage(st);
		icons2[i]=new ImageIcon(icons[i]);
	}
	
	public void setIcon(int i, Icon st){
		icons2[i]=st;
	}
	
	
	
	/**Given the name of a  file in the same .jar package as this file,
	  returns an image with all the file.*/
	 public static Image loadLocalImage(String name) {
		
		try {
			Image io=ImageIO.read( IconSet.class.getClassLoader().getResourceAsStream( name));
			
			return io;
		} catch (Exception e) {
			IssueLog.log("failed to find image in path:  "+name);
			return null;
		}
		
	}

		public Image getImageForIcon(int i) {
			if (i>=icons.length) return getDefaultIcon();
			return icons[i];
		}
		
		public Icon getIcon(int i) {
			if (i>=icons2.length) return new ImageIcon(getDefaultIcon());
			return icons2[i];
		}
		
		/**sets the standard, pressed and rollover icons */
		public void setItemIcons(AbstractButton item) {
			item.setIcon(getIcon(BUTTON_ICON));
			item.setPressedIcon(getIcon(PRESSED_ICON));
			item.setRolloverIcon(getIcon(ROLLOVER_ICON));
			
		}


		private Image getDefaultIcon() {
			if (defaultIcon==null) defaultIcon=loadLocalImage("icons/Blank.jpg");
			return defaultIcon;
		}
		
	
}
