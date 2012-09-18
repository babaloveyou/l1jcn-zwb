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
package com.lineage.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.L1DatabaseFactory;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;

/**
 * 精灵仓库
 */
public class L1DwarfForElfInventory extends L1Inventory {

    private static final long serialVersionUID = 1L;

    private static Logger _log = Logger.getLogger(L1DwarfForElfInventory.class
            .getName());

    private final L1PcInstance _owner;

    public L1DwarfForElfInventory(final L1PcInstance owner) {
        this._owner = owner;
    }

    // ＤＢのcharacter_elf_warehouseから削除
    @Override
    public void deleteItem(final L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("DELETE FROM character_elf_warehouse WHERE id = ?");
            pstm.setInt(1, item.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        this._items.remove(this._items.indexOf(item));
    }

    // ＤＢのcharacter_elf_warehouseへ登録
    @Override
    public void insertItem(final L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("INSERT INTO character_elf_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, bless = ?, attr_enchant_kind = ?, attr_enchant_level = ?,firemr = ?,watermr = ?,earthmr = ?,windmr = ?,addsp = ?,addhp = ?,addmp = ?,hpr = ?,mpr = ?,m_def = ?");
            pstm.setInt(1, item.getId());
            pstm.setString(2, this._owner.getAccountName());
            pstm.setInt(3, item.getItemId());
            pstm.setString(4, item.getName());
            pstm.setInt(5, item.getCount());
            pstm.setInt(6, item.getEnchantLevel());
            pstm.setInt(7, item.isIdentified() ? 1 : 0);
            pstm.setInt(8, item.get_durability());
            pstm.setInt(9, item.getChargeCount());
            pstm.setInt(10, item.getRemainingTime());
            pstm.setTimestamp(11, item.getLastUsed());
            pstm.setInt(12, item.getBless());
            pstm.setInt(13, item.getAttrEnchantKind());
            pstm.setInt(14, item.getAttrEnchantLevel());
            pstm.setInt(15, item.getFireMr());
            pstm.setInt(16, item.getWaterMr());
            pstm.setInt(17, item.getEarthMr());
            pstm.setInt(18, item.getWindMr());
            pstm.setInt(19, item.getaddSp());
            pstm.setInt(20, item.getaddHp());
            pstm.setInt(21, item.getaddMp());
            pstm.setInt(22, item.getHpr());
            pstm.setInt(23, item.getMpr());
            pstm.setInt(24, item.getM_Def());
            pstm.execute();
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

    }

    // ＤＢのcharacter_itemsの读取
    @Override
    public void loadItems() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM character_elf_warehouse WHERE account_name = ?");
            pstm.setString(1, this._owner.getAccountName());

            rs = pstm.executeQuery();

            while (rs.next()) {
                final L1ItemInstance item = new L1ItemInstance();
                final int objectId = rs.getInt("id");
                item.setId(objectId);
                final L1Item itemTemplate = ItemTable.getInstance()
                        .getTemplate(rs.getInt("item_id"));
                item.setItem(itemTemplate);
                item.setCount(rs.getInt("count"));
                item.setEquipped(false);
                item.setEnchantLevel(rs.getInt("enchantlvl"));
                item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
                item.set_durability(rs.getInt("durability"));
                item.setChargeCount(rs.getInt("charge_count"));
                item.setRemainingTime(rs.getInt("remaining_time"));
                item.setLastUsed(rs.getTimestamp("last_used"));
                item.setBless(rs.getInt("bless"));
                item.setAttrEnchantKind(rs.getInt("attr_enchant_kind"));
                item.setAttrEnchantLevel(rs.getInt("attr_enchant_level"));
                item.setFireMr(rs.getInt("firemr"));
                item.setWaterMr(rs.getInt("watermr"));
                item.setEarthMr(rs.getInt("earthmr"));
                item.setWindMr(rs.getInt("windmr"));
                item.setaddSp(rs.getInt("addsp"));
                item.setaddHp(rs.getInt("addhp"));
                item.setaddMp(rs.getInt("addmp"));
                item.setHpr(rs.getInt("hpr"));
                item.setMpr(rs.getInt("mpr"));
                item.setM_Def(rs.getInt("m_def"));
                // 登入钥匙记录
                if (item.getItem().getItemId() == 40312) {
                    InnKeyTable.checkey(item);
                }
                this._items.add(item);
                L1World.getInstance().storeObject(item);
            }

        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // ＤＢのcharacter_elf_warehouseを更新
    @Override
    public void updateItem(final L1ItemInstance item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("UPDATE character_elf_warehouse SET count = ? WHERE id = ?");
            pstm.setInt(1, item.getCount());
            pstm.setInt(2, item.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
