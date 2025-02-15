package com.rafalskrzypczyk.core.extensions

import com.rafalskrzypczyk.core.base.Identifiable

/**
 * Updates an item in the list by its unique identifier (`id`).
 * If an item with the same `id` exists, it gets replaced with the new value.
 *
 * @param value The new item to insert, replacing the existing one with the same `id`.
 * @param T The type of items in the list, which must implement `Identifiable`.
 */
fun <T> MutableList<T>.updateById(value: T) where T : Identifiable {
    indexOfFirst { it.id == value.id }
        .takeIf { it != -1 }
        ?.let { this[it] = value }
}