package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import classes.MembersPending
import com.example.efficiencymaster.R


class PendingAdapter(private var memberList: List<MembersPending>) : RecyclerView.Adapter<PendingAdapter.MemberViewHolder>() {

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pendinglist, parent, false)
        return MemberViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val info = memberList[position]
        holder.bind(info)
    }

    override fun getItemCount() = memberList.size
    fun updateList(temp: ArrayList<MembersPending>) {
        memberList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.textView241)
        private val groupNames: EditText = itemView.findViewById(R.id.textView2411)
        private val status:EditText = itemView.findViewById(R.id.textView2)
        private val dateRequest:EditText = itemView.findViewById(R.id.textView13)


        // This is the init block
        init {
            itemView.setOnClickListener {
                showMenu(itemView)
            }

        }

        // This function will bind the data
        fun bind(info: MembersPending) {
            username.text = info.username
            groupNames.setText(info.groupName)
            groupNames.isEnabled = false
            status.setText(info.status)
            status.isEnabled = false
            dateRequest.setText(info.dateRequest)
            dateRequest.isEnabled = false



        }

        // This function will show the menu
        private fun showMenu(view: View) {

            // This will create a popup menu
            val inflater = LayoutInflater.from(view.context)

            // This will inflate the menu
            val popupView = inflater.inflate(R.layout.wishlist_menu, null)

            // This will create a popup window
            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // This will show the popup menu
            val deniedBtn = popupView.findViewById<TextView>(R.id.home)
            deniedBtn.setText("Denied Membership")
            deniedBtn.setOnClickListener {
                deleteListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            // This will show the popup menu
            val acceptBtn = popupView.findViewById<TextView>(R.id.change_to_admin)
            acceptBtn.setText("Accept Membership")
            acceptBtn.setOnClickListener {
                editListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            // This will show the popup menu
            val changeToUser = popupView.findViewById<TextView>(R.id.change_to_user)
            changeToUser.visibility = View.VISIBLE
            changeToUser.setOnClickListener {
               // cancelListener?.invoke(adapterPosition)
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