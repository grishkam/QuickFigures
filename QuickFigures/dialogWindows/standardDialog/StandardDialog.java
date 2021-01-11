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
/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Version: 2021.1
 */
package standardDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.undo.UndoManager;

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import graphicActionToolbar.CurrentFigureSet;
import locatedObject.ScaleInfo;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import standardDialog.booleans.BooleanArrayInputPanel;
import standardDialog.booleans.BooleanInputEvent;
import standardDialog.booleans.BooleanInputListener;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.channels.ChannelListChoiceInputPanel;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.choices.ItemSelectblePanel;
import standardDialog.colors.ColorCheckbox;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import standardDialog.colors.ColorInputPanel;
import standardDialog.colors.ColorListChoice;
import standardDialog.fonts.FontChooser;
import standardDialog.fonts.FontInputEvent;
import standardDialog.fonts.FontInputListener;
import standardDialog.graphics.FixedEdgeSelectable;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.numbers.NumberArrayInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.numbers.PointInputPanel;
import standardDialog.strings.StringInputEvent;
import standardDialog.strings.StringInputListener;
import standardDialog.strings.StringInputPanel;
import storedValueDialog.StoredValueDilaog;
import undo.AbstractUndoableEdit2;

/**inspired by imageJ's generic dialog. */
public class StandardDialog extends JDialog implements KeyListener, ActionListener, StringInputListener, NumberInputListener, BooleanInputListener,ChoiceInputListener, FontInputListener, ObjectEditListener ,ColorInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public StandardDialog() {}
	public StandardDialog(String st) {
		this.setTitle(st);
	}
	
	/**Creates a dialog of title st. If the boolean is true. dialog will be modeal and centered */
	public StandardDialog(String title, boolean modal) {
		this.setTitle(title);
		this.setModal(modal);
		this.setWindowCentered(modal);
	}

	{this.setLayout(new GridBagLayout());}
	protected ArrayList<StandardDialogListener> listen=new ArrayList<StandardDialogListener>();
	
	protected int gridPositionY=0;
	protected int gx=0;
	protected int gxmax=0;
	protected int gymax=0;
	
	protected JButton OKBut=createOkButton() ;
	protected JButton CancelBut=createCancelButton() ;
	private boolean centerWindow=false;
	
	protected ArrayList<JButton> additionButtons=new ArrayList<JButton>();
	private boolean hideCancel=false;
	private boolean hideOK=false;
	
	boolean useMainPanel=true;
	private String mainPanelName="";
	

	
	
	
	
	public JTabbedPane removeOptionsTab() {
		JTabbedPane output = this.getOptionDisplayTabs();
		this.remove(output);
		return output;
	}
	
	private JTabbedPane optionDisplayTabs=new JTabbedPane(); {
		GridBagConstraints mainPanelgc = new GridBagConstraints();
		mainPanelgc.gridx=1;
		mainPanelgc.gridy=1;
		this.add(getOptionDisplayTabs(), mainPanelgc);
		
	}
	
	
	public GriddedPanel SetupmainPanel() {
		GriddedPanel mainPanel=new GriddedPanel();
		mainPanel.setLayout(new GridBagLayout());
	
		getOptionDisplayTabs().addTab(mainPanelName, mainPanel);
	
		return mainPanel;
	}
	protected GriddedPanel mainPanel=SetupmainPanel();
	
	
	
	public void addButton(JButton b) {
		additionButtons.add(b);
	}
	
	public void moveGrid(int x, int y) {
		gridPositionY+=y;
		gx+=x;
		if (gx>gxmax) gxmax=gx;
		if (gridPositionY>gymax) gymax=gridPositionY;
	}
	
	protected GridBagConstraints getCurrentConstraints() {
		GridBagConstraints output=new GridBagConstraints();
		output.gridx=gx;
		output.gridy=gridPositionY;
		return output;
	}
	
	JButton createOkButton() {
		JButton OKBut=new JButton("OK");
		OKBut.addActionListener(this);
		OKBut.setActionCommand("OK");
		
		return OKBut;
	}
	JButton createCancelButton() {
		JButton OKBut=new JButton("Cancel");
		OKBut.addActionListener(this);
		OKBut.setActionCommand("Cancel");
		
		return OKBut;
	}
	
	protected HashMap<String, StringInputPanel> allStrings=new HashMap<String, StringInputPanel>();
	protected HashMap<String, NumberInputPanel> Numbers=new HashMap<String, NumberInputPanel>();
	protected HashMap<String, NumberArrayInputPanel> NumberSets=new HashMap<String, NumberArrayInputPanel>();
	protected HashMap<String, ChoiceInputPanel> choices=new HashMap<String, ChoiceInputPanel>();
	protected HashMap<String, FontChooser> fonts=new HashMap<String, FontChooser>();
	protected HashMap<String, BooleanInputPanel> bools=new HashMap<String, BooleanInputPanel>();
	protected HashMap<String, ItemSelectblePanel> items=new HashMap<String,ItemSelectblePanel>();
	protected HashMap<String, ColorListChoice> colors=new HashMap<String, ColorListChoice>();
	protected HashMap<String, BooleanArrayInputPanel> boolSets=new HashMap<String, BooleanArrayInputPanel>();

	private HashMap<String, ChannelListChoiceInputPanel> chanChoices=new HashMap<String, ChannelListChoiceInputPanel>();;

	

	
	private ArrayList<ChannelEntry> channelEnt;

	private boolean wasOKed;
	private boolean wasCanceled;

	private JPanel theButtonPanel;

	private JButton[] bonusButtons;

	public AbstractUndoableEdit2 undo;//this undo is added to an undo 

	public UndoManager currentUndoManager=new CurrentFigureSet().getUndoManager();

	private final ArrayList<StandardDialog> subordinateDialogs =new ArrayList<StandardDialog> ();

	
	
	
	
	public void add(String key, StringInputPanel st) {
		allStrings.put(key, st);
		st.addStringInputListener(this);
		st.setKey(key);
		place(st);
	}
	
	public String getString(String key) {
		StringInputPanel ob = allStrings.get(key);
		if(ob==null) return "";
		return ob.getTextFromField();
	}
	
	protected boolean setStringField(String key, String content) {
		StringInputPanel ob = allStrings.get(key);
		if(ob==null) return false;
		ob.setContentText(content);;
		return true;
	}
	
	public void add(String key, NumberInputPanel st) {
		if (st instanceof NumberArrayInputPanel) {NumberSets.put(key, (NumberArrayInputPanel) st);} else
		Numbers.put(key, st);
		st.setKey(key);
		st.addNumberInputListener(this);
		place(st);
	}
	
	public void add(  String key, NumberInputPanel... st) {
		int k=0;
		for(int i=0; i<st.length; i++) {
			this.add(key+i, st[i]);
			this.moveGrid(2, -1);
			k++;
		}
		this.moveGrid(-2*k, 0);
	}
	
	public double getNumber(String key) {
		NumberInputPanel ob = Numbers.get(key);
		if(ob==null) return 0;
		return ob.getNumber();
	}
	
	public int getNumberInt(String key) {
		NumberInputPanel ob = Numbers.get(key);
		if(ob==null) return 0;
		return (int)ob.getNumber();
	}
	
	public void setNumber(String key, double number) {
		NumberInputPanel ob = Numbers.get(key);
		if(ob==null) return;
		ob.setNumber(number);
	}
	
	
	
	/**will return the nubmer array with specified key. Or an empty array if not found*/
	public float[] getNumberArray(String key) {
		NumberArrayInputPanel ob = NumberSets.get(key);
		if(ob==null) return new float[] {};
		return ob.getArray();
	}
	
	public Point2D getPoint(String key) {
		float[] ar = getNumberArray(key);
		if (ar.length<2) return null;
		return new Point2D.Double(ar[0], ar[1]);
	}
	
	public void add(String key, ChoiceInputPanel st) {
		
		if (st instanceof ColorComboboxPanel) {
			colors.put(key, (ColorComboboxPanel) st);
		} else
		choices.put(key, st);
		st.setKey(key);
		st.addChoiceInputListener(this);
		
		place(st);
	}
	public int getChoiceIndex(String key) {
		ChoiceInputPanel ob = choices.get(key);
		if(ob==null) {
			ItemSelectblePanel ob2=items.get(key);
			if(ob2==null) return 0;
			return ob2.getSelectedItemNumber();
		}
		return ob.getSelectedIndex();
	}
	
	public void add(String key, FontChooser st) {
		fonts.put(key, st);
		st.addFontInputListener(this);
		st.setKey(key);
		place(st);
	}
	
	public void add(String key, ColorInputPanel st) {
		colors.put(key, st);
		//st.addObjectEditListener(this);
		st.addColorInputListener(this);
		st.setKey(key);
		place(st);
	}
	
	
	public void add(String key, BooleanInputPanel st) {
		if (st instanceof BooleanArrayInputPanel) {
			boolSets.put(key, (BooleanArrayInputPanel) st);
		} else
		bools.put(key, st);
		st.addBooleanInputListener(this);
		st.setKey(key);
		place(st);
	}
		
	public boolean getBoolean(String key) {
		BooleanInputPanel ob = bools.get(key);
		if(ob==null) return false;
		return ob.isChecked();
	}
	
	/**returns the channel choices*/
	public ArrayList<Integer> getChannelChoices(String key) {
		ChannelListChoiceInputPanel ob = chanChoices.get(key);
		if(ob==null) return new ArrayList<Integer>();
		return  ob.getSelectedIndices();
	}
	
	public boolean[] getBooleanArray(String key) {
		BooleanArrayInputPanel ob = boolSets.get(key);
		if(ob==null) return new boolean[] {false};
		return ob.getArray();
	}
	
	public void add(String key, ItemSelectblePanel st) {
		items.put(key, st);
		st.addChoiceInputListener(this);
		st.setKey(key);
		place(st);
	}
	
	public Object[] getItemsSelected(String key) {
		ItemSelectblePanel ob = items.get(key);
		if(ob==null) return null;
		return ob.getBox().getSelectedObjects();
	}

	
	/**Adds a channel list choice to the dialog*/
	public void add(String key, ChannelListChoiceInputPanel st) {
		
		chanChoices.put(key, st);
		st.addChoiceInputListener(this);
		st.setKey(key);
		place(st);
	}
	
	
	public void place(OnGridLayout st) {
		if (useMainPanel)
			{
			getMainPanel().place(st);
			} else
		st.placeItems(this, gx, gridPositionY);
		if (gxmax<st.gridWidth())gxmax=st.gridWidth();
		gridPositionY+=st.gridHeight();
	}
	
	public Font getFont(String key) {
		FontChooser ob = fonts.get(key);
		if(ob==null) return null;
		return ob.getSelectedFont();
	}
	
	public Color getColor(String key) {
		ColorListChoice ob = colors.get(key);
		if(ob==null) return null;
		return ob.getSelectedColor();
	}
	

	

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	  Insets getInsets(int top, int left, int bottom, int right) {
	       
	            return new Insets(top, left, bottom, right);
	      
	    }
	  
	  
	  protected JPanel addButtonPanel(Container cont) {
		   GridBagConstraints c = getCurrentConstraints();
		  
		   JPanel ButtonPanel = generateButtonPanel();
		   c.gridx=1;
		   c.gridy=this.gymax+2;
		
		
		  c.anchor=GridBagConstraints.EAST;
		cont.add(ButtonPanel, c);
		return ButtonPanel;
	  }
	  
	  /**Creates a panel with OK and Cancel buttons*/
	  public JPanel generateButtonPanel() {
		  JPanel ButtonPanel=new JPanel();
		  ButtonPanel.setLayout(new FlowLayout());
		  for(JButton b:this.additionButtons) {ButtonPanel.add(b);}
		if (!hideCancel)  ButtonPanel.add(CancelBut);
		if (!hideOK)  ButtonPanel.add(OKBut);
		 if (this.bonusButtons!=null) for(JButton b:this.bonusButtons) {ButtonPanel.add(b);}
		  return ButtonPanel;
	  }
	  
	  public void showDialog() {
		this.theButtonPanel=addButtonPanel(this);
		  makeVisible();
		  
	  }
	  
	  
	  public void makeVisible() {
		  pack();
		  if (this.centerWindow()) center(this);
		  setVisible(true);
	  }
	
	public static void main(String[] args) {
		StandardDialog sd = new StandardDialog();
		sd.add("text1", new StringInputPanel("give me text ", "default input"));
		sd.add("num", new NumberInputPanel("Select number", 5));
		sd.add("combo", new ChoiceInputPanel("Select", new String[] {"a","b", "v", "d"}, 3));
		FontChooser sb = new FontChooser(new Font("Arial", Font.BOLD, 12));
		sd.add("font", sb);
		NumberArrayInputPanel pai = new NumberArrayInputPanel(4, 0);
		sd.add("array panel", pai);
		sd.add("combo", new BooleanInputPanel("check ", true));
		AngleInputPanel pai2 = new AngleInputPanel("Angle ", 0, true);
		sd.add("angle", pai2);
		FixedEdgeSelectable f = new FixedEdgeSelectable(3);
		ItemSelectblePanel is = new ItemSelectblePanel("Selecgt Edge", f);
		sd.add("edge fix", is);
		
		sd.setModal(true);
		sd. showDialog();
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==OKBut) {
			this.onOK();
			this.wasOKed=true;
			this.setVisible(false);
			resolveUndo();
		}
		if (arg0.getSource()==CancelBut) {
			afterCancelButtonPress();
			this.wasCanceled=true;
			this.setVisible(false);
		}
		
	}
	
	/**
	 Called in the wake of a cancel button press
	 */
	private void afterCancelButtonPress() {
		this.revertAll();
		this.onCancel();
		if(undo!=null) undo.undo();
		for(StandardDialog t: this.subordinateDialogs) {
			t.afterCancelButtonPress();
		}
	}
	
	protected void resolveUndo() {
		try {
			if(undo!=null)
				{undo.establishFinalState();
			if (currentUndoManager!=null)
				currentUndoManager.addEdit(undo);
				}
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		
	}
	protected void onCancel() {
		 
	}
	protected void afterEachItemChange() {
		
	}	

	
	/**called when a font selection has changed*/
	public void onFontChange(String key, Font f) {}
	
	
	
	public void notifyAllListeners(JPanel key, String string) {
		afterEachItemChange();
		DialogItemChangeEvent di = new DialogItemChangeEvent(this, key) ;
		di.setStringKey(string);
		onListenerLotification(di);
	}
	public void onListenerLotification(DialogItemChangeEvent di) {
		for(StandardDialogListener l:listen ) {if (l!=null)l.itemChange(di);}
	}
	
	public void addDialogListener(StandardDialogListener l) {
		listen.add(l);
	}
	public void removeDialogListener(StandardDialogListener l) {
		listen.remove(l);
	}

	@Override
	public void valueChanged(ChoiceInputEvent ne) {
		
		notifyAllListeners(ne.getSourcePanel(), ne.getKey());
		
	}

	@Override
	public void booleanInput(BooleanInputEvent bie) {
		notifyAllListeners(bie.getSourcePanel(), bie.getKey());
		
	}

	@Override
	public void numberChanged(NumberInputEvent ne) {
		notifyAllListeners(ne.getSourcePanel(), ne.getKey());
		
	}

	@Override
	public void StringInput(StringInputEvent sie) {
		notifyAllListeners(sie.getSourcePanel(), sie.getKey());
		
	}

	@Override
	public void FontChanged(FontInputEvent fie) {
		notifyAllListeners(fie.getSourcePanel(), fie.getKey());
		
	}

	@Override
	public void objectEdited(ObjectEditEvent oee) {
		notifyAllListeners(null, oee.getKey());
		
	}
	@Override
	public void ColorChanged(ColorInputEvent fie) {
		notifyAllListeners(fie.getSourcePanel(), fie.getKey());
		
	}
	
	 /** Positions the specified window in the center of the screen. 
	    I copies this from ij.GUI in imageJ*/
    public static void center(Window w) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowsize = w.getSize();
        if (windowsize.width==0)
            return;
        int newleft = screensize.width/2-windowsize.width/2;
        int newtop = (screensize.height-windowsize.height)/2;
        if (newtop<0) newtop = 0;
        w.setLocation(newleft, newtop);
    }
	public boolean centerWindow() {
		return centerWindow;
	}
	public void setWindowCentered(boolean centerWindow) {
		this.centerWindow = centerWindow;
	}

	
	public void addScaleInfoToDialog(ScaleInfo si) {
		GriddedPanel omp = this.getMainPanel();
		this.setMainPanel(new GriddedPanel());
		
		this.add("units",new StringInputPanel("Units ", si.getUnits(),  5));
		this.add("pw",new NumberInputPanel("Pixel Width ", si.getPixelWidth(), 4));
	
		this.add("ph",new NumberInputPanel("Pixel Height ", si.getPixelHeight(), 4));
		
		
		this.getOptionDisplayTabs().addTab("Calibration", this.getMainPanel());
		this.setMainPanel(omp);
	}

	public void setScaleInfoToDialog(ScaleInfo si) {
		si.setUnits(this.getString("units"));
		si.setPixelWidth(this.getNumber("pw"));
		
		si.setPixelHeight(this.getNumber("ph"));
		
	}
	public JTabbedPane getOptionDisplayTabs() {
		return optionDisplayTabs;
	}
	
	public void setTabName(String newName) {
		getOptionDisplayTabs().setTitleAt(0, newName);
	}

	public GriddedPanel getMainPanel() {
		return mainPanel;
	}
	
	protected void setMainPanel( GriddedPanel g) {
		mainPanel=g;
	}
	
	public void addSubordinateDialog(String shortLabel,
			StandardDialog dis) {
		if (dis==null) return;
		
		if (dis.getOptionDisplayTabs().getTabCount()>1) {
			getOptionDisplayTabs().addTab(shortLabel, dis.removeOptionsTab());
		} else {
			Component p = extractGridPanelFrom(dis);
			if(p==null) return;
			getOptionDisplayTabs().addTab(shortLabel, p);
			}
		subordinateDialogs.add(dis);
	}
	/**
	returns the first tab component of the item
	 */
	protected Component extractGridPanelFrom(StandardDialog dis) {
		Component p=dis.getOptionDisplayTabs().getTabComponentAt(0);//.getMainPanel();
		if (p==null) p=dis.getMainPanel();
		if (p==null) return null;
		dis.remove(p);
		return p;
	}
	
	public void addSubordinateDialogsAsTabs(String shortLabel, StandardDialog... dis) {
		SubDialogSection ss = new SubDialogSection(shortLabel, dis);
		gridPositionY++;
		ss.placeItems(this.getMainPanel(), gx+1,gridPositionY);
		gridPositionY++;
		for(StandardDialog d: dis)this.subordinateDialogs.add(d);
		gridPositionY++;
	}
	
	class SubDialogSection implements OnGridLayout {

		private JTabbedPane tabs;

		/**
		 * @param shortLabel
		 * @param dis
		 */
		public SubDialogSection(String shortLabel, StandardDialog[] dis) {
			tabs=new JTabbedPane();
			for(StandardDialog d: dis)tabs.addTab(d.getName(), extractGridPanelFrom(d));
		}

		@Override
		public void placeItems(Container jp, int x0, int y0) {
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.insets=firstInsets;
			gc.gridx=x0;
			gc.gridy=y0;
			gc.gridwidth=this.gridWidth();
			gc.gridheight=this.gridHeight();
			gc.anchor = GridBagConstraints.WEST;
			jp.add(tabs, gc);;
			
			
		}

		@Override
		public int gridHeight() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int gridWidth() {
			
			return 4;
		}

		}

	
	public JButton alternateCloseButton() {
		JButton cc = new JButton("Close");
		cc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				resolveUndo();
			}

			});
		
		return cc;
	}
	
	public void addChannelCheckBoxes(MultiChannelImage mw) {
		int chan=mw.nChannels();
		channelEnt=new ArrayList<ChannelEntry>();
		
		for(int i=1; i<=chan; i++) {
			ChannelEntry entry = mw.getSliceChannelEntry(i, 1, 1);
			channelEnt.add(entry);
			this.add(entry.getRealChannelName(), new BooleanInputPanel(entry.getRealChannelName(), true, new ColorCheckbox(entry.getColor())));
		}
		
	}
	
	/**returns the channels chosen by checkbox. not currently used but kept in the event of future use*/
	public ArrayList<ChannelEntry> getChannelsChosen() {
		ArrayList<ChannelEntry> channelEnt2 = new ArrayList<ChannelEntry>();
		if (channelEnt==null) return channelEnt2;
		for(ChannelEntry entry: channelEnt) {
			if(entry==null) continue;
			boolean result = this.getBoolean(entry.getRealChannelName());
			if (!result) channelEnt2.add(entry);
		}
		
		return channelEnt2;
	}
	
	/**returns true if the ok button was pressed */
	public boolean wasOKed() {
		return wasOKed;
	}
	/**returns true if the ok button was pressed */
	public boolean wasCanceled() {
		return wasCanceled;
	}
	
	
	/**restores all fields and input panels to original values*/
	public void revertAll() {
		for(StandardDialog dia: this.subordinateDialogs) {dia.revertAll();}
		for(StringInputPanel i:allStrings.values()) {
			i.revert();
		}
		for(NumberInputPanel i:Numbers.values()) {
			i.revert();
		}
		for(NumberInputPanel i:NumberSets.values()) {
			i.revert();
		}
		for(ChoiceInputPanel i:choices.values()) {
			i.revert();
		}
		for(BooleanInputPanel i:bools.values()) {
			i.revert();
		}
		for(ItemSelectblePanel i:items.values()) {
			i.revert();
		}
		for(ColorListChoice i:colors.values()) {
			if (i instanceof ColorComboboxPanel )((ColorComboboxPanel) i).revert();
		}
		for(BooleanInputPanel i:boolSets.values()) {
			i.revert();
		}
		for(FontChooser i:fonts.values()) {
			i.revert();
		}
		for(ChannelListChoiceInputPanel i:chanChoices.values()) {
			i.revert();
		}
		
		this.repaint();
		
	}
	public JPanel getTheButtonPanel() {
		return theButtonPanel;
	}
	
	public void setBonusButtons(JButton... buttons ) {
		this.bonusButtons=buttons;
	}
	
	public boolean hasContent() {
		if (allStrings.keySet().size()>0) return true;
		if (Numbers.keySet().size()>0) return true;
		if (NumberSets.keySet().size()>0) return true;
		if (choices.keySet().size()>0) return true;
		if (fonts.keySet().size()>0) return true;
		if (bools.keySet().size()>0) return true;
		if (boolSets.keySet().size()>0) return true;
		if (items.keySet().size()>0) return true;
		if (colors.keySet().size()>0) return true;
		
		return false;
	}
	
	public static Double getNumberFromUser(String st, double starting) {
		return getNumberFromUser(st, starting, false, new Object[] {});
	}
	public static Double getNumberFromUser(String st, double starting, boolean angle, Object... otherOptions) {
		StandardDialog sd = new StandardDialog(st);
		sd.setModal(true);
		sd.setWindowCentered(true);
		if (angle) sd.add(st, new AngleInputPanel(st, starting, true));
		else
		sd.add(st, new NumberInputPanel(st, starting, 4));
		if (otherOptions!=null &&otherOptions.length>0) {
			for(Object of: otherOptions)StoredValueDilaog.addFieldsForObject(sd, of);
		}
		sd.showDialog();
		
		if(sd.wasOKed) {
			return sd.getNumber(st);
		}
		return starting;
	}
	
	public static Point2D getPointFromUser(String st, Point2D starting) {
		StandardDialog sd = new StandardDialog(st);
		sd.setModal(true);
		sd.setWindowCentered(true);
		sd.add(st, new PointInputPanel(st, starting));
		
		
		sd.showDialog();
		
		if(sd.wasOKed) {
			return sd.getPoint(st);
		}
		return starting;
	}
	public boolean isHideCancel() {
		return hideCancel;
	}
	public void setHideCancel(boolean hideCancel) {
		this.hideCancel = hideCancel;
	}
	

	public void setHideOK(boolean b) {
		this.hideOK=b;
		
	}
	
	/**returns a popup menu with the compoents of this dialog*/
	public SmartPopupJMenu createInPopup() {
		SmartPopupJMenu output = new SmartPopupJMenu();
		output.add(this.getMainPanel());
		return output;
	}
	
	public static Component combinePanels(OnGridLayout... i1) {
		JPanel j = new JPanel(); 
		j.setLayout(new GridBagLayout());
		for(int i=0; i<i1.length; i++)
			i1[i].placeItems(j, 0, 1+i);
		return j;
	}
	
	/**returns a map of very input panel. used by testing functions*/
	public HashMap<String, Object> getAllInputPanels() {
		HashMap<String, Object> output=new HashMap<String, Object> ();
		output.putAll(Numbers);
		output.putAll(NumberSets);
		output.putAll(choices);
		output.putAll(bools);
		output.putAll(allStrings);
		output.putAll(boolSets);
		output.putAll(items);
		output.putAll(choices);
		for(StandardDialog d: this.subordinateDialogs) {output.putAll(d.getAllInputPanels());}
		
		return output;
	}
	
}
