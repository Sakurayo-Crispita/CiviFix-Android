package com.example.project_caxfix

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_caxfix.databinding.ItemReportBinding

class ReportAdapter(
    private var reports: List<Report>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun updateReports(newReports: List<Report>) {
        this.reports = newReports
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        // Validación: Si el reporte carece completamente de título Y descripción, ocultar el elemento
        if (report.title.isBlank() && report.description.isBlank()) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin8dp = (8 * holder.itemView.context.resources.displayMetrics.density).toInt()
                setMargins(margin8dp, margin8dp, margin8dp, margin8dp)
            }
        }

        with(holder.binding) {
            tvAuthorName.text = report.authorName.ifBlank { "Vecino de Cajamarca" }
            
            val location = report.neighborhood.ifBlank { "Cajamarca Centro" }
            val time = report.getFormattedTimeAgo()
            tvNeighborhoodAndTime.text = "$location • $time"

            tvStatusBadge.text = report.status.ifBlank { "En Revisión" }
            tvReportTitle.text = report.title.ifBlank { "Reporte de Incidencia" }
            tvReportDescription.text = report.description.ifBlank { "Descripción no disponible" }
            tvSupportCount.text = "${report.supportCount} apoyos comunitarios"
            tvCommentCount.text = "${report.commentCount} comentarios"

            // Cargar imagen si la Uri no es nula ni vacía
            if (!report.imageUri.isNullOrBlank()) {
                try {
                    ivReportImage.visibility = View.VISIBLE
                    ivReportImage.setImageURI(Uri.parse(report.imageUri))
                } catch (e: Exception) {
                    ivReportImage.visibility = View.GONE
                }
            } else {
                ivReportImage.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = reports.size
}