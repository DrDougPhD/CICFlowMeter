package cic.cs.unb.ca.jnetpcap;

public enum ProtocolEnum {

    ICMP(1),
    TCP(6),
    UDP(17),
    SCTP(132),
    DEFAULT(0);

    public final int val;

    private ProtocolEnum(int num) {
        this.val = num;
    }
}
