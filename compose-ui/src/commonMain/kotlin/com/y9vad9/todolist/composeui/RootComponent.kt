package com.y9vad9.todolist.composeui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.childPanels
import com.y9vad9.ktiny.kotlidator.createOrThrow
import com.y9vad9.todolist.composeui.navigation.DetailScreen
import com.y9vad9.todolist.composeui.navigation.MainScreen
import com.y9vad9.todolist.domain.type.value.TaskId
import com.y9vad9.todolist.presentation.mvi.create.CreateTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.edit.EditTaskMVIComponent
import com.y9vad9.todolist.presentation.mvi.important.ImportantTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.list.ListTasksMVIComponent
import com.y9vad9.todolist.presentation.mvi.settings.SettingsMVIComponent
import com.y9vad9.todolist.presentation.mvi.view.ViewTaskMVIComponent
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

class RootComponent(
    componentContext: ComponentContext,
    koin: Koin,
) : ComponentContext by componentContext {
    @OptIn(ExperimentalDecomposeApi::class)
    val nav = PanelsNavigation<MainScreen, DetailScreen, Nothing>()

    @OptIn(ExperimentalDecomposeApi::class)
    val panels = childPanels(
        source = nav,
        serializers = MainScreen.serializer() to DetailScreen.serializer(),
        initialPanels = { Panels(main = MainScreen.Important) },
        handleBackButton = true,
        mainFactory = { screen, ctx ->
            when (screen) {
                MainScreen.AllTasks -> koin.get<ListTasksMVIComponent> {
                    parametersOf(ctx.childContext("tasks"))
                }

                MainScreen.Important -> koin.get<ImportantTasksMVIComponent> {
                    parametersOf(ctx.childContext("important"))
                }

                MainScreen.Settings -> koin.get<SettingsMVIComponent> {
                    parametersOf(ctx.childContext("settings"))
                }
            }
        },
        detailsFactory = { screen, ctx ->
            when (screen) {
                DetailScreen.AddTask -> koin.get<CreateTaskMVIComponent> {
                    parametersOf(ctx.childContext("createTask"))
                }

                is DetailScreen.EditTask -> koin.get<EditTaskMVIComponent> {
                    parametersOf(
                        ctx.childContext("editTask/${screen.taskId}"),
                        TaskId.factory.createOrThrow(screen.taskId),
                    )
                }

                is DetailScreen.ViewTask -> koin.get<ViewTaskMVIComponent> {
                    parametersOf(
                        ctx.childContext("viewTask/${screen.taskId}"),
                        TaskId.factory.createOrThrow(screen.taskId)
                    )
                }
            }
        },
    )
}

interface DetailComponent
interface MainComponent