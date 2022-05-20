## Surf Accompanist

![picture](https://github.com/surfstudio/surf-accompanist/blob/master/data/just-image.png?raw=true)

Accompanist is a group of libraries that aim to
supplement [Jetpack Compose](https://developer.android.com/jetpack/compose) with features that are commonly required by
developers but not yet available.

## Connection

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fartifactory.surfstudio.ru%2Fartifactory%2Flibs-release-local%2Fru%2Fsurfstudio%2Fcompose%2Faccompanist%2Fmaven-metadata.xml)

```gradle
repositories {
    maven("https://artifactory.surfstudio.ru/artifactory/libs-release-local")
}
dependencies {
    implementation("ru.surfstudio.compose:accompanist:${version}")
}
```

## Features:

### ![picture](https://github.com/google/material-design-icons/blob/master/png/search/manage_search/materialicons/18dp/1x/baseline_manage_search_black_18dp.png?raw=true) [MainScaffoldSearch](https://keygenqt.github.io/surf-accompanist/MainScaffoldSearch)
The main block of the application with topBar which has a built-in search and loader

### ![picture](https://github.com/google/material-design-icons/blob/master/png/action/view_list/materialicons/18dp/1x/baseline_view_list_black_18dp.png?raw=true) [SwipeRefreshList](https://keygenqt.github.io/surf-accompanist/SwipeRefreshList)
[LazyColumn](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Function1))
with embedded [SwipeRefresh](https://google.github.io/accompanist/swiperefresh/) and set of states for content: Error,
Empty, Loading

### ![picture](https://github.com/google/material-design-icons/blob/master/png/content/link/materialicons/18dp/1x/baseline_link_black_18dp.png?raw=true) [ClickableTextColorAnimation](https://keygenqt.github.io/surf-accompanist/ClickableTextColorAnimation)
Clickable text with color animation on click

### ![picture](https://github.com/google/material-design-icons/blob/master/png/content/link/materialicons/18dp/1x/baseline_link_black_18dp.png?raw=true) BaseViewKeyboard
Custom keyboard

## Future?

There are plans to add common functionality to make life easier for developers, saving them from custom solutions.

## License

```
Copyright 2021 Surf LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```