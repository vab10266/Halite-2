package hlt;
public class PhantomShip extends Ship {
    public PhantomShip(Position p) {
        super(-1, -1, p.getXPos(), p.getYPos(),
                0, Ship.DockingStatus.Undocked, -1,
                -1, 0);
    }
}