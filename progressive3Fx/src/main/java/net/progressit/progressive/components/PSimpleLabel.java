package net.progressit.progressive.components;

import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;

import javafx.scene.Node;
import javafx.scene.control.Label;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleLabel extends PLeafComponent<String, String>{

	private Label label = new Label();
	public PSimpleLabel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

	@Override
	protected Node getUiComponent() {
		return label;
	}

	@Override
	protected void renderSelf(String data) {
		label.setText(data);
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return Arrays.asList();
	}

}
