package com.alphawallet.app.event;

public class AirDropEvent
{
    public String txHash;

    public AirDropEvent(String txHash)
    {
        this.txHash = txHash;
    }
}
