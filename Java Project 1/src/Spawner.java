import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Spawner {
    ResourceManager rm;
    GameManager gm;
    
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }

    public void Grub(int playX, int playY) {
    	//Load img
    	Image image = loadImage("player1.png");
    	//Create animation
        Animation anim = new Animation();
        //Add frame to animation
        anim.addFrame(image, 100);
        //Create grub sprite
        Sprite grub = new Sprite(anim);
        
        rm.addSprite(gm.getMap(), grub, playX, playY);
           
    }
	
}