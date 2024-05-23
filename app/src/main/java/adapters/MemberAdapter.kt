package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import classes.Member
import com.example.efficiencymaster.R


class MemberAdapter(private var memberList: List<Member>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memberview, parent, false)
        return MemberViewHolder(view)
    }

    // This function will bind the data
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val info = memberList[position]
        holder.bind(info)
    }

    override fun getItemCount() = memberList.size
    fun updateList(temp: ArrayList<Member>) {
        memberList = temp
        notifyDataSetChanged()

    }

    // This is the inner class
    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.textView241)
        private val roleText: EditText = itemView.findViewById(R.id.textView2411)
        private val userID:EditText = itemView.findViewById(R.id.textView2)
        private val joinedDate:EditText = itemView.findViewById(R.id.textView13)


        // This is the init block
        init {
            itemView.setOnClickListener {
                showMenu(itemView)
            }

        }

        // This function will bind the data
        fun bind(info: Member) {
           username.text = info.username
            roleText.setText(info.role)
            roleText.isEnabled = false
            userID.setText(info.userid)
            userID.isEnabled = false
            joinedDate.setText(info.joinedDate)
            joinedDate.isEnabled = false



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
            val home = popupView.findViewById<TextView>(R.id.home)
            home.setOnClickListener {
                deleteListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            // This will show the popup menu
            val changeToAdmin = popupView.findViewById<TextView>(R.id.change_to_admin)
            changeToAdmin.setOnClickListener {
                editListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            // This will show the popup menu
            val changeToUser = popupView.findViewById<TextView>(R.id.change_to_user)
            changeToUser.setOnClickListener {
                cancelListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            // This will show the popup menu as dropdown
            popupWindow.showAsDropDown(view)
        }

    }
}