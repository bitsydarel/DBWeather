/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.utils.glide

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * Created by darel on 21.10.17.
 *
 * Blur Transformation
 */
class BlurTransformation(context: Context) : BitmapTransformation() {

    companion object {
        private const val BITMAP_SCALE = 0.5f
        private const val BLUR_RADIUS = 10f
    }

    private val renderer = RenderScript.create(context)

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = Math.round(toTransform.width.times(BITMAP_SCALE))
        val height = Math.round(toTransform.height.times(BITMAP_SCALE))

        val inputBitmap = Bitmap.createScaledBitmap(toTransform, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        // Allocate memory for Renderscript to work with
        val engineScript = ScriptIntrinsicBlur.create(renderer, Element.U8_4(renderer))

        // Allocate memory for Renderscript to work with
        val memoryInput = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) Allocation.createFromBitmap(renderer, inputBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED) else Allocation.createFromBitmap(renderer, inputBitmap)
        val memoryOutput = Allocation.createFromBitmap(renderer, outputBitmap)

        // set the blur radius
        engineScript.setRadius(BLUR_RADIUS)

        engineScript.setInput(memoryInput)

        // Start the ScriptIntrinisicBlur
        engineScript.forEach(memoryOutput)

        // Copy the output to the blurred bitmapToTransform
        memoryOutput.copyTo(outputBitmap)

        return outputBitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest?) {
        messageDigest?.update("dbweather blur transformation".toByteArray())
    }
}