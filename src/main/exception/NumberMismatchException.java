package main.exception;

public class NumberMismatchException extends Exception {

    public NumberMismatchException(String msg) {
        super(msg);
        System.out.println(msg);
    }
}
