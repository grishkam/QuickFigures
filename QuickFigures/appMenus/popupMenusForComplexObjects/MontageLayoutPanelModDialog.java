package popupMenusForComplexObjects;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import genericMontageKit.PanelLayout;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.BooleanInputPanel;
import standardDialog.NumberInputPanel;

public class MontageLayoutPanelModDialog extends GraphicItemOptionsDialog implements MouseListener{

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic graphic;
	private PanelLayout layout;
	int panelnum=1;
	int type=1;
	
	
	{this.addMouseListener(this);}
	public MontageLayoutPanelModDialog(PanelLayoutGraphic g, PanelLayout p) {
		graphic=g;
		layout=p;
	}

	public PanelLayoutGraphic getLayoutGraphic() {
		return graphic;
	}
	public PanelLayout getPanelLayout() {
		return layout;
	}
	
	public void showPaneldimDialog(int panelnum) {
		this.panelnum=panelnum;
		double width=this.getPanelLayout().getPanel(panelnum).getWidth();
		double height= this.getPanelLayout().getPanel(panelnum).getHeight();
		this.add("pwidth", new NumberInputPanel("Panel Width", width));
		this.add("pheight", new NumberInputPanel("Panel Height", height));
		
		this.add("swidth", new NumberInputPanel("Standard Panel Width", getPanelLayout().getStandardPanelWidth()));
		this.add("sheight", new NumberInputPanel("Standard Panel Height", getPanelLayout().getStandardPanelHeight()));
		this.add("sw", new BooleanInputPanel("Use Standard Panel Width", this.getPanelLayout().getStandardPanelWidth()==width ));
		this.add("sh", new BooleanInputPanel("Use Standard Panel Height", this.getPanelLayout().getStandardPanelHeight()==height ));
		
		this.showDialog();
	}
	
	protected void setItemsToDiaog() {
		this.getLayoutGraphic().mapPanelLocationsOfLockedItems();
		if (type==1) {
			this.getPanelLayout().setPanelWidth(panelnum, this.getNumberInt("pwidth"));
			this.getPanelLayout().setPanelHeight(panelnum,this.getNumberInt("pheight"));
			boolean sw=this.getBoolean("sw");
			boolean sh=this.getBoolean("sh");
			this.getPanelLayout().setStandardPanelWidth(this.getNumberInt("swidth"));
			this.getPanelLayout().setStandardPanelHeight(this.getNumberInt("sheight"));
			
			if (sw) {
				getPanelLayout().setPanelWidth(panelnum,getPanelLayout().getStandardPanelWidth());
			}
			if (sh) {
				getPanelLayout().setPanelHeight(panelnum,getPanelLayout().getStandardPanelHeight());
			}
			this.getPanelLayout().resetPtsPanels();
		}
		this.getLayoutGraphic().snapLockedItems();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	if (graphic!=null) graphic.select();
		
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
	
	
	
	
}
