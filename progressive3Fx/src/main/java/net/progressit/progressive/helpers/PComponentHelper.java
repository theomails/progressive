package net.progressit.progressive.helpers;

import java.util.Set;

import org.tbee.javafx.scene.layout.MigPane;

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
	public static PPlacers newSimpleContainerPlacer(MigPane container, boolean addWrap) {
		if(addWrap) {
			return new PPlacers((c)->{container.add(c, "wrap");}, (c)->{container.remove(c);});
		}else {
			return newSimpleContainerPlacer(container);
		}
	}
}
