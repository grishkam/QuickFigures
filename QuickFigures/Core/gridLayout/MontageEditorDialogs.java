package gridLayout;
import java.awt.Point;

import logging.IssueLog;
import standardDialog.BooleanInputPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;




public class MontageEditorDialogs implements MontageSpaces {
	
	public void showGeneralEditorDialog( GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
		if (basicMontageLayout==null) return;
		StandardDialog gd = new StandardDialog("Layout Editor", true);
		gd.setWindowCentered(true);
		gd.setModal(true);
			addGeneralEditorFieldsToDialog(gd, basicMontageLayout);
			gd.showDialog();
		

		if (gd.wasOKed()) {
			performEditBasedOnDialog(gd, me, basicMontageLayout);
			basicMontageLayout.setMontageProperties();
		}
	}
	
	
	public void showSpecialSpaceEditorDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
		gd.add("Space Above Montage",new NumberInputPanel("Space Above ", basicMontageLayout.specialSpaceWidthTop, 0));
		gd.add("Space Below Montage",new NumberInputPanel("Space Below ", basicMontageLayout.specialSpaceWidthBottom, 0));
		gd.add("Space Left of Montage",new NumberInputPanel("Space Left ", basicMontageLayout.specialSpaceWidthLeft, 0));
		gd.add("Space right Montage", new NumberInputPanel("Space Right ", basicMontageLayout.specialSpaceWidthRight, 0));
		gd.showDialog();
		
		if (gd.wasOKed()) {
			me.setTopSpecialSpace(basicMontageLayout, (int)gd.getNumber("Space Above Montage"));
			me.setBottomSpecialSpace(basicMontageLayout, (int)gd.getNumber("Space Below Montage"));
			me.setLeftSpecialSpace(basicMontageLayout, (int)gd.getNumber("Space Left of Montage"));
			me.setRightSpecialSpace(basicMontageLayout, (int)gd.getNumber("Space right Montage"));
		 basicMontageLayout.setMontageProperties();
		}
		
		
		
	}
	
	void addColumnAndRowFieldToDialog(StandardDialog gd, BasicMontageLayout basicMontageLayout) {
		gd.add("Columns",new NumberInputPanel("Columns", basicMontageLayout.nColumns(), 0));
		gd.add("Rows",new NumberInputPanel("Rows", basicMontageLayout.nRows(), 0));
	}
	
	void editRowColNumbersToDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
			int newcol=(int)gd.getNumber("Columns");
	int newrow=(int)gd.getNumber("Rows");
	   me.addCols(basicMontageLayout, newcol-basicMontageLayout.nColumns());
	    me.addRows(basicMontageLayout, newrow-basicMontageLayout.nRows());
		basicMontageLayout.setMontageProperties();
	}
	
	void addPanelDimensionFieldToDialog(StandardDialog gd, BasicMontageLayout basicMontageLayout, int colindex, int rowindex) {
		gd.add("Panel Width", new NumberInputPanel("Panel Width", basicMontageLayout.getPanelWidthOfColumn(colindex), 0));
		gd.add("Panel Height", new NumberInputPanel("Panel Height", basicMontageLayout.getPanelHeightOfRow(rowindex), 0));
	}
	
	
	public void showColumnNumberEditorDialog(GenericMontageEditor me, BasicMontageLayout basicMontageLayout, int colindex, int rowindex) {
		showColumnNumberEditorDialog(new StandardDialog("Edit", true), me,basicMontageLayout,1,1);
	}
	public void showColumnNumberEditorDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout, int colindex, int rowindex) {
		addColumnAndRowFieldToDialog( gd, basicMontageLayout);
		addPanelDimensionFieldToDialog(gd, basicMontageLayout, colindex,rowindex);
		gd.add("Row Major Layout", new BooleanInputPanel("Row Major Layout", basicMontageLayout.rowmajor));
		gd.setWindowCentered(true);
		gd.showDialog();
		if (!gd.wasOKed()) return;
		
		editRowColNumbersToDialog(gd, me, basicMontageLayout);
		editRowColWidthBasedOnDialog(gd, me, basicMontageLayout, colindex,rowindex);
	
	//ml.imp.updateAndDraw(); ml.setMontageProperties();
	boolean newlayout=gd.getBoolean("Row Major Layout");
	if (newlayout!=basicMontageLayout.rowmajor) {me.invertPanels( basicMontageLayout);}//ml.invertPanels(current);
	basicMontageLayout.rowmajor=newlayout;
 basicMontageLayout.setMontageProperties();
	}
	
	void editRowColWidthBasedOnDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout, int colindex, int rowindex) {
		int newpwidth=(int)gd.getNumber("Panel Width");
    int newpheight=(int)gd.getNumber("Panel Height");

 
	if (newpwidth!=basicMontageLayout.getPanelWidthOfColumn(colindex)) me.setPanelWidth(basicMontageLayout, newpwidth,colindex);
	if (newpheight!=basicMontageLayout.getPanelHeightOfRow(rowindex))me. setPanelHeight(basicMontageLayout,newpheight,rowindex);
	
	
	basicMontageLayout.setMontageProperties();
}
	
	
	
	
	
	public void showBorderEditorDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
	gd.setModal(true);
		gd.add("Horizontal Border",new NumberInputPanel("Horizontal Border", basicMontageLayout.BorderWidthLeftRight, 0));
	gd.add("Vertical Border",new NumberInputPanel("Vertical Border", basicMontageLayout.BorderWidthBottomTop, 0));	
	
	gd.showDialog();
	
	if (gd.wasOKed()) {
		me.setHorizontalBorder(basicMontageLayout, (int) (gd.getNumber("Horizontal Border"))); 
		me.setVerticalBorder(basicMontageLayout, (int) (gd.getNumber("Vertical Border")));
	basicMontageLayout.setMontageProperties();
	}

}
	
	public void showBorderEditorDialog(GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
		showBorderEditorDialog(getModalCentered("Borders") , me, basicMontageLayout);
	}


	public StandardDialog getModalCentered(String st) {
		StandardDialog output = new StandardDialog(st);
		output.setModal(true);
		output.setWindowCentered(true);
		return output;
	}
	
	
	public void showDialogBasedOnLocation( GenericMontageEditor me, BasicMontageLayout basicMontageLayout, int x, int y) {
		 showDialogBasedOnLocation(me, basicMontageLayout, new Point(x,y));
	}
	
	public void showDialogBasedOnLocation( GenericMontageEditor me, BasicMontageLayout basicMontageLayout, Point p) {
		if (basicMontageLayout==null) {IssueLog.log("you have requested a dialog for a null layout");return;}
		StandardDialog gd = new StandardDialog("Edit", true);
		
		basicMontageLayout.resetPtsPanels();
		int index=basicMontageLayout.getPanelIndex((int)p.getX(), (int)p.getY());
		if (basicMontageLayout.getSelectedSpace(1, ALL_OF_THE+BORDER).contains(p)) {showBorderEditorDialog(gd, me, basicMontageLayout); return;};
		if (basicMontageLayout.getSelectedSpace(1, ALL_OF_THE+PANELS).contains(p)) {showColumnNumberEditorDialog(gd, me, basicMontageLayout, basicMontageLayout.getGridCordAtIndex(index)[0], basicMontageLayout.getGridCordAtIndex(index)[1]); return;};
		if (basicMontageLayout.getSelectedSpace(1, ALL_OF_THE+PANEL_WITH_SPACES).contains(p)) {showLabelSpaceEditorDialog(gd, me, basicMontageLayout);; return;};
		showSpecialSpaceEditorDialog(gd, me, basicMontageLayout);	
	}
	
	
	
	
	public void addGeneralEditorFieldsToDialog(StandardDialog gd, BasicMontageLayout basicMontageLayout) {
		gd.add("columns",new NumberInputPanel("Columns", basicMontageLayout.nColumns(), 0));
		gd.add("rows",new NumberInputPanel("Rows", basicMontageLayout.nRows(), 0));
		gd.add("Vertical Border",new NumberInputPanel("Vertical Border", basicMontageLayout.BorderWidthBottomTop, 0));
		gd.add("Horizontal Border",new NumberInputPanel("Horizontal Border", basicMontageLayout.BorderWidthLeftRight, 0));
		
		addLabelSpacesToDoalog(gd, basicMontageLayout);
		gd.add("Top Extra Space",new NumberInputPanel("Top Extra Space", basicMontageLayout.specialSpaceWidthTop, 0));
		gd.add("Left Extra Space",new NumberInputPanel("Left Extra Space", basicMontageLayout.specialSpaceWidthLeft, 0));
		gd.add("Bottom Extra Space",new NumberInputPanel("Bottom Extra Space", basicMontageLayout.specialSpaceWidthBottom, 0));
		gd.add("Right Extra Space",new NumberInputPanel("Right Extra Space", basicMontageLayout.specialSpaceWidthRight, 0));
		gd.add( "Row Major Layout", new BooleanInputPanel("Row Major Layout", basicMontageLayout.rowmajor));
	}
	
	
	
	public void performEditBasedOnDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {

		basicMontageLayout.setMontageProperties();
		int newcol=(int)gd.getNumber("columns");
		int newrow=(int)gd.getNumber("rows");

		me.addCols(basicMontageLayout, newcol-basicMontageLayout.nColumns());
		me.addRows(basicMontageLayout, newrow-basicMontageLayout.nRows());
		
		me.expandBorderY2(basicMontageLayout, (int)gd.getNumber("Vertical Border")-basicMontageLayout.BorderWidthBottomTop);
		me.expandBorderX2(basicMontageLayout, (int)gd.getNumber("Horizontal Border")-basicMontageLayout.BorderWidthLeftRight); 
		editLabelSpaceBasedOnDialog(gd, me, basicMontageLayout);
		
		
		me.addTopSpecialSpace(basicMontageLayout, (int)gd.getNumber("Top Extra Space")-basicMontageLayout.specialSpaceWidthTop);
		me.addLeftSpecialSpace(basicMontageLayout, (int)gd.getNumber("Left Extra Space")-basicMontageLayout.specialSpaceWidthLeft);
		me.addBottomSpecialSpace(basicMontageLayout, (int)gd.getNumber("Bottom Extra Space")-basicMontageLayout.specialSpaceWidthBottom);
		me.addRightSpecialSpace(basicMontageLayout, (int)gd.getNumber("Right Extra Space")-basicMontageLayout.specialSpaceWidthRight);
		//basicMontageLayout.panelInsertion=gd.getNextChoiceIndex();
		basicMontageLayout.setMontageProperties();
		boolean newlayout=gd.getBoolean("Row Major Layout");
		if (newlayout!=basicMontageLayout.rowmajor) {me.invertPanels( basicMontageLayout);}//ml.invertPanels(current);
		basicMontageLayout.rowmajor=newlayout;
	}
	

	
	public void showUniqueDimensionDialog(BasicMontageLayout basicMontageLayout, GenericMontageEditor me, int kind) {
		if (basicMontageLayout==null) return;
		int nNumbers=basicMontageLayout.nColumns();
		if (kind==1) nNumbers=basicMontageLayout.nRows();
		StandardDialog gd=new StandardDialog("Set uniue dimension", true);
		
		String type="Column ";
		if (kind==1) type="Row ";
		
		if (kind==0) for (int i=0; i<nNumbers; i++) gd.add(type+(1+i),new NumberInputPanel(type+(1+i), basicMontageLayout.getPanelWidthOfColumn(i+1), 1));//gd.add(new NumberInputPanel(type+(1+i), basicMontageLayout.getPanelWidthOfColumn(i+1), 0);
		if (kind==1) for (int i=0; i<nNumbers; i++) gd.add(type+(1+i), new NumberInputPanel(type+(1+i),basicMontageLayout.getPanelHeightOfRow(i+1), 0));
		
		gd.showDialog();
		
		if (gd.wasOKed()) try {
			
			for (int i=0; i<nNumbers; i++) {
				int choice=(int)gd.getNumber(type+(1+i));
				if (kind==0) {me.augmentPanelWidthOfCol(basicMontageLayout, choice-basicMontageLayout.getPanelWidthOfColumn(i+1), i+1);}
				if (kind==1) {me.augmentPanelHeightOfRow(basicMontageLayout, choice-basicMontageLayout.getPanelHeightOfRow(i+1), i+1);}
			}
			basicMontageLayout.setMontageProperties();
		} catch (Throwable t) {IssueLog.logT(t);}
		
	}
	
	public void showUniqueDimensionDialog(BasicMontageLayout ml, int kind) {
		if (ml==null) return;
		int nNumbers=ml.nColumns();
		if (kind==1) nNumbers=ml.nRows();
		StandardDialog gd=new StandardDialog("Set unique dimension", true);
		String type="Column ";
		if (kind==1) type="Row ";
		
		if (kind==0) for (int i=0; i<nNumbers; i++) gd.add(type+(1+i),new NumberInputPanel(type+(1+i), ml.getPanelWidthOfColumn(i+1), 0));
		if (kind==1) for (int i=0; i<nNumbers; i++) gd.add(type+(1+i),new NumberInputPanel(type+(1+i), ml.getPanelHeightOfRow(i+1), 0));
		
		gd.showDialog();
		
		if (gd.wasOKed()) try {
			int[] output=new int[nNumbers];
			for (int i=0; i<nNumbers; i++) output[i]=(int)gd.getNumber(type+(1+i));
			if (kind==0) ml.setIndividualColumnWidths(output);
			if (kind==1) ml.setIndividualRowHegihts(output);
			ml.setMontageProperties();
		} catch (Throwable t) {IssueLog.logT(t);}
		
	}
	
	public void showMontageLayoutEditingDialog(BasicMontageLayout ml) { 
		if (ml==null) return;
		StandardDialog gd = new StandardDialog("Set Layout", true);
		addOptionsToDialog(ml, gd, false);
		gd.showDialog();
		if (gd.wasOKed()) {
			setOptionsToDialog(ml, gd, false) ;
			ml.setMontageProperties();
		}
		
	}
	
	int pre=0;

	
	public void addOptionsToDialog(BasicMontageLayout ml, StandardDialog gd, boolean simple) {
		if (ml==null) {IssueLog.log("cannot add options to dialog with no layout"); return;}
		int pre=0;
	    gd.add("Columns :",new NumberInputPanel("Columns :",  ml.nColumns(), pre));
	    gd.add("Rows :",new NumberInputPanel("Rows :", ml.nRows(), pre));
	    gd.add("Panel Width:",new NumberInputPanel("Panel Width:",  ml.panelWidth, pre));
	    gd.add("Panel Height",new NumberInputPanel("Panel Height", ml.panelHeight, pre));	
	    addLabelSpaceOptionsToDialog(ml, gd) ;
	    gd.add("xAxis Border:",new NumberInputPanel("xAxis Border:",   ml.BorderWidthLeftRight, pre));
	    gd.add("yAxis Border:",new NumberInputPanel("yAxis Border:",  ml.BorderWidthBottomTop, pre));
	  //  gd.add(new NumberInputPanel("Frame Width:", frameBorderWidth, pre);
	    gd.add("xOffset:",new NumberInputPanel("xOffset:",  ml.xshift, pre));
	    gd.add("yOffset:",new NumberInputPanel("yOffset:",  ml.yshift, pre));
	    gd.add("Row Major Order",new BooleanInputPanel("Row Major Order",  ml.rowmajor) );
	    gd.add("Reverse Order",new BooleanInputPanel("Reverse Order", ! ml.firsttoLast)) ;
}

public void setOptionsToDialog(BasicMontageLayout  ml, StandardDialog gd, boolean simple) {
	if (ml==null) {IssueLog.log("cannot add options to dialog with no layout"); return;}
	 ml.setCols((int) gd.getNumber("Columns :"));
	 ml.setRows((int) gd.getNumber("Rows :"));
   
	 ml.setStandardPanelWidth((int) gd.getNumber("Panel Width:"));
	 ml. setStandardPanelHeight((int) gd.getNumber("Panel Height"));
	
	 setLayoutSpacesToDialog( ml, gd);
    ml.setHorizontalBorder((int )gd.getNumber("xAxis Border:"));
    ml. setVerticalBorder((int )gd.getNumber("yAxis Border:"));
   // frameBorderWidth=(int) gd.getNumber();
    ml.resetPtsPanels((int) gd.getNumber("xOffset:"), (int) gd.getNumber("yOffset:"));
    if ( ml.rowmajor!=gd.getBoolean("Row Major Order")) {int newcol= ml.nRows(); int newrow= ml.nColumns();   ml.setCols(newcol);  ml.setRows(newrow);}
    ml.firsttoLast=!gd.getBoolean("Reverse Order");
}




void setLayoutSpacesToDialog(BasicMontageLayout  ml, StandardDialog gd) {
	  ml. setTopSpace((int) gd.getNumber("Top Label Space"));
	  ml. setBottomSpace((int) gd.getNumber("Bottom Label Space"));
	  ml. setLeftSpace((int) gd.getNumber("Left Label Space"));
      ml.setRightSpace((int) gd.getNumber("Right Label Space"));
	
}


void addLabelSpacesToDoalog(StandardDialog gd, BasicMontageLayout basicMontageLayout) {
	gd.add("Top Label Space",new NumberInputPanel("Top Label Space", basicMontageLayout.labelSpaceWidthTop, 0));
	gd.add("Bottom Label Space",new NumberInputPanel("Bottom Label Space", basicMontageLayout.labelSpaceWidthBottom, 0));
	gd.add("Left Label Space",new NumberInputPanel("Left Label Space", basicMontageLayout.labelSpaceWidthLeft, 0));
	gd.add("Right Label Space",new NumberInputPanel("Right Label Space", basicMontageLayout.labelSpaceWidthRight, 0));
}


void editLabelSpaceBasedOnDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
	me.setTopLabelSpace(basicMontageLayout, (int)gd.getNumber("Top Label Space"));
	me.setBottomLabelSpace(basicMontageLayout, (int)gd.getNumber("Bottom Label Space"));
	me.setLeftLabelSpace(basicMontageLayout, (int)gd.getNumber("Left Label Space"));
	me.setRightLabelSpace(basicMontageLayout, (int)gd.getNumber("Right Label Space"));
}

public void addLabelSpaceOptionsToDialog(BasicMontageLayout ml, StandardDialog gd) {
    gd.add("Top Label Space",new NumberInputPanel("Top Label Space", ml.labelSpaceWidthTop,pre ));
    gd.add("Bottom Label Space",new NumberInputPanel("Bottom Label Space",  ml.labelSpaceWidthBottom, pre));
	gd.add("Left Label Space",new NumberInputPanel("Left Label Space",  ml.labelSpaceWidthLeft,pre));
	gd.add("Right Label Space",new NumberInputPanel("Right Label Space",   ml.labelSpaceWidthRight, pre));
	 
}

public void showLabelSpaceEditorDialog(StandardDialog gd, GenericMontageEditor me, BasicMontageLayout basicMontageLayout) {
	addLabelSpaceOptionsToDialog(basicMontageLayout, gd);
	gd.showDialog();
	if (gd.wasOKed()) {
		editLabelSpaceBasedOnDialog(gd, me, basicMontageLayout);
	 basicMontageLayout.setMontageProperties();
	}
	
	}

}
