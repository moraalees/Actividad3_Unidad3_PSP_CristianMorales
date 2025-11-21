package com.example.ej5_tema3_pmdm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.math.BigDecimal


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                AppNavHost()
            }
        }
    }
}


data class Tipo(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

data class Camisa(
    val id: Int,
    val nombre: String,
    val talla: String,
    val color: String,
    val precio: Double,
    val imagenUrl: String?,
    val lat: Double?,
    val lng: Double?,
    val tipo: Tipo
)

data class CamisaRequest(
    val nombre: String,
    val talla: String? = null,
    val color: String? = null,
    val precio: BigDecimal? = null,
    val imagenUrl: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val tipoId: Long
)

fun Camisa.toRequest(): CamisaRequest {
    return CamisaRequest(
        nombre = this.nombre,
        talla = this.talla,
        color = this.color,
        precio = this.precio?.toBigDecimal(),
        imagenUrl = this.imagenUrl,
        lat = this.lat,
        lng = this.lng,
        tipoId = this.tipo.id.toLong()
    )
}

object RetrofitInstance {
    private const val BASE_URL = "https://actividad3-unidad3-psp-cristianmorales.onrender.com/"

    val api: CamisaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CamisaApi::class.java)
    }
}

interface CamisaApi {
    @GET("api/camisas")
    suspend fun getCamisas(): List<Camisa>

    @POST("api/camisas")
    suspend fun addCamisa(@Body camisa: CamisaRequest): Camisa

    @PUT("api/camisas/{id}")
    suspend fun updateCamisa(@Path("id") id: Int, @Body camisa: CamisaRequest): Camisa

    @DELETE("api/camisas/{id}")
    suspend fun deleteCamisa(@Path("id") id: Int)
}


@Composable
fun LoginScreen(
    auth: FirebaseAuth = Firebase.auth,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onLoginSuccess() }
                    .addOnFailureListener { error = it.localizedMessage }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        error?.let { Text(it, color = Color.Red) }
    }
}

@Composable
fun CamisaListScreen(
    viewModel: CamisaViewModel = viewModel()
) {
    val camisas by viewModel.camisas.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var editingCamisa by remember { mutableStateOf<Camisa?>(null) }

    Column {
        Button(onClick = {
            editingCamisa = null
            showForm = true
        }) {
            Text("Añadir Camisa")
        }

        if (showForm) {
            CamisaForm(
                camisa = editingCamisa,
                viewModel = viewModel,
                onCancel = { showForm = false }
            )
        }

        LazyColumn {
            items(camisas) { camisa ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = camisa.imagenUrl,
                            contentDescription = camisa.nombre,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(camisa.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("Talla: ${camisa.talla}")
                            Text("Color: ${camisa.color}")
                            Text("Precio: $${camisa.precio}")
                            Text("Tipo: ${camisa.tipo.nombre}")
                        }
                        Column {
                            Button(onClick = {
                                editingCamisa = camisa
                                showForm = true
                            }) { Text("Editar") }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.deleteCamisa(camisa.id) }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}


class CamisaViewModel : ViewModel() {
    private val _camisas = MutableStateFlow<List<Camisa>>(emptyList())
    val camisas: StateFlow<List<Camisa>> = _camisas

    init { getCamisas() }

    fun getCamisas() {
        viewModelScope.launch {
            try {
                _camisas.value = RetrofitInstance.api.getCamisas()
            } catch (e: Exception) {
                Log.e("CamisaViewModel", "Error al obtener camisas: ${e.localizedMessage}")
            }
        }
    }

    fun addCamisa(camisa: Camisa, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.addCamisa(camisa.toRequest())
                getCamisas()
                onComplete(true, "Camisa añadida correctamente")
            } catch (e: Exception) {
                onComplete(false, "Error al añadir: ${e.localizedMessage}")
                Log.e("CamisaViewModel", "Add error: ${e.localizedMessage}")
            }
        }
    }

    fun updateCamisa(camisa: Camisa, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.updateCamisa(camisa.id, camisa.toRequest())
                getCamisas()
                onComplete(true, "Camisa actualizada correctamente")
            } catch (e: Exception) {
                onComplete(false, "Error al actualizar: ${e.localizedMessage}")
                Log.e("CamisaViewModel", "Update error: ${e.localizedMessage}")
            }
        }
    }


    fun deleteCamisa(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.deleteCamisa(id)
                getCamisas()
            } catch (e: Exception) {
                Log.e("CamisaViewModel", "Delete error: ${e.localizedMessage}")
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("camisas") })
        }
        composable("camisas") { CamisaListScreen() }
    }
}

@Composable
fun CamisaForm(
    camisa: Camisa? = null,
    viewModel: CamisaViewModel = viewModel(),
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(camisa?.nombre ?: "") }
    var talla by remember { mutableStateOf(camisa?.talla ?: "") }
    var color by remember { mutableStateOf(camisa?.color ?: "") }
    var precio by remember { mutableStateOf(camisa?.precio?.toString() ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(nombre, { nombre = it }, label = { Text("Nombre") })
        TextField(talla, { talla = it }, label = { Text("Talla") })
        TextField(color, { color = it }, label = { Text("Color") })
        TextField(precio, { precio = it }, label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = {
                val tipoValido = camisa?.tipo ?: Tipo(id = 1, nombre = "Default", descripcion = "")

                val nuevaCamisa = Camisa(
                    id = camisa?.id ?: 0,
                    nombre = nombre,
                    talla = talla,
                    color = color,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    imagenUrl = null,
                    lat = null,
                    lng = null,
                    tipo = tipoValido
                )

                if (camisa == null) {
                    viewModel.addCamisa(nuevaCamisa) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) onCancel()
                    }
                } else {
                    viewModel.updateCamisa(nuevaCamisa) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) onCancel()
                    }
                }
            }){
                Text("Guardar")
            }

            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onCancel) { Text("Cancelar") }
        }
    }
}