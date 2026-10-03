package com.example.project_caxfix

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.project_caxfix.data.ReportDao
import com.example.project_caxfix.databinding.BottomSheetNewReportBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewReportBottomSheet : BottomSheetDialogFragment() {

    interface OnReportCreatedListener {
        fun onReportCreated()
    }

    var onReportCreatedListener: OnReportCreatedListener? = null

    private var _binding: BottomSheetNewReportBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    // Usar OpenDocument + takePersistableUriPermission para conservar permiso persistente de la imagen
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedImageUri = uri
            binding.btnAttachPhoto.text = "Foto adjuntada"
            Toast.makeText(requireContext(), "Imagen adjuntada correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetNewReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = arrayOf(
            "Baches y Pistas",
            "Alumbrado Público",
            "Limpieza y Residuos",
            "Seguridad Ciudadana",
            "Agua y Saneamiento",
            "Parques y Jardines",
            "Ordenamiento de Tránsito"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actCategory.setAdapter(adapter)

        binding.btnAttachPhoto.setOnClickListener {
            selectImageLauncher.launch(arrayOf("image/*"))
        }

        binding.btnSubmitReport.setOnClickListener {
            val title = binding.etReportTitle.text.toString().trim()
            val category = binding.actCategory.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (title.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos del reporte", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newReport = Report(
                authorName = "Cristopher Cruzado",
                neighborhood = "Cajamarca",
                status = "Pendiente",
                title = title,
                category = category,
                description = description,
                imageUri = selectedImageUri?.toString(),
                createdAt = System.currentTimeMillis(),
                supportCount = 0,
                commentCount = 0
            )

            val reportDao = ReportDao(requireContext())
            val newRowId = reportDao.insertReport(newReport)

            if (newRowId > -1L) {
                Toast.makeText(requireContext(), "Reporte publicado correctamente", Toast.LENGTH_SHORT).show()
                onReportCreatedListener?.onReportCreated()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Error al guardar el reporte en SQLite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NewReportBottomSheet"
    }
}