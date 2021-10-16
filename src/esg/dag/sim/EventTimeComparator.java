package esg.dag.sim;

import java.io.Serializable;
import java.util.Comparator;

public class EventTimeComparator implements Comparator<Event>,Serializable{

    @Override
	public int compare(Event e1, Event e2) {
		if(e1.getTime() >= e2.getTime())
			return 1;
		else
			return -1;
	}
}
