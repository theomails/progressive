package net.progressit.progressive.components;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.google.common.eventbus.EventBus;

import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleLabel extends PLeafComponent<String, String>{

	private JLabel label = new JLabel();
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
	protected JComponent getUiComponent() {
		return label;
	}

	@Override
	protected void renderSelf(String data) {
		label.setText(data);
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of();
	}

}
