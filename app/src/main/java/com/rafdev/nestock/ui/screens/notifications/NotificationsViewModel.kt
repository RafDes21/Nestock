package com.rafdev.nestock.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rafdev.nestock.data.model.AppNotification
import com.rafdev.nestock.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    val unreadCount: StateFlow<Int> = _notifications
        .map { it.count { n -> !n.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                notificationRepository.observeNotifications(uid).collect { _notifications.value = it }
            }
        }
    }

    fun markAsRead(notifId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch { notificationRepository.markAsRead(uid, notifId) }
    }

    fun markAllAsRead() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch { notificationRepository.markAllAsRead(uid) }
    }
}
