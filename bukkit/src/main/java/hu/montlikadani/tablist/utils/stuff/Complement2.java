package hu.montlikadani.tablist.utils.stuff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Complement2 implements Complement {

	private boolean isCriteriaExists;

	public Complement2() {
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
		try {
			Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
			Object component = deserializeComponent(text);
			if (component != null) {
				player.getClass().getMethod("playerListName", componentClass).invoke(player, component);
				return;
			}
		} catch (ReflectiveOperationException ignored) {
		}

		player.setPlayerListName(text);
	}

	@Override
	public String displayName(Player player) {
		try {
			Object component = player.getClass().getMethod("displayName").invoke(player);
			String serialized = serializeComponent(component);
			if (serialized != null) {
				return serialized;
			}
		} catch (ReflectiveOperationException ignored) {
		}

		return player.getDisplayName();
	}

	@Override
	public String motd() {
		try {
			Object component = Bukkit.getServer().getClass().getMethod("motd").invoke(Bukkit.getServer());
			String serialized = serializeComponent(component);
			if (serialized != null) {
				return serialized;
			}
		} catch (ReflectiveOperationException ignored) {
		}

		return Bukkit.getServer().getMotd();
	}

	@Override
	public void displayName(Objective objective, String dName) {
		try {
			Object component = deserializeComponent(dName);
			if (component != null) {
				objective.getClass().getMethod("displayName", component.getClass()).invoke(objective, component);
				return;
			}
		} catch (ReflectiveOperationException ignored) {
		}

		objective.setDisplayName(dName);
	}

	@Override
	public Objective registerNewObjective(Scoreboard board, String name, String criteriaName, String displayName, Object renderType) {
		if (isCriteriaExists) {
			try {
				Class<?> criteriaClass = Class.forName("org.bukkit.scoreboard.Criteria");
				Object criteria;

				switch (criteriaName) {
				case "health":
					criteria = criteriaClass.getField("HEALTH").get(null);
					break;
				case "dummy":
					criteria = criteriaClass.getField("DUMMY").get(null);
					break;
				default:
					return null;
				}

				Object displayComponent = deserializeComponent(displayName);
				if (displayComponent != null) {
					if (renderType != null) {
						try {
							return (Objective) board.getClass()
									.getMethod("registerNewObjective", String.class, criteriaClass, displayComponent.getClass(),
											renderType.getClass())
									.invoke(board, name, criteria, displayComponent, renderType);
						} catch (NoSuchMethodException ignored) {
						}
					}

					try {
						return (Objective) board.getClass()
								.getMethod("registerNewObjective", String.class, criteriaClass, displayComponent.getClass())
								.invoke(board, name, criteria, displayComponent);
					} catch (NoSuchMethodException ignored) {
					}
				}

				return (Objective) board.getClass().getMethod("registerNewObjective", String.class, criteriaClass, String.class)
						.invoke(board, name, criteria, displayName);
			} catch (ReflectiveOperationException ignored) {
			}
		}

		if (renderType != null) {
			try {
				return (Objective) board.getClass()
						.getMethod("registerNewObjective", String.class, String.class, String.class, renderType.getClass())
						.invoke(board, name, criteriaName, displayName, renderType);
			} catch (ReflectiveOperationException ignored) {
			}
		}

		return board.registerNewObjective(name, criteriaName);
	}

	private Object deserializeComponent(String text) {
		try {
			Class<?> serializerClass = Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
			Object serializer = serializerClass.getMethod("legacySection").invoke(null);
			return serializerClass.getMethod("deserialize", String.class).invoke(serializer, text);
		} catch (ReflectiveOperationException ignored) {
			return null;
		}
	}

	private String serializeComponent(Object component) {
		if (component == null) {
			return "";
		}

		try {
			Class<?> serializerClass = Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
			Object serializer = serializerClass.getMethod("legacySection").invoke(null);
			return (String) serializerClass.getMethod("serialize", component.getClass()).invoke(serializer, component);
		} catch (ReflectiveOperationException ignored) {
			return null;
		}
	}
}
