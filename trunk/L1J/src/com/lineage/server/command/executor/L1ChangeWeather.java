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
package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_Weather;

/**
 * GM指令：改变天气
 */
public class L1ChangeWeather implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1ChangeWeather();
    }

    private L1ChangeWeather() {
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName,
            final String arg) {
        try {
            final StringTokenizer tok = new StringTokenizer(arg);
            final int weather = Integer.parseInt(tok.nextToken());
            L1World.getInstance().setWeather(weather);
            L1World.getInstance().broadcastPacketToAll(new S_Weather(weather));
        } catch (final Exception e) {
            pc.sendPackets(new S_SystemMessage("请输入 " + cmdName + " 0～3、16～19。"));
        }
    }
}
