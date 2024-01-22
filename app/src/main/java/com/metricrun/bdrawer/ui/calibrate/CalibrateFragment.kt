package com.metricrun.bdrawer.ui.calibrate

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.metricrun.bdrawer.R
import com.metricrun.bdrawer.BluetoothConnectionService

class CalibrateFragment : Fragment() {

    private lateinit var viewModel: CalibrateViewModel
    private lateinit var bluetoothService: BluetoothConnectionService
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private lateinit var scanButton: Button

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val SCAN_DELAY = 10000L // 10 segundos
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calibrate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CalibrateViewModel::class.java]
        bluetoothService = BluetoothConnectionService(requireContext())
        setupDeviceList()
        setupScanButton(view)
    }

    private fun setupDeviceList() {
        deviceListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
    }

    private fun setupScanButton(view: View) {
        scanButton = view.findViewById(R.id.button_scan)
        scanButton.setOnClickListener {
            startBluetoothScan()
        }
    }

    private fun startBluetoothScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bluetoothService.startScan()
            view?.postDelayed({ checkForDevicesAndShowDialog() }, SCAN_DELAY)
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    bluetoothService.startScan()
                } else {
                    // Permiso denegado, manejar adecuadamente.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkForDevicesAndShowDialog() {
        val devices = bluetoothService.getDevices()
        if (devices.isNotEmpty()) {
            showDevicesDialog()
        } else {
            // Mostrar un mensaje si no se encontraron dispositivos
        }
    }

    @SuppressLint("MissingPermission")
    private fun showDevicesDialog() {
        val devices = bluetoothService.getDevices()
        deviceListAdapter.clear()
        devices.forEach { device ->
            deviceListAdapter.add("${device.name} - ${device.address}")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Seleccione un dispositivo METRICRUN")
            .setAdapter(deviceListAdapter) { _, which ->
                val selectedDevice = devices[which]
                // Aquí puedes realizar la conexión con el dispositivo seleccionado
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bluetoothService.close()
    }
}
