package adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.DoneTask
import classes.Task
import com.example.efficiencymaster.R
import kotlin.concurrent.thread


class DoneTaskAdapter(private var taskList: List<DoneTask>) : RecyclerView.Adapter<DoneTaskAdapter.TaskViewHolder>() {

    private var cancelListener: ((Int) -> Unit)? = null  // This is the listener


    // This function will set the listener
    fun setOnCancelListener(listener: (Int) -> Unit) {
        cancelListener = listener
    }

    // This is the interface
    fun interface OnCancelListener {
        fun onCancel(position: Int)
    }


    // This function will create the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.done_taskview, parent, false)
        return TaskViewHolder(view)
    }

    //  This function will bind the data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = taskList[position]
        holder.bind(info)
    }

    // This function will return the size of the list
    override fun getItemCount() = taskList.size
    fun updateList(temp: ArrayList<DoneTask>) {
        taskList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TaskName: TextView = itemView.findViewById(R.id.textView241)
        private val Details: EditText = itemView.findViewById(R.id.textView2411)
        private val StatusText: EditText = itemView.findViewById(R.id.textView2)
        private val CompletionText:EditText = itemView.findViewById(R.id.textView3)
        private val DeleteTask: ImageButton =itemView.findViewById(R.id.imageButton2)


        // This function will set the listener
        init {
            DeleteTask.setOnClickListener{
                cancelListener?.invoke(adapterPosition)

            }

        }

        // This function will bind the data
        fun bind(info: DoneTask) {
            TaskName.text = info.taskname
            Details.setText(info.taskdescription)
            Details.isEnabled = false
            StatusText.setText(info.status)
            StatusText.isEnabled  = false
            CompletionText.setText(info.completion)
            CompletionText.isEnabled = false


        }
    }
}