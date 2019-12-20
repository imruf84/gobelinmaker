package hu.galambo.gobelin.three;

import com.vaadin.shared.ui.JavaScriptComponentState;

@SuppressWarnings("serial")
public class ThreeComponentState extends JavaScriptComponentState {
	
	private double[] rotation = {0, 0, 0};

	
	public void setRotX(double x) {
		rotation[0] = x;
	}
	
	public double getRotX() {
		return rotation[0];
	}
}
