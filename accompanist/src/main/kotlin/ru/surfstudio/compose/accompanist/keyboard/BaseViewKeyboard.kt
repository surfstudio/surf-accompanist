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
package ru.surfstudio.compose.accompanist.keyboard

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.surfstudio.compose.accompanist.R
import ru.surfstudio.compose.accompanist.keyboard.vibration.Effect
import ru.surfstudio.compose.accompanist.keyboard.vibration.VibratorCompat.getVibrator

/**
 * Custom keyboard
 *
 * @author Vitaliy Zarubin
 */
@Composable
fun BaseViewKeyboard(
    modifier: Modifier = Modifier,
    actionText: String? = null,
    @DrawableRes fingerprintIconRes: Int = R.drawable.ic_default_fingerprint_24,
    @DrawableRes removeIconRes: Int = R.drawable.ic_default_arrow_back_24,
    isEnabled: Boolean = true,
    isShowRemove: Boolean = true,
    textColor: Color = MaterialTheme.colors.onSurface,
    backgroundColor: Color = MaterialTheme.colors.surface,
    isShowFingerprint: Boolean = false,
    onFingerprint: (() -> Unit) = {},
    onPress: (String) -> Unit = {},
    onRemove: () -> Unit = {},
    onActionButton: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clickEffect = remember { Effect.Click() }

    val press: (String) -> Unit = {
        onPress.invoke(it)
        scope.launch {
            context.runVibration(clickEffect)
        }
    }

    val isSmall = screenHeight < 600.dp

    val shape = RoundedCornerShape(if (isSmall) 12.dp else 16.dp)
    val modifierKey = Modifier.size(if (isSmall) 46.dp else 64.dp)

    Column(
        modifier = modifier
            .width(if (isSmall) 200.dp else 260.dp)
    ) {
        val digits = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )
        digits.forEach { row ->
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                row.forEach {
                    ViewKeyboardKey(
                        modifier = modifierKey,
                        shape = shape,
                        isSmall = isSmall,
                        isEnabled = isEnabled,
                        value = it,
                        textColor = textColor,
                        backgroundColor = backgroundColor,
                        onPress = press
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Card(
                elevation = 0.dp,
                backgroundColor = backgroundColor,
                shape = shape,
                modifier = modifierKey
            ) {
                if (!actionText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                onActionButton.invoke()
                            }
                            .fillMaxSize(),
                    ) {
                        Text(
                            color = textColor,
                            text = actionText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            ViewKeyboardKey(
                modifier = modifierKey,
                shape = shape,
                isSmall = isSmall,
                isEnabled = isEnabled,
                value = "0",
                textColor = textColor,
                backgroundColor = backgroundColor,
                onPress = press
            )
            Card(
                elevation = 0.dp,
                backgroundColor = backgroundColor,
                shape = shape,
                modifier = modifierKey
            ) {
                if (isShowRemove) {
                    IconKey(
                        isSmall = isSmall,
                        iconResId = removeIconRes,
                        contentDescription = "Remove",
                        textColor = textColor,
                        onClick = onRemove
                    )
                } else if (isShowFingerprint) {
                    IconKey(
                        isSmall = isSmall,
                        iconResId = fingerprintIconRes,
                        contentDescription = "Fingerprint",
                        textColor = textColor,
                        onClick = onFingerprint
                    )
                }
            }
        }
    }
}

@Composable
private fun IconKey(
    isSmall: Boolean,
    @DrawableRes iconResId: Int,
    contentDescription: String,
    textColor: Color,
    onClick: (() -> Unit),
) {
    Box(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(end = if (isSmall) 0.dp else 3.dp)
            .fillMaxSize(),
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(textColor),
            modifier = Modifier
                .height(if (isSmall) 26.dp else 40.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun ViewKeyboardKey(
    modifier: Modifier,
    shape: RoundedCornerShape,
    isSmall: Boolean,
    value: String,
    isEnabled: Boolean = true,
    textColor: Color = MaterialTheme.colors.onSurface,
    backgroundColor: Color = MaterialTheme.colors.surface,
    onPress: (String) -> Unit = {},
) {
    Card(
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        modifier = modifier,
        shape = shape
    ) {
        Box(
            (if (isEnabled) Modifier.clickable { onPress.invoke(value) } else Modifier)
                .fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(
                    letterSpacing = 0.sp,
                    fontSize = if (isSmall) 26.sp else 32.sp,
                    lineHeight = 0.sp,
                ),
                text = value
            )
        }
    }
}

/**
 * Run custom vibration effect
 *
 * @author Oleg Zhilo
 */
private fun Context.runVibration(effect: Effect) {
    getVibrator(this)?.let(effect::run)
}
