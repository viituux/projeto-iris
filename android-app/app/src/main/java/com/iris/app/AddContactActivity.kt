package com.iris.app

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.iris.app.data.ApiEnvelope
import com.iris.app.data.CreateContactRequest
import com.iris.app.data.EmergencyContact
import com.iris.app.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddContactActivity : AppCompatActivity() {
    private lateinit var contactNameEditText: EditText
    private lateinit var contactPhoneEditText: EditText
    private lateinit var contactRelationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        contactNameEditText = findViewById(R.id.contactNameEditText)
        contactPhoneEditText = findViewById(R.id.contactPhoneEditText)
        contactRelationEditText = findViewById(R.id.contactRelationEditText)

        findViewById<MaterialButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.saveContactButton).setOnClickListener {
            saveContact()
        }
    }

    private fun saveContact() {
        val name = contactNameEditText.text?.toString()?.trim().orEmpty()
        val phone = contactPhoneEditText.text?.toString()?.trim().orEmpty()
        val relation = contactRelationEditText.text?.toString()?.trim().orEmpty()

        if (name.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Informe nome e telefone.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateContactRequest(
            name = name,
            phone = phone,
            email = null,
            relationship = if (relation.isBlank()) null else relation,
            is_primary = true
        )

        ApiClient.create(this).createContact(request).enqueue(object : Callback<ApiEnvelope<EmergencyContact>> {
            override fun onResponse(
                call: Call<ApiEnvelope<EmergencyContact>>,
                response: Response<ApiEnvelope<EmergencyContact>>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddContactActivity, "Contato salvo com sucesso.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddContactActivity, "Erro ao salvar contato: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiEnvelope<EmergencyContact>>, t: Throwable) {
                Toast.makeText(this@AddContactActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
