package hu.galambo.gobelin;

import javax.servlet.annotation.WebServlet;

import org.springframework.scheduling.annotation.EnableAsync;

import com.github.mjvesa.threejs.ThreeJs;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

//@Theme("demo")
//@Push
//@EnableAsync
//@SpringViewDisplay
//@Title(value = "Gobelin Maker")
//@SpringUI(path = DemoUI.APP_ROOT)
//@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
//@Theme("mytheme")
//@SuppressWarnings("serial")
public class DemoUI /*extends UI*/ {
//
//	static final String APP_ROOT = "/ogl";
//	
//    private CssLayout objView;
//    
//    private static final String MATERIAL = "material";
//    private static final String LIGHT = "light";
//    private static final String OBJ = "obj";
//
//    public enum OBJECT {
//        VAADIN, CASTLE, MOLECULE, CAFFEINE;
//    }
//
//    @WebServlet(value = "/*", asyncSupported = true)
//    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class,
//            widgetset = "com.github.mjvesa.threejs.demo.DemoWidgetSet")
//    public static class Servlet extends VaadinServlet {
//    }
//
//    @Override
//    protected void init(VaadinRequest request) {
//        HorizontalLayout hl = new HorizontalLayout();
//        hl.setSizeFull();
////        hl.addComponent(createObjectSelect());
//
//        objView = new CssLayout();
//        objView.setStyleName("demoContentLayout");
//        objView.setSizeFull();
//        
//        
//        hl.addComponent(objView);
//        hl.setExpandRatio(objView, 1);
//        setContent(hl);
//        
//        
//        final ThreeJs three = new ThreeJs();
//        three.setSizeFull();
//        
//        
////        three.loadObj(OBJ, new ThemeResource("vaadin.obj"));                    
//
//        three.createDirectionalLight(LIGHT, 0xffffff, 0.5);
//        three.setDirectionalLightPosition(LIGHT, 0, 0, 2);
//        three.addLight(LIGHT);
//                                            
////        three.createPhongMaterial(MATERIAL, 0x303030,
////                0xdddddd, 0xffffff, 30);
////        three.loadTextureToMaterial(new ThemeResource("steel.jpg"), MATERIAL);
////        three.setMaterialToObj(MATERIAL, OBJ);
////        three.addObj(OBJ);
//        
//        objView.removeAllComponents();
//        objView.addComponent(three);
//        three.startAnimation();
//        
//    }

}