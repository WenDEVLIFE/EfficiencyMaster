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

    private var cancelListener: ((Int) -> Unit)? = null

    private var editListener: ((Int) -> Unit)? = null

    private var deleteListener: ((Int) -> Unit)? = null

    fun setOnDeleteListener(listener: (Int) -> Unit) {
        deleteListener = listener
    }

    fun interface OnDeleteListener {
        fun onDelete(position: Int)
    }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memberview, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val info = memberList[position]
        holder.bind(info)
    }

    override fun getItemCount() = memberList.size
    fun updateList(temp: ArrayList<Member>) {
        memberList = temp
        notifyDataSetChanged()

    }

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.textView241)
        private val roleText: EditText = itemView.findViewById(R.id.textView2411)
        private val userID:EditText = itemView.findViewById(R.id.textView2)
        private val joinedDate:EditText = itemView.findViewById(R.id.textView13)



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
            val inflater = LayoutInflater.from(view.context)
            val popupView = inflater.inflate(R.layout.wishlist_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val home = popupView.findViewById<TextView>(R.id.home)
            home.setOnClickListener {
                deleteListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            val changeToAdmin = popupView.findViewById<TextView>(R.id.change_to_admin)
            changeToAdmin.setOnClickListener {
                editListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            val changeToUser = popupView.findViewById<TextView>(R.id.change_to_user)
            changeToUser.setOnClickListener {
                cancelListener?.invoke(adapterPosition)
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(view)
        }

    }
}