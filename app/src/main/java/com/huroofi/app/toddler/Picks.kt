package com.huroofi.app.toddler

import kotlin.random.Random

/** A random item that is not [except], so nothing repeats twice in a row (plan 05 decisions 3, 5, 13). */
fun <T> pickExcept(items: List<T>, except: T?, random: Random): T {
    require(items.isNotEmpty()) { "nothing to pick from" }
    val pool = items.filter { it != except }.ifEmpty { items }
    return pool[random.nextInt(pool.size)]
}
