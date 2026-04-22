package A51388.spinnet.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.NeonGreen
import A51388.spinnet.ui.theme.OnSurfaceVariant
import A51388.spinnet.ui.theme.SurfaceContainer

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val destination: SpinNetDestination
)

val navItems = listOf(
    NavItem("Training",  Icons.Default.FitnessCenter, SpinNetDestination.Dashboard),
    NavItem("Planner",   Icons.Default.GridView,      SpinNetDestination.RoutinePlanner),
    NavItem("Stats",     Icons.Default.BarChart,       SpinNetDestination.Performance),
    NavItem("Community", Icons.Default.People,          SpinNetDestination.Community),
)

@Composable
fun SpinNetBottomBar(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceContainer,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            val selected = currentDestination == item.destination
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label.uppercase(),
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = NeonGreen,
                    selectedTextColor   = NeonGreen,
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant,
                    indicatorColor      = Color(0xFF1A2E20)
                )
            )
        }
    }
}
