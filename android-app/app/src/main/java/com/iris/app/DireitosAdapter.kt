package com.iris.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iris.app.data.Direito

class DireitosAdapter(private val lista: List<Direito>) :
    RecyclerView.Adapter<DireitosAdapter.DireitoViewHolder>() {

    class DireitoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.direitoTitle)
        val description: TextView = view.findViewById(R.id.direitoDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DireitoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_direito, parent, false)
        return DireitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DireitoViewHolder, position: Int) {
        val direito = lista[position]
        holder.title.text = direito.title
        holder.description.text = direito.description
    }

    override fun getItemCount() = lista.size
}
