/*******************************************************************************
 * Copyright (c) 2025 Gregory Mazo
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
 * Date Created: Dec 28, 2025
 * Date Modified: Dec 28, 2025
 * Copyright (C) 2025 Gregory Mazo
 * 
 */
/**
 A menu item for crop dialog that lets the user reuse recent sizesd
 */
package objectDialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartPopupJMenu;

/**
 
 * 
 */
public class RecentCropAreaMenu extends SmartPopupJMenu {


	private CroppingDialog cropDialog;
	public static ArrayList<Dimension2D> recent_dimensions=new ArrayList<Dimension2D>();
	
	static {recent_dimensions.add(new Dimension(300,200));}
	
	public static void addRecentArea(Rectangle r) {
		Dimension d1 = new Dimension(r.getBounds().width, r.getBounds().height);
		if(recent_dimensions.contains(d1) || hasDim(d1))
			return;
		if(recent_dimensions.size()>10) recent_dimensions.remove(recent_dimensions.get(9));
		recent_dimensions.add(0, d1);
	}
	
	/**
	 * @param d1
	 * @return
	 */
	private static boolean hasDim(Dimension d1) {
		for(Dimension2D d: recent_dimensions) {
			if(d.getWidth()==d1.width && d.getHeight()==d1.getHeight()) {
				return(true);
			}
		}
		return false;
	}

	/**
	 * @param string
	 */
	public RecentCropAreaMenu(CroppingDialog cropDialog) {
		
		this.cropDialog=cropDialog;
		
		for(Dimension2D dim: recent_dimensions) {
			this.add(new RecentCropDimMenuItem(dim));
			
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 
	 * 
	 */
public class RecentCropDimMenuItem extends BasicSmartMenuItem {

	

		private String dimension_text;
		private Dimension2D the_dims;

		/**
		 * @param dim
		 */
		public RecentCropDimMenuItem(Dimension2D dim) {
			dimension_text="w="+((int)dim.getWidth()) + " h="+ ((int)dim.getHeight()) ;
			this.the_dims =dim;
			super.setText(dimension_text);
			super.setName(dimension_text);
			this.addActionListener(this);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**Called when this menu item is pressed*/
		public void actionPerformed(ActionEvent e) {
			
			cropDialog.setNumber("width", the_dims.getWidth());
			cropDialog.setNumber("height", the_dims.getHeight());
			
			cropDialog.update_image_from_dialog();
			cropDialog.repaint();
		}

}

	/**
	 quick method meant to get this to work
	 */
	public void showAsPopup(ActionEvent e) {
	
			JPopupMenu popup = this;
			
			
			popup.pack();
			Object source = e.getSource();
			
				Component triggerButton;
				{triggerButton=(Component) source;}
				popup.show(triggerButton, 0, 0);
			
		
	}


}
