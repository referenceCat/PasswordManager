package referenceCat.passwordmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import referenceCat.passwordmanager.ui.EntryEditScreen
import referenceCat.passwordmanager.ui.ListScreen
import referenceCat.passwordmanager.ui.LoginScreen
import referenceCat.passwordmanager.ui.RegistrationScreen

enum class Routes {
    RegistrationRoute,
    LoginRoute,
    ListRoute,
    EntryEditRoute,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
                referenceCat.passwordmanager.Application()
        }
    }
}

@Preview
@Composable
fun Application () {
    val navController: NavHostController = rememberNavController()
    Scaffold(
        topBar = {
            AppBar(
                canNavigateBack = false,
                showAppBar = true,
                navigateUp = { /* TODO: implement back navigation */ }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.RegistrationRoute.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Routes.RegistrationRoute.name) {
                RegistrationScreen(onSuccessfulRegistration = {navController.navigate(Routes.ListRoute.name)})
            }

            composable(route = Routes.LoginRoute.name) {
                LoginScreen(onSuccessfulLogin = {navController.navigate(Routes.ListRoute.name)})
            }

            composable(route = Routes.ListRoute.name) {
                ListScreen()
            }

            composable(route = Routes.EntryEditRoute.name) {
                EntryEditScreen()
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    showAppBar:Boolean,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showAppBar) {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = modifier,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            }
        )
    }
}