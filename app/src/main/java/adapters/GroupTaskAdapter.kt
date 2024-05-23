package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import com.example.efficiencymaster.R

class GroupTaskAdapter (private var grouptaskList: List<GroupTaskInfo>) : RecyclerView.Adapter<GroupTaskAdapter.TaskViewHolder>()  {

    // This is the listener
    private var cancelListener: ((Int) -> Unit)? = null

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_task_view, parent, false)
        return TaskViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = grouptaskList[position]
        holder.bind(info)
    }

    // This function will return the size of the list
    override fun getItemCount() = grouptaskList.size
    fun updateList(temp: ArrayList<GroupTaskInfo>) {
        grouptaskList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.textView241)
        private val details: EditText = itemView.findViewById(R.id.textView2411)
        private val statusText: EditText = itemView.findViewById(R.id.textView2)
        private val assignedText: EditText = itemView.findViewById(R.id.textView3)
        private val DeleteTask: ImageButton =itemView.findViewById(R.id.imageButton2)
        private val CreatedBy:EditText = itemView.findViewById(R.id.textView11)


        // This is the init block
        init {
            DeleteTask.setOnClickListener{
                cancelListener?.invoke(adapterPosition)

            }

        }

        // This function will bind the data
        fun bind(info: GroupTaskInfo) {
            taskName.text = info.taskname
            details.setText(info.taskdescription)
            details.isEnabled = false
            statusText.setText(info.status)
            statusText.isEnabled = false
            assignedText.setText(info.assigned)
            assignedText.isEnabled = false
            CreatedBy.setText(info.createdby)
            CreatedBy.isEnabled = false

        }
    }
}