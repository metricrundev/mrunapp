package com.metricrun.bdrawer.ui.calibrate


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalibrateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Calibrate Fragment"
    }
    val text: LiveData<String> = _text
}