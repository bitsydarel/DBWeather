/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweathercommon

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 22.03.18.
 * Base View Model from my MVPV (Model ViewModel Presenter View) Architecture
 */
abstract class BaseViewModel : ViewModel() {
    protected abstract val subscriptions: CompositeDisposable
    protected abstract val requestState: MutableLiveData<RequestState>

    /**
     * Request state event
     * This method help me
     * to subscribe to the status
     * of any request made by viewModel
     * And also avoid memory leak :))
     */
    fun getRequestState(): LiveData<RequestState> = requestState

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}