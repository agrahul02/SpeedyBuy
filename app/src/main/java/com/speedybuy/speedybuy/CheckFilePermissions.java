package com.speedybuy.speedybuy;

import java.io.File;

public class CheckFilePermissions {
    public static void main(String[] args) {
        // Specify the path to the file
        String filePath = "C:/example/file.txt";

        // Create a File object for the file
        File file = new File(filePath);

        // Check if the file is readable
        if (file.canRead()) {
            System.out.println("File is readable");
        } else {
            System.out.println("File is not readable");
        }

        // Check if the file is writable
        if (file.canWrite()) {
            System.out.println("File is writable");
        } else {
            System.out.println("File is not writable");
        }

        // Check if the file is executable
        if (file.canExecute()) {
            System.out.println("File is executable");
        } else {
            System.out.println("File is not executable");
        }
    }
}

