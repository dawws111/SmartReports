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
        if (getCommand("report") != null) {
            getCommand("report").setExecutor(this);
        }
        if (getCommand("smartreports") != null) {
            getCommand("smartreports").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /smartreports reload
        if (command.getName().equalsIgnoreCase("smartreports")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("smartreports.admin")) {
                    sender.sendMessage(color("&cYou do not have permission to run this command."));
                    return true;
                }
                reloadConfig();
                sender.sendMessage(color("&aSmartReports configuration reloaded successfully!"));
                return true;
            }
            sender.sendMessage(color("&eUsage: /smartreports reload"));
            return true;
        }

        // /report <player> <reason>
        if (command.getName().equalsIgnoreCase("report")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can submit reports.");
                return true;
            }

            Player reporter = (Player) sender;

            if (args.length < 2) {
                reporter.sendMessage(color("&cUsage: /report <player> <reason>"));
                return true;
            }

            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null || !target.isOnline()) {
                reporter.sendMessage(color("&cPlayer '" + targetName + "' is not online."));
                return true;
            }

            // build reason from remaining arguments
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = reasonBuilder.toString().trim();

            // send alert to staff
            sendStaffAlert(reporter.getName(), target.getName(), reason);
            reporter.sendMessage(color("&aYour report against " + target.getName() + " has been sent to online staff."));
            return true;
        }

        return false;
    }

    private void sendStaffAlert(String reporterName, String targetName, String reason) {
        TextComponent alert = new TextComponent(color("&c&l[REPORT] &f" + reporterName + " &7reported &e" + targetName + "\n&7Reason: &f" + reason + "\n"));

        // teleport Button
        TextComponent tpBtn = new TextComponent(color("&a&l[TELEPORT] "));
        tpBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + targetName));
        tpBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&aClick to teleport to " + targetName))));

        // freeze Button
        TextComponent freezeBtn = new TextComponent(color("&b&l[FREEZE]"));
        freezeBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/freeze " + targetName));
        freezeBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color("&bClick to freeze " + targetName))));

        alert.addExtra(tpBtn);
        alert.addExtra(freezeBtn);

        // broadcast to all staff with permission
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("smartreports.staff")) {
                online.spigot().sendMessage(alert);
            }
        }
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
