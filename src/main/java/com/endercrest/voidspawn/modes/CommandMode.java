package com.endercrest.voidspawn.modes;

import com.endercrest.voidspawn.ConfigManager;
import com.endercrest.voidspawn.TeleportManager;
import com.endercrest.voidspawn.TeleportResult;
import com.endercrest.voidspawn.VoidSpawn;
import com.endercrest.voidspawn.options.Option;
import com.endercrest.voidspawn.modes.status.Status;
import com.endercrest.voidspawn.utils.CommandPlaceholderUtil;
import com.endercrest.voidspawn.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandMode extends BaseMode {
    private final VoidSpawn plugin;

    public CommandMode(VoidSpawn plugin) {
        detachOption(BaseMode.OPTION_HYBRID); // Command mode can't be hybrid.
        this.plugin = plugin;
    }

    @Override
    public TeleportResult onActivate(Player player, String worldName) {
        Location touch = TeleportManager.getInstance().getPlayerLocation(player);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return TeleportResult.INVALID_WORLD;
        }

        // If the player hasn't touch the ground, fallback to the spawn.
        if (touch == null) {
            touch = ConfigManager.getInstance().getSpawn(worldName);
        }
        // If fallback spawn not set, fall back to the spawn point.
        if (touch == null) {
            touch = world.getSpawnLocation();
        }

        Option<String> commandOption = getOption(BaseMode.OPTION_COMMAND);

        String commandString = CommandPlaceholderUtil.apply(
                commandOption.getValue(world).orElse(""),
                getPlaceholders(player, touch)
        );

        String[] commands = commandString.split(";");
        TeleportResult result = TeleportResult.SUCCESS;
        for (String command: commands) {
            boolean status;
            String[] perms = command.split(":", 2);
            //Check if cmd needs to be ran as OP/Console
            if (perms.length > 1 && perms[0].trim().equalsIgnoreCase("op")) {
                String cmd = perms[1].trim();
                status = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } else {
                status = dispatchCommand(player, command.trim());
            }

            if (!status) {
                plugin.log(String.format("&cCommand Failed for %s! (%s)", worldName, command));
                result = TeleportResult.FAILED_COMMAND;
            }
        }
        if (result != TeleportResult.SUCCESS) {
            player.sendMessage(MessageUtil.colorize(VoidSpawn.prefix + "&cContact Admin. One of the commands failed."));
        }
        return result;
    }

    protected boolean dispatchCommand(Player player, String command) {
        return player.performCommand(command);
    }

    private Map<String, String> getPlaceholders(Player player, Location touch) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player.name", player.getName());
        placeholders.put("player.uuid", player.getUniqueId().toString());
        placeholders.put("player.coord.x", player.getLocation().getBlockX() + "");
        placeholders.put("player.coord.y", player.getLocation().getBlockY() + "");
        placeholders.put("player.coord.z", player.getLocation().getBlockZ() + "");
        placeholders.put("player.coord.world", player.getLocation().getWorld().getName());
        placeholders.put("player.touch.x", touch.getBlockX() + "");
        placeholders.put("player.touch.y", touch.getBlockY() + "");
        placeholders.put("player.touch.z", touch.getBlockZ() + "");
        placeholders.put("player.touch.world", touch.getWorld().getName());
        return placeholders;
    }

    @Override
    public boolean onSet(String[] args, String worldName, Player p) {
        ConfigManager.getInstance().setMode(worldName, args[1]);
        return true;
    }

    @Override
    public Status[] getStatus(String worldName) {
        World world = Bukkit.getWorld(worldName);
        Option<String> commandOption = getOption(BaseMode.OPTION_COMMAND);

        Optional<String> command = commandOption.getValue(world);
        return new Status[]{
                new Status(!command.isPresent() ? Status.Type.INCOMPLETE : Status.Type.COMPLETE,
                        String.format("Command Set %s", !command.isPresent() ? "" : String.format("(%s)", command.get())))
        };
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Uses configurable command(s) to send player to spawn";
    }

    @Override
    public String getName() {
        return "Command";
    }
}
