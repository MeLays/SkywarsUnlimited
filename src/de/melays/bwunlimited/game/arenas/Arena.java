package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.colortab.ColorTabAPI;
import de.melays.bwunlimited.entitys.Merchant;
import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.settings.LeaveType;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterHandler;
import de.melays.bwunlimited.map_manager.FineRelativeLocation;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.map_manager.meta.ItemSpawner;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class Arena {

	public Cluster cluster;
	public Location relative;

	public Settings settings;

	public ArenaState state = ArenaState.LOBBY;

	public Main main;

	public ArenaLobby arenaLobby;
	public ArenaTeamManager teamManager;
	public ClusterHandler clusterHandler;
	public DeathManager deathManager;
	public ArenaScoreboard scoreBoard;
	public BlockManager blockManager;

	// PlayerLists
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> specs = new ArrayList<Player>();

	// ItemSpawner
	public ArrayList<ItemSpawner> itemSpawners = new ArrayList<ItemSpawner>();

	public int id;

	public Arena(Main main, Cluster cluster, Location relative, Settings settings, int id)
			throws ClusterAvailabilityException, UnknownClusterException {
		this.main = main;
		this.cluster = cluster;
		this.relative = relative;
		this.settings = settings;
		this.id = id;
		this.settings.setArena(this);

		// Setting up instances
		this.arenaLobby = new ArenaLobby(this);
		this.teamManager = new ArenaTeamManager(this);
		this.deathManager = new DeathManager(this);
		this.scoreBoard = new ArenaScoreboard(this);
		this.blockManager = new BlockManager(this);

		// Starting the generation of the Cluster
		clusterHandler = main.getClusterManager().getNewHandler(cluster.name);
		clusterHandler.generate(relative);

		itemSpawners = cluster.getClusterMeta().getItemSpawners();
	}

	// Playermanagement

	public ArrayList<Player> getAllPlayers() {
		ArrayList<Player> r = new ArrayList<Player>();
		r.addAll(players);
		for (ArenaTeam team : this.teamManager.getTeams()) {
			for (Player p : team.players) {
				if (!r.contains(p))
					r.add(p);
			}
		}
		return r;
	}

	public ArrayList<Player> getAll() {
		ArrayList<Player> r = new ArrayList<Player>();
		r.addAll(getAllPlayers());
		r.addAll(specs);
		return r;
	}

	public boolean addTeamPackage(TeamPackage teamPackage) {
		if (this.getAllPlayers().size() + teamPackage.size() <= settings.max_players) {
			for (String s : teamPackage.getTeams()) {
				for (Player p : teamPackage.getPlayers(s)) {
					if (!main.getArenaManager().isInGame(p))
						join(p, s , true);
				}
			}
			this.updateColors();
			return true;
		}
		return false;
	}

	public boolean addPlayers(ArrayList<Player> players) {
		if (this.getAllPlayers().size() + players.size() <= settings.max_players) {
			for (Player p : players) {
				if (!main.getArenaManager().isInGame(p))
					join(p);
			}
			return true;
		}
		return false;
	}

	public boolean addPlayer(Player p) {
		if (settings.allow_join && this.getAllPlayers().size() < settings.max_players
				&& !this.getAllPlayers().contains(p) && !main.getArenaManager().isInGame(p)
				&& state == ArenaState.LOBBY) {
			join(p);
			return true;
		} else if (!main.getArenaManager().isInGame(p) && state == ArenaState.INGAME && settings.allow_spectate) {
			addSpec(p);
			return true;
		}
		return false;
	}

	private void addSpec(Player p) {
		specs.add(p);
		PlayerTools.resetPlayer(p);
		updateTab();
		setupSpec(p);
	}

	void switchToSpec(Player p) {
		if (!settings.allow_spectate) {
			this.leave(p , true);
			return;
		}
		if (players.contains(p)) {
			players.remove(p);
		}
		ArenaTeam team = teamManager.findPlayer(p);
		specs.add(p);
		PlayerTools.resetPlayer(p);
		updateTab();
		setupSpec(p);
		if (team != null) {
			team.removePlayer(p);
			if (state == ArenaState.INGAME) {
				teamManager.checkWin();
			}
		}
	}

	private void setupSpec(Player p) {
		if (this.getAllPlayers().size() == 0) {
			try {
				p.teleport(this.cluster.getClusterMeta().getTeamSpawn(this.teamManager.getTeams().get(0).team.name).toLocation(relative));
			} catch (UnknownTeamException e) {
				this.leave(p);
			}
		}
		else {
			p.teleport(this.getAllPlayers().get(0).getLocation());
		}
		this.scoreBoard.create(p);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1));
	}

	private void join(Player p) {
		join (p , false);
	}
	
	private void join(Player p , boolean silent) {
		if (this.state == ArenaState.LOBBY) {
			players.add(p);
			arenaLobby.updatePlayer(p);
			if (!silent)this.sendMessage(
					main.getMessageFetcher().getMessage("game.join", true).replaceAll("%player%", p.getName()));
		}
		updateTab();
	}

	private void join(Player p, String team , boolean silent) {
		if (this.state == ArenaState.LOBBY) {
			join(p , silent);
			this.teamManager.getTeam(team).addPlayer(p);
		}
	}

	public void leave (Player p , boolean silent) {
		if (state == ArenaState.LOBBY) {
			if (settings.lobby_leave == LeaveType.ABORT) {
				if (!silent) this.sendMessage(main.getMessageFetcher().getMessage("game.abort", true).replaceAll("%player%", p.getName()));
				stop();
				return;
			}
			if (!silent) this.sendMessage(main.getMessageFetcher().getMessage("game.leave", true).replaceAll("%player%", p.getName()));
		}
		else if(state == ArenaState.INGAME && !specs.contains(p)){
			ArenaTeam team = teamManager.findPlayer(p);
			if (!silent) this.sendMessage(main.getMessageFetcher().getMessage("game.leave_ingame", true).replaceAll("%player%", p.getName())
					.replaceAll("%color%", team.team.Color.toChatColor().toString())
					.replaceAll("%display%", team.team.display));
		}
		if (players.contains(p)) {
			players.remove(p);
		}
		if (specs.contains(p)) {
			p.setAllowFlight(false);
			p.setFlying(false);
			specs.remove(p);
		}
		ArenaTeam team = teamManager.findPlayer(p);
		if (team != null) {
			team.removePlayer(p);
			if(state == ArenaState.INGAME){
				teamManager.checkWin();
			}
		}
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		PlayerTools.resetPlayer(p);
		main.getLobbyManager().toLobby(p);
		updateTab();
		for (Player member : main.getGroupManager().getGroup(p).getMembers()) {
			if (this.getAll().contains(member) && state == ArenaState.LOBBY) {
				this.leave(member);
			}
		}
	}

	public void leave(Player p) {
		leave(p, false);
	}

	public void removeAll() {
		for (Player p : getAll()) {
			if (teamManager.findPlayer(p) != null) {
				teamManager.findPlayer(p).removePlayer(p);
			}
			if (players.contains(p)) {
				players.remove(p);
			}
			if (specs.contains(p)) {
				specs.remove(p);
			}
			PlayerTools.resetPlayer(p);
			p.teleport(main.getLobbyManager().getLobbyLocation());
			resetComplete(p);
		}
	}

	public void resetComplete(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
	}

	// Message management

	public void sendMessage(String message) {
		for (Player p : this.getAll()) {
			p.sendMessage(message);
		}
	}

	public void sendColoredMessage(String message) {
		for (Player p : this.getAll()) {
			p.sendMessage(main.c(message));
		}
	}

	@SuppressWarnings("deprecation")
	public void sendTitle(String str, String str2) {
		for (Player p : this.getAll()) {
			p.sendTitle(main.c(str), main.c(str2));
		}
	}

	// Visibility management

	public void updateTab() {
		updateColors();
		updateVisibility();
	}

	public void updateColors() {
		for (Player p : this.getAll()) {
			ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
		}
		for (Player p : this.getAllPlayers()) {
			ArenaTeam team = this.teamManager.findPlayer(p);
			if (team != null) {
				String prefix = main.c(main.getSettingsManager().getFile().getString("game.tab.format_prefix"))
						.replaceAll("%color%", team.team.Color.toChatColor().toString())
						.replaceAll("%display%", team.team.display);
				String suffix = main.c(main.getSettingsManager().getFile().getString("game.tab.format_suffix"))
						.replaceAll("%color%", team.team.Color.toChatColor().toString())
						.replaceAll("%display%", team.team.display);
				ColorTabAPI.setTabStyle(p, prefix, suffix, 100 + this.teamManager.getTeams().indexOf(team),Bukkit.getOnlinePlayers());
			}
			else if (state == ArenaState.LOBBY){
				String prefix = main.c(main.getSettingsManager().getFile().getString("game.tab.unknown_format_prefix"));
				String suffix = main.c(main.getSettingsManager().getFile().getString("game.tab.unknown_format_suffix"));
				ColorTabAPI.setTabStyle(p, prefix, suffix, 999, Bukkit.getOnlinePlayers());
			}
		}
		for (Player p : specs) {
			ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
			String prefix = main.c(main.getSettingsManager().getFile().getString("game.tab.spectator_format_prefix"));
			String suffix = main.c(main.getSettingsManager().getFile().getString("game.tab.spectator_format_suffix"));
			ColorTabAPI.setTabStyle(p, prefix, suffix, 999, Bukkit.getOnlinePlayers());
		}
	}

	public void updateVisibility() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player player : getAll()) {
				p.hidePlayer(player);
				player.hidePlayer(p);
			}
		}
		for (Player p : getAllPlayers()) {
			for (Player player : getAllPlayers()) {
				p.showPlayer(player);
			}
		}
		for (Player p : specs) {
			for (Player player : getAll()) {
				p.showPlayer(player);
				player.hidePlayer(p);
			}
		}
		for (Player p : specs) {
			for (Player player : specs) {
				p.showPlayer(player);
				player.hidePlayer(p);
			}
		}
	}

	// Gamestart methods

	public void callLobbyEnd() {
		state = ArenaState.INGAME;
		for (ItemSpawner spawner : itemSpawners) {
			spawner.startGenerating(relative);
		}
		// Auto Team add
		for (Player p : new ArrayList<Player>(players)) {
			PlayerTools.resetPlayer(p);
			if (this.teamManager.findPlayer(p) == null) {
				autoTeam(p);
			}
			respawnPlayer(p);
			players.remove(p);
		}
		for (ArenaTeam team : new ArrayList<ArenaTeam>(teamManager.teams)) {
			if (team.players.size() == 0) {
				teamManager.teams.remove(team);
			}
		}
		this.updateTab();
		scoreBoard.create();
		// Start loop
		loop();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {

			@Override
			public void run() {
				// Create Villagers
				try {
					for (FineRelativeLocation loc : cluster.getClusterMeta().getShops()) {
						Merchant.spawn(loc.toLocation(relative),
								main.c(main.getSettingsManager().getFile().getString("shop.display")),
								EntityType.valueOf(
										main.getSettingsManager().getFile().getString("shop.type").toUpperCase()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, 10);
	}

	public void autoTeam(Player p) {
		for (ArenaTeam t : this.teamManager.getTeams()) {
			if (t.addPlayer(p)) {
				return;
			}
		}
	}

	// Game methods

	public void endGame() {
		endGame(null);
	}

	int ending_counter;
	int ending_id;

	public void endGame(ArenaTeam winner) {
		state = ArenaState.ENDING;
		scoreBoard.remove();
		for (Player p : getAll()) {
			for (Player player : getAll()) {
				p.showPlayer(player);
				player.showPlayer(p);
			}
		}
		Bukkit.getScheduler().cancelTask(this.scheduler);
		for (Player p : getAll()) {
			PlayerTools.resetPlayer(p);
			p.teleport(main.getLobbyManager().getGameLobbyLocation());
			p.setGameMode(GameMode.ADVENTURE);
			p.setFlying(false);
			p.setAllowFlight(false);
		}
		if (winner != null) {
			String title = main.c(main.getSettingsManager().getFile().getString("game.titles.win"));
			title = title.replaceAll("%color%", winner.team.Color.toChatColor().toString()).replaceAll("%display%",
					winner.team.display);
			String subtitle = main.c(main.getSettingsManager().getFile().getString("game.titles.win_sub"));
			subtitle = subtitle.replaceAll("%color%", winner.team.Color.toChatColor().toString())
					.replaceAll("%display%", winner.team.display);
			sendTitle(title, subtitle);
		}
		ending_counter = settings.ending_countdown;
		ending_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				if (ending_counter == 0) {
					stop();
					Bukkit.getScheduler().cancelTask(ending_id);
					return;
				}
				PlayerTools.setLevel(getAll(), ending_counter);
				ending_counter -= 1;
			}

		}, 0, 20);
	}

	public void stop() {
		for (Player p : getAll()) {
			ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
			p.setLevel(0);
			main.getLobbyManager().toLobby(p);
		}
		this.teamManager.teams = new ArrayList<ArenaTeam>();
		this.players = new ArrayList<Player>();
		this.specs = new ArrayList<Player>();
		for (ItemSpawner s : this.itemSpawners) {
			s.stop();
		}
		main.getArenaManager().checkOut(id);
		main.getLobbyManager().updateVisibility();
	}

	public void respawnPlayer(Player p) {
		ArenaTeam team = this.teamManager.findPlayer(p);
		if (team != null) {
			try {
				Location loc = cluster.getClusterMeta().getTeamSpawn(team.team.name).toLocation(this.relative);
				p.teleport(loc);
				p.setGameMode(GameMode.SURVIVAL);
				PlayerTools.resetPlayer(p);
				Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
					@Override
					public void run() {
						p.setVelocity(new Vector(0,0,0));
						p.setHealth(p.getMaxHealth());
					}
				}, 1);
				spawnProtection(p);
			} catch (UnknownTeamException e) {

			}
		}
	}
	
	public void spawnProtection (Player p) {
		for (Player player : getAllPlayers()) {
			p.hidePlayer(player);
			player.hidePlayer(p);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				if (state == ArenaState.INGAME)
					for (Player player : getAllPlayers()) {
						p.showPlayer(player);
						player.showPlayer(p);
					}
			}
			
		}.runTaskLater(main, settings.spawn_protection * 20);
	}

	public void BedDestroyed(ArenaTeam team, Player p) {
		team.bed = false;
		String msg = main.getMessageFetcher().getMessage("game.bed.destroy_chat", true);
		ArenaTeam pteam = teamManager.findPlayer(p);
		msg = msg.replaceAll("%color%", pteam.team.Color.toChatColor().toString());
		msg = msg.replaceAll("%display%", pteam.team.display);
		msg = msg.replaceAll("%player%", p.getName());
		msg = msg.replaceAll("%destroyed_color%", team.team.Color.toChatColor().toString());
		msg = msg.replaceAll("%destroyed_display%", team.team.display);
		for (Player member : team.players) {
			SoundDebugger.playSound(member, "ENDERDRAGON_GROWL" ,"ENTITY_ENDERDRAGON_GROWL");
		}
		team.sendTitle(main.c(main.getSettingsManager().getFile().getString("game.titles.bed")), main.c(main.getSettingsManager().getFile().getString("game.titles.bed_sub")));
		sendMessage(msg);
	}

	int scheduler;
	int gametimer;

	public void loop() {
		gametimer = settings.game_end;
		scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {

				if (gametimer == 0) {
					endGame();
				}

				gametimer -= 20;
				scoreBoard.update();

			}

		}, 20, 20);
	}

}
