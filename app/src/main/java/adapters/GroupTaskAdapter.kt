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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_task_view, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = grouptaskList[position]
        holder.bind(info)
    }

    override fun getItemCount() = grouptaskList.size
    fun updateList(temp: ArrayList<GroupTaskInfo>) {
        grouptaskList = temp
        notifyDataSetChanged()

    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.textView241)
        private val details: EditText = itemView.findViewById(R.id.textView2411)
        private val statusText: EditText = itemView.findViewById(R.id.textView2)
        private val assignedText: EditText = itemView.findViewById(R.id.textView3)
        private val DeleteTask: ImageButton =itemView.findViewById(R.id.imageButton2)
        private val CreatedBy:EditText = itemView.findViewById(R.id.textView11)


        init {
            DeleteTask.setOnClickListener{
                cancelListener?.invoke(adapterPosition)

            }

        }

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