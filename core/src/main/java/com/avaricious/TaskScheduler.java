package com.avaricious;

import com.badlogic.gdx.utils.Timer;

import java.util.*;

public class TaskScheduler {

//    private final Map<Runnable, Float> tasks = new LinkedHashMap<>();
    private final LinkedList<ScheduledTask> tasks = new LinkedList<>();
    private final float defaultDelay;

    public TaskScheduler(float defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public void schedule(Runnable r) {
        schedule(r, defaultDelay);
    }

    public void schedule(Runnable r, float delay) {
        tasks.add(new ScheduledTask(r, delay));
    }

    public void scheduleImmediate(Runnable r) {
        float delay = tasks.getLast().delay();
//        tasks.getLast()
    }

    public void runTasks() {
        float delay = defaultDelay;
        for(ScheduledTask task : tasks) {
            Timer.schedule(create(task.runnable), delay);
            delay += task.delay;
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

    private record ScheduledTask(Runnable runnable, float delay) {}
}
