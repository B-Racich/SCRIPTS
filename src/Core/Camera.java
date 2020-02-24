package Core;

import Core.Api.Timing;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.script.MethodProvider;

import static org.osbot.rs07.script.MethodProvider.random;

public class Camera {

    private Client client;
    private MethodProvider mp;

    public Camera(Client client) {
        this.client = client;
        mp = client.osBot.getBot().getMethods();
    }

    public void moveRandom() {
        Thread thread = new Thread(() -> {
            client.api.util.log("Camera: Move Random");
            int cur_yaw = mp.getCamera().getYawAngle();
            int cur_pitch = mp.getCamera().getPitchAngle();
            int min_pitch = mp.getCamera().getLowestPitchAngle();
            int max_pitch = 67;

            int yaw = random(0,360);
            int pitch = random(min_pitch, max_pitch);
            int pitch_offset = (pitch >= cur_pitch) ? pitch-cur_pitch : cur_pitch-pitch;
            mp.getCamera().movePitch(pitch_offset);
            mp.getCamera().moveYaw(yaw);
            if(!mp.myPlayer().isVisible()) {
                mp.getCamera().toEntity(mp.myPlayer());
            }
        });
        thread.start();
    }

    public void lookAt(Entity ob) {
        Thread thread = new Thread(() -> {
            if(ob != null) {
                if(!ob.isVisible()) {
                    client.api.util.log("Camera: Move Entity : "+ob.getName());
                    mp.getCamera().toEntity(ob);
                    Timing.waitCondition(() -> ob.isVisible(), 250, 1000);
                }
            }
        });
        thread.start();
    }

}
