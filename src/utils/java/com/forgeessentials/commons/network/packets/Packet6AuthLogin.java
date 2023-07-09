package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet6AuthLogin implements IFEPacket
{
    /*
     * request to get hash from client
     */

    public Packet6AuthLogin()
    {
    }

    public static Packet6AuthLogin decode(PacketBuffer buf)
    {
        return new Packet6AuthLogin();
    }

    @Override
    public void encode(PacketBuffer buf)
    {
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        NetworkUtils.feletworklog.warn("Packet6AuthLogin was not handled properly");
    }

    public static void handler(final Packet6AuthLogin message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet6AuthLogin");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}