package me.devtec.fang.commands.defaults;

import me.devtec.fang.Fang;
import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import me.devtec.fang.utils.StringUtils;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Position;

import java.util.Arrays;
import java.util.Collection;

public class TeleportCommand extends CommandExecutor implements TabCompleter {
    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if(args.length==0){
            sender.sendMessage("/teleport <player> <x> <y> <z>");
            return;
        }
        if(args.length==1){
            Player player = Fang.getPlayer(args[0]);
            if(player==null){
                sender.sendMessage("Player "+args[0]+" is offline");
                return;
            }
            sender.sendMessage("Teleported "+((Player)sender).getUsername()+" to  "+player.getUsername());
            ((Player)sender).teleport(player.getPosition());
            return;
        }
        if(args.length==2){
            sender.sendMessage("/teleport <player> <x> <y> <z>");
            return;
        }
        if(args.length==3){
            Player player = Fang.getPlayer(args[0]);
            if(player==null){
                String x=args[0], y=args[1], z=args[2];
                Position c = ((Player)sender).getPosition();
                x=x.replace("~", c.getX()+"");
                y=y.replace("~", c.getY()+"");
                z=z.replace("~", c.getZ()+"");
                float xx = StringUtils.calculate(x).floatValue(), yy = StringUtils.calculate(y).floatValue(), zz = StringUtils.calculate(z).floatValue();
                sender.sendMessage("Teleported "+((Player)sender).getUsername()+" to  "+xx+", "+yy+", "+zz);
                ((Player)sender).teleport(new Position(xx, yy, zz));
                return;
            }
            sender.sendMessage("/teleport <player> <x> <y> <z>");
            return;
        }
        Player player = Fang.getPlayer(args[0]);
        if(player==null){
            sender.sendMessage("Player "+args[0]+" is offline");
            return;
        }
        String x=args[1], y=args[2], z=args[3];
        Position c = player.getPosition();
        x=x.replace("~", c.getX()+"");
        y=y.replace("~", c.getY()+"");
        z=z.replace("~", c.getZ()+"");
        float xx = StringUtils.calculate(x).floatValue(), yy = StringUtils.calculate(y).floatValue(), zz = StringUtils.calculate(z).floatValue();
        sender.sendMessage("Teleported "+player.getUsername()+" to  "+xx+", "+yy+", "+zz);
        player.teleport(new Position(xx, yy, zz, c.getYaw(), c.getPitch()));
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length==1) {
            Collection<String> set = Fang.getPlayerNames();
            set.add("~");
            set.add("~ ~");
            set.add("~ ~ ~");
            Position c = ((Player)sender).getPosition();
            set.add(String.format("%2.02f", c.getX()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY())+" "+String.format("%2.02f", c.getZ()));
            return set;
        }
        if(args.length==2) {
            Collection<String> set = Fang.getPlayerNames();
            set.add("~");
            set.add("~ ~");
            set.add("~ ~ ~");
            Position c = ((Player)sender).getPosition();
            set.add(String.format("%2.02f", c.getX()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY())+" "+String.format("%2.02f", c.getZ()));
            return set;
        }
        if(args.length==3) {
            Collection<String> set = Fang.getPlayerNames();
            set.add("~");
            set.add("~ ~");
            set.add("~ ~ ~");
            Position c = ((Player)sender).getPosition();
            set.add(String.format("%2.02f", c.getX()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY()));
            set.add(String.format("%2.02f", c.getX())+" "+String.format("%2.02f", c.getY())+" "+String.format("%2.02f", c.getZ()));
            return set;
        }
        return Arrays.asList();
    }
}
