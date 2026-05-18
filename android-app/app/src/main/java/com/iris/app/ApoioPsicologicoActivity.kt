package com.iris.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.iris.app.data.Direito

class ApoioPsicologicoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apoio_psicologico)

        val recyclerView = findViewById<RecyclerView>(R.id.apoioRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<MaterialButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Reutilizando a data class Direito por ter a mesma estrutura (title, description)
        val listaApoio = listOf(
            Direito(
                "Acolhimento Emocional",
                "Espaço seguro para expressar sentimentos e medos sem julgamentos, fundamental após situações de trauma."
            ),
            Direito(
                "Superação do Trauma",
                "Psicoterapia focada em reduzir os impactos psicológicos causados pela violência e recuperar a autoestima."
            ),
            Direito(
                "Fortalecimento da Autonomia",
                "Auxílio no processo de retomada do controle da própria vida e tomada de decisões independentes."
            ),
            Direito(
                "Grupos de Apoio",
                "Troca de experiências com outras mulheres que passaram por situações semelhantes, combatendo o isolamento."
            ),
            Direito(
                "Rede de Proteção",
                "Orientação sobre como identificar ciclos de violência e estabelecer limites saudáveis em relacionamentos."
            ),
            Direito(
                "Apoio aos Filhos",
                "Acompanhamento psicológico para dependentes que presenciaram ou sofreram violência no ambiente familiar."
            ),
            Direito(
                "Centro de Referência (CRAM)",
                "Atendimento psicossocial especializado oferecido pelo governo para mulheres em situação de violência."
            )
        )

        recyclerView.adapter = DireitosAdapter(listaApoio)
    }
}
