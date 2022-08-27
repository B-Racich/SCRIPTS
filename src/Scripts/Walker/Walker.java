package Scripts.Walker;

import Core.API;
import Core.Api.Common.ApiScript;
import Core.Client;
import jdk.jfr.events.ExceptionThrownEvent;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;

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

public class Walker implements ApiScript {

    public final static String scriptName = "Walker";

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

    public Walker(Client client) {
        this.client = client;
        api = client.api;
    }

    enum state {}

    private int[] combat_stats = new int[7];
    private int cmb_lvl;

    private void getStats() {
    }

    public void showGUI() {
        if(!isGUIOpen) {
            try {
                map_img = ImageIO.read(client.osbot.getScriptResourceAsStream("resources/osrs_world_map.png"));
                map_icon = new ImageIcon(map_img);
                label = new JLabel(map_icon);
                client.log("SCRIPT: showGUI");
                map_frame.add(label);
                //  map_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                map_frame.pack();
                map_frame.setVisible(true);
                isGUIOpen = true;
            } catch (IOException e) {
                client.log("SCRIPT: showGUI Exception: ");
                client.log(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            if (!isGUIOpen || !hasLocation) {
                showGUI();
            } else {

            }
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
