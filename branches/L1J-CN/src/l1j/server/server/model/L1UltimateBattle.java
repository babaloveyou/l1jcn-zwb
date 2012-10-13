/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.identity.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.Lists;
import l1j.william.SystemMessage;

// Referenced classes of package l1j.server.server.model:
// L1UltimateBattle

public class L1UltimateBattle {
	private int _locX;

	private int _locY;

	private L1Location _location; // 中心点

	private short _mapId;

	private int _locX1;

	private int _locY1;

	private int _locX2;

	private int _locY2;

	private int _ubId;

	private int _pattern;

	private boolean _isNowUb;

	private boolean _active; // UB入场可能～竞技终了までtrue

	private int _minLevel;

	private int _maxLevel;

	private int _maxPlayer;

	private boolean _enterRoyal;

	private boolean _enterKnight;

	private boolean _enterMage;

	private boolean _enterElf;

	private boolean _enterDarkelf;

	private boolean _enterDragonKnight;

	private boolean _enterIllusionist;

	private boolean _enterMale;

	private boolean _enterFemale;

	private boolean _usePot;

	private int _hpr;

	private int _mpr;

	private static int BEFORE_MINUTE = 5; // 5分前から入场开始

	private Set<Integer> _managers = new HashSet<Integer>();

	private SortedSet<Integer> _ubTimes = new TreeSet<Integer>();

	//private HashMap<Integer, HashMap<String, Integer>> _ub_supplies = new HashMap<Integer, HashMap<String,Integer>>();
	private List<HashMap<String, Integer>> _ub_supplies = Lists.newList();
	
	private static final Logger _log = Logger.getLogger(L1UltimateBattle.class.getName());

	private final List<L1PcInstance> _members = Lists.newList();
	
	private static final int _itemWin1 = Integer.valueOf(l1j.william.L1WilliamSystemMessage.ShowMessage(130)).intValue(); // 胜利奖励物品1
	private static final int _countWin1 = Integer.valueOf(l1j.william.L1WilliamSystemMessage.ShowMessage(131)).intValue(); // 胜利奖励物品1数量
	private static final int _itemWin2 = Integer.valueOf(l1j.william.L1WilliamSystemMessage.ShowMessage(132)).intValue(); // 胜利奖励物品2
	private static final int _countWin2 = Integer.valueOf(l1j.william.L1WilliamSystemMessage.ShowMessage(133)).intValue(); // 胜利奖励物品2数量

	/**
	 * ラウンド开始时のメッセージを送信する。
	 * 
	 * @param curRound
	 *            开始するラウンド
	 */
	private void sendRoundMessage(int curRound) {
		// XXX - このIDは间违っている
		String msg = "即将开启第" + curRound + "阶段，请大家耐心等待几分钟！";
		sendMessage(msg);
	}

	/**
	 * ポーション等の补给アイテムを出现させる。
	 * 
	 * @param curRound
	 *            现在のラウンド
	 */
	private void spawnSupplies(int curRound) {
		int size = _ub_supplies.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Integer> ub_supplies = _ub_supplies.get(i);
			if(ub_supplies.get("ub_round")==curRound){
				int ub_item_id = ub_supplies.get("ub_item_id");
				int ub_item_stackcont = ub_supplies.get("ub_item_stackcont");
				int ub_item_cont = ub_supplies.get("ub_item_cont");				
				spawnGroundItem(ub_item_id, ub_item_stackcont, ub_item_cont);
			}
		}
	}

	/**
	 * コロシアムから出たメンバーをメンバーリストから削除する。
	 */
	private void removeRetiredMembers() {
		L1PcInstance[] temp = getMembersArray();
		for (L1PcInstance element : temp) {
			if (element.getMapId() != _mapId) {
				removeMember(element);
			}
		}		
	}

	/**
	 * UBに参加しているプレイヤーへメッセージ(S_ServerMessage)を送信する。
	 * 
	 * @param type
	 *            メッセージタイプ
	 * @param msg
	 *            送信するメッセージ
	 */
	private void sendMessage(int type, String msg) {
		for (L1PcInstance pc : getMembersArray()) {
			pc.sendPackets(new S_ServerMessage(type, msg));
		}
	}
	
	private void sendMessage(String msg) {
		for (L1PcInstance pc : getMembersArray()) {
			pc.sendPackets(new S_SystemMessage(msg));
		}
	}

	/**
	 * コロシアム上へアイテムを出现させる。
	 * 
	 * @param itemId
	 *            出现させるアイテムのアイテムID
	 * @param stackCount
	 *            アイテムのスタック数
	 * @param count
	 *            出现させる数
	 */
	private void spawnGroundItem(int itemId, int stackCount, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemId);
		if (temp == null) {
			return;
		}

		for (int i = 0; i < count; i++) {
			L1Location loc = _location.randomLocation((getLocX2() - getLocX1()) / 2, false);
			if (temp.isStackable()) {
				L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
				item.setEnchantLevel(0);
				item.setCount(stackCount);
				L1GroundInventory ground = L1World.getInstance().getInventory(loc.getX(), loc.getY(), _mapId);
				if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
					ground.storeItem(item);
				}
			}
			else {
				L1ItemInstance item = null;
				for (int createCount = 0; createCount < stackCount; createCount++) {
					item = ItemTable.getInstance().createItem(itemId);
					item.setEnchantLevel(0);
					L1GroundInventory ground = L1World.getInstance().getInventory(loc.getX(), loc.getY(), _mapId);
					if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
						ground.storeItem(item);
					}
				}
			}
		}
	}

	/**
	 * コロシアム上のアイテムとモンスターを全て削除する。
	 */
	private void clearColosseum() {
		for (Object obj : L1World.getInstance().getVisibleObjects(_mapId).values()) {
			if (obj instanceof L1MonsterInstance) // モンスター削除
			{
				L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHpDirect(0);
					mob.deleteMe();

				}
			}
			else if (obj instanceof L1Inventory) // アイテム削除
			{
				L1Inventory inventory = (L1Inventory) obj;
				inventory.clearItems();
			}
		}
	}

	/**
	 * コンストラクタ。
	 */
	public L1UltimateBattle() {
	}

	class UbThread implements Runnable {
		/**
		 * 竞技开始までをカウントダウンする。
		 * 
		 * @throws InterruptedException
		 */
		private void countDown() throws InterruptedException {
			// XXX - このIDは间违っている
			final int MSGID_COUNT = 637;
			final int MSGID_START = 632;

			for (int loop = 0; loop < BEFORE_MINUTE * 60 - 10; loop++) { // 开始10秒前まで待つ
				Thread.sleep(1000);
				// removeRetiredMembers();
			}
			removeRetiredMembers();

			sendMessage(MSGID_COUNT, "10"); // 10秒前

			Thread.sleep(5000);
			sendMessage(MSGID_COUNT, "5"); // 5秒前

			Thread.sleep(1000);
			sendMessage(MSGID_COUNT, "4"); // 4秒前

			Thread.sleep(1000);
			sendMessage(MSGID_COUNT, "3"); // 3秒前

			Thread.sleep(1000);
			sendMessage(MSGID_COUNT, "2"); // 2秒前

			Thread.sleep(1000);
			sendMessage(MSGID_COUNT, "1"); // 1秒前

			Thread.sleep(1000);
			sendMessage(MSGID_START, "竞技场战斗开始"); // スタート
			removeRetiredMembers();
		}

		/**
		 * 全てのモンスターが出现した后、次のラウンドが始まるまでの时间を待机する。
		 * 
		 * @param curRound
		 *            现在のラウンド
		 * @throws InterruptedException
		 */
		private void waitForNextRound(int curRound) throws InterruptedException {
			final int WAIT_TIME_TABLE[] =
			{ 6, 6, 12, 18, 18};

			int wait = WAIT_TIME_TABLE[curRound - 1];
			for (int i = 0; i < wait; i++) {
				Thread.sleep(10000);
				// removeRetiredMembers();
			}
			removeRetiredMembers();
		}

		/**
		 * スレッドプロシージャ。
		 */
		@Override
		public void run() {
			try {
				setActive(true);
				countDown();
				setNowUb(true);
				for (int round = 1; round <= 5; round++) {
					sendRoundMessage(round);

					L1UbPattern pattern = UBSpawnTable.getInstance().getPattern(_ubId, _pattern);

					List<L1UbSpawn> spawnList = pattern.getSpawnList(round);

					for (L1UbSpawn spawn : spawnList) {
						if (getMembersCount() > 0) {
							spawn.spawnAll();
						}
						Thread.sleep(spawn.getSpawnDelay() * 1000);
						// removeRetiredMembers();
					}

					if (getMembersCount() > 0) {
						spawnSupplies(round);
					}

					waitForNextRound(round);
				}
				L1World.getInstance().broadcastServerMessage("无限大战10秒后结束。");
				final int MSGID_COUNT = 637;
				sendMessage(MSGID_COUNT, "10"); // 10秒前
				Thread.sleep(5000);
				sendMessage(MSGID_COUNT, "5"); // 5秒前
				Thread.sleep(1000);
				sendMessage(MSGID_COUNT, "4"); // 4秒前
				Thread.sleep(1000);
				sendMessage(MSGID_COUNT, "3"); // 3秒前
				Thread.sleep(1000);
				sendMessage(MSGID_COUNT, "2"); // 2秒前
				Thread.sleep(1000);
				sendMessage(MSGID_COUNT, "1"); // 1秒前
				
				for (L1PcInstance pc : getMembersArray()) // コロシアム内に居るPCを外へ出す
				{
					int rndx = Random.nextInt(4);
					int rndy = Random.nextInt(4);
					int locx = 32581 + rndx;
					int locy = 32929 + rndy;
					short mapid = 0;
					L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
					giveReward(pc);
					removeMember(pc);
				}
				clearColosseum();
				setActive(false);
				setNowUb(false);				
			}
			catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
	/**
	 * 获取无限大战奖励
	 */
	private void giveReward(L1PcInstance pc){
		pc.getInventory().storeItem(_itemWin1,_countWin1);
		pc.getInventory().storeItem(_itemWin2,_countWin2);
		//奖励队员
		L1World.getInstance().broadcastServerMessage("恭喜参加无限大战竞技场的队员获得胜利，特此奖励队员，请查看背包。");
		//无限大赛讯息end
	}
	
	/**
	 * アルティメットバトルを开始する。
	 * 
	 * @param ubId
	 *            开始するアルティメットバトルのID
	 */
	public void start() {
		int patternsMax = UBSpawnTable.getInstance().getMaxPattern(_ubId);
		_pattern = Random.nextInt(patternsMax) + 1; // 出现パターンを决める

		UbThread ub = new UbThread();
		GeneralThreadPool.getInstance().execute(ub);
	}

	/**
	 * プレイヤーを参加メンバーリストへ追加する。
	 * 
	 * @param pc
	 *            新たに参加するプレイヤー
	 */
	public void addMember(L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
		}
	}

	/**
	 * プレイヤーを参加メンバーリストから削除する。
	 * 
	 * @param pc
	 *            削除するプレイヤー
	 */
	public void removeMember(L1PcInstance pc) {
		_members.remove(pc);
	}

	/**
	 * 参加メンバーリストをクリアする。
	 */
	public void clearMembers() {
		_members.clear();
	}

	/**
	 * プレイヤーが、参加メンバーかを返す。
	 * 
	 * @param pc
	 *            调べるプレイヤー
	 * @return 参加メンバーであればtrue、そうでなければfalse。
	 */
	public boolean isMember(L1PcInstance pc) {
		return _members.contains(pc);
	}

	/**
	 * 参加メンバーの配列を作成し、返す。
	 * 
	 * @return 参加メンバーの配列
	 */
	public L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	/**
	 * 参加メンバー数を返す。
	 * 
	 * @return 参加メンバー数
	 */
	public int getMembersCount() {
		return _members.size();
	}

	/**
	 * UB中かを设定する。
	 * 
	 * @param i
	 *            true/false
	 */
	private void setNowUb(boolean i) {
		_isNowUb = i;
	}

	/**
	 * UB中かを返す。
	 * 
	 * @return UB中であればtrue、そうでなければfalse。
	 */
	public boolean isNowUb() {
		return _isNowUb;
	}

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int id) {
		_ubId = id;
	}

	public short getMapId() {
		return _mapId;
	}

	public void setMapId(short mapId) {
		_mapId = mapId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public void setMinLevel(int level) {
		_minLevel = level;
	}

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(int level) {
		_maxLevel = level;
	}

	public int getMaxPlayer() {
		return _maxPlayer;
	}

	public void setMaxPlayer(int count) {
		_maxPlayer = count;
	}

	public void setEnterRoyal(boolean enterRoyal) {
		_enterRoyal = enterRoyal;
	}

	public void setEnterKnight(boolean enterKnight) {
		_enterKnight = enterKnight;
	}

	public void setEnterMage(boolean enterMage) {
		_enterMage = enterMage;
	}

	public void setEnterElf(boolean enterElf) {
		_enterElf = enterElf;
	}

	public void setEnterDarkelf(boolean enterDarkelf) {
		_enterDarkelf = enterDarkelf;
	}

	public void setEnterDragonKnight(boolean enterDragonKnight) {
		_enterDragonKnight = enterDragonKnight;
	}

	public void setEnterIllusionist(boolean enterIllusionist) {
		_enterIllusionist = enterIllusionist;
	}

	public void setEnterMale(boolean enterMale) {
		_enterMale = enterMale;
	}

	public void setEnterFemale(boolean enterFemale) {
		_enterFemale = enterFemale;
	}

	public boolean canUsePot() {
		return _usePot;
	}

	public void setUsePot(boolean usePot) {
		_usePot = usePot;
	}

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int hpr) {
		_hpr = hpr;
	}

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int mpr) {
		_mpr = mpr;
	}

	public int getLocX1() {
		return _locX1;
	}

	public void setLocX1(int locX1) {
		_locX1 = locX1;
	}

	public int getLocY1() {
		return _locY1;
	}

	public void setLocY1(int locY1) {
		_locY1 = locY1;
	}

	public int getLocX2() {
		return _locX2;
	}

	public void setLocX2(int locX2) {
		_locX2 = locX2;
	}

	public int getLocY2() {
		return _locY2;
	}

	public void setLocY2(int locY2) {
		_locY2 = locY2;
	}

	// setされたlocx1～locy2から中心点を求める。
	public void resetLoc() {
		_locX = (_locX2 + _locX1) / 2;
		_locY = (_locY2 + _locY1) / 2;
		_location = new L1Location(_locX, _locY, _mapId);
	}

	public L1Location getLocation() {
		return _location;
	}

	public void addManager(int npcId) {
		_managers.add(npcId);
	}

	public boolean containsManager(int npcId) {
		return _managers.contains(npcId);
	}

	public void addUbTime(int time) {
		_ubTimes.add(time);
	}

	public String getNextUbTime() {
		return intToTimeFormat(nextUbTime());
	}

	private int nextUbTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		int nowTime = Integer.valueOf(sdf.format(getRealTime().getTime()));
		SortedSet<Integer> tailSet = _ubTimes.tailSet(nowTime);
		if (tailSet.isEmpty()) {
			tailSet = _ubTimes;
		}
		return tailSet.first();
	}

	private static String intToTimeFormat(int n) {
		return n / 100 + ":" + n % 100 / 10 + "" + n % 10;
	}

	private static Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	public boolean checkUbTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		realTime.add(Calendar.MINUTE, BEFORE_MINUTE);
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return _ubTimes.contains(nowTime);
	}

	private void setActive(boolean f) {
		_active = f;
	}

	/**
	 * @return UB入场可能～竞技终了まではtrue,それ以外はfalseを返す。
	 */
	public boolean isActive() {
		return _active;
	}

	/**
	 * UBに参加可能か、レベル、クラスをチェックする。
	 * 
	 * @param pc
	 *            UBに参加できるかチェックするPC
	 * @return 参加出来る场合はtrue,出来ない场合はfalse
	 */
	public boolean canPcEnter(L1PcInstance pc) {
		_log.log(Level.FINE, "pcname={0} ubid={1} minlvl={2} maxlvl={3}", new Object[]
		{ pc.getName(), _ubId, _minLevel, _maxLevel });
		// 参加可能なレベルか
		if (!IntRange.includes(pc.getLevel(), _minLevel, _maxLevel)) {
			return false;
		}

		// 参加可能なクラスか
		if (!((pc.isCrown() && _enterRoyal) || (pc.isKnight() && _enterKnight) || (pc.isWizard() && _enterMage) || (pc.isElf() && _enterElf)
				|| (pc.isDarkelf() && _enterDarkelf) || (pc.isDragonKnight() && _enterDragonKnight) || (pc.isIllusionist() && _enterIllusionist))) {
			return false;
		}

		return true;
	}

	private String[] _ubInfo;

	public String[] makeUbInfoStrings() {
		if (_ubInfo != null) {
			return _ubInfo;
		}
		String nextUbTime = getNextUbTime();
		// クラス
		StringBuilder classesBuff = new StringBuilder();
		if (_enterDarkelf) {
			classesBuff.append("ダーク エルフ ");
		}
		if (_enterMage) {
			classesBuff.append("ウィザード ");
		}
		if (_enterElf) {
			classesBuff.append("エルフ ");
		}
		if (_enterKnight) {
			classesBuff.append("ナイト ");
		}
		if (_enterRoyal) {
			classesBuff.append("プリンス ");
		}
		if (_enterDragonKnight) {
			classesBuff.append("ドラゴンナイト ");
		}
		if (_enterIllusionist) {
			classesBuff.append("イリュージョニスト ");
		}
		String classes = classesBuff.toString().trim();
		// 性别
		StringBuilder sexBuff = new StringBuilder();
		if (_enterMale) {
			sexBuff.append("男 ");
		}
		if (_enterFemale) {
			sexBuff.append("女 ");
		}
		String sex = sexBuff.toString().trim();
		String loLevel = String.valueOf(_minLevel);
		String hiLevel = String.valueOf(_maxLevel);
		String teleport = _location.getMap().isEscapable() ? "可能" : "不可能";
		String res = _location.getMap().isUseResurrection() ? "可能" : "不可能";
		String pot = "可能";
		String hpr = String.valueOf(_hpr);
		String mpr = String.valueOf(_mpr);
		String summon = _location.getMap().isTakePets() ? "可能" : "不可能";
		String summon2 = _location.getMap().isRecallPets() ? "可能" : "不可能";
		_ubInfo = new String[]
		{ nextUbTime, classes, sex, loLevel, hiLevel, teleport, res, pot, hpr, mpr, summon, summon2 };
		return _ubInfo;
	}

	public void add_ub_supplies(HashMap<String,Integer> ub_supplies){
		this._ub_supplies.add(ub_supplies);
	}
}