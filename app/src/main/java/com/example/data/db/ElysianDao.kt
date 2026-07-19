package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ElysianDao {

    // --- Wedding Halls ---
    @Query("SELECT * FROM wedding_halls")
    fun getAllHallsFlow(): Flow<List<WeddingHall>>

    @Query("SELECT * FROM wedding_halls")
    suspend fun getAllHalls(): List<WeddingHall>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHalls(halls: List<WeddingHall>)

    @Query("SELECT * FROM wedding_halls WHERE id = :id")
    suspend fun getHallById(id: String): WeddingHall?

    // --- Bookings ---
    @Query("SELECT * FROM bookings ORDER BY eventDate ASC")
    fun getAllBookingsFlow(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings ORDER BY eventDate ASC")
    suspend fun getAllBookings(): List<Booking>

    @Query("SELECT * FROM bookings WHERE eventDate = :date")
    suspend fun getBookingsByDate(date: String): List<Booking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): Booking?

    // --- Vendors ---
    @Query("SELECT * FROM vendors")
    fun getAllVendorsFlow(): Flow<List<Vendor>>

    @Query("SELECT * FROM vendors")
    suspend fun getAllVendors(): List<Vendor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<Vendor>)

    // --- Vendor Tasks ---
    @Query("SELECT * FROM vendor_tasks WHERE bookingId = :bookingId")
    fun getTasksForBookingFlow(bookingId: Int): Flow<List<VendorTask>>

    @Query("SELECT * FROM vendor_tasks WHERE bookingId = :bookingId")
    suspend fun getTasksForBooking(bookingId: Int): List<VendorTask>

    @Query("SELECT * FROM vendor_tasks")
    fun getAllTasksFlow(): Flow<List<VendorTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: VendorTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<VendorTask>)

    @Update
    suspend fun updateTask(task: VendorTask)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
