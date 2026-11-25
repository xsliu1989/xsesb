package com.xs.config;

import lombok.Data;

import java.util.Date;


@Data
public class BinFaBean {

    public BinFaBean(String nonce, String channel,  String tranno, Date date){
        this.nonce = nonce;
        this.channel = channel;
        this.tranno = tranno;
        this.date = date;
    }

    private String nonce;

    private String channel;

    private String tranno;

    private Date date;
}
