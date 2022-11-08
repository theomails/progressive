package net.progressit.progressive;

import java.util.function.Consumer;

import javafx.scene.Node;
import lombok.Data;

/**
 * Holds the UI placer and remover, usually provided by the parent to a PComponent.
 * PComponent just holds the reference to PPlacers, whereas the framework takes care of using the PPlacer.
 * Usually the child PComponent's Nodes are placed into the Parent PComponent's Node.
 * 
 * @author theodore.r
 *
 */
@Data
public class PPlacers{
	final Consumer<Node> placer;
	final Consumer<Node> remover;		
}