package net.progressit.fxtrial;

import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.net.URL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.progressit.jsonformat.ui.JsonFormatPane;
import net.progressit.progressive.PComponent;
import net.progressit.progressive.PComponent.PEventListener;
import net.progressit.progressive.PComponent.PPlacers;

public class Main extends Application {
    public static void main(String[] args) {
        URL iconURL = Main.class.getResource("scriptz-logo.png");
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final Image image = defaultToolkit.getImage(iconURL);

        //this is new since JDK 9
        final Taskbar taskbar = Taskbar.getTaskbar();
        try {
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(image);
        } catch (final UnsupportedOperationException e) {
            System.out.println("The os does not support: 'taskbar.setIconImage'");
        } catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }
        launch(args);
    }

    private StackPane root = new StackPane();
    private PPlacers simplePlacers = new PPlacers( (component)->root.getChildren().add(component), (component)->root.getChildren().remove(component) );
	private JsonFormatPane app = new JsonFormatPane(simplePlacers);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Json Ordered Formatter");
        //primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("scriptz-logo.png")));

        PComponent.place(app, new PEventListener() {}, new Object());
        
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}