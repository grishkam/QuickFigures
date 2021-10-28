/**
 * Author: Greg Mazo
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 7, 2021
 * Version: 2021.2
 */
package figureFormat;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;

import addObjectMenus.FigureAdder;
import appContext.CurrentAppContext;
import channelLabels.ChannelLabelTextGraphic;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**
This class stores an option for a possible figure template
and maintains an preview of that that figure would look like
 */
public class TemplateChoice   {
	
	
	String name="";
	FigureTemplate template;
	
	/**Text for preview to show*/
	private JLabel item;
	
	/**which mutate ones*/
	MutateFigure[] special=new MutateFigure[] {};
	private boolean mockMade;
	private FigureOrganizingLayerPane figure;
	private JLabel comboBoxLabel;
	boolean twoImages=false;
	
	public TemplateChoice(FigureTemplate t) {
		template=t;
	}
	
	
public TemplateChoice(MutateFigure... special) {
		this.special=special;
		if(special.length>0) name=special[0].name().toLowerCase().replace("_", " ");
		for(MutateFigure m: special) {
			if(m.needsSecondImage)
				twoImages=true;//must display two images for preview in this case
		}
	}

public TemplateChoice(String name,  MutateFigure... special) {
	this(special);
	this.name=name;
}
	
	
	public JLabel getComboBoxItem(boolean focus, boolean selected) {
		if(item==null)
			item = generateComboBoxComponent() ;
			comboBoxLabel.setBackground(item.getForeground());
		 if (focus) {
			 comboBoxLabel.setBackground(Color.DARK_GRAY);
			 };
		if (selected) {
			 comboBoxLabel.setBackground(Color.blue);
			 };
		return item;
		
		
	}
	
	public JLabel generateComboBoxComponent() {
		 comboBoxLabel = new JLabel(name);
		 comboBoxLabel.setIcon(generateIconObject());
		 return comboBoxLabel;
	}
	
	
	
	GraphicObjectDisplayBasic<GraphicGroup> generateIconObject() {
		
		createMock();
		GraphicGroup output = new GraphicGroup();
		GraphicLayerPane layerPaneWithFigure = new GraphicLayerPane("figure");
		FigureAdder added = new FigureAdder(true);
		added.autoFigureGenerationOptions.ignoreSavedTemplate=true;
		
		
		/**creates a mock figure for displaying what the template looks like*/
		figure = added.add(layerPaneWithFigure, getMockFilePath(1));
		if (this.twoImages)
			figure.nextMultiChannel(getMockFilePath(2), null);
		
		changeChannelLabels(figure, "CH ");
		if(template!=null)
			template.applyTemplateToLayer(figure);
		for(MutateFigure m: special) 
			m.mutate(figure);
		
		GraphicObjectDisplayBasic<GraphicGroup> component = new GraphicObjectDisplayBasic<GraphicGroup>();
		output.getTheInternalLayer().addItemToLayer(layerPaneWithFigure);
		component.setCurrentDisplayObject(output);
		
		;
		return component;
		
	}
	

	

	/**
	 * @param mockIndex
	 */
	protected void createMock() {
		if (!mockMade)
			for(int mockIndex: new int[] {1,2})
				CurrentAppContext.getMultichannelContext().getDemoExample(false, getMockFilePath(mockIndex), 3, mockIndex, 1);
		mockMade=true;
	}

	/**returns the path for saving the mock images as files
	 * @param mockIndex
	 * @return
	 */
	protected String getMockFilePath(int mockIndex) {
		return DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/"+"Row "+mockIndex+".tiff";
	}

	
	/**Changes the row labels for the figure into a more generic form
	 * @param figure
	 */
	public static void changeChannelLabels(FigureOrganizingLayerPane figure, String gene) {
		/**changes the channel labels*/
		
		ArrayList<ChannelLabelTextGraphic> allLabels = figure.getPrincipalMultiChannel().getChannelLabelManager().getAllLabels();
		for(int i=0; i<allLabels.size(); i++)
				{
			ChannelLabelTextGraphic l= allLabels.get(i);
			if(l.isThisMergeLabel())
				continue;
			l.changeText(gene+(i+1));
			}
	}


	/**
	 * 
	 */
	public FigureTemplate getUseableTemplate() {
		FigureTemplate output = new FigureTemplate();
		output.setToFigure(figure);
		
		return output;
		
	}
	
	
	
}
