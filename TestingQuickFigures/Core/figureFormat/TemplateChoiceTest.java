/**
 * Author: Greg Mazo
 * Date Modified: Apr 8, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package figureFormat;

import java.awt.Window;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import testing.VisualTest;

/**
 
 * 
 */
public class TemplateChoiceTest extends VisualTest {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
			SuggestTemplateDialog sd = new SuggestTemplateDialog();
			Window j=new JFrame();
			JComboBox<TemplateChoice> templateComboBox = sd.getTemplateComboBox();
			j.add(templateComboBox);
			j.pack();
			super.comboBoxVisualTest(j, templateComboBox);
			
		}
	}


