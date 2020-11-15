package popupMenusForComplexObjects;

import javax.swing.JMenu;

import channelMerging.MultiChannelSlotDialog;
import genericMontageKit.PanelList;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;

public class ImageMenuForMultiChannel extends MenuForChannelLabelMultiChannel {

	public ImageMenuForMultiChannel(String name, MultichannelDisplayLayer panel,
			PanelList list) {
		super(name, panel, list, panel.getChannelLabelManager());
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void generateLabelMenuItems() {
		createSetScaleItem() ;
		createCropOption();
		
		JMenu mm = new JMenu("Expert Options") ;
		createSaveImageItem(mm) ;
		createSetImageItem(mm) ;
		createMultiChanOpsions(mm) ;
		createChannelUseItem(mm);
		createShowImageItem(mm);
		this.add(mm);
		
	}
	
	private SmartMenuItem2 createMultiChanOpsions(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Saving Options  ") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				MultiChannelSlotDialog dia = new MultiChannelSlotDialog(display.getSlot()) ;
				dia.showDialog();
			}
			
		};
		thi.add(out);
		return out;
		
		
	}
	
	private SmartMenuItem2 createSetScaleItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Set Scale (Pixel Size)") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction() {
				new SetImageScale(display). showPixelSizeSetDialog();
			}
		};
		this.add(out);
		return out;
		
	}


	
	
	SmartMenuItem2 createShowImageItem(JMenu t) {
		SmartMenuItem2 out=new SmartMenuItem2("Show Image") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction() {
				display.getSlot().showImage();
			}
			
		};
		t.add(out);
		return out;
	}
	
	SmartMenuItem2 createCropOption() {
		SmartMenuItem2 out=new SmartMenuItem2("Re-Crop") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				addUndo(
						FigureOrganizingSuplierForPopup.showRecropDisplayDialog(display, null)
						);
			}
			
		};
		this.add(out);
		return out;
	}
	
	
	


	
	SmartMenuItem2 createSaveImageItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Save Image") {
						private static final long serialVersionUID = 1L;
						public void onAction() {
							display.getSlot().saveImageEmbed();
						}	};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createSetImageItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Set Image") {
			private static final long serialVersionUID = 1L;
			public void onAction() {
				display.getSlot().setImageDialog();
			}
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createChannelUseItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Channel Use ") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction() {
				display. showStackOptionsDialog();
			}
		};
		thi.add(out);
		return out;
	}
}
