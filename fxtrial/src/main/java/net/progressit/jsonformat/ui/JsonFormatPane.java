package net.progressit.jsonformat.ui;

import org.tbee.javafx.scene.layout.MigPane;

import com.google.gson.JsonParseException;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import net.progressit.progressive.PAllToSelfDataPeekers;
import net.progressit.progressive.PComponent;

public class JsonFormatPane extends PComponent<JsonFormatterData, Object>{
	
	private JsonOrderedFormatBO bo = new JsonOrderedFormatBO();
	
	private boolean programSettingData = false;
	private MigPane mainPane = new MigPane("","[grow, fill]","[grow, fill][fill]");
	private TextArea taInput = new TextArea();
	private TextArea taOutput = new TextArea();
	private MigPane twoTexts = new MigPane("insets 0","[grow, fill][grow, fill]","[grow, fill]");
	private ScrollPane spInput = new ScrollPane(taInput);
	private ScrollPane spOutput = new ScrollPane(taOutput);
	private HBox controls = new HBox();
	private CheckBox chkPrettyPrint = new CheckBox("Pretty Print");
	private CheckBox chkSerializeNulls = new CheckBox("Serialize Nulls");
	public JsonFormatPane(PPlacers placers) {
		super(placers);
	}

	@Override
	protected PDataPeekers<JsonFormatterData> getDataPeekers() {
		return new PAllToSelfDataPeekers<JsonFormatterData>();
	}

	@Override
	protected PRenderers<JsonFormatterData> getRenderers() {
		return new PRenderers<JsonFormatterData> (
					()-> mainPane,
					(data)->{ 
						String resultJson = null; 
						try {
							String input = data.getInputJson();
							input = (input==null || "".equals(input.trim()))?"":input;
							resultJson = bo.orderAndFormatJson(input, data.isPrettyPrint(), data.isSerializeNulls());
						}catch(JsonParseException e) {
							resultJson = e.getLocalizedMessage();
						}catch(RuntimeException e) {
							resultJson = e.toString();
						}
						if(!taInput.getText().equals(data.getInputJson())) {
							programSettingData = true;
							taInput.setText(data.getInputJson());
							programSettingData = false;
						}
						if(data.isPrettyPrint() != chkPrettyPrint.isSelected()) {
							chkPrettyPrint.setSelected(data.isPrettyPrint());
						}
						if(data.isSerializeNulls() != chkSerializeNulls.isSelected()) {
							chkSerializeNulls.setSelected(data.isSerializeNulls());
						}
						taOutput.setText(resultJson);
					},
					(data)-> new PChildrenPlan()
				);
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				
				twoTexts.getChildren().addAll(spInput, spOutput) ;
				controls.getChildren().addAll( chkPrettyPrint, chkSerializeNulls );
				
				mainPane.add(twoTexts, "wrap") ;
				mainPane.add(controls, "") ;
				
				spInput.setFitToWidth(true);
				spInput.setFitToHeight(true);
				spOutput.setFitToWidth(true);
				spOutput.setFitToHeight(true);
				
				controls.setAlignment(Pos.BASELINE_RIGHT);
				controls.setSpacing(10);
				
				
				chkPrettyPrint.setOnAction((e)->{
					JsonFormatterData newData = getData().toBuilder().prettyPrint(chkPrettyPrint.isSelected()).build();
					setData(newData);
				});
				chkSerializeNulls.setOnAction((e)->{
					JsonFormatterData newData = getData().toBuilder().serializeNulls(chkSerializeNulls.isSelected()).build();
					setData(newData);
				});
				
				//Below gets fired even when we programmatically do setText on the UI field?
				taInput.textProperty().addListener( (observable, oldValue, newValue)->{
					//TODO: Does it stabilise and all the remove/insert events have fired?
					if(!programSettingData) {
						System.out.println("Data: " + getData());
						JsonFormatterData newData = getData().toBuilder().inputJson(newValue).build();
						setData(newData);
					}
				});
			}
			@Override
			public void postProps() {
				setData( new JsonFormatterData("", true, true) );
			}
		};
	}

}
