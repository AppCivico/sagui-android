package com.eokoe.sagui.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

/**
 * @author Pedro Silva
 * @since 17/08/17
 */
object UnitUtils {
    fun dp2px(resources: Resources, dp: Float) =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

    fun dp2px(context: Context, dp: Float) = dp2px(context.resources, dp)

    fun sp2px(resources: Resources, sp: Float) =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

    fun sp2px(context: Context, sp: Float) = sp2px(context.resources, sp)
}