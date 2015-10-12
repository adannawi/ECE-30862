
/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    private int damageMult = 1;
    private int SCORE = 0;   //default score is 0
    private int HEALTH_PT = 100; // base health of 100
    private boolean GOD = false;
    private boolean isLeft;
    private boolean isRight;
    private boolean onGround;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
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
    
    public void setGOD() {
    	GOD = true;
    }
    
    public boolean getGOD() {
    	return GOD;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }


	public void shoot() {
		
		
	}

	//DAMAGE MULTIPLIER VALUE SET/GET
	//USED FOR CHARGED WEAPONS
	public int getDamageMult() {
		return damageMult;
	}


	public void setDamageMult(int damageMult) {
		this.damageMult = damageMult;
	}

	//SCORE MANAGING
	public int getScore() {
		return SCORE;
	}


	public void incScore(int sCORE) {
		this.SCORE += sCORE;
	}

	//HEALTH MANAGING
	public int getHEALTH_PT() {
		return HEALTH_PT;
	}


	public void setHEALTH_PT(int hEALTH_PT) {
		HEALTH_PT = hEALTH_PT;
	}

}
