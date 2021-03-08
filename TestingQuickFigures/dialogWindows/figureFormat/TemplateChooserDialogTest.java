/**
 * Author: Greg Mazo
 * Date Modified: Feb 10, 2021
 * Version: 2021.1
 */
package figureFormat;

import org.junit.Test;

import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.AttachmentPosition;
import logging.IssueLog;
import testing.DialogTester;

/**
 this is not a fully automated test. 
 user is meant to visually confirm that the dialog window appears correctly
 */
public class TemplateChooserDialogTest {

	/**displays a template choser dialog.
	 * A few example objects are added to a layer as its target (mock of an actual figure)
	 * */
	@Test
	public void test() {


		GraphicLayerPane oc1=new GraphicLayerPane("");
			TextGraphic firstRowLabel = new TextGraphic("Hello #1");
			firstRowLabel.setAttachmentPosition(AttachmentPosition.defaultRowSide());
			oc1.addItemToImage(firstRowLabel);
			
			TextGraphic secondRowLabel = new TextGraphic("Hi #2");
			secondRowLabel.setAttachmentPosition(AttachmentPosition.defaultRowSide());
			oc1.addItemToImage(secondRowLabel);
			
			DefaultLayoutGraphic layout1 = new DefaultLayoutGraphic();layout1.setName("layout 1");
			oc1.addItemToImage(layout1);
			oc1.addItemToImage(new DefaultLayoutGraphic());
			BarGraphic bar = new BarGraphic(); bar.setLengthInUnits(12);bar.setName("Scale bar 1");
			oc1.addItemToImage(bar);
			oc1.addItemToImage(new BarGraphic());
			
			FigureTemplate template = new FigureTemplate();
			TemplateChooserDialog dialog = new TemplateChooserDialog(template, oc1);
			dialog.setWindowCentered(true);
			dialog.setTitle("Manually check dialog appearance and close to finish test");
			
			dialog.setModal(false);
			dialog.showDialog();;
			
			DialogTester.testInputPanelApperance(dialog, 1);
			
			dialog.setTemplateToChoices();
			
			/**to confirm that first row label in the layer is automatically chosen*/
			assert(template.getRowLabelPicker().getModelItem()==firstRowLabel);
			
			/**to confirm that first row label in the layer is automatically chosen*/
			assert(template.getScaleBar().getModelItem()==bar);
			
			/**to confirm that first row label in the layer is automatically chosen*/
			assert(template.getLayoutChooser().getModelItem()==layout1);
			
			/**long enough for the user to try clicking an item*/
			IssueLog.waitSeconds(25);
	}

}
