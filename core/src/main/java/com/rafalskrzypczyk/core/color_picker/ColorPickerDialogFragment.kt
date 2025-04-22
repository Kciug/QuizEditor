package com.rafalskrzypczyk.core.color_picker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.databinding.LayoutColorPickerBinding


class ColorPickerDialogFragment : DialogFragment() {
    private var _binding: LayoutColorPickerBinding? = null
    private val binding get() = _binding!!

    private val predefinedColors = mapOf(
        "White" to Color.parseColor("#FFFFFF"),
        "Black" to Color.parseColor("#000000"),
        "Scarlet Red" to Color.parseColor("#D32F2F"),
        "Coral" to Color.parseColor("#FF6F61"),
        "Violet" to Color.parseColor("#7E57C2"),
        "Midnight Blue" to Color.parseColor("#303F9F"),
        "Sky Blue" to Color.parseColor("#81D4FA"),
        "Turquoise" to Color.parseColor("#1DE9B6"),
        "Mint" to Color.parseColor("#A5D6A7"),
        "Olive Green" to Color.parseColor("#827717"),
        "Amber" to Color.parseColor("#FFC107"),
        "Burnt Orange" to Color.parseColor("#FF7043")
    )

    private var selectedColor: Int = 0
    var selectedButton: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutColorPickerBinding.inflate(inflater, container, false)
        selectedColor = arguments?.getInt("currentColor") ?: 0

        createPredefinedColorsRadioButtons()
        (binding.buttonSubmit.background as GradientDrawable).setColor(selectedColor)

        val hueColors = IntArray(361) { i ->
            val hsv = floatArrayOf(i.toFloat(), 1f, 1f)
            Color.HSVToColor(hsv)
        }
        val hueGradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            hueColors
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
        }

        val customColorPicker = binding.colorPickerSeekbar
        setCustomColor(selectedColor)
        customColorPicker.progressDrawable = hueGradient
        customColorPicker.splitTrack = false
        customColorPicker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    onCustomColorChanged(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.buttonSubmit.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun createPredefinedColorsRadioButtons() {
        var selectedButton: ImageButton? = null

        predefinedColors.forEach {
            val color = it.value
            val colorButton = ImageButton(requireContext()).apply {
                val backgroundSelected = AppCompatResources.getDrawable(requireContext(), R.drawable.button_color_picker_selected)
                (backgroundSelected as GradientDrawable).setColor(color)
                val backgroundUnselected = AppCompatResources.getDrawable(requireContext(), R.drawable.button_color_picker_unselected)
                (backgroundUnselected as GradientDrawable).setColor(color)

                isClickable = true
                background = backgroundUnselected
                tag = it.key
                setOnClickListener{
                    selectedColor = color
                    selectedButton?.let {
                        it.background = (AppCompatResources.getDrawable(requireContext(), R.drawable.button_color_picker_unselected) as GradientDrawable).apply {
                            setColor(predefinedColors[it.tag] ?: Color.TRANSPARENT)
                        }
                    }
                    selectedButton = this
                    background = backgroundSelected
                    setCustomColor(color)
                    (binding.buttonSubmit.background as GradientDrawable).setColor(color)
                }

                if(selectedColor == color){
                    background = backgroundSelected
                    selectedButton = this
                }
            }

            binding.gridPredefindedColors.addView(colorButton)
        }
    }

    private fun setCustomColor(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val initialHue = hsv[0].toInt()
        binding.colorPickerSeekbar.progress = initialHue
    }

    private fun onCustomColorChanged(hue: Int) {
        val hsv = floatArrayOf(hue.toFloat(), 1f, 1f)
        selectedColor = Color.HSVToColor(hsv)
        selectedButton?.let {
            it.background = (AppCompatResources.getDrawable(requireContext(), R.drawable.button_color_picker_unselected) as GradientDrawable).apply {
                setColor(predefinedColors[it.tag] ?: Color.TRANSPARENT)
            }
        }
        (binding.buttonSubmit.background as GradientDrawable).setColor(selectedColor)
        selectedButton = null
    }

    override fun onDestroy() {
        setFragmentResult("selectedColor", bundleOf("selectedColor" to selectedColor))
        _binding = null
        super.onDestroy()
    }
}