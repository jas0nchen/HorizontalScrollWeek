/*
 * Copyright 2017 jason. https://github.com/jas0nchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jas0nchen.horizontalscrollweek;

/**
 * Author: jason
 * Time: 2017/9/28
 */
interface OnPageScrollStateChangeListener {
    int STATE_LOADING = 2;
    int STATE_IDLE = 1;

    void onScrollStateChange(boolean toRight);
}