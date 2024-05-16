package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.Group
import com.example.efficiencymaster.R


class GroupAdapter(private var groupList: List<Group>) : RecyclerView.Adapter<GroupAdapter.TaskViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groupview, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = groupList[position]
        holder.bind(info)
    }

    override fun getItemCount() = groupList.size
    fun updateList(temp: ArrayList<Group>) {
        groupList = temp
        notifyDataSetChanged()

    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val GroupName: TextView = itemView.findViewById(R.id.textView241)
        private val Details: TextView = itemView.findViewById(R.id.textView2411)
        private val MemberSize:TextView = itemView.findViewById(R.id.textView2)
        private val JoinButton: Button =itemView.findViewById(R.id.button3)


        init {
            JoinButton.setOnClickListener{
                cancelListener?.invoke(adapterPosition)

            }

        }

        fun bind(info: Group) {
            GroupName.text = info.groupName
            Details.text = info.groupDescription
            MemberSize.text = info.memberSize



        }
    }
}