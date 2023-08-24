package com.grayseal.traveldiaryapp.ui.main.eventbus

/**
 * Event class used to indicate whether a search result is empty or not.
 *
 * @param isEmpty A boolean value indicating whether the search result is empty (true) or not (false).
 */
class SearchResultEvent(var isEmpty: Boolean)