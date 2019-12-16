package hu.galambo.gobelin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
 
@Push
@EnableAsync
@SpringViewDisplay
@Title(value = "Gobelin Maker")
@SpringUI(path = MainUI.APP_ROOT)
@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
@SuppressWarnings("serial")
@Theme("MyTheme")
public class MainUI extends UI implements ViewDisplay {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MainUI.class);
 
    static final String APP_ROOT = "/gobelin";
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    }
 
    @Override
    public void showView(View view) {
        setContent((Component) view);
    }
 
}