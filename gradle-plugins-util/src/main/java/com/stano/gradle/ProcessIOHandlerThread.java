package com.stano.gradle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ProcessIOHandlerThread extends Thread {
  private final Scanner scanner;

  public ProcessIOHandlerThread(InputStream inputStream) {
    this.scanner = new Scanner(new InputStreamReader(inputStream));
  }

  @Override
  public void run() {
    try {
      while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
      }
    } finally {
      scanner.close();
    }
  }
}
