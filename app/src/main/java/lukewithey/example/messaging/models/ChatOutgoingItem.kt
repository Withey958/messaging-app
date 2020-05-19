package lukewithey.example.messaging.models

import android.util.Log
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_outgoing_row.view.*
import lukewithey.example.messaging.R

private const val TAG = "ChatOutgoingItem"

class ChatOutgoingItem(val text: String): Item<GroupieViewHolder>() {


    override fun getLayout(): Int {
        return R.layout.chat_outgoing_row
        Log.d(TAG, "getLayout called")

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_outgoing_message.text = text
        Log.d(TAG, "Bind called")


    }
}