
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.sound.midi.Sequence;
import javax.swing.ImageIcon;




/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private static Sprite grubSprite;
    private Sprite flySprite;
    private static Sprite bulletSprite;
    private MidiPlayer midiPlayer;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }
   
    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }
    
    //CODE TO ALLOW SPAWNING OF SPRITES WILLINGLY
    public static void spawnSomething(TileMap map) {
    	Sprite bullet = (Sprite)bulletSprite.clone();
    	Sprite grub = (Sprite)grubSprite.clone();
    	Sprite player = map.getPlayer();
    	
    	//set bullet on position of player
    	bullet.setX(player.getX());
    	bullet.setY(player.getY() + (player.getHeight()*0.4f));
    	bullet.setSpawnX(player.getX());
    	
    	//check which direction the player is facing
    	int dir = 0;
    	if (((Player) player).facingLeft() == true) { 
    		dir = -1;
    	}else{ 
    		dir = 1;
    	}
    	
    	//set the velocity
    	bullet.setVelocityX(dir * 1.2f);
    	
    	//bang!
        map.addSprite(bullet);
        
    }
    
    public static void shootPlayer(TileMap map, Creature creature) {
       	Sprite bullet2 = (Sprite)bulletSprite.clone();
    	
    	//set bullet on position of player
    	bullet2.setX(creature.getX());
    	bullet2.setY(creature.getY());
    	bullet2.setSpawnX(creature.getX());
    	
    	//check which direction the player is facing
    	int dir = 0;
    	if (creature.facingLeft() == true) { 
    		dir = -1;
    	}else{ 
    		dir = 1;
    	}
    	
    	//set the velocity
    	bullet2.setVelocityX(dir * 1.2f);
    	
    	//bang!
        map.addSprite(bullet2);
        
    }


    public void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {
    	
    	//MY CUSTOM SPRITE SHEET LOADING CODE
    	
    	BufferedImage playerRunSheet = null;
    	BufferedImage playerIdleSheet = null;
    	BufferedImage JumpShootSheet = null;
    	BufferedImage PlayerFlipSheet = null;
    	BufferedImage PlayerJumpSheet = null;
    	BufferedImage PlayerMorphSheet = null;
    	
    	//Load the spritesheets, if an error occurs, catch it!
    	try {
    	playerRunSheet = ImageIO.read(new File("spritesheet/playerRunLeft.png"));
    	playerIdleSheet = ImageIO.read(new File("spritesheet/playerIdleLeft.png"));
    	PlayerFlipSheet = ImageIO.read(new File("spritesheet/PlayerFlipLeft.png"));
    	PlayerJumpSheet = ImageIO.read(new File("spritesheet/PlayerJumpLeft.png"));
    	JumpShootSheet = ImageIO.read(new File("spritesheet/JumpShootLeft.png"));
    	PlayerMorphSheet = ImageIO.read(new File("spritesheet/PlayerMorphBall.png"));
    	}  catch (IOException e) {
    		e.printStackTrace();
    	}
    	////////////////////////////////////////////////////////////////
    	
    	//Initialize player running arrays
    	Image[][] playerRun = new Image[10][];
    	playerRun[0] =  new Image[10];
    	playerRun[1] =  new Image[playerRun[0].length];
    	playerRun[2] = new Image[playerRun[0].length];
    	playerRun[3] = new Image[playerRun[0].length];
    	
    	//Initialize player idle arrays
    	Image[][] playerIdle = new Image[3][];
    	playerIdle[0] = new Image[3];
    	playerIdle[1] = new Image[playerIdle[0].length];
    	
    	//Initialize player jump arrays
    	Image[][] playerJump = new Image[2][];
    	playerJump[0] = new Image[9];
    	playerJump[1] = new Image[playerJump[0].length];
    	
    	//Initialize player flip arrays
    	Image[][] playerFlip = new Image[2][];
    	playerFlip[0] = new Image[8];
    	playerFlip[1] = new Image[playerFlip[0].length];
    	
    	//Initialize player jump shoot arrays
    	Image[][] playerJumpShoot = new Image[2][];
    	playerJumpShoot[0] = new Image[1];
    	playerJumpShoot[1] = new Image[playerJumpShoot[0].length];
    	
    	//Initialize player morph arrays
    	Image[][] playerMorph = new Image[2][];
    	playerMorph[0] = new Image[7];
    	playerMorph[1] = new Image[playerMorph[0].length];
    	
    	
    	///////////////////////////////////////////////////////////////
    	int gridx = 0;
    	int gridy = 0;
    	
    	//Create player running image array
    	for(int i=0; i < playerRun[0].length; i++) {
    		playerRun[0][i] = playerRunSheet.getSubimage(gridx, gridy, 64 , 86); //run left
    		gridx += 70;
    	}
    	
    	gridx = 0;
  
    	//Create player idle image array
    	for(int i=0; i < playerIdle[0].length; i++) {
    		playerIdle[0][i] = playerIdleSheet.getSubimage(gridx, gridy, 53, 86); //idle left
    		gridx += 55;
    	}
    	
    	gridx = 0;
    	
    	//Create player jump image array
    	for (int i=0; i < playerJump[0].length; i++) {
    		playerJump[0][i] = PlayerJumpSheet.getSubimage(gridx, gridy, 60, 90);
    		gridx += 60;
    	}
    	
    	gridx = 0;
    	////////////////////////////////////////////////////////////////////////////
    	
    	//Create player running sets, playerRun[1] right, [2] left flip, [3] right flip
    	for (int i=0; i<playerRun[0].length; i++) {
    		playerRun[1][i] = getMirrorImage(playerRun[0][i]); //running right
    		playerRun[2][i] = getFlippedImage(playerRun[0][i]);
    		playerRun[3][i] = getFlippedImage(playerRun[1][i]);
    	}
    	
    	//Create flipped player idle
    	for (int i=0; i<playerIdle[0].length; i++) {
    		playerIdle[1][i] = getMirrorImage(playerIdle[0][i]); //idle right
    	}
    	
    	//Create flipped player jump
    	for (int i=0; i<playerJump[0].length; i++) {
    		playerJump[1][i] = getMirrorImage(playerJump[0][i]);
    	}
    	

        //BRACKEENS CODE
    	
        Image[][] images = new Image[4][];
        
        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
            loadImage("Beam1Right.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] playerRunAnim = new Animation[4];
        Animation[] playerIdleAnim = new Animation[2];
        Animation[] playerJumpAnim = new Animation[2];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        Animation[] bulletAnim = new Animation[2]; //Left and right
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);
        }
        
        for (int i = 0; i < 4; i++) {
        	playerRunAnim[i] = createPlayerRunAnim(playerRun[i][0], playerRun[i][1], playerRun[i][2], playerRun[i][3], playerRun[i][4], playerRun[i][5], playerRun[i][6], playerRun[i][7], playerRun[i][8], playerRun[i][9]);
        }
        
        for (int i = 0; i < 2; i++){
        	playerIdleAnim[i] = createPlayerIdleAnim(playerIdle[i][0], playerIdle[i][1], playerIdle[i][2]);
        }
        
        for (int i=0; i<2; i++) {
        	bulletAnim[i] = createBulletAnim(images[i][8]);
        }
        
        for (int i = 0; i < 2; i++) {
        	playerJumpAnim[i] = createPlayerJumpAnim(playerJump[i][0], playerJump[i][1], playerJump[i][2], playerJump[i][3], playerJump[i][4], playerJump[i][5], playerJump[i][6], playerJump[i][7], playerJump[i][8]);
        }
        

        // create creature sprites || [left move, right move, left die, right die, idle left, idle right]
      //  playerSprite = new Player(playerAnim[0], playerAnim[1], playerAnim[2], playerAnim[3]);
        playerSprite = new Player(playerRunAnim[0], playerRunAnim[1], playerRunAnim[2], playerRunAnim[3], playerIdleAnim[0], playerIdleAnim[1], playerJumpAnim[0], playerJumpAnim[1]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3], flyAnim[3], flyAnim[3], flyAnim[3], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3], grubAnim[3], grubAnim[3], grubAnim[3], grubAnim[3]);
        bulletSprite = new Projectile(bulletAnim[0], bulletAnim[1]);
    }

    /**
     * Unused function as it stands
     * 
     * 
     * 
     * 
     */
    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 200);
        anim.addFrame(player2, 150);
        return anim;
    }
    
    /**
     *  Function that creates the running animation for the player
	 *
     */

    private Animation createPlayerRunAnim(Image play1, Image play2, Image play3, Image play4, Image play5, Image play6, Image play7, Image play8, Image play9, Image play10) {
    	Animation anim = new Animation();
    	anim.addFrame(play1, 60);
    	anim.addFrame(play2, 60);
    	anim.addFrame(play3, 60);
    	anim.addFrame(play4, 60);
    	anim.addFrame(play5, 60);
    	anim.addFrame(play6, 60);
    	anim.addFrame(play7, 60);
    	anim.addFrame(play8, 60);
    	anim.addFrame(play9, 60);
    	anim.addFrame(play10, 60);
    	
    	return anim;
    }
    
    /**
     *  Function that creates the idle animation for the player
     */
    private Animation createPlayerIdleAnim(Image idle1, Image idle2, Image idle3) {
    	Animation anim = new Animation();
    	anim.addFrame(idle1, 400);
    	anim.addFrame(idle2, 400);
    	//anim.addFrame(idle3, 500);
    //	anim.addFrame(idle2, 200);
    	return anim;
    }
    
    /**
     *  Function that creates the jump animations for the player
     */
    private Animation createPlayerJumpAnim(Image jump1, Image jump2, Image jump3, Image jump4, Image jump5, Image jump6, Image jump7, Image jump8, Image jump9) {
    	Animation anim = new Animation();
    	anim.addFrame(jump1, 50);
    	anim.addFrame(jump2, 50);
    	anim.addFrame(jump3, 50);
    	anim.addFrame(jump4, 50);
    	anim.addFrame(jump5, 50);
    	anim.addFrame(jump6, 50);
    	anim.addFrame(jump7, 50);
    	anim.addFrame(jump8, 50);
    	anim.addFrame(jump9, 50);
    //	System.out.println("Created jumping animations!");
    	return anim;
    }
    /**
     * Function that creates animations for flies
     */

    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }

    /**
     *  Function that creates animations for grubs
     */
    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }
    
    /**
     * Function that creates bullet animations
     */
    private Animation createBulletAnim(Image bullet) {
    	Animation anim = new Animation();
    	anim.addFrame(bullet, 100);
    	return anim;
    }

    /**
     * Load all the power up sprites that are available.
     */
    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("heart1.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        anim.addFrame(loadImage("heart3.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("star1.png"), 70);
        anim.addFrame(loadImage("star2.png"), 50);
        anim.addFrame(loadImage("star3.png"), 70);
        anim.addFrame(loadImage("star4.png"), 50);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }
}
