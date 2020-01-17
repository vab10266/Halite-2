import hlt.*;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

public class V3 {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("V3");
        final ArrayList<Move> moveList = new ArrayList<>();
        boolean moved;

        while (true) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                moved = false;
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                Map<Double, Planet> planets = gameMap.nearbyPlanetsByDistance(ship);
                Object[] planetKeys = (planets.keySet().toArray());
                for (int i = 0; i < planetKeys.length; i++) {
                    Planet planet = planets.get(planetKeys[i]);
                    if (!planet.isOwned()) {

                        if (ship.canDock(planet) && !moved) {
                            moveList.add(new DockMove(ship, planet));
                            moved = true; break;
                        }

                        final ThrustMove newThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
                        if (newThrustMove != null && !moved) {
                            moveList.add(newThrustMove);
                            moved = true; break;
                        }
                    }
                }
                for (int i = 0; i < planetKeys.length; i++) {
                    Planet planet = planets.get(planetKeys[i]);
                    if (!planet.isFull() && planet.getOwner() == ship.getOwner()) {

                        if (ship.canDock(planet) && !moved) {
                            moveList.add(new DockMove(ship, planet));
                            moved = true; break;
                        }

                        final ThrustMove secondThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
                        if (secondThrustMove != null && !moved) {
                            moveList.add(secondThrustMove);
                            moved = true; break;
                        }

                    }



                }
                Map<Double, Ship> ships = gameMap.nearbyShipsByDistance(ship);
                Object[] shipKeys = (ships.keySet().toArray());
                for (int i = 0; i < shipKeys.length; i++) {
                    if (ships.get(shipKeys[i]).getOwner() != ship.getOwner()) {
                        Ship shipTo = ships.get(shipKeys[i]);
                        Position targetPos = new Position(shipTo.getXPos(), shipTo.getYPos());

                        final ThrustMove myAttackMove = new Navigation(ship, shipTo).navigateTowards(gameMap, targetPos, Constants.MAX_SPEED, true, Constants.MAX_CORRECTIONS, Math.PI/180);

                        if (myAttackMove != null && !moved) {
                            moveList.add(myAttackMove);
                            moved = true; break;
                        }
                    }
                }
            if (!moved) {
                moveList.add(new Move(Move.MoveType.Noop, ship));
                moved = true;
            }
            }
            Networking.sendMoves(moveList);
        }
    }
}
