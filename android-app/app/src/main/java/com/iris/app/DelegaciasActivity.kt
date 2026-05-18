package com.iris.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.iris.app.data.Direito

class DelegaciasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delegacias)

        val recyclerView = findViewById<RecyclerView>(R.id.delegaciasRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<MaterialButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        val listaDelegacias = listOf(
            Direito(
                "DECCM - Centro-Sul (Plantão 24h)",
                "Av. Mário Ypiranga Monteiro, s/nº - Parque Dez. Referência para atendimento de emergência a qualquer hora."
            ),
            Direito(
                "DECCM - Norte/Leste",
                "Av. Nossa Senhora da Conceição, s/nº - Cidade de Deus. Atende ocorrências das zonas Norte e Leste de Manaus."
            ),
            Direito(
                "DECCM - Sul/Oeste",
                "Rua Desembargador Felismino Soares, s/nº - Colônia Oliveira Machado. Atende as zonas Sul e Oeste."
            ),
            Direito(
                "Ronda Maria da Penha",
                "Serviço da Polícia Militar para acompanhamento de mulheres com medidas protetivas. Telefone de emergência: 190."
            ),
            Direito(
                "Disque 180",
                "Central de Atendimento à Mulher. Canal gratuito e anônimo para denúncias de violência e orientações em todo o país."
            ),
            Direito(
                "Delegacia Virtual",
                "Para ocorrências que não exijam exame de corpo de delito, é possível registrar o B.O. online pelo site da Polícia Civil do AM."
            )
        )

        recyclerView.adapter = DireitosAdapter(listaDelegacias)
    }
}
