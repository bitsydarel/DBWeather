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

package com.dbeginc.dbweatherdomain

import io.reactivex.Scheduler

/**
 * Created by darel on 22.03.18.
 *
 * Thread Provider for the system
 */
interface ThreadProvider {
    /**
     * Input / Output Thread
     * This thread is used for network related task
     */
    val IO: Scheduler

    /**
     * UserProfile Interface Thread or Main Thread
     * This thread is used for user interface related task
     */
    val UI: Scheduler

    /**
     * Computation Thread
     * This thread is used for local but intense related task
     */
    val CP: Scheduler
}