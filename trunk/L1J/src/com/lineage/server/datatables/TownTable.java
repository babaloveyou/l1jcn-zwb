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
package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.L1DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Town;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

/**
 * 城镇资料表
 */
public class TownTable {

    private static Logger _log = Logger.getLogger(TownTable.class.getName());

    private static TownTable _instance;

    public static TownTable getInstance() {
        if (_instance == null) {
            _instance = new TownTable();
        }

        return _instance;
    }

    private final Map<Integer, L1Town> _towns = Maps.newConcurrentMap();

    private TownTable() {
        this.load();
    }

    public synchronized void addSalesMoney(final int town_id,
            final int salesMoney) {
        Connection con = null;
        PreparedStatement pstm = null;

        final L1Town town = TownTable.getInstance().getTownTable(town_id);
        final int townTaxRate = town.get_tax_rate();

        int townTax = salesMoney / 100 * townTaxRate;
        int townFixTax = salesMoney / 100 * 2;

        if ((townTax <= 0) && (townTaxRate > 0)) {
            townTax = 1;
        }
        if ((townFixTax <= 0) && (townTaxRate > 0)) {
            townFixTax = 1;
        }

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("UPDATE town SET sales_money = sales_money + ?, town_tax = town_tax + ?, town_fix_tax = town_fix_tax + ? WHERE town_id = ?");
            pstm.setInt(1, salesMoney);
            pstm.setInt(2, townTax);
            pstm.setInt(3, townFixTax);
            pstm.setInt(4, town_id);
            pstm.execute();

            town.set_sales_money(town.get_sales_money() + salesMoney);
            town.set_town_tax(town.get_town_tax() + townTax);
            town.set_town_fix_tax(town.get_town_fix_tax() + townFixTax);

        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public L1Town getTownTable(final int id) {
        return this._towns.get(id);
    }

    public L1Town[] getTownTableList() {
        return this._towns.values().toArray(new L1Town[this._towns.size()]);
    }

    public boolean isLeader(final L1PcInstance pc, final int town_id) {
        final L1Town town = this.getTownTable(town_id);
        return (town.get_leader_id() == pc.getId());
    }

    public void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        this._towns.clear();

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM town");

            int townid;
            rs = pstm.executeQuery();

            while (rs.next()) {
                final L1Town town = new L1Town();
                townid = rs.getInt("town_id");
                town.set_townid(townid);
                town.set_name(rs.getString("name"));
                town.set_leader_id(rs.getInt("leader_id"));
                town.set_leader_name(rs.getString("leader_name"));
                town.set_tax_rate(rs.getInt("tax_rate"));
                town.set_tax_rate_reserved(rs.getInt("tax_rate_reserved"));
                town.set_sales_money(rs.getInt("sales_money"));
                town.set_sales_money_yesterday(rs
                        .getInt("sales_money_yesterday"));
                town.set_town_tax(rs.getInt("town_tax"));
                town.set_town_fix_tax(rs.getInt("town_fix_tax"));

                this._towns.put(new Integer(townid), town);
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateSalesMoneyYesterday() {
        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("UPDATE town SET sales_money_yesterday = sales_money, sales_money = 0");
            pstm.execute();
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateTaxRate() {
        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("UPDATE town SET tax_rate = tax_rate_reserved");
            pstm.execute();
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
