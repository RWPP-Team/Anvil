package com.corrodinggames.rts.game.units;

public abstract class OrderableUnit extends BaseUnit {
	public static boolean L = false;
	protected BitmapOrTexture bodyImage;
	protected BitmapOrTexture turretImage;
	private int a;
	private float b;
	private float c;
	private float d;
	private float e;
	private int waypointsCount = 0;
	public static final Waypoint[] O = new Waypoint[0];
	private Waypoint[] waypoints = O;
	public AttackMode attackMode = AttackMode.onlyInRange;
	int lastBuildFailMillis = -9999;
	public Unit attackTarget;
	public float targetSearchCooldown;
	public float turretAssignTimer;
	public float U;
	private boolean workBeamActive;
	private int lastReclaimTickMillis = -9999;
	public float waypointTimer;
	public float pathStuckTimer;
	public float X;
	public float approachTimeoutTimer;
	private boolean directMoveRequested;
	private boolean hasMoveTarget;
	private float moveTargetX = 3.0F;
	private float moveTargetY = 3.0F;
	private int moveTargetClearance;
	private float pathGoalX;
	private float pathGoalY;
	private byte pathRetryCount;
	private int moveGoalDriftTolerance;
	private float pathFollowSpeed;
	private boolean lowPriorityPathFlag;
	public Unit lastCollisionUnit;
	public int lastCollisionTimeSeconds;
	public float ab;
	public int ac;
	public OrderableUnit queueLeader;
	public boolean inFormation;
	public boolean af;
	public int followerCount;
	public short formationSlotCount;
	public float ai;
	public boolean aj = false;
	public float formationOffsetX = 0.0F;
	public float formationOffsetY = 0.0F;
	public float am = 0.0F;
	public int an = 0;
	public float ao = 0.0F;
	public boolean ap;
	public float fleeHeading = -999.0F;
	public boolean fleeing = false;
	public boolean bomberSearchFailed = false;
	public static final af[] at = new af[0];
	public FlowFieldFollower flowFieldFollower;
	protected af[] pathNodes = at;
	protected int activePathCount = 0;
	private boolean pathTruncated;
	private int pathNodeTotal = 0;
	private int guardFollowState;
	public boolean ax = true;
	public boolean positionDirty;
	public float noTurnTimer;
	public float aA;
	public AbstractGroup aB;
	public Base aC;
	public boolean aD;
	public static final UniquePaint aE = new UniquePaint();
	public static final UniquePaint aF = new UniquePaint();
	public static final PointF aG = new PointF();
	private UniquePaint x = null;
	private int y;
	private UniquePaint z = null;
	private int A;
	private static final Paint B = new Paint();
	private static int C;
	private static final UniquePaint D = a(false);
	private static final UniquePaint E = a(true);
	public static UnitSearchCallback aH = new y$1();
	public byte nearbyCollisionCount = 0;
	public Unit[] nearbyCollisionUnits;
	public float[] nearbyCollisionDists;
	public int nextCollisionCheckMillis = -9999;
	public static final UnitList aM = new UnitList();
	public boolean aN;
	public boolean insufficientResources;
	static final ad aP = new ad();
	public static PassiveTargetCallback aQ = new PassiveTargetCallback(true);
	public static PassiveTargetCallback aR = new PassiveTargetCallback(false);
	public static TurretPassiveTargetCallback aS = new TurretPassiveTargetCallback(true);
	public static TurretPassiveTargetCallback aT = new TurretPassiveTargetCallback(false);
	Path pendingPath = null;
	static FastArrayList aV = new FastArrayList();
	public static final af aW = new af();
	protected static PorterDuffColorFilter aX = new PorterDuffColorFilter(Color.a(200, 255, 200), Mode.MULTIPLY);
	protected static PorterDuffColorFilter aY = new PorterDuffColorFilter(Color.a(70, 255, 70), Mode.MULTIPLY);
	protected static PorterDuffColorFilter aZ = new PorterDuffColorFilter(Color.a(255, 40, 40), Mode.MULTIPLY);
	protected static PorterDuffColorFilter ba = new PorterDuffColorFilter(Color.a(120, 120, 255), Mode.MULTIPLY);
	protected static Paint bb = GameUtils.createPaintNoAntiAlias();
	protected static Paint bc = GameUtils.createPaintNoAntiAlias();
	protected static Paint bd = GameUtils.createPaintNoAntiAlias();
	static final PointF be = new PointF();
	protected static final Point3F bf = new Point3F();
	protected static final PointF bg = new PointF();
	protected static final PointF bh = new PointF();
	protected static final Point3F bi = new Point3F();
	protected static final PointF bj = new PointF();
	static final Point bk = new Point();
	static final Point bl = new Point();
	static final PointF bm = new PointF();
	static final z bn = new z();
	public static final FindNearestUnitRequest bo = new FindNearestUnitRequest();
	public FastArrayList statusEffects;
	static FastArrayList bq = new FastArrayList();

	public strictfp void b(float float1) {
		if (this.noTurnTimer < float1) {
			this.noTurnTimer = float1;
		}
	}

	public strictfp Paint R() {
		boolean var1 = this.aO();
		return var1 ? aF : aE;
	}

	public static strictfp void a(OrderableUnit y1, OrderableUnit y2) {
		try {
			OutputNetStream var2 = new OutputNetStream();
			int var3 = y1.waypointsCount;

			for (int var4 = 0; var4 < var3; var4++) {
				y1.waypoints[var4].a(var2);
			}

			InputNetStream var8 = new InputNetStream(var2.toByteArray());
			y2.waypointsCount = var3;

			for (int var5 = 0; var5 < var3; var5++) {
				int var6 = var5;
				y2.ensureWaypointCapacity(var5);
				if (var5 >= y2.waypoints.length) {
					GameEngine.logWarning("Too many waypoints:" + var5);
					var6 = y2.waypoints.length - 1;
				}

				if (y2.waypoints[var6] == null) {
					y2.waypoints[var6] = new Waypoint();
				}

				y2.waypoints[var6].a(var8);
				y2.waypoints[var6].c();
			}
		} catch (IOException var7) {
			throw new RuntimeException(var7);
		}
	}

	@Override
	public strictfp void write(OutputNetStream as) {
		asx.writeFloat(this.b);
		asx.writeFloat(this.c);
		asx.writeFloat(this.cL[0].reloadTimer);
		asx.writeInt(this.waypointsCount);
		int var2 = this.waypointsCount;
		asx.writeInt(var2);

		for (int var3 = 0; var3 < var2; var3++) {
			this.waypoints[var3].a(asx);
		}

		asx.writeEnum(this.attackMode);
		Unit var5 = this.attackTarget;
		if (var5 != null && var5.dead) {
			var5 = null;
		}

		asx.writeUnit(var5);
		asx.writeFloat(this.targetSearchCooldown);
		asx.writeFloat(this.U);
		asx.writeFloat(this.waypointTimer);
		asx.writeIfDebugOnly("pathing_active:");
		asx.writeBoolean(this.hasMoveTarget);
		asx.writeFloat(this.moveTargetX);
		asx.writeFloat(this.moveTargetY);
		asx.writeFloat(this.pathFollowSpeed);
		asx.writeOrderableUnit(this.queueLeader);
		asx.writeBoolean(this.inFormation);
		asx.writeBoolean(this.af);
		asx.writeBoolean(this.aj);
		asx.writeFloat(this.formationOffsetX);
		asx.writeFloat(this.formationOffsetY);
		asx.writeFloat(this.am);
		asx.writeInt(this.an);
		asx.writeInt(this.ac);
		asx.writeIfDebugOnly("activePathCount:");
		asx.writeInt(this.activePathCount);

		for (int var4 = 0; var4 < this.activePathCount; var4++) {
			this.pathNodes[var4].a(asx);
		}

		asx.writeInt(this.activePathCount);
		asx.writeInt(this.pathNodeTotal);
		if (asx.isWriteIfDebugOnlySupported()) {
		}

		asx.writeByte(12);
		asx.writeFloat(this.pathGoalX);
		asx.writeFloat(this.pathGoalY);
		asx.writeFloat(this.d);
		asx.writeFloat(this.e);
		asx.writeBoolean(this.pathTruncated);
		asx.writeFloat(this.ai);
		asx.writeInt(this.moveTargetClearance);
		asx.writeFloat(this.pathStuckTimer);
		asx.writeFloat(this.fleeHeading);
		asx.writeBoolean(this.fleeing);
		asx.writeBoolean(this.bomberSearchFailed);
		asx.writeShort(this.formationSlotCount);
		asx.writeFloat(this.ab);
		asx.writeInt(this.guardFollowState);
		asx.writeFloat(this.X);
		asx.writeFloat(this.noTurnTimer);
		asx.writeFloat(this.aA);
		StatusEffectManager.a(this, asx);
		super.a(asx);
	}

	@Override
	public strictfp void read(InputNetStream k) {
		this.b = kx.readFloat();
		this.c = kx.readFloat();
		this.cL[0].reloadTimer = kx.readFloat();
		this.waypointsCount = kx.readInt();
		if (this.waypointsCount > 0) {
			this.ensureWaypointCapacity(CommonUtils.min(this.waypointsCount - 1, 29));
		}

		int var2 = 30;
		if (kx.getVersion() >= 42) {
			var2 = kx.readInt();
		}

		for (int var3 = 0; var3 < var2; var3++) {
			int var4 = var3;
			this.ensureWaypointCapacity(var3);
			if (var3 >= this.waypoints.length) {
				GameEngine.logWarning("Too many waypoints:" + var3);
				var4 = this.waypoints.length - 1;
			}

			if (this.waypoints[var4] == null) {
				this.waypoints[var4] = new Waypoint();
			}

			this.waypoints[var4].a(kx);
		}

		this.attackMode = (AttackMode)kx.readEnum(AttackMode.class);
		if (this.attackMode == AttackMode.outOfRange) {
			if (!this.canReceiveOrders()) {
				this.attackMode = AttackMode.onlyInRange;
			}

			if (kx.getVersion() < 74) {
				this.attackMode = AttackMode.onlyInRange;
			}
		}

		long var7 = kx.readLong2();
		this.targetSearchCooldown = kx.readFloat();
		this.U = kx.readFloat();
		this.waypointTimer = kx.readFloat();
		this.hasMoveTarget = kx.readBoolean();
		this.moveTargetX = kx.readFloat();
		this.moveTargetY = kx.readFloat();
		this.pathFollowSpeed = kx.readFloat();
		this.setQueueLeader(kx.readOrderableUnit());
		this.inFormation = kx.readBoolean();
		this.af = kx.readBoolean();
		this.aj = kx.readBoolean();
		this.formationOffsetX = kx.readFloat();
		this.formationOffsetY = kx.readFloat();
		this.am = kx.readFloat();
		this.an = kx.readInt();
		if (kx.getVersion() >= 18) {
			this.ac = kx.readInt();
		}

		if (kx.getVersion() >= 21) {
			int var5 = kx.readInt();

			for (int var6 = 0; var6 < var5; var6++) {
				this.ensurePathNodeCapacity(var6);
				if (this.pathNodes[var6] == null) {
					this.pathNodes[var6] = new af();
				}

				this.pathNodes[var6].a(kx);
			}
		} else {
			byte var8 = 60;

			for (int var10 = 0; var10 < 60; var10++) {
				this.ensurePathNodeCapacity(var10);
				if (this.pathNodes[var10] == null) {
					this.pathNodes[var10] = new af();
				}

				this.pathNodes[var10].a(kx);
			}
		}

		this.activePathCount = kx.readInt();
		this.pathNodeTotal = kx.readInt();
		byte var9 = kx.readByte();
		if (var9 >= 1) {
			this.pathGoalX = kx.readFloat();
			this.pathGoalY = kx.readFloat();
		}

		if (var9 >= 2) {
			this.d = kx.readFloat();
			this.e = kx.readFloat();
		}

		if (var9 >= 3) {
			this.pathTruncated = kx.readBoolean();
		}

		if (var9 >= 4) {
			this.ai = kx.readFloat();
			this.moveTargetClearance = kx.readInt();
		}

		if (var9 >= 5) {
			this.pathStuckTimer = kx.readFloat();
		}

		if (var9 >= 6) {
			this.fleeHeading = kx.readFloat();
			this.fleeing = kx.readBoolean();
			this.bomberSearchFailed = kx.readBoolean();
		}

		if (var9 >= 7) {
			this.formationSlotCount = kx.readShort();
		}

		if (var9 >= 8) {
			this.ab = kx.readFloat();
		}

		if (var9 >= 9) {
			this.guardFollowState = kx.readInt();
		}

		if (var9 >= 10) {
			this.X = kx.readFloat();
		}

		if (var9 >= 11) {
			this.noTurnTimer = kx.readFloat();
			this.aA = kx.readFloat();
		}

		if (var9 >= 12) {
			StatusEffectManager.a(this, kx);
		}

		super.a(kx);
		if (!this.bV) {
			this.attackTarget = GameObject.getUnitFromId(var7, false);

			for (int var11 = 0; var11 < this.waypointsCount; var11++) {
				if (this.waypoints[var11] == null) {
					GameEngine.log("readIn: convertUnitIds is null: " + var11 + " waypointsCount:" + this.waypointsCount);
				} else {
					this.waypoints[var11].c();
				}
			}
		}

		this.S();
		if (this.bV) {
			this.ew = true;
		}
	}

	@Override
	public strictfp void setTeamDirect(Team n) {
		super.b(nx);
		this.S();
	}

	public strictfp void S() {
		this.bodyImage = this.getBodyImage();
		this.turretImage = this.getShadowImage();
	}

	public abstract BitmapOrTexture getBodyImage();

	public abstract BitmapOrTexture getShadowImage();

	public abstract BitmapOrTexture getTurretImage(int integer);

	public strictfp float getTurretImageDrawOffsetX(int integer) {
		return 0.0F;
	}

	public strictfp float getTurretImageDrawOffsetY(int integer) {
		return 0.0F;
	}

	public strictfp BitmapOrTexture T() {
		return null;
	}

	public strictfp Paint a(int integer, ColorFilter colorFilter, boolean boolean3) {
		if (integer == -1 && colorFilter == null) {
			return boolean3 ? E : D;
		} else {
			Object var4;
			int var5;
			if (this.cp) {
				if (colorFilter == null) {
					var4 = B;
					var5 = C;
					C = integer;
				} else {
					var4 = B;
					var5 = -1;
					if (colorFilter == aZ) {
						var4 = bc;
					}

					if (colorFilter == aY) {
						var4 = bb;
					}

					if (colorFilter == ba) {
						var4 = bd;
					}
				}
			} else if (boolean3) {
				if (this.z == null) {
					this.z = a(true);
				}

				var4 = this.z;
				var5 = this.A;
				this.A = integer;
			} else {
				if (this.x == null) {
					this.x = a(false);
				}

				var4 = this.x;
				var5 = this.y;
				this.y = integer;
			}

			if (var5 != integer) {
				var4.b(integer);
			}

			if (var4.h() != colorFilter) {
				var4.a(colorFilter);
			}

			return (Paint)var4;
		}
	}

	public static strictfp UniquePaint a(boolean boolean1) {
		UniquePaint var1 = new UniquePaint();
		if (boolean1) {
			var1.setAntiAlias(true);
			var1.d(true);
			var1.b(true);
		} else {
			var1.setAntiAlias(false);
			var1.d(false);
			var1.b(false);
		}

		return var1;
	}

	public strictfp OrderableUnit(boolean boolean1) {
		super(boolean1);
	}

	public final strictfp void j(int integer) {
		int var2 = this.getTurretCount();

		for (int var3 = 0; var3 < var2; var3++) {
			this.cL[var3].a(integer);
		}
	}

	public strictfp void a(String string) {
		String var2;
		if (this.r() != null) {
			var2 = this.r().i();
		} else {
			var2 = "<NO UNIT TYPE>";
		}

		GameEngine.log("(Unit log:" + var2 + " id:" + this.eh + "): " + string);
	}

	public strictfp void debugPrintTurretState() {
		String var1;
		if (this.r() != null) {
			var1 = this.r().i();
		} else {
			var1 = "<NO UNIT TYPE>";
		}

		GameEngine.log("---- Debug for:" + var1 + " id:" + this.eh + "---");
	}

	@Override
	public strictfp void update(float float1) {
		super.a(float1);
		if (this.positionDirty) {
			this.positionDirty = false;
		}

		if (this.cl != 0.0F) {
			this.cl = CommonUtils.applyDeadzone(this.cl, float1);
		}

		if (!this.bV && this.bT()) {
			GameEngine var2 = GameEngine.getInstance();
			if (this.noTurnTimer > 0.0F) {
				this.noTurnTimer = CommonUtils.applyDeadzone(this.noTurnTimer, float1);
			}

			if (this.aA > 0.0F) {
				this.aA = CommonUtils.applyDeadzone(this.aA, float1);
			}

			if (this.statusEffects != null) {
				StatusEffectManager.a(this, float1);
			}

			float var3 = this.eo;
			float var4 = this.ep;
			int var5 = this.getTurretCount();

			for (int var6 = 0; var6 < var5; var6++) {
				UnitTurretInstance var7 = this.cL[var6];
				if (var7.d == 0.0F) {
					float var8 = this.getTurretDefaultAngle(var6);
					if (this.b(var6, float1) && var7.angle != var8) {
						float var9 = CommonUtils.clampAngleDifference(var7.angle, var8, 360.0F);
						if (CommonUtils.abs(var9) < 0.5F) {
							var7.d = 20.0F;
							var7.c = 0.0F;
						} else {
							this.a(float1, var8, var6);
						}
					}
				} else {
					var7.d = CommonUtils.applyDeadzone(var7.d, float1);
				}
			}

			if (!this.bk()) {
				this.processActiveWaypoint(float1);
			}

			for (int var16 = 0; var16 < var5; var16++) {
				UnitTurretInstance var18 = this.cL[var16];
				if (var18.reloadTimer != 0.0F) {
					var18.reloadTimer = CommonUtils.applyDeadzone(var18.reloadTimer, float1);
				}
			}

			boolean var17 = this.isMoveSlidingMode();
			boolean var19 = false;
			var19 = this.cc != 0.0F || this.cd != 0.0F;
			if ((this.cf != 0.0F || var19) && this.canReceiveOrders()) {
				float var21 = this.cg;
				float var22 = this.z();
				if (this.isMoveIgnoringBody()) {
					var21 = this.ch;
				}

				if (!var17) {
					float var10 = var22 * this.cf * float1;
					var3 += CommonUtils.cos(var21) * var10;
					var4 += CommonUtils.sin(var21) * var10 * this.getMoveYAxisScaling();
					if (var19) {
						var3 += this.cc * float1;
						var4 += this.cd * float1 * this.getMoveYAxisScaling();
						float var11 = CommonUtils.distanceSquared(0.0F, 0.0F, this.cc, this.cd);
						if (var11 > var22 * var22) {
							this.cc = (float)(this.cc - this.cc * 0.05 * float1);
							this.cd = (float)(this.cd - this.cd * 0.05 * float1);
						}

						this.cc = CommonUtils.approachValue(this.cc, 0.0F, 0.5F * var22 * float1);
						this.cd = CommonUtils.approachValue(this.cd, 0.0F, 0.5F * var22 * float1);
					}
				} else {
					float var12;
					float var23;
					float var24;
					if (this.cf != 0.0F) {
						var23 = this.getMoveAccelerationSpeed() * 1.41F;
						var24 = CommonUtils.cos(var21) * var22 * this.cf;
						var12 = CommonUtils.sin(var21) * var22 * this.cf;
					} else {
						var23 = this.getBuildSpeed() * 1.41F;
						var24 = 0.0F;
						var12 = 0.0F;
					}

					float var13 = CommonUtils.distanceSquared(this.cc, this.cd, var24, var12);
					if (var13 > var22 * var22) {
						this.cc = (float)(this.cc - this.cc * 0.05 * float1);
						this.cd = (float)(this.cd - this.cd * 0.05 * float1);
					}

					float var14 = var23 * float1;
					if (var13 < var14 * var14) {
						this.cc = var24;
						this.cd = var12;
					} else {
						float var15 = CommonUtils.angleBetweenPoints(this.cc, this.cd, var24, var12);
						this.cc = this.cc + CommonUtils.cos(var15) * var14;
						this.cd = this.cd + CommonUtils.sin(var15) * var14;
					}

					var3 += this.cc * float1;
					var4 += this.cd * float1 * this.getMoveYAxisScaling();
				}

				this.positionDirty = true;
			}

			if (this.bZ != 0.0F || this.ca != 0.0F) {
				this.bZ = CommonUtils.clamp(this.bZ, -9.0F, 9.0F);
				this.ca = CommonUtils.clamp(this.ca, -9.0F, 9.0F);
				var3 += this.bZ;
				var4 += this.ca;
				this.ca = 0.0F;
				this.bZ = 0.0F;
				this.positionDirty = true;
			}

			if (this.positionDirty && this.canReceiveOrders() && this.cO == null) {
				this.applyCollisionDriftMove(float1, var2, var3, var4);
			}

			if (this.ax) {
				this.ax = false;
				this.updateFogOfWar(false);
				this.positionDirty = true;
			}
		}
	}

	private strictfp void applyCollisionDriftMove(float float1, GameEngine l, float float3, float float4) {
		Map var5 = lx.map;
		float var6 = var5.r;
		float var7 = var5.s;
		float var8 = this.eo * var6;
		float var9 = this.ep * var7;
		float var10 = float3 * var6;
		float var11 = float4 * var7;
		PointF var12 = null;
		boolean var13 = false;
		int var14 = CommonUtils.floor(var8);
		int var15 = CommonUtils.floor(var9);
		int var16 = CommonUtils.floor(var10);
		int var17 = CommonUtils.floor(var11);
		if ((var14 != var16 || var15 != var17) && this.cl == 0.0F && lx.pathEngine.isBlocked(this.h(), var16, var17)) {
			if (var14 != var16 && var15 != var17) {
				boolean var18 = lx.pathEngine.isBlocked(this.h(), var14, var17);
				boolean var19 = lx.pathEngine.isBlocked(this.h(), var16, var15);
				if (var18 && var19) {
					var13 = true;
					aG.a(var8, var9);
					var12 = aG;
				}

				if (var12 == null && var18) {
					var12 = UnitFunctions.a(this.h(), var8, var9, var10, var11, var14, var17, false);
				}

				if (var12 == null && var19) {
					var12 = UnitFunctions.a(this.h(), var8, var9, var10, var11, var16, var15, false);
				}
			}

			if (var12 == null) {
				var12 = UnitFunctions.a(this.h(), var8, var9, var10, var11, var16, var17, false);
			}

			if (var12 == null) {
				var13 = true;
				aG.a(var8, var9);
				var12 = aG;
			}
		}

		boolean var21 = false;
		if (var12 != null) {
			boolean var22 = false;
			boolean var20 = lx.pathEngine.isBlocked(this.h(), var14, var15);
			if (var20 && !lx.pathEngine.isBlockedIgnoreUnits(this.h(), var16, var17)) {
				var22 = true;
			}

			if (!var22) {
				float3 = var12.a * var5.n;
				float4 = var12.b * var5.o;
				var21 = true;
			} else {
				var13 = false;
			}
		}

		if (var21) {
			this.b += float1;
			this.a = 0;
		} else if (this.b != 0.0F && float1 > 0.0F) {
			this.a++;
			if (this.a >= 3) {
				this.b = 0.0F;
			}
		}

		if (!var13) {
			int var23 = CommonUtils.floor(float3 * var6);
			int var24 = CommonUtils.floor(float4 * var7);
			this.eo = float3;
			this.ep = float4;
			if (var14 != var23 || var15 != var24) {
				this.updateFogOfWar(true);
			}
		}
	}

	public strictfp void b(float float1, float float2) {
		Map var3 = GameEngine.getInstance().map;
		float var4 = var3.r;
		float var5 = var3.s;
		int var6 = CommonUtils.floor(this.eo * var4);
		int var7 = CommonUtils.floor(this.ep * var5);
		int var8 = CommonUtils.floor(float1 * var4);
		int var9 = CommonUtils.floor(float2 * var5);
		this.eo = float1;
		this.ep = float2;
		if (var6 != var8 || var7 != var9) {
			this.updateFogOfWar(true);
		}
	}

	public static strictfp void updateAllCollisions(float float1) {
		GameEngine var1 = GameEngine.getInstance();
		var1.timingStatTimer.startTimer(TimingStatTimerIds.update_do_all_collisions);
		int var2 = var1.gameTimeMillis;
		UnitList var3 = aM;
		Unit[] var4 = Unit.unitList.items();
		int var5 = Unit.unitList.size();

		for (int var6 = 0; var6 < var5; var6++) {
			var4[var6].bR();
		}

		for (int var13 = 0; var13 < var5; var13++) {
			if (var4[var13] instanceof OrderableUnit) {
				OrderableUnit var7 = (OrderableUnit)var4[var13];
				if ((var7.positionDirty || var7.cb) && var7.canReceiveOrders() && var7.nextCollisionCheckMillis <= var2) {
					var7.cb = false;
					var7.positionDirty = true;
					float var8;
					if (var7.cK) {
						var8 = var7.cj + 7.0F;
						if (var7.nearbyCollisionCount > 9) {
							var7.nextCollisionCheckMillis = var2 + 200 + var13 % 50;
						} else {
							var7.nextCollisionCheckMillis = var2 + 50 + var13 % 50;
						}
					} else {
						var8 = var7.cj + 5.0F;
						var7.nextCollisionCheckMillis = var2 + 250 + var13 % 50;
					}

					var7.nearbyCollisionCount = 0;
					var3.clear();
					var1.unitGeoIndex.b(var7.eo, var7.ep, var8, var3);
					Unit[] var9 = var3.items();
					int var10 = 0;

					for (int var11 = var3.size; var10 < var11; var10++) {
						Unit var12 = var9[var10];
						var7.resolveUnitCollision(var12, float1, true);
					}

					if (var7.nearbyCollisionCount > 9 && var7.bz > var2 - 400) {
						var7.nextCollisionCheckMillis = var1.gameTimeMillis + 5 + var13 % 5;
						var7.cb = true;
					}
				}
			}
		}

		var1.timingStatTimer.stopTimer(TimingStatTimerIds.update_do_all_collisions);
		var1.timingStatTimer.startTimer(TimingStatTimerIds.update_do_all_collisions2);

		for (int var14 = 0; var14 < var5; var14++) {
			if (var4[var14] instanceof OrderableUnit) {
				OrderableUnit var15 = (OrderableUnit)var4[var14];
				if (var15.positionDirty) {
					byte var16 = var15.nearbyCollisionCount;
					if (var16 > 0 && var15.canReceiveOrders()) {
						if (!var15.cb) {
							var15.cb = true;
						}

						for (int var17 = 0; var17 < var16; var17++) {
							Unit var18 = var15.nearbyCollisionUnits[var17];
							var15.resolveUnitCollision(var18, float1, false);
						}
					}
				}
			}
		}

		var1.timingStatTimer.stopTimer(TimingStatTimerIds.update_do_all_collisions2);
	}

	private final strictfp void resolveUnitCollision(Unit am, float float2, boolean boolean3) {
		if (amx != this) {
			int var5 = this.bU;
			if (var5 != -1 && var5 == amx.bU) {
				if (this.bQ != amx && amx.bQ != this) {
					float var6 = this.eo + this.bZ;
					float var7 = this.ep + this.ca;
					float var8 = amx.eo + amx.bZ;
					float var9 = amx.ep + amx.ca;
					float var10 = CommonUtils.distanceSquared(var6, var7, var8, var9);
					float var11 = this.cj + amx.radius;
					if (boolean3) {
						float var30 = var10;
						if (var10 < var11 * var11) {
							var30 = 0.0F;
						}

						if (amx instanceof OrderableUnit) {
							OrderableUnit var31 = (OrderableUnit)amx;
							byte var34 = var31.nearbyCollisionCount;

							for (int var36 = 0; var36 < var34; var36++) {
								if (var31.nearbyCollisionUnits[var36] == this) {
									return;
								}
							}
						}

						if (this.nearbyCollisionUnits == null) {
							this.nearbyCollisionUnits = new Unit[10];
							this.nearbyCollisionDists = new float[10];
						}

						Unit[] var32 = this.nearbyCollisionUnits;
						float[] var35 = this.nearbyCollisionDists;
						int var37 = -1;

						for (int var38 = 0; var38 < this.nearbyCollisionCount; var38++) {
							if (var30 < var35[var38]) {
								var37 = var38;
								break;
							}
						}

						if (var37 == -1) {
							if (this.nearbyCollisionCount >= var32.length) {
								return;
							}

							var37 = this.nearbyCollisionCount;
						}

						if (this.nearbyCollisionCount < var32.length) {
							this.nearbyCollisionCount++;
						}

						for (int var39 = this.nearbyCollisionCount - 1; var39 > var37; var39--) {
							var32[var39] = var32[var39 - 1];
						}

						var32[var37] = amx;
						var35[var37] = var30;
					} else {
						if (var10 < var11 * var11 && !amx.a(this, float2) && !this.a(amx, float2)) {
							float var12 = CommonUtils.angleBetweenPoints(var6, var7, var8, var9);
							float var13 = (float)Math.sqrt(var10);
							float var14 = var11 - var13 + 0.001F;
							if (var14 <= 0.0F) {
								return;
							}

							int var15 = this.s(amx);
							int var16 = amx.s(this);
							int var17 = var15 > var16 ? var15 : var16;
							if (var17 != 0) {
								float var18 = var14 / var17 * float2;
								if (var18 > var14) {
									var18 = var14;
								}

								var14 = var18;
							}

							var14 *= 0.95F;
							if (var14 > 1.0F) {
								var14 *= 0.7F;
							}

							if (var14 > 3.0F) {
								var14 = 3.0F + (var14 - 3.0F) * 0.7F;
							}

							if (var14 > 6.0F) {
								var14 = 6.0F + (var14 - 6.0F) * 0.7F;
							}

							if (var14 > 10.0F) {
								var14 = 10.0F + (var14 - 10.0F) * 0.7F;
							}

							float var40 = 0.0F;
							float var19 = this.bN();
							float var20 = amx.getDamageOutput();
							OrderableUnit var21 = null;
							if (amx instanceof OrderableUnit) {
								var21 = (OrderableUnit)amx;
							}

							if (this.bX == amx.team) {
								boolean var22 = false;
								float var23 = 1.7F;
								if (var21 != null) {
									if (this.pathStuckTimer > 200.0F || var21.pathStuckTimer > 200.0F) {
										var23 = 5.0F;
									}

									if (this.queueLeader == var21) {
										var20 *= var23;
										var22 = true;
									}

									if (var21.queueLeader == this) {
										var19 *= var23;
										var22 = true;
									}

									if (!var22) {
										if (this.inFormation && var21.queueLeader != null) {
											var19 *= var23;
										} else if (var21.inFormation && this.queueLeader != null) {
											var20 *= var23;
										} else if (this.c == 0.0F && var21.c != 0.0F) {
											var19 *= var23;
										} else if (var21.c == 0.0F && this.c != 0.0F) {
											var20 *= var23;
										}
									}
								}
							}

							if (amx instanceof MovableUnit) {
								var40 = var19 / (var19 + var20);
							}

							float var41 = 1.0F - var40;
							float var42 = CommonUtils.cos(var12);
							float var24 = CommonUtils.sin(var12);
							if (amx instanceof MovableUnit) {
								float var25 = var14 * var40;
								amx.bZ += var42 * var25;
								amx.ca += var24 * var25;
							}

							float var43 = var14 * var41;
							this.bZ -= var42 * var43;
							this.ca -= var24 * var43;
							GameEngine var26 = GameEngine.getInstance();
							int var27 = var26.gameTimeSeconds;
							this.lastCollisionUnit = amx;
							this.lastCollisionTimeSeconds = var27;
							if (var21 != null) {
								var21.lastCollisionUnit = this;
								var21.lastCollisionTimeSeconds = var27;
								if (this.ac != 0 && this.ac == var21.ac) {
									if (this.getCurrentWaypoint() == null) {
										Waypoint var29 = var21.getCurrentWaypoint();
										if (var29 != null && (var29.type == WaypointType.move || var29.type == WaypointType.attackMove)) {
											var21.popWaypoint();
										}
									}

									if (var21.getCurrentWaypoint() == null) {
										Waypoint var44 = this.getCurrentWaypoint();
										if (var44 != null && (var44.type == WaypointType.move || var44.type == WaypointType.attackMove)) {
											this.popWaypoint();
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public strictfp int getTechLevel() {
		return 1;
	}

	public strictfp void a(int integer) {
	}

	protected strictfp void W() {
		GameEngine var1 = GameEngine.getInstance();
		if (this.bX == var1.playerTeam) {
			var1.interfaceEngine.warLogInterface.addUnitUpgradedEntry(this);
		}
	}

	public strictfp float b(float float1, float float2, float float3) {
		if (this.isFixedFiring()) {
			if (this.bI()) {
				return 0.0F;
			} else {
				float var8 = CommonUtils.angleBetweenPoints(this.eo, this.ep, float2, float3);
				return this.c(float1, var8);
			}
		} else if (this.getTurretCount() < 1) {
			return 0.0F;
		} else {
			int var4 = this.getUpgradeTechLevel();
			if (var4 == -1) {
				var4 = 0;
			}

			PointF var5 = this.G(var4);
			float var6 = CommonUtils.angleBetweenPoints(var5.a, var5.b, float2, float3);
			UnitTurretInstance var7 = this.cL[var4];
			var7.a(70);
			return this.a(float1, var6, var4);
		}
	}

	public strictfp float c(float float1, float float2) {
		boolean var3 = false;
		boolean var4 = false;
		if (this.ci && this.bb()) {
			var3 = true;
			var4 = true;
		}

		return this.a(float1, float2, var3, var4);
	}

	@Override
	public strictfp void setAngle(float float1) {
		float var2 = CommonUtils.clampAngleDifference(this.cg, float1, 360.0F);
		if (CommonUtils.abs(var2) > 0.01) {
			this.i(var2);
		}
	}

	public strictfp float a(float float1, float float2, boolean boolean3, boolean boolean4) {
		this.ch = float2;
		if (CommonUtils.abs(this.cg - float2) < 0.01F) {
			if (boolean3 && this.ci) {
				this.j(25);
				this.ci = false;
			}

			return 0.0F;
		} else {
			float var5 = CommonUtils.clampAngleDifference(this.cg, float2, 360.0F);
			if (boolean3) {
				if (boolean4 && CommonUtils.abs(var5) > 100.0F) {
					var5 = CommonUtils.clampAngleDifference(this.cg, float2 + 180.0F, 360.0F);
					if (!this.ci) {
						this.j(25);
						this.ci = true;
					}
				} else if (this.ci) {
					this.j(25);
					this.ci = false;
				}
			}

			if (CommonUtils.abs(var5) < 0.01F) {
				return 0.0F;
			} else {
				if (this.noTurnTimer <= 0.0F) {
					float var6 = this.getTurnAcceleration();
					if (var6 <= 0.0F) {
						float var7 = var5 > 0.0F ? 1.0F : -1.0F;
						float var8 = var7 * this.A() * float1;
						if (CommonUtils.abs(var8) > CommonUtils.abs(var5)) {
							var8 = var5;
						}

						this.i(var8);
					} else {
						float var10 = var5 > 0.0F ? 1.0F : -1.0F;
						float var11 = CommonUtils.abs(this.ce) / var6;
						if (CommonUtils.abs(var5) < var11) {
							this.ce = CommonUtils.approachValue(this.ce, var10 * var6, var6 * float1);
						} else {
							this.ce = CommonUtils.approachValue(this.ce, var10 * this.A(), var6 * float1);
						}

						float var9 = this.ce * float1;
						if (CommonUtils.abs(var9) > CommonUtils.abs(var5)) {
							this.ce = 0.0F;
							var9 = var5;
						}

						this.i(var9);
					}
				}

				return var5;
			}
		}
	}

	public strictfp void i(float float1) {
		this.cg += float1;
		if (this.cg > 180.0F) {
			this.cg -= 360.0F;
		}

		if (this.cg < -180.0F) {
			this.cg += 360.0F;
		}

		if (this.turretRotateWithBody()) {
			int var2 = this.getTurretCount();

			for (int var3 = 0; var3 < var2; var3++) {
				UnitTurretInstance var4 = this.cL[var3];
				var4.angle += float1;
				if (var4.angle > 180.0F) {
					var4.angle -= 360.0F;
				}

				if (var4.angle < -180.0F) {
					var4.angle += 360.0F;
				}
			}
		}
	}

	public strictfp void j(float float1) {
		int var2 = this.getTurretCount();

		for (int var3 = 0; var3 < var2; var3++) {
			UnitTurretInstance var4 = this.cL[var3];
			var4.angle = float1 + this.getTurretIdleDir(var3);
		}
	}

	public strictfp void a(int integer, float float2) {
		UnitTurretInstance var3 = this.cL[integer];
		var3.angle += float2;
	}

	public strictfp float a(float float1, float float2, int integer) {
		UnitTurretInstance var4 = this.cL[integer];
		float var5 = var4.angle;
		float var6 = CommonUtils.clampAngleDifference(var5, float2, 360.0F);
		if (var6 == 0.0F) {
			return var6;
		} else {
			float var7 = this.getTurretTurnSpeedAcceleration(integer);
			if (var7 <= 0.0F) {
				float var8 = CommonUtils.clampAngleDifference(var4.angle, float2, this.getTurretTurnSpeed(integer) * float1);
				this.a(integer, var8);
				var6 -= var8;
			} else {
				float var14 = this.getTurretTurnSpeedDeceleration(integer);
				float var9 = var6 > 0.0F ? 1.0F : -1.0F;
				float var10 = CommonUtils.abs(var4.c) / var14;
				boolean var11 = var6 > 0.0F == var4.c > 0.0F;
				if (CommonUtils.abs(var6) < var10 && var11) {
					var4.c = CommonUtils.approachValue(var4.c, var9 * var14, var14 * float1);
				} else {
					var4.c = CommonUtils.approachValue(var4.c, var9 * this.getTurretTurnSpeed(integer), var7 * float1);
				}

				float var12 = var4.c * float1;
				if (CommonUtils.abs(var12) > CommonUtils.abs(var6)) {
					var4.c = 0.0F;
					var12 = var6;
				}

				this.a(integer, var12);
				var6 -= var12;
			}

			return var6;
		}
	}

	public strictfp Unit X() {
		if (this.workBeamActive) {
			Waypoint var1 = this.getCurrentWaypoint();
			if (var1 != null && (var1.type == WaypointType.repair || var1.type == WaypointType.reclaim) && var1.targetUnit != null && !var1.targetUnit.dead) {
				return var1.targetUnit;
			}
		}

		return null;
	}

	public strictfp boolean Y() {
		Waypoint var1 = this.getCurrentWaypoint();
		return var1 != null && var1.type == WaypointType.reclaim;
	}

	private strictfp void processTriggerActionWaypoint(float float1, Waypoint au, ad ad) {
		if (aux.actionId == null) {
			this.popWaypoint();
			aux = null;
		}

		if (aux != null) {
			boolean var4 = true;
			if (aux.type == WaypointType.triggerActionWhenInRange) {
			}

			if (var4) {
				SpecialAction var5 = this.a(aux.actionId);
				this.debugPrintTurretState();
				if (var5 == null) {
					this.a("Failed to find action:" + aux.actionId.a());
				} else {
					PointF var6 = new PointF(aux.x, aux.y);
					this.a(var5, false, var6, aux.targetUnit);
				}

				this.popWaypoint();
				Object var7 = null;
			}
		}
	}

	private strictfp void processSetPassiveTargetWaypoint(float float1, Waypoint au, ad ad) {
		Unit var4 = aux.i();
		if (var4 != null) {
			this.attackTarget = var4;
			if (this.turretAssignTimer > 5.0F) {
				this.turretAssignTimer = 5.0F;
			}
		}

		this.popWaypoint();
		Object var5 = null;
	}

	private strictfp void processGuardFollowWaypoint(float float1, Waypoint au, ad ad) {
		float var4 = aux.g();
		float var5 = aux.h();
		float var6 = CommonUtils.distanceSquared(this.eo, this.ep, var4, var5);
		boolean var7 = aux.type == WaypointType.guard || aux.type == WaypointType.follow;
		boolean var8 = aux.type == WaypointType.guard;
		Unit var9 = aux.targetUnit;
		if (var7) {
			if (var9 == null || var9.dead) {
				this.popWaypoint();
				aux = null;
			}

			if (aux != null && var9 != null && !var9.cg() && this.bX.isEnemy(var9.team)) {
				this.popWaypoint();
				aux = null;
			}
		}

		if (aux != null) {
			boolean var10 = false;
			float var11 = this.cj;
			if (var7) {
				var11 += var9.radius;
			}

			if (aux.type == WaypointType.follow) {
				if (this.cK) {
					var11 += 30.0F;
				} else {
					var11 += 50.0F;
				}
			} else if (this.cK) {
				var11 += 80.0F;
			} else {
				var11 += 100.0F;
			}

			if (var6 > var11 * var11) {
				this.hasMoveTarget = true;
				this.moveTargetX = var4;
				this.moveTargetY = var5;
				this.moveTargetClearance = 2;
				if (this.pathFollowSpeed > 90.0F) {
					this.pathFollowSpeed = 90.0F;
				}

				this.moveGoalDriftTolerance = 18;
				if (this.queueLeader != null && !this.queueLeader.bT()) {
					adx.d = false;
				}
			} else {
				this.guardFollowState = 0;
			}

			adx.d = false;
			if (!var10 && this.attackTarget != null && !this.attackTarget.dead) {
				boolean var12 = false;
				if (this.canAutoAttackUnit(this.attackTarget, false)) {
					var12 = true;
				}

				if (var12) {
					float var13 = CommonUtils.distanceSquared(this.eo, this.ep, this.attackTarget.eo, this.attackTarget.ep);
					float var14 = this.o(this.attackTarget);
					boolean var15 = false;
					boolean var16 = false;
					if (var13 < var14 * var14) {
						var16 = true;
					}

					if (var16 && !this.aa()) {
						var16 = false;
					}

					if (var6 < 22500.0F) {
						this.guardFollowState = 0;
					}

					if (!var16 && (this.guardFollowState == 1 || var6 > 122500.0F)) {
						var15 = true;
						this.guardFollowState = 1;
					}

					if (var6 > 302500.0F || this.guardFollowState == 1 && var6 > 202500.0F) {
						var15 = true;
						this.guardFollowState = 1;
					}

					if (!var15) {
						var10 = true;
						this.guardFollowState = 0;
						if (var16) {
							this.hasMoveTarget = false;
						} else {
							if (this.pathFollowSpeed > 90.0F) {
								this.pathFollowSpeed = 90.0F;
							}

							this.hasMoveTarget = true;
							this.moveTargetX = this.attackTarget.eo;
							this.moveTargetY = this.attackTarget.ep;
							this.moveTargetClearance = 0;
							this.directMoveRequested = true;
						}
					}
				}
			}

			if (var8 && !var10) {
				Unit var19 = var9.getRecentAttacker(2.0F);
				if (var19 != null && !this.canAutoAttackUnit(var19, true)) {
					var19 = null;
				}

				if (var19 == null && this.guardFollowState != 1) {
					var19 = this.q(2.0F);
					if (var19 != null && !this.canAutoAttackUnit(var19, true)) {
						var19 = null;
					}
				}

				if (var19 != null) {
					var10 = true;
					if (this.pathFollowSpeed > 90.0F) {
						this.pathFollowSpeed = 90.0F;
					}

					this.hasMoveTarget = true;
					this.moveTargetX = var19.eo;
					this.moveTargetY = var19.ep;
					this.moveTargetClearance = 0;
					this.directMoveRequested = true;
				}
			}

			if (var8 && !var10 && this.canRepairUnit(var9) && (var9.hp < var9.maxHp || var9.buildProgress < 1.0F) && this.canRepairUnit(var9)) {
				Waypoint var20 = this.queueWaypointFront();
				if (var20 != null) {
					var20.b(var9);
					var20.m = true;
					var10 = true;
					if (this.pathFollowSpeed > 20.0F) {
						this.pathFollowSpeed = 20.0F;
					}
				}
			}

			if (var8 && !var10 && this.ak() && var9 instanceof OrderableUnit) {
				OrderableUnit var21 = (OrderableUnit)var9;
				Unit var22 = var21.X();
				if (var22 != null && this.canRepairUnit(var22)) {
					Waypoint var23 = this.queueWaypointFront();
					if (var23 != null) {
						var23.b(var22);
						var23.m = true;
						var10 = true;
						if (this.pathFollowSpeed > 20.0F) {
							this.pathFollowSpeed = 20.0F;
						}
					}
				}
			}
		}
	}

	private strictfp void processTouchTargetWaypoint(float float1, Waypoint au, ad ad) {
		float var4 = aux.g();
		float var5 = aux.h();
		float var6 = CommonUtils.distanceSquared(this.eo, this.ep, var4, var5);
		if (aux.targetUnit == null || aux.targetUnit.dead) {
			this.popWaypoint();
			aux = null;
		}

		if (aux != null) {
			boolean var7 = false;
			if (aux.targetUnit.isBuilding()) {
				if (var6 < 961.0F) {
					this.approachTimeoutTimer += float1;
				}

				if (this.approachTimeoutTimer > 240.0F) {
					var7 = true;
				}

				float var8 = 21.0F;
				if (aux.targetUnit.getFootprint().a()) {
					var8 = 11.0F;
				}

				if (this.b > 0.0F) {
					var8 = aux.targetUnit.radius + this.cj + 31.0F;
				}

				if (var6 < var8 * var8) {
					var7 = true;
				}
			} else {
				float var10 = aux.targetUnit.radius + this.cj + 5.0F;
				if (var6 < var10 * var10) {
					var7 = true;
				}
			}

			if (!var7) {
				this.hasMoveTarget = true;
				this.moveTargetX = var4;
				this.moveTargetY = var5;
				this.moveTargetClearance = 0;
				if (aux.targetUnit.isBuilding()) {
					Rect var11 = aux.targetUnit.getFootprint();
					int var9 = CommonUtils.min(var11.c() / 2, var11.b() / 2);
					this.moveTargetClearance = var9 + 1;
				}

				if (this.pathFollowSpeed > 90.0F) {
					this.pathFollowSpeed = 90.0F;
				}

				this.moveGoalDriftTolerance = 18;
				if (var6 < 48400.0F) {
					adx.d = false;
					if (this.pathFollowSpeed > 0.0F && this.getFirstPathNode() == null) {
						this.directMoveRequested = true;
					}
				}

				if (this.queueLeader != null && !this.queueLeader.bT()) {
					adx.d = false;
				}
			}

			if (var7) {
				Unit var12 = aux.targetUnit;
				this.a(UnitEventType.touchTargetSuccess, var12);
				this.popWaypoint();
			}
		}
	}

	private strictfp void processLoadUpWaypoint(float float1, Waypoint au, ad ad) {
		float var4 = aux.g();
		float var5 = aux.h();
		float var6 = CommonUtils.distanceSquared(this.eo, this.ep, var4, var5);
		if (aux.targetUnit == null || aux.targetUnit.dead || !aux.targetUnit.isBuiltAndNotTransported()) {
			this.popWaypoint();
			aux = null;
		}

		if (aux != null && !this.d(aux.targetUnit, false)) {
			this.popWaypoint();
		}

		if (aux != null) {
			this.bQ = aux.targetUnit;
			float var7 = this.cs();
			if (var6 > var7 * var7) {
				this.hasMoveTarget = true;
				this.moveTargetX = var4;
				this.moveTargetY = var5;
				if (this.pathFollowSpeed > 90.0F) {
					this.pathFollowSpeed = 90.0F;
				}

				this.moveGoalDriftTolerance = 18;
				if (var6 < 72900.0F) {
					adx.d = false;
					if (this.pathFollowSpeed > 0.0F && this.pendingPath == null) {
						this.directMoveRequested = true;
					}
				}

				if (this.queueLeader != null && !this.queueLeader.bT()) {
					adx.d = false;
				}
			} else {
				this.e(aux.targetUnit, false);
				this.popWaypoint();
			}
		}
	}

	private strictfp void processMoveWaypoint(float float1, Waypoint au, ad ad, boolean boolean4) {
		float var5 = aux.g();
		float var6 = aux.h();
		float var7 = CommonUtils.distanceSquared(this.eo, this.ep, var5, var6);
		GameEngine var8 = GameEngine.getInstance();
		if (!this.aR()) {
			boolean var9 = false;
			AttachmentSlot var10 = this.dn();
			if (var10 != null && var10.H) {
				this.bx();
				var9 = true;
			}

			if (!var9) {
				this.cancelCurrentWaypoint();
				aux = null;
			}
		}

		float var11 = 7.0F;
		if (var7 < 1681.0F) {
			this.approachTimeoutTimer += float1;
		}

		if (this.approachTimeoutTimer > 240.0F) {
			var11 = 16.0F;
		}

		if (this.approachTimeoutTimer > 340.0F) {
			var11 = 36.0F;
		}

		if (aux != null && aux.type == WaypointType.patrol) {
			if (this.getWaypointCount() != 1) {
				var11 = 20.0F;
				float var12 = 30.0F;
				if (!boolean4 || this.lastCollisionTimeSeconds == var8.gameTimeSeconds || this.lastCollisionTimeSeconds == var8.gameTimeSeconds - 1) {
					var12 = 70.0F;
				}

				if (var7 < var12 * var12) {
					this.d(aux);
					this.cancelCurrentWaypoint();
					aux = null;
				}
			} else {
				var11 = 30.0F;
				if (!boolean4 || this.lastCollisionTimeSeconds == var8.gameTimeSeconds || this.lastCollisionTimeSeconds == var8.gameTimeSeconds - 1) {
					var11 = 80.0F;
				}
			}
		}

		if (aux != null) {
			if (var7 < var11 * var11) {
				if (aux.type == WaypointType.patrol) {
					if (this.getWaypointCount() == 1) {
					}
				} else if (aux.type == WaypointType.attackMove) {
					boolean var13 = false;
					if (this.attackTarget != null && !this.attackTarget.dead && this.canAttackUnit(this.attackTarget, false)) {
						var13 = true;
					}

					if (!var13) {
						this.cancelCurrentWaypoint();
						aux = null;
					}
				} else {
					this.cancelCurrentWaypoint();
					aux = null;
				}
			} else {
				this.hasMoveTarget = true;
				this.moveTargetX = var5;
				this.moveTargetY = var6;
				this.moveTargetClearance = 0;
				if (aux.type == WaypointType.patrol) {
					this.lowPriorityPathFlag = true;
					this.clearFormationState();
				}
			}
		}

		if (aux != null) {
			if (aux.type == WaypointType.attackMove || aux.type == WaypointType.patrol) {
				if (this.attackTarget != null && !this.attackTarget.dead && this.canAttackUnit(this.attackTarget, false)) {
					this.chaseAttackTarget(float1, this.attackTarget, adx, true);
				}

				if (this.queueLeader != null && this.queueLeader.attackTarget != null) {
					adx.d = false;
				}
			}

			if (aux.type == WaypointType.patrol) {
				if (this.attackTarget == null) {
					Unit var14 = this.q(3.0F);
					if (var14 != null && this.canAutoAttackUnit(var14, true)) {
						if (this.pathFollowSpeed > 90.0F) {
							this.pathFollowSpeed = 90.0F;
						}

						this.hasMoveTarget = true;
						this.moveTargetX = var14.eo;
						this.moveTargetY = var14.ep;
						this.moveTargetClearance = 0;
						this.directMoveRequested = true;
					}
				}

				if (this.ak() && var8.gameTimeSeconds % 10 == this.eh % 10L) {
					Waypoint var15 = RepairBay.a(this, float1, 150.0F, true);
					if (var15 != null) {
						var15.m = false;
						var15.k = 200.0F;
						this.hasMoveTarget = false;
						this.resetPathState();
					}
				}
			}
		}
	}

	private strictfp void processBuildWaypoint(float float1, Waypoint au, ad ad) {
		float var4 = aux.g();
		float var5 = aux.h();
		float var6 = CommonUtils.distanceSquared(this.eo, this.ep, var4, var5);
		GameEngine var7 = GameEngine.getInstance();
		UnitTypeInterface var8 = aux.unitType;
		if (var8 == null) {
			this.a("activeBuildingType==null, removing waypoint");
			this.popWaypoint();
			aux = null;
		}

		if (aux != null) {
			float var9 = this.f(var8);
			byte var10 = 30;
			boolean var11 = false;
			if (var9 <= 30.0F) {
				var10 = 9;
			}

			if (var9 <= 25.0F && this.eq > 4.0F) {
				var11 = true;
			}

			if (this.queueLeader != null) {
				Waypoint var12 = this.queueLeader.getCurrentWaypoint();
				if (var12 == null || var12.type != WaypointType.build) {
					adx.d = false;
				}

				if (var12 != null && !aux.b(var12)) {
					adx.d = false;
				}
			}

			boolean var19 = false;
			if (!GameUtils.isTimeWindowOver(this.lastBuildFailMillis, 200)) {
				var19 = true;
			}

			boolean var13;
			if (var9 > 800000.0F) {
				var13 = true;
			} else {
				var13 = var6 <= var9 * var9;
			}

			if (var13 && !var11) {
				if (!var19 && (!this.b_() || !(CommonUtils.abs(this.b(float1, var4, var5)) > 30.0F))) {
					z var14 = this.a(aux, aux.unitType, aux.count, aux.x, aux.y);
					Unit var15 = null;
					if (var14.a != null) {
						var15 = var14.a;
					} else if (var14.b != null) {
						var15 = var14.b;
					}

					if (var15 != null) {
						var14.d.a(this, var15);
						if (this.canRepairUnit(var15)) {
							if (this.b(var15) > 10000.0F) {
								var15.setBuildProgress(1.0F);
								this.cancelCurrentWaypoint();
							} else {
								aux.e();
								aux.type = WaypointType.repair;
								aux.targetUnit = var15;
								this.resetPathState();
							}
						} else {
							this.popWaypoint();
						}

						this.lastBuildFailMillis = -9999;
					} else {
						if (aux.unitType == null) {
							GameEngine.log("active.build==null");
						}

						if (!var14.c) {
							this.popWaypoint();
						}
					}
				}
			} else if (!this.aR()) {
				this.popWaypoint();
				Object var16 = null;
			} else {
				this.hasMoveTarget = true;
				this.moveTargetX = var4;
				this.moveTargetY = var5;
				if (var9 > 58.0F) {
					this.moveTargetClearance = (int)((var9 - 41.0F) / (var7.map.n * 1.414F));
				}

				if (this.pathFollowSpeed > 90.0F) {
					this.pathFollowSpeed = 90.0F;
				}

				if (this.pathRetryCount > 3) {
					this.popWaypoint();
					Object var17 = null;
					return;
				}
			}
		}
	}

	private strictfp void chaseAttackTarget(float float1, Unit am, ad ad, boolean boolean4) {
		AttackMovement var5 = this.getAttackMovement();
		float var6 = amx.eo;
		float var7 = amx.ep;
		float var8 = CommonUtils.distanceSquared(this.eo, this.ep, var6, var7);
		if (this.queueLeader != null) {
			if (var8 < 490000.0F) {
				if (var8 < 48400.0F) {
					adx.d = false;
				}

				float var9 = CommonUtils.distanceSquared(this.queueLeader.eo, this.queueLeader.ep, var6, var7);
				if (var9 < 48400.0F) {
					adx.d = false;
				}

				if (var9 < 270400.0F && this.isMelee()) {
					adx.d = false;
				}
			}

			if (this.queueLeader.attackTarget == amx) {
				adx.d = false;
			}

			if (adx.d) {
				this.ai = 0.0F;
			} else {
				this.ai += float1;
			}
		} else {
			this.ai = 500.0F;
		}

		float var12 = this.o(amx);
		boolean var10 = true;
		if (var8 < var12 * var12) {
			if (this.attackTarget != amx) {
				if (UnitFunctions.a(this, amx)) {
					this.attackTarget = amx;
					this.targetSearchCooldown = 10.0F;
					this.M(-1);
				}
			} else {
				this.targetSearchCooldown = 10.0F;
			}

			float var11 = var12;
			if (!this.isFixedFiring()) {
				var11 = var12 - 1.0F;
				if (this.isMelee()) {
					var11 -= 2.0F;
				}

				if (this.getTurretWarmup(0) > 5.0F) {
					var11 -= 3.0F;
				}
			}

			if (var8 < var11 * var11 && this.getAttackMovement() != AttackMovement.bomber) {
				if (amx == null) {
					var10 = false;
				} else if (this.canFireAtNow(amx)) {
					var10 = false;
					if (boolean4) {
						this.hasMoveTarget = false;
					}
				} else if (!this.canFireAfterAim(amx)) {
					var10 = false;
				}
			}
		}

		if (var10) {
			this.hasMoveTarget = true;
			this.moveTargetX = var6;
			this.moveTargetY = var7;
			this.moveTargetClearance = 0;
			if (var5 == AttackMovement.bomber) {
				this.a(var8, var6, var7);
			}

			this.moveTargetClearance = this.q(amx);
			if (this.pathFollowSpeed > 90.0F) {
				this.pathFollowSpeed = 90.0F;
			}

			if (var8 < 810000.0F) {
				if (this.ct() || this.isMelee()) {
					this.directMoveRequested = true;
				}

				if (!adx.d && this.ai < 120.0F) {
					this.pathFollowSpeed = 0.1F;
					this.directMoveRequested = true;
				}
			}
		}
	}

	private strictfp void processAttackWaypoint(float float1, Waypoint au, ad ad) {
		GameEngine var4 = GameEngine.getInstance();
		AttackMovement var5 = this.getAttackMovement();
		if (var5 == AttackMovement.bomber) {
			if (aux != null && (aux.targetUnit == null || aux.targetUnit.dead || aux.targetUnit.team == this.bX) && !this.bomberSearchFailed) {
				if (this.attackTarget != null && this.attackTarget.dead) {
					this.attackTarget = null;
				}

				float var6 = this.getTargetSearchRange(true) + 200.0F;
				this.searchAttackTarget(var4, float1, var6);
				if (this.attackTarget != null) {
					aux.targetUnit = this.attackTarget;
					this.clearFormationState();
					this.resetPathState();
				} else {
					this.bomberSearchFailed = true;
					this.fleeing = true;
				}
			}

			if (aux != null && (aux.targetUnit == null || aux.targetUnit.dead || aux.targetUnit.team == this.bX)) {
				if (aux.targetUnit == null) {
					this.popWaypoint();
					aux = null;
				} else if (!this.fleeing) {
					this.popWaypoint();
					aux = null;
				}
			}
		} else if (aux.targetUnit == null || aux.targetUnit.dead || aux.targetUnit.team == this.bX) {
			boolean var9 = true;
			if (this.getWaypointCount() > 1) {
				var9 = false;
			}

			aux.targetUnit = null;
			if (var9) {
				if (this.attackTarget != null && this.attackTarget.dead) {
					this.attackTarget = null;
				}

				float var7 = this.getTargetSearchRange(true);
				this.searchAttackTarget(var4, float1, var7);
				if (this.attackTarget != null) {
					aux.targetUnit = this.attackTarget;
					this.clearFormationState();
					this.resetPathState();
				}
			}

			if (aux.targetUnit == null) {
				this.popWaypoint();
				aux = null;
			}
		}

		if (aux != null && aux.targetUnit != null && !aux.targetUnit.dead && !aux.targetUnit.cg() && this.bX.isEnemy(aux.targetUnit.team) && !UnitFunctions.b(this, aux.targetUnit)) {
			this.popWaypoint();
			Object var8 = null;
		} else {
			if (aux != null && !this.aR() && !this.l()) {
				this.popWaypoint();
				aux = null;
			}

			if (aux != null) {
				this.chaseAttackTarget(float1, aux.targetUnit, adx, false);
			}
		}
	}

	private strictfp void processLoadIntoWaypoint(float float1, Waypoint au, ad ad) {
		float var4 = aux.g();
		float var5 = aux.h();
		float var6 = CommonUtils.distanceSquared(this.eo, this.ep, var4, var5);
		if (aux.targetUnit == null || aux.targetUnit.dead) {
			this.popWaypoint();
			aux = null;
		}

		if (aux != null && !aux.targetUnit.canLoad(this, false)) {
			this.popWaypoint();
		}

		if (aux != null) {
			Unit var7 = aux.targetUnit;
			this.bQ = var7;
			boolean var8 = false;
			if (var7.isBuilding()) {
				float var9 = var7.cs();
				float var10 = var9 + 10.0F;
				if (var6 < var10 * var10) {
					this.approachTimeoutTimer += float1;
				}

				if (this.approachTimeoutTimer > 240.0F) {
					var8 = true;
				}

				float var11 = 21.0F;
				if (var7.getFootprint().a()) {
					var11 = 11.0F;
				}

				if (this.b > 0.0F) {
					var11 = var7.radius + 31.0F;
				}

				if (var6 < var11 * var11) {
					var8 = true;
				}
			} else {
				float var12 = var7.cs();
				if (var6 < var12 * var12) {
					var8 = true;
				}
			}

			if (!var8) {
				this.hasMoveTarget = true;
				this.moveTargetX = var4;
				this.moveTargetY = var5;
				if (this.pathFollowSpeed > 90.0F) {
					this.pathFollowSpeed = 90.0F;
				}

				this.moveGoalDriftTolerance = 18;
				if (var6 < 48400.0F) {
					adx.d = false;
					if (this.pathFollowSpeed > 0.0F && this.pendingPath == null) {
						this.directMoveRequested = true;
					}
				}

				if (this.queueLeader != null && !this.queueLeader.bT()) {
					adx.d = false;
				}
			}

			if (var8) {
				Unit var13 = aux.targetUnit;
				var13.loadUnit(this, false);
				this.popWaypoint();
			}
		}
	}

	public strictfp float a_(Unit am) {
		float var2 = amx.getUnitType().getBuildSpeed();
		if (amx.getTechLevel() == 2) {
			var2 *= 0.5F;
		}

		if (amx.getTechLevel() == 3) {
			var2 *= 0.25F;
		}

		return var2 * this.b(amx);
	}

	public strictfp float getNanoUnbuildSpeed(Unit am) {
		float var2 = 5.1F;
		return 0.001F * var2;
	}

	public strictfp CustomPrice g(Unit am) {
		return amx.by != null ? amx.by : amx.getUnitType().getStreamingPrice();
	}

	private strictfp void processRepairReclaimWaypoint(float float1, Waypoint au, ad ad) {
		GameEngine var4 = GameEngine.getInstance();
		boolean var5 = false;
		boolean var6 = false;
		if (aux != null) {
			float var7 = aux.g();
			float var8 = aux.h();
			float var9 = CommonUtils.distanceSquared(this.eo, this.ep, var7, var8);
			if (aux != null && aux.type == WaypointType.reclaim && aux.targetUnit != null && aux.targetUnit.getResourceRate() > 0.0F) {
				var6 = true;
			}

			if (aux != null && (aux.targetUnit == null || aux.targetUnit.dead || aux.targetUnit.transportedBy != null)) {
				if (var6) {
					var5 = true;
				} else {
					this.cancelCurrentWaypoint();
					aux = null;
				}
			}

			if (aux != null && !var5 && var6 && aux.targetUnit != null) {
				boolean var10 = true;
				if (this.lastReclaimTickMillis < var4.gameTimeMillis - 100) {
					var10 = false;
				}

				if (!this.g(aux.targetUnit, var10)) {
					var5 = true;
				}

				if (!var5) {
					this.lastReclaimTickMillis = var4.gameTimeMillis;
				}
			}

			if (aux != null && var5) {
				CustomTagTags var30 = null;
				if (aux.targetUnit != null) {
					var30 = aux.targetUnit.getSimilarResourcesHaveTag();
				}

				int var11 = this.cS();
				Unit var12 = a(this, aux.targetUnit.eo, aux.targetUnit.ep, var11, var30);
				if (var12 != null) {
					aux.targetUnit = var12;
					var7 = aux.g();
					var8 = aux.h();
					var9 = CommonUtils.distanceSquared(this.eo, this.ep, var7, var8);
					this.clearFormationState();
				} else {
					this.cancelCurrentWaypoint();
					aux = null;
				}
			}

			if (aux != null) {
				if (aux.type == WaypointType.repair) {
					if (!this.canRepairUnit(aux.targetUnit)) {
						this.popWaypoint();
						aux = null;
					}
				} else if (!var6 && !this.canReclaimUnit(aux.targetUnit)) {
					this.popWaypoint();
					aux = null;
				}
			}

			if (aux != null && aux.type == WaypointType.repair && aux.targetUnit != null && aux.targetUnit.hp >= aux.targetUnit.maxHp && aux.targetUnit.buildProgress >= 1.0F) {
				this.cancelCurrentWaypoint();
				aux = null;
			}

			if (aux != null && aux.targetUnit == this) {
				this.popWaypoint();
				aux = null;
			}

			if (aux != null && aux != null && aux.targetUnit != null && aux.targetUnit.getResourceRate() != 0.0F) {
				boolean var31 = false;
				if (aux.type == WaypointType.repair) {
					var31 = true;
				}

				if (var31) {
					this.popWaypoint();
					aux = null;
				}
			}

			if (aux != null && aux.type == WaypointType.reclaim && aux.targetUnit.team != this.bX && aux.targetUnit.getResourceRate() == 0.0F) {
				boolean var32 = true;
				if (var4.isSinglePlayer() && this.bX.isSameTeam(aux.targetUnit.team)) {
					var32 = false;
				}

				if (var32) {
					this.popWaypoint();
					aux = null;
				}
			}

			if (aux != null) {
				int var33;
				boolean var34;
				if (aux.type == WaypointType.reclaim) {
					var33 = this.v(aux.targetUnit);
					var34 = this.w(aux.targetUnit);
				} else {
					var33 = this.u(aux.targetUnit);
					var34 = this.x(aux.targetUnit);
				}

				if (this.queueLeader != null) {
					float var35 = CommonUtils.distanceSquared(this.queueLeader.eo, this.queueLeader.ep, var7, var8);
					int var13 = var33 + 80;
					if (var35 < var13 * var13) {
						adx.d = false;
					}

					Waypoint var14 = this.queueLeader.getCurrentWaypoint();
					if (var14 == null) {
						adx.d = false;
					}

					if (var14 != null && !aux.b(var14)) {
						adx.d = false;
					}
				}

				float var36 = var33;
				if (this.workBeamActive) {
					var36 += 5.0F;
				}

				byte var37 = 30;
				if (var33 <= 30) {
					var37 = 9;
				}

				if (var9 > var36 * var36) {
					if (this.aR() && aux.k != 0.0F) {
						boolean var39 = false;
						if (aux.k >= 0.0F) {
							float var15 = CommonUtils.fastSqrt((int)var9) - var36;
							if (aux.k < var15) {
								var39 = true;
							}
						}

						if (var39) {
							this.popWaypoint();
						} else {
							this.hasMoveTarget = true;
							this.moveTargetX = var7;
							this.moveTargetY = var8;
							if (var33 > 58) {
								this.moveTargetClearance = (int)((var33 - 41.0F) / (var4.map.n * 1.414F));
							} else {
								this.moveTargetClearance = 0;
							}

							if (var33 < 30 || var34) {
								if (var9 < 841.0F) {
									this.directMoveRequested = true;
								}

								float var41 = var33 + 14;
								if (var9 < var41 * var41 && this.pathFollowSpeed > 0.0F && this.pendingPath == null) {
									this.directMoveRequested = true;
								}
							}

							this.moveGoalDriftTolerance = this.moveTargetClearance;
							if (this.pathFollowSpeed > 90.0F) {
								this.pathFollowSpeed = 90.0F;
							}
						}
					} else {
						this.popWaypoint();
					}
				} else {
					int var40 = this.getUpgradeTechLevel();
					if (var40 == -1) {
						var40 = 0;
					}

					float var42 = 0.0F;
					if (this.b_()) {
						var42 = this.b(float1, var7, var8);
					}

					boolean var16 = false;
					if (CommonUtils.abs(var42) < 30.0F || !this.b_()) {
						this.workBeamActive = true;
						adx.a = true;
						UnitTurretInstance var17 = this.cL[var40];
						if (var17.f < this.getTurretWarmup(var40)) {
							var17.f += float1;
						} else {
							var17.f = this.getTurretWarmup(var40);
							var16 = true;
						}
					}

					if (var16) {
						Unit var43 = aux.targetUnit;
						if (aux.type != WaypointType.reclaim) {
							if (var43.buildProgress < 1.0F) {
								this.bC();
								float var18 = this.a_(var43);
								float var19 = var18 * float1;
								boolean var20 = false;
								boolean var21 = false;
								CustomPrice var22 = this.g(var43);
								if (var22 != null) {
									if (var43.buildProgress + var19 > 1.0F) {
										var19 = 1.0F - var43.buildProgress;
										var20 = true;
									}

									double var23 = var43.buildProgress + var19 - var43.cn;
									double var25 = 0.0;
									if (var20) {
										var25 = 1.0F - var43.cn;
									} else {
										double var27 = 0.001F;
										if (var23 >= var27) {
											int var29 = (int)(var23 / var27);
											var25 = var29 * var27;
										}
									}

									boolean var55 = false;
									if (var25 > 0.0 && this.bX.resourceList.a(var22)) {
										var55 = true;
									}

									if (var55 || !(var25 <= 0.0) && !var22.c(this, var25)) {
										if (!var55) {
											this.bX.resourceList.a(var22, this, var25);
										}

										var19 = 0.0F;
										var20 = false;
										var21 = true;
									} else {
										var43.cn = (float)(var43.cn + var25);
									}
								}

								if (!var21) {
									this.a(var43, float1, var40);
									float var49 = var43.buildProgress + var19;
									if (var20) {
										var49 = 1.0F;
									}

									var43.setBuildProgress(var49);
									if (var49 >= 1.0F && var18 < 0.3 && var43.team == var4.playerTeam) {
										var4.interfaceEngine.warLogInterface.addUnitCreatedEntry(var43);
									}

									this.insufficientResources = false;
								} else {
									this.insufficientResources = true;
								}
							} else {
								this.a(var43, float1, var40);
								var43.hp = var43.hp + this.c(var43) * float1;
								if (var43.hp > var43.maxHp) {
									var43.hp = var43.maxHp;
									this.popWaypoint();
								}

								this.insufficientResources = false;
							}
						} else {
							this.b(var43, float1, var40);
							this.insufficientResources = false;
							this.bC();
							boolean var44 = false;
							boolean var45 = this.y(var43);
							float var46 = this.z(var43);
							boolean var47 = aux.targetUnit.getResourceRate() > 0.0F;
							CustomPrice var48 = this.g(var43);
							if (!var47 && var48 != null) {
							}

							boolean var50 = false;
							if (!var47 && this.waypointTimer < 100.0F && !var47) {
								if (var43.buildProgress < 0.5) {
									if (var48 == null) {
										var50 = true;
									}
								} else if (var43.hp / var43.maxHp < 0.5) {
									var50 = true;
								}
							}

							if (!var50) {
								if (var43.buildProgress < 1.0F) {
									float var24 = this.getNanoUnbuildSpeed(var43) * float1;
									if (var24 >= var43.buildProgress) {
										var24 = var43.buildProgress;
										var43.buildProgress = 0.0F;
									} else {
										var43.buildProgress -= var24;
									}

									var43.cn = var43.buildProgress;
									if (var48 != null) {
										var48.a(this, (double)var24, true);
									}

									if (var43.buildProgress <= 0.0F) {
										var44 = true;
									}
								} else {
									float var51 = var46 * float1;
									if (var51 >= var43.hp) {
										var51 = var43.hp;
										var43.hp = -1.0F;
									} else {
										var43.hp -= var51;
									}

									var43.damageFlash = 1000.0F;
									if (var45) {
										float var54 = var51 / var43.maxHp;
										CustomPrice var26 = var43.getPrice();
										CustomPrice var56 = var43.getReclaimPrice();
										if (var56 != null) {
											var26 = var56;
										}

										if (!var47 && var48 != null) {
										}

										if (var26.a() > 0) {
											this.ab = this.ab + var54 * var26.a();
											if (this.ab > 1.0F) {
												this.bX.credits = this.bX.credits + (int)this.ab;
												this.ab = this.ab - (int)this.ab;
											}

											var26.a(this, (double)var54, false);
										} else {
											var26.a(this, (double)var54, true);
										}
									}

									if (var43.hp <= 0.0F) {
										var44 = true;
									}
								}
							}

							if (var44 && !var43.dead) {
								if (!var45) {
									CustomPrice var52 = var43.getReclaimPrice();
									if (var52 != null) {
										GameEngine.log("refund: " + var52.a(false, true, 10, true));
										var52.a(this, 1.0, true);
									} else {
										var52 = var43.getPrice();
										if (var43.bx != null) {
											var52 = var43.bx;
											GameEngine.log("refund==null overridePriceBuildCost: " + var52.a(false, true, 10, true));
										}

										var52.a(this, 0.8F, true);
										if (var43.buildProgress >= 1.0F && var48 != null) {
											var48.a(this, 0.8F, true);
										}
									}
								}

								var43.dead = true;
								var43.deathTime = var4.gameTimeMillis;
								var43.killAndRemove();
								if (var43 instanceof OrderableUnit && var43.isBuilding()) {
									var4.pathEngine.updateUnitCosts((OrderableUnit)var43);
								}
							}
						}
					}
				}
			}
		}
	}

	public strictfp void processActiveWaypoint(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		if (this.bQ != null) {
			this.bQ = null;
		}

		if (this.bR != null) {
			this.bS = CommonUtils.applyDeadzone(this.bS, float1);
			this.bQ = this.bR;
			if (this.bS == 0.0F) {
				this.bR = null;
			}
		}

		if (this.pathFollowSpeed != 0.0F) {
			this.pathFollowSpeed = CommonUtils.applyDeadzone(this.pathFollowSpeed, float1);
		}

		if (this.cf != 0.0F) {
			this.c = CommonUtils.applyDeadzone(this.c, float1);
		}

		Waypoint var3 = this.getCurrentWaypoint();
		this.directMoveRequested = false;
		boolean var4 = this.hasMoveTarget;
		this.hasMoveTarget = false;
		this.lowPriorityPathFlag = false;
		this.moveGoalDriftTolerance = 150;
		if (var3 != null && var3.l > 0.0F && var3.l < this.waypointTimer) {
			this.cancelCurrentWaypoint();
			var3 = null;
		}

		ad var5 = aP;
		var5.a();
		if (var3 != null) {
			this.waypointTimer += float1;
			WaypointType var6 = var3.type;
			if (var6 == WaypointType.move || var6 == WaypointType.attackMove || var6 == WaypointType.patrol) {
				this.processMoveWaypoint(float1, var3, var5, var4);
			} else if (var6 == WaypointType.attack) {
				this.processAttackWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.build) {
				this.processBuildWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.repair || var6 == WaypointType.reclaim) {
				this.processRepairReclaimWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.loadInto) {
				this.processLoadIntoWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.loadUp) {
				this.processLoadUpWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.touchTarget) {
				this.processTouchTargetWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.guard
				|| var6 == WaypointType.guardAt
				|| var6 == WaypointType.follow) {
				this.processGuardFollowWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.triggerAction || var6 == WaypointType.triggerActionWhenInRange) {
				this.processTriggerActionWaypoint(float1, var3, var5);
			} else if (var6 == WaypointType.setPassiveTarget) {
				this.processSetPassiveTargetWaypoint(float1, var3, var5);
			}

			if (var3 != this.getCurrentWaypoint()) {
				var3 = null;
			}
		}

		this.workBeamActive = var5.a;
		if (var3 != null && var3.m && this.waypointsCount > 1) {
			boolean var8 = true;
			Waypoint var7 = this.getWaypoint(1);
			if (var7 != null && (var7.type == WaypointType.guard || var7.type == WaypointType.patrol)) {
				var8 = false;
			}

			if (var8) {
				this.popWaypoint();
				var3 = null;
			}
		}

		if (var3 == null) {
			this.hasMoveTarget = false;
		}

		if (this.hasMoveTarget) {
			AttachmentSlot var9 = this.dn();
			if (var9 != null && var9.H) {
				this.bx();
			}
		} else if (this.pathRetryCount != 0) {
			this.pathRetryCount = 0;
		}

		this.updateTurretFiring(var2, float1);
		this.updateWaypointMovement(var2, float1, var3, var5);
	}

	private strictfp void a(float float1, float float2, float float3) {
		if (this.fleeHeading < -900.0F) {
			float var4 = CommonUtils.angleBetweenPoints(this.eo, this.ep, float2, float3);
			this.fleeHeading = var4;
		}

		if (float1 < 10000.0F && this.bX()) {
			this.fleeing = true;
		}

		if (this.fleeing) {
			if (!(this.cB < this.getMaxEnergy() * 0.6) && (!(float1 < 40000.0F) || !(this.cB < this.getMaxEnergy()))) {
				this.fleeing = false;
				this.fleeHeading = -999.0F;
				this.resetPathState();
			} else {
				this.moveTargetX = this.moveTargetX + CommonUtils.cos(this.fleeHeading + 180.0F) * 600.0F;
				this.moveTargetY = this.moveTargetY + CommonUtils.sin(this.fleeHeading + 180.0F) * 600.0F;
			}
		}
	}

	private strictfp void updateQueueFollow(float float1, af af, ad ad, Waypoint au) {
		GameEngine var5 = GameEngine.getInstance();
		OrderableUnit var6 = this.queueLeader;
		float var7 = var6.eo + this.formationOffsetX;
		float var8 = var6.ep + this.formationOffsetY;
		boolean var9 = false;
		int var10 = var5.gameTimeMillis - var6.an;
		float var11 = CommonUtils.distanceSquared(this.eo, this.ep, var7, var8);
		if (var10 > 300 || this.b > 1.0F) {
			this.d += float1;
		}

		boolean var12 = false;
		if (this.d > 300.0F) {
			var12 = true;
		}

		if (var10 > 300 && var11 > 250000.0F) {
			var12 = true;
		}

		if (this.b > 1.0F) {
			if (this.c != 0.0F) {
				var12 = true;
			}

			if (this.d > 10.0F) {
				var12 = true;
			}
		}

		if (var12) {
			this.c = 90.0F;
		}

		if (this.c == 0.0F) {
			this.resetPathState();
			adx.e = var7;
			adx.f = var8;
			af var13 = null;
			if (var10 < 3000 && var13 == null && var6.pathNodeTotal > 2 && var6.pathNodeTotal - var6.activePathCount <= 2) {
				var13 = var6.o(2);
			}

			if (var10 < 1500 && var13 == null && var6.pathNodeTotal > 0 && var6.activePathCount + 0 >= var6.pathNodeTotal) {
				af var14 = var6.o(0);
				var13 = aW;
				float var15 = CommonUtils.angleBetweenPoints(var6.eo, var6.ep, var14.a, var14.b);
				float var16 = 80.0F;
				if (var10 > 300) {
					float var17 = 0.06666667F;
					var16 -= (var10 - 300) * 0.06666667F;
				}

				var13.a = var6.eo + CommonUtils.cos(var15) * var16;
				var13.b = var6.ep + CommonUtils.sin(var15) * var16;
			}

			if (var13 != null) {
				adx.c = true;
				adx.e = var13.a + this.formationOffsetX;
				adx.f = var13.b + this.formationOffsetY;
			} else if (var6.pathNodeTotal >= 2 && var6.activePathCount >= 1) {
				af var26;
				af var29;
				if (var6.activePathCount >= 2) {
					var26 = var6.o(0);
					var29 = var6.o(1);
				} else {
					var26 = var6.o(0);
					var29 = var6.o(0);
				}

				if (var26 != null && var29 != null) {
					float var32 = CommonUtils.distanceInt(var6.eo, var6.ep, var26.a, var26.b);
					float var35 = 1.0F - (var32 - 15.0F) * 0.05F;
					if (var35 > 2.0F) {
						var35 = 2.0F;
					}

					if (var35 < 0.0F) {
						var35 = 0.0F;
					}

					float var38;
					float var42;
					if (var35 > 1.0F) {
						if (var6.activePathCount >= 3) {
							af var20 = var6.o(2);
							var38 = var29.a - var26.a;
							var42 = var29.b - var26.b;
							float var21 = var20.a - var29.a;
							float var22 = var20.b - var29.b;
							var38 += var21 * (var35 - 1.0F);
							var42 += var22 * (var35 - 1.0F);
						} else {
							var38 = var29.a - var26.a;
							var42 = var29.b - var26.b;
						}
					} else {
						float var46 = var29.a - var26.a;
						float var49 = var29.b - var26.b;
						var38 = var46 * var35;
						var42 = var49 * var35;
					}

					var7 = var26.a + this.formationOffsetX + var38;
					var8 = var26.b + this.formationOffsetY + var42;
					adx.e = var7;
					adx.f = var8;
				}
			}

			float var27 = 45.0F;
			if (this.b <= 1.0F) {
				var27 = 60.0F;
			} else if (var10 < 500 && this.b <= 1.0F) {
				var27 = 110.0F;
			}

			if (var11 < var27 * var27) {
				this.d = 0.0F;
			}

			boolean var30 = false;
			Waypoint var33 = var6.getCurrentWaypoint();
			boolean var36 = false;
			if (var33 != null && aux != null) {
			}

			if (var33 == null || var36) {
				this.e += float1;
				boolean var39 = false;
				if (aux != null
					&& (aux.type == WaypointType.move || aux.type == WaypointType.attackMove || aux.type == WaypointType.patrol)
					)
				 {
					var39 = true;
				}

				float var43;
				if (var39 && this.e > 600.0F) {
					var43 = 260.0F;
				} else if (var39 && this.e > 360.0F) {
					var43 = 140.0F;
				} else if (var39 && this.e > 180.0F) {
					var43 = 70.0F;
				} else if (var39 && this.e > 120.0F) {
					var43 = 50.0F;
				} else {
					var43 = 16.0F;
				}

				if (var11 < var43 * var43) {
					var30 = true;
				}

				if (var9) {
					var30 = true;
				}
			}

			if (var30) {
				boolean var40 = false;
				if (var33 == null) {
					var40 = true;
				}

				if (var36) {
					var40 = true;
				}

				if (var40) {
					float var44 = this.c(float1, this.am);
					if (CommonUtils.abs(var44) < 3.0F
						&& aux != null
						&& (aux.type == WaypointType.move || aux.type == WaypointType.attackMove)) {
						this.popWaypoint();
						if (var6 != null) {
							boolean var47 = false;
							Waypoint var50 = this.getCurrentWaypoint();
							Waypoint var51 = var6.getCurrentWaypoint();
							if (var50 != null && var51 != null && var50.b(var51)) {
								var47 = true;
							}

							if (!var47) {
								this.setQueueLeader(null);
							}
						}
					}
				}
			} else if (!var9) {
				adx.b = true;
			}
		} else {
			af var25 = null;
			byte var28 = 8;
			if (var25 == null && var6.pathNodeTotal > 2 && var28 < var6.activePathCount) {
				var25 = var6.o(var28);
			}

			if (var25 == null) {
				var25 = aW;
				var25.a = var6.eo;
				var25.b = var6.ep;
			}

			float var31 = CommonUtils.distanceSquared(this.eo, this.ep, var25.a, var25.b);
			float var34 = this.cj + var6.cj + 15.0F;
			float var37 = this.cj + var6.cj + 100.0F;
			if (var31 < var34 * var34) {
				this.d = 0.0F;
				this.c = 0.0F;
			} else if (var31 < var37 * var37) {
			}

			float var41 = 300.0F;
			boolean var45 = true;
			if (this.pendingPath == null
				&& afx != null
				&& (CommonUtils.abs(this.pathGoalX - var25.a) > 300.0F || CommonUtils.abs(this.pathGoalY - var25.b) > 300.0F)
				&& this.pathFollowSpeed > 30.0F) {
				this.pathFollowSpeed = 30.0F;
			}

			if (this.pathFollowSpeed == 0.0F && this.pendingPath == null) {
				this.pathFollowSpeed = 700.0F;
				boolean var48 = false;
				this.a(var25.a, var25.b, 0, false, var48);
			}

			if (afx != null) {
				adx.e = afx.a;
				adx.f = afx.b;
				if (!var9) {
					adx.b = true;
				}
			}
		}
	}

	private strictfp void updateWaypointMovement(GameEngine l, float float2, Waypoint au, ad ad) {
		boolean var5 = this.canReceiveOrders();
		if (this.pendingPath != null) {
			this.b(lx);
		}

		if (this.queueLeader != null && (this.queueLeader.bV || !this.queueLeader.bT())) {
			this.setQueueLeader(null);
		}

		if (this.hasMoveTarget) {
			af var6 = this.getFirstPathNode();
			Waypoint var7 = this.getCurrentWaypoint();
			if (var7 == null) {
				adx.d = false;
			}

			if (L) {
				adx.d = false;
			}

			if (this.inFormation && this.followerCount > 0 && this.isPathStuck()) {
				this.an = lx.gameTimeMillis;
			}

			if (var7 != null && this.queueLeader != null && adx.d) {
				Waypoint var8 = this.queueLeader.getCurrentWaypoint();
				if (var8 != null && !var8.b(var7)) {
					adx.d = false;
				}
			}

			if (this.queueLeader != null && adx.d) {
				this.updateQueueFollow(float2, var6, adx, aux);
			} else if (this.cl != 0.0F) {
				adx.e = this.moveTargetX;
				adx.f = this.moveTargetY;
				adx.b = true;
			} else {
				boolean var12 = false;
				if (this.pendingPath == null) {
					if (var6 == null) {
						if (this.pathTruncated && this.pathFollowSpeed < 450.0F && this.pendingPath == null) {
							var12 = true;
						}

						if (this.pathFollowSpeed == 0.0F) {
							var12 = true;
						}
					}

					if (this.pathFollowSpeed == 0.0F && (this.ct() || this.isMelee())) {
						float var9 = this.m() - 1.0F;
						if (CommonUtils.abs(this.pathGoalX - this.moveTargetX) > var9 || CommonUtils.abs(this.pathGoalY - this.moveTargetY) > var9) {
							var12 = true;
						}
					}

					if (aux != null && this.pathFollowSpeed == 0.0F && (aux.type == WaypointType.loadInto || aux.type == WaypointType.loadUp)) {
						float var13 = 12.0F;
						if (CommonUtils.abs(this.pathGoalX - this.moveTargetX) > var13 || CommonUtils.abs(this.pathGoalY - this.moveTargetY) > var13) {
							var12 = true;
						}
					}

					if (aux != null) {
						float var14 = this.moveGoalDriftTolerance;
						if (CommonUtils.abs(this.pathGoalX - this.moveTargetX) > var14 || CommonUtils.abs(this.pathGoalY - this.moveTargetY) > var14) {
							if (this.pathFollowSpeed > 30.0F) {
								this.pathFollowSpeed = 30.0F;
							}

							if (this.pathFollowSpeed == 0.0F) {
								var12 = true;
							}
						}
					}
				}

				if (var12) {
					this.pathFollowSpeed = 500.0F;
					boolean var15 = this.inFormation && this.formationSlotCount > 1;
					this.a(this.moveTargetX, this.moveTargetY, this.moveTargetClearance, var15, this.lowPriorityPathFlag);
				}

				if (var6 != null && this.flowFieldFollower == null && this.activePathCount >= 2 && this.z() > 5.0F) {
					af var16 = this.pathNodes[1];
					float var10 = CommonUtils.distanceSquared(this.eo, this.ep, var6.a, var6.b);
					float var11 = CommonUtils.distanceSquared(this.eo, this.ep, var16.a, var16.b);
					if (var10 < 36.0F) {
						this.aJ();
						var6 = this.getFirstPathNode();
					} else if (var11 < 361.0F) {
						this.aJ();
						var6 = this.getFirstPathNode();
					}
				}

				if (var6 != null) {
					adx.e = var6.a;
					adx.f = var6.b;
					adx.b = true;
				} else if (this.directMoveRequested) {
					adx.e = this.moveTargetX;
					adx.f = this.moveTargetY;
					adx.b = true;
				}
			}
		}

		this.updateMoveThrottle(float2, adx, aux, var5);
	}

	private strictfp void updateMoveThrottle(float float1, ad ad, Waypoint au, boolean boolean4) {
		float var5 = 0.0F;
		GameEngine var6 = GameEngine.getInstance();
		if (this.hasMoveTarget && adx.b && boolean4) {
			float var7 = adx.e;
			float var8 = adx.f;
			float var9 = this.z();
			float var10 = CommonUtils.distanceSquared(this.eo, this.ep, var7, var8);
			float var11 = CommonUtils.angleBetweenPoints(this.eo, this.ep, var7, (var8 - this.ep) * this.ba() + this.ep);
			boolean var12 = false;
			float var13 = this.getReverseSpeedPercentage();
			if (var13 > 0.95F) {
				var12 = true;
			} else if (var13 > 0.87) {
				if (this.formationSlotCount <= 1 && this.activePathCount > 0 && this.activePathCount <= 9 && this.inFormation && var10 < 250000.0F) {
					var12 = true;
				}
			} else if (var13 > 0.7) {
				if (this.formationSlotCount <= 1 && this.activePathCount > 0 && this.activePathCount <= 4 && this.inFormation && var10 < 40000.0F) {
					var12 = true;
				}
			} else if (var13 > 0.4 && this.formationSlotCount <= 1 && this.activePathCount > 0 && this.activePathCount <= 2 && this.inFormation && var10 < 10000.0F) {
				var12 = true;
			}

			boolean var14 = true;
			float var15 = 179.0F;
			if (this.attackTarget != null && this.isFixedFiring() && this.isMoveIgnoringBody() && !this.isMelee()) {
				this.ch = var11;
			} else if (this.noTurnTimer <= 0.0F) {
				var15 = this.a(float1, var11, var14, var12);
			}

			float var16 = 20.0F;
			if (var10 > 361.0F) {
				var16 = 46.0F;
			}

			if (var10 > 3600.0F) {
				var16 = 89.0F;
			}

			float var17 = this.A();
			if (var17 < 1.4) {
				if (var10 > 6400.0F) {
					var16 *= 0.5F;
				} else {
					var16 = 17.0F;
				}
			}

			if (var9 > 5.0F && this.cf < 0.01 && this.cf > -0.01) {
				var16 = 1.0F;
			}

			if (var17 < 1.1) {
				var16 *= 0.7F;
			}

			if (this.cf > 0.4 && var10 > 16900.0F) {
				var16 = 180.0F;
			}

			if (this.aY() && this.pathNodeTotal == this.activePathCount) {
				var16 = 1.0F;
			}

			if (this.isMoveIgnoringBody()) {
				var16 = 181.0F;
			}

			float var18 = 4.0F;
			boolean var19 = this.activePathCount == 1;
			if ((!var19 || var10 >= var18 * var18) && CommonUtils.abs(var15) <= var16) {
				var5 = 1.0F;
				if (adx.c) {
					if (var10 < 2500.0F) {
						var5 -= 0.15F;
					}

					if (var10 < 900.0F) {
						var5 -= 0.15F;
					}

					if (var10 < 225.0F) {
						var5 -= 0.3F;
					}
				} else if (this.queueLeader != null) {
					if (var10 > 400.0F) {
						var5 += 0.2F;
					}

					if (var10 < 49.0F) {
						var5 -= 0.15F;
					}

					if (var10 < 9.0F) {
						var5 -= 0.15F;
					}
				}

				if (var10 < 9.0F) {
					var5 = 0.0F;
				}
			}

			if (var19 && var5 != 0.0F) {
				if (var10 < 324.0F && this.getBuildSpeed() < 0.13F && this.z() > 1.0F) {
					var5 = 0.5F * var5;
				}

				if (var10 < 169.0F && this.getBuildSpeed() < 0.15F && this.z() > 0.9F) {
					var5 = 0.5F * var5;
				}

				if (var9 > 5.0F) {
					if (var10 < 324.0F && var5 > 0.5F) {
						var5 = 0.5F;
					}

					if (var10 < 81.0F && var5 > 0.25F) {
						var5 = 0.25F;
					}
				}
			}

			boolean var20 = false;
			if (!var19 && var10 < 256.0F) {
				var20 = true;
			}

			if (var19 && var10 < var18 * var18) {
				var20 = true;
			}

			if ((this.lastCollisionTimeSeconds == var6.gameTimeSeconds || this.lastCollisionTimeSeconds == var6.gameTimeSeconds - 1) && this.lastCollisionUnit != null && this.lastCollisionUnit.isPointWithinRadius(var7, var8, 2.0F)) {
				var20 = true;
			}

			if (var5 > 0.0F) {
				this.pathStuckTimer += float1;
				if (this.pathStuckTimer > 200.0F && var10 < 3600.0F && this.activePathCount >= 2) {
					float var21 = this.pathStuckTimer;
					this.aJ();
					this.pathStuckTimer = var21;
				}

				if (this.pathStuckTimer > 600.0F && this.activePathCount >= 2 && this.flowFieldFollower == null) {
					this.resetPathState();
				}

				if (this.pathStuckTimer > 80.0F && this.b > 30.0F) {
					this.resetPathState();
				}

				if (this.pathStuckTimer > 40.0F && this.activePathCount >= 2 && this.flowFieldFollower == null) {
					af var24 = this.pathNodes[1];
					float var22 = CommonUtils.distanceSquared(this.eo, this.ep, var24.a, var24.b);
					if (var22 < var10) {
						float var23 = this.pathStuckTimer;
						this.aJ();
						this.pathStuckTimer = var23;
					}
				}
			}

			if (var20) {
				this.aJ();
				if (var19) {
					this.d = 0.0F;
					this.c = 0.0F;
					if (!this.pathTruncated && this.queueLeader == null && aux != null && aux.type == WaypointType.move) {
						this.cancelCurrentWaypoint();
					}
				}
			}
		}

		if (this.ci && !this.isMoveIgnoringBody()) {
			var5 = -var5 * this.getReverseSpeedPercentage();
		}

		if (this.noTurnTimer > 0.0F) {
			var5 = 0.0F;
		}

		if (!this.isMoveSlidingMode()) {
			if (this.cf < var5) {
				this.cf = CommonUtils.approachValue(this.cf, var5, this.getMoveAccelerationSpeed() * float1);
			}

			if (this.cf > var5) {
				this.cf = CommonUtils.approachValue(this.cf, var5, this.getBuildSpeed() * float1);
			}
		} else {
			this.cf = var5;
		}

		this.cK = adx.b && boolean4;
	}

	@Deprecated
	public strictfp boolean Z() {
		return this.attackTarget != null;
	}

	public strictfp boolean aa() {
		if (this.attackTarget != null && !this.attackTarget.dead) {
			int var1 = this.getTurretCount();

			for (int var2 = 0; var2 < var1; var2++) {
				if (this.cL[var2].target != null && this.turretCanAttack(var2)) {
					return true;
				}
			}
		}

		return false;
	}

	public strictfp Unit ab() {
		if (this.attackTarget != null && !this.attackTarget.dead) {
			return this.attackTarget;
		} else {
			Waypoint var1 = this.getCurrentWaypoint();
			return var1 != null && var1.targetUnit != null && !var1.targetUnit.dead ? var1.targetUnit : null;
		}
	}

	private strictfp void searchAttackTarget(GameEngine l, float float2, float float3) {
		aQ.a(float3);
		lx.unitGeoIndex.a(this.eo, this.ep, float3, this, float2, aQ);
		if (aQ.a != 0 && (this.attackTarget == null || !this.isTargetInRange(this.attackTarget))) {
			aR.a(float3);
			lx.unitGeoIndex.a(this.eo, this.ep, float3, this, float2, aR);
		}
	}

	public strictfp boolean isTurretMultiTargeting() {
		return this.getTurretCount() > 1;
	}

	private strictfp void updateTurretTargets(GameEngine l, float float2) {
		int var3 = this.getTurretCount();
		if (this.isTurretMultiTargeting()) {
			boolean var9 = false;

			for (int var5 = 0; var5 < var3; var5++) {
				UnitTurretInstance var6 = this.cL[var5];
				if (this.v(var5) == -1) {
					boolean var7 = false;
					boolean var8 = false;
					if (this.canTurretAttack(var5, this.attackTarget, false, false)) {
						var6.target = this.attackTarget;
					} else {
						var9 = true;
						if (var6.target == this.attackTarget) {
							var6.target = null;
						}
					}
				}
			}

			if (var9) {
				float var10 = this.getTargetSearchRange(false);
				aT.a(this);
				lx.unitGeoIndex.a(this.eo, this.ep, var10, this, float2, aT);
			}

			for (int var11 = 0; var11 < var3; var11++) {
				int var12 = this.v(var11);
				if (var12 != -1) {
					this.cL[var11].target = this.cL[var12].target;
				}
			}
		} else {
			for (int var4 = 0; var4 < var3; var4++) {
				this.cL[var4].target = this.attackTarget;
			}
		}
	}

	public strictfp boolean ad() {
		if (!this.l()) {
			return false;
		} else {
			AttachmentSlot var1 = this.dn();
			return var1 == null || var1.M;
		}
	}

	private strictfp void updateTurretFiring(GameEngine l, float float2) {
		int var3 = this.getTurretCount();
		boolean var4 = false;
		if (this.ad()) {
			boolean var5 = false;
			boolean var6 = false;
			if (this.attackTarget != null) {
				AttachmentSlot var7 = this.dn();
				if (var7 != null && this.cO != null && var7.L && this.cO.attackTarget == this.attackTarget) {
					var5 = true;
				}

				if (!this.canAttackUnit(this.attackTarget, false) && !var5) {
					boolean var8 = true;
					if (var8) {
						this.attackTarget = null;
					}
				}
			}

			if (this.attackTarget != null && !var5) {
				var6 = !this.isTargetInRange(this.attackTarget);
			}

			this.targetSearchCooldown = CommonUtils.applyDeadzone(this.targetSearchCooldown, float2);
			this.turretAssignTimer = CommonUtils.applyDeadzone(this.turretAssignTimer, float2);
			if ((this.attackTarget == null || var6) && this.targetSearchCooldown == 0.0F && this.canPassivelyTarget()) {
				this.targetSearchCooldown = 20.0F + this.eo % 5.0F + this.ep % 5.0F;
				float var24 = this.getTargetSearchRange(false);
				this.searchAttackTarget(lx, float2, var24);
				if (this.attackTarget != null) {
					this.turretAssignTimer = 0.0F;
				}
			}

			if (this.attackTarget != null && this.turretAssignTimer == 0.0F) {
				this.turretAssignTimer = 20.0F + this.eo % 5.0F + this.ep % 5.0F;
				this.updateTurretTargets(lx, float2);
			}

			for (int var25 = 0; var25 < var3; var25++) {
				this.cL[var25].g = false;
			}

			if (this.attackTarget != null) {
				float var26 = CommonUtils.distanceSquared(this.eo, this.ep, this.attackTarget.eo, this.attackTarget.ep);
				float var27 = this.o(this.attackTarget);
				if (!(var26 < var27 * var27) && !var5) {
					if (!this.hasMoveTarget && this.isAggressiveAttackMode()) {
						this.directMoveRequested = true;
						this.hasMoveTarget = true;
						this.moveTargetX = this.attackTarget.eo;
						this.moveTargetY = this.attackTarget.ep;
						this.moveTargetClearance = 0;
					}
				} else {
					int var9 = this.getUpgradeTechLevel();

					for (int var10 = 0; var10 < var3; var10++) {
						UnitTurretInstance var11 = this.cL[var10];
						Unit var12 = var11.target;
						if (var12 != null) {
							boolean var13 = var12 == this.attackTarget;
							if (!var13 && !this.canAutoAttackUnit(var12, true)) {
								var11.target = null;
							} else {
								boolean var14 = false;
								boolean var15 = !var13;
								if (!this.canTurretAttack(var10, var12, false, var15)) {
									var11.target = null;
								} else {
									PointF var16 = this.G(var10);
									PointF var17 = this.K(var10);
									var17.a = var17.a + var12.eo;
									var17.b = var17.b + var12.ep;
									float var18 = CommonUtils.angleBetweenPoints(var16.a, var16.b, var17.a, var17.b);
									if (this.v(var10) == -1 && var10 != var9) {
										if (!this.isFixedFiring()) {
											var11.a(70);
											var11.b = var11.angle;
											float var19 = 179.0F;
											if (!var11.b()) {
												var19 = this.a(float2, var18, var10);
											}

											if (CommonUtils.abs(var19) < this.getTurretMaxAttackAngle(var10)) {
												var11.g = true;
											}
										} else {
											boolean var31 = false;
											Waypoint var20 = this.getCurrentWaypoint();
											if (var20 != null
												&& (
													var20.type == WaypointType.build
														|| var20.type == WaypointType.repair
														|| var20.type == WaypointType.reclaim
												)) {
												var31 = true;
											}

											if (!var31 && (!this.hasMoveTarget || this.isMoveIgnoringBody())) {
												float var21 = this.c(float2, var18);
												var11.b = var11.angle;
												if (CommonUtils.abs(var21) < this.getTurretMaxAttackAngle(var10)) {
													var11.g = true;
												}
											}
										}
									}
								}
							}
						}
					}

					for (int var28 = 0; var28 < var3; var28++) {
						UnitTurretInstance var29 = this.cL[var28];
						Unit var30 = var29.target;
						if (var30 != null) {
							if (this.u(var28) && var29.reloadTimer == 0.0F) {
								var4 = true;
							}

							if (this.u(var28)) {
								this.tryFireTurret(float2, var30, var28);
							}
						}
					}
				}
			}
		}

		if (this.aN && this.X() != null) {
			var4 = true;
		}

		for (int var22 = 0; var22 < var3; var22++) {
			UnitTurretInstance var23 = this.cL[var22];
			if (!var4 && var23.f != 0.0F) {
				var23.f = CommonUtils.applyDeadzone(var23.f, this.getTurretWarmupCooldownRate(var22) * float2);
			}
		}
	}

	public strictfp void playTurretWarmupEffect(Unit am, int integer) {
	}

	public strictfp boolean tryFireTurret(float float1, Unit am, int integer) {
		UnitTurretInstance var4 = this.cL[integer];
		int var5 = this.v(integer);
		if (var5 != -1) {
			var4.angle = this.cL[var5].angle;
		}

		boolean var6 = this.isTurretWarmupNoReset(integer);
		boolean var7 = false;
		if (var6) {
			if (var4.f < this.getTurretWarmup(integer)) {
				if (var4.f == 0.0F) {
					this.playTurretWarmupEffect(amx, integer);
				}

				var4.f += float1;
			} else {
				var4.f = this.getTurretWarmup(integer);
			}

			var7 = true;
		}

		if (var4.reloadTimer == 0.0F && this.turretCanAttack(integer)) {
			boolean var8 = false;
			boolean var9 = false;
			if (!this.canTurretAttack(integer, amx, false, false)) {
				var4.reloadTimer = -10.0F;
			} else {
				if (!var6) {
					if (var4.f < this.getTurretWarmup(integer)) {
						if (var4.f == 0.0F) {
							this.playTurretWarmupEffect(amx, integer);
						}

						var4.f += float1;
					} else {
						var7 = true;
					}
				}

				if (var7) {
					var4.reloadTimer = this.getTurretShootDelay(integer) + this.getTurretWarmupDelayTransfer(integer);
					if (!var6) {
						var4.f = 0.0F;
					}

					this.fireTurret(amx, integer);
					this.M(integer);
					var4.m = !var4.m;
					return true;
				}
			}
		}

		return false;
	}

	public strictfp boolean isTargetInRange(Unit am) {
		float var2 = CommonUtils.distanceSquared(this.eo, this.ep, amx.eo, amx.ep);
		float var3 = this.o(amx);
		return var2 < var3 * var3;
	}

	public strictfp boolean ae() {
		return false;
	}

	public strictfp boolean canAttackFlyingUnits() {
		return true;
	}

	public strictfp boolean canAttackLandUnits() {
		return true;
	}

	public strictfp boolean canAttackNotTouchingWaterUnits() {
		return true;
	}

	public strictfp boolean canFireAtNow(Unit am) {
		int var2 = this.getTurretCount();

		for (int var3 = 0; var3 < var2; var3++) {
			boolean var4 = false;
			boolean var5 = false;
			if (this.turretCanAttack(var3) && this.canTurretAttack(var3, amx, false, false)) {
				int var6 = this.v(var3);
				if (var6 == -1 || this.canTurretAttack(var6, amx, false, false)) {
					return true;
				}
			}
		}

		return false;
	}

	public strictfp boolean canFireAfterAim(Unit am) {
		int var2 = this.getTurretCount();

		for (int var3 = 0; var3 < var2; var3++) {
			boolean var4 = true;
			boolean var5 = false;
			if (this.turretCanAttack(var3) && this.canTurretAttack(var3, amx, true, false)) {
				int var6 = this.v(var3);
				if (var6 == -1 || this.canTurretAttack(var6, amx, true, false)) {
					return true;
				}
			}
		}

		return false;
	}

	public strictfp boolean canTurretAttack(int integer, Unit am, boolean boolean3, boolean boolean4) {
		return boolean3 || !boolean4 || this.isTargetInRange(amx);
	}

	public strictfp boolean canTargetUnit(Unit am) {
		if (amx.isAirUnit()) {
			return this.canAttackFlyingUnits();
		} else if (amx.isUnderwater()) {
			return this.ae();
		} else {
			return !this.canAttackNotTouchingWaterUnits() && !amx.cH() ? false : this.canAttackLandUnits();
		}
	}

	public strictfp boolean canRepairUnit(Unit am) {
		return false;
	}

	public strictfp boolean canReclaimUnit(Unit am) {
		return amx.getResourceRate() != 0.0F && this.h(amx, true) ? true : this.canRepairUnit(amx);
	}

	public strictfp SpecialAction a(UnitTypeInterface as, boolean boolean2) {
		return this.a(asx, -1, boolean2);
	}

	public strictfp boolean ai() {
		for (SpecialAction var2 : this.N()) {
			if (var2.g()) {
				return true;
			}
		}

		return false;
	}

	public strictfp SpecialAction a(UnitTypeInterface as, int integer, boolean boolean3) {
		ArrayList var4 = this.N();
		SpecialAction var5 = null;
		if (var4.size() > 0) {
			for (SpecialAction var7 : var4) {
				UnitTypeInterface var8 = var7.y();
				if (boolean3) {
					UnitTypeInterface var9 = var7.E();
					if (var9 != null) {
						var8 = var9;
					}
				}

				if (var8 == asx && (integer == -1 || integer == var7.t())) {
					var5 = var7;
					if (var7.b(this) && var7.a(this, false)) {
						return var7;
					}
				}
			}
		}

		return var5;
	}

	public strictfp boolean b(UnitTypeInterface as, boolean boolean2) {
		SpecialAction var3 = this.a(asx, boolean2);
		if (var3 != null) {
			return var3.g(this) ? false : var3.b(this);
		} else {
			return false;
		}
	}

	@Override
	public strictfp boolean aj() {
		return this.r().canAttackFlyingUnits();
	}

	@Override
	public strictfp boolean ak() {
		return this.r().canAttackUnderwaterUnits();
	}

	public strictfp void m(Unit am) {
	}

	public strictfp boolean dieOnConstruct() {
		return false;
	}

	public final strictfp boolean canAttackUnit(Unit am, boolean boolean2) {
		if (this.bX == amx.team || amx.dead || !this.bX.isEnemy(amx.team)) {
			return false;
		} else if (this.attackMode == AttackMode.holdFire) {
			return false;
		} else if (this.attackMode == AttackMode.returnFire) {
			return false;
		} else if (amx.transportedBy != null) {
			return false;
		} else if (!this.canTargetUnit(amx)) {
			return false;
		} else if (!amx.canBeAttackedBy((Unit)this)) {
			return false;
		} else if (!boolean2) {
			float var3 = CommonUtils.distanceSquared(this.eo, this.ep, amx.eo, amx.ep);
			float var5 = this.getTargetSearchRange(false);
			float var4 = var5 * var5;
			return var3 < var4;
		} else {
			return true;
		}
	}

	public final strictfp boolean canAutoAttackUnit(Unit am, boolean boolean2) {
		return amx.notPassivelyTargetedByOtherUnits() ? false : this.canAttackUnit(amx, boolean2);
	}

	public strictfp float am() {
		return 0.0F;
	}

	public strictfp boolean isAggressiveAttackMode() {
		return this.attackMode == AttackMode.outOfRange || this.attackMode == AttackMode.guardArea || this.attackMode == AttackMode.aggressive;
	}

	public strictfp float getTargetSearchRange(boolean boolean1) {
		float var2 = this.m();
		Waypoint var3 = this.getCurrentWaypoint();
		if (var3 != null
			&& (var3.type == WaypointType.attackMove || var3.type == WaypointType.patrol || var3.type == WaypointType.guard)
			)
		 {
			if (var3.type == WaypointType.patrol) {
				var2 += 110.0F;
			} else if (var3.type == WaypointType.guard) {
				var2 += 90.0F;
			} else {
				var2 += 20.0F;
			}

			if (var2 < 190.0F) {
				var2 = 190.0F;
			}
		}

		if (this.attackMode == AttackMode.outOfRange) {
			var2 += 250.0F;
		} else if (this.attackMode == AttackMode.guardArea) {
			var2 += 150.0F;
		} else if (this.attackMode == AttackMode.aggressive) {
			var2 += 180.0F;
		} else {
			var2 += this.am();
			if (boolean1) {
				var2 += 110.0F;
			}
		}

		return var2;
	}

	public strictfp Waypoint queueWaypointFront() {
		this.ensureWaypointCapacity(29);
		if (this.waypointsCount > 0) {
			this.b(this.waypoints[0]);
		}

		Waypoint var1 = this.waypoints[29];

		for (int var2 = 29; var2 >= 1; var2--) {
			this.waypoints[var2] = this.waypoints[var2 - 1];
		}

		this.waypoints[0] = var1;
		if (this.waypointsCount < 29) {
			this.waypointsCount++;
		}

		if (this.waypoints[0] == null) {
			this.waypoints[0] = new Waypoint();
		}

		Waypoint var3 = this.waypoints[0];
		var3.e();
		this.waypointTimer = 0.0F;
		this.approachTimeoutTimer = 0.0F;
		this.pathStuckTimer = 0.0F;
		this.c(var3);
		this.resetPathState();
		return var3;
	}

	public strictfp void a(Waypoint au) {
	}

	public final strictfp void b(Waypoint au) {
		this.workBeamActive = false;
	}

	public strictfp void c(Waypoint au) {
		this.bC();
		this.lastReclaimTickMillis = -9999;
		if (this.attackTarget != null && this.attackTarget.notPassivelyTargetedByOtherUnits()) {
			this.attackTarget = null;
		}
	}

	public strictfp Waypoint allocateWaypoint() {
		this.ensureWaypointCapacity(this.waypointsCount);
		if (this.waypoints[this.waypointsCount] == null) {
			this.waypoints[this.waypointsCount] = new Waypoint();
		}

		Waypoint var1 = this.waypoints[this.waypointsCount];
		var1.e();
		if (this.waypointsCount < 29) {
			this.waypointsCount++;
		}

		if (this.waypointsCount > 0) {
			this.c(this.waypoints[0]);
		}

		return var1;
	}

	public strictfp Waypoint queueMoveWaypoint(float float1, float float2) {
		Waypoint var3 = this.allocateWaypoint();
		var3.a(float1, float2);
		return var3;
	}

	public strictfp Waypoint queueAttackWaypoint(Unit am) {
		Waypoint var2 = this.allocateWaypoint();
		var2.a(amx);
		return var2;
	}

	public strictfp Waypoint e(float float1, float float2) {
		Waypoint var3 = this.allocateWaypoint();
		var3.b(float1, float2);
		return var3;
	}

	public strictfp boolean isValidNewWaypoint(Waypoint au, boolean boolean2) {
		if (aux == null) {
			if (boolean2) {
				GameEngine.logWarning("isValidNewWaypoint: Skipping null waypoint");
			}

			return false;
		} else {
			if (aux.d() == WaypointType.build) {
				if (aux.unitType == null) {
					if (boolean2) {
						GameEngine.logWarning("isValidNewWaypoint: Skipping build waypoint with no buildType");
					}

					return false;
				}

				SpecialAction var3 = this.a(aux.unitType, aux.count, false);
				if (var3 == null) {
					if (boolean2) {
						GameEngine.logWarning("Unit '" + this.r().i() + "' can not queue build:" + aux.unitType.i());
					}

					return false;
				}

				if (!aux.n) {
					if (var3.g(this)) {
						if (boolean2) {
							GameEngine.logWarning("Builder '" + this.r().i() + "' tried to queue a locked building:" + var3.O());
						}

						return false;
					}

					if (!var3.b(this)) {
						if (boolean2) {
							GameEngine.logWarning("Builder '" + this.r().i() + "' tried to queue a unavailable building:" + var3.O());
						}

						return false;
					}
				}
			}

			return true;
		}
	}

	public strictfp Waypoint d(Waypoint au) {
		Waypoint var2 = this.allocateWaypoint();
		var2.c(aux);
		return var2;
	}

	public strictfp boolean isIdle() {
		return this.getCurrentWaypoint() == null;
	}

	public strictfp Waypoint getCurrentWaypoint() {
		return this.waypointsCount == 0 ? null : this.waypoints[0];
	}

	public strictfp Waypoint getNextWaypoint() {
		return this.waypointsCount <= 1 ? null : this.waypoints[1];
	}

	public strictfp Waypoint getLastWaypoint() {
		return this.waypointsCount == 0 ? null : this.waypoints[this.waypointsCount - 1];
	}

	public strictfp void cancelLastWaypoint() {
		if (this.waypointsCount != 0) {
			if (this.waypointsCount == 1) {
				this.popWaypoint();
			} else {
				this.waypointsCount--;
			}
		}
	}

	public strictfp Waypoint getWaypoint(int integer) {
		return this.waypoints[integer];
	}

	public strictfp int getWaypointCount() {
		return this.waypointsCount;
	}

	public strictfp boolean isAttackWaypointActive() {
		Waypoint var1 = this.getCurrentWaypoint();
		return var1 != null && var1.type == WaypointType.attack;
	}

	public strictfp boolean a(UnitTypeInterface as, float float2, float float3) {
		for (int var4 = 0; var4 < this.waypointsCount; var4++) {
			Waypoint var5 = this.waypoints[var4];
			if (var5.type == WaypointType.build
				&& var5.unitType == asx
				&& CommonUtils.abs(var5.x - float2) < 10.0F
				&& CommonUtils.abs(var5.y - float3) < 10.0F) {
				return true;
			}
		}

		return false;
	}

	public strictfp void ensurePathNodeCapacity(int integer) {
		if (integer >= 120) {
			throw new RuntimeException("PathNode index:" + integer + " too large");
		} else {
			if (this.pathNodes == at) {
				this.pathNodes = new af[120];
			}
		}
	}

	public strictfp void ensureWaypointCapacity(int integer) {
		if (integer >= 30) {
			throw new RuntimeException("Waypoint index:" + integer + " too large");
		} else {
			if (this.waypoints == O) {
				this.waypoints = new Waypoint[30];
			}
		}
	}

	public strictfp void completeWaypoint(int integer) {
		if (this.waypointsCount <= integer) {
			throw new IndexOutOfBoundsException("completeWaypoint: waypointsCount:" + this.waypointsCount + ", waypointIndex:" + integer);
		} else if (integer == 0) {
			this.popWaypoint();
		} else {
			if (this.waypoints.length > 0) {
				Waypoint var2 = this.waypoints[integer];

				for (int var3 = integer; var3 < this.waypointsCount - 1; var3++) {
					this.waypoints[var3] = this.waypoints[var3 + 1];
				}

				this.waypoints[this.waypointsCount - 1] = var2;
			}

			this.waypointsCount--;
		}
	}

	public strictfp void cancelCurrentWaypoint() {
		this.releaseFollowers();
		this.popWaypoint();
	}

	public strictfp void popWaypoint() {
		this.waypointTimer = 0.0F;
		this.approachTimeoutTimer = 0.0F;
		this.pathStuckTimer = 0.0F;
		this.fleeing = false;
		this.fleeHeading = -999.0F;
		this.bomberSearchFailed = false;
		this.guardFollowState = 0;
		if (this.waypointsCount == 0) {
			this.resetPathState();
			this.e = 0.0F;
			this.d = 0.0F;
			this.c = 0.0F;
		} else if (this.waypointsCount == 1) {
			this.b(this.waypoints[0]);
			this.waypointsCount = 0;
			this.resetPathState();
			this.e = 0.0F;
			this.d = 0.0F;
			this.c = 0.0F;
			this.c(null);
		} else {
			if (this.waypoints.length > 0) {
				Waypoint var1 = this.waypoints[0];
				this.b(var1);

				for (int var2 = 0; var2 < this.waypointsCount - 1; var2++) {
					this.waypoints[var2] = this.waypoints[var2 + 1];
				}

				this.waypoints[this.waypointsCount - 1] = var1;
			}

			this.waypointsCount--;
			if (this.waypointsCount > 0) {
				this.c(this.waypoints[0]);
			} else {
				this.c(null);
			}

			this.resetPathState();
		}
	}

	public strictfp void clearWaypoints() {
		int var1 = this.waypointsCount;
		if (this.waypointsCount > 0) {
			this.b(this.waypoints[0]);
		}

		this.waypointTimer = 0.0F;
		this.approachTimeoutTimer = 0.0F;
		this.fleeing = false;
		this.fleeHeading = -999.0F;
		this.bomberSearchFailed = false;
		this.waypointsCount = 0;
		this.resetPathState();
		this.disbandFollowers();
		this.setQueueLeader(null);
		this.e = 0.0F;
		this.d = 0.0F;
		this.c = 0.0F;
		this.guardFollowState = 0;
		if (var1 > 0) {
			this.c(null);
		}
	}

	public strictfp void completeNonBuildWaypoints() {
		for (int var1 = 0; var1 < this.waypointsCount; var1++) {
			Waypoint var2 = this.waypoints[var1];
			if (var2 != null && var2.type != WaypointType.build && var2.type != WaypointType.repair) {
				this.completeWaypoint(var1);
			}
		}
	}

	public strictfp void setQueueLeader(OrderableUnit y) {
		if (this.queueLeader != null) {
			this.queueLeader.followerCount--;
		}

		this.queueLeader = yx;
		if (yx != null) {
			this.queueLeader.followerCount++;
		}
	}

	public strictfp void clearFormationState() {
		this.setQueueLeader(null);
		this.inFormation = false;
		this.aj = false;
		this.formationOffsetX = 0.0F;
		this.formationOffsetY = 0.0F;
		this.ac = 0;
		this.c = 0.0F;
	}

	public strictfp void releaseFollowers() {
		if (this.followerCount != 0) {
			Waypoint var1 = this.getNextWaypoint();
			Unit[] var2 = Unit.unitList.items();
			int var3 = 0;

			for (int var4 = Unit.unitList.size(); var3 < var4; var3++) {
				Unit var5 = var2[var3];
				if (var5 instanceof OrderableUnit) {
					OrderableUnit var6 = (OrderableUnit)var5;
					if (var6.queueLeader == this) {
						float var7 = CommonUtils.distanceSquared(this.eo, this.ep, var6.eo, var6.ep);
						boolean var8 = var7 < 108900.0F;
						boolean var9 = false;
						boolean var10 = false;
						Waypoint var11 = var6.getNextWaypoint();
						if (var1 != null && var11 != null) {
							if (var1.b(var11)) {
								var9 = true;
							}
						} else if (var1 == null && var11 == null) {
							var10 = true;
						}

						if (var9 && var8) {
							var6.popWaypoint();
						} else if (!var10) {
							var6.setQueueLeader(null);
						}
					}
				}
			}
		}
	}

	public strictfp void disbandFollowers() {
		OrderableUnit var1 = null;
		if (this.followerCount != 0) {
			Unit[] var2 = Unit.unitList.items();
			int var3 = 0;

			for (int var4 = Unit.unitList.size(); var3 < var4; var3++) {
				Unit var5 = var2[var3];
				if (var5 instanceof OrderableUnit) {
					OrderableUnit var6 = (OrderableUnit)var5;
					if (var6.queueLeader == this) {
						var6.setQueueLeader(null);
						var1 = var6;
					}
				}
			}

			if (this.followerCount != 0) {
				this.followerCount = 0;
			}

			if (var1 != null) {
				Waypoint var7 = var1.getCurrentWaypoint();
				if (var7 != null) {
					GroupControllerUnitGroup var8 = var7.group;
					if (var8 != null) {
						var8.c();
					}
				}
			}
		}
	}

	public strictfp af getFirstPathNode() {
		if (this.activePathCount == 0) {
			return null;
		} else {
			return this.flowFieldFollower != null ? this.flowFieldFollower.getFirstPathNode(this) : this.pathNodes[0];
		}
	}

	public strictfp af getSecondPathNode() {
		if (this.activePathCount < 2) {
			return null;
		} else {
			return this.flowFieldFollower != null ? this.flowFieldFollower.getSecondPathNode(this) : this.pathNodes[1];
		}
	}

	public strictfp void setPathNode(int integer, float float2, float float3) {
		this.ensurePathNodeCapacity(integer);
		if (this.pathNodes[integer] == null) {
			this.pathNodes[integer] = new af();
		}

		this.pathNodes[integer].a = float2;
		this.pathNodes[integer].b = float3;
	}

	public strictfp boolean isPathStuck() {
		if (this.flowFieldFollower != null) {
			return false;
		} else {
			if (this.activePathCount >= 2) {
				if (this.z() > 0.5) {
					if (this.pathStuckTimer > 150.0F || this.X > 150.0F) {
						return true;
					}
				} else if (this.pathStuckTimer > 300.0F || this.X > 300.0F) {
					return true;
				}
			}

			return false;
		}
	}

	public strictfp void resetPathState() {
		this.activePathCount = 0;
		this.pathTruncated = false;
		this.pathNodeTotal = 0;
		this.pathFollowSpeed = 0.0F;
		this.pathStuckTimer = 0.0F;
		this.X = 0.0F;
		this.pathRetryCount = 0;
	}

	public strictfp void aI() {
		this.resetPathState();
		this.pathNodes = at;
		this.nearbyCollisionCount = 0;
		this.nearbyCollisionUnits = null;
		this.nearbyCollisionDists = null;
	}

	public strictfp void aJ() {
		this.X = this.pathStuckTimer;
		this.pathStuckTimer = 0.0F;
		if (this.flowFieldFollower != null) {
			this.flowFieldFollower.advancePathNode(this);
		} else if (this.activePathCount != 0) {
			if (this.activePathCount == 1) {
				this.activePathCount = 0;
			} else {
				af var1 = this.pathNodes[0];

				for (int var2 = 0; var2 < this.activePathCount - 1; var2++) {
					this.pathNodes[var2] = this.pathNodes[var2 + 1];
				}

				this.pathNodes[this.activePathCount - 1] = var1;
				this.activePathCount--;
			}
		}
	}

	public strictfp boolean aK() {
		GameEngine var1 = GameEngine.getInstance();
		boolean var2 = false;
		boolean var3 = false;
		if (this.ct()) {
			var2 = true;
		}

		var1.map.a(this.eo, this.ep);
		int var4 = var1.map.T;
		int var5 = var1.map.U;
		if (var1.pathEngine.isBlocked(this.h(), var4, var5) && !var1.pathEngine.isBlockedIgnoreUnits(this.h(), var4, var5)) {
			var2 = true;
			var3 = true;
		}

		return var2;
	}

	public strictfp void a(float float1, float float2, int integer, boolean boolean4, boolean boolean5) {
		GameEngine var6 = GameEngine.getInstance();
		PathEngine var7 = var6.pathEngine;
		Map var8 = var6.map;
		this.cK = true;
		boolean var9 = false;
		boolean var10 = false;
		if (this.ct()) {
			var9 = true;
		}

		var8.a(this.eo, this.ep);
		int var11 = var8.T;
		int var12 = var8.U;
		if (var7.isBlocked(this.h(), var11, var12) && !var7.isBlockedIgnoreUnits(this.h(), var11, var12)) {
			var9 = true;
			var10 = true;
		}

		if (float1 != this.pathGoalX || this.pathGoalY != float2) {
			this.pathRetryCount = 0;
		}

		this.pathGoalX = float1;
		this.pathGoalY = float2;
		if (var9) {
			this.pathTruncated = false;
			this.activePathCount = 0;
			this.flowFieldFollower = null;
			float var30 = var8.a(float1);
			float var31 = var8.b(float2);
			if (var10) {
				float var32 = CommonUtils.angleBetweenPoints(this.eo, this.ep, var30, var31);
				float var33 = CommonUtils.distance(this.eo, this.ep, var30, var31);
				if (var33 > 60.0F) {
					var33 = 60.0F;
					this.pathTruncated = true;
					if (this.pathFollowSpeed > 10.0F) {
						this.pathFollowSpeed = 10.0F;
					}
				}

				var30 = this.eo + CommonUtils.cos(var32) * var33;
				var31 = this.ep + CommonUtils.sin(var32) * var33;
			}

			this.setPathNode(this.activePathCount, var30, var31);
			this.activePathCount++;
			this.pathNodeTotal = this.activePathCount;
		} else {
			byte var13 = 1;
			byte var14 = 80;
			byte var15 = 0;
			if (boolean4) {
				var15 = 3;
			}

			boolean var16 = UnitFunctions.a(this.h(), this.eo, this.ep, float1, float2, var14, var15, var13);
			if (var16) {
				this.pathTruncated = false;
				this.activePathCount = 0;
				this.flowFieldFollower = null;
				float var34 = var8.a(float1);
				float var36 = var8.b(float2);
				float var37 = this.eo;
				float var39 = this.ep;
				float var40 = CommonUtils.angleBetweenPoints(this.eo, this.ep, var34, var36);
				float var41 = CommonUtils.distance(this.eo, this.ep, var34, var36);
				float var42 = CommonUtils.cos(var40);
				float var43 = CommonUtils.sin(var40);
				float var25 = 20.0F;
				float var26 = 0.05F;
				int var27 = (int)(var41 * 0.05F - 1.0F);
				int var28 = 1;
				if (var27 < 4) {
					var28 = 0;
				}

				for (int var29 = 0; var29 < var27; var29++) {
					var37 += var42 * 20.0F;
					var39 += var43 * 20.0F;
					if (var28 > 0) {
						var28--;
					} else {
						this.setPathNode(this.activePathCount, var37, var39);
						this.activePathCount++;
						if (this.activePathCount >= 119) {
							this.pathTruncated = true;
							break;
						}
					}
				}

				if (!this.pathTruncated) {
					if (this.activePathCount < 119) {
						this.setPathNode(this.activePathCount, var34, var36);
						this.activePathCount++;
					} else {
						this.pathTruncated = true;
					}
				}

				this.pathNodeTotal = this.activePathCount;
			} else {
				GroupControllerUnitGroup var17 = null;
				boolean var18 = false;
				Waypoint var19 = this.getCurrentWaypoint();
				if (var19 != null) {
					var17 = var19.group;
					if (var17 == null) {
					}
				}

				if (var17 != null && var17.cachedPaths != null) {
					CommandControllerCachedCommandPath var20 = null;
					float var21 = 3600.0F;

					for (CommandControllerCachedCommandPath var23 : var17.cachedPaths) {
						var18 = true;
						if (var23.path != null
							&& var23.path.getNodes() != null
							&& !(CommonUtils.abs(var23.targetX - float1) > 10.0F)
							&& !(CommonUtils.abs(var23.targetY - float2) > 10.0F)
							&& var23.g + 180 >= var6.gameTimeSeconds
							&& var23.movementType == this.h()) {
							float var24 = CommonUtils.distanceSquared(this.eo, this.ep, var23.startX, var23.startY);
							if (var24 < var21) {
								var20 = var23;
							}
						}
					}

					if (var20 != null) {
						this.pendingPath = var20.path;
						return;
					}
				}

				if (L && integer > 2) {
					integer = 2;
				}

				boolean var38 = true;
				this.pendingPath = this.a(float1, float2, integer, boolean4, var38, boolean5);
			}
		}
	}

	public strictfp Path a(float float1, float float2, int integer, boolean boolean4, boolean boolean5, boolean boolean6) {
		GameEngine var7 = GameEngine.getInstance();
		PathEngine var8 = var7.pathEngine;
		Map var9 = var7.map;
		Path var10 = var8.createPath(boolean5);
		var9.a(this.eo, this.ep);
		boolean var11 = false;
		if (this.bb() || this.ci) {
			var11 = true;
		}

		var10.setPath(this.h(), (short)var9.T, (short)var9.U, this.cg, var11);
		var9.a(float1, float2);
		var10.setEnd((short)var9.T, (short)var9.U, (short)integer);
		var10.avoidTightSpaces = boolean4;
		var10.requiredClearance = this.bh();
		var10.lowPriority = boolean6;
		boolean var12 = this.cK;
		this.cK = true;
		if (boolean5 && var10.isFlowField()) {
			Iterator var13 = aV.iterator();

			while (var13.hasNext()) {
				Path var14 = (Path)var13.next();
				if (var14.createdFrame + 60 < var7.gameTimeSeconds) {
					var13.remove();
				} else if (var14.equalsPath(var10)) {
					return var14;
				}
			}
		}

		var8.requestPath(var10, boolean5);
		this.cK = var12;
		if (boolean5 && var10.isFlowField()) {
			aV.add(var10);
		}

		return var10;
	}

	strictfp void b(GameEngine l) {
		if (this.pendingPath != null) {
			Map var2 = lx.map;
			LinkedList var3 = this.pendingPath.getNodes();
			if (var3 != null) {
				this.flowFieldFollower = this.pendingPath.getFollower(this);
				Path var4 = this.pendingPath;
				this.activePathCount = 0;
				this.pathTruncated = false;

				for (PathSolverNode var6 : var3) {
					var2.a(var6.x, var6.y);
					float var7 = var2.T + var2.p;
					float var8 = var2.U + var2.q;
					this.setPathNode(this.activePathCount, var7, var8);
					this.activePathCount++;
					if (this.activePathCount >= 120) {
						this.pathTruncated = true;
						break;
					}
				}

				if (this.activePathCount == 1) {
					this.pathRetryCount++;
				}

				boolean var9 = true;
				boolean var10 = false;
				if (var3.size() != 0) {
					var2.a(this.pathGoalX, this.pathGoalY);
					if (!this.pathTruncated
						&& ((PathSolverNode)var3.getLast()).x == var2.T
						&& ((PathSolverNode)var3.getLast()).y == var2.U) {
						var10 = true;
					}
				}

				if (var10) {
					if (!var9) {
						if (this.activePathCount < 120) {
							this.setPathNode(this.activePathCount, this.pathGoalX, this.pathGoalY);
							this.activePathCount++;
						}
					} else {
						if (this.activePathCount == 0) {
							this.activePathCount++;
						}

						this.setPathNode(this.activePathCount - 1, this.pathGoalX, this.pathGoalY);
					}
				}

				this.pendingPath = null;
				if (this.activePathCount > 120) {
					GameEngine.logWarning("activePathCount>maxPathNodes: activePathCount:" + this.activePathCount);
					this.activePathCount = 120;
				}

				this.pathNodeTotal = this.activePathCount;
			}
		}
	}

	public strictfp long aL() {
		long var1 = 0L;

		for (int var3 = 0; var3 < this.activePathCount; var3++) {
			af var4 = this.pathNodes[var3];
			if (var4 != null) {
				var1 += Float.floatToRawIntBits(var4.a);
				var1 += Float.floatToRawIntBits(var4.b);
			}
		}

		return var1;
	}

	strictfp af o(int integer) {
		if (this.flowFieldFollower != null) {
			return integer == 0 ? this.getFirstPathNode() : this.getSecondPathNode();
		} else {
			return integer >= this.activePathCount ? null : this.pathNodes[integer];
		}
	}

	@Override
	public strictfp void d(float float1) {
		super.d(float1);
	}

	public strictfp float getWhenBeingBuiltMakeTransparentTill() {
		return 1.0F;
	}

	public strictfp int l(float float1) {
		if (float1 < -0.3F) {
			int var2 = (int)((1.0F - -float1 / 10.0F) * 130.0F + 45.0F);
			if (var2 < 45) {
				var2 = 45;
			}

			return var2;
		} else {
			return 255;
		}
	}

	public strictfp Paint aN() {
		PorterDuffColorFilter var1 = null;
		int var2 = -1;
		if (this.eq < -0.3F) {
			int var3 = this.l(this.eq);
			var2 = Color.a(var3, 255, 255, 255);
		} else {
			var2 = -1;
		}

		if (this.cm < 1.0F && this.cm < this.getWhenBeingBuiltMakeTransparentTill()) {
			float var5 = this.cm / this.getWhenBeingBuiltMakeTransparentTill() * 220.0F;
			var2 = Color.a((int)(20.0F + var5), 140, 255, 140);
			var1 = aX;
		}

		if (this.cp) {
			if (this.cs) {
				var2 = Color.a(200, 20, 255, 20);
				var1 = aY;
			}

			if (this.ct) {
				var2 = Color.a(200, 255, 20, 20);
				var1 = aZ;
			}

			if (this.cq) {
				var2 = Color.a(50, 70, 70, 245);
				var1 = ba;
				if (this.ct) {
					var2 = Color.a(50, 255, 20, 20);
					var1 = aZ;
				}
			}

			if (this.cr) {
				var2 = Color.a(150, 100, 100, 100);
			}
		}

		boolean var6 = this.aO();
		return this.a(var2, var1, var6);
	}

	public strictfp boolean aO() {
		GameEngine var1 = GameEngine.getInstance();
		boolean var2 = var1.settings.renderAntiAlias;
		if (!this.dk()) {
			var2 = false;
			float var3 = var1.zoomScale;
			if (var3 < 1.0F) {
				var2 = true;
			}
		}

		if (this.co) {
			var2 = UnitType.ag;
		}

		return var2;
	}

	public strictfp float p(int integer) {
		return 1.0F;
	}

	@Override
	public strictfp boolean c(float float1) {
		GameEngine var2 = GameEngine.getInstance();
		GraphicsEngine var3 = var2.graphics;
		Paint var4 = this.aN();
		float var5 = this.cD();
		if (this.ew) {
			PointF var6 = this.cY();
			float var7 = this.eo + var6.a - var2.cameraRenderX;
			float var8 = this.ep + var6.b - var2.cameraRenderY - this.eq;
			this.aQ();
			if (var5 != 1.0F) {
				var3.restore();
				var3.scaleAroundPoint(var5, var5, var7, var8);
			}

			var3.drawImageRotated(this.bodyImage, var7, var8, this.d(false) - 90.0F, var4);
			if (var5 != 1.0F) {
				var3.save();
			}
		} else {
			PointF var13 = this.cY();
			RectF var14 = this.cF();
			float var15 = var13.a;
			float var9 = var13.b - this.eq;
			var14.a += var15;
			var14.b += var9;
			var14.c += var15;
			var14.d += var9;
			Rect var10 = this.a_(false);
			float var11 = (var14.a + var14.c) * 0.5F;
			float var12 = (var14.b + var14.d) * 0.5F;
			var3.restore();
			this.aQ();
			if (var5 != 1.0F) {
				var3.scaleAroundPoint(var5, var5, var11, var12);
			}

			var3.a(this.d(false), var11, var12);
			var3.drawImage(this.bodyImage, var10, var14, var4);
			var3.save();
		}

		return true;
	}

	public strictfp boolean shouldRenderShadow() {
		return this.eq > 0.0F && this.cm >= 1.0F && !this.cq;
	}

	public strictfp PointF getShadowOffset() {
		be.a(this.G(), this.H());
		return be;
	}

	public strictfp float G() {
		return 0.0F;
	}

	public strictfp float H() {
		return 0.0F;
	}

	public strictfp boolean aQ() {
		if (this.turretImage != null && this.shouldRenderShadow()) {
			GameEngine var1 = GameEngine.getInstance();
			if (!var1.renderSmallUnitTurrets && this.cj < 18.0F && this.eq < 0.5) {
				return true;
			} else if (!var1.renderMediumUnitTurrets && this.cj < 28.0F && this.eq < 5.0F) {
				return true;
			} else {
				PointF var2 = this.getShadowOffset();
				float var3 = this.eo + var2.a - var1.cameraRenderX;
				float var4 = this.ep + var2.b - var1.cameraRenderY;
				float var5 = this.cD();
				GraphicsEngine var6 = var1.graphics;
				if (var5 != 1.0F) {
					var6.restore();
					var6.scaleAroundPoint(var5, var5, var3, var4);
				}

				if (this.cG()) {
					Rect var7 = this.a_(true);
					RectF var8 = dB;
					var8.a(var3 - this.eu, var4 - this.ev, var3 + this.eu, var4 + this.ev);
					var6.restore();
					var6.a(this.d(true), var3, var4);
					var6.drawImage(this.turretImage, var7, var8, this.R());
					var6.save();
				} else {
					var6.drawImageRotated(this.turretImage, var3, var4, this.d(true) - 90.0F, this.R());
				}

				if (var5 != 1.0F) {
					var6.save();
				}

				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public strictfp boolean s_() {
		GameEngine var1 = GameEngine.getInstance();
		return RectF.a(var1.viewRectF, this.cE());
	}

	public abstract boolean canReceiveOrders();

	public strictfp boolean aR() {
		AttachmentSlot var1 = this.dn();
		return var1 != null && !var1.O ? false : this.canReceiveOrders();
	}

	public strictfp boolean aS() {
		return this.aR();
	}

	public strictfp boolean b_() {
		return true;
	}

	public strictfp int getUpgradeTechLevel() {
		return -1;
	}

	public strictfp float o(Unit am) {
		return this.isMelee() && amx != null ? this.m() + this.cj + amx.radius : this.m();
	}

	public strictfp float p(Unit am) {
		return this.isMelee() && amx != null ? this.aU() + this.cj + amx.radius : this.aU();
	}

	public strictfp float aU() {
		return this.m();
	}

	public strictfp int q(Unit am) {
		GameEngine var2 = GameEngine.getInstance();
		int var3 = 0;
		float var4 = this.p(amx);
		if (var4 > 58.0F) {
			var3 = (int)((var4 - 41.0F) / (var2.map.n * 1.414F));
		}

		return var3;
	}

	public abstract float m();

	public strictfp boolean isMelee() {
		return false;
	}

	public abstract float getTurretShootDelay(int integer);

	public strictfp float q(int integer) {
		return 0.0F;
	}

	public strictfp void aW() {
		int var1 = this.getTurretCount();

		for (int var2 = 0; var2 < var1; var2++) {
			if (var2 < this.cL.length) {
				UnitTurretInstance var3 = this.cL[var2];
				if (var3.reloadTimer > this.getTurretShootDelay(var2)) {
					var3.reloadTimer = this.getTurretShootDelay(var2);
				}
			}
		}
	}

	public strictfp ArrayList aX() {
		ArrayList var1 = new ArrayList();
		if (this.l()) {
			int var2 = this.getTurretCount();

			for (int var3 = 0; var3 < var2; var3++) {
				float var4 = this.q(var3);
				if (var4 != 0.0F) {
					float var5 = this.getTurretShootDelay(var3);
					if (var5 == 9000.0F) {
						var5 = 0.0F;
					}

					boolean var6 = false;

					for (aa var8 : var1) {
						if (var8.a == var4 && (var8.b == var5 || var5 == 0.0F || var8.b == 0.0F)) {
							var8.d++;
							if (var8.b == 0.0F) {
								var8.b = var5;
							}

							var6 = true;
							break;
						}
					}

					if (!var6) {
						aa var9 = new aa();
						var9.a = var4;
						var9.b = var5;
						var9.c = this.getTurretWarmup(var3);
						var1.add(var9);
					}
				}
			}
		}

		return var1;
	}

	public strictfp boolean turretCanAttack(int integer) {
		return true;
	}

	public strictfp float getTurretWarmup(int integer) {
		return 0.0F;
	}

	public strictfp boolean isTurretWarmupNoReset(int integer) {
		return false;
	}

	public strictfp float getTurretWarmupDelayTransfer(int integer) {
		return 0.0F;
	}

	public strictfp float getTurretWarmupCooldownRate(int integer) {
		return 4.0F;
	}

	public strictfp boolean u(int integer) {
		int var2 = this.v(integer);
		return var2 == -1 ? this.cL[integer].g : this.cL[var2].g;
	}

	public strictfp int v(int integer) {
		return -1;
	}

	public abstract float A();

	public strictfp float getTurnAcceleration() {
		return -1.0F;
	}

	public abstract float getTurretTurnSpeed(int integer);

	public strictfp float getTurretTurnSpeedAcceleration(int integer) {
		return -1.0F;
	}

	public strictfp float getTurretMaxAttackAngle(int integer) {
		return 5.0F;
	}

	public strictfp float getTurretTurnSpeedDeceleration(int integer) {
		return this.getTurretTurnSpeedAcceleration(integer);
	}

	public strictfp boolean isFixedFiring() {
		return false;
	}

	public strictfp boolean aY() {
		return false;
	}

	public abstract float z();

	public strictfp float getMoveYAxisScaling() {
		return 1.0F;
	}

	public strictfp float ba() {
		return 1.0F;
	}

	public strictfp boolean bb() {
		return this.getReverseSpeedPercentage() > 0.95F;
	}

	public strictfp float getReverseSpeedPercentage() {
		return 0.6F;
	}

	@Override
	public strictfp float getMaxEnergy() {
		return 0.0F;
	}

	public strictfp AttackMovement getAttackMovement() {
		return AttackMovement.normal;
	}

	public strictfp boolean canPassivelyTarget() {
		return true;
	}

	public strictfp boolean joinsGroupFormations() {
		return true;
	}

	public strictfp int bh() {
		return 0;
	}

	public strictfp float getMoveAccelerationSpeed() {
		return 99.0F;
	}

	public strictfp float getBuildSpeed() {
		return 99.0F;
	}

	public strictfp boolean isMoveSlidingMode() {
		return false;
	}

	public strictfp boolean isMoveIgnoringBody() {
		return false;
	}

	public strictfp boolean b(int integer, float float2) {
		return true;
	}

	public abstract void fireTurret(Unit am, int integer);

	public strictfp boolean bk() {
		return false;
	}

	@Override
	public strictfp int getTurretCount() {
		return 1;
	}

	public strictfp boolean turretRotateWithBody() {
		return true;
	}

	public strictfp float getTurretBarrelY(int integer) {
		return 0.0F;
	}

	public strictfp float getTurretLimitingRange(int integer) {
		return 99999.0F;
	}

	public strictfp float getTurretMinRangeSquared(int integer) {
		return -1.0F;
	}

	public strictfp float getTurretIdleDir(int integer) {
		return 0.0F;
	}

	public strictfp float getTurretDefaultAngle(int integer) {
		return this.ci && this.bb() ? this.cg + 180.0F : this.cg;
	}

	public strictfp Point3F bn() {
		int var1 = this.getUpgradeTechLevel();
		return var1 == -1 ? this.getTurretMuzzlePosition(0) : this.getTurretMuzzlePosition(var1);
	}

	public strictfp Point3F getTurretMuzzlePosition(int integer) {
		bf.set(this.getTurretMuzzlePointF(integer));
		return bf;
	}

	public strictfp PointF getTurretMuzzlePointF(int integer) {
		UnitTurretInstance var2 = this.cL[integer];
		float var3 = this.getTurretBarrelY(integer);
		float var4 = this.isFixedFiring() ? this.cg : var2.angle;
		PointF var5 = this.G(integer);
		float var6 = var5.a + CommonUtils.cos(var4) * var3;
		float var7 = var5.b + CommonUtils.sin(var4) * var3;
		bg.a(var6, var7);
		return bg;
	}

	public strictfp Point3F F(int integer) {
		bi.set(this.G(integer));
		bi.z = 0.0F;
		return bi;
	}

	public strictfp PointF G(int integer) {
		UnitTurretInstance var2 = this.cL[integer];
		float var3 = this.eo;
		float var4 = this.ep;
		float var5 = this.getTurretRecoilOffset(integer);
		if (var2.reloadTimer != 0.0F && var5 != 0.0F) {
			float var6 = this.getTurretRecoilOutTime(integer);
			float var7 = this.getTurretRecoilReturnTime(integer);
			float var8 = 0.0F;
			float var9 = this.getTurretShootDelay(integer) - var2.reloadTimer;
			if (var9 < var6) {
				var8 = var9 / var6 * var5;
			} else if (var9 < var7 + var6) {
				var8 = var5 - (var9 - var6) / var7 * var5;
			}

			if (var8 != 0.0F) {
				var3 += CommonUtils.cos(var2.angle) * var8;
				var4 += CommonUtils.sin(var2.angle) * var8;
			}
		}

		bh.a(var3, var4);
		return bh;
	}

	public strictfp float getTurretRecoilOffset(int integer) {
		return 0.0F;
	}

	public strictfp float getTurretRecoilOutTime(int integer) {
		return 4.0F;
	}

	public strictfp float getTurretRecoilReturnTime(int integer) {
		return 6.0F;
	}

	public strictfp PointF K(int integer) {
		PointF var2 = bj;
		var2.a(0.0F, 0.0F);
		UnitTurretInstance var3 = this.cL[integer];
		var2.a = var2.a + var3.h;
		var2.b = var2.b + var3.i;
		return var2;
	}

	public strictfp float getTurretAimOffsetSpread(int integer) {
		return 0.6F;
	}

	public strictfp void M(int integer) {
		if (integer != -1) {
			UnitTurretInstance var4 = this.cL[integer];
			var4.h = 0.0F;
			var4.i = 0.0F;
			if (this.attackTarget != null && this.getTurretAimOffsetSpread(integer) != 0.0F) {
				float var5 = this.attackTarget.radius * this.getTurretAimOffsetSpread(integer);
				var4.h = var4.h + CommonUtils.deterministicRandom(this, (int)(-var5), (int)var5, 1 + integer);
				var4.i = var4.i + CommonUtils.deterministicRandom(this, (int)(-var5), (int)var5, 2 + integer);
			}
		} else {
			int var2 = this.getTurretCount();

			for (int var3 = 0; var3 < var2; var3++) {
				this.M(var3);
			}
		}
	}

	public strictfp void a(UnitSize ab) {
		this.a(abx, true);
	}

	public strictfp void a(UnitSize ab, boolean boolean2) {
		GameEngine var3 = GameEngine.getInstance();
		if (abx == UnitSize.verylargeBuilding) {
			var3.sound.a(SoundEngine.buildingExplode, 0.8F, this.eo, this.ep);
			var3.effectEngine.spawnDelayedExplosions(this.eo, this.ep, this.eq);
			var3.effectEngine.setPriorityOverride(Priority.critical);
			EffectObject var4 = var3.effectEngine.spawnShockwaveLarge(this.eo, this.ep, this.eq, -1127220);
			if (var4 != null) {
				var4.scaleFrom = 0.2F;
				var4.scaleTo = 2.0F;
				var4.drawLayer = 2;
				var4.life = 45.0F;
				var4.lifeMax = var4.life;
				var4.delayedStartTimer = 0.0F;
			}
		} else if (abx == UnitSize.large || abx == UnitSize.building || abx == UnitSize.buildingNoShockwaveOrSmoke) {
			var3.sound.a(SoundEngine.buildingExplode, 0.8F, this.eo, this.ep);
			var3.effectEngine.spawnDelayedExplosions(this.eo, this.ep, this.eq);
		} else if (abx == UnitSize.verysmall) {
			float var6 = 1.0F + CommonUtils.randomFloat(-0.07F, 0.07F);
			var3.sound.a(SoundEngine.unitExplode, 0.4F, var6, this.eo, this.ep);
			var3.effectEngine.spawnExplosion(this.eo, this.ep, this.eq);
		} else if (abx == UnitSize.largeUnit) {
			float var7 = 1.0F + CommonUtils.randomFloat(-0.07F, 0.07F);
			var3.sound.a(SoundEngine.unitExplode, 0.8F, var7, this.eo, this.ep);
			var3.effectEngine.spawnExplosion(this.eo, this.ep, this.eq);
			var3.effectEngine.setPriorityOverride(Priority.critical);
			EffectObject var5 = var3.effectEngine.spawnShockwaveLarge(this.eo, this.ep, this.eq, -1127220);
			if (var5 != null) {
				var5.scaleFrom = 0.2F;
				var5.scaleTo = 2.0F;
				var5.drawLayer = 2;
				var5.life = 45.0F;
				var5.lifeMax = var5.life;
				var5.delayedStartTimer = 0.0F;
			}
		} else {
			float var8 = 1.0F + CommonUtils.randomFloat(-0.07F, 0.07F);
			var3.sound.a(SoundEngine.unitExplode, 0.8F, var8, this.eo, this.ep);
			var3.effectEngine.spawnExplosion(this.eo, this.ep, this.eq);
		}

		if (abx != UnitSize.verysmall) {
			if (abx != UnitSize.buildingNoShockwaveOrSmoke) {
				EffectObject var9 = var3.effectEngine.spawnShockwave(this.eo, this.ep, this.eq, 0);
				if (var9 != null) {
					var9.alpha = 0.9F;
				}
			}

			if (boolean2) {
				if (!this.bO()) {
					this.bo();
				}

				if (abx != UnitSize.buildingNoShockwaveOrSmoke && !this.cK()) {
					EffectEmitter.createSmokeEmitter(this.eo, this.ep);
					EffectEmitter.createFireEmitter(this.eo, this.ep);
					this.drawScorchMark();
				}
			}
		}
	}

	public strictfp void bo() {
		GameEngine var1 = GameEngine.getInstance();
		float var2 = 1.0F;
		float var3 = 1.0F;
		int var4 = this.getNumBitsOnDeath();
		if (var4 >= 10) {
			var2 = 1.2F;
			var3 = 1.4F;
		}

		if (var4 >= 20) {
			var2 = 1.5F;
			var3 = 1.7F;
		}

		if (this.eq > -1.0F) {
			for (int var5 = 0; var5 < var4; var5++) {
				var1.effectEngine.spawnDebrisWithSpeed(this.eo, this.ep, this.eq, var2, var3);
			}
		}
	}

	public strictfp int getNumBitsOnDeath() {
		if (this.dd()) {
			return 8;
		} else {
			return this.bI() ? 7 : 4;
		}
	}

	public strictfp void drawScorchMark() {
		if (!this.cK()) {
			ScorchMark.a(this.eo, this.ep);
		}
	}

	public strictfp int getSightRange() {
		return 15;
	}

	@Override
	public strictfp void updateFogOfWar(boolean boolean1) {
		GameEngine var2 = GameEngine.getInstance();
		if (this.cN == null && this.cO == null) {
			int var3 = this.getSightRange();
			if (var3 > 0) {
				var2.map.a(this.eo, this.ep, var3, this.bX, boolean1);
			}
		}
	}

	public strictfp void br() {
		GameEngine var1 = GameEngine.getInstance();
		RectF var2 = new RectF();
		var2.a(this.cd());
		var2.b = var2.b * var1.map.o;
		var2.d = var2.d * var1.map.o;
		var2.a = var2.a * var1.map.n;
		var2.c = var2.c * var1.map.n;
		var2.a(this.eo, this.ep);
		var2.a(-this.cZ(), -this.da());
		float var3 = 10.0F;
		var2.b -= var3;
		var2.d += var3;
		var2.a -= var3;
		var2.c += var3;

		for (Unit var6 : Unit.getNewUnits()) {
			if (var6 instanceof Unit && var6 != this && var6.intersects(var2)) {
				if (var6 instanceof OrderableUnit && var6.dead) {
					var6.destroy();
				}

				if (var6 instanceof Tree) {
					((Tree)var6).k();
				}
			}
		}
	}

	public strictfp boolean c(Team n) {
		return this.b(false, nx) == null;
	}

	public strictfp boolean a(boolean boolean1, Team n) {
		return this.b(boolean1, nx) == null;
	}

	public strictfp String b(boolean boolean1, Team n) {
		GameEngine var3 = GameEngine.getInstance();
		PlacementRuleSet var4 = this.r().getPlacementRules();
		if (var4 != null) {
			String var5 = var4.a(this, this.eo, this.ep);
			if (var5 != null) {
				return var5;
			}
		}

		if (this.r().placeOnlyOnResPool()) {
			var3.map.a(this.eo, this.ep);
			MapTile var14 = var3.map.e(var3.map.T, var3.map.U);
			if (var14 == null || !var14.i) {
				return "{2}";
			}
		}

		if (!boolean1 && this.a(null, nx)) {
			return "{0}";
		} else {
			if (!this.r().placeOnlyOnResPool()) {
				Rect var15 = this.cd();
				Point var6 = this.a(var3.map, bk);
				int var7 = var6.a;
				int var8 = var6.b;
				UnitTypeInterface var9 = this.r();
				MovementType var10 = var9.getMovementType();

				for (int var11 = var7 + var15.a; var11 <= var7 + var15.c; var11++) {
					for (int var12 = var8 + var15.b; var12 <= var8 + var15.d; var12++) {
						String var13 = Building.a(this, var9, var10, var11, var12, false, nx);
						if (var13 != null) {
							return var13;
						}
					}
				}
			}

			return null;
		}
	}

	public strictfp void N(int integer) {
		GameEngine var2 = GameEngine.getInstance();
		if (!this.r().placeOnlyOnResPool()) {
			Rect var3 = this.cd();
			Point var4 = this.a(var2.map, bl);
			int var5 = var4.a;
			int var6 = var4.b;
			UnitTypeInterface var7 = this.r();
			int var8 = var5 + var3.a;
			int var9 = var6 + var3.b;
			int var10 = var5 + var3.c;
			int var11 = var6 + var3.d;
			if (integer != -2) {
				var2.map.a(this, var8, var9, var10, var11, (int)var2.cameraRenderX, (int)var2.cameraRenderY, var2.graphics, true, integer);
			}
		}
	}

	public strictfp boolean r(Unit am) {
		float var2 = CommonUtils.distanceSquared(this.eo, this.ep, amx.eo, amx.ep);
		float var3 = 9.0F;
		if (!amx.isBuilding()) {
			var3 = this.cj + amx.radius;
			if (var3 < 11.0F) {
				var3 = 11.0F;
			}
		}

		return var2 < var3 * var3;
	}

	public strictfp boolean a(Unit am, Team n) {
		boolean var3 = false;
		if (!this.bI()) {
			var3 = true;
		}

		float var4 = this.cj + CustomUnitMetadataLoader.p + 10.0F;
		float var5 = this.eo - var4;
		float var6 = this.eo + var4;
		float var7 = this.ep - var4;
		float var8 = this.ep + var4;
		Unit[] var9 = Unit.unitList.items();
		int var10 = 0;

		for (int var11 = Unit.unitList.size(); var10 < var11; var10++) {
			Unit var12 = var9[var10];
			float var13 = var12.eo;
			float var14 = var12.ep;
			if (var5 <= var13
				&& var13 <= var6
				&& var7 <= var14
				&& var14 <= var8
				&& var12 != this
				&& (var3 || var12.isBuilding())
				&& !var12.dead
				&& this.r(var12)
				&& var12 != amx
				&& (nx == null || var12.isVisibleToTeam(nx))) {
				return true;
			}
		}

		return false;
	}

	public strictfp OrderableUnit bs() {
		for (Unit var2 : Unit.unitList) {
			if (var2 != this && var2 instanceof OrderableUnit) {
				OrderableUnit var3 = (OrderableUnit)var2;
				if (!var3.bV && var3.bX == this.bX && var3.r() == this.r() && this.t(var3)) {
					return var3;
				}
			}
		}

		return null;
	}

	@Override
	public strictfp void destroy() {
		if (this.cO != null) {
			this.bx();
		}

		this.clearWaypoints();
		this.aI();
		super.a();
	}

	@Override
	public strictfp void bt() {
		this.setQueueLeader(null);
		this.attackTarget = null;
		this.clearWaypoints();
		this.aI();
	}

	@Override
	public strictfp void kill() {
		if (this.cO != null) {
			this.bx();
		}

		super.bu();
	}

	@Override
	public strictfp void die() {
		super.bv();
	}

	@Override
	public strictfp int bw() {
		int var1 = 0;
		var1 = var1 * 31 + super.bw();
		var1 = var1 * 31 + (int)(this.z() * 100.0F);
		var1 = var1 * 31 + (int)(this.A() * 100.0F);
		var1 = var1 * 31 + (int)(this.m() * 100.0F);
		var1 = var1 * 31 + (int)this.getTurretShootDelay(0);
		return var1 * 31 + (int)(this.getMoveAccelerationSpeed() * 100.0F);
	}

	@Override
	strictfp PointF predictFuturePosition(float float1) {
		PointF var2 = this.n(float1);
		dE.a(this.eo + var2.a, this.ep + var2.b);
		return dE;
	}

	public strictfp PointF n(float float1) {
		float var2 = 0.0F;
		float var3 = 0.0F;
		if (this.canReceiveOrders() && this.b == 0.0F) {
			if (this.isMoveSlidingMode()) {
				var2 = this.cc * float1;
				var3 = this.cd * float1;
			} else if (this.cf != 0.0F) {
				float var4 = this.cg;
				if (this.isMoveIgnoringBody()) {
					var4 = this.ch;
				}

				float var5 = this.z() * this.cf * float1;
				var2 = CommonUtils.cos(var4) * var5;
				var3 = CommonUtils.sin(var4) * var5;
			}
		}

		bm.a(var2, var3);
		return bm;
	}

	public strictfp boolean a(CommandType ag) {
		return false;
	}

	public strictfp void a(SpecialAction s, boolean boolean2, float float3, float float4) {
	}

	public strictfp boolean checkTargetedActionOrder(SpecialAction s, float float2, float float3) {
		return true;
	}

	public strictfp void a(Unit am, float float2, int integer) {
		this.U = CommonUtils.applyDeadzone(this.U, float2);
		if (this.U == 0.0F) {
			this.U = 5.0F;
			if (this.s_()) {
				Point3F var4 = this.bn();
				GameEngine var5 = GameEngine.getInstance();
				EffectObject var6 = var5.effectEngine
					.spawnEffect(var4.x, var4.y, this.eq + var4.z, EffectType.custom, false, Priority.low);
				if (var6 != null) {
					float var7 = (float)(amx.eo + (-8.0 + Math.random() * 16.0));
					float var8 = (float)(amx.ep + (-8.0 + Math.random() * 16.0));
					float var9 = CommonUtils.angleBetweenPoints(var4.x, var4.y, var7, var8);
					var6.speedX = CommonUtils.cos(var9) * CommonUtils.randomFloat(2.0F, 4.0F);
					var6.speedY = CommonUtils.sin(var9) * CommonUtils.randomFloat(2.0F, 4.0F);
					var6.frameIndex = 6;
					var6.life = 20.0F;
					var6.lifeMax = var6.life;
					var6.fadeOut = true;
					var6.alpha = 0.8F;
					var6.scaleFrom = 0.2F;
					var6.scaleTo = 1.0F;
				}
			}
		}
	}

	public strictfp void b(Unit am, float float2, int integer) {
		this.U = CommonUtils.applyDeadzone(this.U, float2);
		if (this.U == 0.0F) {
			this.U = 5.0F;
			if (this.s_()) {
				PointF var4 = this.getTurretMuzzlePointF(0);
				GameEngine var5 = GameEngine.getInstance();
				EffectObject var6 = var5.effectEngine
					.spawnEffect(amx.eo, amx.ep, amx.eq, EffectType.custom, false, Priority.low);
				if (var6 != null) {
					float var7 = (float)(var4.a + (-8.0 + Math.random() * 16.0));
					float var8 = (float)(var4.b + (-8.0 + Math.random() * 16.0));
					float var9 = CommonUtils.angleBetweenPoints(amx.eo, amx.ep - amx.eq, var7, var8);
					var6.speedX = CommonUtils.cos(var9) * CommonUtils.randomFloat(2.0F, 4.0F);
					var6.speedY = CommonUtils.sin(var9) * CommonUtils.randomFloat(2.0F, 4.0F);
					var6.frameIndex = 5;
					var6.life = 20.0F;
					var6.lifeMax = var6.life;
					var6.fadeOut = true;
					var6.alpha = 0.8F;
					var6.scaleFrom = 0.2F;
					var6.scaleTo = 1.0F;
				}
			}
		}
	}

	public strictfp z a(Waypoint au, UnitTypeInterface as, int integer, float float4, float float5) {
		GameEngine var6 = GameEngine.getInstance();
		SpecialAction var7 = this.a(asx, integer, false);
		if (var7 == null) {
			GameEngine.logWarning("Unit '" + this.r().i() + "' can not build:" + asx.i());
			return bn.a();
		} else {
			if (!aux.n) {
				if (var7.g(this)) {
					GameEngine.logWarning("Builder '" + this.r().i() + "' tried to build a locked building:" + var7.O());
					return bn.a();
				}

				if (!var7.b(this) && !var7.u(this)) {
					GameEngine.logWarning(
						"Builder '" + this.r().i() + "' tried to build a unavailable building:" + var7.O() + " (add isLocked:false to fix)"
					);
					return bn.a();
				}
			}

			if (!asx.ignoreInUnitCapCalculation() && !var7.x() && this.bX.getBuildingCount() >= this.bX.getUnitsMax()) {
				if (this.bX == var6.playerTeam) {
					var6.interfaceEngine.showImportantScreenMessage(var6.interfaceEngine.battleInterface.al);
				}

				return bn.a();
			} else {
				Unit var8 = Unit.a(asx);
				if (var8 == null) {
					String var15 = "{build is null}";
					if (aux.unitType != null) {
						var15 = aux.unitType.i();
					}

					GameEngine.log("Build unit type missing: " + var15);
					return bn.a();
				} else {
					Unit var9 = Building.g(asx);
					if (!CustomPrice.b(asx.getPrice(), var7.B()) || !CustomPrice.b(asx.getStreamingPrice(), var7.r_())) {
						var9.bx = var7.B();
						var9.by = var7.r_();
					}

					if (var7 instanceof ActionConfig) {
						var9.bx = null;
						var9.by = null;
					}

					var9.buildProgress = 0.0F;
					var9.cn = 0.0F;
					var6.map.b(float4 - var9.getMinimapOriginX() + 1.0F, float5 - var9.getMinimapOriginY() + 1.0F);
					var9.eo = var6.map.T + var9.getMinimapOriginX();
					var9.ep = var6.map.U + var9.getMinimapOriginY();
					var9.setTeamOnCreate(this.bX);
					var9.B(this);
					if (integer != 1 && var9 instanceof OrderableUnit) {
						((OrderableUnit)var9).a(integer);
					}

					var9.cP();
					if (var9 instanceof OrderableUnit) {
						OrderableUnit var10 = (OrderableUnit)var9;
						boolean var11 = false;
						OrderableUnit var12 = null;
						if (this.dieOnConstruct()) {
							var12 = this;
						} else if (!this.bT && !this.bI()) {
							var12 = this;
						}

						if (var10.a(var12, null)) {
							var11 = true;
						}

						if (!var11 && !var10.a(true, null)) {
							var11 = true;
						}

						if (var11) {
							var9.destroy();
							z var13 = bn.a();
							OrderableUnit var14 = ((OrderableUnit)var9).bs();
							var13.b = var14;
							var13.d = var7;
							if (var14 == null) {
							}

							return var13;
						}
					}

					CustomPrice var16 = var7.B();
					if (aux.n) {
						var16 = CustomPrice.a;
					}

					if (!var16.c(this)) {
						var9.destroy();
						z var19 = bn.a();
						this.lastBuildFailMillis = var6.gameTimeMillis;
						if (this.waypointTimer < 1000.0F) {
							var19.c = true;
							Blueprint var20 = Blueprint.getBlueprintAt(this.bX, var9.eo, var9.ep);
							if (var20 != null) {
								var20.i = true;
							}
						}

						return var19;
					} else {
						this.m(var9);
						if (var9 instanceof OrderableUnit) {
							OrderableUnit var17 = (OrderableUnit)var9;
							var17.br();
							if (var9.isBuilding()) {
								var6.pathEngine.updateUnitCosts(var17);
							}
						}

						Team.addUnitToCache(var9);
						z var18 = bn.a();
						var18.a = var9;
						var18.d = var7;
						return var18;
					}
				}
			}
		}
	}

	public strictfp boolean attachRequest(OrderableUnit y, AttachmentSlot n) {
		return false;
	}

	public strictfp boolean deattachRequest(OrderableUnit y) {
		return false;
	}

	public strictfp void bx() {
		if (this.cO != null) {
			if (this.cO.bV) {
			}

			if (!this.cO.deattachRequest(this)) {
				GameEngine.logWarning("Deattach failed, forcing deattach. Child:" + this.cB() + " Parent:" + this.cO.cB());
				this.cO = null;
				this.cP = null;
			}
		}
	}

	public strictfp AttachmentSlot getAttachmentSlotById(short short1) {
		return null;
	}

	public static strictfp Unit a(OrderableUnit y, float float2, float float3, float float4, CustomTagTags h) {
		if (float4 <= 0.0F) {
			return null;
		} else {
			bo.checkAttackable = true;
			bo.includeIncomplete = false;
			bo.foundUnit = null;
			bo.bestDistance = float4 * float4;
			bo.requiredTags = hx;
			bo.x = float2;
			bo.y = float3;
			GameEngine var5 = GameEngine.getInstance();
			var5.unitGeoIndex.a(float2, float3, float4, yx, 0.0F, bo);
			return bo.foundUnit;
		}
	}

	public strictfp CustomPrice getQueuedTotalPrice() {
		return CustomPrice.a;
	}

	public strictfp FastArrayList getTransportedUnits() {
		return bq;
	}

	public strictfp boolean isUnloading() {
		return false;
	}

	public strictfp int getTransportedUnitCount() {
		return 0;
	}

	@Override
	public strictfp void bC() {
		CustomPrice var1 = this.bE();
		CustomPrice var2 = this.getCurrentQueuedPrice();
		CustomPrice var3;
		if (var1 == null) {
			var3 = var2;
		} else if (var2 == null) {
			var3 = var1;
		} else {
			var3 = CustomPrice.a(var1, var2);
		}

		if (this.dJ != null || var3 != null) {
			if (this.dJ == null || var3 == null || !this.dJ.b(var3)) {
				Team.b((Unit)this);
				this.dJ = var3;
				Team.addUnitToCache(this);
			}
		}
	}

	public strictfp CustomPrice getCurrentQueuedPrice() {
		return null;
	}

	public strictfp CustomPrice bE() {
		Unit var1 = this.X();
		if (var1 != null) {
			Waypoint var2 = this.getCurrentWaypoint();
			if (var2 != null) {
				if (var2.type == WaypointType.repair && var1.buildProgress < 1.0F) {
					CustomPrice var3 = this.g(var1);
					float var4 = this.a_(var1) * 60.0F;
					if (var3 != null) {
						return CustomPrice.a(var3, -var4);
					}
				}

				if (var2.type == WaypointType.reclaim) {
					if (var1.buildProgress < 1.0F) {
						CustomPrice var9 = this.g(var1);
						float var11 = this.getNanoUnbuildSpeed(var1) * 60.0F;
						if (var9 != null) {
							return CustomPrice.a(var9, var11);
						}
					} else {
						boolean var10 = this.y(var1);
						if (var10) {
							float var12 = this.z(var1);
							CustomPrice var5 = var1.getPrice();
							CustomPrice var6 = var1.getReclaimPrice();
							if (var6 != null) {
								var5 = var6;
							}

							float var7 = var12 * 60.0F;
							float var8 = var7 / var1.maxHp;
							return CustomPrice.a(var5, var8);
						}
					}
				}
			}
		}

		return null;
	}

	static {
		aE.a(128, 255, 255, 255);
		aE.lockDown();
		aF.a(aE);
		aF.setAntiAlias(true);
		aF.d(true);
		aF.b(true);
		aF.lockDown();
	}
}
