package com.endercrest.voidspawn.modes;

import com.endercrest.voidspawn.VoidSpawn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleMode extends CommandMode {
    public ConsoleMode(VoidSpawn plugin) {
        super(plugin);
    }

    @Override
    protected boolean dispatchCommand(Player player, String command) {
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public String getDescription() {
        return "Uses configurable console command(s) to send player to spawn";
    }

    @Override
    public String getName() {
        return "Console";
    }
}
