package com.funnywolf.hollowkit.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.NestedScrollingChild

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/3/31
 */
fun View.isUnder(rawX: Float, rawY: Float): Boolean {
    val xy = IntArray(2)
    getLocationOnScreen(xy)
    return rawX.toInt() in xy[0]..(xy[0] + width) && rawY.toInt() in xy[1]..(xy[1] + height)
}

fun ViewGroup.findChildUnder(rawX: Float, rawY: Float): View? {
    for (i in 0 until childCount) {
        val c = getChildAt(i)
        if (c.isUnder(rawX, rawY)) {
            return c
        }
    }
    return null
}

fun View.findScrollableTarget(rawX: Float, rawY: Float, dScrollY: Int): View? {
    return when {
        !isUnder(rawX, rawY) -> null
        canScrollVertically(dScrollY) -> this
        this !is ViewGroup -> null
        else -> {
            var t: View? = null
            for (i in 0 until childCount) {
                t = getChildAt(i).findScrollableTarget(rawX, rawY, dScrollY)
                if (t != null) {
                    break
                }
            }
            t
        }
    }
}

fun ViewGroup.findHorizontalNestedScrollingTarget(rawX: Float, rawY: Float): View? {
    for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (!v.isUnder(rawX, rawY)) {
            continue
        }
        if (v is NestedScrollingChild
            && (v.canScrollHorizontally(1)
                    || v.canScrollHorizontally(-1))) {
            return v
        }
        if (v !is ViewGroup) {
            continue
        }
        val t = v.findHorizontalNestedScrollingTarget(rawX, rawY)
        if (t != null) {
            return t
        }
    }
    return null
}

fun ViewGroup.findVerticalNestedScrollingTarget(rawX: Float, rawY: Float): View? {
    for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (!v.isUnder(rawX, rawY)) {
            continue
        }
        if (v is NestedScrollingChild
            && (v.canScrollVertically(1) || v.canScrollVertically(-1))) {
            return v
        }
        if (v !is ViewGroup) {
            continue
        }
        val t = v.findVerticalNestedScrollingTarget(rawX, rawY)
        if (t != null) {
            return t
        }
    }
    return null
}

fun ViewGroup.containsChild(v: View?): Boolean {
    v ?: return false
    return if (indexOfChild(v) >= 0) {
        true
    } else {
        repeat(childCount) {
            val child = getChildAt(it)
            if (child is ViewGroup && child.containsChild(v)) {
                return true
            }
        }
        false
    }
}