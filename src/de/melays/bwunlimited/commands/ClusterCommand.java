package de.melays.bwunlimited.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.error.InvalidNameException;
import de.melays.bwunlimited.map_manager.ClusterHandler;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.map_manager.error.ClusterAlreadyExistsException;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.map_manager.error.WrongWorldException;

public class ClusterCommand {
	
	Main main;
	
	public ClusterCommand(Main main) {
		this.main = main;
	}
	
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw cluster help");
		helpSender.addAlias("getTool", "Get the Cluster-Template Selection Tool", "Gives you an selection tool\nto select the corners of a cluster template" , "/bw cluster getTool");
		helpSender.addAlias("create <name>", "Create a Cluster-Template from Selection", "Create a Cluster-Template from\nthe locations you set with\nthe selction tool" , "/bw cluster create <name>");
		helpSender.addAlias("createWithTemplate", "Create a Cluster-Template", "\nRun the command for more help\nGenerates a Cluster-Template\nto make it easier for you\nto create a new Cluster" , "/bw cluster createWithTemplate");
		helpSender.addAlias("generate <name>", "Generate a Cluster-Template", "For test purposes only!" , "/bw cluster generate <cluster>");
		
		if (args.length == 1) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[1].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length == 2) {
				helpSender.sendHelp(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[2]);
					helpSender.sendHelp(sender, page);
				} catch (NumberFormatException e) {
					helpSender.sendHelp(sender, 1);
				}
			}
		}
		
		else if (args[1].equalsIgnoreCase("getTool")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			main.getMarkerTool().givePlayer((Player)sender);
		}
		
		else if (args[1].equalsIgnoreCase("create")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length == 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw cluster create <name>"));
				return;
			}
			if (!main.getMarkerTool().isReady((Player) sender)){
				sender.sendMessage(main.prefix + "You need to set 2 different locations in the same world!");
			}
			try {
				main.getClusterManager().createCluster(args[2], main.getMarkerTool().get1((Player) sender) , main.getMarkerTool().get2((Player) sender));
				sender.sendMessage(main.prefix + "The cluster has been created successfully!");
			} catch (ClusterAlreadyExistsException e) {
				sender.sendMessage(main.prefix + "There is already a cluster with this name!");
			} catch (InvalidNameException e) {
				sender.sendMessage(main.prefix + "Please use only alphanumeric characters (A-Z , a-z , 0-9)");
			} catch (WrongWorldException e) {
				sender.sendMessage(main.prefix + "Please create the presets in the preset-world!");
				sender.sendMessage(main.prefix + "Go there using /bw worldtp presets");
			}
		}
		
		else if (args[1].equalsIgnoreCase("createWithTemplate")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.prefix + "createWithTemplate Syntax:");
				sender.sendMessage(main.prefix + "  /bw cluster createWithTemplate <template> <name>");
				sender.sendMessage(main.prefix + "  This will create a new Cluster with the given name");
				sender.sendMessage(main.prefix + "  and will generate a example Cluster in front of you.");
				sender.sendMessage(main.prefix + "    What to fill in instead of <template>?");
				sender.sendMessage(main.prefix + "    2X - A long Cluster made for 2 Teams");
				sender.sendMessage(main.prefix + "    4X - A cubic Cluster made for 4+ Teams");
				sender.sendMessage(main.prefix + "    128w;32h;128l;4t - Fill in custom values:"); 
				sender.sendMessage(main.prefix + "       w - width , h - height , l - lenght , t - teams");
				sender.sendMessage(main.prefix + "  This command will check if there is enaught space!");
				return;
			}
			if (!((Player)sender).getWorld().getName().equals(main.presetworld)) {
				sender.sendMessage(main.prefix + "Please create the presets in the preset-world!");
				sender.sendMessage(main.prefix + "Go there using /bw worldtp presets");
				return;
			}
			try {
				if (args[2].equalsIgnoreCase("2X")) {
					if (ClusterTools.generateTemplate(main.getClusterManager() , args[3] , ((Player)sender).getLocation() , 32 , 20 , 86 , 2)) {
						sender.sendMessage(main.prefix + "A cluster-template has been generated successfully!");
						sender.sendMessage(main.prefix + "The cluster is not loaded yet!");
					}
					else {
						sender.sendMessage(main.prefix + "The area you want to create a cluster in is not empty!");
					}
				}
				else if (args[2].equalsIgnoreCase("4X")) {
					if (ClusterTools.generateTemplate(main.getClusterManager() , args[3] ,((Player)sender).getLocation() , 86 , 20 , 86 , 4)) {
						sender.sendMessage(main.prefix + "A cluster-template has been generated successfully!");
						sender.sendMessage(main.prefix + "The cluster is not loaded yet!");
					}
					else {
						sender.sendMessage(main.prefix + "The area you want to create a cluster in is not empty!");
					}
				}
				else {
					try {
						String[] split = args[2].split(";");
						if (split.length != 4) {
							throw new NumberFormatException();
						}
						int w = Integer.parseInt(split[0].replaceAll("w", ""));
						int h = Integer.parseInt(split[1].replaceAll("h", ""));
						int l = Integer.parseInt(split[2].replaceAll("l", ""));
						int t = Integer.parseInt(split[3].replaceAll("t", ""));
						
						if (t != 4 && t != 2 && t != 0) {
							sender.sendMessage(main.prefix + "Only 2 and 4 Teams can be generated. Generating 0!");
						}
						
						if (ClusterTools.generateTemplate(main.getClusterManager() , args[3] ,((Player)sender).getLocation() , w , h , l , t)) {
							sender.sendMessage(main.prefix + "A cluster-template has been generated successfully!");
							sender.sendMessage(main.prefix + "The cluster is not loaded yet!");
						}
						else {
							sender.sendMessage(main.prefix + "The area you want to create a cluster in is not empty!");
						}
						
					} catch (NumberFormatException e) {
						e.printStackTrace();
						sender.sendMessage(main.prefix + "Please check you template syntax! '"+args[2]+"' doesn't seem valid!");
					}
				}
			} catch (ClusterAlreadyExistsException e) {
				sender.sendMessage(main.prefix + "There is already a cluster with this name!");
			} catch (InvalidNameException e) {
				sender.sendMessage(main.prefix + "Please use only alphanumeric characters (A-Z , a-z , 0-9)");
			} catch (WrongWorldException e) {
				sender.sendMessage(main.prefix + "Please create the presets in the preset-world!");
				sender.sendMessage(main.prefix + "Go there using /bw worldtp presets");
				return;
			} catch (Exception e) {
				sender.sendMessage(main.prefix + "An error occured! Please check you template syntax! '"+args[2]+"' doesn't seem valid!");
				return;
			}
		}
		
		else if (args[1].equalsIgnoreCase("generate")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw cluster generate <cluster>"));
				return;
			}
			try {
				ClusterHandler handler = main.getClusterManager().getNewHandler(args[2]);
				sender.sendMessage(main.prefix +  "Generating the cluster at your location...");
				handler.generate(((Player) sender).getLocation());
			} catch (UnknownClusterException e) {
				sender.sendMessage(main.prefix +  "This cluster does not exist!");
			} catch (ClusterAvailabilityException e) {
				sender.sendMessage(main.prefix +  "This cluster is not ready yet!");
			}
		}
	}
	
}
