package net.edara.edaracash

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun Fragment.navigateSafely(directions: NavDirections) {
    try {
        with(findNavController()) {
            currentDestination?.getAction(directions.actionId)?.destinationId ?: return
            navigate(directions)
        }
    } catch (e: Exception) {
        return
    }
}
fun Fragment.navigateSafely(@IdRes direction: Int) {
    try {
        with(findNavController()) {
            if (currentDestination?.id == direction) return
            navigate(direction)
        }
    } catch (e: Exception) {
        return
    }
}