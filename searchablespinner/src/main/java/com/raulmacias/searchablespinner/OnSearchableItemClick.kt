package com.raulmacias.searchablespinner

import java.io.Serializable

interface OnSearchableItemClick<T>: Serializable{
    fun onSearchableItemClicked(item: T?, position: Int)
}