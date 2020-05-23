package lukewithey.example.messaging.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.models.ChatMessage
import lukewithey.example.messaging.models.User

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return  R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_description_latest_message_row.text = chatMessage.text


        val chatPartnerId: String = if(chatMessage.incomingId == FirebaseAuth.getInstance().uid) {
            chatMessage.outgoingId
        } else {
            chatMessage.incomingId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textview_username_latest_message_row.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageview_latest_message_row
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)

            }
        })




    }
}