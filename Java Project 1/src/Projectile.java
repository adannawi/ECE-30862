
import java.lang.reflect.Constructor;

/**
    A Projectile is a Sprite that is not affected by gravity and can not die. It has one animation: moving.
*/
public class Projectile extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private Animation movingLeft;
    private Animation movingRight;
    
    private boolean isLeft;
    private boolean isRight;
    
    public static int IMPACT = 0;


    /**
        Creates a new Creature with the specified Animations.
    */
    public Projectile(Animation movingLeft, Animation movingRight)
    {
        super(movingRight);
        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
    }
    
    public void isLeft() {
    	isLeft = true;
    	isRight = false;
    }
    
    public void isRight() {
    	isLeft = false;
    	isRight = true;
    }
    
    public void impact() {
    	IMPACT = 1;
    }
    
    public void clear() {
    	IMPACT = 0;
    }
    
    public int getImpact() {
    	return IMPACT;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)movingLeft.clone(),
                (Animation)movingRight.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 5;
    }


    /**
        Checks if this creature is alive.
    */



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


    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = movingLeft;
        }
        else if (getVelocityX() > 0) {
            newAnim = movingRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }
        }
    }

