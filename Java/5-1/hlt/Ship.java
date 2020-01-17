package hlt;
import java.util.ArrayList;
import java.util.Map;

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
        if (p.getOwner() != this.getOwner() && p.isOwned()) {
            // ArrayList<Ship> enemyShips = new ArrayList<>();
            // for (final Ship ship : gameMap.getAllShips()) {
            //     if (this.getDistanceTo(p) <= Constants.DOCK_RADIUS + p.getRadius() +50 && ship.getOwner() != this.getOwner()) {
            //         enemyShips.add(ship);
            //     }
            // }

            // for (int i = 0; i < enemyShips.size(); i++) {
            //     Ship shipTo = enemyShips.get(i);
            //     if (this.getDistanceTo(shipTo) > 5 /*&& shipTo.getDockingStatus() != Ship.DockingStatus.Undocked*/) {
            //         final ThrustMove myAttackMove;
            //         myAttackMove = new Navigation(this, shipTo).navigateToAttack(gameMap);
            //         if (myAttackMove != null) {
            //             return (myAttackMove);
            //         }
            //     }
            // }
            Map<Double, Ship> ships = gameMap.nearbyShipsByDistance(this);
                Object[] shipKeys = (ships.keySet().toArray());
                for (int i = 0; i < shipKeys.length; i++) {
                    Ship shipTo = ships.get(shipKeys[i]);
                    if (shipTo.getOwner() != this.getOwner() && this.getDistanceTo(shipTo) > 5 && shipTo.getDockingStatus() != Ship.DockingStatus.Undocked) {
                        final ThrustMove myAttackMove;

                            myAttackMove = new Navigation(this, shipTo).navigateToAttack(gameMap, stuff);

                        if (myAttackMove != null) {
                            return (myAttackMove);
                        }
                    }
                }

        } else {
            if (this.canDock(p)) {
                return (new DockMove(this, p));
            }

            final ThrustMove newThrustMove = new Navigation(this, p).navigateToDock(gameMap, Constants.MAX_SPEED, stuff);
            if (newThrustMove != null) {
                return (newThrustMove);
            }
        }
        return new Move(Move.MoveType.Noop, this);

    }
    public Move takeOver(Planet p, GameMap gameMap) {
        if (p.getOwner() != this.getOwner() && p.isOwned()) {
            // ArrayList<Ship> enemyShips = new ArrayList<>();
            // for (final Ship ship : gameMap.getAllShips()) {
            //     if (this.getDistanceTo(p) <= Constants.DOCK_RADIUS + p.getRadius() +50 && ship.getOwner() != this.getOwner()) {
            //         enemyShips.add(ship);
            //     }
            // }

            // for (int i = 0; i < enemyShips.size(); i++) {
            //     Ship shipTo = enemyShips.get(i);
            //     if (this.getDistanceTo(shipTo) > 5 /*&& shipTo.getDockingStatus() != Ship.DockingStatus.Undocked*/) {
            //         final ThrustMove myAttackMove;
            //         myAttackMove = new Navigation(this, shipTo).navigateToAttack(gameMap);
            //         if (myAttackMove != null) {
            //             return (myAttackMove);
            //         }
            //     }
            // }
            Map<Double, Ship> ships = gameMap.nearbyShipsByDistance(this);
                Object[] shipKeys = (ships.keySet().toArray());
                for (int i = 0; i < shipKeys.length; i++) {
                    Ship shipTo = ships.get(shipKeys[i]);
                    if (shipTo.getOwner() != this.getOwner() && this.getDistanceTo(shipTo) > 5 && shipTo.getDockingStatus() != Ship.DockingStatus.Undocked) {
                        final ThrustMove myAttackMove;

                            myAttackMove = new Navigation(this, shipTo).navigateToAttack(gameMap);

                        if (myAttackMove != null) {
                            return (myAttackMove);
                        }
                    }
                }

        } else {
            if (this.canDock(p)) {
                return (new DockMove(this, p));
            }

            final ThrustMove newThrustMove = new Navigation(this, p).navigateToDock(gameMap, Constants.MAX_SPEED);
            if (newThrustMove != null) {
                return (newThrustMove);
            }
        }
        return new Move(Move.MoveType.Noop, this);

    }
}
