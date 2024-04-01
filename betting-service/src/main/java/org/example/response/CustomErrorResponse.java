package org.example.response;

public class CustomErrorResponse {
    private int code;
    private String error;
    private String solution;

    public CustomErrorResponse(int code, String error, String solution) {
        this.code = code;
        this.error = error;
        this.solution = solution;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
