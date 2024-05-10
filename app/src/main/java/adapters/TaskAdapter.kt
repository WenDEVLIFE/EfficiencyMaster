package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.Task
import com.example.efficiencymaster.R
import kotlin.concurrent.thread


class TaskAdapter(private var taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var cancelListener: ((Int) -> Unit)? = null

    private var editListener: ((Int) -> Unit)? = null

    fun setOnCancelListener(listener: (Int) -> Unit) {
        cancelListener = listener
    }

    fun interface OnCancelListener {
        fun onCancel(position: Int)
    }

    fun setOnEditListener(listener: (Int) -> Unit) {
        editListener = listener
    }

    fun interface OnEditListener {
        fun onEdit(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskviewer, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = taskList[position]
        holder.bind(info)
    }

    override fun getItemCount() = taskList.size
    fun updateList(temp: ArrayList<Task>) {
        taskList = temp
        notifyDataSetChanged()

    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TaskName: TextView = itemView.findViewById(R.id.textView241)
        private val Details: TextView = itemView.findViewById(R.id.textView2411)
        private val DoneTask: CheckBox = itemView.findViewById(R.id.checkBox)


        init {
            DoneTask.setOnClickListener {
                cancelListener?.invoke(adapterPosition)

                // Add threat sleep to simulate the animation checking the checkbox
                thread {
                    Thread.sleep(1000)
                    DoneTask.isChecked = false
                }
            }
        }

        fun bind(info: Task) {
            TaskName.text = info.taskname
            Details.text = info.taskdescription


        }
    }
}