package hu.galambo.gobelin.three;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

@SuppressWarnings("serial")
@JavaScript({"three.min.js", "three-component.js", "three-connector.js"})
public class ThreeComponent extends AbstractJavaScriptComponent {
	
private transient ArrayList<Consumer<String>> rotationListeners = new ArrayList<>();
    
    
    public void onRotationChange(Consumer<String> listener) {
        rotationListeners.add(listener);
    }
    
    public void setRotX(double x) {
		getState().setRotX(x);
		markAsDirty();
	}
	
	public double getRotX() {
		return getState().getRotX();
	}
	
	@Override
    public ThreeComponentState getState() {
        return (ThreeComponentState) super.getState();
    }
	
}
