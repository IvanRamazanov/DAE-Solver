/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Ivan
 */
class View extends ImageView{
    View(String root,double x,double y){
        this.setImage(new Image(root));
        this.setSmooth(true);
        this.setPreserveRatio(true);
        double h=this.getImage().getHeight();
        this.setFitHeight(h/5);            
        this.setTranslateX(x);
        this.setTranslateY(y);
    }
}
