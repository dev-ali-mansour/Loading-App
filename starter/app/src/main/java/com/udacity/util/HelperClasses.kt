package com.udacity.util


sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}

enum class DownloadStatus { SUCCESS, FAIL }