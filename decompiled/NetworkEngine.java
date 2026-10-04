package com.corrodinggames.rts.gameFramework.j;

public final class NetworkEngine {
	public static final boolean a = false;
	public static boolean b = true;
	public static boolean c = false;
	public NetworkCallbacks networkCallbacks = new NetworkCallbacks();
	public int gameVersion;
	ArrayList f;
	public boolean extraDebug;
	public int maxMessagesPerMinute = 25;
	public boolean i;
	public float j;
	public float k;
	public boolean saveDesyncSaves = false;
	public int networkPort;
	public String serverPassword;
	public boolean modsEnabled;
	public boolean sandboxMode;
	public boolean serverVisible;
	public static boolean r = true;
	public boolean allowJoinInProgress;
	public int t = 5005;
	public String currentGameMapPath;
	public boolean chatOnlyServer = false;
	public long nextUnitId = 1L;
	public boolean customUnitsEnabled = false;
	public String playerName;
	private boolean bG;
	public Team localTeam;
	public boolean A;
	private boolean clientReadyStateSent = false;
	public volatile boolean networked = false;
	public boolean isServer;
	public boolean D;
	public String E;
	public boolean singlePlayerServer = false;
	public boolean G;
	public boolean proxyController;
	public int I = 0;
	private volatile float currentStepRate = 1.0F;
	public volatile float resyncStepRate = 1.0F;
	public Float stepRateOverride;
	public String queryString;
	public ArrayList banList = new ArrayList();
	public boolean quickResyncPending;
	public int blockingFrameCount;
	public int blockingLagFrameCount;
	public int blockingFrameInterval;
	public int blockingFrameGrace;
	public String serverUUID;
	public int integrityChallenge = -1;
	public int extraChallenge = -1;
	public int V = -1;
	public int extraChallengeSeed = CommonUtils.randomIntBetween(1, 9000000);
	public int nextBlockingFrame = 0;
	public boolean waitingForServerStep;
	public float gameStartCountdown;
	boolean allPlayersReady;
	public float startWaitElapsed;
	public float startWaitReminderTimer;
	public boolean ad;
	public float ae;
	public boolean af;
	public boolean waitingForResync;
	public int lastChecksumFrame = -1;
	public int checksumIntervalSeconds = 300;
	public boolean pauseOnDesync;
	public boolean pausedDueToDesync;
	public boolean gamePaused;
	public GameChecksum checksum = new GameChecksum();
	public boolean checksumSent;
	public boolean showDesyncErrors = true;
	public int desyncCount;
	public int checksumMatchCount;
	public int resyncCount;
	public static boolean disableDesyncFixing;
	float pingCheckTimer = 0.0F;
	long playerListDirtySince;
	public boolean receivedServerInfo;
	public int teamUnitCap = 5;
	public int teamUnitCapMax = 5;
	public GameSetup gameSetup = new GameSetup();
	public String mapName = null;
	public InputNetStream savedGameStream;
	public InputNetStream customMapStream;
	public ChatLog chatLog = new ChatLog();
	Thread tcpListenThread;
	NewConnectionRunnable tcpListenRunnable;
	Thread udpListenThread;
	NewConnectionRunnable udpListenRunnable;
	Timer aH;
	PingerTask pingerTask;
	Thread aJ;
	NetworkBroadcastRunnable broadcastRunnable;
	Connection localConnection;
	public ConcurrentLinkedQueue connections = new ConcurrentLinkedQueue();
	ConcurrentLinkedQueue incomingPacketQueue = new ConcurrentLinkedQueue();
	boolean aO;
	volatile int nextConnectionId = 1;
	Object connectionIdLock = new Object();
	String sessionSecret;
	String aS;
	public String portCheckStatusMessage;
	public Boolean portCheckSuccess;
	public Boolean portCheckInProgress;
	public boolean gameStarted;
	public boolean checkedTeamBalance = false;
	boolean returnToBattleroomPending = false;
	boolean returnTimerActive = false;
	public float returnToBattleroomCountdown;
	public boolean onePlayerPerTeam;
	public boolean startGameFailed;
	public boolean bd;
	public boolean serverEndedGame;
	public boolean bf;
	public String bg;
	public String bh = null;
	public ConcurrentLinkedQueue discoveredServers = new ConcurrentLinkedQueue();
	public Player spectatorPlayer;
	public Player adminPlayer;
	public final Object bl = new Object();
	public boolean disconnectQueued = false;
	float desyncResyncTimer;
	float desyncEscalationTimer;
	int resyncCycleCount;
	int lastResyncFrame;
	boolean quickResyncCommandSent = false;
	public long lastUpdateTime;
	public long lastProgressNotifyTime;
	boolean lastShouldPauseState = false;
	public Socket lastConnectedSocket = null;
	public String currentServerId = null;
	public boolean reconnectDialogShown;
	boolean machineKeyChecked = false;
	boolean registerConnectionSent = false;
	static ArrayList localIpAddressCache;
	boolean chatNotificationActive = false;
	final Object bC = new Object();
	Timer bD;
	public static PasswordPrompt passwordPrompt = new PasswordPrompt();
	ConnectSocketToServerRunnable joinServerRunnable;

	public strictfp BanEntry getBanEntry(Connection c) {
		String var2 = cx.getHostAddress();
		long var3 = System.currentTimeMillis();
		if (var2 == null) {
			cx.logWarning("Is banned: No target");
			return null;
		} else {
			synchronized (this.banList) {
				for (BanEntry var7 : this.banList) {
					if (var2.equals(var7.a) && var7.expiryTime > var3) {
						return var7;
					}
				}

				return null;
			}
		}
	}

	public strictfp boolean banConnection(Connection c, String string, int integer) {
		if (cx == null) {
			GameEngine.logWarning("Ban failed: No connection");
			return false;
		} else {
			String var4 = cx.getHostAddress();
			if (var4 == null) {
				cx.logWarning("Ban failed: No target");
				return false;
			} else {
				BanEntry var5 = new BanEntry();
				var5.a = cx.getHostAddress();
				var5.expiryTime = System.currentTimeMillis() + integer * 1000;
				var5.reason = string;
				synchronized (this.banList) {
					this.removeExpiredBans();
					this.banList.add(var5);
				}

				cx.log("Banned " + var4 + " for " + integer + "s");
				return true;
			}
		}
	}

	public strictfp void clearBans() {
		synchronized (this.banList) {
			this.banList.clear();
		}
	}

	public strictfp void removeExpiredBans() {
		synchronized (this.banList) {
			int var2 = 0;
			long var3 = System.currentTimeMillis();
			Iterator var5 = this.banList.iterator();

			while (var5.hasNext()) {
				var2++;
				BanEntry var6 = (BanEntry)var5.next();
				boolean var7 = false;
				if (var6.expiryTime < var3) {
					var7 = true;
				}

				if (var2 > 1000) {
					var7 = true;
				}

				if (var7) {
					var5.remove();
				}
			}
		}
	}

	public strictfp String sanitiseAndStorePlayerName(String string) {
		string = string.trim();
		string = string.replace(" ", "_");
		this.playerName = string;
		GameEngine var2 = GameEngine.getInstance();
		if (this.playerName != null && !this.playerName.equals(var2.settings.lastNetworkPlayerName)) {
			var2.settings.lastNetworkPlayerName = this.playerName;
			var2.settings.save();
		}

		return string;
	}

	public strictfp void setCurrentStepRate(float float1, String string) {
		if (float1 < 0.1) {
			reportDesync("setCurrentStepRate:" + float1 + " is too small, source:" + string, true);
		} else {
			this.currentStepRate = float1;
		}
	}

	public strictfp float getCurrentStepRate() {
		return this.currentStepRate;
	}

	public strictfp void resetChecksumPeriod() {
		GameEngine var1 = GameEngine.getInstance();
		this.lastChecksumFrame = var1.gameTimeSeconds;
		this.checksum.recompute();
		this.checksumSent = false;
	}

	public strictfp void writeGameSetup(OutputNetStream as) {
		GameEngine var2 = GameEngine.getInstance();
		asx.writeByte(0);
		this.gameSetup.write(asx);
		asx.writeInt(var2.unitsMaxDefault);
		asx.writeInt(var2.unitsMaxCap);
	}

	public strictfp void readGameSetup(InputNetStream k) {
		GameEngine var2 = GameEngine.getInstance();
		kx.readByte();
		this.gameSetup.read(kx);
		var2.unitsMaxDefault = kx.readInt();
		var2.unitsMaxCap = kx.readInt();
	}

	public strictfp GameSetup getChangeableSetup() {
		GameSetup var1;
		if (this.isServer) {
			var1 = this.gameSetup;
		} else if (this.proxyController) {
			var1 = this.gameSetup.c();
		} else {
			var1 = null;
			GameEngine.logWithTag("getChangeableSetup", "Clicked but not server or proxy controller");
		}

		return var1;
	}

	public strictfp void updateAIDifficulty() {
		if (this.singlePlayerServer) {
			GameEngine.getInstance().settings.aiDifficulty = this.gameSetup.aiDifficulty;
		}

		if (this.isServer || this.singlePlayerServer) {
			if (this.gameStarted) {
				GameEngine.logWarningWithStack("updateAIDifficulty with gameHasBeenStarted=true");
			} else {
				for (int var1 = 0; var1 < Team.maxTeamId; var1++) {
					Team var2 = Team.getTeam(var1);
					if (var2 != null) {
						this.updateTeamAIDifficulty(var2);
					}
				}
			}

			this.updateNamesOfAI();
		}
	}

	public strictfp void updateTeamAIDifficulty(Team n) {
		if (nx.isAI) {
			nx.debugLog("aiDifficultyOverride=" + nx.aiDifficultyOverride);
			if (nx.aiDifficultyOverride != null) {
				nx.aiDifficulty = nx.aiDifficultyOverride;
			} else {
				nx.aiDifficulty = this.gameSetup.aiDifficulty;
			}
		}
	}

	public strictfp boolean updateAITeamName(Team n) {
		boolean var2 = false;
		if (nx.isAI) {
			String var3 = "AI - " + this.b(nx.getEffectiveAIDifficulty());
			if (!var3.equals(nx.name)) {
				nx.name = var3;
				var2 = true;
			}
		}

		return var2;
	}

	public strictfp void applyChangedSetup(GameSetup ah) {
		if (this.isServer) {
			this.updateAIDifficulty();
			this.markPlayerListDirty();
			this.sendServerInfoToAll();
			MultiplayerBattleroomActivity.o();
		} else if (this.proxyController) {
			this.applyProxyControl(ahx);
		} else {
			GameEngine.log("applyChangedSetup but not server or proxy controller");
		}
	}

	private strictfp void applyProxyControl(GameSetup ah) {
		GameEngine var2 = GameEngine.getInstance();
		GameEngine.log("applyProxyControl");
		GameSetup var3 = this.gameSetup;
		if (!var3.mapName.equals(ahx.mapName)) {
			String var4 = MapSelectActivity.e(ahx.mapName);
			var4 = FileLoader.o(var4);
			var2.networkEngine.sendChatCommand("-map '" + var4 + "'");
		}

		if (var3.revealedMap != ahx.revealedMap) {
			String var6 = !ahx.revealedMap ? "true" : "false";
			var2.networkEngine.sendChatCommand("-revealedmap " + var6);
		}

		if (var3.fogMode != ahx.fogMode) {
			String var7 = var2.networkEngine.getFogModeCommandLine(ahx.fogMode);
			var2.networkEngine.sendChatCommand("-fog " + var7);
		}

		if (var3.startingCredits != ahx.startingCredits) {
			int var8 = var2.networkEngine.getStartingCreditsOptionValue(ahx.startingCredits);
			var2.networkEngine.sendChatCommand("-credits " + var8);
		}

		if (!CommonUtils.nearlyEqualExact(var3.incomeMultiplier, ahx.incomeMultiplier)) {
			var2.networkEngine.sendChatCommand("-income " + CommonUtils.formatDecimal(ahx.incomeMultiplier, 1));
		}

		if (var3.noNukes != ahx.noNukes) {
			String var9 = !ahx.noNukes ? "true" : "false";
			var2.networkEngine.sendChatCommand("-nukes " + var9);
		}

		if (var3.aiDifficulty != ahx.aiDifficulty) {
			var2.networkEngine.sendChatCommand("-ai " + ahx.aiDifficulty);
		}

		if (var3.startingUnits != ahx.startingUnits) {
			var2.networkEngine.sendChatCommand("-startingunits " + ahx.startingUnits);
		}

		if (var3.sharedControl != ahx.sharedControl) {
			String var10 = ahx.sharedControl ? "true" : "false";
			var2.networkEngine.sendChatCommand("-sharedControl " + var10);
		}
	}

	public strictfp String getFogModeName() {
		if (this.gameSetup.fogMode == 0) {
			return "No fog";
		} else if (this.gameSetup.fogMode == 1) {
			return "Basic fog";
		} else {
			return this.gameSetup.fogMode == 2 ? "Line of Sight" : "Unknown";
		}
	}

	public strictfp String getFogModeCommandLine(int integer) {
		if (integer == 0) {
			return "off";
		} else if (integer == 1) {
			return "basic";
		} else {
			return integer == 2 ? "los" : "Unknown";
		}
	}

	public strictfp String b(int integer) {
		return this.getAIDifficultyName(integer);
	}

	public strictfp String getAIDifficultyName(int integer) {
		if (integer == -2) {
			return "Very Easy";
		} else if (integer == -1) {
			return "Easy";
		} else if (integer == 0) {
			return "Medium";
		} else if (integer == 1) {
			return "Hard";
		} else if (integer == 2) {
			return "Very Hard";
		} else {
			return integer == 3 ? "Impossible" : "Unknown";
		}
	}

	public strictfp String getCurrentStartingUnitsName() {
		return this.getStartingUnitsName(this.gameSetup.startingUnits);
	}

	public strictfp ArrayList getStartingUnitOptions() {
		ArrayList var1 = new ArrayList();

		for (int var2 = 1; var2 <= 4; var2++) {
			var1.add(var2);
		}

		var1.addAll(CustomUnitMetadata.getExtraSlotIds());
		return var1;
	}

	public strictfp String getStartingUnitsName(int integer) {
		if (integer == 1) {
			return "Normal (1 builder)";
		} else if (integer == 2) {
			return "Small Army";
		} else if (integer == 3) {
			return "3 Engineers";
		} else if (integer == 4) {
			return "3 Engineers (No Command Center)";
		} else if (integer == 5) {
			return "Experimental Spider";
		} else if (integer == 9) {
			return "Custom";
		} else {
			CustomUnitMetadata var2 = CustomUnitMetadata.getExtraUnitBySlotId(integer);
			return var2 != null ? var2.getDisplayName() : "Unknown";
		}
	}

	public strictfp String getStartingCreditsDescription() {
		return this.gameSetup.startingCredits == 0 ? "Default ($" + this.getCurrentStartingCredits() + ")" : "$" + this.getCurrentStartingCredits();
	}

	public final strictfp int getCurrentStartingCredits() {
		return this.getStartingCreditsOptionValue(this.gameSetup.startingCredits);
	}

	public strictfp int getStartingCreditsOptionValue(int integer) {
		if (integer == 0) {
			return 4000;
		} else if (integer == 1) {
			return 0;
		} else if (integer == 2) {
			return 1000;
		} else if (integer == 3) {
			return 2000;
		} else if (integer == 4) {
			return 5000;
		} else if (integer == 5) {
			return 10000;
		} else if (integer == 6) {
			return 50000;
		} else if (integer == 7) {
			return 100000;
		} else {
			return integer == 8 ? 200000 : 999;
		}
	}

	public strictfp String getMapDisplayName() {
		return FileLoader.o(this.mapName);
	}

	public strictfp void initIntegrityCheck() {
		new FastArrayList();
		CommonUtils.clampToByte(256);
		Obfuscation.a(5.0F, 6.0F, 7.0F);
		Obfuscation2.a(5);
		this.bg = Obfuscation2.a();
		this.bf = true;
	}

	public strictfp boolean hasGameBeenStarted() {
		return this.gameStarted;
	}

	public strictfp boolean isGameStarting() {
		return this.networkCallbacks.e();
	}

	public strictfp synchronized void setPortCheckStatus(boolean boolean1, String string, Boolean boolean3) {
		this.portCheckInProgress = boolean1;
		this.portCheckStatusMessage = string;
		this.portCheckSuccess = boolean3;
		MultiplayerBattleroomActivity.o();
	}

	strictfp void addDiscoveredServer(DiscoveredServer g) {
		for (DiscoveredServer var3 : this.discoveredServers) {
			if (var3.isLan && var3.host.equals(gx.host) && var3.port == gx.port) {
				var3.o = this.currentTimeMillis();
			}
		}

		gx.o = this.currentTimeMillis();
		this.discoveredServers.add(gx);
		ServerListActivity.l();
	}

	public strictfp long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public strictfp NetworkEngine() {
		GameEngine var1 = GameEngine.getInstance();
		this.gameVersion = var1.getVersionCode(true);
		this.sessionSecret = CommonUtils.randomString(40);
		this.localConnection = new Connection(this, null);
		this.localConnection.connected = true;
		this.spectatorPlayer = new Player(-3, false);
		this.spectatorPlayer.v = "SPECTATOR";
		this.adminPlayer = new Player(-1, false);
		this.adminPlayer.v = "ADMIN";
	}

	public strictfp void resetNetworkState() {
		this.resetNetworkState(false);
	}

	public strictfp void resetNetworkStateKeepingTeams() {
		this.resetNetworkState(true);
	}

	public strictfp void resetGameNetworkState() {
		this.clientReadyStateSent = false;
		this.bG = false;
		this.localTeam = null;
		this.sandboxMode = false;
		this.lastUpdateTime = System.currentTimeMillis();
		this.nextBlockingFrame = 0;
		this.I = 0;
		this.nextUnitId = 1L;
		this.setCurrentStepRate(1.0F, "new");
		this.gameStartCountdown = 10.0F;
		this.quickResyncPending = false;
		this.blockingFrameInterval = 10;
		this.blockingFrameGrace = 0;
		this.waitingForServerStep = false;
		this.allPlayersReady = false;
		this.gamePaused = false;
		this.pausedDueToDesync = false;
		this.startWaitElapsed = 0.0F;
		this.startWaitReminderTimer = 0.0F;
		this.ad = false;
		this.af = false;
		this.gameStarted = false;
		this.returnToBattleroomPending = false;
		this.returnTimerActive = false;
		this.returnToBattleroomCountdown = 0.0F;
		this.checkedTeamBalance = false;
		this.onePlayerPerTeam = false;
		this.startGameFailed = false;
		this.bd = false;
		this.serverEndedGame = false;
		this.waitingForResync = false;
		this.lastChecksumFrame = -1;
		this.checksum.total = 0L;
		this.quickResyncCommandSent = false;
		this.checksum.reset();
		this.checksumSent = false;
		this.showDesyncErrors = true;
		this.desyncCount = 0;
		this.checksumMatchCount = 0;
		this.resyncCount = 0;
		this.pingCheckTimer = 0.0F;
		this.desyncResyncTimer = 0.0F;
		this.desyncEscalationTimer = 0.0F;
		this.resyncCycleCount = 0;
		this.lastResyncFrame = -1000;
		Obfuscation.i = 55;
		Obfuscation.j = 66;
	}

	public strictfp void resetNetworkState(boolean boolean1) {
		this.networked = false;
		this.isServer = false;
		this.f = null;
		this.singlePlayerServer = false;
		this.D = false;
		this.E = null;
		this.customUnitsEnabled = false;
		this.proxyController = false;
		this.G = false;
		this.receivedServerInfo = false;
		this.A = false;
		this.resetGameNetworkState();
		this.serverUUID = null;
		this.networkPort = 0;
		this.i = false;
		this.j = 0.0F;
		this.k = 0.0F;
		this.registerConnectionSent = false;
		this.customMapStream = null;
		this.teamUnitCapMax = GameEngine.getInstance().settings.teamUnitCapHostedGame;
		if (this.teamUnitCapMax < 1) {
			this.teamUnitCapMax = 1;
		}

		this.teamUnitCap = this.teamUnitCapMax;
		this.gameSetup.startingUnits = 1;
		this.gameSetup.incomeMultiplier = 1.0F;
		this.gameSetup.noNukes = false;
		this.gameSetup.j = false;
		this.gameSetup.sharedControl = false;
		this.gameSetup.startingCredits = 0;
		this.gameSetup.teamsLocked = false;
		this.gameSetup.enforceTwoTeams = false;
		this.gameSetup.allowSpectators = true;
		this.gameSetup.lockedRoom = false;
		this.gameSetup.randomSeed = 0;
		this.clearBans();
		this.chatLog.clear();
		GameEngine.getInstance().interfaceEngine.clearMessageLogs();
		if ("<CHAT ONLY>".equals(this.gameSetup.mapName)) {
			GameEngine.log("Chat only map selection - restarting");
			this.gameSetup.resetToDefaults();
		}

		if (!boolean1) {
			Team.clearAllTeams();
		}

		String var2 = CustomUnitMetadataLoader.b(this.modsEnabled);
	}

	public strictfp void t() {
	}

	public strictfp synchronized void disconnect(String string) {
		GameEngine var2 = GameEngine.getInstance();
		GameEngine.log("Disconnect: " + string);
		if (this.isServer) {
			this.stopMasterServerTimer();
			MasterServerConnector.startRemoveServerRequest();
			if (this.tcpListenRunnable != null) {
				this.tcpListenRunnable.stopListening();

				try {
					if (this.tcpListenThread != null) {
						this.tcpListenThread.join();
					}
				} catch (InterruptedException var9) {
				}

				this.tcpListenRunnable = null;
				this.tcpListenThread = null;
			}

			if (this.udpListenRunnable != null) {
				this.udpListenRunnable.stopListening();

				try {
					if (this.udpListenThread != null) {
						this.udpListenThread.join();
					}
				} catch (InterruptedException var8) {
				}

				this.udpListenRunnable = null;
				this.udpListenThread = null;
			}

			if (this.aH != null) {
				this.aH.cancel();
				this.aH = null;
				this.pingerTask = null;
			}

			if (this.broadcastRunnable != null) {
				this.broadcastRunnable.b();
				this.broadcastRunnable = null;
				this.aJ = null;
			}
		}

		this.disconnectAllConnections(string);
		SteamEngineDisabled.a().j();
		synchronized (this.bl) {
			this.networked = false;
			this.isServer = false;
			this.singlePlayerServer = false;
			this.f = null;

			try {
				this.wait(50L);
			} catch (InterruptedException var6) {
				var6.printStackTrace();
			}

			this.gameStarted = false;
			var2.replayEngine.stop();
			var2.updateGame();
			this.updateMultiplayerNotifications();
			this.disconnectQueued = false;
			this.bl.notifyAll();
		}
	}

	public strictfp void waitForNetworkGameToEnd() {
		synchronized (this.bl) {
			if (this.networked) {
				this.disconnectQueued = true;

				try {
					this.bl.wait();
				} catch (InterruptedException var4) {
					var4.printStackTrace();
				}
			}
		}
	}

	public strictfp void removeConnection(Connection c) {
		this.connections.remove(cx);
	}

	private strictfp synchronized void removeClosedConnections() {
		Iterator var1 = this.connections.iterator();

		while (var1.hasNext()) {
			Connection var2 = (Connection)var1.next();
			if (var2.closed) {
				var1.remove();
			}
		}
	}

	strictfp void saveDesyncSave(byte[] arr, Connection c) {
		if (!GameEngine.isDedicatedServer()) {
			Log.d("RustedWarfare", "Ignoring incoming resync tagged as debug only");
		} else {
			if (cx.desyncSaveWritten) {
				Log.d("RustedWarfare", "Ignoring desync client save, as past desync was already saved");
				return;
			}

			cx.desyncSaveWritten = true;
			Log.d("RustedWarfare", "Saving client save for debugging");
			String var3 = "desyncs/";
			String var4 = "desync_" + CommonUtils.formatDate("d MMM yyyy HH.mm.ss") + "_" + cx.id;
			File var5 = new File(var3 + var4);
			var5.getParentFile().mkdirs();

			try {
				FileOutputStream var6 = new FileOutputStream(var5);
				var6.write(arr);
				var6.close();
			} catch (IOException var8) {
				var8.printStackTrace();
			}
		}
	}

	public strictfp void requestQuickResync() {
		if (!this.quickResyncCommandSent) {
			GameEngine.log("Adding quick resync command");
			GameEngine var1 = GameEngine.getInstance();
			CommandControllerCommand var2 = var1.commandController.createCommand();
			var2.team = Team.grayTeam;
			var2.hasSystemAction = true;
			var2.systemActionId = 200;
			var1.networkEngine.queueCommand(var2);
			this.quickResyncCommandSent = true;
		}
	}

	public strictfp void performQuickResync() {
		GameEngine var1 = GameEngine.getInstance();
		OutputNetStream var2 = new OutputNetStream();

		try {
			var1.gameSaver.saveGame(var2);
		} catch (IOException var10) {
			throw new RuntimeException(var10);
		}

		try {
			var2.flushAll();
		} catch (IOException var9) {
			var9.printStackTrace();
		}

		byte[] var3 = var2.toByteArray();
		var2.close();
		if (this.isServer) {
			for (Connection var5 : this.connections) {
				if (var5.resyncRequested) {
					var5.resyncRequested = false;
					var5.desyncDetected = false;
					this.sendResyncSaveToConnection(var5, var3, this.saveDesyncSaves, false);
				}
			}
		}

		GameEngine.log("Loading quick resync save data (bytes:" + var3.length + ")");
		InputNetStream var11 = new InputNetStream(var3);
		var1.setLoadingMessage("Game resync (quick)...", true);
		int var12 = var1.gameTimeSeconds;
		int var6 = var1.gameTimeMillis;
		var1.gameSaver.loadGameFromStream(var11, true, true, true);
		var1.gameTimeSeconds = var12;
		var1.gameTimeMillis = var6;
		this.nextBlockingFrame = var1.gameTimeSeconds + 1;
		this.waitingForResync = false;
		this.lastChecksumFrame = this.nextBlockingFrame + 1;
		this.checksum.total = 0L;

		for (Connection var8 : this.connections) {
			var8.desyncDetected = false;
		}

		this.quickResyncCommandSent = false;
		this.resyncCount++;
		this.desyncResyncTimer = 0.0F;
		this.desyncEscalationTimer = 0.0F;
		if (this.resyncCycleCount < 1) {
			this.resyncCycleCount++;
		}

		this.lastResyncFrame = var1.gameTimeSeconds;
	}

	public strictfp synchronized void assertAllClientsSynced() {
		for (Connection var2 : this.connections) {
			if (var2.resyncRequested) {
				throw new RuntimeException("Player: " + var2.getPlayerName() + " has complete desync");
			}

			if (var2.desyncDetected) {
				throw new RuntimeException("Player: " + var2.getPlayerName() + " has minor desync");
			}

			if (var2.resyncCount == 0) {
				throw new RuntimeException("Player: " + var2.getPlayerName() + " has no sync matches");
			}
		}
	}

	private strictfp synchronized void updateDesyncTimers(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		boolean var3 = false;
		boolean var4 = false;
		boolean var5 = false;
		this.desyncResyncTimer += float1;

		for (Connection var7 : this.connections) {
			if (var7.resyncRequested) {
				var3 = true;
			}

			if (var7.desyncDetected) {
				if (this.extraDebug) {
					GameEngine.log("desync_count:" + var7.desyncCount + " lastResyncTimer:" + this.desyncResyncTimer);
				}

				if (var7.desyncCount < 4 || this.desyncResyncTimer > 3600.0F) {
					var5 = true;
				}
			}
		}

		if (var5) {
			this.desyncEscalationTimer += float1;
			if (c && this.desyncEscalationTimer > 5.0F) {
				var4 = true;
			}

			if (this.resyncCycleCount == 0) {
				if (this.desyncEscalationTimer > 60.0F) {
					var4 = true;
				}
			} else if (this.resyncCycleCount == 1) {
				if (this.desyncEscalationTimer > 420.0F) {
					var4 = true;
				}
			} else if (this.resyncCycleCount == 2) {
				if (this.desyncEscalationTimer > 3600.0F) {
					var4 = true;
				}
			} else if (this.resyncCycleCount == 3 && this.desyncEscalationTimer > 14400.0F) {
				var4 = true;
			}
		}

		if (disableDesyncFixing && var4) {
			GameEngine.log("disableDesyncFixing==true, running quick resync instead");
			var4 = false;
			var3 = true;
		}

		if (!var4 && var3) {
			if (b) {
				this.requestQuickResync();
			} else {
				var4 = true;
			}
		}

		if (var4) {
			String var9 = "";

			for (Connection var8 : this.connections) {
				if (var8.resyncRequested || var8.desyncDetected) {
					if (!var9.equals("")) {
						var9 = var9 + ", ";
					}

					var9 = var9 + var8.getPlayerName();
				}
			}

			this.sendSystemMessage("Resyncing game for " + var9 + "...");
			this.prepareClientsForResync();
			this.sendResyncSave(this.saveDesyncSaves, false, true);
		}
	}

	private strictfp void prepareClientsForResync() {
		GameEngine var1 = GameEngine.getInstance();
		this.desyncResyncTimer = 0.0F;
		this.desyncEscalationTimer = 0.0F;
		this.resyncCycleCount++;
		this.lastResyncFrame = var1.gameTimeSeconds;

		for (Connection var3 : this.connections) {
			var3.resyncRequested = false;
			var3.desyncDetected = false;
			var3.resyncCount = 0;
		}
	}

	public strictfp void kickAllConnections(String string) {
		this.disconnectAllConnections(string);
	}

	private strictfp void disconnectAllConnections(String string) {
		for (Connection var3 : this.connections) {
			var3.sendKickWithReason(string);
		}

		this.connections.clear();
		this.incomingPacketQueue.clear();
		this.nextConnectionId = 1;
		this.aO = false;
	}

	public strictfp long getNextUnitId() {
		boolean var1 = false;
		if (var1) {
			GameEngine.log("New id set:" + this.nextUnitId + 1);
			GameEngine.logCurrentStackTrace();
		}

		long var2 = this.nextUnitId++;
		if (var2 == 0L) {
			GameEngine.log("getNextUnitId: id==0");
			GameEngine.logCurrentStackTrace();
		}

		return var2;
	}

	public strictfp long peekNextUnitId() {
		return this.nextUnitId;
	}

	public strictfp void setNextUnitId(long long1) {
		this.nextUnitId = long1;
	}

	public strictfp boolean areAllClientsReady(boolean boolean1, int integer) {
		for (Connection var4 : this.connections) {
			if (var4.connected && var4.isOpen() && !var4.isRelay && !var4.clientReady) {
				if (boolean1) {
					this.sendSystemMessage("Still waiting on: " + var4.getPlayerName());
				}

				return false;
			}
		}

		return true;
	}

	public strictfp void clearClientReadyFlags() {
		for (Connection var2 : this.connections) {
			var2.C = false;
			var2.clientReady = false;
		}
	}

	public strictfp int getPlayerConnectionCount() {
		int var1 = 0;

		for (Connection var3 : this.connections) {
			if (var3.connected && var3.isOpen() && !var3.isRelay) {
				var1++;
			}
		}

		return var1;
	}

	public strictfp int getUniquePlayerCount() {
		ArrayList var1 = new ArrayList();
		int var2 = 0;

		for (Connection var4 : this.connections) {
			if (var4.connected && var4.isOpen() && !var4.isRelay) {
				Player var5 = var4.player;
				if (var5 != null) {
					if (var1.contains(var5)) {
						continue;
					}

					var1.add(var5);
				}

				var2++;
			}
		}

		return var2;
	}

	public strictfp int getConnectionCount() {
		int var1 = 0;

		for (Connection var3 : this.connections) {
			if (var3.connected && !var3.isRelay) {
				var1++;
			}
		}

		return var1;
	}

	public strictfp int getPlayerCountIncludingLocal() {
		int var1 = 0;
		var1 += this.getUniquePlayerCount();
		if (!GameEngine.isDedicatedServer()) {
			var1++;
		}

		return var1;
	}

	public strictfp void logNetwork(String string) {
		Log.b("RustedWarfare", "network:" + string);
	}

	public static strictfp void logNetworkDebug(String string) {
		GameEngine.log("network debug: " + string);
	}

	public strictfp void reportProblem(String string) {
		Log.d("RustedWarfare", "reportProblem:" + string);
		if (this.gameStarted) {
			this.receiveChatMessage(null, -1, null, string);
		} else {
			this.receiveChatMessage(null, -1, null, string);
		}
	}

	public static strictfp void reportDesync(String string) {
		reportDesync(string, false);
	}

	public static strictfp void reportDesyncWithPopup(String string) {
		reportDesync(string, true);
	}

	public static strictfp void reportDesync(String string, boolean boolean2) {
		GameEngine var2 = GameEngine.getInstance();
		NetworkEngine var3 = var2.networkEngine;
		String var4 = "desync:" + string;
		GameEngine.logWarning(var4);
		GameEngine.logCurrentStackTrace();
		var3.desyncCount++;
		if (var3.showDesyncErrors) {
			if (var3.desyncCount > 2 || disableDesyncFixing) {
				boolean2 = true;
			}

			String var5;
			if (var3.desyncCount > 10) {
				var5 = "<suppressing desync errors>";
				var3.showDesyncErrors = false;
				boolean2 = true;
			} else {
				var5 = var4;
			}

			if (boolean2) {
				var5 = "-i " + var5;
			}

			var3.sendChatMessage(var5);
		}
	}

	public static strictfp void showGameMessage(String string1, String string2) {
		GameEngine var2 = GameEngine.getInstance();
		var2.replayEngine.recordChatMessage(-1, string1, string2, var2.gameTimeSeconds);
		if (var2.interfaceEngine != null && var2.interfaceEngine.messageInterface != null) {
			var2.interfaceEngine.messageInterface.addMessage(string1, string2);
		} else {
			GameEngine.logWarningWithStack("interfaceEngine/messageInterface==null");
		}
	}

	public strictfp void F() {
	}

	public strictfp void queueCommand(CommandControllerCommand e) {
		GameEngine var2 = GameEngine.getInstance();
		ex.scheduledGameSecond = this.nextBlockingFrame;
		ex.deferUnitsToIds();
		var2.commandController.localPendingCommands.add(ex);
	}

	public strictfp void checkConnectionPings() {
		for (Connection var2 : this.connections) {
			if (var2.connected && var2.getPing() != -2 && var2.getPing() <= 500 && var2.getPing() < 0) {
			}
		}
	}

	public strictfp void showPlayerListPopup() {
		GameEngine var1 = GameEngine.getInstance();
		String var2 = "";

		for (Team var5 : Team.a(true)) {
			if (var5 != null) {
				String var6 = "unnamed";
				if (var5.name != null) {
					var6 = var5.name;
				}

				String var7 = " " + var5.getPingText();
				String var8 = "•";
				var2 = var2 + var8 + var5.getColorName().toLowerCase() + " [Team " + var5.getTeamLetter() + "] - " + var6 + var7 + "\n";
			}
		}

		GameEngine.log("showPlayerListPopup(): Showing playlist messagebox.");
		var1.showMessageBox("Players", var2);
	}

	public strictfp void update(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		this.pingCheckTimer += float1;
		if (this.returnTimerActive) {
			if (this.returnToBattleroomCountdown > 0.0F) {
				this.returnToBattleroomCountdown -= float1 / 60.0F;
				GameEngine.getInstance().interfaceEngine.showScreenMessage("Returning to battleroom in " + (int)this.returnToBattleroomCountdown + "...", 3500);
			} else {
				GameEngine.log("Sending returnToBattleroomEvent...");
				this.returnTimerActive = false;
				this.sendReturnToBattleroom(null);
			}
		}

		if (this.returnToBattleroomPending) {
			this.returnToBattleroom();
		}

		if (this.pingCheckTimer > 60.0F) {
			this.checkConnectionPings();
			this.pingCheckTimer = 0.0F;
		}

		if (this.gameStarted && !this.checkedTeamBalance) {
			this.checkedTeamBalance = true;
			ArrayList var3 = Team.getTeamIds();
			int var4 = 0;
			int var5 = 0;

			for (Integer var7 : var3) {
				int var8 = Team.countTeamsInAllyGroup(var7, false);
				if (var8 > var5) {
					var5 = var8;
				}

				var4++;
			}

			if (var4 > 2 && var5 <= 1) {
				this.onePlayerPerTeam = true;
			}
		}

		if (!this.isServer && !this.clientReadyStateSent) {
			this.sendClientReadyState();
			this.clientReadyStateSent = true;
		}

		if (this.isServer) {
			if (!this.allPlayersReady && this.gameStarted) {
				if (this.areAllClientsReady(false, 0)) {
					this.gameStartCountdown = CommonUtils.applyDeadzone(this.gameStartCountdown, float1);
					if (this.gameStartCountdown == 0.0F) {
						this.allPlayersReady = true;
						showGameMessage("", "<All players ready>");
						this.networkCallbacks.a();
					}
				} else {
					this.startWaitElapsed += float1;
					this.startWaitReminderTimer += float1;
					float var13 = 900.0F;
					if (this.startWaitElapsed > var13) {
						this.allPlayersReady = true;
						showGameMessage("", "Starting game without all players ready!");
					} else if (this.startWaitReminderTimer > 180.0F) {
						this.startWaitReminderTimer = 0.0F;
						this.areAllClientsReady(true, (int)((var13 - this.startWaitElapsed) / 60.0F));
					}
				}
			}

			if (this.allPlayersReady) {
				boolean var14 = false;
				if (this.pausedDueToDesync) {
					var14 = true;
				}

				if (this.gamePaused) {
					var14 = true;
				}

				if (var2.gameTimeSeconds >= this.nextBlockingFrame - this.blockingFrameGrace && !var14) {
					int var20 = this.nextBlockingFrame + this.blockingFrameInterval;
					this.blockingFrameCount++;
					boolean var24 = false;

					for (int var28 = 0; var28 < Team.maxTeamId; var28++) {
						Team var32 = Team.getTeam(var28);
						if (var32 != null && var32.stepsBehindCount != 0 && !var32.isTimedOut() && var32.stepsBehindCount < 40) {
							var24 = true;
						}
					}

					if (var2.getGameStep() != 0 && var2.getGameStep() < 40 && !GameEngine.isDedicatedServer()) {
						var24 = true;
					}

					if (var24) {
						this.blockingLagFrameCount++;
					}

					if (this.blockingFrameCount > 8) {
						float var29 = 1.0F;
						if (this.blockingLagFrameCount > 4) {
							var29 = 2.0F;
						}

						if (this.stepRateOverride != null) {
							var29 = this.stepRateOverride;
						}

						if (var29 != this.getCurrentStepRate()) {
							GameEngine.log("Changing step rate to " + var29);
							CommandControllerCommand var33 = var2.commandController.createCommand();
							var33.team = Team.grayTeam;
							var33.hasSystemAction = true;
							var33.changeStepRate = var29;
							this.queueCommand(var33);
						}

						this.blockingFrameCount = 0;
						this.blockingLagFrameCount = 0;
					}

					OutputNetStream var30 = new OutputNetStream();

					try {
						var30.writeInt(var20);
						int var34 = 0;

						for (CommandControllerCommand var9 : var2.commandController.localPendingCommands) {
							if (var9.scheduledGameSecond == this.nextBlockingFrame) {
								var34++;
							}
						}

						var30.writeInt(var34);

						for (CommandControllerCommand var39 : var2.commandController.localPendingCommands) {
							if (var39.scheduledGameSecond == this.nextBlockingFrame) {
								var39.write(var30);
							}
						}
					} catch (IOException var12) {
						throw new RuntimeException(var12);
					}

					Packet var35 = var30.createPacket(10);
					var35.reliable = true;
					this.sendPacketToAll(var35);
					this.nextBlockingFrame = var20;
				}
			}
		}

		if (!var2.commandController.networkPendingCommands.isEmpty()) {
			Iterator var15 = var2.commandController.networkPendingCommands.iterator();

			while (var15.hasNext()) {
				CommandControllerCommand var21 = (CommandControllerCommand)var15.next();
				boolean var25 = false;
				if (var25) {
					var2.commandController.c.add(var21);
					var15.remove();
				} else {
					if (!var21.pathfindingQueued) {
						var21.queuePathfinding();
					}

					if (var21.allPathsReady()) {
						var2.commandController.c.add(var21);
						var15.remove();
					}
				}
			}
		}

		if (!this.isServer) {
			if (!var2.commandController.c.isEmpty()) {
				for (CommandControllerCommand var22 : var2.commandController.c) {
					if (!var22.isComplete()) {
						var22.j();
						OutputNetStream var26 = new OutputNetStream();

						try {
							var22.write(var26);
						} catch (IOException var10) {
							throw new RuntimeException(var10);
						}

						this.sendPacketToAll(var26.createPacket(20));
					}
				}

				var2.commandController.c.clear();
			}
		} else if (!var2.commandController.c.isEmpty()) {
			for (CommandControllerCommand var23 : var2.commandController.c) {
				if (!var23.isComplete()) {
					if (!var23.rebuildAllowedTeamMask()) {
						reportDesync("Skipped command issued from server");
					} else {
						var23.j();
						this.queueCommand(var23);
					}
				}
			}

			var2.commandController.c.clear();
		}

		while (!this.incomingPacketQueue.isEmpty()) {
			Packet var18 = (Packet)this.incomingPacketQueue.remove();

			try {
				this.processGamePacket(var18);
			} catch (IOException var11) {
				String var27 = "None";
				Connection var31 = var18.connection;
				if (var31 != null) {
					var27 = var31.getDisplayHostAddress();
					String var36 = var11.getMessage();
					if (var36 == null) {
						var36 = "IO error";
					}

					var31.sendKickWithReason(var36);
					reportDesync("IO error on processGamePacket for " + var31.getPlayerName());
				}

				GameEngine.logException("Error on processGamePacket ip:" + var27, (Throwable)var11);
			}
		}

		if (this.isServer) {
			if (!this.networked) {
				GameEngine.log("Skipping server updates, not networked");
			} else {
				this.removeClosedConnections();
				if (!this.pauseOnDesync) {
					this.updateDesyncTimers(float1);
				}
			}
		}

		if (this.networked) {
			String var19 = "Game paused.";
			if (this.gamePaused) {
				var2.interfaceEngine.showScreenMessageAlt("Game paused.", 100);
			} else {
				var2.interfaceEngine.showScreenMessageDefault("Game paused.");
			}
		}

		if (var2.gameTimeSeconds < this.nextBlockingFrame) {
			this.waitingForServerStep = false;
		}

		if (this.disconnectQueued) {
			this.disconnect("queDisconnect");
		}
	}

	public strictfp void updateClientConnectionStatus(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		if (var2 != null) {
			if (!this.isServer && this.networked) {
				boolean var3 = false;

				for (Connection var5 : this.connections) {
					if (var5.connected && !var5.closed) {
						var3 = true;
					}
				}

				if (this.serverEndedGame && this.hasGameBeenStarted()) {
					var2.interfaceEngine.showImportantScreenMessage("Game ended by server.");
					MultiplayerBattleroomActivity.o();
				} else if (!var3 && this.hasGameBeenStarted()) {
					var2.interfaceEngine.showImportantScreenMessage("Server Disconnected.");
					MultiplayerBattleroomActivity.o();
				}

				if (var3 && (this.waitingForServerStep || this.lastUpdateTime + 1000L < System.currentTimeMillis()) && !this.isServer) {
					Connection var6 = this.getClientConnection();
					if (var6 != null && var6.receivingProgressTotal > 20000) {
						String var7 = "Receiving network data: " + var6.receivingProgressBytes + "/" + var6.receivingProgressTotal;
						GameEngine.log(var7);
						var2.interfaceEngine.showTransientMessage(var7);
						if (!this.gameStarted && this.lastProgressNotifyTime + 4000L < System.currentTimeMillis()) {
							this.lastProgressNotifyTime = System.currentTimeMillis();
							this.addLocalMessage(var7);
						}

						this.sendReceivingProgress(var6, var6.receivingProgressBytes, var6.receivingProgressTotal);
					}
				}
			}
		}
	}

	public strictfp void updateChecksums(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		this.lastUpdateTime = System.currentTimeMillis();
		if (this.networked && (this.lastChecksumFrame + this.checksumIntervalSeconds < var2.gameTimeSeconds || this.lastChecksumFrame == -1)) {
			this.resetChecksumPeriod();
			var2.replayEngine.recordChecksum(this.checksum);
		}

		if ((this.networked || var2.replayEngine.isPlayingReplay()) && this.quickResyncPending) {
			this.quickResyncPending = false;
			this.performQuickResync();
		}

		if (this.networked) {
			if (this.isServer && !this.checksumSent && this.lastChecksumFrame + this.checksumIntervalSeconds / 2 < var2.gameTimeSeconds && this.lastChecksumFrame != -1) {
				try {
					OutputNetStream var3 = new OutputNetStream();
					var3.writeInt(this.lastChecksumFrame);
					var3.writeLong(this.checksum.total);
					var3.writeInt(this.checksum.values.size());

					for (GameChecksumValue var5 : this.checksum.values) {
						var3.writeLong(var5.value);
					}

					Packet var7 = var3.createPacket(30);
					this.sendPacketToClientsNonRelay(var7);
					if (this.extraDebug) {
						GameEngine.log("Sent checksum to client [" + this.lastChecksumFrame + "]");
					}

					this.checksumSent = true;
				} catch (IOException var6) {
					throw new RuntimeException(var6);
				}
			}
		}
	}

	public strictfp boolean shouldGameBePaused() {
		GameEngine var1 = GameEngine.getInstance();
		if (var1.pathEngine.hasOverduePaths()) {
			if (!this.lastShouldPauseState) {
				GameEngine.log("shouldGameBePaused: isGoingToBlockThisFrame()==true: " + var1.pathEngine.getOverduePathsInfo());
			}

			this.lastShouldPauseState = true;
			return true;
		} else {
			if (this.lastShouldPauseState) {
				GameEngine.log("shouldGameBePaused: isGoingToBlockThisFrame()==false");
			}

			this.lastShouldPauseState = false;
			return false;
		}
	}

	public strictfp void checkCanRunNextGameStep(float float1, boolean boolean2) {
		GameEngine var3 = GameEngine.getInstance();
		if (var3.gameTimeSeconds >= this.nextBlockingFrame) {
			if (var3.gameTimeSeconds > this.nextBlockingFrame) {
				throw new RuntimeException("game frame:" + var3.gameTimeSeconds + " is greater then nest step:" + this.nextBlockingFrame);
			}

			this.waitingForServerStep = true;
		}

		if (boolean2 && this.shouldGameBePaused()) {
			this.waitingForServerStep = true;
		}
	}

	public strictfp void processGamePacket(Packet au) {
		GameEngine var2 = GameEngine.getInstance();
		if (this.isPacketFiltered(aux)) {
			this.logNetwork("filtered packet (type:" + aux.type + ")");
		} else {
			switch (aux.type) {
				case 10:
					if (this.isServer) {
						this.logNetwork("we are a server! we don't follow orders");
					} else if (aux.connection.ignoreIncomingCommands) {
						this.logNetwork("ignoring command");
					} else {
						InputNetStream var22 = new InputNetStream(aux);
						int var26 = var22.readInt();
						int var30 = var22.readInt();

						for (int var34 = 0; var34 < var30; var34++) {
							CommandControllerCommand var37 = var2.commandController.createCommand();
							var37.scheduledGameSecond = this.nextBlockingFrame;
							var37.read(var22);
							this.queueCommand(var37);
						}

						if (var26 < this.nextBlockingFrame) {
							String var35 = "New nextBlockingFrame:" + var26 + " is smaller than current step:" + this.nextBlockingFrame;
							reportDesync(var35);
						}

						this.nextBlockingFrame = var26;
					}
					break;
				case 20:
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					} else {
						InputNetStream var21 = new InputNetStream(aux);
						Connection var25 = aux.connection;
						if (!var25.isCommandRateLimited()) {
							Player var29 = var25.player;
							if (var29 == null) {
								this.logNetwork("Player is null for message ADDCLIENTCOMMAND, skipping");
							} else {
								CommandControllerCommand var33 = var2.commandController.createCommand();
								var33.read(var21);
								var33.player = var29;
								if (var33.hasSystemAction) {
									this.logNetwork("Got system action from client, ignoring (" + var25.id + ")");
									var33.hasSystemAction = false;
								}

								if (var33.getTeam() == null) {
									reportDesync("Invalid command from '" + var29.v + "', no team found");
								} else if (!var33.rebuildAllowedTeamMask()) {
									reportDesync("Ignored command from '" + var29.v + "', check failed");
								} else {
									this.queueCommand(var33);
								}
							}
						}
					}
					break;
				case 30:
					Connection var20 = aux.connection;
					InputNetStream var24 = new InputNetStream(aux);
					int var28 = var24.readInt();
					long var32 = var24.readLong();
					if (this.waitingForResync) {
						this.logNetwork("PACKET_SYNCCHECKSUM: skipping frame:" + var28 + ", we were told to wait for resync");
					} else {
						OutputNetStream var39 = new OutputNetStream();
						var39.writeByte(0);
						var39.writeInt(var28);
						var39.writeInt(this.lastChecksumFrame);
						if (this.lastChecksumFrame == var28 && this.checksum.total != 0L) {
							var39.writeBoolean(true);
							Log.d("RustedWarfare", "Running checksum");
							var39.writeLong(var32);
							var39.writeLong(this.checksum.total);
							boolean var40 = false;
							if (var32 != this.checksum.total) {
								reportDesync("Checksum doesn't match. Got:" + var32 + " expected:" + this.checksum.total);
								var40 = true;
								GameEngine.log("--- Desync for frame: " + var28 + " ---");

								for (Team var46 : Team.getTeams()) {
									var46.verifyUnitCaches();
								}
							} else {
								this.checksumMatchCount++;
							}

							int var44 = var24.readInt();
							if (var44 != this.checksum.values.size()) {
								Log.d("RustedWarfare", "checkSumSize!=syncCheckList.size()");
							}

							var39.startBlock("checkList");
							var39.writeInt(var44);
							var39.writeInt(this.checksum.values.size());

							for (GameChecksumValue var49 : this.checksum.values) {
								long var51 = var24.readLong();
								var39.writeLong(var51);
								var39.writeLong(var49.value);
								if (var51 != var49.value && var49.active) {
									reportDesync("[" + var28 + "] check(" + var49.name + "): " + var51 + "!=" + var49.value);
									var40 = true;
								}
							}

							var39.endBlock("checkList");
							var39.writeBoolean(var40);
						} else {
							var39.writeBoolean(false);
							Log.d("RustedWarfare", "got remoteSyncFrame for:" + var28 + " needed:" + this.lastChecksumFrame + " lastSyncCheckSum:" + this.checksum.total);
						}

						if (!this.isServer) {
							Packet var41 = var39.createPacket(31);
							this.sendPacketOnConnection(var20, var41);
						}
					}
					break;
				case 31:
					if (!this.isServer) {
						this.logNetwork("we are not a server, but got PACKET_SYNCCHECKSUM_STATUS");
					} else {
						Connection var19 = aux.connection;
						InputNetStream var23 = new InputNetStream(aux);
						var23.readByte();
						int var27 = var23.readInt();
						int var31 = var23.readInt();
						boolean var36 = var23.readBoolean();
						if (var36) {
							long var38 = var23.readLong();
							long var42 = var23.readLong();
							var23.startBlock("checkList");
							var23.readInt();
							int var48 = var23.readInt();
							if (var48 != this.checksum.values.size()) {
								Log.d("RustedWarfare", "checkSumSize!=syncCheckList.size()");
							}

							for (GameChecksumValue var14 : this.checksum.values) {
								long var15 = var23.readLong();
								long var17 = var23.readLong();
								if (var15 != var17) {
									GameEngine.logWarning(var14.name + " Checksum [" + var27 + "]. server:" + var15 + " client:" + var17);
								}
							}

							var23.endBlock("checkList");
							boolean var50 = var23.readBoolean();
							if (this.lastResyncFrame >= var27) {
								this.logNetwork("Not marking desync, already resynced before frame: " + this.lastResyncFrame + "<=" + var27);
							} else {
								if (!var19.desyncDetected && var50) {
									var19.desyncCount++;
								}

								var19.desyncDetected = var50;
								if (!var50) {
									if (this.extraDebug) {
										GameEngine.log("checksum: client checksum match [" + var27 + "]");
									}

									var19.resyncCount++;
								} else {
									GameEngine.log("client:" + var19.getPlayerName() + " desync [" + var27 + "]");
									if (this.pauseOnDesync && !this.pausedDueToDesync) {
										reportDesync("pauseOnDesync is active, pausing");
										this.pausedDueToDesync = true;
									}
								}
							}
						} else if (this.extraDebug) {
							GameEngine.log("checksum for:" + var19.getPlayerName() + " frameMatch==false client:" + var31 + " server:[" + var27 + "]");
						}
					}
					break;
				case 35:
					InputNetStream var3 = new InputNetStream(aux);
					var3.readByte();
					int var4 = var3.readInt();
					int var5 = var3.readInt();
					float var6 = var3.readFloat();
					float var7 = var3.readFloat();
					if (!this.isServer && var6 < 0.1) {
						reportDesync("resync packet with setCurrentStepRate:" + var6 + " is too small", true);
					}

					Connection var8 = aux.connection;
					if (var8.ignoreIncomingCommands) {
						this.logNetwork("ignoring resync command");
					} else {
						boolean var9 = var3.readBoolean();
						boolean var10 = var3.readBoolean();
						if (var10) {
							if (!this.isServer) {
								this.logNetwork("we are not a server, but got a debug game save! skipping");
							} else {
								byte[] var11 = var3.getBlockRaw("gameSave");
								this.saveDesyncSave(var11, var8);
							}
						} else {
							GameEngine.log("Reloading from network save");
							if (var9 && !this.isServer) {
								this.sendResyncSave(false, true, false);
							}

							byte[] var45 = var3.getBlockRaw("gameSave");
							GameEngine.log("Save size: " + var45.length);
							if (this.saveDesyncSaves) {
								this.saveDesyncSave(var45, var8);
							}

							var2.replayEngine.recordResyncData(var45, var2.gameTimeSeconds, var4, var5, var6, var7);
							InputNetStream var12 = new InputNetStream(var45);
							var2.setLoadingMessage("Resyncing game from server...", true);
							var2.gameSaver.loadGameFromStream(var12, true, true, true);
							var2.clearLoadingMessage();
							this.resyncCount++;
							var2.gameTimeSeconds = var4;
							var2.gameTimeMillis = var5;
							this.nextBlockingFrame = var4 + 1;
							this.waitingForResync = false;
							this.lastChecksumFrame = this.nextBlockingFrame + 1;
							this.checksum.total = 0L;
							if (var6 < 0.1) {
								reportDesync("resync setCurrentStepRate:" + var6 + " is too small", true);
							}

							this.setCurrentStepRate(var6, "rsync");
							this.resyncStepRate = var7;
						}
					}
					break;
				default:
					this.logNetwork("we did not handle packet:" + aux.type);
			}
		}
	}

	public strictfp synchronized boolean isPacketFiltered(Packet au) {
		if (this.isServer) {
			Connection var2 = aux.connection;
			if (var2 == null) {
				return false;
			}

			if (!var2.connected && aux.type != 105 && aux.type != 110 && aux.type != 111 && aux.type != 108 && aux.type != 160) {
				return true;
			}
		}

		return false;
	}

	public strictfp synchronized void processSystemPacket(Packet au) {
		GameEngine var2 = GameEngine.getInstance();
		if (this.isPacketFiltered(aux)) {
			this.logNetwork("filtered packet (type:" + aux.type + ")");
		} else {
			switch (aux.type) {
				case 4:
					Connection var56 = aux.connection;
					InputNetStream var77 = new InputNetStream(aux);
					byte var95 = var77.readByte();
					var77.readInt();
					var77.readInt();
					break;
				case 105:
					this.logNetwork("got PACKET_GET_SERVER_INFO");
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					}
					break;
				case 106:
					if (this.isServer) {
						this.logNetwork("we are a server! we don't follow orders");
					} else {
						InputNetStream var55 = new InputNetStream(aux);
						Connection var76 = aux.connection;
						var55.readUTF();
						var55.readInt();
						this.gameSetup.mapType = (GameSetupMapType)var55.readEnum(GameSetupMapType.class);
						this.gameSetup.mapName = var55.readUTF();
						this.gameSetup.startingCredits = var55.readInt();
						this.gameSetup.fogMode = var55.readInt();
						this.gameSetup.revealedMap = var55.readBoolean();
						this.gameSetup.aiDifficulty = var55.readInt();
						byte var94 = var55.readByte();
						this.G = var55.readBoolean();
						this.proxyController = var55.readBoolean();
						this.receivedServerInfo = true;
						if (var94 >= 1) {
							this.teamUnitCap = var55.readInt();
							this.teamUnitCapMax = var55.readInt();
						}

						if (var94 >= 2) {
							this.gameSetup.startingUnits = var55.readInt();
							this.gameSetup.incomeMultiplier = var55.readFloat();
							this.gameSetup.noNukes = var55.readBoolean();
							this.gameSetup.j = var55.readBoolean();
						}

						if (var94 >= 3) {
							boolean var112 = var55.readBoolean();
							if (var112) {
								try {
									CustomUnitMetadata.readCustomUnitsFromStream(var55);
									this.customUnitsEnabled = true;
								} catch (DetailedUnitConfigException var31) {
									this.disconnect("Missing unit:" + var31.getMessage() + " d:" + var31.b);
									this.closeBattleroom("Server sync mismatch", var31.getMessage());
									if (!GameEngine.isDesktopVersion()) {
										var2.showAlert(var31.getMessage());
									}

									String var137 = "Server sync mismatch";
									if (var31.a != null) {
										var137 = var31.a;
									}

									var2.queueMessageBox(var137, var31.getMessage());
									break;
								}
							}
						}

						if (var94 >= 4) {
							this.gameSetup.sharedControl = var55.readBoolean();
						}

						if (var94 >= 5) {
							this.gameSetup.teamsLocked = var55.readBoolean();
						}

						if (var94 >= 6) {
							this.gameSetup.enforceTwoTeams = var55.readBoolean();
						}

						if (var94 >= 7) {
							this.gameSetup.allowSpectators = var55.readBoolean();
							this.gameSetup.lockedRoom = var55.readBoolean();
						}

						if (var94 >= 8) {
							this.gameSetup.randomSeed = var55.readInt();
						}

						MultiplayerBattleroomActivity.o();
					}
					break;
				case 108:
					Connection var54 = aux.connection;
					InputNetStream var75 = new InputNetStream(aux);
					long var93 = var75.readLong();
					var75.readByte();
					OutputNetStream var125 = new OutputNetStream();
					var125.writeLong(var93);
					var125.writeByte(1);
					int var136 = var2.getGameStep();
					if (var136 > 130) {
						var136 = 130;
					}

					var125.writeByte(var136);
					Packet var150 = var125.createPacket(109);
					this.sendPacketOnConnection(var54, var150);
					break;
				case 109:
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					} else {
						long var53 = System.currentTimeMillis();
						Connection var92 = aux.connection;
						InputNetStream var111 = new InputNetStream(aux);
						long var124 = var111.readLong();
						byte var149 = var111.readByte();
						byte var160 = 0;
						if (var149 >= 1) {
							var160 = var111.readByte();
						}

						int var167 = (int)(var53 - var124);
						var92.lastPingValue = var167;
						var92.lastPingTime = var53;
						if (var92.player != null) {
							var92.player.W = var167;
							var92.player.X = var53;
							var92.player.V = var160;
						}

						if (var92.relayServer && this.isServer && this.D && this.localTeam != null) {
							this.localTeam.ping = var167;
							this.localTeam.lastPingTimeReceivedAt = var53;
						}

						if (!this.gameStarted) {
							MultiplayerBattleroomActivity.o();
						}
					}
					break;
				case 110:
					this.logNetwork("got REGISTER_CONNECTION");
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					} else {
						InputNetStream var52 = new InputNetStream(aux);
						Connection var74 = aux.connection;
						String var91 = var52.readUTF();
						int var110 = var52.readInt();
						int var123 = var52.readInt();
						int var135 = var52.readInt();
						String var147 = var52.readUTF();
						String var159 = var52.readNullableUTF();
						String var166 = null;
						var74.version = var123;
						if (var110 >= 1) {
							var74.packageName = var52.readUTF();
						}

						if (var110 >= 2) {
							var166 = var52.readUTF();
						}

						int var172 = -1;
						if (var110 >= 3) {
							var172 = var52.readInt();
						}

						String var175 = "MISSING";
						if (var110 >= 4) {
							var175 = var52.readUTF();
						}

						String var14 = "";
						if (var110 >= 5) {
							var14 = var52.readUTF();
						}

						if (var147.length() > 20) {
							this.sendKick(var74, "Your username is too long");
							var74.sendKickWithReason("kicked");
						} else {
							var147 = sanitiseUserName(var147);
							if (var147.length() < 2) {
								this.sendKick(var74, "Your username is too short");
								var74.sendKickWithReason("kicked");
							} else {
								Player var15 = null;
								if (var166 != null) {
									var15 = Team.findExistingPlayer(var166);
									if (var15 != null) {
										this.logNetwork("Existing player: " + var15.k + " - " + var15.v);
									}
								}

								BanEntry var16 = this.getBanEntry(var74);
								if (var16 != null) {
									GameEngine.log("Connection banned for " + var16.b() + " more seconds");
									String var17 = var16.a();
									this.sendKick(var74, var17);
									var74.sendKickWithReason("kicked");
								} else {
									String var176 = this.networkCallbacks.onPlayerJoining(var74, var147, var123, var135, var74.packageName, var15);
									if (var176 != null) {
										this.sendKick(var74, var176);
										var74.sendKickWithReason("kicked");
									} else if (var123 < this.gameVersion && !this.chatOnlyServer) {
										this.sendKick(var74, "Game is out of date, please update to v" + var2.getGameVersionFull());
										var74.sendKickWithReason("kicked");
									} else if (var123 > this.gameVersion && !this.chatOnlyServer) {
										this.sendKick(var74, "Your client is newer then the server. Server is on: v" + var2.getGameVersionFull());
										var74.sendKickWithReason("kicked");
									} else if (!this.chatOnlyServer && var172 != var2.getNetworkPort()) {
										GameEngine.log(
											"New Player kicked: Unit checksum mismatch: clientUnitsChecksum=" + var172 + " game.getAllUnitsChecksum():" + var2.getNetworkPort()
										);
										this.sendKick(var74, "Your core units are different to the server's core units. Game can not be synchronized");
										var74.sendKickWithReason("kicked");
									} else {
										if (!this.chatOnlyServer) {
											String var18 = this.getIntegrityResponse(var74.integrityCheckValue);
											if (!var18.equals(var175)) {
												GameEngine.log("New Player kicked: Integrity Check Failed: expectedResponse=" + var18 + " clientResponse=" + var175);
												this.sendKick(var74, "Your 'Rusted Warfare' client is different to the server. Game can not be synchronized.");
												var74.sendKickWithReason("kicked");
												break;
											}
										}

										if (!this.gameStarted && this.gameSetup.lockedRoom) {
											this.sendKick(var74, "Room is locked. New players cannot join this server.");
											var74.sendKickWithReason("kicked");
										} else if (this.gameStarted && var15 == null && !this.allowJoinInProgress) {
											this.sendKick(var74, "A game has already been started on this server");
											var74.sendKickWithReason("kicked");
										} else {
											if (this.serverPassword != null && var15 == null) {
												String var177 = CommonUtils.sha256Hex(this.serverPassword);
												if (!var177.equals(var159)) {
													if (var159 == null) {
														GameEngine.logWithTag("processSystemPacket", "Player tried to join but needs a password");
													} else {
														GameEngine.logWithTag("processSystemPacket", "Player tried to join but had an incorrect password");
													}

													this.sendIncorrectPassword(var74);
													break;
												}
											}

											String var178 = this.getExtraChallengeResponse(this.extraChallengeSeed);
											if (!var178.equals(var14)) {
												var74.log("no extra");
												var74.extraChallengeFailed = true;
											}

											if (var74.player == null) {
												synchronized (this.bC) {
													int var20;
													if (var15 == null) {
														var20 = Team.findFreeTeamId();
													} else {
														var20 = var15.k;
													}

													if (var20 == -1 && !this.chatOnlyServer) {
														this.sendKick(var74, "No free slots on server");
														var74.sendKickWithReason("no free slots");
													} else {
														String var21 = this.networkCallbacks.a(var74, var147);
														if (var21 != null) {
															this.sendKick(var74, var21);
															var74.sendKickWithReason("kicked");
														} else {
															Obfuscation.a(var74);
															if (!this.chatOnlyServer && var74.O) {
																this.sendKick(var74, "");
																var74.sendKickWithReason("kicked");
															} else {
																String var22 = null;
																if (var15 != null) {
																	var74.player = var15;
																	String var23 = "";
																	if (this.gameStarted) {
																		if (var15.b()) {
																			var23 = " (Spectator)";
																		} else {
																			var23 = " (Team " + var15.h() + ")";
																		}
																	}

																	this.sendSystemMessage("'" + var74.player.v + "' reconnected. " + var23);
																	var74.resyncRequested = true;
																	var22 = var15.v;
																	var15.P = var74.m;
																} else {
																	if (this.chatOnlyServer && var20 == -1) {
																		var74.player = new Player(-3);
																	} else {
																		var74.player = new Player(var20);
																		var74.player.r = var20 % 2;
																	}

																	if (this.gameStarted && this.allowJoinInProgress) {
																		var74.resyncRequested = true;
																	}
																}

																if (var15 == null && var147 != null) {
																	ArrayList var179 = this.getTeams();

																	for (int var24 = 0; var24 < 10; var24++) {
																		boolean var25 = false;
																		String var26 = var147;
																		if (var24 > 0) {
																			var26 = var147 + "(" + var24 + ")";
																		}

																		for (Team var28 : var179) {
																			if (var26.equalsIgnoreCase(var28.name)) {
																				var25 = true;
																			}
																		}

																		if (!var25) {
																			var147 = var26;
																			break;
																		}
																	}
																}

																var74.player.v = var147;
																var74.player.O = var166;
																var74.player.P = var74.m;
																var74.version = var123;
																GameEngine.logWithTag(
																	"processSystemPacket", "New player: " + var147 + ", networkVersion:" + var74.version + " existing:" + (var15 != null)
																);
																var74.connected = true;
																if (var15 == null) {
																	this.networkCallbacks.a(var74.player);
																}

																MultiplayerBattleroomActivity.o();
																this.sendUpdatePlayer(var74);
																this.sendServerInfo(var74);
																this.networkCallbacks.c(var74, var147, var22);
																if ((var15 != null || this.allowJoinInProgress) && this.gameStarted) {
																	boolean var180 = true;
																	this.sendStartGame(var74, var180);
																}
															}
														}
													}
												}
											} else {
												GameEngine.logWithTag("processSystemPacket", "This connection already has a player");
											}
										}
									}
								}
							}
						}
					}
					break;
				case 111:
					InputNetStream var51 = new InputNetStream(aux);
					Connection var73 = aux.connection;
					String var90 = null;

					try {
						var90 = var51.readUTF();
					} catch (IOException var30) {
						GameEngine.logException("Error reading disconnect reason", (Throwable)var30);
					}

					this.logNetwork("Got a disconnect packet:" + var90);
					if (var73 != null) {
						var73.handleRemoteDisconnect(false, false, var90);
					}

					if (!this.isServer) {
					}
					break;
				case 112:
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					} else {
						Connection var50 = aux.connection;
						InputNetStream var72 = new InputNetStream(aux);
						var50.C = var72.readBoolean();
						var50.clientReady = var72.readBoolean();
					}
					break;
				case 113:
					if (this.isServer) {
						this.logNetwork("we are a server! skipping: " + aux.type);
					} else {
						showPasswordPrompt(passwordPrompt);
					}
					break;
				case 115:
					if (this.isServer) {
						this.logNetwork("we are a server! we don't follow orders");
					} else {
						InputNetStream var49 = new InputNetStream(aux);
						var49.setProtocolVersion(aux.connection.version);
						Connection var71 = aux.connection;
						int var89 = var49.readInt();
						Object var109 = null;
						int var122 = 8;
						boolean var134 = false;
						if (var49.getProtocolVersion() >= 90) {
							boolean var144 = false;
							if (var49.getProtocolVersion() >= 141) {
								var144 = true;
								var134 = var49.readBoolean();
							}

							var122 = var49.readInt();
							Team.setMaxTeamId(var122, false);
							var49.startBlock("teams", var144);
							if (var122 > Team.maxTeamId) {
								throw new IOException("Cannot load:" + var122 + " teams");
							}
						} else if (this.gameStarted) {
							reportDesync("Warning old team system used in started game, stream version:" + var49.getProtocolVersion());
						}

						for (int var145 = 0; var145 < var122; var145++) {
							Object var157 = Team.getTeam(var145);
							boolean var165 = var49.readBoolean();
							if (!var165) {
								if (var157 != null) {
									if (this.gameStarted) {
										reportDesync("Warning team:" + var145 + " removed while game is running");
									}

									var157.removeFromTeamArray();
								}
							} else {
								int var171 = var49.readInt();
								if (var157 == null) {
									if (this.gameStarted) {
										reportDesync("Warning team:" + var145 + " added while game is running");
									}

									if (!this.isServer && var157 instanceof AI) {
										reportDesync("Warning we are a client with an AI team");
									}

									var157 = new Player(var145);
								}

								if (var134) {
									var157.readPingUpdate(var49);
								} else {
									var157.readTeamState(var49, this.gameStarted);
								}
							}

							if (var157 != null && ((Team)var157).teamId == var89) {
								var109 = var157;
							}
						}

						if (var49.getProtocolVersion() >= 90) {
							var49.endBlock("teams");
						}

						this.localTeam = (Team)var109;
						this.gameSetup.fogMode = var49.readInt();
						this.gameSetup.startingCredits = var49.readInt();
						this.gameSetup.revealedMap = var49.readBoolean();
						this.gameSetup.aiDifficulty = var49.readInt();
						byte var146 = var49.readByte();
						this.teamUnitCap = var49.readInt();
						this.teamUnitCapMax = var49.readInt();
						if (var146 >= 2) {
							this.gameSetup.startingUnits = var49.readInt();
							this.gameSetup.incomeMultiplier = var49.readFloat();
							this.gameSetup.noNukes = var49.readBoolean();
							this.gameSetup.j = var49.readBoolean();
						}

						if (var146 >= 3) {
							boolean var158 = var49.readBoolean();
							if (var158) {
								try {
									CustomUnitMetadata.readCustomUnitsFromStream(var49);
									this.customUnitsEnabled = true;
								} catch (DetailedUnitConfigException var33) {
									this.disconnect("Missing unit:" + var33.getMessage() + " d:" + var33.b);
									this.closeBattleroom("Connection Failed", var33.getMessage());
									if (!GameEngine.isDesktopVersion()) {
										var2.showAlert(var33.getMessage());
									}

									var2.queueMessageBox("Connection Failed", var33.getMessage());
									break;
								}
							}
						}

						if (var146 >= 4) {
							this.gameSetup.sharedControl = var49.readBoolean();
						}

						if (var146 >= 5) {
							this.gamePaused = var49.readBoolean();
						}

						MultiplayerBattleroomActivity.o();
					}
					break;
				case 116:
					if (this.isServer) {
						this.logNetwork("we are a server! we don't follow orders");
					} else {
						InputNetStream var48 = new InputNetStream(aux);
						Connection var70 = aux.connection;
						int var88 = var48.readInt();
						boolean var108 = var48.readBoolean();
						if (var108 && !this.serverEndedGame) {
							this.serverEndedGame = var108;
						}
					}
					break;
				case 117:
					Connection var47 = aux.connection;
					if (this.isServer && !var47.relayServer) {
						this.logNetwork("we are a server! skipping: " + aux.type);
					} else {
						InputNetStream var69 = new InputNetStream(aux);
						var69.readByte();
						int var87 = var69.readInt();
						String var107 = var69.readUTF();
						PasswordPrompt var121 = new PasswordPrompt();
						var121.d = true;
						var121.c = var87;
						var121.b = var107;
						showPasswordPrompt(var121);
					}
				case 118:
					break;
				case 120:
					if (this.isServer) {
						this.logNetwork("error, we are a server but got: PACKET_START_GAME");
					} else {
						InputNetStream var46 = new InputNetStream(aux);
						var46.readByte();
						this.gameSetup.mapType = (GameSetupMapType)var46.readEnum(GameSetupMapType.class);
						if (this.gameSetup.mapType == GameSetupMapType.savedGame) {
							this.savedGameStream = var46.readSubStream();
						} else if (this.gameSetup.mapType == GameSetupMapType.customMap) {
							this.customMapStream = var46.readSubStream();
						}

						this.mapName = var46.readUTF();
						this.startNetworkGame();
					}
					break;
				case 122:
					if (this.isServer) {
						this.logNetwork("error, we are a server but got: PACKET_RETURN_TO_BATTLEROOM");
					} else {
						this.queueReturnToBattleroom();
					}
					break;
				case 140:
					if (!this.isServer) {
						this.logNetwork("we are not a server! skipping");
					} else {
						Connection var45 = aux.connection;
						InputNetStream var68 = new InputNetStream(aux);
						Player var86 = var45.player;
						if (var86 == null) {
							if (!var45.relayServer) {
								this.logNetwork("player is null for message, skipping");
								break;
							}

							this.logNetwork("Allowing message from non player on forwarding connection");
							var86 = this.adminPlayer;
						}

						String var105 = var68.readUTF();
						var68.readByte();
						var105 = sanitiseChatMessage(var105);
						if (this.networkCallbacks.a(var45, var86.v, var105)) {
							if (this.chatLog.countRecentMessagesFromConnection(var45, 60000) > this.maxMessagesPerMinute) {
								if (CommonUtils.nanosToMillis(var45.lastSpamWarningNanos, System.nanoTime()) > 60000L) {
									var45.lastSpamWarningNanos = System.nanoTime();
									this.sendSystemMessage("Anti-spam: Too many messages from '" + var45.getPlayerName() + "'");
								}

								if (this.extraDebug) {
									GameEngine.log("extraDebug:" + var105);
								}
							} else {
								this.routeChatMessage(var45, var86, var86.v, var105);
								this.networkCallbacks.b(var45, var86.v, var105);
								this.handleChatCommand(var45, var86, var86.v, var105);
							}
						}
					}
					break;
				case 141:
					if (this.isServer) {
						Connection var43 = aux.connection;
						if (!var43.relayServer) {
							this.logNetwork("error, we are a server but got: PACKET_RECEIVE_CHAT_FROM_SERVER");
							break;
						}
					}

					InputNetStream var44 = new InputNetStream(aux);
					String var67 = var44.readUTF();
					byte var85 = var44.readByte();
					String var104 = var44.readNullableUTF();
					var44.readInt();
					int var120 = -1;
					if (var85 >= 3) {
						var120 = var44.readInt();
					}

					this.receiveChatMessage(null, var120, var104, var67);
					break;
				case 150:
					if (this.isServer) {
						this.logNetwork("error, we are a server but got: PACKET_SEND_KICK");
					} else {
						InputNetStream var42 = new InputNetStream(aux);
						String var65 = var42.readUTF();
						var65 = LocaleEngine.c(var65);
						this.logNetwork("we got kicked, reason:" + var65);
						this.disconnect("I was kicked");
						this.closeBattleroom("Kicked", "Kicked: " + var65);
						var2.queueMessageBox("Kicked", "Kicked: " + var65);
						var2.showAlert("Kicked: " + var65);
					}
					break;
				case 151:
					Connection var41 = aux.connection;
					if (this.isServer && !var41.relayServer) {
						this.logNetwork("error, we are a server but got: 151");
					} else {
						long var64 = TimingStatTimer.timeNow();
						InputNetStream var103 = new InputNetStream(aux);
						int var119 = var103.readInt();
						int var133 = var103.readInt();
						if (var103.readBoolean()) {
							Obfuscation.i = var103.readInt();
						}

						if (var103.readBoolean()) {
							Obfuscation.j = var103.readInt();
						}

						String var143 = "";
						if (var133 == 0) {
							var143 = "" + Obfuscation.i;
						}

						if (var133 == 1) {
							var143 = "" + Obfuscation.j;
						}

						if (var133 == 2) {
							var143 = this.getIntegrityResponse(Obfuscation.i);
						}

						if (var133 == 3) {
							var143 = CommonUtils.sha256Hex14(Obfuscation.i + "|" + Obfuscation.j);
						}

						if (var133 == 4) {
							var143 = CommonUtils.sha256Hex14(Obfuscation.i + "|" + Obfuscation.j);
						}

						if (var133 == 5 || var133 == 6) {
							String var154 = var103.readUTF();
							String var162 = var103.readUTF();
							int var169 = var103.readInt();
							if (var133 == 6) {
								var162 = var162 + Obfuscation.i;
							}

							if (var169 > 10000000) {
								var143 = "max";
							} else {
								var143 = "-1";

								for (int var174 = 0; var174 <= var169; var174++) {
									if (CommonUtils.sha256Hex14(var162 + var174).equals(var154)) {
										var143 = "" + var174;
										break;
									}
								}
							}
						}

						if (var133 == 7) {
							String var155 = var103.readUTF();
							int var163 = var103.readInt();
							if (var163 > 10000) {
								var143 = "max";
							} else {
								var143 = "";

								for (int var170 = 0; var170 < var163; var170++) {
									var143 = var143 + var155;
								}
							}
						}

						float var156 = TimingStatTimer.getElapsedMillis(var64);
						OutputNetStream var164 = new OutputNetStream();
						var164.writeInt(var119);
						var164.writeInt(var133);
						var164.writeUTF(var143);
						var164.writeFloat(var156);
						this.sendPacketOnConnection(var41, var164.createPacket(152));
					}
					break;
				case 160:
					InputNetStream var40 = new InputNetStream(aux);
					Connection var63 = aux.connection;
					String var84 = var40.readUTF();
					int var102 = var40.readInt();
					int var118 = var40.readInt();
					int var131 = 1;
					if (var102 >= 1) {
						var131 = var40.readInt();
					}

					if (var63.i) {
						GameEngine.log("steam: request info packet");
					}

					if (var102 >= 2) {
						String var141 = var40.readNullableUTF();
						if (var141 != null) {
							var63.log("Using query string: " + var141);
							var63.o = var141;
						}
					}

					if (var102 >= 3) {
						var40.readUTF();
					}

					if (var102 >= 4) {
						String var142 = var40.readUTF();
						String var153 = var40.readUTF();
						if (GameEngine.isDedicatedServer()) {
							var63.log("Misc: " + var153);
						}
					}

					this.sendPreregisterInfo(var63);
					break;
				case 161:
					if (this.isServer) {
						this.logNetwork("we are a server! we don't PREREGISTER_INFO");
					} else {
						InputNetStream var39 = new InputNetStream(aux);
						Connection var62 = aux.connection;
						if (var62.i) {
							GameEngine.log("steam: got info packet");
						}

						String var83 = var39.readUTF();
						int var101 = var39.readInt();
						int var117 = var39.readInt();
						int var130 = var39.readInt();
						String var140 = var39.readUTF();
						this.serverUUID = var39.readUTF();
						var62.version = var117;
						if (var101 >= 1) {
							this.integrityChallenge = var39.readInt();
						}

						if (var101 >= 2) {
							this.extraChallenge = var39.readInt();
							this.V = var39.readInt();
						}

						if (this.registerConnectionSent) {
							this.logNetwork("PACKET_SEND_PREREGISTER_INFO: Register connection has already been sent (resending)");
						}

						this.sendRegisterConnection(var62);
					}
					break;
				case 163:
					if (this.isServer) {
						this.logNetwork("we are already a server");
					} else {
						InputNetStream var38 = new InputNetStream(aux);
						var38.readByte();
						int var61 = var38.readInt();
						int var82 = var38.readInt();
						String var100 = var38.readNullableUTF();
						this.logNetwork("Relay version: " + var61);
					}
					break;
				case 170:
					this.logNetwork("Got 'become server' packet");
					if (this.isServer) {
						this.logNetwork("we are already a server");
					} else {
						Connection var37 = aux.connection;
						InputNetStream var60 = new InputNetStream(aux);
						byte var81 = var60.readByte();
						boolean var99 = var60.readBoolean();
						boolean var116 = var60.readBoolean();
						String var129 = var60.readNullableUTF();
						boolean var139 = var60.readBoolean();
						boolean var152 = var60.readBoolean();
						String var161 = var60.readNullableUTF();
						boolean var168 = false;
						if (var81 >= 1) {
							var168 = var60.readBoolean();
						}

						String var173 = null;
						if (var81 >= 2) {
							var173 = var60.readNullableUTF();
						}

						this.logNetwork("Multicast:" + var168);
						var37.r = var168;
						if (var99) {
							var37.relayServer = true;
						}

						if (var116) {
							var37.isRelay = true;
						}

						this.D = true;
						this.E = var161;
						var2.networkEngine.serverPassword = null;
						var2.networkEngine.modsEnabled = var139;
						var2.networkEngine.serverVisible = var152;
						this.setupLocalServerPlayer(false);
						if (var173 != null) {
							if (this.localTeam != null) {
								this.localTeam.clientId2 = var173;
							} else {
								GameEngine.log("Become server: No local team");
							}
						}

						if (var2.networkEngine.serverVisible) {
						}

						if (var129 != null) {
							var2.settings.networkServerId = var129;
						}

						if (var2.gameTimeSeconds > 60) {
							this.allPlayersReady = true;
						}

						if (!this.customUnitsEnabled && !this.gameStarted) {
							GameEngine.log("enableAllCustomUnitsPossible mods:" + this.modsEnabled);
							CustomUnitMetadataLoader.b(this.modsEnabled);
							this.customUnitsEnabled = true;
						}
					}
					break;
				case 172:
					Connection var36 = aux.connection;
					if (!var36.relayServer) {
						this.logNetwork("forwarding not allowed on this connection");
					} else {
						this.logNetwork("got FORWARD_CLIENT_ADD");
						InputNetStream var59 = new InputNetStream(aux);
						byte var80 = var59.readByte();
						int var98 = var59.readInt();
						String var115 = var59.readUTF();
						String var128 = var59.readNullableUTF();
						String var138 = null;
						if (var80 >= 1) {
							var138 = var59.readNullableUTF();
						}

						if (this.getForwardedConnection(var36, var98) != null) {
							this.logNetwork("Not adding client:" + var98 + " already exists");
						} else {
							Connection var151 = this.addForwardedConnection(var36, var98, var115, var138);
							if (var151 != null && var128 != null) {
								Player var11 = Team.findPlayerByClientId2(var115);
								if (var11 == null) {
									this.logNetwork("PACKET_FORWARD_CLIENT_ADD: Failed to find existing player with id:" + var115);

									for (Team var13 : Team.getTeams()) {
										if (var13 != null) {
											this.logNetwork("option: " + var13.name + " - " + var13.clientId2 + " - localPlayer:" + (this.localTeam == var13));
										}
									}
								} else {
									var11.clientId = var128;
								}
							}
						}
					}
					break;
				case 173:
					Connection var35 = aux.connection;
					if (!var35.relayServer) {
						this.logNetwork("forwarding not allowed on this connection");
					} else {
						this.logNetwork("got FORWARD_CLIENT_REMOVE");
						InputNetStream var58 = new InputNetStream(aux);
						byte var79 = var58.readByte();
						int var97 = var58.readInt();
						Object var114 = null;
						Connection var127 = this.getForwardedConnection(var35, var97);
						if (var127 != null) {
							this.disconnectConnection(var127, (String)var114);
						}
					}
					break;
				case 174:
					Connection var34 = aux.connection;
					if (!var34.relayServer) {
						this.logNetwork("forwarding not allowed on this connection");
					} else {
						InputNetStream var57 = new InputNetStream(aux);
						int var78 = var57.readInt();
						byte[] var96 = var57.readByteArray();
						Connection var113 = this.getForwardedConnection(var34, var78);
						if (var113 == null) {
							this.logNetwork("PACKET_FORWARD_CLIENT_FROM failed, cannot find client");
						} else if (!(var113.socket instanceof ForwardedSocket)) {
							this.logNetwork("PACKET_FORWARD_CLIENT_FROM failed, socket is wrong type");
						} else {
							ForwardedSocket var126 = (ForwardedSocket)var113.socket;
							var126.inputStream.addBuffer(var96);
						}
					}
					break;
				case 175:
					this.logNetwork("got PACKET_FORWARD_CLIENT_TO");
					break;
				case 176:
					this.logNetwork("got PACKET_FORWARD_CLIENT_TO_REPEATED");
					break;
				case 178:
					this.logNetwork("got PACKET_RECONNECT_TO");
					Connection var3 = aux.connection;
					if (this.isServer && !var3.relayServer) {
						this.logNetwork("we are a server, ");
					} else {
						InputNetStream var4 = new InputNetStream(aux);
						var4.readByte();
						int var5 = var4.readInt();
						boolean var6 = var4.readBoolean();
						int var7 = var4.readInt();
						ArrayList var8 = new ArrayList();

						for (int var9 = 0; var9 < var7; var9++) {
							String var10 = var4.readUTF();
							var8.add(var10);
						}

						this.startJoinServerThread(var8, var6);
					}
					break;
				default:
					this.logNetwork("we did not handle packet:" + aux.type);
			}
		}
	}

	public static strictfp String sanitiseChatMessage(String string) {
		if (string == null) {
			return null;
		} else {
			if (string.length() > 250) {
				string = string.substring(0, 250);
			}

			if (string.contains("\n")) {
				string = string.replace("\n", "?");
			}

			string = string.replace("\u0000", ".");
			boolean var1 = false;

			for (char var5 : string.toCharArray()) {
				if (Character.isISOControl(var5)) {
					var1 = true;
					break;
				}
			}

			if (var1) {
				StringBuilder var8 = new StringBuilder();

				for (char var6 : string.toCharArray()) {
					if (!Character.isISOControl(var6)) {
						var8.append(var6);
					}
				}

				string = var8.toString();
			}

			return string;
		}
	}

	public strictfp void openMenuOption21() {
		GameEngine var1 = GameEngine.getInstance();
		var1.interfaceEngine.battleInterface.PENDING_menuOption21();
	}

	public strictfp void closeBattleroom() {
		this.closeBattleroom(null, null);
	}

	public strictfp void closeBattleroom(String string1, String string2) {
		GameEngine.log("closeBattleroom..");
		MultiplayerBattleroomActivity.a(string1, string2);
		this.networkCallbacks.d();
	}

	public strictfp synchronized void sendServerInfoToAll() {
		for (Connection var2 : this.connections) {
			if (var2.connected) {
				this.sendServerInfo(var2);
			}
		}
	}

	public strictfp synchronized void sendServerInfo(Connection c) {
		if (!this.isServer) {
			this.logNetwork("sendServerInfo: we are not a server!");
		} else {
			OutputNetStream var2 = new OutputNetStream();

			try {
				var2.writeUTF("com.corrodinggames.rts");
				var2.writeInt(this.gameVersion);
				var2.writeEnum(this.gameSetup.mapType);
				if (this.chatOnlyServer) {
					var2.writeUTF("<CHAT ONLY>");
				} else {
					var2.writeUTF(this.gameSetup.mapName == null ? "<NULL>" : FileLoader.o(this.gameSetup.mapName));
				}

				var2.writeInt(this.gameSetup.startingCredits);
				var2.writeInt(this.gameSetup.fogMode);
				var2.writeBoolean(this.gameSetup.revealedMap);
				var2.writeInt(this.gameSetup.aiDifficulty);
				var2.writeByte(8);
				var2.writeBoolean(this.networkCallbacks.a(cx));
				boolean var3 = this.networkCallbacks.b(cx);
				var2.writeBoolean(var3);
				var2.writeInt(this.teamUnitCap);
				var2.writeInt(this.teamUnitCapMax);
				var2.writeInt(this.gameSetup.startingUnits);
				var2.writeFloat(this.gameSetup.incomeMultiplier);
				var2.writeBoolean(this.gameSetup.noNukes);
				var2.writeBoolean(this.gameSetup.j);
				if (this.chatOnlyServer) {
					var2.writeBoolean(false);
				} else {
					var2.writeBoolean(true);
					CustomUnitMetadata.writeCustomUnitsToStream(var2);
				}

				var2.writeBoolean(this.gameSetup.sharedControl);
				var2.writeBoolean(this.gameSetup.teamsLocked);
				var2.writeBoolean(this.gameSetup.enforceTwoTeams);
				var2.writeBoolean(this.gameSetup.allowSpectators);
				var2.writeBoolean(this.gameSetup.lockedRoom);
				var2.writeInt(this.gameSetup.randomSeed);
			} catch (IOException var4) {
				throw new RuntimeException(var4);
			}

			this.sendPacketOnConnection(cx, var2.createPacket(106));
		}
	}

	public strictfp synchronized void sendKick(Connection c, String string) {
		if (!this.isServer) {
			this.logNetwork("sendKick: we are not a server!");
		} else {
			this.logNetwork("kicking client reason:" + string);
			OutputNetStream var3 = new OutputNetStream();

			try {
				var3.writeUTF(string);
			} catch (IOException var5) {
				throw new RuntimeException(var5);
			}

			this.sendPacketOnConnection(cx, var3.createPacket(150));
		}
	}

	public strictfp synchronized void sendIncorrectPassword(Connection c) {
		if (!this.isServer) {
			this.logNetwork("sendIncorrectPassword: we are not a server!");
		} else {
			this.logNetwork("sendIncorrectPassword");
			OutputNetStream var2 = new OutputNetStream();

			try {
				var2.writeInt(0);
			} catch (IOException var4) {
				throw new RuntimeException(var4);
			}

			this.sendPacketOnConnection(cx, var2.createPacket(113));
		}
	}

	public strictfp void assignTeamDisplaySlots() {
		if (this.isServer) {
			for (int var1 = 0; var1 < Team.totalTeamSlots; var1++) {
				Team var2 = Team.getTeam(var1);
				if (var2 != null) {
					if (this.chatOnlyServer) {
						var2.sortOrder = 0;
					} else if (var2.isSpectator()) {
						var2.sortOrder = 100;
					} else {
						var2.sortOrder = var2.allyTeam;
					}

					if (var2.isSpectator()) {
						var2.displaySlot = -1;
					} else {
						int var3 = var2.getDefaultDisplaySlot();
						if (var2.colorOverride != null) {
							var3 = var2.colorOverride;
						} else if (this.isSlotUsedByOtherTeam(var3, null)) {
							var3 = -1;
						}

						var2.displaySlot = var3;
					}
				}
			}

			for (int var4 = 0; var4 < Team.totalTeamSlots; var4++) {
				Team var5 = Team.getTeam(var4);
				if (var5 != null && var5.displaySlot == -1 && !var5.isSpectator()) {
					var5.displaySlot = this.findFreeTeamSlot();
				}
			}
		}
	}

	public strictfp int findFreeTeamSlot() {
		for (int var1 = 0; var1 < 10; var1++) {
			if (!this.isSlotTaken(var1)) {
				return var1;
			}
		}

		return -1;
	}

	public strictfp boolean isSlotTaken(int integer) {
		for (int var2 = 0; var2 < Team.totalTeamSlots; var2++) {
			Team var3 = Team.getTeam(var2);
			if (var3 != null && var3.displaySlot == integer && !var3.isSpectator()) {
				return true;
			}
		}

		return false;
	}

	public strictfp boolean isSlotUsedByOtherTeam(int integer, Team n) {
		for (int var3 = 0; var3 < Team.totalTeamSlots; var3++) {
			Team var4 = Team.getTeam(var3);
			if (var4 != null && var4 != nx && var4.colorOverride != null && var4.colorOverride == integer && !var4.isSpectator()) {
				return true;
			}
		}

		return false;
	}

	public strictfp void updateTeamStatuses() {
		if (this.isServer) {
			long var1 = System.currentTimeMillis();
			int var3 = GameEngine.getInstance().gameTimeMillis;
			if (this.localTeam != null && !this.D) {
				this.localTeam.ping = -99;
				this.localTeam.lastPingTimeReceivedAt = var1;
			}

			this.assignTeamDisplaySlots();

			for (int var4 = 0; var4 < Team.maxTeamId; var4++) {
				Team var5 = Team.getTeam(var4);
				if (var5 != null) {
					boolean var6 = this.localTeam == var5;
					var5.setLocalPlayerTeamFlag(var6);
					if (!this.gameStarted) {
					}

					if (this.gameStarted && !this.singlePlayerServer && !var5.isAI) {
						boolean var7 = false;
						if (var5.isTimedOut()) {
							var7 = true;
						}

						long var8 = 60000L;
						if (var5.inactiveSinceGameTime > 180000) {
							var8 = 160000L;
						}

						boolean var10 = false;
						if (this.allPlayersReady) {
							if (var5.inactiveSince == -1L) {
								var5.inactiveSince = var1;
								var5.inactiveSinceGameTime = var3;
							}

							if ((this.pausedDueToDesync || this.gamePaused) && !var5.isAFK) {
								var5.inactiveSince = var1;
								var5.inactiveSinceGameTime = var3;
							}

							if (var5.inactiveSince + var8 < var1) {
								var10 = true;
							}
						}

						if (var5.isAFK != var10) {
							var5.isAFK = var10;
						}

						if (var10) {
							var7 = true;
							if (!var5.aa) {
								boolean var11 = var5.wipedOut || var5.defeated || var5.controlShared || var5.isSpectator();
								if (!var11) {
									var5.aa = true;
								}
							}
						}

						if (var5.controlShared != var7) {
							if (var7 && !var5.wipedOut && !var5.defeated && !var5.sharedControlEnabled && !var5.isSpectator()) {
								String var13 = "-t [Sharing control due to disconnect]";
								if (var10) {
									var13 = "-t [Sharing control due to afk]";
								}

								GameEngine.log(var5.name + " - " + var13);
								int var12 = Team.countTeamsInAllyGroup(var5.allyTeam, true);
								if (var12 > 1) {
									this.routeChatMessage(null, var5, var5.name, var13);
								}
							}

							var5.controlShared = var7;
						}
					}
				}
			}
		}
	}

	public strictfp void markPlayerListDirty() {
		if (this.playerListDirtySince == 0L) {
			this.playerListDirtySince = System.currentTimeMillis();
		}
	}

	public strictfp void resetPlayerListDirty() {
		this.playerListDirtySince = 0L;
		this.sendUpdatePlayer(null);
	}

	public strictfp void sendUpdatePlayer(Connection c) {
		if (!this.isServer) {
			this.logNetwork("sendUpdatePlayer: we are not a server!");
		} else {
			this.updateTeamStatuses();

			for (Connection var3 : this.connections) {
				if (var3.connected) {
					OutputNetStream var4 = new OutputNetStream(var3.version);

					try {
						var4.writeInt(var3.getPlayerPing());
						int var5 = Team.maxTeamId;
						boolean var6 = false;
						if (var4.getVersion() >= 90) {
							boolean var7 = false;
							if (var4.getVersion() >= 141) {
								var7 = true;
								if (this.gameStarted && var3.Q) {
									var6 = true;
								}

								var4.writeBoolean(var6);
							}

							var4.writeInt(var5);
							var4.startBlock("teams", var7);
						} else {
							var5 = 8;
							if (!this.chatOnlyServer) {
								this.logNetwork("sendUpdatePlayer: warning saving with lower team count");
							}
						}

						for (int var12 = 0; var12 < var5; var12++) {
							Team var8 = Team.getTeam(var12);
							var4.writeBoolean(var8 != null);
							if (var8 != null) {
								byte var9 = 0;
								if (var8 instanceof AI) {
									var9 = 1;
								}

								var4.writeInt((int)var9);
								if (var6) {
									var8.read(var4);
								} else {
									var8.writeTeamState(var4);
								}
							}
						}

						if (var4.getVersion() >= 90) {
							var4.endBlock("teams");
						}

						var4.writeInt(this.gameSetup.fogMode);
						var4.writeInt(this.gameSetup.startingCredits);
						var4.writeBoolean(this.gameSetup.revealedMap);
						var4.writeInt(this.gameSetup.aiDifficulty);
						var4.writeByte(5);
						var4.writeInt(this.teamUnitCap);
						var4.writeInt(this.teamUnitCapMax);
						var4.writeInt(this.gameSetup.startingUnits);
						var4.writeFloat(this.gameSetup.incomeMultiplier);
						var4.writeBoolean(this.gameSetup.noNukes);
						var4.writeBoolean(this.gameSetup.j);
						var4.writeBoolean(false);
						var4.writeBoolean(this.gameSetup.sharedControl);
						var4.writeBoolean(this.gamePaused);
					} catch (IOException var10) {
						throw new RuntimeException(var10);
					}

					short var11 = -1;
					if (cx == var3 && var3.version <= 26) {
						var11 = 1000;
					}

					var3.Q = true;
					this.sendPacketOnConnection(var3, var4.createPacketOnChannel(115, var11));
				}
			}
		}
	}

	public strictfp void sendReceivingProgress(Connection c, int integer2, int integer3) {
		OutputNetStream var4 = new OutputNetStream();

		try {
			var4.writeByte(0);
			var4.writeInt(integer2);
			var4.writeInt(integer3);
		} catch (IOException var6) {
			throw new RuntimeException(var6);
		}

		this.sendPacketOnConnection(cx, var4.createPacket(4));
	}

	public strictfp synchronized boolean startSandboxMode() {
		if (this.startSingleplayerServer()) {
			this.sandboxMode = true;
			this.gameSetup.fogMode = 0;
			return true;
		} else {
			return false;
		}
	}

	public strictfp synchronized boolean startSingleplayerServer() {
		if (this.networked) {
			this.disconnect("Started singleplayer");
		}

		GameEngine var1 = GameEngine.getInstance();
		this.resetNetworkStateKeepingTeams();
		this.networked = true;
		this.isServer = true;
		this.singlePlayerServer = true;
		this.gameSetup.mapType = var1.getMapType();
		this.gameSetup.mapName = var1.getMapFileName();
		this.generateNewServerId();
		this.localTeam = var1.playerTeam;
		MultiplayerBattleroomActivity.o();
		this.networkPort = var1.settings.networkPort;
		this.logNetwork("singleplayer server started");
		return true;
	}

	private strictfp void randomizeGameSeed() {
		this.gameSetup.randomSeed = CommonUtils.randomIntBetween(1, 1000000000);
	}

	public strictfp synchronized boolean startServer(boolean boolean1) {
		if (this.networked) {
			throw new RuntimeException("networking already started");
		} else {
			this.resetNetworkState();
			this.networked = true;
			this.isServer = true;
			this.generateNewServerId();
			this.randomizeGameSeed();
			GameEngine var2 = GameEngine.getInstance();
			this.setupLocalServerPlayer(boolean1);
			MultiplayerBattleroomActivity.o();
			this.networkPort = var2.settings.networkPort;
			SteamEngineDisabled.a().i();
			this.tcpListenRunnable = new NewConnectionRunnable(this);

			try {
				this.tcpListenRunnable.a(false);
			} catch (IOException var6) {
				var6.printStackTrace();
				var2.showAlert("Could not open tcp port:" + this.networkPort + ", check this port is not in use or change the port in the game settings", 1);
				this.disconnect("Could not open tcp port");
				return false;
			}

			this.tcpListenThread = new Thread(this.tcpListenRunnable);
			this.tcpListenThread.setDaemon(true);
			this.tcpListenThread.start();
			boolean var3 = true;
			if (var3) {
				this.udpListenRunnable = new NewConnectionRunnable(this);

				try {
					this.udpListenRunnable.a(true);
				} catch (IOException var5) {
					var5.printStackTrace();
					var2.showAlert("Could not open udp port:" + this.networkPort + ", check this port is not in use or change the port in the game settings", 1);
					this.disconnect("Could not open udp port");
					return false;
				}

				this.udpListenThread = new Thread(this.udpListenRunnable);
				this.udpListenThread.start();
			}

			this.updateMultiplayerNotifications();
			if (this.serverVisible) {
				MasterServerConnector.startCreateServerRequest();
			}

			this.portCheckInProgress = null;
			if (r) {
				MasterServerConnector.startGetOwnInfo();
			}

			this.logNetwork("server started");
			return true;
		}
	}

	public strictfp void setupLocalServerPlayer(boolean boolean1) {
		this.isServer = true;
		GameEngine var2 = GameEngine.getInstance();
		if (this.localTeam == null) {
			Player var4 = null;
			int var3;
			if (!boolean1) {
				var3 = Team.findFreeTeamId();
				if (var3 == -1) {
					throw new RuntimeException("playerId is -1 for server player");
				}
			} else {
				var4 = this.adminPlayer;
				var3 = this.adminPlayer.k;
			}

			if (var4 == null) {
				var4 = new Player(var3);
				var4.v = this.playerName;
				var2.playerTeam = var4;
			}

			this.localTeam = var4;
		}

		if (this.pingerTask == null) {
			GameEngine.log("pingerTask starting");
			this.pingerTask = new PingerTask(this);
			this.aH = new Timer();
			this.aH.schedule(this.pingerTask, 100L, 100L);
		} else {
			GameEngine.log("pingerTask already active");
		}

		MultiplayerBattleroomActivity.o();
	}

	public strictfp boolean isUdpMultiplayerEnabled() {
		GameEngine var1 = GameEngine.getInstance();
		return var1.settings.udpInMultiplayer;
	}

	public strictfp ConnectSocketToServerRunnable connectToServerAsync(String string, boolean boolean2, Runnable runnable) {
		ConnectSocketToServerRunnable var4 = new ConnectSocketToServerRunnable(string, boolean2, runnable);
		var4.start();
		return var4;
	}

	public static strictfp Socket connectToServer(String string, boolean boolean2) {
		GameEngine var2 = GameEngine.getInstance();
		GameEngine.log("Connect to server: " + string + " (force tcp:" + boolean2 + ")");
		boolean var3 = false;
		String var4 = string.trim();
		if (var4.startsWith("get|")) {
			String[] var29 = var4.split("\\|");

			String var35;
			int var40;
			boolean var41;
			try {
				String var32 = var29[0];
				var35 = var29[1];
				var40 = Integer.parseInt(var29[2]);
				var41 = Boolean.parseBoolean(var29[3]);
				int var43 = Integer.parseInt(var29[4]);
			} catch (NumberFormatException var21) {
				var21.printStackTrace();
				String var48 = "Bad server connect string";
				throw new IOException(var48);
			}

			if (var41) {
				var2.networkEngine.serverPassword = null;
				Object var11 = new Object();
				ad$1 var49 = new ad$1(var11);
				GameEngine.log("Asking for password..");
				synchronized (var11) {
					showPasswordPrompt(var49);

					try {
						var11.wait();
					} catch (InterruptedException var19) {
						var19.printStackTrace();
					}
				}

				if (var2.networkEngine.serverPassword == null) {
					GameEngine.logWarning("No password entered");
					throw new ConnectCancelledException();
				}

				GameEngine.log("Password has been entered");
			}

			String var44 = null;
			if (var41) {
				var44 = var2.networkEngine.serverPassword;
				if (var44 == null) {
					throw new IOException("This server requires a password but no password was provided");
				}
			}

			Object var50 = new Object();
			ad$2 var13 = new ad$2(var50);
			synchronized (var50) {
				MasterServerConnector.startGetGameServerInfo(var13, var35, var40, var44);

				try {
					var50.wait(15000L);
				} catch (InterruptedException var17) {
				}
			}

			if (var13.b != null) {
				throw new IOException(var13.b);
			} else if (var13.a == null) {
				throw new IOException("Failed to get game server info.");
			} else {
				return connectToServer(var13.a, boolean2);
			}
		} else {
			if (var4.toLowerCase(Locale.ENGLISH).endsWith(".relay")) {
				var4 = var4 + ".corrodinggames.com";
			}

			if (var4.startsWith("[TCP]")) {
				var4 = var4.substring("[TCP]".length());
				boolean2 = true;
			}

			if (var4.length() > 4 && !var4.contains(":") && !var4.contains(".") && !var4.equals("localhost") && !var4.contains("/") && !var4.contains("\\")) {
				String var5 = ".relay.corrodinggames.com";
				String var6 = "" + var4.charAt(0);
				String var7 = var6 + var5 + "/" + var4;
				GameEngine.log("Converting connect string to: " + var7);
				var4 = var7;
			}

			var2.networkEngine.queryString = null;
			if (var4.contains("/") || var4.contains("\\")) {
				int var26 = var4.indexOf("/");
				int var30 = var4.indexOf("\\");
				if (var26 == -1) {
					var26 = var4.length();
				}

				if (var30 == -1) {
					var30 = var4.length();
				}

				int var33 = CommonUtils.min(var26, var30);
				String var8 = var4.substring(var33 + 1);
				var8 = var8.trim();
				if (!var8.equals("")) {
					var2.networkEngine.queryString = var8;
				}

				var4 = var4.substring(0, var33);
			}

			String var27 = var4;
			int var31 = 5123;
			String[] var34 = var4.split(":");
			if (var34.length > 1) {
				var27 = null;

				for (int var37 = 0; var37 < var34.length - 1; var37++) {
					if (var27 == null) {
						var27 = "";
					} else {
						var27 = var27 + ":";
					}

					var27 = var27 + var34[var37];
				}

				String var38 = var34[var34.length - 1];

				try {
					var31 = Integer.parseInt(var38);
				} catch (NumberFormatException var23) {
					String var10 = "Bad port number:" + var38;
					var23.printStackTrace();
					throw new IOException(var10);
				}
			}

			if (!boolean2 && var2.networkEngine.isUdpMultiplayerEnabled()) {
				var3 = true;
			}

			short var9 = 7000;
			GameEngine.log("");
			GameEngine.log("===============================");
			GameEngine.log("Connect to: " + var4);
			Object var39;
			if (!var3) {
				var39 = new Socket();
				GameEngine.log("connecting to Server.. (tcp)");
			} else {
				var39 = new ReliableSocket();
				GameEngine.log("connecting to Server.. (udp)");
				var9 = 5000;
			}

			var39.setTcpNoDelay(true);

			InetSocketAddress var42;
			try {
				var42 = new InetSocketAddress(InetAddress.getByName(var27), var31);
			} catch (IllegalArgumentException var22) {
				String var12 = "Incorrect server format";
				GameEngine.logWarning("IllegalArgumentException.." + var12);
				var22.printStackTrace();
				throw new IOException(var12, var22);
			}

			try {
				var39.connect(var42, var9);
				return (Socket)var39;
			} catch (UnknownHostException var24) {
				String var47 = "Failed to connect to host";
				if (var3) {
					var47 = var47 + " (udp)";
				}

				GameEngine.log("UnknownHostException.." + var47);
				var24.printStackTrace();
				throw new IOException(var47, var24);
			} catch (IOException var25) {
				String var45 = "Failed to connect to host";
				if (var3) {
					var45 = var45 + " (udp)";
				}

				var45 = var45 + " - " + var25.getMessage();
				GameEngine.log("IOException.." + var45);
				var25.printStackTrace();
				throw new IOException(var45, var25);
			}
		}
	}

	public strictfp void showReconnectDialog() {
		GameEngine var1 = GameEngine.getInstance();
		PopupDialog var2 = PopupDialog.a(
			LocaleEngine.a("menus.ingame.multiplayerReconnect.message"), false
		);
		var2.a(LocaleEngine.a("menus.ingame.resume"), new ad$3(this, var2));
		var2.a(LocaleEngine.a("menus.ingame.reconnect"), new ad$4(this, var2));
		var2.a(LocaleEngine.a("menus.ingame.disconnect"), new ad$5(this, var2, var1));
		var1.interfaceEngine.showPopupDialog(var2);
		this.reconnectDialogShown = true;
	}

	public strictfp synchronized boolean reconnectToServer() {
		Socket var1 = this.lastConnectedSocket;
		if (var1 == null) {
			GameEngine.log("reconnectToServer: lastConnectedTo==null");
			return false;
		} else {
			GameEngine.log("reconnectToServer attempted");
			if (this.networked) {
				GameEngine.log("reconnectToServer: disconnecting");
				this.disconnect("reconnecting");
			}

			if (var1.getInetAddress() == null) {
				GameEngine.log("reconnectToServer: lastConnectedTo.getInetAddress()==null");
				return false;
			} else {
				String var2 = var1.getInetAddress().getHostAddress();
				int var3 = var1.getPort();
				String var4 = var2 + ":" + var3;
				GameEngine.log("reconnectToServer: connecting to: " + var4);

				try {
					boolean var6 = false;
					Socket var5 = connectToServer(var4, var6);
					return this.startClientConnection(var5);
				} catch (IOException var8) {
					var8.printStackTrace();
					return false;
				} catch (ConnectCancelledException var9) {
					var9.printStackTrace();
					return false;
				}
			}
		}
	}

	public strictfp synchronized boolean startClientConnection(Socket socket) {
		if (this.networked) {
			this.disconnect("starting new");
		}

		if (socket == null) {
			throw new RuntimeException("connectedSocket==null");
		} else {
			this.resetNetworkState();
			GameEngine var2 = GameEngine.getInstance();
			this.networkPort = socket.getPort();
			this.networked = true;
			this.isServer = false;
			this.logNetwork("connected to Server..");
			Connection var3 = new Connection(this, socket);
			var3.connected = true;
			var3.startWorkers();
			this.connections.add(var3);
			this.sendServerInfoRequest(var3);
			this.updateMultiplayerNotifications();
			this.lastConnectedSocket = socket;
			return true;
		}
	}

	public strictfp Connection getConnectionForTeam(Team n) {
		for (Connection var3 : this.connections) {
			if (var3.player == nx) {
				return var3;
			}
		}

		return null;
	}

	public strictfp Connection getActiveConnectionForTeam(Team n) {
		for (Connection var3 : this.connections) {
			if (!var3.closed && var3.player == nx) {
				return var3;
			}
		}

		return null;
	}

	public strictfp Connection getClientConnection() {
		if (this.isServer) {
			return null;
		} else {
			for (Connection var2 : this.connections) {
				if (!var2.closed) {
					return var2;
				}
			}

			return null;
		}
	}

	public strictfp void sendPacketToAll(Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketToAll, not networked");
		} else {
			this.sendPacketToAllClients(aux);
		}
	}

	private strictfp void sendPacketToAllClients(Packet au) {
		for (Connection var3 : this.connections) {
			if (var3.connected && !var3.closed && !var3.isRelay) {
				var3.sendPacket(aux);
			}
		}
	}

	public strictfp void sendPacketToAllIncludingRelay(Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketToAllIncludingRelay, not networked");
		} else {
			for (Connection var3 : this.connections) {
				if (var3.connected && !var3.closed) {
					var3.sendPacket(aux);
				}
			}
		}
	}

	public strictfp void sendPacketToServer(Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketToServer, not networked");
		} else if (this.isServer) {
			throw new RuntimeException("We are a server");
		} else {
			this.sendPacketToAll(aux);
		}
	}

	public strictfp void sendPacketToClients(Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketToClients, not networked");
		} else if (!this.isServer) {
			throw new RuntimeException("We are not a server");
		} else {
			this.sendPacketToAllIncludingRelay(aux);
		}
	}

	public strictfp void sendPacketToClientsNonRelay(Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketToClients, not networked");
		} else if (!this.isServer) {
			throw new RuntimeException("We are not a server");
		} else {
			this.sendPacketToAll(aux);
		}
	}

	public strictfp void sendPacketOnConnection(Connection c, Packet au) {
		if (!this.networked) {
			GameEngine.log("Skipping sendPacketOnConnection, not networked");
		} else {
			cx.sendPacket(aux);
		}
	}

	public strictfp void sendRegisterConnectionToAll() {
		if (this.isServer) {
			this.logNetwork("registerConnection: We are a server");
		}

		for (Connection var2 : this.connections) {
			this.sendRegisterConnection(var2);
		}
	}

	public strictfp void regenerateClientId() {
		GameEngine var1 = GameEngine.getInstance();
		var1.settings.networkClientId = null;
		if (this.serverUUID == null) {
			GameEngine.log("generateNewClientId: serverUUID==null");
			this.serverUUID = "x";
		}

		this.getOwnClientIdHashed();
		var1.settings.save();
	}

	public strictfp String getOwnClientIdHashed() {
		GameEngine var1 = GameEngine.getInstance();
		boolean var2 = false;
		if (var1.settings.networkClientId == null) {
			var2 = true;
		}

		if (!this.machineKeyChecked) {
			this.machineKeyChecked = true;
			if (GameEngine.isDesktopVersion()) {
				String var3 = this.getMachineKey();
				if (!var3.equals(var1.settings.networkClientIdMachineKey)) {
					if (var1.settings.networkClientIdMachineKey != null) {
						GameEngine.log("Machine appears to have changed: " + var1.settings.networkClientIdMachineKey + " vs " + var3);
					}

					var1.settings.networkClientIdMachineKey = var3;
					var2 = true;
				}
			}
		}

		if (var2) {
			GameEngine.log("new networkClientId needed");
			var1.settings.networkClientId = UUID.randomUUID().toString();
			var1.settings.save();
		}

		String var5 = var1.settings.networkClientId;
		if (this.serverUUID == null) {
			throw new RuntimeException("getOwnClientIdHashed: serverUUID==null");
		} else {
			return CommonUtils.sha256Hex(var5 + this.serverUUID);
		}
	}

	public strictfp void generateNewServerId() {
		GameEngine var1 = GameEngine.getInstance();
		var1.settings.networkServerId = UUID.randomUUID().toString();
		var1.settings.save();
	}

	public strictfp String getServerId() {
		GameEngine var1 = GameEngine.getInstance();
		if (var1.settings.networkServerId == null) {
			this.generateNewServerId();
		}

		return var1.settings.networkServerId;
	}

	public strictfp String getCurrentServerId() {
		GameEngine var1 = GameEngine.getInstance();
		return this.isServer ? var1.settings.networkServerId : this.serverUUID;
	}

	public strictfp void sendServerInfoRequest(Connection c) {
		OutputNetStream var2 = new OutputNetStream();

		try {
			byte var3 = 4;
			byte var4 = 1;
			if (GameEngine.isDesktopVersion()) {
				var4 = 2;
			}

			if (GameEngine.isIOSPlatform) {
				var4 = 3;
			}

			var2.writeUTF("com.corrodinggames.rts");
			var2.writeInt((int)var3);
			var2.writeInt(this.gameVersion);
			var2.writeInt((int)var4);
			var2.writeNullableUTF(this.queryString);
			var2.writeUTF(this.playerName);
			var2.writeUTF(LocaleEngine.c());
			String var5 = "";
			if (GameEngine.aT) {
				var5 = var5 + "d";
			}

			var2.writeUTF(var5);
		} catch (IOException var6) {
			throw new RuntimeException(var6);
		}

		this.sendPacketOnConnection(cx, var2.createPacket(160));
	}

	public strictfp void sendPreregisterInfo(Connection c) {
		OutputNetStream var2 = new OutputNetStream();

		try {
			GameEngine var3 = GameEngine.getInstance();
			var2.writeUTF("com.corrodinggames.rts");
			var2.writeInt(2);
			var2.writeInt(this.gameVersion);
			var2.writeInt(var3.getVersionCode(true));
			var2.writeUTF(var3.getGameTitle());
			var2.writeUTF(this.getServerId());
			var2.writeInt(cx.integrityCheckValue);
			var2.writeInt(this.extraChallengeSeed);
			var2.writeInt(0);
		} catch (IOException var4) {
			throw new RuntimeException(var4);
		}

		this.sendPacketOnConnection(cx, var2.createPacket(161));
	}

	public strictfp void sendRegisterConnection(Connection c) {
		GameEngine.log("sendRegisterConnection...");
		OutputNetStream var2 = new OutputNetStream();

		try {
			var2.writeUTF("com.corrodinggames.rts");
			var2.writeInt(5);
			var2.writeInt(this.gameVersion);
			GameEngine var3 = GameEngine.getInstance();
			var2.writeInt(var3.getVersionCode(true));
			var2.writeUTF(this.playerName);
			String var4 = null;
			if (this.serverPassword != null) {
				var4 = CommonUtils.sha256Hex(this.serverPassword);
			}

			var2.writeNullableUTF(var4);
			var2.writeUTF(var3.getGameTitle());
			var2.writeUTF(this.getOwnClientIdHashed());
			var2.writeInt(var3.getNetworkPort());
			var2.writeUTF(this.getIntegrityResponse(this.integrityChallenge));
			var2.writeUTF(this.getExtraChallengeResponse(this.extraChallenge));
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.sendPacketOnConnection(cx, var2.createPacket(110));
		this.registerConnectionSent = true;
	}

	public strictfp String getIntegrityResponse(int integer) {
		String var2 = "";
		var2 = var2 + "c:" + integer;
		var2 = var2 + "m:" + (integer * 87 + 24);
		var2 = var2 + "0:" + this.getStartingCreditsOptionValue(0) * 11 * integer;
		var2 = var2 + "1:" + (this.getStartingCreditsOptionValue(1) * 12 + integer);
		var2 = var2 + "2:" + this.getStartingCreditsOptionValue(2) * 13 * integer;
		var2 = var2 + "3:" + (this.getStartingCreditsOptionValue(3) * 14 + integer);
		var2 = var2 + "4:" + this.getStartingCreditsOptionValue(4) * 15 * integer;
		var2 = var2 + "5:" + (this.getStartingCreditsOptionValue(5) * 16 + integer);
		var2 = var2 + "6:" + this.getStartingCreditsOptionValue(6) * 17 * integer;
		var2 = var2 + "7:" + this.getStartingCreditsOptionValue(7) * 18 * integer;
		var2 = var2 + "8:" + this.getStartingCreditsOptionValue(8) * 19 * integer;
		var2 = var2 + "t1:" + Team.nullTeam.credits * 11.0 * integer;
		int var3 = 5 * integer;
		if (this.getCurrentStartingCredits() != this.getStartingCreditsOptionValue(this.gameSetup.startingCredits)) {
			var3 = 7 * integer;
		}

		return var2 + "d:" + var3;
	}

	public strictfp String getExtraChallengeResponse(int integer) {
		return CommonUtils.colorToHexString(integer);
	}

	public strictfp void sendClientReadyState() {
		if (this.isServer) {
			throw new RuntimeException("We are a server");
		} else {
			GameEngine var1 = GameEngine.getInstance();
			OutputNetStream var2 = new OutputNetStream();

			try {
				var2.writeBoolean(this.bG);
				var2.writeBoolean(var1.gameLoopActive);
			} catch (IOException var4) {
				throw new RuntimeException(var4);
			}

			this.sendPacketToServer(var2.createPacket(112));
		}
	}

	public strictfp void sendSystemMessage(String string) {
		if (!this.isServer) {
			this.logNetwork("cannot send sendSystemMessage:" + string + ", we are not a server");
		} else if (this.networked && !this.singlePlayerServer) {
			GameEngine.log("sendSystemMessage:" + string);
			this.routeChatMessage(null, null, null, string);
		} else {
			this.logNetwork("cannot send sendSystemMessage:" + string + ", not networked");
		}
	}

	public strictfp void sendChatCommand(String string) {
		this.sendChatMessage("-qc " + string);
	}

	public strictfp void l(String string) {
		boolean var2 = true;
		String var3 = null;
		if (string != null) {
			String var4 = string.trim();
			if ((var4.startsWith("-") || var4.startsWith(".") || var4.startsWith("_")) && var4.length() >= 2) {
				String var5 = var4.substring(1).trim();
				int var6 = var5.indexOf(" ");
				if (var6 == -1) {
					var6 = var5.length();
				}

				var3 = var5.substring(0, var6).toLowerCase(Locale.ENGLISH);
			}
		}

		if ("share".equals(var3)) {
			var2 = false;
		}

		if ("t".equals(var3)) {
			var2 = false;
		}

		if (var2) {
			string = "-t " + string;
		}

		this.sendChatMessage(string);
	}

	public strictfp void sendChatMessage(String string) {
		if (!this.networked) {
			GameEngine.log("sendChatMessage: not networked:" + string);
			this.receiveChatMessage(null, -1, null, string);
		} else if (this.isServer) {
			this.routeChatMessage(null, this.localTeam, this.playerName, string);
			this.handleChatCommand(null, this.localTeam, this.playerName, string);
		} else {
			try {
				OutputNetStream var2 = new OutputNetStream();
				var2.writeUTF(string);
				var2.writeByte(0);
				this.sendPacketToServer(var2.createPacket(140));
			} catch (IOException var3) {
				throw new RuntimeException(var3);
			}
		}
	}

	public strictfp void routeChatMessage(Connection c, Team n, String string3, String string4) {
		this.routeChatMessageViaRelay(cx, nx, string3, string4, null);
	}

	public strictfp void routeChatMessageViaRelay(Connection c1, Team n, String string3, String string4, Connection c5) {
		try {
			boolean var6 = false;
			boolean var7 = false;
			String var8 = parseChatCommand(string4);
			if ("t".equalsIgnoreCase(var8)) {
				if (nx != null) {
					var6 = true;
					string4 = string4.substring("-t".length());
					string4 = "[TEAM] " + string4;
				} else {
					GameEngine.logWarning("toOnlyTeams failed team==null");
				}
			}

			if (nx != null && "surrender".equalsIgnoreCase(var8)) {
				var6 = true;
				string4 = "[TEAM] " + string4;
			}

			if (nx != null && "i".equalsIgnoreCase(var8)) {
				var7 = true;
				string4 = string4.substring("-i".length());
				string4 = "[INFO] " + string4;
			}

			if (nx != null && "qc".equalsIgnoreCase(var8)) {
				var7 = true;
				string4 = string4.substring("-qc".length());
				string4 = "[COMMAND] " + string4;
			}

			if (!var7 && nx != null && nx != this.spectatorPlayer && nx != this.adminPlayer && !this.networkCallbacks.a(c1, nx, string4, var6)) {
				var7 = true;
			}

			OutputNetStream var9 = new OutputNetStream();
			var9.writeUTF(string4);
			var9.writeByte(3);
			var9.writeNullableUTF(string3);
			var9.writeConnectionId(c1);
			int var10 = -1;
			if (nx != null) {
				var10 = nx.teamId;
			}

			var9.writeInt(var10);
			Packet var11 = var9.createPacket(141);
			if (var6) {
				for (Connection var13 : this.connections) {
					if (var13.connected && !var13.closed) {
						Player var14 = var13.player;
						if (var14 != null && var14.d(nx)) {
							var13.sendPacket(var11);
						}
					}
				}

				Team var19 = this.localTeam;
				if (var19 != null && var19.isSameTeam(nx)) {
					this.receiveChatMessage(c1, var10, string3, string4);
				}
			} else if (var7) {
				GameEngine.logWarning("info message:" + formatChatMessage(string3, string4));
			} else {
				if (c5 != null) {
					this.sendPacketOnConnection(c5, var11);
				} else {
					this.sendPacketToClients(var11);
				}

				this.receiveChatMessage(c1, var10, string3, string4);
			}
		} catch (IOException var15) {
			throw new RuntimeException(var15);
		}
	}

	public static strictfp String parseChatCommand(String string) {
		if (string == null) {
			return null;
		} else {
			String var1 = string.trim();
			if ((var1.startsWith("-") || var1.startsWith(".") || var1.startsWith("_")) && var1.length() >= 2) {
				String var2 = var1.substring(1).trim();
				int var3 = var2.indexOf(" ");
				if (var3 == -1) {
					var3 = var2.length();
				}

				return var2.substring(0, var3).toLowerCase(Locale.ENGLISH);
			} else {
				return null;
			}
		}
	}

	public static strictfp String formatChatMessage(String string1, String string2) {
		return string1 != null ? string1 + ": " + string2 : string2;
	}

	public strictfp void addLocalMessage(String string) {
		string = LocaleEngine.c(string);
		byte var2 = -1;
		Object var3 = null;
		Object var4 = null;
		this.chatLog.addMessage(var2, (String)var3, string, (Connection)var4);
		this.networkCallbacks.a(var2, (String)var3, string, (Connection)var4);
		boolean var5 = false;
		if (this.gameStarted) {
			var5 = true;
		}

		if (!this.networked) {
			var5 = true;
		}

		if (var5) {
			showGameMessage((String)var3, string);
		} else {
			String var6 = formatChatMessage((String)var3, string);
			if (!GameEngine.isDedicatedServerPlatform) {
				MultiplayerBattleroomActivity.d(var6);
			}
		}
	}

	private strictfp void receiveChatMessage(Connection c, int integer, String string3, String string4) {
		if (this.networked || !string4.startsWith("-i ")) {
			if (this.networked || !string4.startsWith("-qc ")) {
				string4 = LocaleEngine.c(string4);
				if (string3 != null) {
					boolean var5 = true;
					if (string4 != null) {
						if (string4.equals("-surrender")) {
						}

						if (this.localTeam != null && integer >= 0 && this.localTeam.teamId == integer) {
						}
					}

					if (var5) {
						this.showChatNotification("New Message", string3 + ": " + string4);
					}
				}

				Connection var9 = null;
				if (this.isServer) {
					var9 = cx;
				}

				this.chatLog.addMessage(integer, string3, string4, var9);
				this.networkCallbacks.a(integer, string3, string4, cx);
				boolean var6 = false;
				if (this.gameStarted) {
					var6 = true;
				}

				if (!this.networked) {
					var6 = true;
				}

				if (var6) {
					showGameMessage(string3, string4);
				} else {
					String var7 = formatChatMessage(string3, string4);
					if (!GameEngine.isDedicatedServerPlatform) {
						MultiplayerBattleroomActivity.d(var7);
					}
				}
			}
		}
	}

	public strictfp void sendResyncSaveToConnection(Connection c, byte[] arr, boolean boolean3, boolean boolean4) {
		GameEngine var5 = GameEngine.getInstance();

		try {
			OutputNetStream var6 = new OutputNetStream();
			var6.writeByte(0);
			var6.writeInt(var5.gameTimeSeconds);
			var6.writeInt(var5.gameTimeMillis);
			var6.writeFloat(this.getCurrentStepRate());
			var6.writeFloat(1.0F);
			var6.writeBoolean(boolean3);
			var6.writeBoolean(boolean4);
			var6.startBlock("gameSave");
			var6.writeBytes(arr);
			var6.endBlock("gameSave");
			Packet var7 = var6.createPacket(35);
			this.sendPacketOnConnection(cx, var7);
		} catch (IOException var8) {
			throw new RuntimeException(var8);
		}
	}

	public strictfp void sendResyncSave(boolean boolean1, boolean boolean2, boolean boolean3) {
		GameEngine var4 = GameEngine.getInstance();

		try {
			OutputNetStream var5 = new OutputNetStream();
			var5.writeByte(0);
			var5.writeInt(var4.gameTimeSeconds);
			var5.writeInt(var4.gameTimeMillis);
			var5.writeFloat(this.getCurrentStepRate());
			var5.writeFloat(1.0F);
			var5.writeBoolean(boolean1);
			var5.writeBoolean(boolean2);
			var5.startBlock("gameSave");
			var4.gameSaver.saveGame(var5);
			var5.endBlock("gameSave");
			if (boolean1) {
			}

			Packet var6 = var5.createPacket(35);
			this.sendPacketToAll(var6);
			if (boolean3) {
				if (!this.isServer) {
					throw new RuntimeException("sendResyncSave: reloadCreatedSave: We are not a server");
				}

				var6.connection = this.localConnection;
				this.processGamePacket(var6);
			}
		} catch (IOException var7) {
			throw new RuntimeException(var7);
		}
	}

	public strictfp boolean startGame() {
		this.resetPlayerListDirty();
		this.sendServerInfoToAll();
		return this.sendStartGame(null, false);
	}

	public strictfp boolean sendStartGame(Connection c, boolean boolean2) {
		GameEngine.log("Sending start game....");
		if (!this.isServer) {
			throw new RuntimeException("We are not a server");
		} else {
			GameEngine var3 = GameEngine.getInstance();
			OutputNetStream var4 = new OutputNetStream();

			try {
				var4.writeByte(0);
				var4.writeEnum(this.gameSetup.mapType);
				if (this.gameSetup.mapType == GameSetupMapType.savedGame) {
					try {
						var3.gameSaver.writeSaveFileToStream(this.gameSetup.mapName, var4);
					} catch (IOException var7) {
						var7.printStackTrace();
						var3.showMessageBox("Map error starting game", "Map error: " + var7.getMessage());
						return false;
					}

					var4.writeUTF("SAVE:" + this.gameSetup.mapName);
				} else if (this.gameSetup.mapType == GameSetupMapType.customMap) {
					GameEngine.log("Starting with custom map: " + this.getMapDisplayName());

					try {
						Map.a(this.mapName, var4);
					} catch (IOException var6) {
						var6.printStackTrace();
						var3.showMessageBox("Map error starting game", "Map error: " + var6.getMessage());
						return false;
					}

					var4.writeUTF("STEAM:" + this.getMapDisplayName());
				} else {
					var4.writeUTF(this.getMapDisplayName());
				}

				var4.writeBoolean(boolean2);
			} catch (IOException var8) {
				throw new RuntimeException(var8);
			}

			Packet var5 = var4.createPacket(120);
			if (cx == null) {
				this.sendPacketToClients(var5);
			} else {
				this.sendPacketOnConnection(cx, var5);
			}

			if (!this.gameStarted) {
				this.startNetworkGame();
			}

			return true;
		}
	}

	public strictfp void onStartGameFailed() {
		this.startGameFailed = true;
		GameEngine.log("onStartGameFailed");
		if (this.isServer) {
			this.gameStarted = false;
			this.sendSystemMessage("Map load failed.");
		} else {
			this.disconnect("Map load failed");
		}
	}

	private strictfp void startNetworkGame() {
		this.returnToBattleroomPending = false;
		this.gameStarted = true;
		this.startGameFailed = false;
		this.bd = false;
		GameEngine.log("Starting new network game (" + this.getCurrentServerId() + ")");
		if (this.serverVisible && this.isServer) {
			MasterServerConnector.startUpdateServerRequest();
		}

		if (!GameEngine.isDedicatedServerPlatform) {
			MultiplayerBattleroomActivity.p();
		}

		this.networkCallbacks.startGameEvent();
	}

	public strictfp void scheduleReturnToBattleroomSoon() {
		this.scheduleReturnToBattleroom(5.0F);
	}

	public strictfp void scheduleReturnToBattleroom(float float1) {
		if (!this.isServer) {
			throw new RuntimeException("We are not a server");
		} else if (!this.returnTimerActive) {
			GameEngine.log("Setting up return to battleroom timer...");
			this.returnToBattleroomCountdown = float1;
			this.returnTimerActive = true;
			this.sendSystemMessage("Game ended by host. Returning to battleroom in " + (int)float1 + " seconds...");
		}
	}

	public strictfp void sendReturnToBattleroom(Connection c) {
		if (!this.isServer) {
			throw new RuntimeException("We are not a server");
		} else {
			try {
				OutputNetStream var2 = new OutputNetStream();
				var2.writeByte(0);
				Packet var3 = var2.createPacket(122);
				if (cx == null) {
					this.sendPacketToClientsNonRelay(var3);
				} else {
					this.sendPacketOnConnection(cx, var3);
				}
			} catch (IOException var4) {
				throw new RuntimeException(var4);
			}

			this.queueReturnToBattleroom();
		}
	}

	private strictfp void queueReturnToBattleroom() {
		this.returnToBattleroomPending = true;
	}

	private strictfp void returnToBattleroom() {
		GameEngine.log("----- returnToBattleroom -----");
		this.returnToBattleroomPending = false;
		GameEngine var1 = GameEngine.getInstance();
		var1.replayEngine.stop();
		Team var2 = this.localTeam;
		var1.renderGame();
		this.resetGameNetworkState();
		this.localTeam = var2;
		var1.gameTimeSeconds = 0;
		var1.gameTimeMillis = 0;
		this.clearClientReadyFlags();
		Team.resetAllTeams();
		if (this.isServer) {
			this.randomizeGameSeed();
		}

		this.openMenuOption21();
		if (this.serverVisible && this.isServer) {
			MasterServerConnector.startUpdateServerRequest();
		}

		if (!GameEngine.isDedicatedServerPlatform) {
		}
	}

	public strictfp String getLocalIpAddress() {
		ArrayList var1 = this.getLocalIpAddressList();
		return var1 != null && var1.size() != 0 ? (String)var1.get(0) : null;
	}

	public strictfp String getLocalIpAddressesString() {
		ArrayList var1 = this.getLocalIpAddressList();
		if (var1 != null && var1.size() != 0) {
			String var2 = "";
			boolean var3 = true;

			for (String var5 : var1) {
				if (var3) {
					var3 = false;
				} else {
					var2 = var2 + ", ";
				}

				var2 = var2 + var5;
			}

			return var2;
		} else {
			return null;
		}
	}

	public strictfp ArrayList getLocalIpAddressList() {
		if (localIpAddressCache != null) {
			return new ArrayList(localIpAddressCache);
		} else {
			long var1 = TimingStatTimer.timeNow();
			ArrayList var3 = null;
			ArrayList var4 = this.getLocalIpAddresses(true);
			if (var4 != null && var4.size() > 0) {
				var3 = var4;
			} else {
				var3 = this.getLocalIpAddresses(false);
			}

			double var5 = TimingStatTimer.getElapsedMillis(var1);
			if (var5 > 2.0) {
				GameEngine.logWarning("getLocalIpAddressList was slow, taking:" + TimingStatTimer.formatMillis(var5));
			}

			if (var5 > 10.0 && var3 != null && var3.size() > 0) {
				GameEngine.log("getLocalIpAddressList: creating cache");
				localIpAddressCache = new ArrayList(var3);
			}

			return var3;
		}
	}

	public strictfp String getMachineKey() {
		String var1 = null;

		try {
			Enumeration var2 = NetworkInterface.getNetworkInterfaces();

			while (var2.hasMoreElements()) {
				NetworkInterface var3 = (NetworkInterface)var2.nextElement();
				byte[] var4 = var3.getHardwareAddress();
				if (var4 != null) {
					String var5 = new String(var4);
					var5 = var5.trim();
					if (var5.length() > 2) {
						var1 = var5;
						break;
					}
				}
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

		return var1 != null ? CommonUtils.sha256Hex14(var1) : "[blank]";
	}

	public strictfp ArrayList getLocalIpAddresses(boolean boolean1) {
		ArrayList var2 = new ArrayList();

		try {
			Enumeration var3 = NetworkInterface.getNetworkInterfaces();

			while (var3.hasMoreElements()) {
				NetworkInterface var4 = (NetworkInterface)var3.nextElement();
				Enumeration var5 = var4.getInetAddresses();

				while (var5.hasMoreElements()) {
					InetAddress var6 = (InetAddress)var5.nextElement();
					if (!var6.isLoopbackAddress()) {
						String var7 = var6.getHostAddress().toString();
						if (!var7.contains("%")) {
							if (!boolean1) {
								var2.add(var7);
							} else if (var7.contains(".")) {
								var2.add(var7);
							}
						}
					}
				}
			}
		} catch (SocketException var8) {
			Log.d("RustedWarfare", var8.toString());
		}

		return var2;
	}

	strictfp InetAddress getBroadcastAddress() {
		try {
			GameEngine var1 = GameEngine.getInstance();
			WifiManager var2 = (WifiManager)var1.context.c("wifi");
			DhcpInfo var3 = var2.getDhcpInfo();
			int var4 = var3.ipAddress & var3.netmask | ~var3.netmask;
			byte[] var5 = new byte[4];

			for (int var6 = 0; var6 < 4; var6++) {
				var5[var6] = (byte)(var4 >> var6 * 8 & 0xFF);
			}

			return InetAddress.getByAddress(var5);
		} catch (UnknownHostException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	public strictfp void showChatNotification(String string1, String string2) {
		if (!GameEngine.isDedicatedServerPlatform) {
			GameEngine var3 = GameEngine.getInstance();
			if (!this.singlePlayerServer && !var3.replayEngine.isPlayingReplay()) {
				boolean var4 = MultiplayerBattleroomActivity.l();
				GameViewInterface var5 = var3.gameView;
				if (var5 != null && !var5.e()) {
					var4 = true;
				}

				if (var4) {
					if (this.chatNotificationActive) {
						this.cancelNotification(2);
					}
				} else {
					NotificationManager var6 = (NotificationManager)var3.context.c("notification");
					Intent var7 = new Intent(var3.context, com.corrodinggames.rts.appFramework.a.class);
					PendingIntent var8 = PendingIntent.getActivity(var3.context, 0, var7, 2);
					if (VERSION.SDK_INT >= 11) {
						Builder var9 = new Builder(var3.context);
						var9.setContentTitle("Rusted Warfare Multiplayer");
						var9.setContentText(string1 + ": " + string2);
						var9.setSmallIcon(drawable.icon);
						var9.setContentIntent(var8);
						var9.setOngoing(false);
						var9.setAutoCancel(true);
						this.createNotificationChannels(var6);
						this.setNotificationChannel(var9, "multiplayerChatId");
						Notification var10 = var9.getNotification();
						var6.notify(2, var10);
						this.chatNotificationActive = true;
					}
				}
			}
		}
	}

	public strictfp void updateMultiplayerNotifications() {
		GameEngine var1 = GameEngine.getInstance();
		if (this.networked && var1 != null && var1.isNetworkedClient()) {
			this.showGameInProgressNotification();
		} else {
			this.cancelNotification(1);
			this.cancelNotification(2);
		}
	}

	private strictfp void setNotificationChannel(Builder builder, String string) {
		if (VERSION.SDK_INT >= 26) {
			try {
				Method var3 = builder.getClass().getDeclaredMethod("setChannelId", String.class);
				var3.invoke(builder, string);
			} catch (Exception var4) {
				GameEngine.logException("setChannelId failed", (Throwable)var4);
			}
		}
	}

	private strictfp void createNotificationChannels(NotificationManager notificationManager) {
		this.createNotificationChannel(notificationManager, "multiplayerChatId", "Multiplayer Chat");
		this.createNotificationChannel(notificationManager, "multiplayerStatusId", "Multiplayer Status");
	}

	private strictfp void createNotificationChannel(NotificationManager notificationManager, String string2, String string3) {
		if (VERSION.SDK_INT >= 26) {
			byte var4 = 3;

			try {
				Class var5 = Class.forName("android.app.NotificationChannel");
				Constructor var6 = var5.getDeclaredConstructor(String.class, CharSequence.class, int.class);
				Object var7 = var6.newInstance(string2, string3, Integer.valueOf(var4));
				Method var8 = notificationManager.getClass().getDeclaredMethod("createNotificationChannel", var5);
				var8.invoke(notificationManager, var7);
			} catch (Exception var9) {
				GameEngine.logException("Creating notification channel failed", (Throwable)var9);
			}
		}
	}

	private strictfp void showGameInProgressNotification() {
		if (!GameEngine.isDedicatedServerPlatform) {
			GameEngine var1 = GameEngine.getInstance();
			Intent var2 = new Intent(var1.context, com.corrodinggames.rts.appFramework.a.class);
			PendingIntent var3 = PendingIntent.getActivity(var1.context, 0, var2, 2);
			NotificationManager var4 = (NotificationManager)var1.context.c("notification");
			if (VERSION.SDK_INT >= 11) {
				if (VERSION.SDK_INT >= 26) {
				}

				Builder var5 = new Builder(var1.context);
				var5.setContentTitle("Rusted Warfare Multiplayer");
				var5.setContentText("A multiplayer game is in progress");
				var5.setSmallIcon(drawable.icon);
				var5.setContentIntent(var3);
				var5.setOngoing(true);
				this.createNotificationChannels(var4);
				this.setNotificationChannel(var5, "multiplayerStatusId");
				if (VERSION.SDK_INT >= 16) {
					var5.build();
				}

				Notification var6 = var5.getNotification();
				var4.notify(1, var6);
			}
		}
	}

	private strictfp void cancelNotification(int integer) {
		if (!GameEngine.isDedicatedServerPlatform) {
			GameEngine var2 = GameEngine.getInstance();
			NotificationManager var3 = (NotificationManager)var2.context.c("notification");
			var3.cancel(integer);
		}
	}

	public strictfp int getHumanTeamCount() {
		int var1 = 0;

		for (int var2 = 0; var2 < Team.maxTeamId; var2++) {
			Team var3 = Team.getTeam(var2);
			if (var3 != null && !var3.isAI) {
				var1++;
			}
		}

		return var1;
	}

	public strictfp int getTeamCount() {
		int var1 = 0;

		for (int var2 = 0; var2 < Team.maxTeamId; var2++) {
			Team var3 = Team.getTeam(var2);
			if (var3 != null) {
				var1++;
			}
		}

		return var1;
	}

	public strictfp void kickTeamAndAttachedPlayer(Team n) {
		if (this.isServer) {
			this.kickTeam(nx);
		} else if (this.proxyController) {
			this.sendChatCommand("-kick " + (nx.teamId + 1));
		} else {
			GameEngine.logWarning("kickTeamAndAttachedPlayer: but not server or proxy controller");
		}
	}

	public strictfp void kickTeam(Team n) {
		if (nx instanceof AI) {
			nx.removeFromTeamArray();
		} else {
			if (this.localTeam == nx) {
				GameEngine.logWithTag("kickTeamAndAttachedPlayer", "Cannot kick self");
				return;
			}

			Connection var2 = this.getConnectionForTeam(nx);
			if (var2 == null) {
				reportDesync("Kick player: cannot find connection for team");
			} else {
				int var3 = GameEngine.getInstance().settings.banTimeInSecondsAfterKick;
				if (var3 > 0) {
					this.banConnection(var2, "Temporarily banned due to recent kick", var3);
				}

				this.sendKick(var2, "Kicked by host");
				var2.sendKickWithReason("Kicked by host");
			}

			nx.removeFromTeamArray();
		}

		this.markPlayerListDirty();
		MultiplayerBattleroomActivity.o();
	}

	public strictfp void addAIToGame() {
		GameEngine var1 = GameEngine.getInstance();
		if (!this.isServer) {
			GameEngine.logWithTag("addAIToGame", "We are not a server");
		} else {
			int var2 = Team.findFreeTeamId();
			if (var2 == -1) {
				var1.showAlert("No free slots for AI", 1);
			}

			AI var3 = new AI(var2);
			var3.v = "AI";
			var3.r = var2 % 2;
			var3.x = this.gameSetup.aiDifficulty;
			this.updateNamesOfAI();
			var1.networkEngine.networkCallbacks.a(var3);
			var1.networkEngine.sendUpdatePlayer(null);
			MultiplayerBattleroomActivity.o();
		}
	}

	public strictfp boolean updateNamesOfAI() {
		if (!this.isServer && this.networked) {
			GameEngine.logWithTag("updateNamesOfAI", "We are not a server");
			return false;
		} else {
			boolean var1 = false;

			for (int var2 = 0; var2 < Team.maxTeamId; var2++) {
				Team var3 = Team.getTeam(var2);
				if (var3 != null && this.updateAITeamName(var3)) {
					var1 = true;
				}
			}

			return var1;
		}
	}

	public strictfp void moveTeamToSlot(Team n, int integer) {
		synchronized (this.bC) {
			this.swapTeamIds(nx, integer);
		}
	}

	private strictfp void swapTeamIds(Team n, int integer) {
		if (nx.teamId != integer) {
			int var3 = nx.teamId;
			int var4 = nx.allyTeam;
			boolean var5 = false;
			if (integer == -3) {
				var5 = true;
				integer = Team.findFreeSpectatorTeamId();
				if (integer == -1) {
					logNetworkDebug("No free spectator slots");
					return;
				}
			}

			Team var6 = Team.getTeam(integer);
			nx.f(integer);
			nx.allyTeam = var4;
			if (var5) {
				nx.allyTeam = -3;
			}

			if (var6 != null) {
				int var7 = var6.allyTeam;
				var6.f(var3);
				if (var7 == -3) {
					var6.allyTeam = -3;
				} else {
					var6.allyTeam = var4;
				}
			}

			this.assignTeamDisplaySlots();
			this.markPlayerListDirty();
		}
	}

	public strictfp void overrideTeamLayout(TeamLayout am) {
		synchronized (this.bC) {
			this.applyTeamLayout(amx);
		}
	}

	private strictfp synchronized void applyTeamLayout(TeamLayout am) {
		GameEngine var2 = GameEngine.getInstance();
		if (!var2.networkEngine.isServer) {
			GameEngine.log("Not server");
		} else {
			if (amx == TeamLayout.layout_2sides) {
				ArrayList var3 = new ArrayList();

				for (int var4 = 0; var4 < Team.maxTeamId; var4++) {
					Team var5 = Team.getTeam(var4);
					if (var5 != null) {
						var3.add(var5);
					}
				}

				Collections.shuffle(var3);
				int var14 = var3.size() / 2;
				if (var3.size() % 2 != 0) {
					var14 += CommonUtils.randomIntBetween(0, 1);
				}

				if (var14 >= var3.size()) {
					var14 = var3.size();
				}

				int var21 = 0;
				byte var6 = 0;

				for (int var7 = var21; var7 < var14; var7++) {
					((Team)var3.get(var7)).f((int)var6);
					var6 += 2;
					((Team)var3.get(var7)).allyTeam = 0;
				}

				var21 += var14;
				var6 = 1;

				for (int var36 = var21; var36 < var3.size(); var36++) {
					((Team)var3.get(var36)).f((int)var6);
					var6 += 2;
					((Team)var3.get(var36)).allyTeam = 1;
				}
			} else if (amx == TeamLayout.layout_3sides) {
				ArrayList var11 = new ArrayList();

				for (int var15 = 0; var15 < Team.maxTeamId; var15++) {
					Team var23 = Team.getTeam(var15);
					if (var23 != null) {
						var11.add(var23);
					}
				}

				Collections.shuffle(var11);
				int var16 = var11.size() / 3;
				if (var16 >= var11.size()) {
					var16 = var11.size();
				}

				int var24 = 0;
				byte var32 = 0;

				for (int var37 = var24; var37 < var16; var37++) {
					Team var8 = (Team)var11.get(var37);
					var8.f((int)var32);
					var8.allyTeam = 0;
					var32 += 3;
					var11.set(var37, null);
				}

				var24 += var16;
				int var38 = var24 + var11.size() / 3;
				if (var38 >= var11.size()) {
					var38 = var11.size();
				}

				if (var24 >= var11.size()) {
					var24 = var11.size();
				}

				var32 = 1;

				for (int var39 = var24; var39 < var38; var39++) {
					Team var9 = (Team)var11.get(var39);
					var9.f((int)var32);
					var9.allyTeam = 1;
					var32 += 3;
					var11.set(var39, null);
				}

				var24 += var16;
				if (var24 >= var11.size()) {
					var24 = var11.size();
				}

				var32 = 2;

				for (int var40 = var24; var40 < var11.size(); var40++) {
					Team var42 = (Team)var11.get(var40);
					if (var32 >= Team.maxTeamId) {
						var42.f((int)var32);
						var42.allyTeam = 2;
						var32 += 3;
						var11.set(var40, null);
					}
				}

				for (int var41 = 0; var41 < var11.size(); var41++) {
					Team var43 = (Team)var11.get(var41);
					if (var43 != null) {
						for (int var10 = 0; var10 < Team.maxTeamId; var10++) {
							if (Team.getTeam(var10) == null) {
								var43.f(var10);
								var43.allyTeam = 2;
								var11.set(var41, null);
							}
						}
					}
				}
			} else if (amx == TeamLayout.layout_ffa) {
				ArrayList var12 = new ArrayList();

				for (int var17 = 0; var17 < Team.maxTeamId; var17++) {
					Team var27 = Team.getTeam(var17);
					if (var27 != null) {
						var12.add(var27);
					}
				}

				Collections.shuffle(var12);
				int var18 = 0;

				for (int var28 = 0; var28 < var12.size(); var28++) {
					((Team)var12.get(var28)).f(var18);
					((Team)var12.get(var28)).allyTeam = var18++;
				}
			} else {
				if (amx != TeamLayout.layout_spectators) {
					throw new RuntimeException("overrideTeamLayout: unhandled layout: " + amx);
				}

				ArrayList var13 = new ArrayList();

				for (int var19 = 0; var19 < Team.maxTeamId; var19++) {
					Team var29 = Team.getTeam(var19);
					if (var29 != null) {
						var13.add(var29);
					}
				}

				Collections.shuffle(var13);
				int var20 = 0;

				for (int var30 = 0; var30 < var13.size(); var30++) {
					int var35 = Team.findFreeSpectatorTeamId();
					if (var35 != -1) {
						((Team)var13.get(var30)).f(var35);
					}

					((Team)var13.get(var30)).allyTeam = -3;
					var20++;
				}
			}

			this.assignTeamDisplaySlots();
		}
	}

	public strictfp void sendMoveTeamCommand(Team n, int integer, Integer integer) {
		String var4 = "";
		if (integerx != null) {
			var4 = " " + integerx;
		}

		if (!this.proxyController && this.localTeam == nx) {
			this.sendChatCommand("-self_move " + (integer + 1) + var4);
		} else {
			this.sendChatCommand("-move " + (nx.teamId + 1) + " " + (integer + 1) + var4);
		}
	}

	public strictfp void sendSetTeamCommand(Team n, int integer) {
		if (integer != -1) {
			integer++;
		}

		if (!this.proxyController && this.localTeam == nx) {
			this.sendChatCommand("-self_team " + integer);
		} else {
			this.sendChatCommand("-team " + (nx.teamId + 1) + " " + integer);
		}
	}

	public strictfp void announceVictory(Team n) {
		if (!nx.victoryAnnounced) {
			nx.victoryAnnounced = true;
			String var2 = nx.name;
			if (var2 == null) {
				var2 = "Player - " + (nx.teamId + 1) + "";
			}

			String var3 = var2 + " is victorious!";
			this.sendSystemMessage(var3);
		}
	}

	public strictfp void announceDefeat(Team n) {
		GameEngine var2 = GameEngine.getInstance();
		boolean var3 = false;
		String var4 = nx.name;
		if (var4 == null) {
			var4 = "Player - " + (nx.teamId + 1) + "";
		}

		String var5 = var4 + " was defeated";
		if (!this.onePlayerPerTeam) {
			var5 = var5 + " (Team: " + nx.getTeamLetter() + ")";
		} else {
			int var6 = Team.countRemainingPlayers();
			var5 = var5 + " (" + var6 + " players remaining)";
			if (var6 == 1) {
				var3 = true;
			}
		}

		if (!var2.isMultiplayer() && var2.gameTimeSeconds < 60) {
			GameEngine.log("Not showing defeated message: " + var5);
			var5 = null;
		}

		if (nx.eliminated) {
			var5 = null;
		}

		if (var5 != null) {
			this.sendSystemMessage(var5);
		}

		if (var3) {
			Team.announceWinners();
		}
	}

	public strictfp void announceWipedOut(Team n) {
		GameEngine var2 = GameEngine.getInstance();
		String var3 = nx.name;
		if (var3 == null) {
			var3 = "Player - " + (nx.teamId + 1) + "";
		}

		boolean var4 = false;
		String var5;
		if (var2.gameTimeSeconds < 10) {
			var5 = var3 + " had no starting units";
		} else {
			var5 = var3 + " has been wiped out";
		}

		if (!this.onePlayerPerTeam) {
			var5 = var5 + " (Team: " + nx.getTeamLetter() + ")";
		} else {
			int var6 = Team.countRemainingPlayers();
			var5 = var5 + " (" + var6 + " players remaining)";
			if (var6 == 1) {
				var4 = true;
			}
		}

		if (!var2.isMultiplayer() && var2.gameTimeSeconds < 60) {
			GameEngine.log("Not showing defeated message: " + var5);
			var5 = null;
		}

		if (nx.eliminated) {
			var5 = null;
		}

		if (nx.isSpectator()) {
			var5 = null;
		}

		if (var5 != null) {
			this.sendSystemMessage(var5);
		}

		if (var4) {
			Team.announceWinners();
		}
	}

	public strictfp synchronized void stopMasterServerTimer() {
		if (this.bD != null) {
			this.bD.cancel();
			this.bD = null;
		}
	}

	public strictfp synchronized void startMasterServerTimer() {
		if (this.serverVisible && this.isServer && this.bD == null) {
			this.bD = new Timer();
			ad$6 var1 = new ad$6(this);
			this.bD.schedule(var1, 60000L, 60000L);
		}
	}

	public strictfp String getGameInfoText() {
		GameEngine var1 = GameEngine.getInstance();
		String var2 = "";
		if (var1.networkEngine.isServer && !var1.networkEngine.singlePlayerServer) {
			String var3 = var1.networkEngine.getLocalIpAddressesString();
			if (this.D) {
				if (this.E != null) {
					String var4 = this.E;
					var2 = var2 + var4;
				}
			} else if (var3 != null) {
				String var11 = "Local IP address: " + var3 + " port: " + var1.networkEngine.networkPort;
				if (var1.networkEngine.portCheckInProgress != null) {
					if (!var1.networkEngine.portCheckInProgress) {
						var11 = var11 + "\nUnable to get a public IP address, check your internet connection";
					} else if (var1.networkEngine.portCheckStatusMessage != null && var1.networkEngine.portCheckSuccess != null) {
						var11 = var11 + "\nYour public address is " + (var1.networkEngine.portCheckSuccess ? "<Open>" : "<CLOSED>") + " to the internet";
					}
				} else {
					var11 = var11 + "\nRetrieving your public IP...";
				}

				var2 = var2 + var11;
			} else {
				var2 = var2 + "You do not have a network connection";
			}
		}

		if (var1.isSinglePlayer()) {
			if (this.sandboxMode) {
				var2 = var2 + "SandBox Mode!\nPlace any unit, Control all teams, Special powers";
			} else {
				var2 = var2 + "Local skirmish";
			}
		}

		boolean var10 = true;
		if (GameEngine.isNotDedicatedServer() && var1.networkEngine.isServer) {
			var10 = false;
		}

		if (var2.length() != 0) {
			var2 = var2 + "\n";
			if (GameEngine.isDesktopVersion()) {
				var2 = var2 + "\n";
			}
		}

		if (var1.networkEngine.receivedServerInfo || var1.networkEngine.isServer) {
			if (var10) {
				if (var1.networkEngine.gameSetup.mapType != null) {
					var2 = var2 + "Game Mode: " + var1.networkEngine.gameSetup.mapType.a();
				}

				if (var1.networkEngine.gameSetup.mapName != null) {
					var2 = var2 + "\nMap: " + MapSelectActivity.e(var1.networkEngine.gameSetup.mapName);
				}
			}

			var2 = var2 + "\nStarting Credits: " + var1.networkEngine.getStartingCreditsDescription();
			var2 = var2 + "\nFog: " + var1.networkEngine.getFogModeName();
			if (var1.networkEngine.gameSetup.startingUnits != 1) {
				var2 = var2 + "\nStarting Units: " + var1.networkEngine.getCurrentStartingUnitsName();
			}

			if (var1.networkEngine.gameSetup.incomeMultiplier != 1.0F) {
				var2 = var2 + "\n" + CommonUtils.formatDecimal(var1.networkEngine.gameSetup.incomeMultiplier, 1) + "X income";
			}

			if (var1.networkEngine.gameSetup.noNukes) {
				var2 = var2 + "\nNo nukes";
			}

			if (var1.networkEngine.gameSetup.sharedControl) {
				var2 = var2 + "\nShared control: On";
			}

			if (this.isServer) {
				if (var1.networkEngine.serverPassword != null) {
					var2 = var2 + "\nPassword Protection: On";
				}

				if (!var1.networkEngine.serverVisible && !var1.networkEngine.singlePlayerServer) {
					var2 = var2 + "\nServer Visibility: Hidden";
				}

				if (var1.networkEngine.modsEnabled && !var1.networkEngine.singlePlayerServer) {
					ArrayList var12 = var1.modManager.getEnabledMods();
					var2 = var2 + "\n-- Required Mods: --\n";
					int var5 = 0;

					for (Mod var7 : var12) {
						if (var5 > 2 && var5 < var12.size() - 1) {
							var2 = var2 + "" + (var12.size() - var5) + " more mods...";
							break;
						}

						var5++;
						String var8 = var7.getTitleShort();
						var8.replace("\"", "'");
						var8.replace(";", ".");
						var2 = var2 + " mod: \"" + var8 + "\"\n";
					}
				}
			}
		}

		return var2;
	}

	public strictfp String getEnabledModsSummary() {
		if (!this.modsEnabled) {
			return null;
		} else {
			GameEngine var1 = GameEngine.getInstance();
			ArrayList var2 = var1.modManager.getEnabledMods();
			String var3 = "";
			int var4 = 0;

			for (Mod var6 : var2) {
				if (var4 != 0) {
					var3 = var3 + "; ";
				}

				if (var4 > 1 && var4 < var2.size() - 1) {
					var3 = var3 + "" + (var2.size() - var4) + " more...";
					break;
				}

				var4++;
				String var7 = var6.getTitleShort();
				var7.replace(";", ".");
				var3 = var3 + var7;
			}

			return var3;
		}
	}

	public strictfp String getNetworkMapPath() {
		GameEngine var1 = GameEngine.getInstance();
		if (var1.networkEngine.gameSetup.mapName == null) {
			return null;
		} else if (var1.networkEngine.gameSetup.mapType == null) {
			return null;
		} else if (var1.networkEngine.gameSetup.mapType == GameSetupMapType.skirmishMap) {
			return "maps/skirmish/" + var1.networkEngine.gameSetup.mapName;
		} else if (var1.networkEngine.gameSetup.mapType == GameSetupMapType.customMap) {
			return "/SD/rusted_warfare_maps/" + var1.networkEngine.gameSetup.mapName;
		} else {
			GameEngine.log("getNetworkMapPath: unhandled type:" + var1.networkEngine.gameSetup.mapType);
			return null;
		}
	}

	public strictfp boolean canChangeGameSetup() {
		return this.isServer || this.proxyController;
	}

	public strictfp void sendCommandError(String string, Connection c) {
		GameEngine.log("sendCommandError: " + string);
		if (cx == null) {
			this.receiveChatMessage(null, -1, null, string);
		} else {
			this.routeChatMessageViaRelay(null, null, null, string, cx);
		}
	}

	public strictfp boolean handleChatCommand(Connection c, Team n, String string3, String string4) {
		String var5 = null;
		String var6 = "";
		String[] var7 = new String[0];
		String var8 = string4.trim();
		boolean var9 = false;
		if (var8.startsWith("-qc ")) {
			var8 = var8.substring("-qc ".length());
			var8 = var8.trim();
			var9 = true;
		}

		if ((var8.startsWith("-") || var8.startsWith(".") || var8.startsWith("_")) && var8.length() >= 2) {
			String var10 = var8.substring(1).trim();
			int var11 = var10.indexOf(" ");
			if (var11 == -1) {
				var11 = var10.length();
			}

			var5 = var10.substring(0, var11).toLowerCase(Locale.ENGLISH);
			if (var11 != -1 && var10.length() >= var11 + 1) {
				var6 = var10.substring(var11 + 1).trim();
				var7 = var6.split(" ");
			}
		}

		if (var5 == null) {
			return false;
		} else if (var9 && !"self_move".equals(var5) && !"self_team".equals(var5)) {
			return false;
		} else if (!"pause".equals(var5) && !"unpause".equals(var5)) {
			if ("endgame".equals(var5)) {
				if (nx == null) {
					this.sendCommandError("[Could not find player]", cx);
					return true;
				} else if (!this.isServer || nx != this.localTeam) {
					this.sendCommandError("[Only the host can end game]", cx);
					return true;
				} else if (!this.gameStarted) {
					this.sendCommandError("[Game not yet started]", cx);
					return true;
				} else {
					this.scheduleReturnToBattleroomSoon();
					return true;
				}
			} else if ("teamlock".equals(var5)) {
				if (nx == null) {
					this.sendCommandError("[Could not find player]", cx);
					return true;
				} else if ((!this.isServer || nx != this.localTeam) && !this.networkCallbacks.b(cx)) {
					this.sendCommandError("[Only the host can change teamlock]", cx);
					return true;
				} else if ("true".equalsIgnoreCase(var6) || "on".equalsIgnoreCase(var6)) {
					this.gameSetup.teamsLocked = true;
					this.sendCommandError("[teams are locked]", cx);
					return true;
				} else if (!"false".equalsIgnoreCase(var6) && !"off".equalsIgnoreCase(var6)) {
					this.sendCommandError("[Expected true or false]", cx);
					return true;
				} else {
					this.gameSetup.teamsLocked = false;
					this.sendCommandError("[teams are unlocked]", cx);
					return true;
				}
			} else if ("roomlock".equals(var5)) {
				if (nx == null) {
					this.sendCommandError("[Could not find player]", cx);
					return true;
				} else if (!this.isServer || nx != this.localTeam) {
					this.sendCommandError("[Only the host can change roomlock]", cx);
					return true;
				} else if ("true".equalsIgnoreCase(var6) || "on".equalsIgnoreCase(var6)) {
					this.gameSetup.lockedRoom = true;
					this.sendCommandError("[room is locked]", cx);
					return true;
				} else if (!"false".equalsIgnoreCase(var6) && !"off".equalsIgnoreCase(var6)) {
					this.sendCommandError("[Expected true or false]", cx);
					return true;
				} else {
					this.gameSetup.lockedRoom = false;
					this.sendCommandError("[room is unlocked]", cx);
					return true;
				}
			} else if ("share".equals(var5)) {
				if (nx == null) {
					this.sendCommandError("[Could not find player]", cx);
					return true;
				} else if (!this.gameSetup.sharedControl) {
					this.sendCommandError("[Shared control is not enabled in this game]", cx);
					return true;
				} else if (!"true".equalsIgnoreCase(var6) && !"on".equalsIgnoreCase(var6)) {
					if (!"false".equalsIgnoreCase(var6) && !"off".equalsIgnoreCase(var6)) {
						this.sendCommandError("[Expected true or false]", cx);
						return true;
					} else {
						if (nx.sharedControlEnabled) {
							nx.sharedControlEnabled = false;
							this.sendSystemMessage("[shared control now off for " + string3 + "]");
						} else {
							this.sendSystemMessage("[shared control already off for " + string3 + "]");
						}

						return true;
					}
				} else {
					if (!nx.sharedControlEnabled) {
						nx.sharedControlEnabled = true;
						this.sendSystemMessage("[shared control now on for " + string3 + "]");
					} else {
						this.sendSystemMessage("[shared control already on for " + string3 + "]");
					}

					return true;
				}
			} else if ("self_move".equals(var5)) {
				if (nx == null) {
					this.sendCommandError("[Cannot Move - Player not found]", cx);
					return true;
				} else if (this.gameStarted) {
					this.sendCommandError("[Cannot Move '" + nx.name + "' - Game has been started]", cx);
					return true;
				} else if (this.isGameStarting()) {
					this.sendCommandError("[Cannot Move '" + nx.name + "' - Game is starting]", cx);
					return true;
				} else if (this.gameSetup.teamsLocked) {
					this.sendCommandError("[Cannot Move '" + nx.name + "' - Teams locked]", cx);
					return true;
				} else if (var7.length > 0) {
					int var28;
					try {
						var28 = Integer.valueOf(var7[0]);
					} catch (NumberFormatException var20) {
						this.sendCommandError("[Cannot Move '" + nx.name + "' - team '" + var7[0] + "' is not a number]", cx);
						return true;
					}

					Integer var32 = null;
					if (var7.length > 1) {
						try {
							var32 = Integer.valueOf(var7[1]);
						} catch (NumberFormatException var19) {
							this.sendCommandError("[Cannot Move '" + nx.name + "' - ally group '" + var7[1] + "' is not a number]", cx);
							return true;
						}

						if (var32 != -1 && (var32 < 1 || var32 > 99)) {
							this.sendCommandError("[Cannot Move Team - Ally group - Out of range]", cx);
							return true;
						}
					}

					boolean var33 = false;
					if (var28 - 1 == -3) {
						if (!this.gameSetup.allowSpectators) {
							this.sendCommandError("[Spectators are disabled on this server]", cx);
							return true;
						}

						synchronized (this.bC) {
							var28 = Team.findFreeSpectatorTeamId();
							if (var28 != -1) {
								this.moveTeamToSlot(nx, -3);
							}
						}

						var33 = true;
					}

					int var34 = nx.allyTeam;
					boolean var14 = var34 == -3;
					if (!var33) {
						if (var28 < 1 || var28 > Team.maxTeamId) {
							this.sendCommandError("[Cannot Move '" + nx.name + "' - target slotId must between 1-" + Team.maxTeamId + "]", cx);
							return true;
						}

						synchronized (this.bC) {
							if (this.localTeam != nx) {
								Team var16 = Team.getTeam(var28 - 1);
								if (var16 != null && !var16.isAI && !var16.isSpectator()) {
									this.sendCommandError("[Cannot move '" + nx.name + "' to slot: " + var28 + " - Player: " + var16.name + " is in that slot.]", cx);
									return true;
								}
							}

							this.moveTeamToSlot(nx, var28 - 1);
						}
					}

					nx.allyTeam = var34;
					if (var32 != null) {
						if (var32 == -1) {
							nx.allyTeam = nx.teamId % 2;
						} else {
							nx.allyTeam = var32;
						}
					}

					if (this.gameSetup.enforceTwoTeams) {
						nx.allyTeam = nx.teamId % 2;
					}

					if (var33) {
						nx.allyTeam = -3;
					}

					if (var33) {
						if (!var14) {
							this.sendSystemMessage("Player '" + nx.name + "' is now a spectator");
						}
					} else {
						this.sendSystemMessage("Player '" + nx.name + "' moved themselves to: " + var28);
					}

					this.markPlayerListDirty();
					MultiplayerBattleroomActivity.o();
					return true;
				} else {
					this.sendCommandError("[Cannot Move '" + nx.name + "' - No target]", cx);
					return true;
				}
			} else if (!"self_team".equals(var5)) {
				if (!"surrender".equals(var5)) {
					return false;
				} else if (!this.gameStarted) {
					this.sendCommandError("[Cannot Surrender - Game has not started]", cx);
					return true;
				} else if (nx == null) {
					this.sendCommandError("[Could not find player]", cx);
					return true;
				} else {
					String var26 = "";
					if (!nx.hasVotedSurrender()) {
						nx.castSurrenderVote();
						boolean var30 = nx.canVoteSurrender();
						GameEngine.log(
							string3 + ": Is voting to surrender (can surrender:" + var30 + ", afk:" + nx.isAFK + ", defeated:" + nx.wipedOut + ", disconnected:" + nx.isTimedOut() + ")"
						);
						if (var30) {
							var26 = "";
						} else {
							var26 = "(Cannot vote) ";
						}
					} else {
						GameEngine.log(string3 + ": Is already voting to surrender but updating timestamp");
						nx.castSurrenderVote();
						var26 = "(Already voted) ";
					}

					String var31 = Team.countSurrenderVotes(nx.allyTeam) + "/" + Team.countSurrenderEligible(nx.allyTeam);
					String var12 = "-t " + var26 + "[Votes to surrender " + var31 + "]";
					this.routeChatMessage(cx, nx, string3, var12);
					return true;
				}
			} else if (nx == null) {
				this.sendCommandError("[Cannot Set Team - Player not found]", cx);
				return true;
			} else if (this.gameStarted) {
				this.sendCommandError("[" + nx.name + ": Cannot Set Team - Game has been started]", cx);
				return true;
			} else if (this.isGameStarting()) {
				this.sendCommandError("[" + nx.name + ": Cannot Set Team - Game is starting]", cx);
				return true;
			} else if (this.gameSetup.teamsLocked) {
				this.sendCommandError("[" + nx.name + ": Cannot Set Team - Teams locked]", cx);
				return true;
			} else if (this.gameSetup.enforceTwoTeams) {
				return true;
			} else {
				int var25;
				try {
					var25 = Integer.valueOf(var6);
				} catch (NumberFormatException var21) {
					this.sendChatMessage("'" + var6 + "' is not a number");
					return true;
				}

				int var29;
				if (var25 == -1) {
					var29 = nx.teamId % 2;
				} else {
					if (var25 < 1 || var25 > 99) {
						this.sendCommandError("[Cannot Set Team - Out of range]", cx);
						return true;
					}

					var29 = var25 - 1;
				}

				if (nx.allyTeam != var29) {
					nx.allyTeam = var29;
					this.sendCommandError("Player '" + nx.name + "' team changed to: " + var25, cx);
				}

				this.markPlayerListDirty();
				MultiplayerBattleroomActivity.o();
				return true;
			}
		} else if (nx == null) {
			this.sendCommandError("[Could not find player]", cx);
			return true;
		} else if ((!this.isServer || nx != this.localTeam) && !this.networkCallbacks.b(cx)) {
			this.sendCommandError("[Only the host can change pause state]", cx);
			return true;
		} else if (!this.gameStarted) {
			this.sendCommandError("[Game not yet started]", cx);
			return true;
		} else {
			boolean var24 = !this.gamePaused;
			if ("unpause".equals(var5)) {
				var24 = false;
			}

			this.setGamePaused(var24);
			return true;
		}
	}

	public static strictfp void showPasswordPrompt(PasswordPrompt ae) {
		GameEngine var1 = GameEngine.getInstance();
		if (var1.networkEngine != null) {
			var1.networkEngine.networkCallbacks.onShowPasswordPrompt(aex);
		}

		if (!GameEngine.isDedicatedServerPlatform) {
			ad$7 var2 = new ad$7(aex);
			AppContext.a(var2);
		}
	}

	public strictfp ArrayList getTeams() {
		synchronized (this.bC) {
			return Team.getTeams();
		}
	}

	public strictfp void setGamePaused(boolean boolean1) {
		this.gamePaused = boolean1;
		if (this.gamePaused) {
			this.sendSystemMessage("Game Paused");
		} else {
			this.sendSystemMessage("Game unpaused");
		}
	}

	public strictfp void disconnectConnection(Connection c, String string) {
		cx.handleRemoteDisconnect(false, false, string);
	}

	public strictfp void disconnectChildConnections(Connection c, String string) {
		for (Connection var4 : this.connections) {
			if (var4.parent == cx) {
				this.disconnectConnection(var4, string);
			}
		}
	}

	public strictfp Connection addForwardedConnection(Connection c, int integer, String string3, String string4) {
		GameEngine var5 = GameEngine.getInstance();
		ForwardedSocket var6 = new ForwardedSocket(cx, integer);
		Connection var7 = new Connection(this, var6);
		var7.forwardedPort = integer;
		var7.parent = cx;
		var7.m = string3;
		var7.forwardedHost = string4;

		try {
			var7.startWorkers();
			var5.networkEngine.connections.add(var7);
			var5.networkEngine.resetPlayerListDirty();
			return var7;
		} catch (IOException var9) {
			var9.printStackTrace();
			var7.sendKickWithReason("crash");
			return null;
		}
	}

	public strictfp Connection getForwardedConnection(Connection c, int integer) {
		for (Connection var4 : this.connections) {
			if (var4.forwardedPort == integer && var4.parent == cx) {
				return var4;
			}
		}

		return null;
	}

	public static strictfp String sanitiseUserName(String string) {
		string = string.trim();
		string = string.replace("\n", ".");
		string = string.replace("\r", ".");
		string = string.replace("\t", ".");
		string = string.replace("\u0000", ".");
		string = string.replace(" ", "_");

		while (string.startsWith(".") || string.startsWith("-") || string.startsWith(" ")) {
			string = string.substring(1);
		}

		StringBuilder var1 = new StringBuilder();

		for (char var5 : string.toCharArray()) {
			if (!Character.isISOControl(var5)) {
				var1.append(var5);
			}
		}

		return var1.toString();
	}

	public strictfp void startJoinServerThread(ArrayList arrayList, boolean boolean2) {
		if (this.joinServerRunnable != null) {
			GameEngine.log("startJoinServerInternalThread: Already joining");
		} else if (arrayList.size() == 0) {
			GameEngine.log("startJoinServerInternalThread: no servers");
		} else {
			String var3 = (String)arrayList.get(0);
			boolean var4 = false;
			ad$8 var5 = new ad$8(this, boolean2);
			this.joinServerRunnable = this.connectToServerAsync(var3, var4, var5);
		}
	}
}
