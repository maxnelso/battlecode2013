package fibbybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
  public static void run(RobotController rc) {
    while (true) {
      try {
        if (rc.getType() == RobotType.HQ) {
          if (rc.isActive()) {
            // Spawn a soldier
            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            if (rc.canMove(dir))
              rc.spawn(dir);
          }
        } else if (rc.getType() == RobotType.SOLDIER) {
          doIt(rc);
        }

        // End turn
        rc.yield();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void doIt(RobotController rc) throws GameActionException {
    if (rc.isActive()) {
      // Choose a random direction, and move that way if possible
      MapLocation[] encampmentSquares = rc.senseAllEncampmentSquares();
      for (int i = 0; i < encampmentSquares.length; i++) {
        if (rc.getLocation().distanceSquaredTo(encampmentSquares[i]) == 0) {
          rc.captureEncampment(RobotType.GENERATOR);
          return;
        }
      }

      boolean found = false;
      System.out.println(encampmentSquares.length);
      for (int i = 0; i < encampmentSquares.length; i++) {
        if (rc.canSenseSquare(encampmentSquares[i]) &&
            rc.senseObjectAtLocation(encampmentSquares[i]) == null) {
          Direction d = rc.getLocation().directionTo(encampmentSquares[i]);
          if (tryStuffs(rc, d)) {
            found = true;
            break;
          }
        }
      }

      if (!found) {
        for (int i = 0; i < encampmentSquares.length; i++) {
          Direction d = rc.getLocation().directionTo(encampmentSquares[i]);
          if (tryStuffs(rc, d)) {
            break;
          }
        }
      }
    }
  }

  private static boolean tryStuffs(RobotController rc, Direction d) throws GameActionException {
    for (int i = 0; i < 8; i++) {
      if (rc.senseMine(rc.getLocation().add(d)) != null) {
        rc.defuseMine(rc.getLocation().add(d));
        return true;
      }
      if (rc.canMove(d)) {
        rc.move(d);
        return true;
      }
    }
    return false;
  }
}