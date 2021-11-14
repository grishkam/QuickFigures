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
 
 * 
 */
package standardDialog.choices;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.junit.Test;

import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import standardDialog.StandardDialog;
import testing.VisualTest;

/**
 
 * 
 */
public class GraphicComboBoxTest extends VisualTest {

	/**displays a window with a combo box, user must click on it to confirm that appearance and function are normal*/
	@Test
	public void test() {
		
			JFrame ff = new JFrame("frame");
			ff.add(new JButton("button"));
			ArrayList<SimpleGraphicalObject> ac = new ArrayList<SimpleGraphicalObject> ();
			{ac.add(new TextGraphic("An obect"));
			
			RectangularGraphic rect =RectangularGraphic.filledRect( new Rectangle(0,0,20,35));
			rect.setStrokeColor(Color.blue);
			rect.setStrokeWidth(10);
			ac.add(rect);
			
			/**to confirm that the shape is seen in the icon even if the location is far from origin*/
			rect =CircularGraphic.blankOval(new Rectangle(120,100,40,35), Color.green, CircularGraphic.CHORD_ARC);
			rect.setStrokeWidth(5);
			ac.add(rect);
			
			ac.add(new BarGraphic());
			
			ac.add(new TextGraphic("this is a text item"));}
			
			GraphicComboBox sb = new GraphicComboBox(ac, Color.orange);
			ff.add(sb);
			ff.pack();
			
			ff.setVisible(true);
			StandardDialog.center(ff);
			ff.pack();
			
			comboBoxVisualTest(ff, sb);
			
			ff.setVisible(false);
	}

	

}
