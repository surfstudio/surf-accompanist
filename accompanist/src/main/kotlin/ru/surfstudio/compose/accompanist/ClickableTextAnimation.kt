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

import androidx.compose.animation.Animatable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Clickable text color animation
 *
 * @param modifier Modifier to apply to this layout node.
 * @param colorDefault Color for default state.
 * @param colorAction Color for animate.
 * @param colorDisable Color for disabled state.
 * @param text Text value.
 * @param enabled Enabled state.
 * @param delay Delay animation time.
 * @param style Text style.
 * @param underline Text style underline.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary.
 * @param overflow How visual overflow should be handled.
 * @param onTextLayout How visual overflow should be handled.
 * @param onClick Callback that is executed when users click the text. This callback is called
 * with clicked character's offset.
 *
 * @since 0.0.3
 * @author Vitaliy Zarubin
 *
 * @see <a href="https://github.com/keygenqt/android-DemoCompose/blob/master/app/src/main/kotlin/com/keygenqt/demo_contacts/modules/other/ui/screens/onboarding/OnboardingItem2.kt#L69">OnboardingItem2.kt#L69</a>
 */
@Composable
fun ClickableTextColorAnimation(
    modifier: Modifier = Modifier,
    colorDefault: Color,
    colorAction: Color,
    colorDisable: Color? = null,
    text: String,
    enabled: Boolean = true,
    delay: Long = 500,
    style: TextStyle = TextStyle.Default,
    underline: Boolean = true,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }
    var click: Boolean by remember { mutableStateOf(false) }
    val color = remember { Animatable(colorDefault) }

    ClickableText(
        modifier = modifier,
        style = style.copy(textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None),
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = if (enabled) color.value else colorDisable ?: color.value,
                )
            ) {
                append(text)
            }
        },
    ) {
        if (enabled) {
            click = true
            job?.cancel()
            job = scope.launch {
                delay(delay)
                onClick.invoke()
                click = false
            }
        }
    }

    LaunchedEffect(click) {
        color.animateTo(if (click) colorAction else colorDefault)
    }
}