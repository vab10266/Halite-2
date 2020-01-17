import hlt.*;
import java.util.Map;
import java.util.ArrayList;

public class V2 {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("V2");
        final ArrayList<Move> moveList = new ArrayList<>();

        while (true) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                Map<Double, Entity> planets = gameMap.nearbyEntitiesByDistance(ship);
                for (final Planet planet : gameMap.getAllPlanets().values()) {
                    if (planet.isOwned()) {
                        continue;
                    }

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    final ThrustMove newThrustMove = new Navigation(ship, planet).navigateToDock(gameMap, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }

                    break;
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}
