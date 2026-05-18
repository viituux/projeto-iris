package com.iris.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.iris.app.data.SessionManager
import com.iris.app.data.UserProfile
import com.iris.app.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var welcomeText: TextView

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val sosHoldHandler = Handler(Looper.getMainLooper())
    private var sosTriggeredByHold = false

    private var mediaRecorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private val recordingDuration = 10000L // 10 segundos de gravação

    private val sosHoldRunnable = Runnable {
        sosTriggeredByHold = true
        checkPermissionsAndStartSos()
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

            if (locationGranted && audioGranted) {
                startSosFlow()
            } else {
                Toast.makeText(this, "Permissões necessárias negadas.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        welcomeText = findViewById(R.id.welcomeText)

        loadProfile()

        findViewById<MaterialButton>(R.id.antecedentesButton).setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Informação")
                .setMessage("Essa funcionalidade está em desenvolvimento")
                .setPositiveButton("OK", null)
                .show()
        }
        findViewById<MaterialButton>(R.id.apoioPsicologicoButton).setOnClickListener {
            startActivity(Intent(this, ApoioPsicologicoActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.delegaciasButton).setOnClickListener {
            startActivity(Intent(this, DelegaciasActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.direitosButton).setOnClickListener {
            startActivity(Intent(this, MeusDireitosActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            SessionManager(this).clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<MaterialButton>(R.id.goToAddContactButton).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.sosButton).setOnClickListener {
            checkPermissionsAndStartSos()
        }
    }

    private fun loadProfile() {
        ApiClient.create(this).getMe().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    welcomeText.text = profile.username
                }
            }
            override fun onFailure(call: Call<UserProfile>, t: Throwable) {}
        })
    }

    private fun checkPermissionsAndStartSos() {
        val hasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        if (hasLocation && hasAudio) {
            startSosFlow()
        } else {
            requestPermissionsLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
            ))
        }
    }

    private fun startSosFlow() {
        Toast.makeText(this, "Gravando áudio de emergência (10s)...", Toast.LENGTH_SHORT).show()
        startRecording()
        
        Handler(Looper.getMainLooper()).postDelayed({
            stopRecording()
            Handler(Looper.getMainLooper()).postDelayed({
                activateSosWithCurrentLocation()
            }, 800)
        }, recordingDuration)
    }

    private fun startRecording() {
        try {
            val timestamp = System.currentTimeMillis()
            currentAudioFile = File(externalCacheDir, "sos_$timestamp.m4a")
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(currentAudioFile?.absolutePath)
                prepare()
                start()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Erro ao iniciar microfone.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            mediaRecorder = null
        }
    }

    private fun activateSosWithCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val lat = location?.latitude ?: -3.119028
            val lon = location?.longitude ?: -60.021731
            sendSosMultipart(lat, lon)
        }.addOnFailureListener {
            sendSosMultipart(-3.119028, -60.021731)
        }
    }

    private fun sendSosMultipart(lat: Double, lon: Double) {
        val api = ApiClient.create(this)
        
        // Formata as coordenadas para no máximo 6 casas decimais (exigência do backend)
        val latStr = String.format(java.util.Locale.US, "%.6f", lat)
        val lonStr = String.format(java.util.Locale.US, "%.6f", lon)

        val descPart = "Alerta SOS IRIS - Captura de Áudio".toRequestBody("text/plain".toMediaTypeOrNull())
        val latPart = latStr.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonPart = lonStr.toRequestBody("text/plain".toMediaTypeOrNull())
        
        val audioPart = currentAudioFile?.takeIf { it.exists() && it.length() > 0 }?.let { file ->
            val requestFile = file.asRequestBody("audio/mp4".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("audio", file.name, requestFile)
        }

        api.activateSos(descPart, latPart, lonPart, audioPart).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "SOS e áudio enviados com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    val errorDetail = response.errorBody()?.string() ?: ""
                    Toast.makeText(this@MainActivity, "Erro 400: $errorDetail", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        sosHoldHandler.removeCallbacks(sosHoldRunnable)
        stopRecording()
    }
}
