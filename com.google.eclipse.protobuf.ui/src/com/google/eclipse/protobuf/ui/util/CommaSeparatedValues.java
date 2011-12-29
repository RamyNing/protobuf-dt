/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

/**
 * Utility methods related to comma-separated values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class CommaSeparatedValues {
  private static final String CSV_PATTERN = "[\\s]*,[\\s]*";

  /**
   * Splits the given {@code String} containing comma-separated values.
   * @param s the given {@code String}.
   * @return an array containing an element per value in the given {@code String}.
   */
  public static String[] splitCsv(String s) {
    return s.split(CSV_PATTERN);
  }

  private CommaSeparatedValues() {}
}
