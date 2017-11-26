package org.voidptr.bionicgopher.exception;

/**
 * Created by errant on 11/11/17.
 */

public class GopherParseError extends RuntimeException {
    private String line;

    public GopherParseError(String line){
        this.line = line;
    }

    public String toString(){
        return "Parse error on: "+line;
    }
}
