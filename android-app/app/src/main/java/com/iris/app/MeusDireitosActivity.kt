package com.iris.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.iris.app.data.Direito

class MeusDireitosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_direitos)

        val recyclerView = findViewById<RecyclerView>(R.id.direitosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<MaterialButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        val listaDireitos = listOf(
            Direito(
                "Lei Maria da Penha",
                "Principal legislação para prevenir e punir a violência doméstica e familiar contra a mulher no Brasil."
            ),
            Direito(
                "Medidas Protetivas de Urgência",
                "Podem ser solicitadas à justiça para afastar o agressor do lar e proibir qualquer tipo de contato com a vítima."
            ),
            Direito(
                "Atendimento Especializado",
                "Direito a atendimento humanizado em Delegacias Especializadas de Atendimento à Mulher (DEAM) e serviços de saúde."
            ),
            Direito(
                "Assistência Jurídica",
                "A vítima tem direito a orientação jurídica e defesa gratuita realizada pela Defensoria Pública."
            ),
            Direito(
                "Sigilo de Dados",
                "Proteção do nome e endereço da mulher e seus dependentes para garantir sua segurança e evitar novas agressões."
            ),
            Direito(
                "Estabilidade Trabalhista",
                "Manutenção do vínculo de emprego por até seis meses quando for necessário o afastamento do local de trabalho."
            ),
            Direito(
                "Acesso à Moradia",
                "Direito a ser encaminhada para casas-abrigo ou programas de moradia se estiver em risco de morte."
            )
        )

        recyclerView.adapter = DireitosAdapter(listaDireitos)
    }
}
