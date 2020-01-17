import hlt.*;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeMap;
import java.lang.Math;

public class MyBot {

    final static double dockingValue = 1.0;
    final static double distanceValue = 1.0;
    final static double emptyValue = 1.3;
    final static double fullValue = 0.0;
    final static double enemyValue = 1.5;
    final static double centerValue2 = 1.0;
    final static double centerValue4 = 0.66;

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("VAB10266");
        final ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Entity> posList = new ArrayList<>();
        ArrayList<Ship> shipList = new ArrayList<>();



        while (true) {
            moveList.clear();
            posList.clear();
            shipList.clear();
            for (int i = 0; i < gameMap.getAllPlanets().values().size(); i++) {
                Planet planet = (Planet) gameMap.getAllPlanets().values().toArray()[i];
                planet.plannedShips = 0;
            }
            gameMap.updateMap(Networking.readLineIntoMetadata());

            Collection<Planet> planets = gameMap.getAllPlanets().values();

            for (Ship s : gameMap.getMyPlayer().getShips().values()) {
                if (s.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    Position newPos = new Position(s.getXPos(), s.getYPos());
                    posList.add(new PhantomShip(newPos));
                    continue;
                }
                TreeMap<Double, Planet> planetsByValue = new TreeMap<>();

                for (Planet p : planets) {

                    //DebugLog.addLog(p.getYPos() +"");

                    int maxShips = p.getDockingSpots();
                    int currentShips = p.getDockedShips().size();
                    double centerValue = (gameMap.getAllPlayers().size() == 2)? 1.0 : (p.getDistanceTo(new Position(gameMap.getWidth() / 2, gameMap.getHeight() / 2)));
                    //centerValue = (p.getDistanceTo(new Position(gameMap.getWidth() / 2, gameMap.getHeight() / 2))) * centerValue;
                    double value = p.getDockingSpots() * centerValue  * enemyValue * dockingValue / (s.getDistanceTo(p) * distanceValue);
                    //if (p.plannedShips != 0) value = value/p.plannedShips;
                    if (!p.isOwned()) value *= emptyValue;
                    if (p.getOwner() == s.getOwner() && currentShips == maxShips) value *= fullValue;

                    planetsByValue.put(value, p);
                }

                Object[] planetKeys = (planetsByValue.keySet().toArray());
                Planet p = planetsByValue.get(planetKeys[planetKeys.length-1]);
                Move m = s.takeOver(p, gameMap, posList);

                if(m != null) {
                    p.plannedShips ++;
                    moveList.add(m);
                    if (m instanceof ThrustMove) {
                        double newXPos = (s.getXPos() + (((ThrustMove)m).getThrust() * Math.cos(((ThrustMove)m).getAngle())));
                        double newYPos = (s.getYPos() + (((ThrustMove)m).getThrust() * Math.sin(((ThrustMove)m).getAngle())));
                        Position newPos = new Position(newXPos, newYPos);
                        posList.add(new PhantomShip(newPos));
                    } else {
                        Position newPos = new Position(s.getXPos(), s.getYPos());
                        posList.add(new PhantomShip(newPos));
                    }
                }
            }
            Networking.sendMoves(moveList);

        }
    }
}
