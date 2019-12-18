package hu.galambo.gobelin.views;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import hu.galambo.gobelin.ThreeComponent;
 
@Push
@SpringView(name = DefaultView.VIEW_NAME)
@SuppressWarnings("serial")
public class DefaultView extends DefaultDesign implements View {
//public class DefaultView extends Panel implements View {
 
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(DefaultView.class);
	
    public static final String VIEW_NAME = "";
    
    
    @Value("${app.version}")
	private String appVersion;
 
    @PostConstruct
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
 
    @Override
    public void enter(ViewChangeEvent event) {
    }

}