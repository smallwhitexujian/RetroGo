package com.future.retronet.thread;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public enum  ThreadStrategy {
    IO(Schedulers.io()),
    SINGLE(Schedulers.single()),
    COMPUTATION(Schedulers.computation()),
    TRAMPOLINE(Schedulers.trampoline()),
    NEW_THREAD(Schedulers.newThread()),
    MAIN(AndroidSchedulers.mainThread());

    private Scheduler mScheduler;

    ThreadStrategy(Scheduler scheduler) {
        this.mScheduler = scheduler;
    }

    public Scheduler cheduler() {
        return mScheduler;
    }
}
