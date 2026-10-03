package com.example.project_caxfix

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.example.project_caxfix.data.ReportDao
import com.example.project_caxfix.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var reportDao: ReportDao

    // Lista completa de todos los reportes cargados desde SQLite
    private var allReports: List<Report> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportDao = ReportDao(this)

        setupRecyclerView()
        setupChipFilters()
        setupBottomNavigation()

        binding.fabNewReport.setOnClickListener {
            val bottomSheet = NewReportBottomSheet().apply {
                onReportCreatedListener = object : NewReportBottomSheet.OnReportCreatedListener {
                    override fun onReportCreated() {
                        loadReports()
                    }
                }
            }
            bottomSheet.show(supportFragmentManager, NewReportBottomSheet.TAG)
        }

        // Cargar reportes iniciales almacenados en SQLite
        loadReports()
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(emptyList())
        binding.rvFeed.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = reportAdapter
        }
    }

    private fun setupChipFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, _ ->
            applyCategoryFilter()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_reports -> {
                    // Permanece en HomeActivity y muestra el feed actual
                    true
                }
                R.id.nav_activity -> {
                    Toast.makeText(this, "Módulo de actividad en desarrollo", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil ciudadano en desarrollo", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadReports() {
        allReports = reportDao.getAllReports()
        applyCategoryFilter()
    }

    private fun applyCategoryFilter() {
        val checkedChipId = binding.chipGroupFilters.checkedChipId

        if (checkedChipId == View.NO_ID || checkedChipId == binding.chipAll.id) {
            reportAdapter.updateReports(allReports)
            return
        }

        val selectedChip = binding.chipGroupFilters.findViewById<Chip>(checkedChipId)
        val categoryText = selectedChip?.text?.toString() ?: "Todos"

        if (categoryText == "Todos") {
            reportAdapter.updateReports(allReports)
        } else {
            val filteredReports = allReports.filter { 
                it.category.equals(categoryText, ignoreCase = true) 
            }
            reportAdapter.updateReports(filteredReports)
        }
    }
}