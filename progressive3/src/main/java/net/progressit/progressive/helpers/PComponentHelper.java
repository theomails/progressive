package net.progressit.progressive.helpers;

import java.awt.Container;
import java.util.Set;

import javax.swing.JComponent;

import net.progressit.progressive.PChildrenPlan;
import net.progressit.progressive.PPlacers;

public class PComponentHelper {
	public static Set<Object> setWithNoData(){
		return Set.of();
	}
	public static Set<Object> setWithAllData(Object data){
		return Set.of(data);
	}
	public static void addToContainer(JComponent component, Container container) {
		container.add(component);
	}
	public static void removeFromContainer(JComponent component, Container container) {
		container.remove(component);
	}
	public static PChildrenPlan emptyChildrenPlan() {
		return new PChildrenPlan();
	}
	public static PPlacers newSimpleContainerPlacer(Container container) {
		return new PPlacers((c)->{container.add(c);}, (c)->{container.remove(c);});
	}
	public static PPlacers newSimpleContainerPlacer(Container container, boolean addWrap) {
		if(addWrap) {
			return new PPlacers((c)->{container.add(c, "wrap");}, (c)->{container.remove(c);});
		}else {
			return newSimpleContainerPlacer(container);
		}
	}
}
