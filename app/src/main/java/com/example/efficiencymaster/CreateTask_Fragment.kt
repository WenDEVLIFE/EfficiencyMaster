package com.example.efficiencymaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTask_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTask_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_task_, container, false)

        val TaskName = view.findViewById<EditText>(R.id.editTextText2)
        val TaskDescription = view.findViewById<EditText>(R.id.desscripts)

        val create_Task_btn = view.findViewById<Button>(R.id.button2)
        create_Task_btn.setOnClickListener {
            val taskname = TaskName.text.toString()
            val taskdescription = TaskDescription.text.toString()

           if(taskname.isEmpty()) {
              TaskName.error = "Please Enter Task Name"
           }else{
               if(taskdescription.isEmpty()) {
                   TaskDescription.error = "Please Enter Task Description"
               }else{
                   val builder = AlertDialog.Builder(requireContext())
                   builder.setTitle("Task Created")
                   builder.setMessage("Task Name: $taskname\nTask Description: $taskdescription")
                   builder.setPositiveButton("OK"){dialog, which ->}
                   builder.show()
               }
           }
        }

        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

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
         * @return A new instance of fragment CreateTask_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateTask_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}