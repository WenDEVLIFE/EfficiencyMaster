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
import java.time.LocalDate

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
    lateinit var percentage : TextView
    lateinit var TaskDone : TextView
    lateinit var circularProgressBar : CircularProgressBar
    lateinit var circularProgressBar2 : CircularProgressBar
    lateinit var barChart : BarChart

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

       percentage = view.findViewById<TextView>(R.id.textView5)

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
        val barDataSet = BarDataSet(entries, "Tasks in past 7  days")
        val barData = BarData(barDataSet)


        barChart = view.findViewById(R.id.bargraph)


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
        LoadDatas(barChart, barData, barDataSet)
        TaskDone = view.findViewById(R.id.done_task_count)

        circularProgressBar = view.findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.apply {
            // Set Progress
            //progress = 100f
            // or with animation
            //setProgressWithAnimation(100f, 3000) // =1s

            // Set Progress Max
            progressMax = 100f

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
        }
        circularProgressBar2 = view.findViewById<CircularProgressBar>(R.id.circularProgressBar2)
        circularProgressBar2.apply {
            // Set Progress
            //progress = 100f
            // or with animation
            //setProgressWithAnimation(100f, 3000) // =1s

            // Set Progress Max
            progressMax = 100f

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
        }
        LoadStats()


        return view
    }

    private fun LoadDatas(barChart: BarChart?, barData: BarData, barDataSet: BarDataSet) {
        val sevenDaysAgo = LocalDate.now().minusDays(7)
        val CollectionReference1 = db.collection("Task")
        val query1 = CollectionReference1.whereEqualTo("Status", "Done").whereGreaterThanOrEqualTo("Date", sevenDaysAgo)
        query1.get().addOnSuccessListener {
            val pending = it.size()

            for (i in 0 until pending) {
                barDataSet.addEntry(BarEntry(i.toFloat(), pending.toFloat()))
                barChart?.data = barData
                barChart?.invalidate()
            }


        }

    }

    fun LoadStats(){
        var retriveCount1:Double = 0.00
        var retriveCount2:Double = 0.00
        val CollectionReference1 = db.collection("Task")
        val query1 = CollectionReference1.whereEqualTo("Status", "Pending")
        query1.get().addOnSuccessListener {
            val pending = it.size()
            retriveCount1 = pending.toDouble()

            val query2 = CollectionReference1.whereEqualTo("Status", "Done")
            query2.get().addOnSuccessListener {
                val done = it.size()
                retriveCount2 = done.toDouble()

                val total = retriveCount1 + retriveCount2
                val percentages1 = retriveCount2 / total * 100
                val percentages2 = retriveCount1 / total * 100

                val stringg1 = percentages1.toString()
                val stringg2 = percentages2.toString()
                val subStr = subString(stringg1)
                val subStr2 = subString(stringg2)
                val FloatPercentages1 = percentages1.toFloat()
                val FloatPercentages2 = percentages2.toFloat()

                percentage.text = "$subStr %"
                TaskDone.text = "$subStr2 %"
                circularProgressBar.setProgressWithAnimation(FloatPercentages1, 3000)
                circularProgressBar2.setProgressWithAnimation(FloatPercentages2, 3000)
                // Update the UI here with the calculated percentage
            }
        }
    }

    fun subString (string: String): String {
        return string.substring(0, 5)
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