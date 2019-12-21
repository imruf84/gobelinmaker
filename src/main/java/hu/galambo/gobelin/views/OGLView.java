package hu.galambo.gobelin.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;

import hu.galambo.gobelin.three.ThreeComponent;

@Push
@SuppressWarnings("serial")
public class OGLView extends OGLDesign implements View {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(OGLView.class);
	
    
    public OGLView() {
    	init();
	}
    
    void init() {

    	ThreeComponent tc = new ThreeComponent();
    	tc.setSizeFull();
    	getOglLayout().addComponent(tc);
    	
    	getRotXSlider().addValueChangeListener(event -> {
    		tc.setRotX(event.getValue());
    		getRotLabel().setValue(event.getValue()+" deg");
    	});
    	
    	/*
    	MyComponent js = new MyComponent();
//    	js.setSizeFull();
    	js.setValue("0");
    	js.onChange(newValue -> Notification.show("New value: " + newValue));
    	layout.addComponent(js);
    	
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				if (getUI() == null) {
					return;
				}
				
				getUI().access(new Runnable() {
					@Override
					public void run() {
						int i = Integer.parseInt(js.getValue());
						i++;
						js.setValue(i+"");
					}
				});
				
			}
		}, 0, 1000);
    */	
    }
    
}
