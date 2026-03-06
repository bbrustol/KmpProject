package com.bbrustol.core.infrastructure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * BasePresenter implements the MVI pattern on top of KMP ViewModel.
 *
 * @param Event   User actions dispatched to the presenter.
 * @param UiState Immutable state consumed by the UI.
 * @param SideEffect One-time effects (navigation, toasts). Consumed once and gone.
 */
abstract class BasePresenter<in Event, UiState, SideEffect> : ViewModel() {

    /**
     * Backing property for the StateFlow which holds the current UI state.
     */
    private val _uiState by lazy { MutableStateFlow(setInitialState()) }

    /**
     * StateFlow that exposes the current UI state to observers.
     * Updated by calling [updateState].
     */
    val uiState: StateFlow<UiState> get() = _uiState

    /**
     * Channel that collects events dispatched from the view or the presenter itself.
     * Uses [Channel.UNLIMITED] to guarantee events are never dropped.
     */
    private val _event = Channel<Event>(Channel.UNLIMITED)

    /**
     * Channel used to handle side effects with buffered transmission.
     */
    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)

    /**
     * Flow that allows observers to receive side effects emitted by the presenter.
     * Each effect is consumed once and then gone.
     */
    val sideEffect = _sideEffect.receiveAsFlow()

    /**
     * Initializes the presenter by starting the event collection loop.
     */
    init {
        handleEvents()
    }

    /**
     * Implement this method to define the initial state of the UI.
     * Called once when the presenter is created.
     *
     * @return the initial state of type [UiState].
     */
    protected abstract fun setInitialState(): UiState

    /**
     * Implement this method to handle and process events dispatched to the presenter.
     * Each event should trigger a [updateState] call, a [sendSideEffect] call, or both.
     *
     * @param event the event to process.
     */
    protected abstract fun process(event: Event)

    /**
     * Collects dispatched events sequentially and delegates each to [process].
     * Automatically invoked during initialization.
     */
    private fun handleEvents() {
        viewModelScope.launch {
            _event.receiveAsFlow().collect { process(it) }
        }
    }

    /**
     * Dispatches one or more events to be processed by the presenter.
     *
     * @param event vararg of events to dispatch.
     */
    fun dispatch(vararg event: Event) {
        viewModelScope.launch {
            event.forEach { _event.send(it) }
        }
    }

    /**
     * Updates the current UI state based on the provided reducer function.
     *
     * @param reducer a lambda that takes the current [UiState] and returns a new modified state.
     */
    fun updateState(reducer: UiState.() -> UiState) {
        _uiState.update(reducer)
    }

    /**
     * Sends a side effect to be consumed once by the observers.
     *
     * @param builder a lambda that returns the [SideEffect] instance to emit.
     */
    protected fun sendSideEffect(builder: () -> SideEffect) {
        viewModelScope.launch {
            _sideEffect.send(builder())
        }
    }
}
