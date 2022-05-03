package com.forgeessentials.client.handler;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * Just a utility class. Pressing the buttons while there is no question asked will only give you an error message.
 */
public class QuestionerKeyHandler
{
    private static final String category = "keys.fe.questioner";
    private static final KeyBinding yes = new KeyBinding("keys.fe.yes", 297, category);
    private static final KeyBinding no = new KeyBinding("keys.fe.no", 298, category);

    public QuestionerKeyHandler()
    {
        ClientRegistry.registerKeyBinding(yes);
        ClientRegistry.registerKeyBinding(no);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onKeyPress(ClickInputEvent e)
    {
        if (!FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            return;
        }
        if (yes.isDown())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/yes");
        }
        else if (no.isDown())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/no");
        }
    }
}
