package hu.montlikadani.tablist.utils.stuff;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("deprecation")
public final class Complement1 implements Complement {

	private boolean isCriteriaExists;

	public Complement1() {
		try {
			Class.forName("org.bukkit.scoreboard.Criteria");
			isCriteriaExists = true;
		} catch (ClassNotFoundException e) {
			isCriteriaExists = false;
		}
	}

	@Override
	public void sendMessage(org.bukkit.command.CommandSender sender, String text) {
		sender.sendMessage(text);
	}

	@Override
	public void playerListName(Player player, String text) {
		player.setPlayerListName(text);
	}

	@Override
	public String displayName(Player player) {
		return player.getDisplayName();
	}

	@Override
	public String motd() {
		return org.bukkit.Bukkit.getServer().getMotd();
	}

	@Override
	public void displayName(Objective objective, String dName) {
		objective.setDisplayName(dName);
	}

	@Override
	public Objective registerNewObjective(Scoreboard board, String name, String criteria /* Switch to enum? */, String displayName,
			Object renderType) {
		if (isCriteriaExists) {
			try {
				Class<?> criteriaClass = Class.forName("org.bukkit.scoreboard.Criteria");
				Object crit;

				switch (criteria) {
				case "health":
					crit = criteriaClass.getField("HEALTH").get(null);
					break;
				case "dummy": // not used
					crit = criteriaClass.getField("DUMMY").get(null);
					break;
				default:
					return null; // prob not
				}

				if (renderType != null) {
					try {
						return (Objective) board.getClass()
								.getMethod("registerNewObjective", String.class, criteriaClass, String.class, renderType.getClass())
								.invoke(board, name, crit, displayName, renderType);
					} catch (NoSuchMethodException ignored) {
					}
				}

				return (Objective) board.getClass().getMethod("registerNewObjective", String.class, criteriaClass, String.class)
						.invoke(board, name, crit, displayName);
			} catch (ReflectiveOperationException ignored) {
			}
		}

		if (renderType != null) {
			try {
				return (Objective) board.getClass()
						.getMethod("registerNewObjective", String.class, String.class, String.class, renderType.getClass())
						.invoke(board, name, criteria, displayName, renderType);
			} catch (ReflectiveOperationException ignored) {
			}
		}

		return board.registerNewObjective(name, criteria);
	}
}
