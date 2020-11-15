package utilityClassesForObjects;

import java.io.IOException;
import java.util.ArrayList;

import logging.IssueLog;
import utilityClasses1.ArraySorter;

public class LocationChangeListenerList extends
		ArrayList<LocationChangeListener> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int[] getInts() {
		return new int[] {9,6,7,5};
	}
	
	public static void main(String[] args) {
		
		for(int i: getInts()) {
			IssueLog.log("number "+i);
		}
		/**
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
	try {
		ObjectOutputStream oo = new ObjectOutputStream(bs);
		oo.writeObject(null);
		locationChangeListenerList ll = new locationChangeListenerList();
		//ll.dontserialize=true;
		oo.writeObject(ll);
		//oo.writeObject("Hello");
		oo.flush();
		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/

	}
	
	/**returns an array with all the listeners*/
	public LocationChangeListener[] getAll() {
		LocationChangeListener[] obs=new LocationChangeListener[this.size()];
		for(int i=0;i<obs.length; i++ ) {
			obs[i]=this.get(i);
		}
	
		return obs;
	}

	public void notifyListenersOfMoveMent(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectMoved(l);
		}
	
	}
	
	public void notifyListenersOfSizeChange(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectSizeChanged(l);
		}
		
	}
	
	public void notifyListenersOfUserSizeChange(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.userSizeChanged(l);
		}
		
	}
	
	public void notifyListenersOfDeath(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectEliminated(l);
		}
	}
	
	
	public void notifyListenersOfUserMove(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.userMoved(l);
		}
	}
	

	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		ArraySorter.removeDeadItems(this);
		ArraySorter.removeNonSerialiazble(this);
		out.defaultWriteObject();
	}
	
	
//	public static void main(String[] args) {}
	
	
}
