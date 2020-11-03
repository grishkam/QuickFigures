package externalToolBar;


import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import logging.IssueLog;

public class IconSet {
	
	 public Image[] icons;
	 
	 Image defaultIcon=loadLocalImage("icons/Blank.jpg");
	 
	 Icon[] icons2;
	 
	 /**Given the name of a  file in the same .jar package as this file,
	  returns an image with all the file.*/
	 public Image loadImage(String name) {
		
		return loadLocalImage(name);
		
	}
	 
	
	public IconSet() {}
	public IconSet(String...strings ) {
		setIcons(strings );
	}
	public IconSet(Icon...strings ) {
		setIcons(strings );
	}
	
	
	private void setIcons(Icon[] strings) {
		// TODO Auto-generated method stub
		icons2=strings;
		for(int i=0; i<strings.length; i++) {
			setIcon( i,strings[i]);
		}
	}


	public void setIcons(String...strings ) {
		icons=new Image[strings.length];
		icons2=new Icon[strings.length] ;
		for(int i=0; i<strings.length; i++) try {
			setIcon( i,strings[i]);
		} catch (Throwable t) {IssueLog.log(t);}
	}
	
	/**sets the icon based on a string that tells the code how to find the icon*/
	public void setIcon(int i, String st){
		icons[i]=loadImage(st);
		icons2[i]=new ImageIcon(icons[i]);
	}
	
	public void setIcon(int i, Icon st){
		//icons[i]=loadImage(st);
		icons2[i]=st;
	}
	
	
	String directory="";
	
	/**Given the name of a  file in the same .jar package as this file,
	  returns an image with all the file.*/
	 public static Image loadLocalImage(String name) {
		
		try {
			Image io=ImageIO.read( IconSet.class.getClassLoader().getResourceAsStream( name));
			
			return io;
		} catch (Exception e) {
			IssueLog.log("failed to find image in path:  "+name);
			return null;
		}
		
	}

		public Image getImageForIcon(int i) {
			if (i>=icons.length) return defaultIcon;
			return icons[i];
		}
		
		public Icon getIcon(int i) {
			if (i>=icons2.length) return new ImageIcon(defaultIcon);
			return icons2[i];
		}
		
		public void setItemIcons(AbstractButton item) {
			item.setIcon(getIcon(0));
			item.setPressedIcon(getIcon(1));
			item.setRolloverIcon(getIcon(2));
			
		}
		
	
}
