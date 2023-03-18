package net.progressit.progressive.helpers;

import java.util.Set;

import com.google.common.collect.Sets;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import net.progressit.progressive.PChildrenPlan;
import net.progressit.progressive.PPlacers;

public class PComponentHelper {
	public static Set<Object> setWithNoData(){
		return Sets.newHashSet();
	}
	public static Set<Object> setWithAllData(Object data){
		return Sets.newHashSet(data);
	}
	public static void addToContainer(Node component, Pane container) {
		container.getChildren().add(component);
	}
	public static void removeFromContainer(Node component, Pane container) {
		container.getChildren().remove(component);
	}
	public static PChildrenPlan emptyChildrenPlan() {
		return new PChildrenPlan();
	}
	public static PPlacers newSimpleContainerPlacer(Pane container) {
		return new PPlacers((c)->{container.getChildren().add(c);}, (c)->{container.getChildren().add(c);});
	}
}
