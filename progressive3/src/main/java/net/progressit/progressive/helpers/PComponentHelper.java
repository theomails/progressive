package net.progressit.progressive.helpers;

import java.awt.Container;
import java.util.Set;

import javax.swing.JComponent;

import net.progressit.progressive.PChildrenPlan;

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
}
