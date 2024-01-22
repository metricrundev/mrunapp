package com.metricrun.bdrawer

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import java.util.*
import android.util.Log

class BluetoothConnectionService(private val context: Context) {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null
    private val devices = mutableListOf<BluetoothDevice>()

    // UUIDs para el servicio y características específicas que tu dispositivo ofrece
    private val SERVICE_UUID: UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
    private val CHARACTERISTIC_UUID_A0: UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
    private val CHARACTERISTIC_UUID_A1: UUID = UUID.fromString("beb5483f-36e1-4688-b7f5-ea07361b26a9")

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (device.name == "METRICRUN") {
                        devices.add(device)
                        // Puedes agregar un log aquí si deseas
                        Log.d("BluetoothService", "Dispositivo METRICRUN encontrado: ${device.name} - ${device.address}")
                    }
                }
            }
        }
    }

    init {
        // Registrar el BroadcastReceiver
        Log.d("BluetoothService", "BluetoothConnectionService iniciado")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        Log.d("BluetoothService", "Iniciando escaneo de Bluetooth")
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                bluetoothGatt = null
                // Manejar desconexión aquí
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Aquí puedes suscribirte a las notificaciones de las características
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Manejar la lectura de la característica aquí
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            // Manejar los cambios en la característica aquí
        }
    }

    @SuppressLint("MissingPermission")
    fun close() {
        Log.d("BluetoothService", "Cerrando BluetoothConnectionService")
        bluetoothGatt?.close()
        bluetoothGatt = null
        context.unregisterReceiver(receiver)
    }

    fun getDevices(): List<BluetoothDevice> {
        return devices
    }
}

