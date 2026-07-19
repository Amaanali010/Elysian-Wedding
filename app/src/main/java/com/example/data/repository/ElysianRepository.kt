package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.db.AppDatabase
import com.example.data.db.ElysianDao
import com.example.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class ElysianRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    val dao: ElysianDao = db.elysianDao()

    val hallsFlow: Flow<List<WeddingHall>> = dao.getAllHallsFlow()
    val bookingsFlow: Flow<List<Booking>> = dao.getAllBookingsFlow()
    val vendorsFlow: Flow<List<Vendor>> = dao.getAllVendorsFlow()
    val notificationsFlow: Flow<List<Notification>> = dao.getAllNotificationsFlow()
    val chatMessagesFlow: Flow<List<ChatMessage>> = dao.getAllChatMessagesFlow()
    val tasksFlow: Flow<List<VendorTask>> = dao.getAllTasksFlow()

    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val existingHalls = dao.getAllHalls()
        if (existingHalls.isEmpty()) {
            Log.d("ElysianRepository", "Database is empty. Populating with elegant sample data...")
            
            // 1. Insert Halls
            val halls = listOf(
                WeddingHall(
                    id = "hall_1",
                    name = "The Grand Ballroom",
                    capacity = 500,
                    pricePerDay = 3500.0,
                    description = "A luxurious majestic hall adorned with royal crystal chandeliers, cathedral ceilings, and a massive velvet stage. Perfect for grand galas.",
                    location = "Palace Wing, Level 1",
                    imageUrl = "ballroom"
                ),
                WeddingHall(
                    id = "hall_2",
                    name = "The Royal Gardens",
                    capacity = 300,
                    pricePerDay = 2500.0,
                    description = "An exquisite botanical garden complete with blooming floral arches, glowing fairy lights, a stone bridge, and a beautiful sunset wedding pavilion.",
                    location = "North Meadows Wing",
                    imageUrl = "garden"
                ),
                WeddingHall(
                    id = "hall_3",
                    name = "The Glass Pavilion",
                    capacity = 150,
                    pricePerDay = 1800.0,
                    description = "A stunning glass-enclosed lakeside pavilion offering panoramic views of the water, custom LED floor lighting, and scenic sunset backdrops.",
                    location = "West Lake Pier",
                    imageUrl = "pavilion"
                )
            )
            dao.insertHalls(halls)

            // 2. Insert Vendors
            val vendors = listOf(
                Vendor(
                    id = "v_1",
                    name = "Grand Feast Catering",
                    category = "Catering",
                    rating = 4.9,
                    contact = "+1 (555) 012-3456",
                    description = "Fine-dining custom menus, gourmet dessert towers, and premium silver service customized to your cultural preferences.",
                    imageUrl = "catering_img"
                ),
                Vendor(
                    id = "v_2",
                    name = "Luxe Petals Florals",
                    category = "Decoration",
                    rating = 4.8,
                    contact = "+1 (555) 012-7890",
                    description = "Luxury stage backdrops, thematic floral walls, ceiling cascade installations, and bridal bouquet designs.",
                    imageUrl = "decor_img"
                ),
                Vendor(
                    id = "v_3",
                    name = "Golden Hour Cinema",
                    category = "Photography",
                    rating = 4.9,
                    contact = "+1 (555) 012-9988",
                    description = "Cinematic slow-motion reels, drone coverage, 4K multi-cam setups, and premium leather-bound memory albums.",
                    imageUrl = "photo_img"
                ),
                Vendor(
                    id = "v_4",
                    name = "Elysian Strings & Beats",
                    category = "Entertainment",
                    rating = 4.7,
                    contact = "+1 (555) 012-4422",
                    description = "Elegant classical live string quartets for the ceremony, transitioning into award-winning DJ and lighting show for the party.",
                    imageUrl = "music_img"
                )
            )
            dao.insertVendors(vendors)

            // 3. Create 2 Sample Bookings
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            
            cal.add(Calendar.DAY_OF_YEAR, 10)
            val date1 = formatter.format(cal.time)
            
            cal.add(Calendar.DAY_OF_YEAR, 15)
            val date2 = formatter.format(cal.time)

            val booking1Id = dao.insertBooking(
                Booking(
                    hallId = "hall_1",
                    hallName = "The Grand Ballroom",
                    clientName = "Victoria & Richard",
                    clientEmail = "victoria.r@example.com",
                    eventDate = date1,
                    slot = "Evening",
                    status = "Approved",
                    price = 3500.0,
                    notes = "Requested white-rose decorations and champagne-welcome reception."
                )
            ).toInt()

            val booking2Id = dao.insertBooking(
                Booking(
                    hallId = "hall_2",
                    hallName = "The Royal Gardens",
                    clientName = "Isabella & Mason",
                    clientEmail = "isabella.m@example.com",
                    eventDate = date2,
                    slot = "Morning",
                    status = "Pending",
                    price = 2500.0,
                    notes = "Outdoor ceremony. Need backup canopy setup in case of light rain."
                )
            ).toInt()

            // 4. Create Vendor Checklist items for these bookings
            val tasks = listOf(
                VendorTask(bookingId = booking1Id, vendorCategory = "Catering", taskName = "Menu Selection & Food Tasting", status = "Completed", updatedBy = "Staff"),
                VendorTask(bookingId = booking1Id, vendorCategory = "Catering", taskName = "Wedding Cake Flavors Finalization", status = "In Progress", updatedBy = "Vendor"),
                VendorTask(bookingId = booking1Id, vendorCategory = "Decoration", taskName = "Floral Color Theme Selection", status = "Completed", updatedBy = "Client"),
                VendorTask(bookingId = booking1Id, vendorCategory = "Decoration", taskName = "Fairy Light Setup & Arches", status = "Pending", updatedBy = "Staff"),
                VendorTask(bookingId = booking1Id, vendorCategory = "Photography", taskName = "Drone Flight Approvals", status = "Completed", updatedBy = "Vendor"),
                VendorTask(bookingId = booking1Id, vendorCategory = "Entertainment", taskName = "DJ Song List Submission", status = "In Progress", updatedBy = "Client"),

                VendorTask(bookingId = booking2Id, vendorCategory = "Catering", taskName = "Outdoor Buffet Layout Approved", status = "Completed", updatedBy = "Staff"),
                VendorTask(bookingId = booking2Id, vendorCategory = "Decoration", taskName = "Garden Canopy Installation Design", status = "Pending", updatedBy = "Vendor"),
                VendorTask(bookingId = booking2Id, vendorCategory = "Photography", taskName = "Pre-Wedding Shoot Date", status = "Pending", updatedBy = "Client")
            )
            dao.insertTasks(tasks)

            // 5. Initial Notifications
            dao.insertNotification(
                Notification(
                    title = "Elysian System Initialized",
                    message = "Elysian Wedding Hall Management System has loaded successfully with standard halls and vendor integrations.",
                    category = "System"
                )
            )
            dao.insertNotification(
                Notification(
                    title = "New Booking Request",
                    message = "Isabella & Mason submitted a new pending request for The Royal Gardens on $date2.",
                    category = "Booking"
                )
            )
        }
    }

    // --- Bookings ---
    suspend fun createBooking(booking: Booking): Int = withContext(Dispatchers.IO) {
        val id = dao.insertBooking(booking).toInt()
        
        // Auto-create standard checklist tasks for the new booking
        val tasks = listOf(
            VendorTask(bookingId = id, vendorCategory = "Catering", taskName = "Choose Menu Selection", status = "Pending", updatedBy = "Staff"),
            VendorTask(bookingId = id, vendorCategory = "Decoration", taskName = "Discuss Theme & Color Swatches", status = "Pending", updatedBy = "Staff"),
            VendorTask(bookingId = id, vendorCategory = "Photography", taskName = "Confirm Event Timeline with Camera Crew", status = "Pending", updatedBy = "Staff"),
            VendorTask(bookingId = id, vendorCategory = "Entertainment", taskName = "Submit Playlist & Entrance Songs", status = "Pending", updatedBy = "Staff")
        )
        dao.insertTasks(tasks)

        // Send Notification
        dao.insertNotification(
            Notification(
                title = "New Booking Registered",
                message = "${booking.clientName} booked ${booking.hallName} for ${booking.eventDate} (${booking.slot}).",
                category = "Booking"
            )
        )
        id
    }

    suspend fun updateBooking(booking: Booking) = withContext(Dispatchers.IO) {
        dao.updateBooking(booking)
        dao.insertNotification(
            Notification(
                title = "Booking Updated",
                message = "The booking status for ${booking.clientName} has been updated to ${booking.status}.",
                category = "Booking"
            )
        )
    }

    suspend fun deleteBooking(bookingId: Int) = withContext(Dispatchers.IO) {
        dao.deleteBookingById(bookingId)
    }

    // --- Tasks ---
    suspend fun updateTaskStatus(task: VendorTask, newStatus: String, userRole: String) = withContext(Dispatchers.IO) {
        val updatedTask = task.copy(status = newStatus, updatedBy = userRole, lastUpdated = System.currentTimeMillis())
        dao.updateTask(task = updatedTask)

        // Notification
        dao.insertNotification(
            Notification(
                title = "Task Progress Update",
                message = "Task '${task.taskName}' updated to $newStatus by $userRole.",
                category = "Task"
            )
        )
    }

    // --- Chat & Scheduling Agent ---
    suspend fun sendMessageToChatbot(userText: String): String = withContext(Dispatchers.IO) {
        // 1. Log user message
        dao.insertChatMessage(ChatMessage(sender = "User", text = userText))

        // 2. Automated Smart Scheduling Check
        // If user says "schedule for YYYY-MM-DD", let's extract date or try to schedule!
        val datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})")
        val matcher = datePattern.matcher(userText)
        var autoScheduledMessage = ""
        
        if (matcher.find()) {
            val extractedDate = matcher.group(1) ?: ""
            // See if we can parse or schedule a basic appt!
            val halls = dao.getAllHalls()
            val chosenHall = halls.firstOrNull() ?: WeddingHall("hall_1", "Grand Ballroom", 500, 3500.0, "", "", "")
            
            // Let's see if date is already booked
            val bookings = dao.getBookingsByDate(extractedDate)
            val isBooked = bookings.any { it.slot == "Full Day" || it.slot == "Evening" }
            
            if (isBooked) {
                autoScheduledMessage = "\n\n[Automated Scheduling System Alert]: Note that $extractedDate is currently fully reserved for Evening/Full Day slots. I recommend choosing an alternative date or contacting our planning desk!"
            } else {
                // Auto schedule appointment!
                val bookingId = dao.insertBooking(
                    Booking(
                        hallId = chosenHall.id,
                        hallName = chosenHall.name,
                        clientName = "Chatbot User Appointment",
                        clientEmail = "inquiry@elysian.com",
                        eventDate = extractedDate,
                        slot = "Evening",
                        status = "Pending",
                        price = chosenHall.pricePerDay,
                        notes = "Automated appointment scheduled via Elysian AI Chatbot."
                    )
                ).toInt()

                // Insert tasks
                val tasks = listOf(
                    VendorTask(bookingId = bookingId, vendorCategory = "Catering", taskName = "Consult with Caterer", status = "Pending", updatedBy = "System")
                )
                dao.insertTasks(tasks)

                // Insert system notification
                dao.insertNotification(
                    Notification(
                        title = "Automated Booking Scheduled",
                        message = "New appointment auto-scheduled via AI Chatbot for $extractedDate.",
                        category = "Booking"
                    )
                )

                autoScheduledMessage = "\n\n🎉 *[AUTOMATED SCHEDULING SUCCESS]*: I have successfully registered a tentative appointment reservation for **${chosenHall.name}** on **$extractedDate** (Evening slot) under 'Chatbot User Appointment'. Our planning manager will review it and notify you!"
            }
        }

        // 3. Generate AI Answer
        val apiKey = BuildConfig.GEMINI_API_KEY
        val aiResponse: String = if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                // Get chat history for conversational context
                val history = dao.getAllChatMessagesFlow().first().takeLast(10)
                val conversationParts = history.map { 
                    Part(text = "${it.sender}: ${it.text}")
                }.toMutableList()
                
                // Add current message (we already inserted it so it is included in history if taken reactively, 
                // but let's append it manually if not yet reflected)
                if (conversationParts.none { it.text?.contains(userText) == true }) {
                    conversationParts.add(Part(text = "User: $userText"))
                }

                val systemInstruction = "You are Elysian, the beautiful AI Wedding Hall Concierge of the luxurious Elysian Wedding Halls (Halls: The Grand Ballroom, The Royal Gardens, The Glass Pavilion). Your purpose is to answer client questions about halls, prices, capacities, and help them schedule automated appointments. If they mention a date formatted as YYYY-MM-DD (like 2026-08-15), acknowledge warmly that you have triggered our automated booking scheduler for that date. Keep answers premium, warm, short (under 4 paragraphs), and elegant."

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = conversationParts)),
                    generationConfig = GenerationConfig(temperature = 0.7f),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                    ?: "I am here to guide your journey to happily ever after. How may I assist you with our wedding halls today?"
                
                rawText + autoScheduledMessage
            } catch (e: Exception) {
                Log.e("ElysianRepository", "Gemini API error, falling back to rule-based engine", e)
                generateFallbackResponse(userText) + autoScheduledMessage
            }
        } else {
            generateFallbackResponse(userText) + autoScheduledMessage
        }

        // 4. Log AI response
        dao.insertChatMessage(ChatMessage(sender = "AI", text = aiResponse))
        aiResponse
    }

    private fun generateFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Welcome to **Elysian Weddings**! 🌸 I am your personal wedding concierge. I can answer inquiries about our elegant wedding halls, coordinate vendors, and assist you in *automated appointment scheduling*. To trigger our scheduling system, simply specify a date in the format `YYYY-MM-DD` (e.g. `2026-08-15`)!"
            }
            lower.contains("hall") || lower.contains("venue") || lower.contains("room") || lower.contains("ballroom") -> {
                "Elysian offers three stunning masterpieces:\n\n" +
                "1. **The Grand Ballroom** (Cap: 500 | $3,500/day): A royal indoor venue with luxury chandeliers.\n" +
                "2. **The Royal Gardens** (Cap: 300 | $2,500/day): Outdoor botanical garden with twinkling lights.\n" +
                "3. **The Glass Pavilion** (Cap: 150 | $1,800/day): A glass sunset-facing room overlooking the lake.\n\n" +
                "Would you like to schedule an appointment to view any of these?"
            }
            lower.contains("price") || lower.contains("cost") || lower.contains("rate") || lower.contains("fee") -> {
                "Our venue rental prices range from **$1,800** to **$3,500 per day**:\n" +
                "- **The Glass Pavilion**: $1,800/day\n" +
                "- **The Royal Gardens**: $2,500/day\n" +
                "- **The Grand Ballroom**: $3,500/day\n" +
                "This includes professional planning staff assistance, high-fidelity sound, and essential decor setup."
            }
            lower.contains("vendor") || lower.contains("caterer") || lower.contains("photograph") || lower.contains("decor") -> {
                "We coordinate with verified premium vendors:\n" +
                "- **Catering**: *Grand Feast Catering* (Fine culinary dining)\n" +
                "- **Decoration**: *Luxe Petals Florals* (Artistic ceiling installations)\n" +
                "- **Photography**: *Golden Hour Cinema* (Cinematic 4K coverage)\n" +
                "- **Entertainment**: *Elysian Strings & Beats* (Quartets & DJs)\n\n" +
                "Clients and vendors can coordinate in real-time through their respective dashboards in this application."
            }
            lower.contains("schedule") || lower.contains("book") || lower.contains("appointment") -> {
                "I would love to schedule a viewing appointment for you! Please tell me your preferred date in the `YYYY-MM-DD` format (for example: **2026-10-24**) and I will reserve your slot instantly!"
            }
            else -> {
                "I am here to guide you through planning your perfect wedding. I can answer questions about **halls**, **rates**, **vendor checklists**, and perform **automated scheduling** for viewing appointments. Please let me know how I can help!"
            }
        }
    }
    
    suspend fun clearChat() = withContext(Dispatchers.IO) {
        dao.clearChatHistory()
        // Insert initial message
        dao.insertChatMessage(
            ChatMessage(
                sender = "AI",
                text = "Welcome to the Elysian Wedding Planning chatbot! 🌸 How can I help you plan your dream wedding or schedule a private viewing appointment today? (Try typing a date like `2026-08-25` to see automated scheduling in action!)"
            )
        )
    }
}
