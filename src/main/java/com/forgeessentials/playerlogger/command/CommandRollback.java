package com.forgeessentials.playerlogger.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRollback extends BaseCommand
{

    public CommandRollback(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    public static final String PERM = ModulePlayerLogger.PERM_COMMAND + ".rollback";
    public static final String PERM_ALL = PERM + Zone.ALL_PERMS;
    public static final String PERM_PREVIEW = PERM + ".preview";

    private static final String[] subCommands = { "help", "start", "cancel", "confirm", "play", "stop", "+", "-" };

    private Map<UUID, RollbackInfo> rollbacks = new HashMap<>();

    private Timer playbackTimer = new Timer();

    @Override
    public String getPrimaryAlias()
    {
        return "rollback";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "rb" };
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String arg = params.toString();
        switch (arg)
        {
        case "help":
            help(ctx.getSource());
            break;
        case "start":
            startRollback(ctx);
            break;
        case "cancel":
            cancelRollback(ctx);
            break;
        case "confirm":
            confirmRollback(ctx);
            break;
        case "+":
            stepRollback(ctx, 1);
            break;
        case "-":
            stepRollback(ctx, -1);
            break;
        case "play":
            playRollback(ctx);
            break;
        case "stop":
            stopRollback(ctx);
            break;
        default:
            throw new TranslatedCommandException("Unknown subcommand");
        }
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("deprecation")
    private void startRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);}


        if (rollbacks.containsKey(getServerPlayer(ctx.getSource()).getUUID()))
            cancelRollback(ctx);

        Selection area = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (area == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");

        int step = -60;
        if (!args.isEmpty())
        {
            String time = args.remove();
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date parsedDate = format.parse(time);
                Date currentDate = new Date();
                Date date = new Date();
                date.setSeconds(parsedDate.getSeconds());
                date.setMinutes(parsedDate.getMinutes());
                date.setHours(parsedDate.getHours());
                step = (int) ((date.getTime() - currentDate.getTime()) / 1000);
            }
            catch (ParseException e)
            {
                throw new TranslatedCommandException("Invalid time format: %s", time);
            }
        }

        RollbackInfo rb = new RollbackInfo(getServerPlayer(ctx.getSource()), area);
        rollbacks.put(getServerPlayer(ctx.getSource()).getUUID(), rb);
        rb.step(step);
        rb.previewChanges();

        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void stepRollback(CommandContext<CommandSource> ctx, int sec) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);}

        if (!args.isEmpty())
            sec = (int) (args.parseTimeReadable() / 1000) * sec;


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        rb.step(sec);
        rb.previewChanges();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Showing changes since " + FEConfig.FORMAT_DATE_TIME_SECONDS.format(rb.getTime()));
    }

    private void confirmRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM)) {throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);}

        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        rb.confirm();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Successfully restored changes");
    }

    private void cancelRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        RollbackInfo rb = rollbacks.remove(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress.");

        rb.cancel();
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cancelled active rollback");
    }

    private void playRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);}

        int speed = 1;
        if (!args.isEmpty())
            speed = parseInt(args.remove());
        if (speed == 0)
            speed = 1;
        if (Math.abs(speed) > 10)
            speed = (int) (Math.signum(speed) * 10);


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");

        if (rb.task != null)
        {
            rb.task.cancel();
            rb.task = null;
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped playback");
        }
        else
        {
            rb.task = new RollbackInfo.PlaybackTask(rb, (int) (Math.signum(speed)));
            playbackTimer.schedule(rb.task, 1000, 1000 / Math.abs(speed));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started playback");
        }
    }

    private void stopRollback(CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(),PERM_PREVIEW)) {throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);}


        RollbackInfo rb = rollbacks.get(getServerPlayer(ctx.getSource()).getUUID());
        if (rb == null)
            throw new TranslatedCommandException("No rollback in progress. Start with /rollback first.");
        if (rb.task == null)
            throw new TranslatedCommandException("No playback running");

        rb.task.cancel();
        rb.task = null;
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped playback");
    }

    private static void help(CommandSource sender)
    {
        ChatOutputHandler.chatConfirmation(sender, "/rollback: Start rollback");
        ChatOutputHandler.chatConfirmation(sender, "/rollback start [time]: Start rollback at specified time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback + [duration]: Go forward in time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback - [duration]: Go back in time");
        ChatOutputHandler.chatConfirmation(sender, "/rollback play [speed]: Playback changes like a video");
        ChatOutputHandler.chatConfirmation(sender, "/rollback stop: Stop playback");
        ChatOutputHandler.chatConfirmation(sender, "/rollback confirm: Confirm changes");
        ChatOutputHandler.chatConfirmation(sender, "/rollback cancel: Cancel rollback");
    }
}
