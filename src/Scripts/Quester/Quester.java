package Scripts.Quester;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

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

    private Client client;
    private API api;

    private long timeBegan;
    private long timeRan;

    private JFrame map_frame = new JFrame();
    private String img_str;
    private Image map_img;
    private ImageIcon map_icon;
    private JLabel label;

    private boolean isGUIOpen = false;
    private boolean hasLocation = false;

    public Quester(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    private int[] combat_stats = new int[7];
    private int cmb_lvl;

    private void getStats() {
    }



    @Override
    public void run() {
        try {

        } catch (Exception e) {
            client.log("SCRIPT: Exception: ");
            client.log(e.getMessage());
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
    public void setTask(HashMap<String, Integer> tasks) {

    }

    @Override
    public boolean hasTask() {
        return false;
    }

    @Override
    public boolean completedTask() {
        return false;
    }

    @Override
    public state getState() {
        return null;
    }
}
