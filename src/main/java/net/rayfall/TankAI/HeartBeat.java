package net.rayfall.TankAI;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.rayfall.TankAI.Enums.MovementDirection;
import net.rayfall.TankAI.Enums.OutgoingControls;
import net.rayfall.TankAI.Enums.RotationDirection;
import net.rayfall.TankAI.Enums.TerrainType;

public class HeartBeat {

	private Match match;
	private ArrayList<Player> players;
	private ArrayList<Tank> friendlyTanks;
	private ArrayList<Tank> enemyTanks;
	private Map map;
	private GameState gameState;

	public HeartBeat() {

	}

	public void launchGameHeartBeat() {
		JSONObject wait = waitForGameStart();
		if (wait != null) {
			match = new Match(wait);
		} else {
			match = new Match();
		}
		for (int game = match.getGameNumber(); game <= match.getGameCount(); game = match.getGameNumber()) {
			// Loads base values for the game.
			initGame();
			// Locks thread and does game logic until game is done.
			singleGameHeartBeat();
			// Check if match is over just in case
			JSONObject testState = getGameState();
			if (getCurrentGameState(testState).equals("MatchEnd")) {
				// Match is over lets break out!
				break;
			}
			JSONObject wait2 = waitForGameStart();
			if (wait2 != null) {
				match = new Match(wait2);
			}
		}

	}

	public void initGame() {
		// wait for game state
		while (true) {
			JSONObject testState = getGameState();
			if (getCurrentGameState(testState).equals("GAMESTATE")) {
				gameState = new GameState(testState);
				break;
			}
		}
		// Build everything from this game state
		players = buildPlayers(gameState);
		friendlyTanks = new ArrayList<Tank>();
		enemyTanks = new ArrayList<Tank>();
		for (Player player : players) {
			if (player.isFriendly()) {
				friendlyTanks.addAll(buildTanksFromPlayer(player));
			} else {
				enemyTanks.addAll(buildTanksFromPlayer(player));
			}
		}
		map = new Map(gameState.map);
	}

	public JSONObject waitForGameStart() {
		int c = 0;
		while (true) {
			JSONObject gameState = Client.comm.getJSONGameState();
			String state = null;
			try {
				state = gameState.getString("comm_type");
			} catch (JSONException e) {
				System.err.println("[Wait For Game Start] JSON error" + e);
			}
			System.err.println(state);
			if (state.equals("GAME_START")) {
				return gameState;
			}
			System.err.println("[Wait For Game Start] GAME_START not sent!");
			if (c > 3) {
				return null;
			}
			c++;
		}
	}

	public JSONObject getGameState() {
		return Client.comm.getJSONGameState();

	}

	public String getCurrentGameState(JSONObject gameState) {
		String state = null;
		try {
			state = gameState.getString("comm_type");
		} catch (JSONException e) {
			System.err.println("[Checking Game State] JSON Error");
		}
		return state;

	}

	public ArrayList<Player> buildPlayers(GameState gameState) {
		JSONArray rawPlayers = gameState.getPlayersRaw();
		ArrayList<Player> hold = new ArrayList<Player>();
		for (int i = 0; i < rawPlayers.length(); i++) {
			try {
				hold.add(new Player(rawPlayers.getJSONObject(i)));
			} catch (JSONException e) {
				System.err.println("[Player] JSON Error");
			}
		}
		return hold;
	}

	public ArrayList<Tank> buildTanksFromPlayer(Player player) {
		ArrayList<Tank> tanks = new ArrayList<Tank>();
		JSONArray tankList = player.getTankArray();
		for (int i = 0; i < tankList.length(); i++) {
			try {
				tanks.add(new Tank(tankList.getJSONObject(i)));
			} catch (JSONException e) {
				System.err.println("[Tank List] JSON Error");
			}
		}
		return tanks;
	}

	public void singleGameHeartBeat() {
		while (true) {
			HashMap<String, Position> previous = new HashMap<String, Position>();
			for (int i = 0; i < friendlyTanks.size(); i++) {
				if (friendlyTanks.get(i).getPosition().getLastDirection() == null) {
					friendlyTanks.get(i).getPosition().setLastDirection(MovementDirection.FWD);
				}
			}
			for (int i = 0; i < friendlyTanks.size(); i++) {
				System.out.println("Tank number: " + i);
				if (!friendlyTanks.get(i).isAlive()) {
					continue;
				}
				for (int j = 0; j < friendlyTanks.size(); j++) {
					for (int k = 0; k < friendlyTanks.get(j).getProjectiles().size(); k++) {
						double distance = distanceToProjectile(
								friendlyTanks.get(i).getPosition().getXPosition().doubleValue(),
								friendlyTanks.get(i).getPosition().getYPosition().doubleValue(),
								friendlyTanks.get(j).getProjectiles().get(k).getPosition().getXPosition(),
								friendlyTanks.get(j).getProjectiles().get(k).getPosition().getYPosition(),
								friendlyTanks.get(j).getProjectiles().get(k).getRange(),
								friendlyTanks.get(j).getProjectiles().get(k).getDirection());
						if (distance < 20) {
							double newDirection = Math.PI / 2
									+ friendlyTanks.get(j).getProjectiles().get(k).getDirection().doubleValue();
							if (newDirection > 2 * Math.PI) {
								newDirection -= 2 * Math.PI;
							}
							double currentDirection = friendlyTanks.get(i).getTankRotation().doubleValue();
							double Rot = newDirection - currentDirection;
							if (Rot < 0) {
								Rot += 2 * Math.PI;
							}

							if (Rot < Math.PI / 2 && Rot >= 0) {
								friendlyTanks.get(i).rotateTank(RotationDirection.CCW, Rot);
								friendlyTanks.get(i).moveTank(MovementDirection.FWD, 30);
							} else if (Rot >= Math.PI / 2 && Rot < Math.PI) {
								Rot = Math.PI - Rot;
								friendlyTanks.get(i).rotateTank(RotationDirection.CW, Rot);
								friendlyTanks.get(i).moveTank(MovementDirection.REV, 30);
							} else if (Rot >= Math.PI && Rot < Math.PI * 3 / 2) {
								Rot -= Math.PI;
								friendlyTanks.get(i).rotateTank(RotationDirection.CCW, Math.abs(Rot));
								friendlyTanks.get(i).moveTank(MovementDirection.REV, 30);
							} else if (Rot > Math.PI * 3 / 2 && Rot < 2 * Math.PI) {
								Rot = 2 * Math.PI - Rot;
								friendlyTanks.get(i).rotateTank(RotationDirection.CW, Math.abs(Rot));
								friendlyTanks.get(i).moveTank(MovementDirection.FWD, 30);
							}
						}
					}
				}
				for (int j = 0; j < enemyTanks.size(); j++) {
					for (int k = 0; k < enemyTanks.get(j).getProjectiles().size(); k++) {
						double distance = distanceToProjectile(
								friendlyTanks.get(i).getPosition().getXPosition().doubleValue(),
								friendlyTanks.get(i).getPosition().getYPosition().doubleValue(),
								enemyTanks.get(j).getProjectiles().get(k).getPosition().getXPosition(),
								enemyTanks.get(j).getProjectiles().get(k).getPosition().getYPosition(),
								enemyTanks.get(j).getProjectiles().get(k).getRange(),
								enemyTanks.get(j).getProjectiles().get(k).getDirection());
						if (distance < 20) {
							double newDirection = Math.PI / 2
									+ enemyTanks.get(j).getProjectiles().get(k).getDirection().doubleValue();
							if (newDirection > 2 * Math.PI) {
								newDirection -= 2 * Math.PI;
							}
							double currentDirection = friendlyTanks.get(i).getTankRotation().doubleValue();
							double Rot = newDirection - currentDirection;
							if (Rot < 0) {
								Rot += 2 * Math.PI;
							}
							if (Rot < Math.PI / 2 && Rot >= 0) {
								friendlyTanks.get(i).rotateTank(RotationDirection.CCW, Rot);
								friendlyTanks.get(i).moveTank(MovementDirection.FWD, 30);
							} else if (Rot >= Math.PI / 2 && Rot < Math.PI) {
								Rot = Math.PI - Rot;
								friendlyTanks.get(i).rotateTank(RotationDirection.CW, Rot);
								friendlyTanks.get(i).moveTank(MovementDirection.REV, 30);
							} else if (Rot >= Math.PI && Rot < Math.PI * 3 / 2) {
								Rot -= Math.PI;
								friendlyTanks.get(i).rotateTank(RotationDirection.CCW, Math.abs(Rot));
								friendlyTanks.get(i).moveTank(MovementDirection.REV, 30);
							} else if (Rot > Math.PI * 3 / 2 && Rot < 2 * Math.PI) {
								Rot = 2 * Math.PI - Rot;
								friendlyTanks.get(i).rotateTank(RotationDirection.CW, Math.abs(Rot));
								friendlyTanks.get(i).moveTank(MovementDirection.FWD, 30);
							}
						}
					}
				}

				// find the nearest enemy tank
				double dist = 150;
				double shortdist = 150;
				int index = 0;
				for (int j = 0; j < enemyTanks.size(); j++) {
					dist = distanceToTank(friendlyTanks.get(i).getPosition().getXPosition().doubleValue(),
							friendlyTanks.get(i).getPosition().getYPosition().doubleValue(),
							enemyTanks.get(j).getPosition().getXPosition().doubleValue(),
							enemyTanks.get(j).getPosition().getYPosition().doubleValue());
					if ((dist < shortdist) && isLineOfSiteClear(friendlyTanks.get(i), enemyTanks.get(j))) {
						index = j;
						shortdist = dist;
					}
				}

				double xDist = enemyTanks.get(index).getPosition().getXPosition().doubleValue()
						- friendlyTanks.get(i).getPosition().getXPosition().doubleValue();
				double yDist = enemyTanks.get(index).getPosition().getYPosition().doubleValue()
						- friendlyTanks.get(i).getPosition().getYPosition().doubleValue();

				double newDirection = Math.acos(xDist / Math.hypot(xDist, yDist));
				if (yDist < 0) {
					newDirection = 2 * Math.PI - newDirection;
				}

				double TRotation = newDirection - friendlyTanks.get(i).getTurretRotation().doubleValue();
				if (TRotation > 0) {
					friendlyTanks.get(i).rotateTurret(RotationDirection.CCW, TRotation);
				} else if (TRotation < 0) {
					friendlyTanks.get(i).rotateTurret(RotationDirection.CW, -TRotation);
				}

				if (/*TRotation < 0.5 && TRotation > -0.5 &&*/ shortdist < 125
						&& !isFriendlyInTheWay(friendlyTanks.get(i), enemyTanks.get(index))) {
					friendlyTanks.get(i).fire();
				}
				/*
				for (int j = 0; j < map.getTerrainList().size(); j++) {
					dist = distanceToTerrain(friendlyTanks.get(i).getPosition().getXPosition().doubleValue(),
							friendlyTanks.get(i).getPosition().getYPosition().doubleValue(),
							map.getTerrainList().get(j).getRegion().getCornerX().doubleValue(),
							map.getTerrainList().get(j).getRegion().getCornerY().doubleValue(),
							map.getTerrainList().get(j).getRegion().getSizeX().doubleValue(),
							map.getTerrainList().get(j).getRegion().getSizeY().doubleValue());

					if (dist < 30) {
						friendlyTanks.get(i).rotateTank(RotationDirection.CCW, 1 - dist / 30);
						friendlyTanks.get(i).moveTank(friendlyTanks.get(i).getPosition().getLastDirection(), 5);
					}
				}
				if (friendlyTanks.get(i).getPosition().getXPosition().doubleValue() < 30) {
					friendlyTanks.get(i).rotateTank(RotationDirection.CCW,
							1 - friendlyTanks.get(i).getPosition().getXPosition().doubleValue() / 30);
					friendlyTanks.get(i).moveTank(friendlyTanks.get(i).getPosition().getLastDirection(), 5);
				} else if (map.getMapSizeX().doubleValue()
						- friendlyTanks.get(i).getPosition().getXPosition().doubleValue() < 30) {
					friendlyTanks.get(i).rotateTank(RotationDirection.CCW, 1 - (map.getMapSizeX().doubleValue()
							- friendlyTanks.get(i).getPosition().getXPosition().doubleValue()) / 30);
					friendlyTanks.get(i).moveTank(friendlyTanks.get(i).getPosition().getLastDirection(), 5);
				}
				if (map.getMapSizeY().doubleValue()
						- friendlyTanks.get(i).getPosition().getXPosition().doubleValue() < 30) {
					friendlyTanks.get(i).rotateTank(RotationDirection.CCW, 1 - (map.getMapSizeY().doubleValue()
							- friendlyTanks.get(i).getPosition().getYPosition().doubleValue()) / 30);
					friendlyTanks.get(i).moveTank(friendlyTanks.get(i).getPosition().getLastDirection(), 5);
				} else if (friendlyTanks.get(i).getPosition().getYPosition().doubleValue() < 30) {
					friendlyTanks.get(i).rotateTank(RotationDirection.CCW,
							1 - friendlyTanks.get(i).getPosition().getYPosition().doubleValue() / 30);
					friendlyTanks.get(i).moveTank(friendlyTanks.get(i).getPosition().getLastDirection(), 5);
				}
				previous.put(friendlyTanks.get(i).getTankID(), friendlyTanks.get(i).getPosition());
				if (previous.get(friendlyTanks.get(i).getTankID()) == friendlyTanks.get(i).getPosition()) {
					if (friendlyTanks.get(i).getPosition().getLastDirection() == MovementDirection.FWD) {
						friendlyTanks.get(i).getPosition().setLastDirection(MovementDirection.REV);
					} else if (friendlyTanks.get(i).getPosition().getLastDirection() == MovementDirection.REV) {
						friendlyTanks.get(i).getPosition().setLastDirection(MovementDirection.FWD);
					}
				}
				*/
			}

			// Check to see if game is over, if it is break.
			JSONObject testState = getGameState();
			if (getCurrentGameState(testState).equals("GAME_END")) {
				// Game is over lets break out!
				break;
			} else if (getCurrentGameState(testState).equals("GAMESTATE")) {
				gameState = new GameState(testState);
			} else {
				System.err.println("[singleGameHeartBeat] Unexspected Value: " + getCurrentGameState(testState));
			}
			// Refresh all Values
			players = buildPlayers(gameState);
			friendlyTanks = new ArrayList<Tank>();
			enemyTanks = new ArrayList<Tank>();
			for (Player player : players) {
				if (player.isFriendly()) {
					friendlyTanks.addAll(buildTanksFromPlayer(player));
				} else {
					enemyTanks.addAll(buildTanksFromPlayer(player));
				}
			}
			map = new Map(gameState.map);
		}
	}

	private double distanceToCenter(Number x, Number y, Number xc, Number yc) {
		return Math.sqrt((x.doubleValue() - xc.doubleValue()) * (x.doubleValue() - xc.doubleValue())
				+ (y.doubleValue() - yc.doubleValue()) * (y.doubleValue() - yc.doubleValue()));
	}

	private double distanceToTank(Number x, Number y, Number xt, Number yc) {
		double d = (x.doubleValue() - xt.doubleValue()) * (x.doubleValue() - xt.doubleValue())
				+ (y.doubleValue() - yc.doubleValue()) * (y.doubleValue() - yc.doubleValue());
		return Math.sqrt(d);
	}

	private double distanceToProjectile(Number x, Number y, Number xp, Number yp, Number r, Number dir) {
		Line2D projPath = new Line2D.Double(xp.doubleValue(),
				xp.doubleValue() + r.doubleValue() * Math.cos(dir.doubleValue()), yp.doubleValue(),
				yp.doubleValue() + r.doubleValue() * Math.sin(dir.doubleValue()));
		return projPath.ptSegDist(x.doubleValue(), y.doubleValue());
	}

	private double distanceToTerrain(Number x, Number y, Number xt, Number yt, Number w, Number h) {
		double xc = (xt.doubleValue() + w.doubleValue()) / 2;
		double yc = (yt.doubleValue() + h.doubleValue()) / 2;
		xc = Math.max(Math.abs(x.doubleValue() - xc) - w.doubleValue() / 2, 0);
		yc = Math.max(Math.abs(y.doubleValue() - yc) - h.doubleValue() / 2, 0);
		return xc * xc + yc * yc;
	}

	private boolean isLineOfSiteClear(Tank friendTank, Tank enemyTank) {
		Line2D lineOfTarget = new Line2D.Double(friendTank.getPosition().getXPosition().doubleValue(),
				friendTank.getPosition().getYPosition().doubleValue(),
				enemyTank.getPosition().getXPosition().doubleValue(),
				enemyTank.getPosition().getYPosition().doubleValue());
		ArrayList<Terrain> obs = map.getTerrainList();
		for (int t = 0; t < obs.size(); t++) {
			if (obs.get(t).getTerrainType() == TerrainType.SOLID) {
				Region reg = obs.get(t).getRegion();
				Rectangle2D nogo = new Rectangle2D.Double(reg.getCornerX().doubleValue(),
						reg.getCornerY().doubleValue(), reg.getSizeX().doubleValue(), reg.getSizeY().doubleValue());
				if (lineOfTarget.intersects(nogo)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isFriendlyInTheWay(Tank friendTank, Tank enemyTank) {
		Line2D lineOfTarget = new Line2D.Double(friendTank.getPosition().getXPosition().doubleValue(),
				friendTank.getPosition().getYPosition().doubleValue(),
				enemyTank.getPosition().getXPosition().doubleValue(),
				enemyTank.getPosition().getYPosition().doubleValue());
		for (int e = 0; e < friendlyTanks.size(); e++) {
			Tank test = friendlyTanks.get(e);
			if (!friendTank.getTankID().equals(test.getTankID())) {
				Rectangle2D noshoot = new Rectangle2D.Double(test.getPosition().getXPosition().doubleValue(),
						test.getPosition().getYPosition().doubleValue(), 1, 1);
				if (lineOfTarget.intersects(noshoot)) {
					System.out.println("Firing has been halted becasue a friend is in the way.");
					System.out.println("Subject tank: " + friendTank.getTankID() + " at: "
							+ friendTank.getPosition().getXPosition() + "," + friendTank.getPosition().getYPosition());
					System.out.println("Tank that caused this: " + test.getTankID() + " at: "
							+ test.getPosition().getXPosition() + "," + test.getPosition().getYPosition());
					return true;
				}
			}
		}
		return false;
	}
}
