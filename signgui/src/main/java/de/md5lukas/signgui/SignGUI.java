package de.md5lukas.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedRegistrable;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class SignGUI {

  private static final int OFFSET = 4; // Needs to be closer to the player since 1.20
  private static final boolean newSignFormat = new MinecraftVersion("1.20").atOrAbove();
  private static final List<Component> EMPTY_LINES =
      Arrays.asList(Component.empty(), Component.empty(), Component.empty(), Component.empty());

  private final @NotNull Plugin plugin;
  private final @NotNull ProtocolManager protocolManager;
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
    this.protocolManager = ProtocolLibrary.getProtocolManager();
    this.player = player;
    this.onClose = onClose;
    this.color = color;
    this.lines = lines;
    this.open = false;
  }

  private void open() {
    if (open) {
      return;
    }
    open = true;

    final var signLocation = getSignLocation();

    protocolManager.addPacketListener(
        new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.UPDATE_SIGN) {
          @Override
          public void onPacketReceiving(PacketEvent event) {
            if (event.getPlayer() != player) {
              return;
            }
            protocolManager.removePacketListener(this);
            event.setCancelled(true);
            open = false;
            player.sendBlockChange(signLocation, signLocation.getBlock().getBlockData());

            final var lines = event.getPacket().getStringArrays().read(0);

            for (int index = 0; index < 3; index++) {
              SignGUI.this.lines.set(index, fromPlain(lines[index]));
            }

            onClose.accept(lines);
          }
        });

    player.sendBlockChange(signLocation, plugin.getServer().createBlockData(Material.OAK_SIGN));

    final var signBlockPos = new BlockPosition(
        signLocation.getBlockX(), signLocation.getBlockY(), signLocation.getBlockZ());

    if (lines != EMPTY_LINES) {
      final var changeSignContent = new PacketContainer(PacketType.Play.Server.TILE_ENTITY_DATA);
      changeSignContent.getBlockPositionModifier().write(0, signBlockPos);
      changeSignContent
          .getBlockEntityTypeModifier()
          .write(0, WrappedRegistrable.blockEntityType("sign"));
      changeSignContent.getNbtModifier().write(0, createSignTileData());
      protocolManager.sendServerPacket(player, changeSignContent);
    }

    final var openSignPacket = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
    openSignPacket.getBlockPositionModifier().write(0, signBlockPos);
    openSignPacket.getBooleans().writeSafely(0, true); // 1.20 front
    protocolManager.sendServerPacket(player, openSignPacket);
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

  private @NotNull NbtCompound createSignTileData() {
    final var signData = NbtFactory.ofCompound("");

    // See https://minecraft.fandom.com/wiki/Sign#Block_data
    if (newSignFormat) {
      signData.put("is_waxed", asByte(false));
      signData.put(createTextSideCompound("front_text", lines));
      signData.put(createTextSideCompound("back_text", EMPTY_LINES));
    } else {
      signData.put("GlowingText", asByte(false));
      signData.put("Color", color.name().toLowerCase(Locale.ROOT));

      for (int index = 0; index < 3; index++) {
        signData.put(
            "Text" + (index + 1), GsonComponentSerializer.gson().serialize(lines.get(index)));
      }
    }

    return signData;
  }

  private @NotNull NbtCompound createTextSideCompound(
      @NotNull String name, @NotNull List<@NotNull Component> lines) {
    final var text = NbtFactory.ofCompound(name);

    text.put("has_glowing_text", asByte(false));
    text.put("color", color.name().toLowerCase(Locale.ROOT));

    final NbtList<String> messages = NbtFactory.ofList("messages");

    for (int index = 0; index < 4; index++) {
      messages.add(GsonComponentSerializer.gson().serialize(lines.get(index)));
    }

    text.put(messages);

    return text;
  }

  private @NotNull Component fromPlain(@NotNull String json) {
    return PlainTextComponentSerializer.plainText().deserialize(json);
  }

  private byte asByte(boolean value) {
    return (byte) (value ? 1 : 0);
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
