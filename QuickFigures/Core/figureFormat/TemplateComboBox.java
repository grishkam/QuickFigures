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
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 8, 2021
 * Version: 2021.1
 */
package figureFormat;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_Shapes.SimpleGraphicalObject;

/**a combo box that lets the user choose various objects that are each drawn within the menu*/
public class TemplateComboBox extends JComboBox<TemplateChoice>{
	
	Color backgroundColor = Color.white;
	Color selectionBackGround = new Color(50,50,200);
	
	{this.setRenderer(new GraphicalObjectCellRenerer());}
	private SimpleGraphicalObject selectedGraphic=null;
	private static final long serialVersionUID = 1L;
	ArrayList<SimpleGraphicalObject> objectList=new ArrayList<SimpleGraphicalObject> ();
	
	
	  public TemplateComboBox(Vector<TemplateChoice> items) {
	        super(items);
	    }


	
	
	/**returns the selected graphic, if the selected index is 0 will return null as the index of 0 indicates nothing is selected*/
	public SimpleGraphicalObject getSelectedGraphicalObject() {
		int i=this.getSelectedIndex();
		if (i<1) return null;
		 selectedGraphic=objectList.get(this.getSelectedIndex()-1);
		return selectedGraphic;
	}
	

	

	
	/**a cell renderer that paints the graphics as icons in a combo box*/
	class GraphicalObjectCellRenerer  implements ListCellRenderer<TemplateChoice> {
		BasicComboBoxRenderer render=new BasicComboBoxRenderer();
		
		public Color currentColor=Color.black;

		public  Component	getListCellRendererComponent(JList<? extends TemplateChoice> list, TemplateChoice value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel output = (JLabel) render.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			JLabel copy = value.getComboBoxItem(cellHasFocus,  isSelected);
			output.setIcon(copy.getIcon());
				
			output.setText(copy.getText());
			return output;
			
				}
		
		
	}







	

}
