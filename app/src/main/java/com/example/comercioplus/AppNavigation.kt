package com.example.comercioplus

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.comercioplus.model.Role

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel, userViewModel: UserViewModel) {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()

    val factory = ViewModelFactory(productViewModel, userViewModel)

    val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
    val aiViewModel: AiViewModel = viewModel()

    val storeViewModel: StoreViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )

    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = "role_selection") {
        composable("role_selection") {
            RoleSelectionScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("main") {
            MainScreen(
                mainNavController = navController,
                categoryViewModel = categoryViewModel,
                storeViewModel = storeViewModel,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                userViewModel = userViewModel,
                themeViewModel = themeViewModel,
                dashboardViewModel = dashboardViewModel
            )
        }
        composable(
            route = "auth/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) {
            val roleString = it.arguments?.getString("role")
            val role = Role.valueOf(roleString ?: Role.CLIENTE.name)
            LoginScreen(navController = navController, userViewModel = userViewModel, authViewModel = authViewModel, role = role)
        }
        composable("user_main") {
            UserMainScreen(
                mainNavController = navController,
                productViewModel = productViewModel,
                categoryViewModel = categoryViewModel,
                cartViewModel = cartViewModel,
                userViewModel = userViewModel,
                themeViewModel = themeViewModel,
                storeViewModel = storeViewModel,
                dashboardViewModel = dashboardViewModel
            )
        }
        composable("ia_reports") {
            AiReportsScreen(
                navController = navController,
                aiViewModel = aiViewModel,
                dashboardViewModel = dashboardViewModel,
                productViewModel = productViewModel
            )
        }
        composable("checkout") {
            CheckoutScreen(navController = navController, cartViewModel = cartViewModel)
        }
        composable("scanner") {
            ScannerScreen(
                onResult = { sku ->
                    navController.navigate("add_product_with_sku/$sku") {
                        popUpTo("main")
                    }
                },
                onClose = { navController.popBackStack() }
            )
        }
        composable("edit_store") {
            EditStoreScreen(navController = navController, storeViewModel = storeViewModel)
        }
        composable("categories") {
            AddCategoryScreen(navController = navController, viewModel = categoryViewModel)
        }
        composable(
            route = "edit_product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            EditProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                categoryViewModel = categoryViewModel,
                userViewModel = userViewModel,
                productId = productId
            )
        }
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                userViewModel = userViewModel
            )
        }
        composable("add_product") {
            AddProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                categoryViewModel = categoryViewModel,
                userViewModel = userViewModel,
                storeViewModel = storeViewModel
            )
        }
        composable(
            route = "add_product_with_sku/{sku}",
            arguments = listOf(navArgument("sku") { type = NavType.StringType })
        ) { backStackEntry ->
            val sku = backStackEntry.arguments?.getString("sku") ?: ""
            AddProductScreen(
                navController = navController,
                productViewModel = productViewModel,
                categoryViewModel = categoryViewModel,
                userViewModel = userViewModel,
                initialSku = sku,
                storeViewModel = storeViewModel
            )
        }
    }
}
