package com.test.bighand.ui;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * An Event Bus pattern to post and observe event. The implementation is based on RxAndroid.
 *
 * @author Usman
 */
public class EventBus {


    private static EventBus thisInstance;
    private final Subject<Object, Object> mEventBus = new SerializedSubject<>(PublishSubject.create());


    private EventBus() {
    }

    public static EventBus get() {
        if (thisInstance == null) {
            thisInstance = new EventBus();
        }
        return thisInstance;
    }

    public void postEvent(Object o) {
        mEventBus.onNext(o);
    }

    public Observable<Object> getObservable() {
        return mEventBus;
    }


}
