
/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {

    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation idleLeft, Animation idleRight, Animation jumpLeft, Animation jumpRight)
    {
        super(left, right, deadLeft, deadRight, idleLeft, idleRight, jumpLeft, jumpRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
