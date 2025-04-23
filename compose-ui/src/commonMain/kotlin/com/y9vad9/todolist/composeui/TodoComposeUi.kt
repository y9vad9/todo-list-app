@file:OptIn(ExperimentalDecomposeApi::class)

package com.y9vad9.todolist.composeui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanels
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsAnimators
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.slide
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.activateDetails
import com.arkivanov.decompose.router.panels.activateMain
import com.arkivanov.decompose.router.panels.dismissDetails
import com.arkivanov.decompose.router.panels.setMode
import com.y9vad9.todolist.composeui.localization.LocalStrings
import com.y9vad9.todolist.composeui.navigation.DetailScreen
import com.y9vad9.todolist.composeui.navigation.DetailScreen.AddTask
import com.y9vad9.todolist.composeui.navigation.DetailScreen.EditTask
import com.y9vad9.todolist.composeui.navigation.DetailScreen.ViewTask
import com.y9vad9.todolist.composeui.navigation.LocalComponentContext
import com.y9vad9.todolist.composeui.navigation.MainScreen
import com.y9vad9.todolist.composeui.navigation.MainScreen.AllTasks
import com.y9vad9.todolist.composeui.navigation.MainScreen.Important
import com.y9vad9.todolist.composeui.navigation.MainScreen.Settings
import com.y9vad9.todolist.composeui.screen.CreateTaskScreenComponent
import com.y9vad9.todolist.composeui.screen.EditTaskScreenComponent
import com.y9vad9.todolist.composeui.screen.ImportantTasksComponent
import com.y9vad9.todolist.composeui.screen.ListTasksScreenComponent
import com.y9vad9.todolist.composeui.screen.SettingsScreenComponent
import com.y9vad9.todolist.composeui.screen.ViewTaskScreenComponent
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.important.ImportantTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIComponent
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIComponent
import org.koin.compose.LocalKoinApplication

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TodoComposeUi(windowSize: WindowSizeClass) {
    val componentCtx = LocalComponentContext.current
    val koin = LocalKoinApplication.current

    val mode =
        if (windowSize.widthSizeClass < WindowWidthSizeClass.Expanded) ChildPanelsMode.SINGLE else ChildPanelsMode.DUAL
    val rootComponent = remember { RootComponent(componentCtx, koin) }

    DisposableEffect(mode) {
        rootComponent.nav.setMode(mode)
        onDispose {}
    }

    var selectedTask: TaskId? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ChildPanels(
            panels = rootComponent.panels,
            mainChild = { child ->
                CompositionLocalProvider(LocalSelectedTaskId provides selectedTask) {
                    val block = @Composable {
                        MainContent(
                            component = child.instance as ComponentContext,
                            screen = child.configuration,
                            onTaskClicked = {
                                selectedTask = it
                                rootComponent.nav.activateDetails(ViewTask(it.int))
                            },
                        )
                    }

                    if (windowSize.widthSizeClass <= WindowWidthSizeClass.Compact) {
                        CompactLayout(
                            navigation = rootComponent.nav,
                            screen = child.configuration,
                        ) {
                            block()
                        }
                    } else if (windowSize.widthSizeClass < WindowWidthSizeClass.Expanded) {
                        MediumLayout(
                            navigation = rootComponent.nav,
                            screen = child.configuration,
                        ) {
                            block()
                        }
                    } else if (windowSize.widthSizeClass >= WindowWidthSizeClass.Expanded) {
                        LargeLayout(
                            navigation = rootComponent.nav,
                            screen = child.configuration,
                        ) {
                            block()
                        }
                    }
                }
            },
            detailsChild = {
                DetailsContent(
                    it.configuration,
                    it.instance as ComponentContext,
                    navigation = rootComponent.nav,
                    onTaskDeselected = { selectedTask = null }
                )
            },
            animators = ChildPanelsAnimators(
                single = fade(),
                dual = null to fade()
            ),
        )
    }
}

val LocalSelectedTaskId = compositionLocalOf<TaskId?> { null }

@OptIn(DelicateDecomposeApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CompactLayout(
    navigation: PanelsNavigation<MainScreen, DetailScreen, Nothing>,
    screen: MainScreen,
    block: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (screen) {
                            AllTasks -> LocalStrings.current.allTasksTitle
                            Important -> LocalStrings.current.importantTitle
                            Settings -> LocalStrings.current.settingsTitle
                        }
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen is Important,
                    onClick = { navigation.activateMain(Important) },
                    icon = {
                        Icon(
                            imageVector = if (screen is Important)
                                Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(LocalStrings.current.importantTitle)
                    }
                )

                NavigationBarItem(
                    selected = screen is AllTasks,
                    onClick = { navigation.activateMain(AllTasks) },
                    icon = {
                        Icon(
                            imageVector = if (screen is AllTasks)
                                Icons.AutoMirrored.Filled.ListAlt else Icons.AutoMirrored.Outlined.ListAlt,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(LocalStrings.current.allTasksTitle)
                    }
                )

                NavigationBarItem(
                    selected = screen is Settings,
                    onClick = { navigation.activateMain(Settings) },
                    icon = {
                        Icon(
                            imageVector = if (screen is Settings)
                                Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(LocalStrings.current.settingsTitle)
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigation.activateDetails(AddTask)
                },
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            block()
        }
    }
}


@Composable
private fun MainContent(
    component: ComponentContext,
    screen: MainScreen,
    onTaskClicked: (TaskId) -> Unit,
) {
    when (screen) {
        AllTasks -> {
            ListTasksScreenComponent(
                container = component as ListTasksMVIComponent,
                onTaskClicked = onTaskClicked,
            )
        }

        Important -> {
            ImportantTasksComponent(
                mvi = component as ImportantTasksMVIComponent,
                onTaskClick = onTaskClicked,
            )
        }

        Settings -> {
            SettingsScreenComponent(
                container = component as SettingsMVIComponent
            )
        }
    }
}

@OptIn(DelicateDecomposeApi::class)
@Composable
private fun MediumLayout(
    navigation: PanelsNavigation<MainScreen, DetailScreen, Nothing>,
    screen: MainScreen,
    block: @Composable BoxScope.() -> Unit,
) {
    Row(Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            header = {
                FloatingActionButton(
                    onClick = {
                        navigation.activateDetails(AddTask)
                    },
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            },
        ) {
            NavigationRailItem(
                selected = screen is Important,
                onClick = { navigation.activateMain(Important) },
                icon = {
                    Icon(
                        imageVector = if (screen is Important)
                            Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                    )
                }
            )
            NavigationRailItem(
                selected = screen is AllTasks,
                onClick = { navigation.activateMain(AllTasks) },
                icon = {
                    Icon(
                        imageVector = if (screen is AllTasks)
                            Icons.AutoMirrored.Filled.ListAlt else Icons.AutoMirrored.Outlined.ListAlt,
                        contentDescription = null,
                    )
                }
            )
            NavigationRailItem(
                selected = screen is Settings,
                onClick = { navigation.activateMain(Settings) },
                icon = {
                    Icon(
                        imageVector = if (screen is Settings)
                            Icons.Filled.Settings else Icons.Outlined.Settings,
                        contentDescription = null,
                    )
                }
            )
        }

        Box(Modifier.weight(1f)) {
            block()
        }
    }
}

@Composable
private fun LargeLayout(
    navigation: PanelsNavigation<MainScreen, DetailScreen, Nothing>,
    screen: MainScreen,
    block: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigation.activateDetails(AddTask)
                },
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Box(Modifier.fillMaxSize().padding(it)) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(modifier = Modifier.width(240.dp)) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = LocalStrings.current.appName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = if (screen is Important)
                                            Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(text = LocalStrings.current.importantTitle) },
                                selected = screen is Important,
                                onClick = {
                                    navigation.activateMain(Important)
                                }
                            )
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = if (screen is AllTasks)
                                            Icons.AutoMirrored.Filled.ListAlt else Icons.AutoMirrored.Outlined.ListAlt,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(text = LocalStrings.current.allTasksTitle) },
                                selected = screen is AllTasks,
                                onClick = {
                                    navigation.activateMain(AllTasks)
                                }
                            )
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = if (screen is Settings)
                                            Icons.Filled.Settings else Icons.Outlined.Settings,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(text = LocalStrings.current.settingsTitle) },
                                selected = screen is Settings,
                                onClick = {
                                    navigation.activateMain(Settings)
                                }
                            )
                        }
                    }
                },
            ) {
                block()
            }
        }
    }
}

@Composable
private fun DetailsContent(
    screen: DetailScreen,
    component: ComponentContext,
    navigation: PanelsNavigation<MainScreen, DetailScreen, Nothing>,
    onTaskDeselected: () -> Unit,
) {
    when (screen) {
        AddTask -> CreateTaskScreenComponent(
            mvi = component as CreateTaskMVIComponent,
            onBack = {
                navigation.dismissDetails()
                onTaskDeselected()
            },
        )

        is EditTask -> EditTaskScreenComponent(
            mvi = component as EditTaskMVIComponent,
            onBack = { isDeleted ->
                if (isDeleted) {
                    navigation.dismissDetails()
                    onTaskDeselected()
                } else navigation.activateDetails(ViewTask(screen.taskId))
            }
        )

        is ViewTask -> ViewTaskScreenComponent(
            container = component as ViewTaskMVIComponent,
            onBack = {
                navigation.dismissDetails()
                onTaskDeselected()
            },
            onEdit = { navigation.activateDetails(EditTask(screen.taskId)) }
        )
    }
}
