package lukewithey.example.messaging.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.models.ChatIncomingItem
import lukewithey.example.messaging.models.ChatMessage
import lukewithey.example.messaging.models.ChatOutgoingItem
import lukewithey.example.messaging.models.User

private const val TAG = "ChatLogActivity"

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Log.d(TAG, "onCreateCalled")


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username

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


        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(reference.key!!, text, outgoingId, incomingId, System.currentTimeMillis()/1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message ${reference.key}")
            } .addOnFailureListener {
                Log.d(TAG, "Did not save due to: ${it.message}")
            }
    }

    private fun setupDummyData() {
        Log.d(TAG, "setupDummyDataCalled")

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatIncomingItem("From my b boy james herrington\n who loves to know whats\n going on in the\n main thing"))
        adapter.add(ChatOutgoingItem("yes bu"))


        recyclerview_chat_log.adapter = adapter
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage?.text)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }
}



