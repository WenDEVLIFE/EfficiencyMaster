package com.example.efficiencymaster

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragmentation.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragmentation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var username: String? = null
    private var email: String? = null
    val db  = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username")
            email = it.getString("email")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_home_fragmentation, container, false)

       val percentage = view.findViewById<TextView>(R.id.textView5)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username")
            email = it.getString("email")

        }

        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 2f))
        entries.add(BarEntry(2f, 4f))
        entries.add(BarEntry(3f, 6f))
        entries.add(BarEntry(4f, 8f))
        entries.add(BarEntry(5f, 10f))
        entries.add(BarEntry(6f, 12f))
        entries.add(BarEntry(7f, 14f))
        entries.add(BarEntry(8f, 16f))
        entries.add(BarEntry(9f, 18f))
        entries.add(BarEntry(10f, 20f))

        val barDataSet = BarDataSet(entries, "Tasks")
        val barData = BarData(barDataSet)


        val barChart = view.findViewById<BarChart>(R.id.bargraph)

        // Set the legend
        val legend = barChart.legend
        legend.isEnabled = true
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 14f
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        barChart.data = barData
        barDataSet.setColor(Color.GREEN) // Set the color of the bars
        barChart.invalidate() // refreshes the chart


        val circularProgressBar = view.findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.apply {
            // Set Progress
            progress = 100f
            // or with animation
            setProgressWithAnimation(100f, 1000) // =1s

            // Set Progress Max
            progressMax = 200f

            // Set ProgressBar Color
            progressBarColor = Color.GREEN
            // or with gradient
            progressBarColorStart = Color.GREEN
            progressBarColorEnd = Color.GREEN
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.GREEN
            // or with gradient
            backgroundProgressBarColorStart = Color.WHITE
            backgroundProgressBarColorEnd = Color.WHITE
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
            percentage.setText("$progress %")
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragmentation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragmentation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}