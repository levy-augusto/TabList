package hu.montlikadani.tablist.utils.scheduler;

import org.bukkit.plugin.Plugin;

public final class FoliaScheduler implements TLScheduler {

    private final Plugin plugin;
    private Object scheduledTask;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public TLScheduler submitAsync(Runnable task, long initialDelay, long period) {
        try {
            Object asyncScheduler = plugin.getServer().getClass().getMethod("getAsyncScheduler").invoke(plugin.getServer());
            Class<?> timeUnitClass = Class.forName("java.util.concurrent.TimeUnit");
            Object milliseconds = java.lang.Enum.valueOf((Class<? extends Enum>) timeUnitClass.asSubclass(Enum.class), "MILLISECONDS");

            scheduledTask = asyncScheduler.getClass()
                    .getMethod("runAtFixedRate", Plugin.class, java.util.function.Consumer.class, long.class, long.class, timeUnitClass)
                    .invoke(asyncScheduler, plugin, (java.util.function.Consumer<Object>) consumer -> task.run(), initialDelay, period * 20L,
                            milliseconds);
        } catch (ReflectiveOperationException ignored) {
        }

        return this;
    }

    @Override
    public void runDelayed(Runnable task, org.bukkit.Location location, long delay) {
        try {
            Object regionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler").invoke(plugin.getServer());
            regionScheduler.getClass().getMethod("runDelayed", Plugin.class, org.bukkit.Location.class, java.util.function.Consumer.class, long.class)
                    .invoke(regionScheduler, plugin, location, (java.util.function.Consumer<Object>) consumer -> task.run(), delay);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public void submitSync(Runnable runnable) {
        try {
            Object globalRegionScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler").invoke(plugin.getServer());
            globalRegionScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class).invoke(globalRegionScheduler, plugin, runnable);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public void runTaskAsynchronously(Runnable task) {
        try {
            Object asyncScheduler = plugin.getServer().getClass().getMethod("getAsyncScheduler").invoke(plugin.getServer());
            asyncScheduler.getClass().getMethod("runNow", Plugin.class, java.util.function.Consumer.class).invoke(asyncScheduler, plugin,
                    (java.util.function.Consumer<Object>) schedule -> task.run());
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public void cancelTask() {
        if (scheduledTask != null) {
            try {
                scheduledTask.getClass().getMethod("cancel").invoke(scheduledTask);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }
}
