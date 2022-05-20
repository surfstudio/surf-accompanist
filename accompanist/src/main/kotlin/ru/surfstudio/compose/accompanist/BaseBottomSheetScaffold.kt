/*
 * Copyright 2021 Surf LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.surfstudio.compose.accompanist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.MutableStateFlow
import ru.surfstudio.compose.accompanist.BottomSheetScaffoldActivity.Companion.DEFAULT_BACKGROUND_COLOR
import ru.surfstudio.compose.accompanist.BottomSheetScaffoldActivity.Companion.DEFAULT_TITLE_SHAPE

/**
 * State for driving open/close
 */
private val state: MutableStateFlow<Boolean> = MutableStateFlow(true)

/**
 * Bottom sheet scaffold for app with auto show/hide and resize
 *
 * @author Vitaliy Zarubin
 */
@Deprecated("Remains as an idea. Use `androidx.compose.material.BottomSheetScaffold` instead")
@Composable
fun BaseBottomSheetScaffold(
    isShow: Boolean,
    navigationBarColor: Color = Color.White,
    systemBarsColor: Color = Color.Transparent,
    backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
    titleShape: Shape = DEFAULT_TITLE_SHAPE,
    onClose: () -> Unit = {},
    titleContent: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {

    val context = LocalContext.current
    val stateAction = state.collectAsState()

    LaunchedEffect(isShow) {
        state.value = isShow
        if (isShow) {
            BottomSheetScaffoldActivity.setActivityData(
                systemBarsColor = systemBarsColor,
                titleContent = titleContent,
                composeContent = content,
                navigationBarColor = navigationBarColor,
                backgroundColor = backgroundColor,
                titleShape = titleShape
            )
            context.startActivity(Intent(context, BottomSheetScaffoldActivity::class.java))
        }
    }

    LaunchedEffect(stateAction.value) {
        if (!stateAction.value && isShow) {
            onClose.invoke()
        }
    }
}

/**
 * Bottom sheet scaffold run in activity for exclude dependence compose stack
 *
 * @author Vitaliy Zarubin
 */
@OptIn(ExperimentalMaterialApi::class)
class BottomSheetScaffoldActivity : FragmentActivity() {
    companion object {

        val DEFAULT_TITLE_SHAPE = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        val DEFAULT_BACKGROUND_COLOR = Color.Black.copy(alpha = 0.7f)

        private var activitySystemBarsColor: Color? = null
        private var activityNavigationBarColor: Color? = null
        private var activityBackgroundColor: Color = DEFAULT_BACKGROUND_COLOR
        private var activityTitleShape: Shape = DEFAULT_TITLE_SHAPE
        private var activityComposeTitleContent: (@Composable () -> Unit)? = null
        private var activityComposeContent: (@Composable ColumnScope.() -> Unit)? = null
        private var currentActivity: FragmentActivity? = null

        fun setActivityData(
            systemBarsColor: Color?,
            navigationBarColor: Color?,
            backgroundColor: Color,
            titleShape: Shape,
            titleContent: @Composable () -> Unit,
            composeContent: (@Composable ColumnScope.() -> Unit),
        ) {
            activitySystemBarsColor = systemBarsColor
            activityNavigationBarColor = navigationBarColor
            activityBackgroundColor = backgroundColor
            activityTitleShape = titleShape
            activityComposeTitleContent = titleContent
            activityComposeContent = composeContent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this
        WindowCompat.setDecorFitsSystemWindows(window, false)
        theme.applyStyle(R.style.Theme_DialogTransparent, true)
        setContent {
            MaterialTheme {
                ProvideWindowInsets {
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        activitySystemBarsColor?.let {
                            systemUiController.setSystemBarsColor(it)
                        }
                        activityNavigationBarColor?.let {
                            systemUiController.setNavigationBarColor(it)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsWithImePadding()
                            .background(activityBackgroundColor)
                    ) {
                        BaseBottomSheetScaffold()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        state.value = false
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            overridePendingTransition(0, 0)
        }, 150)
    }

    @Composable
    fun BaseBottomSheetScaffold() {

        val stateAction = state.collectAsState()

        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(
                initialValue = BottomSheetValue.Collapsed
            )
        )

        BottomSheetScaffold(
            modifier = Modifier,
            sheetGesturesEnabled = false,
            sheetBackgroundColor = Color.Transparent,
            backgroundColor = Color.Transparent,
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 0.dp,
            sheetElevation = 0.dp,
            sheetContent = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .clip(activityTitleShape)
                            .background(MaterialTheme.colors.background)
                            .align(Alignment.BottomStart)
                    ) {
                        activityComposeTitleContent?.invoke()

                        Column {
                            activityComposeContent?.invoke(this)
                        }
                    }
                }
            }
        ) {}

        LaunchedEffect(stateAction.value) {
            if (stateAction.value) {
                bottomSheetScaffoldState.bottomSheetState.expand()
            } else {
                onBackPressed()
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        }
    }
}