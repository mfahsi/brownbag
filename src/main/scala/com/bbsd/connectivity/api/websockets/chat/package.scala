package com.bbsd.connectivity.api.websockets

import scala.language.implicitConversions

package object chat {
  implicit def chatEventToChatMessage(event: IncomingMessage): ChatMessage = ChatMessage(event.sender, event.message)
}
