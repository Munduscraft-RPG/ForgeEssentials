package com.forgeessentials.mapper.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.mapper.MapperUtil;
import com.forgeessentials.mapper.ModuleMapper;
import com.forgeessentials.util.CommandParserArgs;

public class CommandMapper extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "mapper";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.mapper.command";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        int x = (int) Math.floor(arguments.senderPlayer.position().x);
        int z = (int) Math.floor(arguments.senderPlayer.position().z);
        ServerWorld world = (ServerWorld) arguments.senderPlayer.getLevel();
        BufferedImage img = ModuleMapper.getInstance().getRegionImage(world, MapperUtil.worldToRegion(x), MapperUtil.worldToRegion(z));
        try
        {
            ImageIO.write(img, "png", new File(ForgeEssentials.getFEDirectory(), "region.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
