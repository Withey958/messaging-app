package lukewithey.example.messaging.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.views.ChatIncomingItem
import lukewithey.example.messaging.models.ChatMessage
import lukewithey.example.messaging.views.ChatOutgoingItem
import lukewithey.example.messaging.models.User

private const val TAG = "ChatLogActivity"
private val adapter = GroupAdapter<GroupieViewHolder>()

private var incomingUser: User? = null


class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Log.d(TAG, "onCreateCalled")

        incomingUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        recyclerview_chat_log.adapter = adapter

        Log.d(TAG, "incomingId onCreate: ${incomingUser?.uid}")

        if(incomingUser==null) {
            Log.d(TAG, "Incoming user is null nothing will work")
            return
        }

        supportActionBar?.title = incomingUser?.username

//        setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage()
        }

        Log.d(TAG, "onCreateEnded")


    }

    private fun performSendMessage() {
        // How do we send message using firebase

        val text = edittext_chat.text.toString()
        val outgoingId = FirebaseAuth.getInstance().uid ?: return

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val incomingId = user.uid


//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$outgoingId/$incomingId").push()

        val incomingReference = FirebaseDatabase.getInstance().getReference("/user-messages/$incomingId/$outgoingId").push()

        val chatMessage = ChatMessage(reference.key!!, text, outgoingId, incomingId, System.currentTimeMillis()/1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message ${reference.key}")
                edittext_chat.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            } .addOnFailureListener {
                Log.d(TAG, "Did not save due to: ${it.message}")
            }

        incomingReference.setValue(chatMessage)

        val latestMessageReference = FirebaseDatabase.getInstance().getReference("/LatestMessages/$outgoingId/$incomingId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageIncomingReference = FirebaseDatabase.getInstance().getReference("/LatestMessages/$incomingId/$outgoingId")
        latestMessageIncomingReference.setValue(chatMessage)


    }

    private fun listenForMessages() {

        val outgoingId = FirebaseAuth.getInstance().uid ?: return
        val incomingId = incomingUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$outgoingId/$incomingId")
        Log.d(TAG, "outGoingId listenForMessages: $outgoingId")
        Log.d(TAG, "inComingId listenForMessages: $incomingId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled: Called")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "onChildMoved: Called")

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "onChildChanged: Called")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "onChildAdded: Called")

                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.outgoingId == FirebaseAuth.getInstance().uid) { // checks uid of logged in user
                        val currentUser = LatestMessageActivity.currentUser ?: return
                        adapter.add(
                            ChatOutgoingItem(
                                chatMessage.text,
                                currentUser
                            )
                        ) //Add messages to recylcer view for outgoing messages
                    } else {
                        adapter.add(
                            ChatIncomingItem(
                                chatMessage.text,
                                incomingUser!!
                            )
                        )
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(TAG, "onChildRemoved: Called")

            }
        })
    }
}



