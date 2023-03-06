package Scripts.Quester;

import Core.API;
import Core.Api.Common.Interfaces.ApiScript;
import Scripts.Quester.Quests.F2P.*;

import java.awt.*;

/**
 * The basic script template
 *
 * state - can be used to setup a state based script, otherwise create a regular onLoop script within the run() method
 *
 * pause() - if setup can be used by other classes to pause the script when needed
 *
 * if tasks are supported complete the task methods
 */

public class Quester implements ApiScript {

    public final static String scriptName = "Quester";

    private API api;

    private CooksAssistant cooksAssistant;
    private WitchesPotion witchesPotion;
    private SheepShearer sheepShearer;
    private RomeoAndJuliet romeoAndJuliet;
    private RuneMysteries runeMysteries;
    private ImpCatcher impCatcher;
    private GoblinDiplomacy goblinDiplomacy;
    private RestlessGhost restlessGhost;
    private ErnestTheChicken ernestTheChicken;

    private API.ScriptState scriptState;

    public Quester(API api) {
        this.api = api;

        cooksAssistant = new CooksAssistant(api);
        witchesPotion = new WitchesPotion(api);
        sheepShearer = new SheepShearer(api);
        romeoAndJuliet = new RomeoAndJuliet(api);
        runeMysteries = new RuneMysteries(api);
        impCatcher = new ImpCatcher(api);
        goblinDiplomacy = new GoblinDiplomacy(api);
        restlessGhost = new RestlessGhost(api);
        ernestTheChicken = new ErnestTheChicken(api);

        api.antiban.idleFight = true;
    }

    enum state {}

    @Override
    public void run() {
        try {
            if(!cooksAssistant.isCompleted()) cooksAssistant.run();
            if(!witchesPotion.isCompleted()) witchesPotion.run();
            if(!sheepShearer.isCompleted()) sheepShearer.run();
            if(!romeoAndJuliet.isCompleted()) romeoAndJuliet.run();
            if(!runeMysteries.isCompleted()) runeMysteries.run();
            if(!impCatcher.isCompleted()) impCatcher.run();
            if(!goblinDiplomacy.isCompleted()) goblinDiplomacy.run();
            if(!restlessGhost.isCompleted()) restlessGhost.run();
            if(!ernestTheChicken.isCompleted()) ernestTheChicken.run();
        } catch (Exception e) {
            api.log("SCRIPT: Exception: ");
            api.log(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void paint(Graphics2D g) {

    }

    @Override
    public state getState() {
        return null;
    }
}
