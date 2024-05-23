package adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.Task
import com.example.efficiencymaster.R
import kotlin.concurrent.thread


class TaskAdapter(private var taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var cancelListener: ((Int) -> Unit)? = null // This is the listener

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskviewer, parent, false)
        return TaskViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = taskList[position]
        holder.bind(info)
    }

    // This function will return the size of the list
    override fun getItemCount() = taskList.size
    fun updateList(temp: ArrayList<Task>) {
        taskList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TaskName: TextView = itemView.findViewById(R.id.textView241)
        private val Details: EditText = itemView.findViewById(R.id.textView2411)
        private val DoneTask: CheckBox = itemView.findViewById(R.id.checkBox)


        // This function will set the listener
        init {
            DoneTask.setOnClickListener {
                cancelListener?.invoke(adapterPosition)

                // Add threat sleep to simulate the animation checking the checkbox
                thread {
                    Thread.sleep(1000)
                    (itemView.context as Activity).runOnUiThread {
                        DoneTask.isChecked = false
                    }
                }
            }
        }

        // This function will bind the data
        fun bind(info: Task) {
            TaskName.text = info.taskname
            Details.setText(info.taskdescription)
            Details.isEnabled = false


        }
    }
}