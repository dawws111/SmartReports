package com.universal.reports;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SmartReports extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getCommand("report") != null) getCommand("report").setExecutor(this);
        if (getCommand("smartreports") != null) getCommand("smartreports").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("smartreports")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("smartreports.admin")) return true;
                reloadConfig();
                return true;
            }
            return true;
        }

        if (!(sender instanceof Player)) return true;
        Player reporter = (Player) sender;

        if (args.length < 2) return true;

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) return true;

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) reasonBuilder.append(args[i]).append(" ");
        String reason = reasonBuilder.toString().trim();

        sendStaffAlert(reporter.getName(), target.getName(), reason);
        return true;
    }

    private void sendStaffAlert(String reporterName, String targetName, String reason) {
        TextComponent alert = new TextComponent(color("&cREPORT &f" + reporterName + " &7reported &e" + targetName + "\n&7Reason &f" + reason + "\n"));

        TextComponent tpBtn = new TextComponent(color("&a[TELEPORT] "));
        tpBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + targetName));
        tpBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&aTeleport to " + targetName))));

        TextComponent freezeBtn = new TextComponent(color("&b[FREEZE]"));
        freezeBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/freeze " + targetName));
        freezeBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&bFreeze " + targetName))));

        alert.addExtra(tpBtn);
        alert.addExtra(freezeBtn);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("smartreports.staff")) online.spigot().sendMessage(alert);
        }
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
