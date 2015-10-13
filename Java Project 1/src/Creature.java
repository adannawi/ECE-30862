
import java.lang.reflect.Constructor;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public abstract class Creature extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    
    private Animation left; //Move left
    private Animation right; //Move right
    private Animation deadLeft; //Dead while facing left
    private Animation deadRight; //Dead while facing right
    private Animation idleLeft; //Idle facing left
    private Animation idleRight; //Idle facing right
    private Animation jumpLeft; //Jump without pushing movement keys then left
    private Animation jumpRight; //Jump without pushing movement keys then right
    private Animation flipLeft; //Jump while moving left, causes a flip
    private Animation flipRight; //Jumps while moving right, causes a flip
    private Animation morphBall; //Morphball
    
    private boolean isLeft; //Triggered when creature is facing left
    private boolean isRight; //Triggered when creature is facing right
    private boolean isJumping = false;
    private int state;
    private int health = 60; //Health that can be removed, creature dies when this hits 0
    private long stateTime;

    /**
        Creates a new Creature with the specified Animations.
    */
    public Creature(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation idleLeft, Animation idleRight, Animation jumpLeft, Animation jumpRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        this.idleRight = idleRight;
        this.idleLeft = idleLeft;
        this.jumpLeft = jumpLeft;
        this.jumpRight = jumpRight;
        state = STATE_NORMAL;
    }

    public void setHealth(int health) {
    	this.health = health;
    }
    
    public void getShot() {
    	this.health -= 20;
    }
    
    public int health() {
    	return health;
    }
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone(),
                (Animation)idleLeft.clone(),
                (Animation)idleRight.clone(),
                (Animation)jumpLeft.clone(),
                (Animation)jumpRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    
    
    public void isLeft() {
    	isLeft = true;
    	isRight = false;
    }
    
    public void isRight() {
    	isLeft = false;
    	isRight = true;
    }
    
    public boolean facingLeft() {
    	if (isLeft == true) {
    		return true;
    	} else {
    		return false;
    	}
    }


    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }


    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this creature is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }


    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }
    
    public void isJumping(boolean state) {
    	if (state) {
    		isJumping = true;
    	} else {
    		isJumping = false;
    	}
    }


    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        
        //Handle jumping animations, stopping and resuming required when reaching max height and falling down
        if (isJumping) {
        	if (isLeft) {
    			newAnim = jumpLeft;
        		if (getVelocityY() > 0) {
        			if(jumpLeft.getFrame() == 5) {
        			   jumpLeft.stop();
        			}
        		}
        		if (getVelocityY() < 0) {
        			jumpLeft.resume();
        		}
        	}
        	if (isRight) {
        		newAnim = jumpRight;
        		if (getVelocityY() > 0) {
        			if(jumpRight.getFrame() == 5) {
        				jumpRight.stop();
        			}		
        		}
        		if (getVelocityY() < 0) {
        			jumpRight.resume();
        		}
        	}
        }
        
        //Spawn idling right
        if (getVelocityX() == 0 && (!isJumping)) {
        	newAnim = idleRight;
        }
        
        //Idle left animations
        if ((getVelocityX() == 0) && (isLeft) && (!isJumping)) {
        	newAnim = idleLeft;
        }
        
        //Idle right animations
        if ((getVelocityX() == 0) && (isRight) && (!isJumping)) {
        	newAnim = idleRight;
        }
      
        //Move left
        if (getVelocityX() < 0 && (!isJumping)) {
            newAnim = left;
        }
        
        //Move right
        else if (getVelocityX() > 0 && (!isJumping)) {
            newAnim = right;
        }
        
        //Die left
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        }
        
        //Die right
        else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
