package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wedding_halls")
data class WeddingHall(
    @PrimaryKey val id: String,
    val name: String,
    val capacity: Int,
    val pricePerDay: Double,
    val description: String,
    val location: String,
    val imageUrl: String
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hallId: String,
    val hallName: String,
    val clientName: String,
    val clientEmail: String,
    val eventDate: String, // YYYY-MM-DD
    val slot: String, // Morning, Evening, Full Day
    val status: String, // Pending, Approved, Cancelled
    val price: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vendors")
data class Vendor(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // Catering, Decoration, Photography, Entertainment
    val rating: Double,
    val contact: String,
    val description: String,
    val imageUrl: String
)

@Entity(tableName = "vendor_tasks")
data class VendorTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val vendorCategory: String,
    val taskName: String,
    val status: String, // Pending, In Progress, Completed
    val updatedBy: String, // Vendor, Client, Staff, Manager
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val category: String, // System, Booking, Chat, Task
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // User, AI
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
