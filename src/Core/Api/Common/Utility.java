package Core.Api.Common;

import Core.API;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;

public class Utility {

    public static boolean debug = false;

    private MethodProvider mp;

    public Utility(API api) {
        API api1 = api;
        mp = api.mp;
    }

    public void log(String str) {
        if(debug) {
            mp.log(str);
        }
    }

    private final Area[] BANKS = {Banks.VARROCK_WEST, Banks.AL_KHARID, Banks.ARCEUUS_HOUSE, Banks.ARDOUGNE_NORTH, Banks.ARDOUGNE_SOUTH,
        Banks.CAMELOT, Banks.CANIFIS, Banks.CASTLE_WARS, Banks.CATHERBY, Banks.DRAYNOR, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks.FALADOR_WEST,
        Banks.DUEL_ARENA, Banks.GNOME_STRONGHOLD, Banks.GRAND_EXCHANGE, Banks.HOSIDIUS_HOUSE, Banks.LOVAKENGJ_HOUSE, Banks.LOVAKITE_MINE,
        Banks.LUMBRIDGE_LOWER, Banks.LUMBRIDGE_UPPER, Banks.YANILLE, Banks.VARROCK_EAST, Banks.TZHAAR, Banks.PISCARILIUS_HOUSE, Banks.SHAYZIEN_HOUSE,
    };

    private final Area[] BANKS_F2P = {Banks.VARROCK_WEST, Banks.AL_KHARID, Banks.DRAYNOR, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks.FALADOR_WEST,
            Banks.GRAND_EXCHANGE, Banks.LUMBRIDGE_UPPER, Banks.VARROCK_EAST
    };

    public Area findNearestBank(boolean f2p) {
        Area[] banks;
        if(f2p) { banks = BANKS_F2P; }
        else { banks = BANKS; }
        Position my_pos = mp.myPosition();
        Area a = null, b = null;
        int dA = Integer.MAX_VALUE-1, dB = Integer.MAX_VALUE;
        for (Area bank : BANKS) {
            int dBank = mp.getMap().realDistance(bank.getRandomPosition(),my_pos);
            if(dBank < dA) {
                b = a;
                a = bank;
                dB = dA;
                dA = dBank;

            } else if(dBank < dB) {
                dB = dBank;
                b = bank;
            }
        }
        if(Math.random() < 0.15) { return b; }
        else { return a; }
    }

}
