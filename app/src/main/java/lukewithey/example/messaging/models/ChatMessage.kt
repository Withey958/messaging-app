package lukewithey.example.messaging.models

class ChatMessage(val id: String, val text: String, val outgoingId: String, val incomingId: String, val timestamp: Long) {
    constructor(): this("", "", "", "", -1)

}