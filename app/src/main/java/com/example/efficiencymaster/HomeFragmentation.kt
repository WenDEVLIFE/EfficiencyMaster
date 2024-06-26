package com.example.efficiencymaster

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    private lateinit var percentage : TextView
    private lateinit var taskDone : TextView
    private lateinit var circularProgressBar : CircularProgressBar
    private lateinit var circularProgressBar2 : CircularProgressBar
    private lateinit var barChart : BarChart
    lateinit var bundle:Bundle
    lateinit var fragment:Fragment
    private val networkManager = NetworkManager()

    private var username: String? = null
    private var email: String? = null
    val db  = Firebase.firestore
    private var retriveCount1:Double = 0.00
    private var retriveCount2:Double = 0.00

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace with your previous fragment here
                val fragment = HomeFragmentation()
                val bundle = Bundle()
                bundle.putString("username", username)
                fragment.arguments = bundle
                replaceFragment((fragment))
            }
        })

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())


        // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_home_fragmentation, container, false)

       percentage = view.findViewById(R.id.textView5)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username")
            email = it.getString("email")

        }

        // Image Button and its ID
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

        barChart = view.findViewById(R.id.bargraph)

        loadDatas(barChart)


        taskDone = view.findViewById(R.id.done_task_count)

        circularProgressBar = view.findViewById(R.id.circularProgressBar)
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
        circularProgressBar2 = view.findViewById(R.id.circularProgressBar2)
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
        loadStats()

        // task buton and its ID
        val taskButton = view.findViewById<ImageButton>(R.id.imageButton1)
        taskButton.setOnClickListener{
            // This will go to create task
            fragment = InvidividualTask()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        val doneTaskButton = view.findViewById<ImageButton>(R.id.imageButton3)
        doneTaskButton.setOnClickListener {
            // This will go to done task
            fragment = DoneTaskFragment()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        val joinbuttonGroup = view.findViewById<ImageButton>(R.id.imageButton4)
        joinbuttonGroup.setOnClickListener {
            // This will go to join group
            fragment = YourJoinedGroup()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }


        return view
    }


    // replace fragment
    private  fun replaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // This will load the data from the database
    private fun loadDatas(barChart: BarChart?) {
        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener { userit  ->
            if (userit.isEmpty){
                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (userdocument in userit){
                    val iD = userdocument.data["UserID"].toString()
                    val entries = ArrayList<BarEntry>()

                    // get the data from the past 7 days
                    val sevenDaysAgo = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    // Collection Reference
                    val collectionReference1 = db.collection("Task")

                    // Get the query of the Done task and its completion date
                    val query1 = collectionReference1.whereEqualTo("Status", "Done").whereEqualTo("UserID", iD).whereGreaterThanOrEqualTo("CompletionDate", sevenDaysAgo)
                    query1.get().addOnSuccessListener { documents ->

                        // Print the documents
                        Toast.makeText(context, "Firestore query results $documents", Toast.LENGTH_SHORT).show()

                        // Group the tasks by date
                        val tasksByDate = documents.groupBy {
                            it.getString("CompletionDate")
                        }

                        // Get the task counts
                        val taskCounts = tasksByDate.values.map {
                            it.size
                        }



                        if (documents.isEmpty) {
                            for  (i in 1..7){
                                val entry1 = BarEntry(i.toFloat(), 0f)
                                entries.add(entry1)
                            }
                        } else{
                            // Create BarEntry objects and add them to the entries list
                            for (i in taskCounts.indices) {

                                // Print the entries
                                val entry = BarEntry(i.toFloat(), taskCounts[i].toFloat())

                                // add the entries.
                                entries.add(entry)
                                println("BarEntry: $entry") // Debug print
                            }
                        }


                        // Create BarDataSet and BarData objects
                        val barDataSet = BarDataSet(entries, "Your tasks in past 7 days")
                        val barData = BarData(barDataSet)

                        // Set the legend
                        val legend = barChart?.legend

                        legend?.isEnabled = true
                        legend?.form = Legend.LegendForm.LINE
                        legend?.textSize = 14f
                        legend?.textColor = Color.BLACK

                        legend?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                        legend?.orientation = Legend.LegendOrientation.HORIZONTAL
                        legend?.setDrawInside(false)
                        barChart?.data = barData
                        barDataSet.setColor(Color.GREEN) // Set the color of the bars
                        barChart?.invalidate() // refreshes the chart

                    }.addOnFailureListener { exception ->
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("Error")
                        alertDialog.setMessage("Error loading data: $exception")
                        alertDialog.setPositiveButton("OK") { _, _ ->
                            // Do nothing
                        }
                        alertDialog.show()
                    }
                }
            }
        }

    }

    private fun loadStats(){
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener{ userit ->
            if (userit.isEmpty) {
                percentage.text = buildString {
        append("User does not exist")
    }
            }
            else{
                for (document in userit){
                    val iD = document.data["UserID"].toString()
                    db.collection("ProgresssUser").whereEqualTo("UserID", iD).get().addOnSuccessListener { progressit ->
                        if (progressit.isEmpty){
                            percentage.text = buildString {
                                append("Progress: 0%")
                            }
                            taskDone.text = buildString {
                                append("Task Done: 0")
                            }
                        }else{
                            for (progressdocument in progressit){

                                // Collection Reference
                                val collectionReference1 = db.collection("Task")

                                // Get the query of the Pending task for the specific user
                                val query1 = collectionReference1.whereEqualTo("Status", "Pending").whereEqualTo("UserID", iD)
                                query1.get().addOnSuccessListener { pendingit ->
                                    val pending = pendingit.size()
                                    retriveCount1 = pending.toDouble()

                                    // Get the query of the Done task for the specific user
                                    val query2 = collectionReference1.whereEqualTo("Status", "Done").whereEqualTo("UserID", iD)
                                    query2.get().addOnSuccessListener {doneit ->
                                        val done = doneit.size()
                                        retriveCount2 = done.toDouble()

                                        val total = retriveCount1 + retriveCount2
                                        val percentages1 = retriveCount2 / total * 100
                                        val percentages2 = retriveCount1 / total * 100

                                        // set to substring 0 to 5
                                        val stringg1 = percentages1.toString()
                                        val stringg2 = percentages2.toString()
                                        val subStr = subString(stringg2)
                                        val subStr2 = subString(stringg1)

                                        // get the float value of the percentages
                                        val floatPercentages1 = percentages1.toFloat()
                                        val floatPercentages2 = percentages2.toFloat()

                                        // set the text of the percentage
                                        percentage.text = "$subStr %"
                                        taskDone.text = "$subStr2 %"

                                        // set the progress of the circular progress bar
                                        circularProgressBar.setProgressWithAnimation(floatPercentages2, 3000)
                                        circularProgressBar2.setProgressWithAnimation(floatPercentages1, 3000)
                                        // Update the UI here with the calculated percentage
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // Limit the word from 0 to 5 letters only.
    private fun subString (string: String): String {
        return if (string.length <= 5) string else string.substring(0, 5)
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