package com.j2kb.keez.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.j2kb.keez.data.api.model.SampleData
import com.j2kb.keez.domain.usecase.SampleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(private val sampleUseCase: SampleUseCase): ContainerHost<SampleData, SampleSideEffect>, ViewModel() {

    override val container = container<SampleData, SampleSideEffect>(SampleData())
    private var testJob: Job? = null

    init {
        getData()
    }

    private fun getData() = intent {
        testJob?.cancel()
        testJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = sampleUseCase()
                if (result.isNotNull()) {
                    reduce {
                        state.copy(result.id, result.name, result.values)
                    }
                } else {
                    showSideEffect("result is null")
                }
            } catch (e: RuntimeException) {
                showSideEffect(e.message)
            }
        }
    }

    private fun showSideEffect(message: String?) {
        intent {
            postSideEffect(SampleSideEffect.Toast(message ?: "Unknown Error Message"))
        }
    }
}