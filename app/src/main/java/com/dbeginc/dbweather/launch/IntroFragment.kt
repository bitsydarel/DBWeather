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

package com.dbeginc.dbweather.launch


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.FragmentIntroBinding
import com.dbeginc.dbweather.utils.utility.goToChooseLocationScreen
import com.dbeginc.dbweather.utils.utility.goToGpsLocationFinder


/**
 * A simple [Fragment] subclass.
 *
 */
class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_intro,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseLocationBtn.setOnClickListener {
            activity?.let {
                goToChooseLocationScreen(container = it, layoutId = R.id.launchContent)
            }
        }

        binding.useGpsLocationBtn.setOnClickListener {
            activity?.let {
                goToGpsLocationFinder(container = it, layoutId = R.id.launchContent)
            }
        }
    }


}
