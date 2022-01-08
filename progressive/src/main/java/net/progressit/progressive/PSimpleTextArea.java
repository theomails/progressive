package net.progressit.progressive;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lombok.Data;

public class PSimpleTextArea extends PComponent<String, String>{

	@Data
	public static class PSTAValueEvent{
		private final String value;
	}
	
	private JTextArea textArea = new JTextArea();
	private JScrollPane spTextArea = new JScrollPane(textArea);
	public PSimpleTextArea(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToSelfDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> spTextArea, (data)->{
			if(!textArea.getText().equals(data)) {
				textArea.setText(data);
			}
		}, (data)-> new PChildrenPlan());

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
							post(new PSTAValueEvent(text));
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

}
