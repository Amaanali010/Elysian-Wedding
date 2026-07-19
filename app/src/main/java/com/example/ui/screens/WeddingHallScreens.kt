package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.*
import com.example.ui.viewmodel.ElysianViewModel
import com.example.ui.viewmodel.UserRole
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeddingHallAppScreen(
    viewModel: ElysianViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifications = notifications.filter { !it.isRead }.size

    var currentTab by remember { mutableStateOf(0) }
    var showRoleMenu by remember { mutableStateOf(false) }

    val tabs = listOf(
        TabItem("Calendar", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
        TabItem("AI Support", Icons.Default.Chat, Icons.Outlined.Chat),
        TabItem("Vendors", Icons.Default.Handshake, Icons.Outlined.Handshake),
        TabItem("Analytics", Icons.Default.BarChart, Icons.Outlined.BarChart),
        TabItem("Alerts", Icons.Default.Notifications, Icons.Outlined.Notifications, badgeCount = unreadNotifications)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ELYSIAN WEDDINGS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = currentRole.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80))
                                )
                                Text(
                                    text = "Live Tracking",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        if (currentRole == UserRole.ADMIN || currentRole == UserRole.MANAGER) {
                            Box {
                                FilterChip(
                                    selected = true,
                                    onClick = { showRoleMenu = !showRoleMenu },
                                    label = {
                                        Text(
                                            text = currentRole.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "Role Selector",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Expand Roles"
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )

                                DropdownMenu(
                                    expanded = showRoleMenu,
                                    onDismissRequest = { showRoleMenu = false }
                                ) {
                                    UserRole.values().forEach { role ->
                                        DropdownMenuItem(
                                            text = { 
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    val roleIcon = when (role) {
                                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                                        UserRole.MANAGER -> Icons.Default.ManageAccounts
                                                        UserRole.STAFF -> Icons.Default.AssignmentInd
                                                        UserRole.CLIENT -> Icons.Default.Favorite
                                                    }
                                                    Icon(roleIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(role.name)
                                                }
                                            },
                                            onClick = {
                                                viewModel.setRole(role)
                                                showRoleMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.logOut() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab.badgeCount > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text(tab.badgeCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == index) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = { Text(tab.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Role Banner indicator to keep users oriented on RBAC access level
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (roleDesc, icon) = when (currentRole) {
                        UserRole.ADMIN -> "Administrator Mode: Full system privileges & Revenue Analytics" to Icons.Default.AdminPanelSettings
                        UserRole.MANAGER -> "Manager Mode: Booking Approvals & Vendor Assignment" to Icons.Default.ManageAccounts
                        UserRole.STAFF -> "Staff Mode: View Bookings & Check-off coordination items" to Icons.Default.AssignmentInd
                        UserRole.CLIENT -> "Client Portal: Custom Bookings, Vendor Progress & Support" to Icons.Default.Favorite
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = roleDesc,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Tab rendering
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> CalendarAvailabilityTab(viewModel)
                    1 -> ChatbotSupportTab(viewModel)
                    2 -> VendorCoordinationTab(viewModel)
                    3 -> AnalyticsDashboardTab(viewModel)
                    4 -> AlertNotificationsTab(viewModel)
                }
            }
        }
    }
}

// Data class for bottom navigation tab configuration
data class TabItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)

// ==========================================
// 1. CALENDAR & AVAILABILITY TAB
// ==========================================
@Composable
fun CalendarAvailabilityTab(viewModel: ElysianViewModel) {
    val halls by viewModel.halls.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val selectedHallId by viewModel.selectedHallId.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var showBookingSheet by remember { mutableStateOf(false) }

    val currentSelectedHall = halls.find { it.id == selectedHallId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hall Selector Row
        item {
            Text(
                text = "Select Wedding Hall",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(halls) { hall ->
                    val isSelected = hall.id == selectedHallId
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .testTag("hall_chip_${hall.id}")
                            .clickable { viewModel.selectHall(hall.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Canvas Drawing representation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                when (hall.id) {
                                    "hall_1" -> ChandelierCanvas()
                                    "hall_2" -> GardenCanvas()
                                    else -> LakePavilionCanvas()
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = hall.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Cap: ${hall.capacity} guests",
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Date Picker/Scroller Row (Real-time Availability visual calendar)
        item {
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalendarStrip(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )
        }

        // Daily Slot Status Panel
        item {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormatted: String = try {
                val parsed = formatter.parse(selectedDate)
                SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(parsed ?: Date())
            } catch (e: Exception) {
                selectedDate
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dateFormatted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSelectedHall?.name ?: "Hall Status",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Action Book Button
                        Button(
                            onClick = { showBookingSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.testTag("quick_book_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Book Slot", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Split Slot: Morning vs Evening
                    val morningBooking = bookings.find { it.eventDate == selectedDate && it.hallId == selectedHallId && (it.slot == "Morning" || it.slot == "Full Day") }
                    val eveningBooking = bookings.find { it.eventDate == selectedDate && it.hallId == selectedHallId && (it.slot == "Evening" || it.slot == "Full Day") }

                    SlotRow(slotName = "Morning Slot (8:00 AM - 2:00 PM)", booking = morningBooking, viewModel = viewModel, currentRole = currentRole)
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))
                    SlotRow(slotName = "Evening Slot (4:00 PM - 10:00 PM)", booking = eveningBooking, viewModel = viewModel, currentRole = currentRole)
                }
            }
        }

        // Selected Hall Details
        currentSelectedHall?.let { hall ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Venue Details & Inclusions", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(hall.description, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextAndIconChip(icon = Icons.Default.People, text = "Up to ${hall.capacity} PAX")
                            TextAndIconChip(icon = Icons.Default.Place, text = hall.location)
                            TextAndIconChip(icon = Icons.Default.Payments, text = "$${hall.pricePerDay.toInt()}/Day")
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet representation (simplified custom Dialog overlay for ease & 100% reliability in Compose template)
    if (showBookingSheet && currentSelectedHall != null) {
        BookingRequestDialog(
            hall = currentSelectedHall,
            selectedDate = selectedDate,
            onDismiss = { showBookingSheet = false },
            onSubmit = { name, email, slot, notes ->
                viewModel.requestBooking(
                    hallId = currentSelectedHall.id,
                    hallName = currentSelectedHall.name,
                    clientName = name,
                    clientEmail = email,
                    date = selectedDate,
                    slot = slot,
                    price = currentSelectedHall.pricePerDay,
                    notes = notes
                )
                showBookingSheet = false
            }
        )
    }
}

@Composable
fun SlotRow(
    slotName: String,
    booking: Booking?,
    viewModel: ElysianViewModel,
    currentRole: UserRole
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(slotName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            if (booking != null) {
                Text(
                    text = "Booked by: ${booking.clientName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (booking.status) {
                        "Approved" -> Color(0xFF4CAF50) // Green
                        "Pending" -> Color(0xFFFF9800) // Orange
                        else -> Color(0xFFF44336) // Red
                    }
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(booking.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            } else {
                Text("Available for reservation", fontSize = 12.sp, color = Color(0xFF4CAF50))
            }
        }

        // Action buttons based on Role-Based Access Control
        if (booking != null) {
            if (booking.status == "Pending" && (currentRole == UserRole.ADMIN || currentRole == UserRole.MANAGER)) {
                Row {
                    IconButton(
                        onClick = { viewModel.approveBooking(booking) },
                        modifier = Modifier.testTag("approve_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Approve", tint = Color(0xFF4CAF50))
                    }
                    IconButton(
                        onClick = { viewModel.rejectBooking(booking) },
                        modifier = Modifier.testTag("reject_btn")
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = "Reject", tint = Color(0xFFF44336))
                    }
                }
            } else if (currentRole == UserRole.ADMIN || currentRole == UserRole.MANAGER) {
                IconButton(
                    onClick = { viewModel.deleteBooking(booking.id) },
                    modifier = Modifier.testTag("delete_booking_btn")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        } else {
            Icon(Icons.Default.CheckCircle, contentDescription = "Free", tint = Color(0xFF4CAF50).copy(alpha = 0.4f))
        }
    }
}

@Composable
fun TextAndIconChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CalendarStrip(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = remember {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        // Offer current date + 15 upcoming days
        for (i in 0..15) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(dates) { date ->
            val dateStr = formatter.format(date)
            val isSelected = dateStr == selectedDate
            
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
            val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
            val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(date)

            Card(
                modifier = Modifier
                    .width(64.dp)
                    .clickable { onDateSelected(dateStr) }
                    .testTag("date_strip_$dateStr"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(dayName.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(dayNum, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(monthName, fontSize = 10.sp, color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun BookingRequestDialog(
    hall: WeddingHall,
    selectedDate: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf("Evening") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book ${hall.name}", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Date Selected: $selectedDate", fontWeight = FontWeight.Medium)
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Client Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("booking_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column {
                    Text("Select Time Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Morning", "Evening", "Full Day").forEach { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text(slot, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Requests / Theme Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onSubmit(name, email, selectedSlot, notes)
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier.testTag("submit_booking_confirm_btn")
            ) {
                Text("Confirm Booking Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================
// 2. AI CUSTOMER SUPPORT CHATBOT TAB
// ==========================================
@Composable
fun ChatbotSupportTab(viewModel: ElysianViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isSending by remember { derivedStateOf { viewModel.isChatSending } }
    val listState = rememberLazyListState()

    // Keep scrolling to the newest message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header with Clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Elysian Wedding AI", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Powered by Gemini-3.5-flash", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            TextButton(onClick = { viewModel.clearChat() }) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear History", fontSize = 12.sp)
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

        // Preset Suggested Questions
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                PresetChip(text = "Rates & Inclusions?") {
                    viewModel.triggerPresetQuestion("What are the pricing rates for all three wedding halls?")
                }
            }
            item {
                PresetChip(text = "Botanical Gardens capacity?") {
                    viewModel.triggerPresetQuestion("What is the capacity and style of the Royal Gardens?")
                }
            }
            item {
                PresetChip(text = "Schedule Viewing Appointment?") {
                    viewModel.triggerPresetQuestion("I would like to schedule a viewing appointment.")
                }
            }
            item {
                PresetChip(text = "Auto Booking Trigger?") {
                    viewModel.triggerPresetQuestion("Schedule an appointment for 2026-08-25")
                }
            }
        }

        // Messages List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (chatMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Start your dream conversation",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Type to ask about venues, coordinate vendors,\nor schedule appointments automatically.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatMessages) { msg ->
                        ChatBubble(message = msg)
                    }
                    if (isSending) {
                        item {
                            AITypingIndicator()
                        }
                    }
                }
            }
        }

        // Input Row
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.chatInput,
                    onValueChange = { viewModel.chatInput = it },
                    placeholder = { Text("Ask Elysian AI or type date to auto-schedule...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    trailingIcon = {
                        if (viewModel.chatInput.isNotEmpty()) {
                            IconButton(onClick = { viewModel.chatInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { viewModel.sendChatMessage() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("chat_send_button"),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun PresetChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "User"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val bubbleShape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = bubbleShape,
            color = containerColor,
            shadowElevation = if (isUser) 1.dp else 0.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun AITypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .widthIn(max = 80.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "dots")
        val dot1Alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot1"
        )
        val dot2Alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = 200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot2"
        )
        val dot3Alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = 400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot3"
        )

        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = dot1Alpha)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = dot2Alpha)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = dot3Alpha)))
    }
}


// ==========================================
// 3. MULTI-USER VENDOR COORDINATION TAB
// ==========================================
@Composable
fun VendorCoordinationTab(viewModel: ElysianViewModel) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val vendors by viewModel.vendors.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val selectedBookingId by viewModel.selectedBookingId.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    val activeBooking = bookings.find { it.id == selectedBookingId }
    val activeTasks = allTasks.filter { it.bookingId == selectedBookingId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Coordination Selector (Which Wedding Booking event)
        item {
            Text(
                text = "Select Client Wedding Event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No weddings booked yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bookings) { b ->
                        val isSelected = b.id == selectedBookingId
                        Card(
                            modifier = Modifier
                                .width(190.dp)
                                .clickable { viewModel.selectBooking(b.id) }
                                .testTag("booking_card_${b.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = b.clientName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(b.hallName, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(b.eventDate, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = b.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (b.status == "Approved") Color(0xFF4CAF50) else Color(0xFFFF9800)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Checklist Progress Card
        activeBooking?.let { booking ->
            val total = activeTasks.size
            val completed = activeTasks.count { it.status == "Completed" }
            val progressPercent = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Coordination Checklist",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Event: ${booking.clientName}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            // Add Task button
                            Button(
                                onClick = { showAddTaskDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Icon(Icons.Default.AddTask, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Task", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress representation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { progressPercent },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(progressPercent * 100).toInt()}% Done",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "$completed of $total tasks completed successfully",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Vendor checklists categorized
            val categories = listOf("Catering", "Decoration", "Photography", "Entertainment")
            categories.forEach { category ->
                val categoryTasks = activeTasks.filter { it.vendorCategory == category }
                val associatedVendor = vendors.find { it.category == category }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$category - ${associatedVendor?.name ?: ' '}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(onClick = {
                                val context = associatedVendor?.contact ?: "Inquiry Desk"
                                // Simply a notification to demonstrate interactive contact link
                                viewModel.addTaskForBooking(booking.id, category, "Call Vendor Contact")
                            }) {
                                Icon(Icons.Default.Phone, contentDescription = "Call Vendor", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (categoryTasks.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No coordination tasks scheduled for $category.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column {
                                    categoryTasks.forEachIndexed { idx, task ->
                                        TaskItemRow(task = task, viewModel = viewModel, currentRole = currentRole)
                                        if (idx < categoryTasks.size - 1) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog && activeBooking != null) {
        var customTaskName by remember { mutableStateOf("") }
        var selectedCat by remember { mutableStateOf("Catering") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Coordination Task") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = customTaskName,
                        onValueChange = { customTaskName = it },
                        label = { Text("Task Action Item") },
                        modifier = Modifier.fillMaxWidth().testTag("new_task_input"),
                        singleLine = true
                    )

                    Column {
                        Text("Select Assigned Vendor Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Catering", "Decoration", "Photography", "Entertainment").forEach { cat ->
                                FilterChip(
                                    selected = selectedCat == cat,
                                    onClick = { selectedCat = cat },
                                    label = { Text(cat, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTaskName.isNotBlank()) {
                            viewModel.addTaskForBooking(activeBooking.id, selectedCat, customTaskName)
                            showAddTaskDialog = false
                        }
                    },
                    enabled = customTaskName.isNotBlank(),
                    modifier = Modifier.testTag("new_task_submit_btn")
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskItemRow(
    task: VendorTask,
    viewModel: ElysianViewModel,
    currentRole: UserRole
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.taskName,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = if (task.status == "Completed") MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Last updated by ${task.updatedBy}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        // Multi-state Toggle representation
        val (badgeColor, label) = when (task.status) {
            "Completed" -> Color(0xFF4CAF50) to "Completed"
            "In Progress" -> Color(0xFF2196F3) to "In Progress"
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) to "Pending"
        }

        AssistChip(
            onClick = {
                // Anyone can cycle status to make testing multi-user dashboard seamless!
                val nextStatus = when (task.status) {
                    "Pending" -> "In Progress"
                    "In Progress" -> "Completed"
                    else -> "Pending"
                }
                viewModel.updateTask(task, nextStatus)
            },
            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor) },
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
            modifier = Modifier.testTag("task_chip_${task.id}")
        )
    }
}


// ==========================================
// 4. ANALYTICS & PERFORMANCE DASHBOARD TAB
// ==========================================
@Composable
fun AnalyticsDashboardTab(viewModel: ElysianViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    // Access control check: Only ADMIN and MANAGER can see Analytics!
    if (currentRole != UserRole.ADMIN && currentRole != UserRole.MANAGER) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Access Blocked",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Role Authorization Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your current role access group (${currentRole.name}) is restricted from viewing revenue and performance metrics.\n\nPlease escalate your authorization role to ADMIN or MANAGER using the chip in the top bar to preview the performance charts.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.setRole(UserRole.ADMIN) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Switch Role to ADMIN")
                }
            }
        }
    } else {
        // Analytics Success State (ADMIN or MANAGER)
        val approvedBookings = bookings.filter { it.status == "Approved" }
        val pendingBookings = bookings.filter { it.status == "Pending" }

        val totalProjectedRevenue = approvedBookings.sumOf { it.price }
        val depositCollected = totalProjectedRevenue * 0.50 // 50% deposit
        val pendingInvoices = pendingBookings.sumOf { it.price }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Elysian Financial Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Real-time revenue metrics from approved client accounts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Key performance indicators row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KPICard(
                        title = "Gross Approved",
                        value = "$${totalProjectedRevenue.toInt()}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    KPICard(
                        title = "Cash Deposited",
                        value = "$${depositCollected.toInt()}",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    KPICard(
                        title = "Pending Bookings",
                        value = "$${pendingInvoices.toInt()}",
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Custom Analytics Visual Canvas Chart (Popularity of Wedding Halls)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Halls Booking Popularity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ratio of approved vs pending events",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        // Render Canvas Visual Performance Chart (M3 styled bars)
                        val bCountBallroom = bookings.count { it.hallId == "hall_1" }
                        val bCountGarden = bookings.count { it.hallId == "hall_2" }
                        val bCountPavilion = bookings.count { it.hallId == "hall_3" }
                        val maxCount = maxOf(bCountBallroom, bCountGarden, bCountPavilion, 1)

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            BarMetric(
                                label = "The Grand Ballroom",
                                count = bCountBallroom,
                                maxCount = maxCount,
                                barColor = MaterialTheme.colorScheme.primary
                            )
                            BarMetric(
                                label = "The Royal Gardens",
                                count = bCountGarden,
                                maxCount = maxCount,
                                barColor = MaterialTheme.colorScheme.secondary
                            )
                            BarMetric(
                                label = "The Glass Pavilion",
                                count = bCountPavilion,
                                maxCount = maxCount,
                                barColor = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            // Client Conversion funnel statistics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Conversion Performance Funnel", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val totalInquiries = bookings.size + 12 // Simulated inquiry base
                        FunnelRow(stage = "1. AI Support Chat Inquiries", value = totalInquiries, percentage = 100)
                        FunnelRow(stage = "2. Automated Calendar Bookings Requested", value = bookings.size, percentage = (bookings.size.toFloat() / totalInquiries * 100).toInt())
                        FunnelRow(stage = "3. Approved Weddings Organized", value = approvedBookings.size, percentage = (approvedBookings.size.toFloat() / totalInquiries * 100).toInt())
                    }
                }
            }
        }
    }
}

@Composable
fun KPICard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun BarMetric(
    label: String,
    count: Int,
    maxCount: Int,
    barColor: Color
) {
    val barRatio = count.toFloat() / maxCount.toFloat()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("$count bookings", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        Spacer(modifier = Modifier.height(6.dp))
        
        // Custom draw bar background & filling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = barRatio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(barColor.copy(alpha = 0.7f), barColor)
                        )
                    )
            )
        }
    }
}

@Composable
fun FunnelRow(stage: String, value: Int, percentage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stage, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(
            text = "$value ($percentage%)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


// ==========================================
// 5. REAL-TIME NOTIFICATIONS & LOGS TAB
// ==========================================
@Composable
fun AlertNotificationsTab(viewModel: ElysianViewModel) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "System Alerts & Log Hub",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Push update center and historical venue activity logs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            TextButton(onClick = { viewModel.clearNotifications() }) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Logs", fontSize = 11.sp)
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your feed is fully serene.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("New bookings, vendor milestones, or system activities show here.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(notifications) { notif ->
                    NotificationCard(notification = notif, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification, viewModel: ElysianViewModel) {
    val icon = when (notification.category) {
        "System" -> Icons.Default.SettingsSuggest
        "Booking" -> Icons.Default.CalendarMonth
        "Chat" -> Icons.Default.ChatBubble
        else -> Icons.Default.TaskAlt
    }
    val iconTint = when (notification.category) {
        "System" -> MaterialTheme.colorScheme.primary
        "Booking" -> Color(0xFF2196F3) // Blue
        "Chat" -> Color(0xFF9C27B0) // Purple
        else -> Color(0xFF4CAF50) // Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_${notification.id}")
            .clickable { viewModel.markNotificationAsRead(notification.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (notification.isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconTint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(notification.timestamp)),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// ==========================================
// CUSTOM VECTOR CANVAS RENDERING FUNCTIONS
// ==========================================

@Composable
fun ChandelierCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Luxurious burgundy back radial glowing background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF8A2E50), Color(0xFF421025)),
                center = Offset(width / 2f, 0f),
                radius = width * 0.8f
            )
        )

        // Draw support chain
        drawLine(
            color = Color(0xFFD4AF37),
            start = Offset(width / 2f, 0f),
            end = Offset(width / 2f, height * 0.35f),
            strokeWidth = 2.dp.toPx()
        )

        // Draw main luxury chandelier ring
        drawOval(
            color = Color(0xFFD4AF37),
            topLeft = Offset(width / 2f - 24.dp.toPx(), height * 0.35f),
            size = Size(48.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Draw hanging crystals representing light reflection
        drawCircle(
            color = Color(0xFFFFFDFC),
            radius = 3.dp.toPx(),
            center = Offset(width / 2f, height * 0.42f)
        )
        drawCircle(
            color = Color(0xFFFFFDFC),
            radius = 2.dp.toPx(),
            center = Offset(width / 2f - 16.dp.toPx(), height * 0.40f)
        )
        drawCircle(
            color = Color(0xFFFFFDFC),
            radius = 2.dp.toPx(),
            center = Offset(width / 2f + 16.dp.toPx(), height * 0.40f)
        )
    }
}

@Composable
fun GardenCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Elegant twilight garden green-purple background
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF2E6B4E), Color(0xFF1E352F)),
                start = Offset(0f, height),
                end = Offset(width, 0f)
            )
        )

        // Draw romantic floral arch
        val path = Path().apply {
            moveTo(width * 0.2f, height)
            cubicTo(
                width * 0.2f, height * 0.2f,
                width * 0.8f, height * 0.2f,
                width * 0.8f, height
            )
        }
        drawPath(
            path = path,
            color = Color(0xFFE8B6BC).copy(alpha = 0.5f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw glowing fairy light dots
        val random = Random(42)
        for (i in 0..6) {
            val progress = i.toFloat() / 6f
            val x = width * 0.2f + (width * 0.6f * progress)
            val y = height * 0.6f - (height * 0.35f * (1f - (progress - 0.5f) * (progress - 0.5f) * 4f))
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 3.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun LakePavilionCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Gorgeous sunset lakeshore background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF9E80), Color(0xFF9E5D6A), Color(0xFF2E1C2E))
            )
        )

        // Draw sunset sun glowing
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 12.dp.toPx(),
            center = Offset(width / 2f, height * 0.5f)
        )

        // Draw lake water horizon line
        drawLine(
            color = Color(0xFF4A148C).copy(alpha = 0.6f),
            start = Offset(0f, height * 0.7f),
            end = Offset(width, height * 0.7f),
            strokeWidth = 1.dp.toPx()
        )

        // Draw glass pavilion converging architectural frame lines
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(width * 0.3f, height),
            end = Offset(width * 0.45f, height * 0.3f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(width * 0.7f, height),
            end = Offset(width * 0.55f, height * 0.3f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(width * 0.45f, height * 0.3f),
            end = Offset(width * 0.55f, height * 0.3f),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}
