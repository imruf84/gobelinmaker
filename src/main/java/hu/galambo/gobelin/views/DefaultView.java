package hu.galambo.gobelin.views;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.spring.annotation.SpringView;

import hu.galambo.gobelin.views.DefaultDesign;
 
@Push
@SpringView(name = DefaultView.VIEW_NAME)
@SuppressWarnings("serial")
public class DefaultView extends DefaultDesign implements View {
 
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(DefaultView.class);
	
    public static final String VIEW_NAME = "";
    
    @Value("${app.version}")
	private String appVersion;
 
    @PostConstruct
    void init() {
    	getLabel().setValue(appVersion);
    }
 
    @Override
    public void enter(ViewChangeEvent event) {
    }

}