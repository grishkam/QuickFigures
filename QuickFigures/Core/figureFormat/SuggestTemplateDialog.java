/**
 * Author: Greg Mazo
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 8, 2021
 * Version: 2021.2
 */
package figureFormat;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;

import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

/**
 A dialog that allows the user to select a template from a
 list of visible templates
 */
public class SuggestTemplateDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<TemplateChoice> box;
	JButton defaultTemplateButton=new JButton("Make this the new default");{defaultTemplateButton.addActionListener(this);}
	JButton applyButton=new JButton("Apply this template"); {applyButton.addActionListener(this);}
	
	public  SuggestTemplateDialog() {
		super("Select an example from the list");
		this.setWindowCentered(true);
		addTemplateComboBox() ;
		super.setBonusButtons(defaultTemplateButton, applyButton);
		this.setHideOK(true);
	}
	
	
	/**Adds a combo box*/
	public void addTemplateComboBox() {
		Vector<TemplateChoice> options=new Vector<TemplateChoice>();
		MutateFigure[] shortList = MutateFigure.getShortList();
	
		options.add(new TemplateChoice("Normal", MutateFigure.FONT_12));
		for(int i=0; i<shortList.length; i++) {
			options.add(new TemplateChoice(shortList[i], MutateFigure.FONT_12));
		}
		
		options.add(new TemplateChoice("Wide form", MutateFigure.BORDER_8, MutateFigure.FONT_12));
		options.add(new TemplateChoice("Square", MutateFigure.MERGE_FIRST, MutateFigure.CHANNEL_LABELS_INSIDE,   MutateFigure.TWO_COLUMN, MutateFigure.FONT_12));
		options.add(new TemplateChoice("Merged Figure", MutateFigure.FONT_12, MutateFigure.CHANNEL_LABELS_MERGED_ONLY,MutateFigure.MERGE_ONLY,   
				MutateFigure.TWO_COLUMN,   MutateFigure.CHANNEL_LABELS_MERGED_ONLY, MutateFigure.FONT_12));
	
		
		
		box=new TemplateComboBox(options);
		super.add("choice", new ChoiceInputPanel("Select Example", getTemplateComboBox()));
	}


	/**will return the template that the user selects
	 * @return
	 */
	public static FigureTemplate getTemplate() {
		SuggestTemplateDialog d = new SuggestTemplateDialog();
		//d.setHideOK(true);
		d.setModal(true);
		d.showDialog();
		if(d.wasCanceled())
			return null;
		if(d.wasOKed())
			try {
				return d.getSelectedTemplate();
			
			}catch (Throwable t) {
				IssueLog.log(t);
			}
		
		
		return null;
	}
	
	
	public FigureTemplate getSelectedTemplate() {
		TemplateChoice templateChoice = (TemplateChoice) getTemplateComboBox().getSelectedItem();
		return templateChoice.getUseableTemplate();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		if (arg0.getSource()==applyButton) {
			
			this.wasOKed=true;
			this.setVisible(false);
			
		}
		
		if (arg0.getSource()==defaultTemplateButton) {
			boolean ans = ShowMessage.showOptionalMessage("Saved Default Template", true, "Are you sure?", "", "new template be applied to newly created figures", "you may also apply the default template to existing figures (without removing panels)");
			if(!ans)
				return;
			new  TemplateUserMenuAction( TemplateUserMenuAction.SAVE_TEMPLATE, true).saveDefaultTemplate(this.getSelectedTemplate());
			this.setVisible(false);
		}
		
	}


	public JComboBox<TemplateChoice> getTemplateComboBox() {
		return box;
	}

}
