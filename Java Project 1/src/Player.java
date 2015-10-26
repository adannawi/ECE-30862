
/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    private int damageMult = 1;
    private int SCORE = 0;   //default score is 0
    private int maxHealth = 40; //max health is 40 for players
    
    //Booleans that control paramaters
    private boolean GOD = false; //God mode parameter
    private boolean onGround; //Is on ground parameter
    private boolean isMorphable; //Morphable paramater, player wont be able to morph ball unless this is true.
    private boolean hasMissiles; //Missiles parameter, allows player to use missiles.
    
    
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation idleLeft, Animation idleRight, Animation jumpLeft, Animation jumpRight)
    {
        super(left, right, deadLeft, deadRight, idleLeft, idleRight, jumpLeft, jumpRight);
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
            isJumping(false);
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
         //   isJumping(true);
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
            isJumping(!onGround);
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.24f;
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


}
