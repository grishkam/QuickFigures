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
 * Date Modified: Jan 16, 2021
 * Version: 2023.1
 */
package standardDialog.choices;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.TextGraphic;
import layersGUI.GraphicCellRenderer;
import layersGUI.TextGraphicListCellComponent;
import standardDialog.graphics.GraphicComboBoxIcon;

/**a combo box that lets the user choose various objects that are each drawn within the menu*/
public class GraphicComboBox extends JComboBox<SimpleGraphicalObject> implements UserSelectable{
	
	Color backgroundColor = Color.white;
	Color selectionBackGround = new Color(50,50,200);
	
	{this.setRenderer(new GraphicalObjectCellRenerer());}
	private SimpleGraphicalObject selectedGraphic=null;
	private static final long serialVersionUID = 1L;
	ArrayList<SimpleGraphicalObject> objectList=new ArrayList<SimpleGraphicalObject> ();
	
	public GraphicComboBox(ArrayList<? extends SimpleGraphicalObject> c, Color background) {
		if (background!=null) backgroundColor =background;
		addItem(getNullComponent() );
		for(SimpleGraphicalObject ci:c) {this.addItem(ci);objectList.add(ci);}
	}
	
	/**returns the selected item, may return null*/
	@Override
	public Object getSelectedItem() {
		if (this.getSelectedIndex()==0) return null;
		else return super.getSelectedItem();
	}

	
	
	/**returns the selected graphic, if the selected index is 0 will return null as the index of 0 indicates nothing is selected*/
	public SimpleGraphicalObject getSelectedGraphicalObject() {
		int i=this.getSelectedIndex();
		if (i<1) return null;
		 selectedGraphic=objectList.get(this.getSelectedIndex()-1);
		return selectedGraphic;
	}
	

	@Override
	public int getSelectionNumber() {
		return super.getSelectedIndex();
	}
	
	@Override
	public void setSelectionNumber(int index) {
		super.setSelectedIndex(index);
		
	}

	
	/**What is drawn when a null appears on the list*/
	private TextGraphic getNullComponent() {
		TextGraphic tg = new TextGraphic("none");
	
		tg.setFont(tg.getFont().deriveFont((float)10));
		return tg;
	}
	
	/**a cell renderer that paints the graphics as icons in a combo box*/
	class GraphicalObjectCellRenerer extends JLabel implements ListCellRenderer<SimpleGraphicalObject> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public Color currentColor=Color.black;

		public  Component	getListCellRendererComponent(JList<? extends SimpleGraphicalObject> list, SimpleGraphicalObject value, int index, boolean isSelected, boolean cellHasFocus) {
			
				try {
					boolean noneSelected=false;
					
					SimpleGraphicalObject item =null;
					if (index!=0) item=value;
					if (value==null||index==0) {
						item=getNullComponent();
						noneSelected=true;
					}
					
					String s="none";
					
					if (item!=null) s=item.toString();
					
					item.deselect();
					this.setText(s);
					
					
					if (isSelected) {
			            
			            
						this.setBackground(selectionBackGround);
			           this.setOpaque(true);
			        } else {
			            setBackground(backgroundColor);
			            setForeground(list.getForeground());
			        }
					
					if (index!=0)
						this.setIcon( new GraphicComboBoxIcon(item, isSelected&&index!=-1, selectionBackGround));
					else setIcon(null);
					
					
					
				if(index==-1&&getSelectedItem()==null) {
					this.setIcon(null);
					this.setText("no item selected");
					noneSelected=true;
				}
				if(index==-1&&getSelectedItem()!=null) {
					Component output = new GraphicCellRenderer().getTreeCellRendererComponent(null, getSelectedItem(), false, false, true, 0, false);
					if (output instanceof TextGraphicListCellComponent && getSelectedItem() instanceof TextGraphic) {
						TextGraphicListCellComponent t=(TextGraphicListCellComponent) output;
						t.setIcon(null);//icon not needed for text
					}
					
					return output;
				}
				
				/**test in an icon followed by more text would be repetitive*/
				if (value instanceof TextGraphic &&!noneSelected)
					this.setText(""); 
				
					return this;
				} catch (Exception e) {
					
					return this;
				}
				
			
				}
		
		
	}

	

}
