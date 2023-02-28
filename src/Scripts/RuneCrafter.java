package Scripts;

import Core.*;
import Core.API;
import Core.Api.Banking;
import Core.Api.Common.ApiScript;
import Core.Api.Common.Timing;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;
import java.util.HashMap;

public class RuneCrafter implements ApiScript {

    private Client client;
    private API api;
    private MethodProvider mp;
    private states state;
    private boolean firstRun = true;
    public Altar altar;
    public boolean runTime = false;

    static private Area AIR_ALTAR = new Area(new Position(2989, 3290, 0), new Position(2991, 3292, 0));
    static private Area FIRE_ALTAR = new Area(new Position(3308, 3250, 0), new Position(3311, 3252, 0));
    static private Area MIND_ALTAR = new Area(new Position(2979, 3511, 0), new Position(2980, 3513, 0));

    private HashMap<String, Integer> withdraw = new HashMap<String, Integer>() {{put("Rune essence", 28);}};
    private HashMap<String, Integer> tasks = new HashMap<>();

    public enum Altar {
        AIR(Banks.FALADOR_EAST, AIR_ALTAR, "Air rune", "Air tiara"),
        //        EARTH(),
//        WATER(),
        FIRE(Banks.AL_KHARID, FIRE_ALTAR, "Fire rune", "Fire tiara"),
        MIND(Banks.FALADOR_WEST, MIND_ALTAR, "Mind rune", "Mind tiara")
        ;

        private final Area bank;
        private final Area ruins;
        public final String rune;
        private final String tiara;

        Altar(Area bank, Area altar, String rune, String tiara) {
            this.bank = bank;
            this.ruins = altar;
            this.rune = rune;
            this.tiara = tiara;
        }

        public HashMap getRune() {
            return new HashMap<String, Integer>(){{put(rune, -1);}};
        }
        public HashMap getTiara() {
            return new HashMap<String, Integer>(){{put(tiara, 1);}};
        }
    }

    enum states {
        START,
        MOVE_TO_ALTAR,
        CRAFT_RUNE,
        BANK
    }

    public RuneCrafter(Client client) {
        this.client = client;
        api = client.api;
        mp = api.mp;
    }

    private void setup() {
        state();
        api.stats.track(Skill.RUNECRAFTING);
        if(state == states.MOVE_TO_ALTAR) {
            moveToAltar();
        } else if(state == states.CRAFT_RUNE) {
            craftRune();
        } else {
            if (altar.bank.contains(mp.myPlayer())) {
                if (!api.myPlayer.hasEquipped(EquipmentSlot.HAT, altar.tiara)) {
                    api.banking.bank(altar.getRune(), altar.getTiara(), Banking.methods.DEPOSIT_ALL_WITHDRAW);
                    api.banking.close();
                    api.myPlayer.equip(altar.tiara);
                }
                api.banking.bank(altar.getRune(), withdraw, Banking.methods.DEPOSIT_ALL_WITHDRAW);
                api.util.log("Move to altar");
            } else {
                if(api.util.existsOb("Portal")) {
                    api.interact.interactOb("Portal", "Use");
                    Timing.waitCondition(() -> !api.util.existsOb("Portal"), 250, 2000);
                }
                if(!api.util.existsOb("Portal")) {
                    api.myPlayer.moveTo(altar.bank);
                }
            }
        }
    }

    private void moveToAltar() {
        if(altar.ruins.contains(mp.myPlayer())) {
            api.interact.interactOb("Mysterious ruins", "Enter");
            Timing.waitCondition(() -> api.util.existsOb("Altar"), 250, 3000);
            if(api.util.existsOb("Altar")) {
                state = states.CRAFT_RUNE;
            }
        } else {
            if(api.util.existsOb("Portal")) {
                api.interact.interactOb("Portal", "Use");
                Timing.waitCondition(() -> !api.util.existsOb("Portal"), 250, 2000);
            }
            if(!api.util.existsOb("Portal")) {
                api.myPlayer.moveTo(altar.ruins);
            }
        }
    }

    private void craftRune() {
        if(api.util.existsOb("Altar")) {
            api.interact.interactOb("Altar", "Craft-rune");
            Timing.waitCondition(() -> !mp.getInventory().contains("Rune essence"), 250, 3000);
            if(!mp.getInventory().contains("Rune essence")) {
                state = states.BANK;
            }
        } else {
            state = states.MOVE_TO_ALTAR;
        }
    }

    private void bank() {
        if(altar.bank.contains(mp.myPlayer())) {
            api.logger.log(altar.rune, mp.getInventory().getAmount(altar.rune));
            api.banking.bank(null, withdraw, Banking.methods.DEPOSIT_ALL_WITHDRAW);
            if(isReady()) {
                state = states.MOVE_TO_ALTAR;
            }
        } else {
            if(api.util.existsOb("Portal")) {
                api.interact.interactOb("Portal", "Use");
                Timing.waitCondition(() -> !api.util.existsOb("Portal"), 250, 2000);
            }
            if(!api.util.existsOb("Portal")) {
                api.myPlayer.moveTo(altar.bank);
            }
        }
    }

    private boolean isReady() {
        if(mp.getInventory().contains("Rune essence") && !mp.getInventory().contains(altar.rune)) {
            return true;
        } else {
            return false;
        }
    }

    private void state() {
        if(mp.getEquipment().getItemInSlot(EquipmentSlot.HAT.slot).getName().equals(altar.tiara)) {
            if(mp.getInventory().contains("Rune essence")) {
                if(api.util.existsOb("Altar") && mp.getObjects().closest("Altar").hasAction("Enter")) {
                    state = states.CRAFT_RUNE;
                } else {
                    state = states.MOVE_TO_ALTAR;
                }
            } else if(mp.getInventory().contains(altar.rune)) {
                state = states.BANK;
            } else {
                state = states.BANK;
            }
        } else {
            state = states.START;
        }
    }

    public void paint(Graphics2D g) {
        g.drawString(api.logger.print(altar.rune), 10, 20);
        g.drawString(api.stats.print(Skill.RUNECRAFTING), 10, 40);
    }

    @Override
    public void setTask(HashMap<String, Integer> tasks) {
        this.tasks = tasks;
    }

    public boolean hasTask() {
        if(tasks !=  null && tasks.size() > 0) return true;
        else return false;
    }

    public boolean completedTask() {
        if(hasTask())
            if(api.logger.hasItems(tasks)) return true;
        return false;
    }

    @Override
    public <state> state getState() {
        return null;
    }

    private void execute() {
        api.util.log(state.name());
        switch(state) {
            case START:
                setup();
                break;
            case MOVE_TO_ALTAR:
                moveToAltar();
                break;
            case CRAFT_RUNE:
                craftRune();
                break;
            case BANK:
                bank();
                break;
        }
    }

    public void run() {
        if(firstRun) {
            setup();
            firstRun = false;
        }
        try {
            execute();
        } catch(NullPointerException e) {
            api.util.log(e.toString() + " - "+ state);
        }
    }

    public void pause() {

    }

}
