package protocols;


public abstract class Protocol {
    protected class MessageType {
        public final static String Putchunk = "PUTCHUNK";
        public final static String Stored = "STORED";
    }
}
