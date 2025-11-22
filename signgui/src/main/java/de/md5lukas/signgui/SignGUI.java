package de.md5lukas.signgui;

import com.google.common.base.Preconditions;
import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class SignGUI implements Listener {

  private static final int OFFSET = 4; // Needs to be closer to the player since 1.20
  private static final List<Component> EMPTY_LINES =
      List.of(Component.empty(), Component.empty(), Component.empty(), Component.empty());

  private final @NotNull Plugin plugin;
  private final @NotNull Player player;
  private final @NotNull Consumer<@NotNull String @NotNull []> onClose;

  private final @NotNull DyeColor color;
  private final @NotNull List<@NotNull Component> lines;

  private boolean open;

  private SignGUI(
      @NotNull Plugin plugin,
      @NotNull Player player,
      @NotNull Consumer<@NotNull String @NotNull []> onClose,
      @NotNull DyeColor color,
      @NotNull List<@NotNull Component> lines) {
    this.plugin = plugin;
    this.player = player;
    this.onClose = onClose;
    this.color = color;
    this.lines = lines;
    this.open = false;
  }

  @SuppressWarnings("UnstableApiUsage")
  private void open() {
    if (open) {
      return;
    }
    open = true;

    final var signLocation = getSignLocation();

    plugin
        .getServer()
        .getPluginManager()
        .registerEvent(
            UncheckedSignChangeEvent.class,
            this,
            EventPriority.NORMAL,
            (listener, event) -> {
              if (listener == this && event instanceof UncheckedSignChangeEvent signChangeEvent) {
                if (signChangeEvent.getPlayer() != player) {
                  return;
                }
                HandlerList.unregisterAll(this);
                signChangeEvent.setCancelled(true);
                open = false;

                plugin.getServer().getRegionScheduler().execute(plugin, signLocation, () -> {
                  // This API should only be called on the main server thread
                  final var realBlock = signLocation.getBlock();
                  player.sendBlockChange(signLocation, realBlock.getBlockData());
                  if (realBlock.getState() instanceof TileState tileState) {
                    player.sendBlockUpdate(signLocation, tileState);
                  }
                });

                onClose.accept(signChangeEvent.lines().stream()
                    .map(PlainTextComponentSerializer.plainText()::serialize)
                    .toArray(String[]::new));
              }
            },
            plugin);

    final var blockData = plugin.getServer().createBlockData(Material.OAK_SIGN);
    player.sendBlockChange(signLocation, blockData);

    if (lines != EMPTY_LINES) {
      final var blockState = (Sign) blockData.createBlockState();
      final var side = blockState.getSide(Side.FRONT);
      side.setColor(color);

      for (int index = 0; index < 4; index++) {
        side.line(index, lines.get(index));
      }

      player.sendBlockUpdate(signLocation, blockState);
    }

    player.openVirtualSign(signLocation, Side.FRONT);
  }

  private @NotNull Location getSignLocation() {
    final @NotNull var signLocation = player.getLocation().toBlockLocation();

    if (signLocation.getY() <= signLocation.getWorld().getMinHeight() + OFFSET) {
      signLocation.add(0, OFFSET, 0);
    } else {
      signLocation.subtract(0, OFFSET, 0);
    }
    return signLocation;
  }

  public static @NotNull Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {}

    private Plugin plugin;
    private Player player;
    private Consumer<@NotNull String @NotNull []> onClose;
    private @NotNull DyeColor color = DyeColor.BLACK;
    private @NotNull List<@NotNull Component> lines = EMPTY_LINES;

    public @NotNull Builder plugin(@NotNull Plugin plugin) {
      Preconditions.checkNotNull(plugin);
      this.plugin = plugin;
      return this;
    }

    public @NotNull Builder player(@NotNull Player player) {
      Preconditions.checkNotNull(player);
      this.player = player;
      return this;
    }

    public @NotNull Builder onClose(@NotNull Consumer<@NotNull String @NotNull []> onClose) {
      Preconditions.checkNotNull(onClose);
      this.onClose = onClose;
      return this;
    }

    public @NotNull Builder color(@NotNull DyeColor color) {
      Preconditions.checkNotNull(color);
      this.color = color;
      return this;
    }

    public @NotNull Builder lines(@NotNull @Unmodifiable List<@NotNull String> lines) {
      Preconditions.checkNotNull(lines);
      Preconditions.checkArgument(lines.size() == 4);
      this.lines = new ArrayList<>(4);
      for (final var line : lines) {
        this.lines.add(PlainTextComponentSerializer.plainText().deserialize(line));
      }
      return this;
    }

    public @NotNull SignGUI open() {
      Preconditions.checkNotNull(plugin);
      Preconditions.checkNotNull(player);
      Preconditions.checkNotNull(onClose);
      final var signGui = new SignGUI(plugin, player, onClose, color, lines);
      signGui.open();
      return signGui;
    }
  }
}
