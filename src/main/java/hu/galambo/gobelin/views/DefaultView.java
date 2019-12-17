package hu.galambo.gobelin.views;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.mjvesa.threejs.ThreeJs;
import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.galambo.gobelin.views.DefaultDesign;
 
@Push
@SpringView(name = DefaultView.VIEW_NAME)
@SuppressWarnings("serial")
//public class DefaultView extends DefaultDesign implements View {
public class DefaultView extends Panel implements View {
 
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(DefaultView.class);
	
    public static final String VIEW_NAME = "";
    
    
    
private CssLayout objView;
    
    private static final String MATERIAL = "material";
    private static final String LIGHT = "light";
    private static final String OBJ = "obj";
    
    
    @Value("${app.version}")
	private String appVersion;
 
    @PostConstruct
    void init() {

    	HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
//        hl.addComponent(createObjectSelect());

        objView = new CssLayout();
        objView.setStyleName("demoContentLayout");
//        objView.setSizeFull();
        objView.setWidth("50%");
        objView.setHeight("50%");
        
        
        hl.addComponent(objView);
        hl.setExpandRatio(objView, 1);
        setContent(hl);
        setSizeFull();
        
        
        final ThreeJs three = new ThreeJs();
        three.setSizeFull();
        
        
        three.loadObj(OBJ, new ThemeResource("vaadin.obj"));                    

        three.createDirectionalLight(LIGHT, 0xffffff, 0.5);
        three.setDirectionalLightPosition(LIGHT, 0, 0, 2);
        three.addLight(LIGHT);
                                            
        three.createPhongMaterial(MATERIAL, 0x303030,
                0xdddddd, 0xffffff, 30);
//        three.loadTextureToMaterial(new ThemeResource("steel.jpg"), MATERIAL);
        three.setMaterialToObj(MATERIAL, OBJ);
        three.addObj(OBJ);
        
        objView.removeAllComponents();
        objView.addComponent(three);
        three.startAnimation();
    	
    }
 
    @Override
    public void enter(ViewChangeEvent event) {
    }

}