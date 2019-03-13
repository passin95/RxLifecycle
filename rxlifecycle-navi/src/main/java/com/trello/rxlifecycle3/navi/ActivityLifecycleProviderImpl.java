/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle3.navi;

import static com.trello.rxlifecycle3.navi.NaviLifecycleMaps.ACTIVITY_EVENT_FILTER;
import static com.trello.rxlifecycle3.navi.NaviLifecycleMaps.ACTIVITY_EVENT_MAP;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.rx.RxNavi;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

final class ActivityLifecycleProviderImpl implements LifecycleProvider<ActivityEvent> {
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    public ActivityLifecycleProviderImpl(final NaviComponent activity) {
        if (!activity.handlesEvents(Event.CREATE, Event.START, Event.RESUME, Event.PAUSE, Event.STOP, Event.DESTROY)) {
            throw new IllegalArgumentException("NaviComponent does not handle all required events");
        }

        RxNavi.observe(activity, Event.ALL)
            .filter(ACTIVITY_EVENT_FILTER)
            .map(ACTIVITY_EVENT_MAP)
            .subscribe(lifecycleSubject);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent... events) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, events);
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }
}
