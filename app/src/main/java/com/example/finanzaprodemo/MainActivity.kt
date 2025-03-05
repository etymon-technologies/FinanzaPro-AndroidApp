package com.example.finanzaProDemo

import CustomerModel
import CustomerResponse
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.finanzaProDemo.ui.theme.JetpackLearningTheme
import com.example.finanzaProDemo.ui.theme.LocalCustomColors
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.example.finanzaProDemo.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false
        setContent {
            JetpackLearningTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    FindCustomerView(navController = rememberNavController()) // Direct call
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FindCustomerView(navController: NavController) {
        val context = LocalContext.current

        var searchText by remember { mutableStateOf("") }
        val customers by remember { mutableStateOf(loadCustomersFromJson(context)) }
        val filteredCustomers = customers.filter {
            val terms = searchText.trim().split(" ")
            terms.all { term ->
                it.name.contains(term, ignoreCase = true) ||
                        it.email?.contains(term, ignoreCase = true) == true ||
                        it.phone1?.contains(term, ignoreCase = true) == true ||
                        it.identificationNumber?.contains(term, ignoreCase = true) == true
            }
        }

        Column (modifier = Modifier.background(LocalCustomColors.current.rowBackgroundColor)) {
            Column (modifier = Modifier.height(120.dp)
                .background(LocalCustomColors.current.headerSectionColor),
                verticalArrangement =  Arrangement.Bottom
            ) {
                Text(text = "Clientes",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )
                CenterAlignedTopAppBar(
                    title = {
                        SearchBar(searchText) { searchText = it }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LocalCustomColors.current.headerSectionColor, // Blue color for the top bar
                        titleContentColor = Color.White
                    )
                )
            }

            if (filteredCustomers.isEmpty() && searchText.isNotEmpty()) {
                Text("No customers found", modifier = Modifier.padding(16.dp))
            } else {
                CustomerList(filteredCustomers, navController)
            }
        }
    }

    @Composable
    fun SearchBar(searchText: String, onSearchChanged: (String) -> Unit) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchChanged,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(40.dp) // Proper height to avoid text cut-off
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(LocalCustomColors.current.headerControlBgColor),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp), // Adjust padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search, // Search icon
                        contentDescription = "Buscar",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp) // Icon size
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                "Buscar...",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                        innerTextField() // TextField content
                    }
                }
            }
        )
    }

    @Composable
    fun CustomerList(customers: List<CustomerModel>, navController: NavController) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
            // Optional padding
        ) {
            items(customers) { customer ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CustomerItem(customer) {
                            navController.navigate("customerDetails/${customer.identificationNumber}")
                        }
                    }
                    HorizontalDivider(color = LocalCustomColors.current.rowSeparatorColor, thickness = 1.dp)
                }
            }
        }
    }

    @Composable
    fun CustomerItem(customer: CustomerModel, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalCustomColors.current.rowBackgroundColor)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        tint = LocalCustomColors.current.iconTintColor,
                        contentDescription = "Customer",
                        modifier = Modifier.size(35.dp).align(Alignment.Top),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        //Customer Name
                        Text(customer.name,
                            color = LocalCustomColors.current.rowPrimaryTextColor,
                            fontSize = 15.sp)
                        // id row
                        customer.identificationNumber?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                it,
                                fontSize = 12.sp,
                                color = LocalCustomColors.current.rowSecondaryTextColor,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        // email row
                        customer.email?.takeIf { it.isNotBlank() }?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Email,
                                    tint = LocalCustomColors.current.iconTintColor,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(18.dp)
                                        .padding(start = 0.dp, top = 1.dp, bottom = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(it,
                                    color = LocalCustomColors.current.rowPrimaryTextColor,
                                    fontSize = 13.sp)
                            }
                        }
                        // phone row
                        customer.phone1?.takeIf { it.isNotBlank() }?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.phone),
                                    tint = LocalCustomColors.current.iconTintColor,
                                    contentDescription = "Phone",
                                    modifier = Modifier.size(18.dp)
                                        .padding(start = 0.dp, top = 1.dp, bottom = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(it,
                                    color = LocalCustomColors.current.rowPrimaryTextColor,
                                    fontSize = 13.sp)
                            }
                        }
                    }
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        tint = LocalCustomColors.current.iconTintColor,
                        contentDescription = "Customer",
                        modifier = Modifier.size(25.dp).align(Alignment.CenterVertically),
                    )
                }
            }
        }

    }

    private fun loadCustomersFromJson(context: Context): List<CustomerModel> {
        return try {
            val inputStream = context.assets.open("customers.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<CustomerResponse>() {}.type
            val response: CustomerResponse = Gson().fromJson(json, type)
            response.data
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    @Composable
    @Preview
    fun PreviewFindCustomerView() {
        FindCustomerView(navController = rememberNavController())
    }
}