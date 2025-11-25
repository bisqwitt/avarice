package com.avaricious;

import com.badlogic.gdx.utils.Timer;

public class TaskFactory {

    public static Timer.Task create(Runnable r) {
        return new Timer.Task() {
            @Override
            public void run() {
                r.run();
            }
        };
    }

}
