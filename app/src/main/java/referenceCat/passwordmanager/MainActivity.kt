package referenceCat.passwordmanager

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import referenceCat.passwordmanager.backend.PasswordsStorage
import referenceCat.passwordmanager.ui.EditScreen
import referenceCat.passwordmanager.ui.EditScreenInitContentViewModel
import referenceCat.passwordmanager.ui.ListScreen
import referenceCat.passwordmanager.ui.LoginScreen
import referenceCat.passwordmanager.ui.RegistrationScreen

enum class Screen(
    val showAppBar: Boolean = true,
    val canNavigateBack: Boolean = false,
) {
    RegistrationRoute(showAppBar = false),
    LoginRoute(showAppBar = false),
    ListRoute(),
    EntryEditRoute(canNavigateBack = true),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Application()
        }
    }
}

@Composable
fun Application(navController: NavHostController = rememberNavController()) {
    val editScreenInitContentViewModel = viewModel<EditScreenInitContentViewModel>()
    val backStackEntry by navController.currentBackStackEntryAsState()
    navController.enableOnBackPressed(false)
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: if (PasswordsStorage.getInstance()
                .isMasterPasswordInitiated(LocalContext.current)
        ) Screen.LoginRoute.name else Screen.RegistrationRoute.name,
    )

    Scaffold(
        topBar = {
            ApplicationTopBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentScreen = currentScreen,

                )
        }
    ) { innerPadding ->
        ApplicationNavigation(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            viewModel = editScreenInitContentViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTopBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    if (currentScreen.showAppBar) {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = modifier,
            navigationIcon = {
                if (canNavigateBack && currentScreen.canNavigateBack) {
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


@Composable
fun ApplicationNavigation(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: EditScreenInitContentViewModel,
) {
    var startDestination = Screen.RegistrationRoute.name

    if (PasswordsStorage.getInstance().isMasterPasswordInitiated(LocalContext.current))
        startDestination = Screen.LoginRoute.name

    if (PasswordsStorage.getInstance().isMasterPasswordApplied())
        startDestination = Screen.ListRoute.name

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.RegistrationRoute.name) {
            RegistrationScreen(onSuccessfulRegistration = { navController.navigate(Screen.ListRoute.name) })
        }

        composable(route = Screen.LoginRoute.name) {
            LoginScreen(onSuccessfulLogin = { navController.navigate(Screen.ListRoute.name) })
        }

        composable(route = Screen.ListRoute.name) {
            val activity = (LocalContext.current as? Activity)
            BackHandler(true) {
                activity?.finish()
            }
            ListScreen(
                onActionButtonClick = {
                    viewModel.clearData()
                    navController.navigate(Screen.EntryEditRoute.name)
                },
                onEditClick = { id: Int, name: String, website: String, password: String ->
                    viewModel.setData(id, name, website, password)
                    navController.navigate(Screen.EntryEditRoute.name)
                }
            )
        }

        composable(route = Screen.EntryEditRoute.name) {
            EditScreen(id = viewModel.id.intValue,
                name = viewModel.name.value,
                website = viewModel.website.value,
                password = viewModel.password.value,
                onSave = { navController.navigate(Screen.ListRoute.name) })
        }
    }
}