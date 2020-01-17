package hlt;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.NavigableMap;

public class Ship extends Entity {

    public enum DockingStatus { Undocked, Docking, Docked, Undocking }

    private final DockingStatus dockingStatus;
    private final int dockedPlanet;
    private final int dockingProgress;
    private final int weaponCooldown;
    public boolean movedThisTurn;

    public Ship(final int owner, final int id, final double xPos, final double yPos,
                final int health, final DockingStatus dockingStatus, final int dockedPlanet,
                final int dockingProgress, final int weaponCooldown) {

        super(owner, id, xPos, yPos, health, Constants.SHIP_RADIUS);

        this.dockingStatus = dockingStatus;
        this.dockedPlanet = dockedPlanet;
        this.dockingProgress = dockingProgress;
        this.weaponCooldown = weaponCooldown;
    }

    public int getWeaponCooldown() {
        return weaponCooldown;
    }

    public DockingStatus getDockingStatus() {
        return dockingStatus;
    }

    public int getDockingProgress() {
        return dockingProgress;
    }

    public int getDockedPlanet() {
        return dockedPlanet;
    }

    public boolean canDock(final Planet planet) {
        return getDistanceTo(planet) <= Constants.DOCK_RADIUS + planet.getRadius();
    }

    @Override
    public String toString() {
        return "Ship[" +
                super.toString() +
                ", dockingStatus=" + dockingStatus +
                ", dockedPlanet=" + dockedPlanet +
                ", dockingProgress=" + dockingProgress +
                ", weaponCooldown=" + weaponCooldown +
                "]";
    }
    public Move takeOver(Planet p, GameMap gameMap, ArrayList<Entity> stuff) {
        TreeMap<Double, Ship> ships = gameMap.nearbyShipsByDistance(p);
        NavigableMap<Double, Ship> closeShips = ships.subMap(0.0, false, p.getRadius() + Constants.DOCK_RADIUS, true);
        DebugLog.addLog("");
        Object[] shipKeys = (closeShips.keySet().toArray());
        for(Object o :shipKeys) {
            Ship s = closeShips.get(o);
            if (s.getOwner() == this.getOwner()) {
                closeShips.remove(o);
                //DebugLog.addLog(s.toString());
            }

        }
        shipKeys = (closeShips.keySet().toArray());
        DebugLog.addLog("0");
        if(shipKeys.length == 0) {
            DebugLog.addLog("1");
            if (this.canDock(p)) {
                DebugLog.addLog("2");
                return (new DockMove(this, p));
            }
            DebugLog.addLog("3");
            final ThrustMove newThrustMove = new Navigation(this, p).navigateToDock(gameMap, Constants.MAX_SPEED, stuff);
            if (newThrustMove != null) {
                DebugLog.addLog("4");
                return (newThrustMove);
            }
        } else {
            TreeMap<Double, Ship> closeShipByDistance = new TreeMap<>();
            TreeMap<Double, Ship> closeDockedShipByDistance = new TreeMap<>();
            for(Object o :shipKeys) {
                Ship s = closeShips.get(o);
                if(s.getDockingStatus() != DockingStatus.Undocked) {
                    closeDockedShipByDistance.put(this.getDistanceTo(s), s);
                }
                closeShipByDistance.put(this.getDistanceTo(s), s);
            }
            Object[] closeShipKeys = (closeShipByDistance.keySet().toArray());
            Object[] dockedShipKeys = (closeDockedShipByDistance.keySet().toArray());
            if (closeDockedShipByDistance.size() > 0) {
                ThrustMove newThrustMove = new Navigation(this, closeDockedShipByDistance.get(dockedShipKeys[0])).navigateToAttack(gameMap, stuff);
                if (newThrustMove != null) {
                    DebugLog.addLog("5");
                    return (newThrustMove);
                }
            } else {
                ThrustMove newThrustMove = new Navigation(this, closeShipByDistance.get(closeShipKeys[0])).navigateToAttack(gameMap, stuff);
                if (newThrustMove != null) {
                    DebugLog.addLog("6");
                    return (newThrustMove);
                }
            }

        }
        return new ThrustMove(this, 0, 0);

    }

}
