/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package standardDialog.colors;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import standardDialog.choices.ComboBoxPanel;

public class ColorComboboxPanel extends ComboBoxPanel implements ColorListChoice, MouseListener {


	
	public static Color[] standardC= new Color[] {Color.white, Color.black, Color.gray, Color.red, Color.green, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.orange, Color.pink,new Color(143, 0, 255), new Color(0,0,0,0), new Color(5,5,5), new Color(250,250,250)};
	public static ArrayList<Color> semitransparentC(Color[] cs, int trans) {
		ArrayList<Color> out = new  ArrayList<Color> ();
		for(Color c:cs) {
			out.add(new Color(c.getRed(), c.getGreen(), c.getBlue(), trans));
		}
		return out;
	}
	
	ColorComboBox box2=null;
	
	
	public ColorComboboxPanel(String labeln, Color[] c, Color innitial) {
		super(labeln, new String[] {"o"}, 0);
		if (c==null) c=standardC;
		ArrayList<Color> colors = new ArrayList<Color> ();
		colors.add(innitial);
		for(Color c1:c) {colors.add(c1);}
		colors.addAll(semitransparentC(new Color[] {Color.red, Color.green, Color.blue}, 140));
		colors.add(Color.black);
		this.box=new ColorComboBox(colors, c.length);
		box2=(ColorComboBox) box;
		box2.setSelectedItem(innitial);
		box.addItemListener(this);
		
		{label.addMouseListener(this);}
		{box.addMouseListener(this);}
		this.addMouseListener(this);
		// TODO Auto-generated constructor stub
	}
	
	public Color getSelectedColor() {
		return box2.getSelectedColor();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Color> getColors() {
		// TODO Auto-generated method stub
		return box2.getColors();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount()==2) {
			this.box2.addUserColor(arg0.getComponent());
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getRainbow() {
		if (box2==null) return 1000;
		return box2.getRainbow();
	}

}
