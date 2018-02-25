package se.kth.id1212.sockets.hangman.common;

    public class MessageException extends RuntimeException {
        public MessageException(String msg) {
            super(msg);
        }

        public MessageException(Throwable rootCause) {
            super(rootCause);
    }
}
