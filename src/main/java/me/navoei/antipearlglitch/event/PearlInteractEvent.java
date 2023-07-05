package me.navoei.antipearlglitch.event;

import me.navoei.antipearlglitch.AntiPearlGlitch;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.logging.Logger;

public class PearlInteractEvent implements Listener {

    AntiPearlGlitch plugin = AntiPearlGlitch.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlTeleport(PlayerTeleportEvent event) {

        if (!(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;

        Player player = event.getPlayer();

        Location pearlLandingLocation = event.getTo();
        double patchedPearlYLocation = Math.rint(pearlLandingLocation.getY());
        pearlLandingLocation.setY(patchedPearlYLocation);
        //Set the initial pearl landing location and check if the player can bypass the checks.
        event.setTo(pearlLandingLocation);

        //Create a bounding box with and set it's center at the pearl landing location,
        BoundingBox playerHitBox = player.getBoundingBox();
        //Subtract the original bounding box coordinates to set them to zero. Then add the new bounding box coordinates to match the pearl land location.
        playerHitBox.shift(-playerHitBox.getCenterX()+pearlLandingLocation.getX(), -playerHitBox.getCenterY()+pearlLandingLocation.getY(), -playerHitBox.getCenterZ()+pearlLandingLocation.getZ());


        Block pearlLandingBlock = pearlLandingLocation.getBlock();
        Block pearlLandingBlockNorth = pearlLandingBlock.getRelative(BlockFace.NORTH);
        Block pearlLandingBlockSouth = pearlLandingBlock.getRelative(BlockFace.SOUTH);
        Block pearlLandingBlockEast = pearlLandingBlock.getRelative(BlockFace.EAST);
        Block pearlLandingBlockWest = pearlLandingBlock.getRelative(BlockFace.WEST);

        //Patch carpets.
        if (pearlLandingBlock.getType().name().contains("CARPET")) {
            pearlLandingLocation.setY(pearlLandingLocation.getY() + 0.08);
            playerHitBox.shift(0.0, 0.08, 0.0);
        }

        if (player.hasPermission("antipearlglitch.bypass")) return;

        //If the pearl lands somewhere without any blocks adjacent to the landing spot then return.
        if (
                pearlLandingBlock.getType().isAir() &&
                pearlLandingBlockNorth.getType().isAir() &&
                pearlLandingBlockSouth.getType().isAir() &&
                pearlLandingBlockEast.getType().isAir() &&
                pearlLandingBlockWest.getType().isAir()
        )  {
            return;
        }

        //Create bounding boxes for blocks that are adjacent to the pearl landing location.
        BoundingBox blockBoundingBox = pearlLandingBlock.getBoundingBox();
        BoundingBox blockBoundingBoxNorth = pearlLandingBlockNorth.getBoundingBox();
        BoundingBox blockBoundingBoxSouth = pearlLandingBlockSouth.getBoundingBox();
        BoundingBox blockBoundingBoxEast = pearlLandingBlockEast.getBoundingBox();
        BoundingBox blockBoundingBoxWest = pearlLandingBlockWest.getBoundingBox();

        //Check if the bounding boxes of the blocks under overlap with the bounding box of the player.
        if (playerHitBox.overlaps(blockBoundingBox) && !pearlLandingBlock.isPassable() && !pearlLandingBlock.getType().name().contains("CARPET")) {

            Block pearlLandingBlockDown = pearlLandingBlock.getRelative(BlockFace.DOWN);
            //Get all the blocks relative to the block that's under the pearl landing location.
            Block pearlLandingBlockDownNorth = pearlLandingBlockDown.getRelative(BlockFace.NORTH);
            Block pearlLandingBlockDownSouth = pearlLandingBlockDown.getRelative(BlockFace.SOUTH);
            Block pearlLandingBlockDownEast = pearlLandingBlockDown.getRelative(BlockFace.EAST);
            Block pearlLandingBlockDownWest = pearlLandingBlockDown.getRelative(BlockFace.WEST);

            if (pearlLandingBlockDown.isPassable()) {
                if (
                        !pearlLandingBlockDownNorth.getBoundingBox().overlaps(playerHitBox) && pearlLandingBlockDownNorth.isPassable() &&
                        !pearlLandingBlockDownSouth.getBoundingBox().overlaps(playerHitBox) && pearlLandingBlockDownSouth.isPassable() &&
                        !pearlLandingBlockDownEast.getBoundingBox().overlaps(playerHitBox) && pearlLandingBlockDownEast.isPassable() &&
                        !pearlLandingBlockDownWest.getBoundingBox().overlaps(playerHitBox) && pearlLandingBlockDownWest.isPassable()
                ) {
                    pearlLandingLocation.setY(pearlLandingLocation.getY()-1.0);
                    //Set the final teleport location.
                    event.setTo(pearlLandingLocation);
                    return;
                }
            }
            if (
                    pearlLandingBlockDownNorth.getBoundingBox().overlaps(playerHitBox) && !pearlLandingBlockDownNorth.isPassable() &&
                    pearlLandingBlockDownSouth.getBoundingBox().overlaps(playerHitBox) && !pearlLandingBlockDownSouth.isPassable() &&
                    pearlLandingBlockDownEast.getBoundingBox().overlaps(playerHitBox) && !pearlLandingBlockDownEast.isPassable() &&
                    pearlLandingBlockDownWest.getBoundingBox().overlaps(playerHitBox) && !pearlLandingBlockDownWest.isPassable() &&
                    !pearlLandingBlockDown.getBoundingBox().overlaps(playerHitBox) &&
                    !pearlLandingBlockDown.getRelative(BlockFace.DOWN).getBoundingBox().overlaps(playerHitBox)
            ) {
                pearlLandingLocation.setY(pearlLandingLocation.getY()-1.0);
                //Set the final teleport location.
                event.setTo(pearlLandingLocation);
                return;
            }

            //Checks are now done, proceed to cancel the event.
            //Return the pearl to the player then cancel the event.
            returnPearl(player);
            warnStaff(player, pearlLandingBlock);
            event.setCancelled(true);
            return;
        }

        if (playerHitBox.overlaps(blockBoundingBoxNorth) && !pearlLandingBlockNorth.isPassable() && !pearlLandingBlockNorth.getType().name().contains("CARPET")) {
            warnStaff(player, pearlLandingBlockNorth);

            pearlLandingLocation.setZ(pearlLandingBlockNorth.getRelative(BlockFace.SOUTH).getZ()+0.3);
        }
        if (playerHitBox.overlaps(blockBoundingBoxSouth) && !pearlLandingBlockSouth.isPassable() && !pearlLandingBlockSouth.getType().name().contains("CARPET")) {
            warnStaff(player, pearlLandingBlockSouth);

            pearlLandingLocation.setZ(pearlLandingBlockSouth.getRelative(BlockFace.NORTH).getZ()+0.7);
        }
        if (playerHitBox.overlaps(blockBoundingBoxEast) && !pearlLandingBlockEast.isPassable() && !pearlLandingBlockEast.getType().name().contains("CARPET")) {
            warnStaff(player, pearlLandingBlockEast);

            pearlLandingLocation.setX(pearlLandingBlockEast.getRelative(BlockFace.WEST).getX()+0.7);
        }
        if (playerHitBox.overlaps(blockBoundingBoxWest) && !pearlLandingBlockWest.isPassable() && !pearlLandingBlockWest.getType().name().contains("CARPET")) {
            warnStaff(player, pearlLandingBlockWest);

            pearlLandingLocation.setX(pearlLandingBlockWest.getRelative(BlockFace.EAST).getX()+0.3);
        }

        //Set the final teleport location.
        event.setTo(pearlLandingLocation);
    }

    public void returnPearl(Player player) {
        //Return the pearl if in survival or adventure mode.
        if (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL)) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
    }

    public void warnStaff(Player pearledPlayer, Block pearlGlitchBlock) {

        FileConfiguration config = plugin.getConfig();
        String message = config.getString("pearl-glitch-notify-message");
        boolean sendToConsole = config.getBoolean("log-pearl-glitch-to-console");
        if (message == null) return;

        String blockX = Integer.toString(pearlGlitchBlock.getX());
        String blockY = Integer.toString(pearlGlitchBlock.getY());
        String blockZ = Integer.toString(pearlGlitchBlock.getZ());
        String coordinates = "x=" + blockX + " y=" + blockY + " z=" + blockZ;

        String messageToPlayer = message.replace("%player%", pearledPlayer.getName()).replace("%block%", pearlGlitchBlock.getType().name()).replace("%coordinates%", coordinates);

        if (sendToConsole) {
            Logger logger = Bukkit.getServer().getLogger();
            logger.info(ChatColor.translateAlternateColorCodes('&', messageToPlayer));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("antipearlglitch.notify")) return;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageToPlayer));
        }
    }

}
