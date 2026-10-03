package com.huroofi.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.data.progress.DEFAULT_LIMIT_MINUTES
import com.huroofi.app.debug.DEBUG_GALLERY_ROUTE
import com.huroofi.app.debug.debugDestinations
import com.huroofi.app.gate.ParentGateScreen
import com.huroofi.app.learn.HomeRoute
import com.huroofi.app.learn.LearnBackHandler
import com.huroofi.app.learn.LearnRoutes
import com.huroofi.app.learn.LessonRoute
import com.huroofi.app.learn.TraceRoute
import com.huroofi.app.learn.NavTab
import com.huroofi.app.learn.PathStep
import com.huroofi.app.parent.ChildRoutes
import com.huroofi.app.parent.ParentZoneRoute
import com.huroofi.app.parent.RestMove
import com.huroofi.app.parent.RestRoute
import com.huroofi.app.parent.UsageMeter
import com.huroofi.app.parent.limitReached
import com.huroofi.app.parent.restMove
import com.huroofi.app.toddler.ToddlerBackHandler
import com.huroofi.app.toddler.ToddlerHomeRoute
import com.huroofi.app.toddler.cards.LookListenScreen
import com.huroofi.app.toddler.find.FindScreen
import com.huroofi.app.toddler.paint.PaintScreen
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.LocalHuroofiColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object Routes {
    const val Gate = "gate"
    const val ParentZone = "parent_zone"
    const val ToddlerHome = "toddler_home"
    const val ToddlerCards = "toddler_cards"
    const val ToddlerFind = "toddler_find"
    const val ToddlerPaint = "toddler_paint"
    const val Rest = "rest"
}

/** Home screen for the stored mode. Read once per app start (plan 05 decision 12: no live switch). */
fun startRoute(mode: AgeMode): String = when (mode) {
    AgeMode.TODDLER -> Routes.ToddlerHome
    AgeMode.PRESCHOOL, AgeMode.READER -> LearnRoutes.Home
}

@Composable
fun HuroofiApp(onCloseApp: () -> Unit = {}) {
    HuroofiTheme {
        val progress = LocalAppContainer.current.progress
        var mode by rememberSaveable { mutableStateOf<AgeMode?>(null) }
        LaunchedEffect(Unit) {
            if (mode == null) mode = progress.mode.first()
        }
        when (val start = mode) {
            null -> Box(Modifier.fillMaxSize().background(LocalHuroofiColors.current.sky))
            else -> HuroofiNavHost(startRoute(start), onCloseApp)
        }
    }
}

@Composable
private fun HuroofiNavHost(startRoute: String, onCloseApp: () -> Unit) {
    val nav = rememberNavController()
    val toHome: () -> Unit = { nav.popBackStack(Routes.ToddlerHome, inclusive = false) }
    // The Parent zone always returns to this session's home (plan 06 decision 8).
    val toStart: () -> Unit = { if (!nav.popBackStack(startRoute, inclusive = false)) nav.navigate(startRoute) }
    // From a toddler screen the gate replaces it, so Back on the gate lands on Toddler Home (decision 6).
    val toddlerToGate: () -> Unit = { nav.navigate(Routes.Gate) { popUpTo(Routes.ToddlerHome) } }
    PlayTimeKeeper(nav, startRoute)
    NavHost(navController = nav, startDestination = startRoute) {
        learnDestinations(nav)
        composable(Routes.ToddlerHome) {
            ToddlerBackHandler(Routes.ToddlerHome, startRoute, onPopToToddlerHome = toHome)
            ToddlerHomeRoute(onOpenActivity = { nav.navigate(it.route) }, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerCards) {
            ToddlerBackHandler(Routes.ToddlerCards, startRoute, onPopToToddlerHome = toHome)
            LookListenScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerFind) {
            ToddlerBackHandler(Routes.ToddlerFind, startRoute, onPopToToddlerHome = toHome)
            FindScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerPaint) {
            ToddlerBackHandler(Routes.ToddlerPaint, startRoute, onPopToToddlerHome = toHome)
            PaintScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.Rest) {
            ToddlerBackHandler(Routes.Rest, startRoute, onPopToToddlerHome = toHome)
            RestRoute(onRequestParentZone = { nav.navigate(Routes.Gate) { popUpTo(startRoute) } })
        }
        composable(Routes.Gate) {
            ToddlerBackHandler(Routes.Gate, startRoute, onPopToToddlerHome = toHome)
            ParentGateScreen(
                onOpenParentZone = { nav.navigate(Routes.ParentZone) { popUpTo(Routes.Gate) { inclusive = true } } },
                onCloseApp = onCloseApp,
            )
        }
        composable(Routes.ParentZone) {
            ParentZoneRoute(onBack = toStart) {
                DEBUG_GALLERY_ROUTE?.let { route -> PrimaryButton("Component gallery (debug)", onClick = { nav.navigate(route) }) }
            }
        }
        debugDestinations(onRequestParentZone = { nav.navigate(Routes.Gate) })
    }
}

/**
 * Daily play time, in one place (plan 06 decision 6): counts seconds while a child screen is shown
 * and the app is resumed, saves every 10 s and on pause, and swaps child screens for Rest at the limit.
 */
@Composable
private fun PlayTimeKeeper(nav: NavHostController, startRoute: String) {
    val progress = LocalAppContainer.current.progress
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val stored by progress.usageSecondsToday.collectAsStateWithLifecycle(0)
    val limit by progress.dailyLimitMinutes.collectAsStateWithLifecycle(DEFAULT_LIMIT_MINUTES)
    val meter = remember { UsageMeter() }
    var pending by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val counting = route in ChildRoutes
    LifecycleResumeEffect(counting) {
        val ticking = if (counting) {
            scope.launch {
                while (true) {
                    delay(1_000)
                    val save = meter.tick()
                    pending = meter.pending
                    if (save != null) progress.addUsageSeconds(save)
                }
            }
        } else {
            null
        }
        onPauseOrDispose {
            ticking?.cancel()
            val rest = meter.drain()
            pending = 0
            if (rest > 0) scope.launch { progress.addUsageSeconds(rest) }
        }
    }
    val reached = limitReached(stored, pending, limit)
    LaunchedEffect(route, reached) {
        when (restMove(route, reached)) {
            RestMove.ToRest -> nav.navigate(Routes.Rest) {
                popUpTo(startRoute)
                launchSingleTop = true
            }
            RestMove.LeaveRest -> nav.popBackStack(startRoute, inclusive = false)
            RestMove.None -> Unit
        }
    }
}

/** Preschool and Early reader screens (plan 07). Every learn screen opens on top of Home. */
private fun NavGraphBuilder.learnDestinations(nav: NavHostController) {
    val overHome: NavOptionsBuilder.() -> Unit = { popUpTo(LearnRoutes.Home) }
    val toGate: () -> Unit = { nav.navigate(Routes.Gate) }
    val toHome: () -> Unit = { nav.popBackStack(LearnRoutes.Home, inclusive = false) }
    val indexArg = listOf(navArgument(LearnRoutes.ARG_INDEX) { type = NavType.IntType })
    composable(LearnRoutes.Home) {
        LearnBackHandler(LearnRoutes.Home, onToGate = toGate)
        HomeRoute(
            steps = listOf(PathStep.MEET, PathStep.TRACE),
            navTabs = listOf(NavTab.HOME, NavTab.PARENTS),
            onStep = { step, index ->
                when (step) {
                    PathStep.MEET -> nav.navigate(LearnRoutes.lesson(index), overHome)
                    PathStep.TRACE -> nav.navigate(LearnRoutes.trace(index), overHome)
                    PathStep.PLAY -> Unit
                }
            },
            onMap = null,
            onTab = { tab -> if (tab == NavTab.PARENTS) toGate() },
        )
    }
    composable(LearnRoutes.Lesson, arguments = indexArg) { entry ->
        val index = entry.arguments?.getInt(LearnRoutes.ARG_INDEX) ?: return@composable
        LessonRoute(index, onBack = toHome, onNext = { nav.navigate(LearnRoutes.trace(index), overHome) })
    }
    composable(LearnRoutes.Trace, arguments = indexArg) { entry ->
        val index = entry.arguments?.getInt(LearnRoutes.ARG_INDEX) ?: return@composable
        val toLesson = { nav.navigate(LearnRoutes.lesson(index), overHome) }
        LearnBackHandler(LearnRoutes.Trace, onToLesson = toLesson)
        // Play arrives in slice 3; until then a finished trace goes back to Home.
        TraceRoute(index, onBack = toLesson, onDone = toHome)
    }
}
