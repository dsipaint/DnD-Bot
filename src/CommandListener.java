import java.util.Random;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter
{
	private Random r;
	
	public CommandListener()
	{
		r = new Random();
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e)
	{
		String msg = e.getMessage().getContentRaw();
		String[] arguments = msg.split(" ");
		
		if(arguments[0].equalsIgnoreCase(Main.PREFIX + "roll"))
		{
			if(arguments.length >= 2)
			{
				String[] diceArgs = arguments[1].split("d");
				
				int number_of_throws = 1;
				if(diceArgs[0].matches("\\d+") && diceArgs[0].length() < 6)//>roll "x"d20
					number_of_throws = Integer.parseInt(diceArgs[0]);
				
				int dice_size = 1;
				if(diceArgs[1].matches("\\d+") && diceArgs[1].length() < 6)
					dice_size = Integer.parseInt(diceArgs[1]);
				if(dice_size == 0)
					dice_size = 1; //stops people trying to put annoying values in the bot
				
				int stat_bonus = 0;
				int DC = 0;
				if(arguments.length >= 3)
				{
					//we have >roll "x"d"y" + number
					if(arguments[2].matches("[+-]\\d+") && arguments.length < 6)
					{
						stat_bonus = Integer.parseInt(arguments[2]);
						if(arguments.length >= 4) //if a stat bonus is stated, find the DC
						{
							if(arguments[3].matches("\\d+") && arguments.length < 6)
								DC = Integer.parseInt(arguments[3]);
						}
					}
					
					//if no stat bonus is given, find the DC
					if(arguments[2].matches("\\d+") && arguments.length < 6)
						DC = Integer.parseInt(arguments[2]);
				}
				
				String roll_msg = "*You roll  " + number_of_throws + " time" + (number_of_throws == 1 ? "":"s") + ". Your roll" + (number_of_throws == 1 ? " is":"s are") + ":*";
				String dice_rolls = Integer.toString(r.nextInt(dice_size) + 1 + stat_bonus);
				int total = Integer.parseInt(dice_rolls); //tally the first roll too
				if(total - stat_bonus == 20)
					dice_rolls += " (Critical hit!) ";
				if(total - stat_bonus == 1)
					dice_rolls += " (Critical failure!) ";

				for(int i = 1; i < number_of_throws; i++)
				{
					int roll = r.nextInt(dice_size) + 1 + stat_bonus;
					dice_rolls += " + " + roll;
					
					if(roll - stat_bonus == 20)
						dice_rolls += " (Critical hit!) ";
					if(roll - stat_bonus == 1)
						dice_rolls += " (Critical failure!) ";
					
					total += roll;
				}
				
				if(number_of_throws == 1)
					roll_msg += "\n" + dice_rolls;
				else
					roll_msg += "\n" + dice_rolls + " = " + total;
				
				if(DC != 0)
				{
					if(total >= DC)
						roll_msg += "\nThe check was a success!";
					else
						roll_msg += "\nThe check was a failure!";
					
				}
				
				e.getChannel().sendMessage(roll_msg).queue();
			}
		}
		
		if(arguments[0].equalsIgnoreCase(Main.PREFIX + "stop"))
		{
			e.getChannel().sendMessage("***Shutting down***").queue();
			System.exit(0);
		}
	}
	
}
