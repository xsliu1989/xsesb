package com.xs.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SimulatorResponse implements Serializable {
    private boolean success;
    private int status;
    private String body;
    private Map<String, String> headers;
    private long costMs;
    private String error;
}
