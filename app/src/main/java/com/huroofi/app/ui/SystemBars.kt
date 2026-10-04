package com.huroofi.app.ui

import com.huroofi.app.Routes

/**
 * Every screen a child can reach hides the status and navigation bars; only the Parent zone shows
 * them (plan 08 decision 7). Null (nothing shown yet) counts as a child screen.
 */
fun barsHidden(route: String?): Boolean = route != Routes.ParentZone
