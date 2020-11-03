package figureTemplates;

import java.io.Serializable;
import java.util.ArrayList;

import logging.IssueLog;
import utilityClassesForObjects.ObjectContainer;

public abstract class ItemPicker<ItemType extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ItemType modelItem;
	
	public ItemPicker() {
		
	}
	
	public ItemPicker(ItemType model) {
		modelItem=model;
	}
	

	
	abstract boolean isDesirableItem(Object o);
	public abstract void applyProperties(Object item);
  public  void applyPropertiesToList(Iterable<?> item) {
	  
	  for(Object o: item) {
		  applyProperties(o);
	  }
  }
	
	
	@SuppressWarnings("unchecked")
	ArrayList<ItemType> getDesiredItemsOnly(ArrayList<?> input) {
		ArrayList<ItemType> output = new ArrayList<ItemType>();
		for(Object ob:input) {
			if (isDesirableItem(ob)) try {
				output.add((ItemType) ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return output;
	}
	
	public ArrayList<ItemType> getDesiredObjects(ObjectContainer oc) {
		return getDesiredItemsOnly(oc.getLocatedObjects());
	}
	
	public abstract String getOptionName() ;
	
	public String getKeyName() {
		return getOptionName();
	}



	public ItemType getModelItem() {
		return modelItem;
	}



	public void setModelItem(Object modelItem) {
		if (modelItem==null) {
			this.modelItem=null;
			return;
		}
		if (!isDesirableItem(modelItem)) return;
		try{this.modelItem = (ItemType)modelItem;} catch (Exception e) {
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}
	
	

}
