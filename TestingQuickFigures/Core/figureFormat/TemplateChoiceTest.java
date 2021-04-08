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

import static org.junit.Assert.*;

import java.util.Vector;

import javax.swing.JFrame;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import graphicalObjects_LayerTypes.GraphicGroup;
import logging.IssueLog;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**
 
 * 
 */
public class TemplateChoiceTest {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
			new SuggestTemplateDialog().showDialog();;
			IssueLog.waitSeconds(50);
		}
	}


