package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.Group
import classes.Member
import com.example.efficiencymaster.R
import com.sun.mail.imap.protocol.FetchResponse.getItem


class MemberAdapter(private var groupList: List<Member>) : RecyclerView.Adapter<MemberAdapter.TaskViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_view2, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val info = groupList[position]
        holder.bind(info)
    }

    override fun getItemCount() = groupList.size
    fun updateList(temp: ArrayList<Member>) {
        groupList = temp
        notifyDataSetChanged()

    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.textView241)
        private val roleText: EditText = itemView.findViewById(R.id.textView2411)
        private val userID:EditText = itemView.findViewById(R.id.textView2)
        private val joinedDate:EditText = itemView.findViewById(R.id.textView3)



        init {
            itemView.setOnClickListener {
                showMenu(itemView)
            }

        }

        fun bind(info: Member) {
           username.text = info.username
            roleText.setText(info.role)
            roleText.isEnabled = false
            userID.setText(info.userid)
            userID.isEnabled = false
            joinedDate.setText(info.joinedDate)
            joinedDate.isEnabled = false



        }

        private fun showMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.wishlist_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> {
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }


    }
}