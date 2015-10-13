

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {

    public Fly(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation idleLeft, Animation idleRight, Animation jumpLeft, Animation jumpRight)
    {
        super(left, right, deadLeft, deadRight, idleLeft, idleRight, jumpLeft, jumpRight);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
