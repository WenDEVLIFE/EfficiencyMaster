package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import com.example.efficiencymaster.R


class groupDoneTaskAdapter(private var memberList: List<GroupTaskInfo>) : RecyclerView.Adapter<groupDoneTaskAdapter.TaskViewHolder>() {

    private var cancelListener: ((Int) -> Unit)? = null // This is the listener

    private var editListener: ((Int) -> Unit)? = null // This is the listener

    private var deleteListener: ((Int) -> Unit)? = null // This is the listener

    // This function will set the listener
    fun setOnDeleteListener(listener: (Int) -> Unit) {
        deleteListener = listener
    }

    fun setOnCancelListener(listener: (Int) -> Unit) {
        cancelListener = listener
    }

    fun setOnEditListener(listener: (Int) -> Unit) {
        editListener = listener
    }

    // This is the interface
    fun interface OnEditListener {
        fun onEdit(position: Int)
    }
    fun interface OnDeleteListener {
        fun onDelete(position: Int)
    }
    fun interface OnCancelListener {
        fun onCancel(position: Int)
    }

    // This function will create the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grouptaskdone_view, parent, false)
        return TaskViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = memberList[position]
        holder.bind(info)
    }

    override fun getItemCount() = memberList.size
    fun updateList(temp: ArrayList<GroupTaskInfo>) {
        memberList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.textView241)
        private val details: EditText = itemView.findViewById(R.id.textView2411)
        private val statusText: EditText = itemView.findViewById(R.id.textView2)
        private val assignedText: EditText = itemView.findViewById(R.id.textView3)
        private val CreatedBy:EditText = itemView.findViewById(R.id.textView11)

        // This is the init block
        init {
            itemView.setOnClickListener {
                showMenu(itemView)
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

        // This function will show the menu
        private fun showMenu(view: View) {

            // This will create a popup menu
            val inflater = LayoutInflater.from(view.context)

            // This will inflate the menu
            val popupView = inflater.inflate(R.layout.whishlist2, null)

            // This will create a popup window
            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // This will show the popup menu
            val deniedBtn = popupView.findViewById<TextView>(R.id.home)
            deniedBtn.setText("Delete Task")
            deniedBtn.setOnClickListener {
                deleteListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }


            val cancel_menu = popupView.findViewById<TextView>(R.id.close_menu)
            cancel_menu.setOnClickListener {
                popupWindow.dismiss()
            }

            // This will show the popup menu as dropdown
            popupWindow.showAsDropDown(view)
        }

    }
}