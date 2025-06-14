package ru.smak.copier.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.smak.copier.viewmodels.ClientViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(
    viewModel: ClientViewModel = viewModel<ClientViewModel>(),
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier){
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit){
                    detectDragGestures(
                        onDragStart = { pos ->
                            viewModel.addPos(pos)
                        },
                        onDragEnd = {
                            viewModel.clearPos()
                        },
                        onDrag = { pos ->
                            viewModel.appendPos(pos)
                        },
                        onDragCancel = {
                            viewModel.clearPos()
                        }
                    )
                }
        ){
            val pts = viewModel.points.toList()
            pts.forEachIndexed{ index, point ->
                if (index > 0) {
                    point.first?.let { p2 ->
                        viewModel.points[index - 1].first?.let { p1 ->
                            drawLine(
                                point.second,
                                p1,
                                p2
                            )
                        }
                    }
                }
            }
        }
    }
}