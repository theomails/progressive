package net.progressit.progressive;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import lombok.Data;

public class PSimpleButton extends PComponent<String, String>{
	@Data
	public static class PSBActionEvent{
		private final ActionEvent event;
	}
	
	private JButton button = new JButton();
	public PSimpleButton(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<String> getDataPeekers() {
		return new PAllToSelfDataPeekers<String>();
	}

	@Override
	protected PRenderers<String> getRenderers() {
		return new PRenderers<String>( ()-> button, (data)->{
			button.setText(data);
		}, (data)-> new PChildrenPlan());
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				button.addActionListener((e)->{
					post(new PSBActionEvent(e));
				});
			}
			@Override
			public void postProps() {
				setData(getProps());
			}
		};
	}

}
