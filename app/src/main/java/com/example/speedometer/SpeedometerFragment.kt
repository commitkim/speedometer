package com.example.speedometer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.speedometer.databinding.FragmentSpeedoMeterBinding

class SpeedoMeterFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentSpeedoMeterBinding
    private val locationManager by lazy { activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private var preventScreen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedoMeterBinding.inflate(LayoutInflater.from(requireContext()))
        binding.imageSpeedometer.indicator.color = Color.RED
        binding.imageButton.setOnClickListener {
            setPreventScreen(preventScreen)
        }

        startSpeedometer()

        return binding.root
    }

    private fun setPreventScreen(preventScreen: Boolean) {
        if(!preventScreen) {
            this.preventScreen = true
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.imageButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_power_on, null))
            showToast("화면 꺼짐 방지")
        } else {
            this.preventScreen = false
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.imageButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_power_off, null))
            showToast("화면 꺼짐 방지 해제")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Boolean =
        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun showPermissionToast() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(context, "please approve fine location permission", Toast.LENGTH_SHORT).show()
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(context, "please approve coarse location permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startSpeedometer() {
        if(checkPermission()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        } else {
            showPermissionToast()
            checkPermission()
        }
    }

    override fun onLocationChanged(p0: Location) {
        binding.imageSpeedometer.speedTo(p0.speed * 3600 / 1000)
    }

    override fun onResume() {
        super.onResume()
        if(checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        }
        setPreventScreen(!preventScreen)
    }
}