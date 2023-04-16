package com.bridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.bridge.commands.startgame;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public final class Bridge extends JavaPlugin implements Listener {

    public static Gson gson = new Gson();


    public static HashMap<UUID, Location> spawn = new HashMap<UUID, Location>();
    public static HashMap<UUID, Location> bed = new HashMap<UUID, Location>();
    public static HashMap<UUID, Integer> count = new HashMap<UUID, Integer>();
    public static Location pastpoint; // *Pastepoint
    public static JavaPlugin plugin; // this
    public static boolean trap = true; // Trap on or not
    public static double trapmultiplyer = 1.0; // Angle of the Trap: x to the side | 1 up
    public static short blockcount = 64; // How many Blocks are in you Inventory
    public static List<Block> resetBlocks = new ArrayList<Block>(); // To replace worldedit

    public static boolean saveconfig() {
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection configspawn = config.createSection("spawn");
        ConfigurationSection configbed = config.createSection("bed");
        for (UUID uuid : spawn.keySet()) {
            configspawn.set(uuid.toString(), spawn.get(uuid));
        }
        for (UUID uuid : bed.keySet()) {
            configbed.set(uuid.toString(), bed.get(uuid));
        }
        config.set("paste", pastpoint);
        try {
            config.save("plugins/hole/saves.yml");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void loadconfig() {
        File file = new File("plugins/hole/saves.yml");
        if (!file.exists() || file.isDirectory()) {
            File directory = new File("plugins/hole");
            directory.mkdir();
            return;
        };
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load("plugins/hole/saves.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ConfigurationSection configspawn = config.getConfigurationSection("spawn");
        ConfigurationSection configbed = config.getConfigurationSection("bed");
        for (String suuid : configspawn.getKeys(true)) {
            UUID uuid = UUID.fromString(suuid);
            spawn.put(uuid, (Location)configspawn.get(suuid));
        }
        for (String suuid : configbed.getKeys(true)) {
            UUID uuid = UUID.fromString(suuid);
            bed.put(uuid, (Location)configbed.get(suuid));
        }
        pastpoint = (Location) config.get("paste");
    }


    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("game").setExecutor(this);
        Bukkit.getPluginCommand("game").setTabCompleter(this);

        Bukkit.getPluginCommand("startgame").setExecutor(new startgame());
        Bukkit.getPluginCommand("startgame").setTabCompleter(new startgame());
        loadconfig();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!spawn.containsKey(p.getUniqueId())) {
            all("Kein Spawn von "+p.getDisplayName());
            return;
        }
        if (p.getLocation().getBlockY() <= 32) {
            //p.teleport(spawn.get(p.getUniqueId()));
            e.setTo(spawn.get(p.getUniqueId()));
            resetinv(p);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) return;
        Material m = block.getType();
        if (m == null) return;
        if (m == Material.BED || m == Material.BED_BLOCK) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
            }
        }
    }

    private static BlockFace[] directions = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.getType() == Material.BED_BLOCK || b.getType() == Material.BED) {
            e.setCancelled(true);
            for (UUID uuidp : bed.keySet()) {
                Player p = Bukkit.getPlayer(uuidp);

                Block bedBlock = bed.get(uuidp).getBlock();
                String facing = bedBlock.getMetadata("facing").toString();

                boolean isHisBed = bedBlock.getX() == b.getX() && bedBlock.getY() == b.getY() && bedBlock.getZ() == b.getZ();
                if (!isHisBed) {
                    for (BlockFace face : directions) {
                        Block block = b.getRelative(face);
                        if (bedBlock.getX() == block.getX() && bedBlock.getY() == block.getY() && bedBlock.getZ() == block.getZ()) {
                            isHisBed = true;
                            break;
                        }
                    }
                }
                if (isHisBed) {
                    if (!count.containsKey(uuidp)) {
                        count.put(uuidp, 1);
                    }else {
                        count.put(uuidp, count.get(uuidp) + 1);
                    }
                    if (p != null) {
                        if (p.getName().equalsIgnoreCase("SchBieSensei") && ((int)count.get(uuidp)) == 10) {
                            stall("Never Gone Give You Up");
                        }
                        all(p.getDisplayName() + " #Noob "+count.get(uuidp));
                    }else {
                        all("Offline" + " #Ehrenlos "+count.get(uuidp));
                    }
                    reset();
                    break;
                }
            }
        }
        if (b.getType() != Material.SANDSTONE) {
            e.setCancelled(true);
        }
    }

    public boolean isBed(Block b) {
        return b.getType() == Material.BED || b.getType() == Material.BED_BLOCK;
    }

    @EventHandler
    public void onplace(BlockPlaceEvent e) {
        if (trap) {
            Block block = e.getBlock();
            for (UUID uuid : spawn.keySet()) {
                Location loc = (Location)spawn.get(uuid);
                int x_abstand = loc.getBlockX() - block.getX();
                int y_abstand = loc.getBlockY() - block.getY();
                int z_abstand = loc.getBlockZ() - block.getZ();
                x_abstand = Math.abs(x_abstand);
                y_abstand = Math.abs(y_abstand);
                z_abstand = Math.abs(z_abstand);

                y_abstand *= trapmultiplyer;
                if (y_abstand >= x_abstand && y_abstand >= z_abstand) {
                    e.setCancelled(true);
                    return;
                }
            }
        }else {
            Block block = e.getBlock();
            for (UUID uuid : spawn.keySet()) {
                Location loc = (Location)spawn.get(uuid);
                int x_abstand = loc.getBlockX() - block.getX();
                int y_abstand = loc.getBlockY() - block.getY();
                int z_abstand = loc.getBlockZ() - block.getZ();
                x_abstand = Math.abs(x_abstand);
                y_abstand = Math.abs(y_abstand);
                z_abstand = Math.abs(z_abstand);

                if (x_abstand == 0 && z_abstand == 0 && y_abstand <= 1) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        resetBlocks.add(e.getBlock());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.FALL) {
            e.setCancelled(true);
        }
        e.setDamage(0.0);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        resetinv(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player)s;
        Player p2;
        if (!(args.length >= 1)) return true;

        switch (args[0].toLowerCase()) {
            case "bed":
                if (!p.isOp()) {
                    return true;
                }
                if (args.length != 2) return true;
                p2 = Bukkit.getPlayer(args[1]);
                if (p2 == null) return true;
                bed.put(p2.getUniqueId(), p.getLocation().getBlock().getLocation());
                p.sendMessage("bed set to: "+p.getLocation().toString());
                break;

            case "spawn":
                if (!p.isOp()) {
                    return true;
                }
                if (args.length != 2) return true;
                p2 = Bukkit.getPlayer(args[1]);
                if (p2 == null) return true;
                spawn.put(p2.getUniqueId(), p.getLocation().getBlock().getLocation());
                p.sendMessage("spawn set to: "+p.getLocation().toString());
                break;

            case "reset":
                count.clear();
                resetAll();
                p.sendMessage("reset");
                break;

            case "paste":
                if (!p.isOp()) {
                    return true;
                }
                pastpoint = p.getLocation().getBlock().getLocation();
                p.sendMessage("paste set to: "+p.getLocation().toString());
                break;

            case "save":
                if (!p.isOp()) {
                    return true;
                }
                if (saveconfig()) {
                    p.sendMessage("Config gespeichert");
                }else {
                    p.sendMessage("Error");
                }
                break;

            case "trap":
                if (args.length != 2) {
                    trapmultiplyer = 1.0;
                    trap = true;
                    p.sendMessage("Multiplikator auf 1,0 gesetzt");
                }else {
                    if (args[1].equalsIgnoreCase("off")) {
                        trap = false;
                        p.sendMessage("Fallenschutz deaktiviert");
                    }else if (args[1].equalsIgnoreCase("on")) {
                        trap = true;
                        p.sendMessage("Fallenschutz aktiviert");
                    }else {
                        trap = true;
                        try {
                            double d = Double.parseDouble(args[1]);
                            trapmultiplyer = d;
                        }catch (Exception e) {
                            try {
                                int i = Integer.parseInt(args[1]);
                                trapmultiplyer = (double)i;
                            }catch (Exception e2) {
                                p.sendMessage("Bitte gebe eine Zahl ein");
                                return true;
                            }
                        }
                        p.sendMessage("Multiplikator auf "+Double.toString(trapmultiplyer)+" gesetzt");
                    }
                }
                break;

            case "block":
                if (args.length != 2) {
                    blockcount = 64;
                    p.sendMessage("BlockCount auf 64 gesetzt");
                }else {
                    try {
                        short i = (short) (Short.parseShort(args[1]));
                        blockcount = i;
                    }catch (Exception e) {
                        p.sendMessage("Bitte gebe eine Zahl ein");
                        return true;
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return null;
        Player p = (Player)s;
        if (p.isOp()) {
            if (args.length == 1) {
                List<String> list = new ArrayList<String>();
                list.add("bed");
                list.add("spawn");
                list.add("reset");
                list.add("paste");
                list.add("save");
                list.add("trap");
                list.add("block");
                return list;
            }else if (args.length == 2) {
                List<String> list = new ArrayList<String>();
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    list.add(pl.getName());
                }
                return list;
            }
        }else {
            if (args.length == 1) {
                List<String> list = new ArrayList<String>();
                list.add("reset");
                list.add("trap");
                list.add("block");
                return list;
            }else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("trap")) {
                    List<String> list = new ArrayList<String>();
                    list.add("off");
                    list.add("on");
                    list.add("1.0");
                    return list;
                }
            }
        }
        return null;
    }

    public static void error(String str) {
        for (OfflinePlayer op : Bukkit.getOperators()) {
            if (op.isOnline()) {
                Player p = Bukkit.getPlayer(op.getUniqueId());
                p.sendMessage(str);
            }
        }
    }

    public static void all(String str) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(str);
        }
    }

    public static void stall(String str) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("", str);
        }
    }

    public static Player getop() {
        for (OfflinePlayer op : Bukkit.getOperators()) {
            if (op.isOnline()) {
                return Bukkit.getPlayer(op.getUniqueId());
            }
        }
        return null;
    }

    public static Player getplayer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            return p;
        }
        return null;
    }

    public static void reset() {
        for (Block res : resetBlocks) {
            res.setType(Material.AIR);
        }
        resetBlocks.clear();
        for (UUID uuidp : spawn.keySet()) {
            Player p = Bukkit.getPlayer(uuidp);
            if (p != null) {
                resetinv(p);
            }
        }
    }
    public static void resetAll() {
        Player op = getop();
        boolean b = false;
        if (op == null) {
            b = true;
            op = getplayer();
            if (op == null) {
                return;
            }
            op.setOp(true);
        }
        op.teleport(pastpoint);
        op.performCommand("/schem load Map");
        op.performCommand("/paste");
        resetBlocks.clear();
        for (UUID uuidp : spawn.keySet()) {
            Player p = Bukkit.getPlayer(uuidp);
            if (p != null) {
                resetinv(p);
            }
        }
        if (b) {
            op.setOp(false);
        }
    }
    public static void resetinv(Player p) {
        p.teleport(spawn.get(p.getUniqueId()));
        p.getInventory().clear();
        ItemStack is = new ItemStack(Material.SANDSTONE);
        is.setAmount(blockcount);
        p.getInventory().setItem(8, is);
        p.getInventory().setItem(4, new ItemStack(Material.IRON_PICKAXE, 1));
        is = new ItemStack(Material.STICK);
        ItemMeta meta = is.getItemMeta();
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        is.setItemMeta(meta);
        p.getInventory().setItem(0, is);
    }
}
