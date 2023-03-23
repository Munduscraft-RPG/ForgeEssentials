package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.List;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> <effect> <seconds> <amplifier>
 */
public class EffectPotion extends WorldBorderEffect implements Loadable
{

    public int id;

    public int duration;

    public int modifier;

    public int interval;

    public EffectPotion()
    {
    }

    @Override
    public void provideArguments(List<String> args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = CommandUtils.parseInt(args.remove(0));

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing potion id argument");
        id = CommandUtils.parseInt(args.remove(0));;

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing duration id argument");
        duration = CommandUtils.parseInt(args.remove(0));

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing modifier id argument");
        modifier =CommandUtils.parseInt(args.remove(0));
    }

    @Override
    public void afterLoad()
    {
        if (id == 0)
        {
            id = 9;
            duration = 5;
            modifier = 0;
        }
    }

    @Override
    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            player.addEffect(new EffectInstance(Effect.byId(id), duration, modifier, false, true, true));
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.addEffect(new EffectInstance(Effect.byId(id), duration, modifier, false, true, true));
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    @Override
    public String getSyntax()
    {
        return "<interval> <effect> <seconds> <amplifier>";
    }

    @Override
    public String toString()
    {
        return String.format("potion interval: %d1 id: %d2 duration: %d3 amplifier: %d4", interval, id, duration, modifier);
    }

}
