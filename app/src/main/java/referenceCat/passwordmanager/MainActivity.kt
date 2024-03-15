package referenceCat.passwordmanager

import android.app.Activity
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.widget.Toast
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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
import java.util.concurrent.Executor

enum class Route(
    val showAppBar: Boolean = true,
    val canNavigateBack: Boolean = false,
    val titleStringId: Int = R.string.app_name
) {
    RegistrationRoute(showAppBar = false),
    LoginRoute(showAppBar = false),
    ListRoute(titleStringId = R.string.list_screen_title),
    EntryEditRoute(canNavigateBack = true, titleStringId = R.string.edit_screen_title),
}

class MainActivity : FragmentActivity() {


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
    val currentScreen = Route.valueOf(
        backStackEntry?.destination?.route ?: if (PasswordsStorage.getInstance()
                .isMasterPasswordInitiated(LocalContext.current)
        ) Route.LoginRoute.name else Route.RegistrationRoute.name,
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
    currentScreen: Route,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    if (currentScreen.showAppBar) {
        TopAppBar(
            title = { Text(stringResource(id = currentScreen.titleStringId)) },
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
    var startDestination = Route.RegistrationRoute.name

    if (PasswordsStorage.getInstance().isMasterPasswordInitiated(LocalContext.current))
        startDestination = Route.LoginRoute.name

    if (PasswordsStorage.getInstance().isMasterPasswordApplied())
        startDestination = Route.ListRoute.name

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Route.RegistrationRoute.name) {
            RegistrationScreen(onSuccessfulRegistration = { navController.navigate(Route.ListRoute.name) })
        }

        composable(route = Route.LoginRoute.name) {
            LoginScreen(
                onSuccessfulLogin = { navController.navigate(Route.ListRoute.name) },
                onClearData = {navController.navigate(Route.RegistrationRoute.name)}
            )
        }

        composable(route = Route.ListRoute.name) {
            val activity = (LocalContext.current as? Activity)
            BackHandler(true) {
                activity?.finish()
            }
            ListScreen(
                onActionButtonClick = {
                    viewModel.clearData()
                    navController.navigate(Route.EntryEditRoute.name)
                },
                onEditClick = { id: Int, name: String, website: String, password: String ->
                    viewModel.setData(id, name, website, password)
                    navController.navigate(Route.EntryEditRoute.name)
                }
            )
        }

        composable(route = Route.EntryEditRoute.name) {
            EditScreen(id = viewModel.id.intValue,
                name = viewModel.name.value,
                website = viewModel.website.value,
                password = viewModel.password.value,
                onSave = { navController.navigate(Route.ListRoute.name) })
        }
    }
}