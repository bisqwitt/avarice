package com.avaricious;

import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class TaskScheduler {

    private final List<Runnable> tasks = new ArrayList<>();
    private final float delayBetweenTasks;

    public TaskScheduler(float delayBetweenTasks) {
        this.delayBetweenTasks = delayBetweenTasks;
    }

    public void schedule(Runnable r) {
        tasks.add(r);
    }

    public void runTasks() {
        float delay = delayBetweenTasks;
        for(Timer.Task timerTask : tasks.stream().map(this::create).toList()) {
            Timer.schedule(timerTask, delay);
            delay += delayBetweenTasks;
        }
    }

    private Timer.Task create(Runnable r) {
        return new Timer.Task() {
            @Override
            public void run() {
                r.run();
            }
        };
    }
}
