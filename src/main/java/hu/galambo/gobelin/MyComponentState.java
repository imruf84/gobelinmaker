package hu.galambo.gobelin;

import com.vaadin.shared.ui.JavaScriptComponentState;


@SuppressWarnings("serial")
public class MyComponentState extends JavaScriptComponentState {

    private String value;
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
