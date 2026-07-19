package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.ElysianRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class UserRole {
    ADMIN,
    MANAGER,
    STAFF,
    CLIENT
}

class ElysianViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ElysianRepository(application)

    // Reactive streams from Room Database
    val halls: StateFlow<List<WeddingHall>> = repository.hallsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<Booking>> = repository.bookingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vendors: StateFlow<List<Vendor>> = repository.vendorsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<Notification>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<VendorTask>> = repository.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state states
    private val _currentRole = MutableStateFlow(UserRole.CLIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _selectedHallId = MutableStateFlow("hall_1")
    val selectedHallId: StateFlow<String> = _selectedHallId.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedBookingId = MutableStateFlow<Int?>(null)
    val selectedBookingId: StateFlow<Int?> = _selectedBookingId.asStateFlow()

    var chatInput by mutableStateOf("")
    var isChatSending by mutableStateOf(false)

    init {
        // Automatically populate elegant wedding data if DB empty
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            
            // Set selected date to today by default
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            _selectedDate.value = formatter.format(Date())
            
            // Setup default selected booking for vendor view if exists
            bookings.collect { bookingList ->
                if (_selectedBookingId.value == null && bookingList.isNotEmpty()) {
                    _selectedBookingId.value = bookingList.first().id
                }
            }
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
        viewModelScope.launch {
            repository.dao.insertNotification(
                Notification(
                    title = "Role Authorization Switched",
                    message = "Active session access control escalated to user group: ${role.name}.",
                    category = "System"
                )
            )
        }
    }

    fun logIn(role: UserRole) {
        _currentRole.value = role
        _isLoggedIn.value = true
        viewModelScope.launch {
            repository.dao.insertNotification(
                Notification(
                    title = "Portal Signed In",
                    message = "Authorized session started for ${role.name}.",
                    category = "System"
                )
            )
        }
    }

    fun logOut() {
        _isLoggedIn.value = false
    }

    fun selectHall(hallId: String) {
        _selectedHallId.value = hallId
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun selectBooking(bookingId: Int) {
        _selectedBookingId.value = bookingId
    }

    // --- Database Operations ---

    fun requestBooking(
        hallId: String,
        hallName: String,
        clientName: String,
        clientEmail: String,
        date: String,
        slot: String,
        price: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val status = if (currentRole.value == UserRole.ADMIN || currentRole.value == UserRole.MANAGER) {
                "Approved"
            } else {
                "Pending"
            }
            
            val booking = Booking(
                hallId = hallId,
                hallName = hallName,
                clientName = clientName,
                clientEmail = clientEmail,
                eventDate = date,
                slot = slot,
                status = status,
                price = price,
                notes = notes
            )
            val newId = repository.createBooking(booking)
            _selectedBookingId.value = newId
        }
    }

    fun approveBooking(booking: Booking) {
        viewModelScope.launch {
            repository.updateBooking(booking.copy(status = "Approved"))
        }
    }

    fun rejectBooking(booking: Booking) {
        viewModelScope.launch {
            repository.updateBooking(booking.copy(status = "Cancelled"))
        }
    }

    fun deleteBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
            if (_selectedBookingId.value == bookingId) {
                _selectedBookingId.value = bookings.value.firstOrNull()?.id
            }
        }
    }

    // --- Task coordination ---

    fun updateTask(task: VendorTask, newStatus: String) {
        viewModelScope.launch {
            repository.updateTaskStatus(task, newStatus, currentRole.value.name)
        }
    }

    fun addTaskForBooking(bookingId: Int, category: String, taskName: String) {
        viewModelScope.launch {
            val newTask = VendorTask(
                bookingId = bookingId,
                vendorCategory = category,
                taskName = taskName,
                status = "Pending",
                updatedBy = currentRole.value.name
            )
            repository.dao.insertTask(newTask)
            repository.dao.insertNotification(
                Notification(
                    title = "New Custom Vendor Task",
                    message = "Added task '$taskName' under category $category.",
                    category = "Task"
                )
            )
        }
    }

    // --- Chatbot ---

    fun sendChatMessage() {
        val query = chatInput.trim()
        if (query.isEmpty() || isChatSending) return

        chatInput = ""
        isChatSending = true

        viewModelScope.launch {
            try {
                repository.sendMessageToChatbot(query)
            } catch (e: Exception) {
                // Repository handles failures internally, but catch just in case
            } finally {
                isChatSending = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun triggerPresetQuestion(question: String) {
        chatInput = question
        sendChatMessage()
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.dao.clearAllNotifications()
        }
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.dao.markNotificationAsRead(id)
        }
    }
}
