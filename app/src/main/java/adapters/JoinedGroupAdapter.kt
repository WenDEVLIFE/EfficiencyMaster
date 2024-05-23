package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.Group
import com.example.efficiencymaster.R


class JoinedGroupAdapter(private var groupList: List<Group>) : RecyclerView.Adapter<JoinedGroupAdapter.TaskViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groupview1, parent, false)
        return TaskViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = groupList[position]
        holder.bind(info)
    }

    // This function will return the size of the list
    override fun getItemCount() = groupList.size
    fun updateList(temp: ArrayList<Group>) {
        groupList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val GroupName: TextView = itemView.findViewById(R.id.textView241)
        private val Details: EditText = itemView.findViewById(R.id.textView2411)
        private val MemberSize: EditText = itemView.findViewById(R.id.textView2)
        private val JoinButton: Button =itemView.findViewById(R.id.button3)


        // This function will set the listener
        init {
            JoinButton.setOnClickListener{
                cancelListener?.invoke(adapterPosition)

            }

        }

        // This function will bind the data
        fun bind(info: Group) {
            GroupName.text = info.groupName
            Details.setText(info.groupDescription)
            Details.isEnabled = false
            MemberSize.setText(info.memberSize)
            MemberSize.isEnabled = false



        }
    }
}