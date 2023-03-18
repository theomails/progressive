package net.progressit.progressive.components;

import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.eventbus.EventBus;

import lombok.Data;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class PSimpleTextArea extends PLeafComponent<String, String>{

	@Data
	public static class ValueEvent{
		private final String value;
	}
	
	private JTextArea textArea = new JTextArea();
	private JScrollPane spTextArea = new JScrollPane(textArea);
	public PSimpleTextArea(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				
				//Below gets fired even when we programmatically do setText on the UI field.
				textArea.getDocument().addDocumentListener(new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						postChange();
					}
					public void removeUpdate(DocumentEvent e) {
						postChange();
					}
					public void insertUpdate(DocumentEvent e) {
						postChange();
					}
					
					private void postChange() {
						//Needed because getText doesn't stabilise until all the remove/insert events have fired.
						SwingUtilities.invokeLater(()->{
							String text = textArea.getText();
							post(new ValueEvent(text));
						});
					}
				});
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

	@Override
	protected JComponent getUiComponent() {
		return spTextArea;
	}

	@Override
	protected void renderSelf(String data) {
		if(!textArea.getText().equals(data)) {
			textArea.setText(data);
		}
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return Arrays.asList(ValueEvent.class);
	}

}
