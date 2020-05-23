package lukewithey.example.messaging.views

import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_outgoing_row.view.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.models.User

private const val TAG = "ChatOutgoingItem"

class ChatOutgoingItem(val text: String, val user: User): Item<GroupieViewHolder>() {


    override fun getLayout(): Int {
        return R.layout.chat_outgoing_row
        Log.d(TAG, "getLayout called")

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_outgoing_message.text = text
        Log.d(TAG, "Bind called")

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_outgoing_row
        Picasso.get().load(uri).into(targetImageView)


    }
}