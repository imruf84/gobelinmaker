package hu.galambo.gobelin.mycomponent;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;

@SuppressWarnings("serial")
@JavaScript({"mylibrary.js", "mycomponent-connector.js"})
public class MyComponent extends AbstractJavaScriptComponent {

	private transient ArrayList<Consumer<String>> listeners = new ArrayList<>();
    
    
    public void onChange(Consumer<String> listener) {
        listeners.add(listener);
    }
    
    public void setValue(String value) {
        getState().setValue(value);
        markAsDirty();
    }
    
    public String getValue() {
        return getState().getValue();
    }

    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }
    
    public MyComponent() {
        addFunction("onClick", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                getState().setValue(arguments.getString(0));
                for (Consumer<String> listener: listeners)
                    listener.accept(arguments.getString(0));
            }
        });
    }
}
