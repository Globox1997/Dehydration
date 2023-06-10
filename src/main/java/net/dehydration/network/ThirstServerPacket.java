package net.dehydration.network;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ThirstServerPacket {

    public static final Identifier THIRST_UPDATE = new Identifier("dehydration", "thirst_update");
    public static final Identifier EXCLUDED_SYNC = new Identifier("dehydration", "excluded_player_sync");
    public static final Identifier HYDRATION_TEMPLATE_SYNC = new Identifier("dehydration", "hydration_template_sync");

    public static void init() {
    }

    public static void writeS2CExcludedSyncPacket(ServerPlayerEntity serverPlayerEntity, boolean setThirst) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(setThirst);
        serverPlayerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(EXCLUDED_SYNC, buf));
    }

    public static void writeS2CThirstUpdatePacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIntArray(new int[] { serverPlayerEntity.getId(), ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().getThirstLevel() });
        serverPlayerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(ThirstServerPacket.THIRST_UPDATE, buf));
    }

    public static void writeS2CHydrationTemplateSyncPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        IntArrayList intList = new IntArrayList();
        DehydrationMain.HYDRATION_TEMPLATES.forEach((template) -> {
            intList.add(template.getHydration());
            intList.add(template.getItems().size());
        });
        buf.writeIntList(intList);

        DehydrationMain.HYDRATION_TEMPLATES.forEach((template) -> {
            template.getItems().forEach((item) -> {
                buf.writeIdentifier(Registries.ITEM.getId(item));
            });
        });
        serverPlayerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(ThirstServerPacket.HYDRATION_TEMPLATE_SYNC, buf));
    }
}
