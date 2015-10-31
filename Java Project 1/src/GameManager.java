
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.*;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ListIterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;



/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private Sound shootSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;
    private Spawner spawn;
    private Creature aggressor;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction shoot;
    private GameAction crouch;

    

    public void init() {
        super.init();
        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground( null);
            //resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();
        

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");
        shootSound = soundManager.getSound("sounds/shoot1.wav");
        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =  midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
      //  toggleDrumPlayback();
        
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        shoot = new GameAction("shoot");
        crouch = new GameAction("crouch", GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(crouch, KeyEvent.VK_DOWN);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_S);
    }

    //VARIABLES FOR CHECKINPUT, UNSURE HOW TO DECLARE OTHERWISE
    private long timer = 2000;
    private int bulletCounter = 0;
    private long eventTime = 0;
    private long coolDownTime = 0;
    private long hitTime = 0;
    private long idleTime = 0;
    
    private boolean isCooling = false;
   	boolean prevState = false;
    
    private void checkInput(long elapsedTime) {

    	if (((System.currentTimeMillis() - eventTime) > 20) && !isCooling) {
    		timer = 2000;
    		bulletCounter = 0;
    	}
    	
    	if ((( System.currentTimeMillis() - coolDownTime) > 1000) && isCooling) {
    		bulletCounter = 0;
    		isCooling = false;
    	}

        if (exit.isPressed()) {
            stop();
        }
        

        Player player = (Player)map.getPlayer();
        
        
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                player.isLeft();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                player.isRight();
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            if (shoot.isPressed()) {
            	if (timer % 500 != 0) {
                   //do nothing if modulo isnt 0 		
            	}else if(!isCooling){
            		if (bulletCounter == 10) { //check if 10 bullets have been fired
            			isCooling = true;
            			coolDownTime = System.currentTimeMillis();
            		}
            			soundManager.play(shootSound);
            			ResourceManager.spawnSomething(getMap());   

            			prevState = true;
            			bulletCounter++;
            	}
            	eventTime =  System.currentTimeMillis();
            	timer+=100;
            }
            if (moveLeft.isPressed() && moveRight.isPressed()) {
            	//do nothing
            }
            player.setVelocityX(velocityX);
        }
        

    }


    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());
        Creature player = (Creature)map.getPlayer();
        String health = ""+player.health();
        //display health
        g.setColor(Color.WHITE);
        g.drawString("Health:"+health, screen.getWidth()/10, screen.getHeight()/6);
        
        //debugging
        g.setColor(Color.RED);
        g.drawString("Idle Count"+(System.currentTimeMillis() - idleTime), screen.getWidth()/10, screen.getHeight()/7);
        
        g.setColor(Color.BLUE);
        g.drawString("Immunity:"+(System.currentTimeMillis() - hitTime), screen.getWidth()/10, screen.getHeight()/8);
        
        g.setColor(Color.CYAN);
        g.drawString("X Velocity: "+player.getVelocityX()+" Y Velocity: "+player.getVelocityY(), screen.getWidth()/10, screen.getHeight()/5);
        
        g.setColor(Color.GREEN);
        g.drawString("Idle State:"+player.getIdle(), screen.getWidth()/10, screen.getHeight()/4);
        
        g.setColor(Color.RED);
        g.drawString("(X,Y): ("+player.getX()+","+player.getY()+")", screen.getWidth()/10, screen.getHeight()/3);
        
        g.setColor(Color.YELLOW);
        g.drawString("Is player on ground: "+((Player) player).isOnGround(), screen.getWidth()/10, screen.getHeight()/3.5f);
        
        if (aggressor != null) {
        g.setColor(Color.PINK);
        g.drawString("Aggressor stats: (X,Y): ("+this.aggressor.getX()+","+this.aggressor.getY()+")", screen.getWidth()/10, screen.getHeight()/2.5f);
        }else{
        	 g.drawString("Awaiting aggressor...", screen.getWidth()/10, screen.getHeight()/2.5f);
        }
        
        if (aggressor != null) {
        g.drawString("Spotted!", screen.getWidth()/10, screen.getHeight()/2);
        }
        
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {

        Creature player = (Creature)map.getPlayer();
        Graphics2D g = screen.getGraphics();
        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }
        
        //Increment health by 1 for every tile walked on
        if (Math.abs(player.getX() - player.getSpawnX()) > 64) {
           player.setSpawnX(player.getX());
           player.addHealth(1);
        }
        
        //Check if 1 second passed for immunity
        if (((System.currentTimeMillis() - hitTime) % 1000) == 0) {
        	player.setImmunity(false);
        }
        
        //Check if player is idle
        if (player.getVelocityX() == 0 && player.getVelocityY() < 0.2) {
        	player.setIdle(true);
        } else {player.setIdle(false);}
        
        //Get start time
        if (!player.getIdle()){
        	idleTime = System.currentTimeMillis();
        }
        //Cheese it up, baby!
        if (player.getIdle()) {
        		if ((System.currentTimeMillis() - idleTime > 1000)) {
        				player.addHealth(1);
        				idleTime = System.currentTimeMillis();
        			}
        }
        
       

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                
                if (creature.getState() == Creature.STATE_DEAD) {
                	if (creature == this.aggressor) {
                		this.aggressor = null;      	
                	}
                	player.addHealth(5);             	
                    i.remove();
                }
                else { //Make the monsters chase the player
                	if (Math.abs(player.getX() - creature.getX()) < 500) { //is within 500 units
                		if (creature instanceof Fly) {
                			if (player.getY() < creature.getY()) { 
                				creature.setVelocityY(-0.02f);
                			}
                			if (player.getY() > creature.getY()) {
                				creature.setVelocityY(0.02f);
                			}
                		}
                		
                	if (((player.getX() < creature.getX()))){  //is player to the left of creature
                		if (creature instanceof Fly) {  //is this creature a fly
                		creature.setVelocityX(-creature.getMaxSpeed()/1.8f); //slow it down so player can jump away and not get rekt
                		}else{	//creature isn't a fly, good, let it chase at full speed
            			creature.setVelocityX(-creature.getMaxSpeed());
                		}
                	} else if (((player.getX() > creature.getX()))){  
                		if (creature instanceof Fly) { 
                		creature.setVelocityX(creature.getMaxSpeed()/1.8f);
                		}else{
            			creature.setVelocityX(creature.getMaxSpeed());
                		}
                	}
                	
                	if (Math.abs(player.getX() - creature.getX()) < (64*4)){ // once within 100 meters do this
                		if (player.getX() < creature.getX()) {
                	//       ResourceManager.shootPlayer(map, creature); //crashes
                			if ((creature instanceof Grub) && (creature.facingLeft())){
            					this.aggressor = creature;
            			    }

                		}
                		if (player.getX() > creature.getX()) {
                 	//	   ResourceManager.spawnSomething(map); //crashes
                			if ((creature instanceof Grub) && (!creature.facingLeft())){
            					this.aggressor = creature;
            			    }
                		}
                	} else {this.aggressor = null;}
                	}
                    updateCreature(creature, elapsedTime);
                }
            }
            //Update projectiles
            if (sprite instanceof Projectile) {
            	Projectile projectile = (Projectile)sprite;
            	updateProjectiles(projectile, elapsedTime);
            	if (projectile.getImpact() == 1) {
            		i.remove();
            		projectile.clear();
            	}
            }
            // normal update
            sprite.update(elapsedTime);
        }
        if (this.aggressor != null) {
        ResourceManager.shootPlayer(map, this.aggressor);
        }
    }
    
    /**Update projectiles currently in map, make sure they despawn when they collide with a tile, if enemy, hurt them
    * 
    */
    private void updateProjectiles(Projectile projectile, long elapsedTime) {
    	//change x
    	float dx = projectile.getVelocityX();
    	float oldX = projectile.getX();
    	float newX = oldX + dx * elapsedTime;
    	Point tile = getTileCollision(projectile, newX, projectile.getY());
    	if ((newX - projectile.getSpawnX())> 640) { //Make the bullets fizzle out after 10 tiles
    		projectile.impact();
    	}
    	if (tile == null) {
    		projectile.setX(newX);
    	} else {
    		projectile.impact();
    	}
    	checkBulletCollision(projectile, true);
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {
    	
    	if (this.aggressor == creature) {
       		
       	}
    	
    	if (creature.health() < 0) {
    		creature.setState(Creature.STATE_DYING);
    		creature.setVelocityX(0);
    		creature.setVelocityY(0);
    	}
        // apply gravity 
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        
        
        if (newX < oldX) { creature.isLeft(); }
        if (newX > oldX) { creature.isRight(); }
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();        
        }
        
        //Set max health and enforce it for player and enemies
        if (creature instanceof Player) {
        	if (creature.health() > creature.maxHealthPlayer()) {
        		creature.setHealth(creature.maxHealthPlayer());
        	}
        }else{
        	if (creature.health() > creature.maxHealthEnemy()) {
        		creature.setHealth(creature.maxHealthEnemy());
        	}
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }
        

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                
                player.addHealth(10);
               
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
            }
            else {
                // player dies! (unless godmode)
            	if (!player.getImmunity()) { 
            	  player.getHit(5);
            	  player.setImmunity(true);
            	  hitTime = System.currentTimeMillis();
                  
            	}
            }
        }
    }
    
    /**
     * Checks to see if the bullets have hit anything that is killable.
     * @param projectile
     * @param canKill
     */
    public void checkBulletCollision(Projectile projectile, boolean canKill) {
    	Sprite collisionSprite = getSpriteCollision(projectile);
    	if (collisionSprite instanceof Creature) {
    		Creature badguy = (Creature)collisionSprite;
    		if (canKill) {
    			badguy.getHit(30);
    			projectile.impact();
    		}
    	}
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);
        Creature player = (Creature)map.getPlayer();
        
        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
        	player.addHealth(1);
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            //toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }

}
