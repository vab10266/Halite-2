import hlt.*;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

public class V4 {



    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("V4");
        final ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Position> posList = new ArrayList<>();
        boolean moved;
        boolean phase2 = false;

        while (true) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            Map<Integer, Planet> plans = gameMap.getAllPlanets();
            Object[] planKeys = (plans.keySet().toArray());
            boolean hasEmpty = true;
            for (int i = 0; i < planKeys.length; i++) {
                hasEmpty = hasEmpty && plans.get(planKeys[i]).isOwned();
                plans.get(planKeys[i]).plannedShips = 0;
            }
            phase2 = phase2 || !hasEmpty;
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
                        Move m = ship.takeOver(planet, gameMap);
                        if(m != null && !moved) {
                            moved = true;
                            moveList.add(m);
                        }
                        // Map<Double, Ship> ships = gameMap.nearbyShipsByDistance(planet);
                        // Object[] shipKeys = (ships.keySet().toArray());
                        // //if (planetKeys[i] == shipKeys[0]) {
                        //     int maxShips = (phase2)? 200:1;
                        //     int currentShips = planet.getDockedShips().size();
                        //     int plannedShips = currentShips;
                        //     if (ship.canDock(planet) && !moved && planet.plannedShips < maxShips) {
                        //         planet.plannedShips++;
                        //         moveList.add(new DockMove(ship, planet));
                        //         moved = true; break;
                        //     }

                        //     final ThrustMove newThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
                        //     if (newThrustMove != null && !moved && planet.plannedShips < maxShips) {
                        //         planet.plannedShips++;
                        //         moveList.add(newThrustMove);
                        //         moved = true; break;
                        //     }
                        // //}
                    }
                }
                for (int i = 0; i < planetKeys.length; i++) {
                    Planet planet = planets.get(planetKeys[i]);
                    if (!planet.isFull() && planet.getOwner() == ship.getOwner()) {
                        int maxShips = planet.getDockingSpots();
                        int currentShips = planet.getDockedShips().size();
                        int plannedShips = currentShips;
                        if (ship.canDock(planet) && !moved && planet.plannedShips < maxShips) {
                            planet.plannedShips++;
                            moveList.add(new DockMove(ship, planet));
                            moved = true; break;
                        }

                        final ThrustMove secondThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
                        if (secondThrustMove != null && !moved && planet.plannedShips < maxShips) {
                            planet.plannedShips++;
                            moveList.add(secondThrustMove);
                            moved = true; break;
                        }

                    }



                }
                Map<Double, Ship> ships = gameMap.nearbyShipsByDistance(ship);
                Object[] shipKeys = (ships.keySet().toArray());
                for (int i = 0; i < shipKeys.length; i++) {
                    Ship shipTo = ships.get(shipKeys[i]);
                    if (shipTo.getOwner() != ship.getOwner() && ship.getDistanceTo(shipTo) > 5 && shipTo.getDockingStatus() != Ship.DockingStatus.Undocked) {
                        final ThrustMove myAttackMove;

                            myAttackMove = new Navigation(ship, shipTo).navigateToAttack(gameMap);

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
